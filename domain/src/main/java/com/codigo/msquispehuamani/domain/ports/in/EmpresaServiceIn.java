package com.codigo.msquispehuamani.domain.ports.in;

import com.codigo.msquispehuamani.domain.aggregates.dto.EmpresaDTO;
import com.codigo.msquispehuamani.domain.aggregates.dto.PersonaDTO;
import com.codigo.msquispehuamani.domain.aggregates.request.EmpresaRequest;
import com.codigo.msquispehuamani.domain.aggregates.request.PersonaRequest;

import java.util.List;
import java.util.Optional;

public interface EmpresaServiceIn {
    EmpresaDTO crearEmpresaIn(EmpresaRequest empresaRequest);
    Optional<EmpresaDTO> buscarPorIdIn(Long id);
    List<EmpresaDTO> obtenerTodosIn();
    EmpresaDTO actualizarIn(Long id, EmpresaRequest empresaRequest);
    EmpresaDTO deleteIn(Long id);
}
