package com.example.sportcenter;

import com.example.sportcenter.entity.Client;
import com.example.sportcenter.config.HibernateSessionConfig;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SessionSmoke {
    public static void main(String[] args) {
        try (Session session = HibernateSessionConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Client c = session.get(Client.class, 4);
            System.out.println("Session OK, client with id=4: " + (c == null ? "not found" : c.getFirstName()+" "+c.getLastName()));

            tx.commit();
        } finally {
            HibernateSessionConfig.shutdown();
        }

    }
}

