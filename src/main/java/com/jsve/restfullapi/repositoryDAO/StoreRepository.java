package com.jsve.restfullapi.repositoryDAO;

import com.jsve.restfullapi.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT s FROM Store s WHERE upper(s.name) LIKE upper(concat('%', :name, '%'))")
    List<Store> filterByName(@Param("name") String name);
    Optional<Store> findStoreById(Long id);
    Optional<Store> findByNameIgnoreCase(String name);
}
