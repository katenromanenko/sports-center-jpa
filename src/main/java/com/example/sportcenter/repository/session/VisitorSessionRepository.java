package com.example.sportcenter.repository.session;

import com.example.sportcenter.config.HibernateSessionConfig;
import com.example.sportcenter.entity.ClientStatus;
import com.example.sportcenter.entity.Visitor;
import java.util.List;
import java.util.Optional;

public class VisitorSessionRepository {

    public Visitor add(Visitor v) {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var tx = session.beginTransaction();
            session.persist(v);
            tx.commit();
            return v;
        }
    }

    public Optional<Visitor> findById(Long id) {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Visitor.class, id));
        }
    }

    public Optional<Visitor> findByPhone(String phone) {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var q = session.createQuery(
                    "from Visitor v where v.phone = :phone", Visitor.class);
            q.setParameter("phone", phone);
            return q.uniqueResultOptional();
        }
    }

    public List<Visitor> findByStatus(ClientStatus status) {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var q = session.createQuery(
                    "from Visitor v where v.status = :status", Visitor.class);
            q.setParameter("status", status);
            return q.getResultList();
        }
    }
}

