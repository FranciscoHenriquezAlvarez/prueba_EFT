package com.duoc.cursos.bff.dto;

import jakarta.validation.constraints.NotBlank;

public class IntentoExamenRequestDTO {

    @NotBlank(message = "Las respuestasJson son obligatorias")
    private String respuestasJson;

    public String getRespuestasJson() {
        return respuestasJson;
    }

    public void setRespuestasJson(String respuestasJson) {
        this.respuestasJson = respuestasJson;
    }
}
