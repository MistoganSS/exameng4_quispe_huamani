package com.codigo.msquispehuamani.infraestructure.adapters;

import com.codigo.msquispehuamani.domain.aggregates.constants.Constant;
import com.codigo.msquispehuamani.domain.aggregates.dto.EmpresaDTO;
import com.codigo.msquispehuamani.domain.aggregates.dto.SunatDTO;
import com.codigo.msquispehuamani.domain.aggregates.request.EmpresaRequest;
import com.codigo.msquispehuamani.domain.ports.out.EmpresaServiceOut;
import com.codigo.msquispehuamani.infraestructure.client.ClientSunat;
import com.codigo.msquispehuamani.infraestructure.dao.EmpresaRepository;
import com.codigo.msquispehuamani.infraestructure.entity.EmpresaEntity;
import com.codigo.msquispehuamani.infraestructure.mapper.EmpresaMapper;
import com.codigo.msquispehuamani.infraestructure.redis.RedisService;
import com.codigo.msquispehuamani.infraestructure.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaAdapter implements EmpresaServiceOut {

    private final EmpresaRepository empresaRepository;
    private final ClientSunat clientSunat;
    private final RedisService redisService;

    @Value("${token.apis}")
    private String tokenSunat;

    @Override
    public EmpresaDTO crearEmpresaOut(EmpresaRequest empresaRequest) {
        EmpresaEntity empresaEntity = getEmpresaCreate(empresaRequest);
        return EmpresaMapper.fromEntity(empresaRepository.save(empresaEntity));
    }


    @Override
    public Optional<EmpresaDTO> buscarPorIdOut(Long id) {
        String redisInfo = redisService.getFromRedis(Constant.REDIS_KEY_OBTENEREMPRESA+id);
        if(redisInfo != null){
            EmpresaDTO empresaDTO = Util.convertirDesdeString(redisInfo, EmpresaDTO.class);
            return Optional.of(empresaDTO);
        }else{
            Optional<EmpresaEntity> opEmpresa=empresaRepository.findById(id);
            if(opEmpresa.isEmpty()) return null;

            EmpresaDTO empresaDTO = EmpresaMapper.fromEntity(opEmpresa.get());
            String dataParaRedis = Util.convertirAString(empresaDTO);
            redisService.saveInRedis(Constant.REDIS_KEY_OBTENEREMPRESA+id,dataParaRedis,Constant.TIME_EXPIRE_REDIS);
            return Optional.of(empresaDTO);
        }
    }

    @Override
    public List<EmpresaDTO> obtenerTodosOut() {
        return empresaRepository.findAll().stream().map(EmpresaMapper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public EmpresaDTO actualizarOut(Long id, EmpresaRequest empresaRequest) {
        Optional<EmpresaEntity> opEmpresa=empresaRepository.findById(id);
        if(opEmpresa.isEmpty()) return null;

        return updateRedis(id, empresaRepository.save(getEmpresaModifi(opEmpresa.get(), empresaRequest)) );
    }

    @Override
    public EmpresaDTO deleteOut(Long id) {
        Optional<EmpresaEntity> opEmpresa=empresaRepository.findById(id);
        if(opEmpresa.isEmpty()) return null;

        return updateRedis(id, empresaRepository.save(getEmpresaDelete(opEmpresa.get())));
    }

    private EmpresaDTO updateRedis(Long id,EmpresaEntity entity){
        EmpresaDTO empresaDTO= EmpresaMapper.fromEntity(entity);
        String dataRedis=Util.convertirAString(empresaDTO);
        redisService.updateInRedis(Constant.REDIS_KEY_OBTENEREMPRESA+id,dataRedis,Constant.TIME_EXPIRE_REDIS);
        return empresaDTO;
    }
    private EmpresaEntity getEmpresaCreate(EmpresaRequest empresaRequest){
        EmpresaEntity entity= new EmpresaEntity();
        getEntity(entity, empresaRequest);
        entity.setEstado(Constant.STATUS_ACTIVE);
        entity.setUsuaCrea(Constant.USU_ADMIN);
        entity.setDateCreate(getTimestamp());
        return entity;
    }

    private EmpresaEntity getEmpresaModifi(EmpresaEntity empresaEntity, EmpresaRequest empresaRequest){
        getEntity(empresaEntity, empresaRequest);
        empresaEntity.setUsuaModif(Constant.USU_ADMIN);
        empresaEntity.setDateModif(getTimestamp());
        return empresaEntity;
    }
    private EmpresaEntity getEmpresaDelete(EmpresaEntity empresaEntity){
        empresaEntity.setEstado(Constant.STATUS_INACTIVE);
        empresaEntity.setUsuaDelet(Constant.USU_ADMIN);
        empresaEntity.setDateDelet(getTimestamp());
        return empresaEntity;
    }
    private void getEntity(EmpresaEntity entity,EmpresaRequest empresaRequest) {
        //LLamando a mi metodo de apoyo que ejecuta la peticion en el cliente externo
        SunatDTO sunatDTO = getExecutionSunat(empresaRequest.getNumDoc());

        entity.setRazonSocial(sunatDTO.getRazonSocial());
        entity.setTipoDocumento(sunatDTO.getTipoDocumento());
        entity.setNumeroDocumento(sunatDTO.getNumeroDocumento());
        entity.setCondicion(sunatDTO.getCondicion());
        entity.setDireccion(sunatDTO.getDireccion());
        entity.setDistrito(sunatDTO.getDistrito());
        entity.setProvincia(sunatDTO.getDistrito());
        entity.setDepartamento(sunatDTO.getDepartamento());
        entity.setEsAgenteRetencion(sunatDTO.isEsAgenteRetencion());
    }

    private Timestamp getTimestamp(){
        long currenTIme = System.currentTimeMillis();
        return new Timestamp(currenTIme);
    }

    private SunatDTO getExecutionSunat(String numero){
        String authorization="Bearer "+tokenSunat;
        return clientSunat.getInfoSunat(numero, authorization);
    }
}
