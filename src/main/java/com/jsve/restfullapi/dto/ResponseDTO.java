package com.jsve.restfullapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {

    @Schema(description = "Mensaje de respuesta del servicio", example = "Tienda borrada correctamente")
    private String message;
}
