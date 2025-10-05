// src/main/java/com/example/sportcenter/util/JPAUtil.java
package com.example.sportcenter.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaConfig {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("sportsPU");
    public static EntityManagerFactory getEmf() { return EMF; }
    public static void shutdown() { if (EMF.isOpen()) EMF.close(); }
}
