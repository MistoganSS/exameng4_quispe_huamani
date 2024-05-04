package com.codigo.msquispehuamani.infraestructure.mapper;

import com.codigo.msquispehuamani.domain.aggregates.dto.PersonaDTO;
import com.codigo.msquispehuamani.infraestructure.entity.PersonaEntity;

public class PersonaMapper {
    public static PersonaDTO fromEntity(PersonaEntity entity) {
        PersonaDTO dto = new PersonaDTO();
        dto.setIdPersona(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setTipoDocumento(entity.getTipoDocumento());
        dto.setNumeroDocumento(entity.getNumeroDocumento());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setDireccion(entity.getDireccion());
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
