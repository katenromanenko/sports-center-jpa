package com.example.sportcenter.repository;

import com.example.sportcenter.entity.Client;
import com.example.sportcenter.entity.ClientStatus;
import com.example.sportcenter.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

public class ClientRepositoryImpl implements ClientRepository {

    @Override
    public Client save(Client c) {
        EntityManager em = JPAUtil.getEmf().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(c);
            tx.commit();
            return c;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Client> findAll() {
        var em = JPAUtil.getEmf().createEntityManager();
        try {
            return em.createQuery("select c from Client c order by c.id", Client.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override public Optional<Client> findById(Long id) { throw new UnsupportedOperationException(); }
    @Override
    public void deleteById(Long id) {
        var em = JPAUtil.getEmf().createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var found = em.find(Client.class, id);
            if (found != null) {
                em.remove(found);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void updateStatus(Long id, ClientStatus newStatus) {
        var em = JPAUtil.getEmf().createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var c = em.find(Client.class, id);
            if (c != null) {
                c.setStatus(newStatus);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

