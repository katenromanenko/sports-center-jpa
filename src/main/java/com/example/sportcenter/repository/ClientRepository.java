package com.example.sportcenter.repository;

import com.example.sportcenter.entity.Client;
import com.example.sportcenter.entity.ClientStatus;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client save(Client c);
    List<Client> findAll();
    void deleteById(Long id);
    void updateStatus(Long id, ClientStatus newStatus);

    //1) Выполнить поиск клиента по имени
    List<Client> findByName(String name);
    Optional<Client> findByExactName(String name);


}

