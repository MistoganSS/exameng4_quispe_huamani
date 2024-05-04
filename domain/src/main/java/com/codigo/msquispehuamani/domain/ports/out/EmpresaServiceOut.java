package com.codigo.msquispehuamani.domain.ports.out;

import com.codigo.msquispehuamani.domain.aggregates.dto.EmpresaDTO;
import com.codigo.msquispehuamani.domain.aggregates.request.EmpresaRequest;

import java.util.List;
import java.util.Optional;

public interface EmpresaServiceOut {
    EmpresaDTO crearEmpresaOut(EmpresaRequest empresaRequest);
    Optional<EmpresaDTO> buscarPorIdOut(Long id);
    List<EmpresaDTO> obtenerTodosOut();
    EmpresaDTO actualizarOut(Long id, EmpresaRequest empresaRequest);
    EmpresaDTO deleteOut(Long id);
}
