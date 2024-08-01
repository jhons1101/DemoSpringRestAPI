package com.jsve.restfullapi.dto;

import com.jsve.restfullapi.errors.dto.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseApi {

    @Schema(description = "Respuesta en caso de éxito", example = "ResponseDTO.class")
    private ResponseDTO response;

    @Schema(description = "Respuesta en caso de error", example = "ErrorMessage.class")
    private ErrorMessage errors;
}
