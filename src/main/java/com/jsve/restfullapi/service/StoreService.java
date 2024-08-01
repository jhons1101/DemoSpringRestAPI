package com.jsve.restfullapi.service;

import com.jsve.restfullapi.entity.Store;
import com.jsve.restfullapi.errors.StoreNotFoundException;

import java.util.List;
import java.util.Optional;

public interface StoreService {

    List<Store> findAllStores() throws StoreNotFoundException;
    Store findStoreById(Long id) throws StoreNotFoundException;
    Store findByNameIgnoreCase(String name) throws StoreNotFoundException;
    List<Store> filterByName(String name) throws StoreNotFoundException;

    Store saveStore(Store store) throws StoreNotFoundException;
    Store updateStore(Long id, Store store) throws StoreNotFoundException;
    String deleteStore(Long id) throws Exception;
}
