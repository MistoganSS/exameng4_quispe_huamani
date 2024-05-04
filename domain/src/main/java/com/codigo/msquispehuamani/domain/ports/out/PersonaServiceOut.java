package com.codigo.msquispehuamani.domain.ports.out;

import com.codigo.msquispehuamani.domain.aggregates.dto.PersonaDTO;
import com.codigo.msquispehuamani.domain.aggregates.request.PersonaRequest;

import java.util.List;
import java.util.Optional;

public interface PersonaServiceOut {
    PersonaDTO crearPersonaOut(PersonaRequest personaRequest);
    Optional<PersonaDTO> buscarPorIdOut(Long id);
    List<PersonaDTO> obtenerTodosOut();
    PersonaDTO actualizarOut(Long id, PersonaRequest personaRequest);
    PersonaDTO deleteOut(Long id);
}
