package com.jsve.restfullapi.service;

import com.jsve.restfullapi.entity.Store;
import com.jsve.restfullapi.errors.StoreNotFoundException;
import com.jsve.restfullapi.repositoryDAO.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class StoreServiceTest {

    @Autowired
    private StoreService storeService;

    @MockBean
    private StoreRepository storeRepository;

    private Store store_1, store_2;
    private List<Store> storeList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        store_1 = Store.builder()
                        .id(1L)
                        .name("nike")
                        .code("01")
                        .local("001")
                        .build();

        store_2 = Store.builder()
                        .id(2L)
                        .name("Tienda")
                        .code("02")
                        .local("002")
                        .build();
    }

    @Test
    void findAllStoresFound() throws StoreNotFoundException {

        storeList.add(store_1);
        storeList.add(store_2);

        Mockito.when(storeRepository.findAll()).thenReturn(storeList);

        List<Store> stores = storeService.findAllStores();
        assertFalse(stores.isEmpty());
    }

    @Test
    void findAllStoresException() throws StoreNotFoundException {

        String msgException = null;
        List<Store> stores = null;

        try {
            Mockito.when(storeRepository.findAll()).thenReturn(storeList);
            stores = storeService.findAllStores();
        } catch (StoreNotFoundException e) {
            msgException = e.getMessage();
        }
        assertNull(stores);
        assertNotNull(msgException);
    }

    @Test
    void findStoreById() throws StoreNotFoundException {

        Optional<Store> store;
        Long storeId = 1L;

        Mockito.when(storeRepository.findStoreById(storeId)).thenReturn(Optional.of(store_1));
        store = Optional.ofNullable(storeService.findStoreById(storeId));

        assertTrue(store.isPresent());
    }

    @Test
    void findStoreByIdException() throws StoreNotFoundException {

        String msgException = null;
        Optional<Store> store = Optional.empty();
        Long storeId = 1L;

        try {
            Mockito.when(storeRepository.findStoreById(storeId)).thenReturn(Optional.empty());
            store = Optional.ofNullable(storeService.findStoreById(storeId));
        } catch (StoreNotFoundException e) {
            msgException = e.getMessage();
        }
        assertTrue(store.isEmpty());
        assertNotNull(msgException);
    }

    @Test
    public void findByNameIgnoreCaseFound() throws StoreNotFoundException {

        String storeName = "Nike";
        Mockito.when(storeRepository.findByNameIgnoreCase(storeName))
                .thenReturn(Optional.ofNullable(store_1));

        Optional<Store> store = Optional.ofNullable(storeService.findByNameIgnoreCase(storeName));
        assertTrue(store.isPresent());
    }

    @Test
    void findByNameIgnoreCaseException() throws StoreNotFoundException {

        String storeName = "Adidas";
        String expectedResponse = "La tienda con el name enviado no existe";
        String storeExep = null;
        Optional<Store> store = null;

        try {
            Mockito.when(storeRepository.findByNameIgnoreCase(storeName))
                    .thenReturn(Optional.empty());
            store = Optional.ofNullable(storeService.findByNameIgnoreCase(storeName));
        } catch (Exception e) {
            storeExep = e.getMessage();
        }
        assertNull(store);
        assertNotNull(storeExep);
    }

    @Test
    void filterByName() throws StoreNotFoundException {

        String storeName = "nike";
        List<Store> storesFound = new ArrayList<>();
        storeList.add(store_1);

        Mockito.when(storeRepository.filterByName(storeName)).thenReturn(storeList);
        storesFound = storeService.filterByName(storeName);

        assertFalse(storesFound.isEmpty());
    }

    @Test
    void filterByNameException() throws StoreNotFoundException {

        String storeName = "Adidas";
        String expectedResponse = "No hay tiendas con el name enviado";
        List<Store> storesFound = null;
        String msgException = null;

        try {
            Mockito.when(storeRepository.filterByName(storeName)).thenReturn(storeList);
            storesFound = storeService.filterByName(storeName);
        } catch (StoreNotFoundException e) {
            msgException = e.getMessage();
        }

        assertNull(storesFound);
        assertNotNull(msgException);
    }

    @Test
    void saveStore() throws StoreNotFoundException {
        Store store = Store.builder()
                .id(1L)
                .name("Tienda")
                .code("003")
                .local("101")
                .build();
        Store storeReturn;

        Mockito.when(storeRepository.save(store)).thenReturn(store);
        storeReturn = storeService.saveStore(store);

        assertEquals(1L, store.getId());
        assertNotNull(store.toString());
    }

    @Test
    void saveStoreException() throws StoreNotFoundException {

        Store store = new Store();
        String msgExpect = "";

        Mockito.when(storeRepository.save(store)).thenThrow(new RuntimeException(msgExpect));

        StoreNotFoundException storeNotFoundException = assertThrows(StoreNotFoundException.class, () -> {
            storeService.saveStore(store);
        });
    }

    @Test
    void updateStore() throws StoreNotFoundException {

        Store updatedStore  = null;
        String updatedName = "Tienda update";
        store_1.setName(updatedName);

        Mockito.when(storeRepository.findById(1L)).thenReturn(Optional.of(store_1));
        Mockito.when(storeRepository.save(store_1)).thenReturn(store_1);
        updatedStore  = storeService.updateStore(store_1.getId(), store_1);

        assertEquals(updatedName, updatedStore.getName());
        verify(storeRepository).save(store_1);
    }

    @Test
    void updateStoreException() {

        Long storeId = 1L;
        Mockito.when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThrows(StoreNotFoundException.class, () -> {
            storeService.updateStore(storeId, store_1);
        });
    }

    @Test
    void updateStoreWithNoChanges() throws StoreNotFoundException {

        Long storeId = 1L;
        Store store = new Store();

        Mockito.when(storeRepository.findById(storeId)).thenReturn(Optional.of(store_1));
        Mockito.when(storeRepository.save(store_1)).thenReturn(store_1);

        Store updatedStore = storeService.updateStore(storeId, store);

        assertEquals("nike", store_1.getName());
        verify(storeRepository).save(store_1);
    }

    @Test
    void deleteStore() throws Exception {

        Long storeId = 1L;
        String expectedMsg = null;
        String msg = "Tienda borrada correctamente";

        Mockito.when(storeRepository.findStoreById(storeId)).thenReturn(Optional.ofNullable(store_1));
        expectedMsg = storeService.deleteStore(storeId);

        assertNotNull(expectedMsg);
        assertEquals(msg, expectedMsg);
        verify(storeRepository).deleteById(storeId);
    }

    @Test
    void deleteStoreNotFoundException() {

        Long storeId = 1L;

        Mockito.when(storeRepository.findStoreById(storeId)).thenReturn(Optional.empty());

        assertThrows(StoreNotFoundException.class, () -> {
            storeService.deleteStore(storeId);
        });
    }

    @Test
    void deleteStoreThrowException() {

        Long storeId = 1L;

        Mockito.when(storeRepository.findStoreById(storeId)).thenReturn(Optional.of(store_1));
        Mockito.doThrow(new RuntimeException("Database error")).when(storeRepository).deleteById(storeId);

        assertThrows(Exception.class, () -> {
            storeService.deleteStore(storeId);
        });
    }
}