package com.codigo.msquispehuamani.infraestructure.dao;

import com.codigo.msquispehuamani.infraestructure.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<EmpresaEntity, Long> {
    boolean existsByNumeroDocumento(String numeroDocumento);
    Optional<EmpresaEntity> findByNumeroDocumentoIs(String numeroDocumento);

}
