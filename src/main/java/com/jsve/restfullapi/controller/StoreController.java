package com.jsve.restfullapi.controller;

import com.jsve.restfullapi.dto.ResponseDTO;
import com.jsve.restfullapi.entity.Store;
import com.jsve.restfullapi.errors.StoreNotFoundException;
import com.jsve.restfullapi.dto.ResponseApi;
import com.jsve.restfullapi.errors.dto.ErrorMessage;
import com.jsve.restfullapi.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Store API", description = "Métodos de consulta y transacción para operaciónes con la tabla Store")
public class StoreController {

    @Autowired
    StoreService storeService;

    private ResponseApi responseApi;
    private ResponseDTO responseDTO;
    private ErrorMessage errorMessage;


    @GetMapping("/store/findAllStores")
    @Tag(name = "Consultas", description = "Consultas del API Store")
    @Operation(summary = "Obtener todas las tiendas", description = "Obtiene el listado de tiendas desde la DB")
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Listado de tiendas encontradas",
                content = { @Content(
                        array = @ArraySchema(schema = @Schema (implementation = Store.class)),
                        mediaType = "application/json")
                }),
        @ApiResponse(
                responseCode = "404",
                description = "No hay tiendas en la DB",
                content = { @Content(schema = @Schema(implementation = ResponseApi.class), mediaType = "application/json") })
    })
    List<Store> findAllStores() throws StoreNotFoundException {
        return storeService.findAllStores();
    }


    @GetMapping("/store/findStoreById/{id}")
    @Tag(name = "Consultas", description = "Consultas del API Store")
    @Operation(summary = "Buscar una tienda por id", description = "Busca una tienda por id y si no la encuentra retorna un 404 response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tienda encontrada"),
            @ApiResponse(responseCode = "404", description = "La tienda con Id no existe")
    })
    Store findStoreById(
            @Parameter(name =  "id", description  = "Identificator store", example = "1", required = true) @PathVariable Long id
    ) throws StoreNotFoundException {
        return storeService.findStoreById(id);
    }


    @GetMapping("/store/findByNameIgnoreCase")
    @Tag(name = "Consultas", description = "Consultas del API Store")
    @Operation(summary = "Buscar una tienda por el name omitiendo mayusculas y minusculas", description = "Buscar una tienda por el name enviado omitiendo mayusculas y minusculas")
    @Parameter(name = "name", description = "Nombre de la tienda a buscar", example = "tienda")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Tienda encontrada",
                    content = { @Content( schema = @Schema (implementation = Store.class), mediaType = "application/json") }),
            @ApiResponse(
                    responseCode = "404", description = "La tienda con el name enviado no existe",
                    content = { @Content(schema = @Schema(implementation = ResponseApi.class), mediaType = "application/json")})
    })
    Store findByNameIgnoreCase(@RequestParam(required = true) String name) throws StoreNotFoundException{
        return storeService.findByNameIgnoreCase(name);
    }

    @GetMapping("/store/filterByName")
    @Tag(name = "Consultas", description = "Consultas del API Store")
    @Operation(summary = "Busca todas las tiendas que coincidan con el name enviado", description = "Busca todas las tiendas que coincidan con el name enviado")
    @Parameter(name = "name", description = "Nombre de la tienda a buscar", example = "tienda")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de tiendas encontradas",
                    content = { @Content(
                            array = @ArraySchema(schema = @Schema (implementation = Store.class)),
                            mediaType = "application/json")
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "No hay tiendas con el name enviado",
                    content = { @Content(schema = @Schema(implementation = ResponseApi.class), mediaType = "application/json")
                    })
    })
    List<Store> findByName(@RequestParam(required = true) String name) throws StoreNotFoundException {
        return storeService.filterByName(name);
    }


    @PostMapping("/store/saveStore")
    @Operation( summary = "Guarda un tienda en la DB", tags = { "Transacciones" },
                description = "Valida los campos obligatorios para su guardado y notifica en caso de fallo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Tienda creada",
                    content = { @Content(schema = @Schema (implementation = Store.class), mediaType = "application/json")}),
            @ApiResponse(
                    responseCode = "500", description = "Error generado en el server/DB",
                    content = { @Content(schema = @Schema(implementation = ResponseApi.class), mediaType = "application/json")
                    })
    })
    ResponseEntity<Store> saveStore(@Valid @RequestBody Store store) throws StoreNotFoundException {
        Store storeDB = null;
        try {
            storeDB = storeService.saveStore(store);
            return ResponseEntity.status(HttpStatus.CREATED).body(storeDB);
        } catch (StoreNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(storeDB);
        }
    }


    @PutMapping("/store/updateStore/{id}")
    @Tag(name = "Transacciones", description = "Transacciones en el API Store")
    @Operation( summary = "Actualiza un tienda de la DB", tags = { "Transacciones" },
                description = "Actualiza una tienda y notifica en caso de fallo")
    ResponseEntity<Store> updateStore(@PathVariable Long id, @RequestBody Store store) throws StoreNotFoundException {
        Store storeDB = null;
        try {
            storeDB = storeService.updateStore(id, store);
            return ResponseEntity.status(HttpStatus.OK).body(storeDB);
        } catch (StoreNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(storeDB);
        }
    }


    @DeleteMapping("/store/deleteStore/{id}")
    @Tag(name = "Transacciones", description = "Transacciones en el API Store")
    @Operation( summary = "Elimina un tienda en la DB por su ID", tags = { "Transacciones" },
                description = "Elimina una tienda y notifica en caso de fallo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Tienda borrada correctamente",
                    content = { @Content(schema = @Schema (implementation = ResponseApi.class), mediaType = "application/json")}),
            @ApiResponse(
                    responseCode = "404", description = "La tienda con Id no existe",
                    content = { @Content(schema = @Schema(implementation = ResponseApi.class), mediaType = "application/json")}),
            @ApiResponse(
                    responseCode = "500", description = "Error generado en el server/DB",
                    content = { @Content(schema = @Schema(implementation = ResponseApi.class), mediaType = "application/json")})
    })
    ResponseEntity<ResponseApi> deleteStore(@PathVariable Long id) throws StoreNotFoundException {

        HttpStatus httpStatus = null;
        responseApi = new ResponseApi();

        try {
            String response = storeService.deleteStore(id);
            responseDTO = new ResponseDTO();
            responseDTO.setMessage(response);
            responseApi.setResponse(responseDTO);
            httpStatus = HttpStatus.OK;
        } catch (StoreNotFoundException e) {
            errorMessage = new ErrorMessage();
            errorMessage.setMessage(e.getMessage());
            responseApi.setErrors(errorMessage);
            httpStatus = HttpStatus.NOT_FOUND;
        } catch (Exception e) {
            errorMessage = new ErrorMessage();
            errorMessage.setMessage(e.getMessage());
            responseApi.setErrors(errorMessage);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(httpStatus).body(responseApi);
    }
}
