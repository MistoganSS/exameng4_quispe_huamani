package com.codigo.msquispehuamani.infraestructure.adapters;

import com.codigo.msquispehuamani.domain.aggregates.constants.Constant;
import com.codigo.msquispehuamani.domain.aggregates.dto.EmpresaDTO;
import com.codigo.msquispehuamani.domain.aggregates.dto.PersonaDTO;
import com.codigo.msquispehuamani.domain.aggregates.dto.ReniecDTO;
import com.codigo.msquispehuamani.domain.aggregates.request.PersonaRequest;
import com.codigo.msquispehuamani.domain.ports.out.PersonaServiceOut;
import com.codigo.msquispehuamani.infraestructure.client.ClientReniec;
import com.codigo.msquispehuamani.infraestructure.dao.EmpresaRepository;
import com.codigo.msquispehuamani.infraestructure.dao.PersonaRepository;
import com.codigo.msquispehuamani.infraestructure.entity.EmpresaEntity;
import com.codigo.msquispehuamani.infraestructure.entity.PersonaEntity;
import com.codigo.msquispehuamani.infraestructure.mapper.EmpresaMapper;
import com.codigo.msquispehuamani.infraestructure.mapper.PersonaMapper;
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
public class PersonaAdapter implements PersonaServiceOut {

    private final PersonaRepository personaRepository;
    private final EmpresaRepository empresaRepository;
    private final ClientReniec clientReniec;
    private final RedisService redisService;

    @Value("${token.apis}")
    private String tokenReniec;

    @Override
    public PersonaDTO crearPersonaOut(PersonaRequest personaRequest) {
        if(empresaRepository.existsByNumeroDocumento(personaRequest.getEmpresa())){
            PersonaEntity personaEntity = getPersonaCreate(personaRequest);
            return PersonaMapper.fromEntity(personaRepository.save(personaEntity));
        }
        throw new RuntimeException("Empresa no encontrada, RUC: "+personaRequest.getEmpresa());
    }


    @Override
    public Optional<PersonaDTO> buscarPorIdOut(Long id) {
        String redisInfo = redisService.getFromRedis(Constant.REDIS_KEY_OBTENERPERSONA+id);
        if(redisInfo != null){
            PersonaDTO personaDTO = Util.convertirDesdeString(redisInfo, PersonaDTO.class);
            return Optional.of(personaDTO);
        }else{
            Optional<PersonaEntity> entity=personaRepository.findById(id);
            if(entity.isEmpty()) return null;

            PersonaDTO personaDTO = PersonaMapper.fromEntity(entity.get());
            EmpresaDTO empresaDTO= EmpresaMapper.fromEntity(entity.get().getEmpresa());
            personaDTO.setEmpresa(empresaDTO);
            String dataParaRedis = Util.convertirAString(personaDTO);
            redisService.saveInRedis(Constant.REDIS_KEY_OBTENERPERSONA+id,dataParaRedis,Constant.TIME_EXPIRE_REDIS);
            return Optional.of(personaDTO);
        }
    }

    @Override
    public List<PersonaDTO> obtenerTodosOut() {
        return personaRepository.findAll().stream().map(PersonaMapper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public PersonaDTO actualizarOut(Long id, PersonaRequest personaRequest) {
        Optional<PersonaEntity> opPersona=personaRepository.findById(id);
        if(opPersona.isEmpty()) return null;

        return updateRedis(id, personaRepository.save(getPersonaModifi(opPersona.get(), personaRequest)));
    }

    @Override
    public PersonaDTO deleteOut(Long id) {
        Optional<PersonaEntity> opPersona=personaRepository.findById(id);
        if(opPersona.isEmpty()) return null;

        return updateRedis(id,personaRepository.save(getPersonaDelete(opPersona.get())));
    }
    private PersonaDTO updateRedis(Long id,PersonaEntity entity){
        PersonaDTO personaDTO= PersonaMapper.fromEntity(entity);
        String dataRedis=Util.convertirAString(personaDTO);
        redisService.updateInRedis(Constant.REDIS_KEY_OBTENEREMPRESA+id,dataRedis,Constant.TIME_EXPIRE_REDIS);
        return personaDTO;
    }
    private PersonaEntity getPersonaCreate(PersonaRequest personaRequest){
        PersonaEntity entity= new PersonaEntity();
        getEntity(entity, personaRequest);
        entity.setEstado(Constant.STATUS_ACTIVE);
        entity.setUsuaCrea(Constant.USU_ADMIN);
        entity.setDateCreate(getTimestamp());
        return entity;
    }

    private PersonaEntity getPersonaModifi(PersonaEntity personaEntity, PersonaRequest personaRequest){
        getEntity(personaEntity, personaRequest);
        personaEntity.setUsuaModif(Constant.USU_ADMIN);
        personaEntity.setDateModif(getTimestamp());
        return personaEntity;
    }
    private PersonaEntity getPersonaDelete(PersonaEntity personaEntity){
        personaEntity.setEstado(Constant.STATUS_INACTIVE);
        personaEntity.setUsuaDelet(Constant.USU_ADMIN);
        personaEntity.setDateDelet(getTimestamp());
        return personaEntity;
    }
    private void getEntity(PersonaEntity entity,PersonaRequest personaRequest) {
        //LLamando a mi metodo de apoyo que ejecuta la peticion en el cliente externo
        ReniecDTO reniecDTO = getExecutionReniec(personaRequest.getNumeroDoc());

        entity.setNombre(reniecDTO.getNombres());
        entity.setApellido(reniecDTO.getApellidoPaterno() +" "+ reniecDTO.getApellidoMaterno());
        entity.setTipoDocumento(reniecDTO.getTipoDocumento());
        entity.setNumeroDocumento(reniecDTO.getNumeroDocumento());
        entity.setEmail(personaRequest.getEmail());
        entity.setTelefono(personaRequest.getTelefono());
        entity.setDireccion(personaRequest.getDireccion());

        Optional<EmpresaEntity> empresaEntity= empresaRepository.findByNumeroDocumentoIs(personaRequest.getEmpresa());
        if(empresaEntity.isEmpty()) throw new RuntimeException("Empresa no encontrada o modificada, RUC: "+personaRequest.getEmpresa());

        entity.setEmpresa(empresaEntity.get());
    }

    private Timestamp getTimestamp(){
        long currenTIme = System.currentTimeMillis();
        return new Timestamp(currenTIme);
    }

    private ReniecDTO getExecutionReniec(String numero){
        String authorization = "Bearer "+tokenReniec;
        return clientReniec.getInfoReniec(numero,authorization);
    }
}
