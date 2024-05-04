package com.codigo.msquispehuamani.infraestructure.mapper;

import com.codigo.msquispehuamani.domain.aggregates.dto.EmpresaDTO;
import com.codigo.msquispehuamani.infraestructure.entity.EmpresaEntity;

public class EmpresaMapper {
    public static EmpresaDTO fromEntity(EmpresaEntity entity){
        EmpresaDTO dto= new EmpresaDTO();
        dto.setIdEmpresa(entity.getId());
        dto.setRazonSocial(entity.getRazonSocial());
        dto.setTipoDocumento(entity.getTipoDocumento());
        dto.setNumeroDocumento(entity.getNumeroDocumento());
        dto.setCondicion(entity.getCondicion());
        dto.setDireccion(entity.getDireccion());
        dto.setDistrito(entity.getDistrito());
        dto.setProvincia(entity.getProvincia());
        dto.setDepartamento(entity.getDepartamento());
        dto.setEsAgenteRetencion(entity.isEsAgenteRetencion());
        dto.setEstado(entity.getEstado());
        dto.setUsuaCrea(entity.getUsuaCrea());
        dto.setDateCreate(entity.getDateCreate());
        dto.setUsuaModif(entity.getUsuaModif());
        dto.setDateModif(entity.getDateModif());
        dto.setUsuaDelet(entity.getUsuaDelet());
        dto.setDateDelet(entity.getDateDelet());
        return dto;
    }
}
