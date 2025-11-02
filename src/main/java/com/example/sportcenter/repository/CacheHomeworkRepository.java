package com.example.sportcenter.repository;

import com.example.sportcenter.config.JpaConfig;
import com.example.sportcenter.entity.Employee;
import com.example.sportcenter.entity.ServiceOffer;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CacheHomeworkRepository {

    public ServiceOffer runThreeQueries(boolean clearBeforeFind) {
        try (EntityManager em = JpaConfig.getEmf().createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();

                System.out.println("\n[КЭШ] Запрос 1: берём все активности для панели");
                List<ServiceOffer> activities = em.createQuery(
                                "select s from ServiceOffer s order by s.id", ServiceOffer.class)
                        .getResultList();
                System.out.println("Нашли активностей: " + activities.size());

                System.out.println("[КЭШ] Запрос 2: берём всех работников для отчёта");
                List<Employee> employees = em.createQuery(
                                "select e from Employee e order by e.id", Employee.class)
                        .getResultList();
                System.out.println("Нашли сотрудников: " + employees.size());

                if (clearBeforeFind) {
                    System.out.println("Очищаем контекст — представим, что вкладку закрыли и открыли заново.");
                    em.clear();
                } else {
                    System.out.println("Контекст оставляем как есть — Hibernate хранит активность в памяти.");
                }

                ServiceOffer found = null;
                if (!activities.isEmpty()) {
                    ServiceOffer firstActivity = activities.get(0);
                    System.out.println("[КЭШ] Запрос 3: смотрим карточку активности id=" + firstActivity.getId());
                    found = em.find(ServiceOffer.class, firstActivity.getId());
                    if (found != null) {
                        System.out.println("Карточка открылась: " + found.getName());
                    }
                } else {
                    System.out.println("Активностей нет — третий запрос не выполняется.");
                }

                tx.commit();
                return found;
            } catch (RuntimeException ex) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw ex;
            }
        }
    }

    public void checkSecondLevelCache(long activityId) {
        try (EntityManager firstManager = JpaConfig.getEmf().createEntityManager()) {
            System.out.println("\n[КЭШ-2] Первый менеджер прогружает услугу в кэш");
            firstManager.find(ServiceOffer.class, activityId);
        }

        try (EntityManager secondManager = JpaConfig.getEmf().createEntityManager()) {
            System.out.println("[КЭШ-2] Второй менеджер открывает ту же услугу — ждём обращение к кэшу");
            secondManager.find(ServiceOffer.class, activityId);
        }
    }
}