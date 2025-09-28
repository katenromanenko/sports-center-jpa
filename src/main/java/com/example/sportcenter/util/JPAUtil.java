// src/main/java/com/example/sportcenter/util/JPAUtil.java
package com.example.sportcenter.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("sportsPU");
    public static EntityManagerFactory getEmf() { return EMF; }
    public static void shutdown() { if (EMF.isOpen()) EMF.close(); }
}
