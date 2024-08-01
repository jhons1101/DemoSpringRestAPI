package com.jsve.restfullapi.errors.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    @Schema(description = "Mensaje de respuesta del servicio", example = "Errores del server / DB")
    private String message;
}
