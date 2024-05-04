package com.codigo.msquispehuamani.domain.ports.in;

import com.codigo.msquispehuamani.domain.aggregates.dto.PersonaDTO;
import com.codigo.msquispehuamani.domain.aggregates.request.PersonaRequest;

import java.util.List;
import java.util.Optional;

public interface PersonaServiceIn {
    PersonaDTO crearPersonaIn(PersonaRequest personaRequest);
    Optional<PersonaDTO> buscarPorIdIn(Long id);
    List<PersonaDTO> obtenerTodosIn();
    PersonaDTO actualizarIn(Long id, PersonaRequest personaRequest);
    PersonaDTO deleteIn(Long id);
}
