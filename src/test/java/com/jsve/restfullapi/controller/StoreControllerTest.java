package com.jsve.restfullapi.controller;

import com.google.gson.Gson;
import com.jsve.restfullapi.dto.ResponseApi;
import com.jsve.restfullapi.entity.Store;
import com.jsve.restfullapi.errors.StoreNotFoundException;
import com.jsve.restfullapi.errors.dto.ErrorMessage;
import com.jsve.restfullapi.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoreController.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    private Store store1;
    private Store store2;
    private List<Store> storelist = new ArrayList<>();

    @BeforeEach
    void setUp() {

        store1 = Store.builder()
                .id(1L)
                .name("Tienda 1")
                .code("001")
                .local("101")
                .build();

        store2 = Store.builder()
                .id(2L)
                .name("Tienda 2")
                .code("002")
                .local("102")
                .build();

        storelist.add(store1);
        storelist.add(store2);

    }

    @Test
    public void findAllStores() throws Exception {

        Mockito.when(storeService.findAllStores()).thenReturn(storelist);

        mockMvc.perform(get("/store/findAllStores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(storelist.size())))
                .andExpect(jsonPath("$[0].id").value(storelist.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(storelist.get(0).getName()));
    }

    @Test
    public void findStoreById() throws Exception {

        Long storeId = 1L;
        Mockito.when(storeService.findStoreById(storeId)).thenReturn(store1);

        mockMvc.perform(get("/store/findStoreById/{id}", storeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(store1.getName()));
    }

    @Test
    public void findStoreByIdReturnException() throws Exception {

        Long storeId = 1L;
        String expectedResponse = "La tienda con Id no existe";
        StoreNotFoundException storeIDNotFoundException = new StoreNotFoundException(expectedResponse);
        Mockito.when(storeService.findStoreById(storeId)).thenThrow(storeIDNotFoundException);

        mockMvc.perform(get("/store/findStoreById/{id}", storeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedResponse));
    }

    @Test
    public void findByNameIgnoreCase() throws Exception {

        String expectedResponse  = "tienda 1";
        Long expectedStoreId = 1L;
        Mockito.when(storeService.findByNameIgnoreCase(expectedResponse)).thenReturn(store1);

        mockMvc.perform(get("/store/findByNameIgnoreCase")
                        .param("name", expectedResponse))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedStoreId));
    }

    @Test
    public void findByNameIgnoreCaseReturnException() throws Exception {

        String expectedNameResponse  = "tienda 3";
        String msgException = "La tienda con el name enviado no existe";
        StoreNotFoundException storeNameNotFoundException = new StoreNotFoundException(msgException);
        Mockito.when(storeService.findByNameIgnoreCase(expectedNameResponse)).thenThrow(storeNameNotFoundException);

        mockMvc.perform(get("/store/findByNameIgnoreCase")
                        .param("name", expectedNameResponse))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(msgException));
    }

    @Test
    public void findByName() throws Exception {

        String expectedNameResponse  = "Tienda";
        Mockito.when(storeService.filterByName("Tienda")).thenReturn(storelist);

        mockMvc.perform(get("/store/filterByName")
                        .param("name", expectedNameResponse))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Tienda 1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Tienda 2"));
    }

    @Test
    public void findByNameReturnException() throws Exception {

        String expectedNameResponse  = "Nike";
        String msgException = "No hay tiendas con el name enviado";
        StoreNotFoundException notStoreFoundException = new StoreNotFoundException(msgException);
        Mockito.when(storeService.filterByName(expectedNameResponse)).thenThrow(notStoreFoundException);

        mockMvc.perform(get("/store/filterByName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", expectedNameResponse))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(msgException));
    }

    @Test
    public void saveStore() throws Exception {

        Long storeId = 1L;
        Store postStore = new Store();
        postStore.setName("Tienda 1");
        postStore.setCode("001");
        postStore.setLocal("101");

        String storeJson = new Gson().toJson(postStore);
        Mockito.when(storeService.saveStore(postStore)).thenReturn(store1);

        this.mockMvc.perform(post("/store/saveStore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(storeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(storeId));
    }

    @Test
    public void saveStoreReturnException404() throws Exception {

        String msgException = "";
        StoreNotFoundException saveErrorStoreException = new StoreNotFoundException(msgException);

        Store postStore = new Store();
        postStore.setName("Tienda 1");
        postStore.setCode("12345678901");
        postStore.setLocal("101");

        String storeJson = new Gson().toJson(postStore);
        Mockito.when(storeService.saveStore(postStore)).thenThrow(saveErrorStoreException);

        this.mockMvc.perform(post("/store/saveStore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(storeJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveStoreReturnException500() throws Exception {

        String msgException = "";
        StoreNotFoundException saveErrorStoreException = new StoreNotFoundException(msgException);

        Store postStore = new Store();
        postStore.setName("Tienda 1");
        postStore.setCode("1234567890");
        postStore.setLocal("101");
        postStore.setHola(11);

        String storeJson = new Gson().toJson(postStore);
        Mockito.when(storeService.saveStore(postStore)).thenThrow(saveErrorStoreException);

        this.mockMvc.perform(post("/store/saveStore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(storeJson))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateStore() throws Exception {

        Long storeId = 1L;
        store1.setName("Nueva Tienda");
        String storeJson = new Gson().toJson(store1);
        Mockito.when(storeService.updateStore(storeId, store1)).thenReturn(store1);

        this.mockMvc.perform(put("/store/updateStore/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(storeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(storeId));
    }

    @Test
    public void updateStoreReturnException500() throws Exception {

        Long storeId = 3L;
        String msgException = "";
        StoreNotFoundException updateErrorStoreException = new StoreNotFoundException(msgException);
        store1.setName("Nueva Tienda");

        String storeJson = new Gson().toJson(store1);
        Mockito.when(storeService.updateStore(storeId, store1)).thenThrow(updateErrorStoreException);

        this.mockMvc.perform(put("/store/updateStore/{id}", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(storeJson))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void deleteStore() throws Exception {

        Long storeId = 1L;
        String expectedResponse  = "Tienda borrada correctamente";
        Mockito.when(storeService.deleteStore(storeId)).thenReturn(expectedResponse );

        this.mockMvc.perform(delete("/store/deleteStore/{id}", storeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.message").value(expectedResponse));
    }

    @Test
    public void deleteStoreReturnException404() throws Exception {

        Long storeId = 1L;
        String expectedResponse = "La tienda con Id no existe";
        ResponseApi responseApi = new ResponseApi();
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(expectedResponse);
        responseApi.setErrors(errorMessage);
        StoreNotFoundException deleteErrorStoreException = new StoreNotFoundException(expectedResponse);

        Mockito.when(storeService.deleteStore(storeId)).thenThrow(deleteErrorStoreException);

        this.mockMvc.perform(delete("/store/deleteStore/{id}", storeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.message").value(expectedResponse));
    }

    @Test
    public void deleteStoreReturnException500() throws Exception {

        Long storeId = 1L;
        String expectedResponse = "Error inesperado";
        ResponseApi responseApi = new ResponseApi();
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(expectedResponse);
        responseApi.setErrors(errorMessage);

        Mockito.when(storeService.deleteStore(storeId)).thenThrow(new Exception(expectedResponse));

        this.mockMvc.perform(delete("/store/deleteStore/{id}", storeId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errors.message").value(expectedResponse));
    }

}