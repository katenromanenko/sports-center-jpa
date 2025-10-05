package com.example.sportcenter.config;

import com.example.sportcenter.entity.Client;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateSessionConfig {
    private static final SessionFactory SESSION_FACTORY = build();

    private static SessionFactory build() {
        var registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        return new MetadataSources(registry)
                .addAnnotatedClass(Client.class)
                .buildMetadata()
                .buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() { return SESSION_FACTORY; }

    public static void shutdown() {
        if (!SESSION_FACTORY.isClosed()) SESSION_FACTORY.close();
    }
}

