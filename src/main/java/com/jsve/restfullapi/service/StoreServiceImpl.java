package com.jsve.restfullapi.service;

import com.jsve.restfullapi.entity.Store;
import com.jsve.restfullapi.errors.StoreNotFoundException;
import com.jsve.restfullapi.repositoryDAO.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StoreServiceImpl  implements StoreService {

    @Autowired
    StoreRepository storeRepository;

    @Override
    public List<Store> findAllStores() throws StoreNotFoundException {
        List<Store> listado =  storeRepository.findAll();
        if (listado.isEmpty()) {
            throw new StoreNotFoundException("No hay tiendas en la DB");
        }
        return listado;
    }

    @Override
    public Store findStoreById(Long id) throws StoreNotFoundException {
        Optional<Store> store = storeRepository.findStoreById(id);
        if (!store.isPresent()) {
            throw new StoreNotFoundException("La tienda con Id no existe");
        }
        return store.get();
    }

    @Override
    public Store findByNameIgnoreCase(String name) throws StoreNotFoundException {
        Optional<Store> store = storeRepository.findByNameIgnoreCase(name);
        if (!store.isPresent()) {
            throw new StoreNotFoundException("La tienda con el name enviado no existe");
        }
        return store.get();
    }

    @Override
    public List<Store> filterByName(String name) throws StoreNotFoundException {
        List<Store> listado = storeRepository.filterByName(name);
        if (listado.isEmpty()) {
            throw new StoreNotFoundException("No hay tiendas con el name enviado");
        }
        return listado;
    }

    @Override
    public Store saveStore(Store store) throws StoreNotFoundException {
        Store response = null;
        try {
            response = storeRepository.save(store);
        } catch (Exception e) {
            throw new StoreNotFoundException(e.getMessage());
        }
        return response;
    }

    @Override
    public Store updateStore(Long id, Store store) throws StoreNotFoundException {

        Store response = null;

        try {
            Store storeDB = storeRepository.findById(id).get();

            if (Objects.nonNull(store.getName()) && !"".equalsIgnoreCase(store.getName())) {
                storeDB.setName(store.getName());
            }

            if (Objects.nonNull(store.getLocal()) && !"".equalsIgnoreCase(store.getLocal())) {
                storeDB.setLocal(store.getLocal());
            }

            if (Objects.nonNull(store.getCode()) && !"".equalsIgnoreCase(store.getCode())) {
                storeDB.setCode(store.getCode());
            }
            response = storeRepository.save(storeDB);
        } catch (Exception e) {
            throw new StoreNotFoundException(e.getMessage());
        }
        return response;
    }

    @Override
    public String deleteStore(Long id) throws Exception, StoreNotFoundException {
        try {
            Optional<Store> store = storeRepository.findStoreById(id);
            if (!store.isPresent()) {
                throw new StoreNotFoundException("La tienda con Id no existe");
            }
            storeRepository.deleteById(id);
            return "Tienda borrada correctamente";
        } catch (StoreNotFoundException e) {
            throw new StoreNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
