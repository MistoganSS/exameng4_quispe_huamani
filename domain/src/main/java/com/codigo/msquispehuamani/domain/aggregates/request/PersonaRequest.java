package com.codigo.msquispehuamani.domain.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaRequest {
    private Integer tipoDocumento;
    private String numeroDoc;
    private String empresa;
    private String email;
    private String telefono;
    private String direccion;
}
