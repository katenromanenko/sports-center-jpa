package com.example.sportcenter.service;

import com.example.sportcenter.entity.Client;
import com.example.sportcenter.entity.ClientStatus;
import com.example.sportcenter.repository.ClientRepository;
import com.example.sportcenter.repository.ClientRepositoryImpl;

import java.util.List;

public class ClientService {
    private final ClientRepository repo = new ClientRepositoryImpl();

    public Client add(Client c) { return repo.save(c); }
    public List<Client> getAll() { return repo.findAll(); }
    public void delete(Long id) { repo.deleteById(id); }
    public void changeStatus(Long id, ClientStatus newStatus) { repo.updateStatus(id, newStatus); }
}
