package com.example.sportcenter.repository.session;

import com.example.sportcenter.entity.ServiceOffer;
import com.example.sportcenter.config.HibernateSessionConfig;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ServiceOfferSessionRepository {
    public ServiceOffer add(ServiceOffer s) {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var tx = session.beginTransaction();
            session.persist(s);
            tx.commit();
            return s;
        }
    }
}
