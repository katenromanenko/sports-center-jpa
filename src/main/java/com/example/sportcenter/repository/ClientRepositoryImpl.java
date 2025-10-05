package com.example.sportcenter.repository;

import com.example.sportcenter.config.JpaConfig;
import com.example.sportcenter.entity.Client;
import com.example.sportcenter.entity.ClientStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class ClientRepositoryImpl implements ClientRepository {

    @Override
    public Client save(Client c) {
        try (EntityManager em = JpaConfig.getEmf().createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(c);
                tx.commit();
                return c;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<Client> findAll() {
        try (EntityManager em = JpaConfig.getEmf().createEntityManager()) {
            return em.createQuery(
                    "select c from Client c order by c.id", Client.class
            ).getResultList();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (EntityManager em = JpaConfig.getEmf().createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Client found = em.find(Client.class, id);
                if (found != null) {
                    em.remove(found);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public void updateStatus(Long id, ClientStatus newStatus) {
        try (EntityManager em = JpaConfig.getEmf().createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Client c = em.find(Client.class, id);
                if (c != null) {
                    c.setStatus(newStatus);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}


