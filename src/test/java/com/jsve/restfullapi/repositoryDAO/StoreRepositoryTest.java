package com.jsve.restfullapi.repositoryDAO;

import com.jsve.restfullapi.entity.Store;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        Store store_1 = Store.builder()
                .name("Americanino")
                .code("Ame01")
                .local("104")
                .build();

        Store store_2 =Store.builder()
                .name("Levis")
                .code("Lev01")
                .local("105")
                .build();

        testEntityManager.persist(store_1);
        testEntityManager.persist(store_2);
    }

    @Test
    public void filterByNameFound() {
        String storeName = "Americanino";
        List<Store> storeList =  storeRepository.filterByName(storeName);
        assertFalse(storeList.isEmpty());
        assertEquals(storeList.get(0).getName(), storeName);
    }

    @Test
    public void filterByNameNotFound() {
        String storeName = "Adidas";
        List<Store> storeList =  storeRepository.filterByName(storeName);
        assertTrue(storeList.isEmpty());
    }

    @Test
    public void findStoreByIdFound() {
        List<Store> storeList = storeRepository.findAll();
        Long id = storeList.get(0).getId();
        Optional<Store> store = storeRepository.findStoreById(id);
        assertTrue(store.isPresent());
    }

    @Test
    public void findStoreByIdNotFound() {
        Long id = 3L;
        Optional<Store> store = storeRepository.findStoreById(id);
        assertTrue(store.isEmpty());

    }

    @Test
    public void findByNameIgnoreCaseFound() {
        String storeName = "Americanino";
        Optional<Store> store = storeRepository.findByNameIgnoreCase(storeName);
        assertTrue(store.isPresent());
        assertEquals(store.get().getName(), storeName);
    }

    @Test
    public void findByNameIgnoreCaseNotFound() {
        String storeName = "Ela";
        Optional<Store> store = storeRepository.findByNameIgnoreCase(storeName);
        assertTrue(store.isEmpty());
    }
}