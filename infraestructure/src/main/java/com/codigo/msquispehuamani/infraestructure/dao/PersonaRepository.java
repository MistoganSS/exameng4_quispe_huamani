package com.codigo.msquispehuamani.infraestructure.dao;

import com.codigo.msquispehuamani.infraestructure.entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<PersonaEntity,Long> {
}
