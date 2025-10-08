package com.example.sportcenter.repository.session;

import com.example.sportcenter.config.HibernateSessionConfig;
import com.example.sportcenter.entity.Facility;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

public class FacilitySessionRepository {

    public Facility add(Facility f) {
        try (Session session = HibernateSessionConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(f);
            tx.commit();
            return f;
        }
    }

    public Optional<Facility> findById(Long id) {
        try (Session session = HibernateSessionConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Facility.class, id));
        }
    }

    public Facility addCopyWithNewIdentityNumber(Long sourceId, String newIdentityNumber) {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var tx = session.beginTransaction();

            try {
                var src = session.get(Facility.class, sourceId); // managed
                if (src == null) {
                    throw new IllegalArgumentException("Не найдено помещение: id=" + sourceId);
                }

            session.detach(src);

            src.setId(null);
            src.setIdentityNumber(newIdentityNumber);

            session.persist(src);
            tx.commit();

            return src;
            } catch (RuntimeException e) {
                tx.rollback();
                throw e;
            }
        }
    }

    public Optional<Facility> findByIdentityNumber(String identityNumber) {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var q = session.createQuery(
                    "from Facility f where f.identityNumber = :num", Facility.class);
            q.setParameter("num", identityNumber);
            return q.uniqueResultOptional(); // Optional.empty(), если не нашли
        }
    }

    public void updateHourRate(Long id, java.math.BigDecimal newRate) {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var tx = session.beginTransaction();
            var f = session.get(Facility.class, id); // managed
            if (f != null) {
                f.setHourRate(newRate);              // UPDATE произойдёт на commit
            }
            tx.commit();
        }
    }

}
