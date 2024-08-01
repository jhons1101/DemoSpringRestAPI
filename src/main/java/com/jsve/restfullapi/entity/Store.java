package com.jsve.restfullapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stores")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Tabla Store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identificador unico de la tienda", example = "1")
    private Long id;

    @NotNull(message = "Por favor, envíe un valor para el campo name")
    @Size(min = 1, max = 100, message = "La longitud debe de estar entre 1 y 100")
    @Schema(description = "Nombre de la tienda", example = "Tienda de prueba")
    private String name;

    @Size(min = 1, max = 10, message = "La longitud debe de estar entre 1 y 10")
    @Schema(
            description = "Código de la tienda dentro del centro comercial",
            name = "code",
            type = "string",
            example = "St-01")
    private String code;

    @Size(min = 1, max = 4, message = "La longitud debe de estar entre 1 y 4")
    @Schema(description = "Número de local dentro del centro comercial", example = "101")
    private String local;

    @Min(0)
    @Max(100)
    @Schema(description = "Número de empleados de la tienda dentro del centro comercial", example = "12")
    private Integer employees;

    private Integer hola;
}