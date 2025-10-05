package com.example.sportcenter.repository.session;

import com.example.sportcenter.entity.Client;
import com.example.sportcenter.config.HibernateSessionConfig;
import org.hibernate.Session;

import java.util.Optional;

public class ClientSessionRepository {

    public Optional<Client> findById(Long id) {
        try (Session session = HibernateSessionConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Client.class, id));
        }
    }
}