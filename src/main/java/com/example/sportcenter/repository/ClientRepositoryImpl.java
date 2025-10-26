package com.example.sportcenter.repository;

import com.example.sportcenter.config.JpaConfig;
import com.example.sportcenter.entity.Client;
import com.example.sportcenter.entity.ClientStatus;
import com.example.sportcenter.entity.Visitor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

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
                if (c instanceof Visitor v) {
                    v.setStatus(newStatus);
                }

                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<Client> findByName(String namePart) {
        try (EntityManager em = JpaConfig.getEmf().createEntityManager()) {
            return em.createQuery(
                            "select c from Client c " +
                                    "where lower(c.firstName) like lower(concat('%', :q, '%')) " +
                                    "   or lower(c.lastName)  like lower(concat('%', :q, '%')) " +
                                    "order by c.lastName, c.firstName",
                            Client.class
                    )
                    .setParameter("q", namePart == null ? "" : namePart.trim())
                    .getResultList();
        }
    }

    @Override
    public Optional<Client> findByExactName(String name) {
        try (EntityManager em = JpaConfig.getEmf().createEntityManager()) {
            String q = (name == null ? "" : name.trim().replaceAll("\\s+", " "));
            var list = em.createQuery(
                            "select c from Client c " +
                                    "where lower( concat( concat(c.firstName, ' '), c.lastName) ) = lower(:q)",
                            Client.class
                    )
                    .setParameter("q", q)
                    .setMaxResults(1)
                    .getResultList();
            return list.stream().findFirst();
        }
    }



}


