package com.example.sportcenter.repository.criteria;

import com.example.sportcenter.config.JpaConfig;
import com.example.sportcenter.entity.Employee;
import com.example.sportcenter.entity.Facility;
import com.example.sportcenter.entity.ServiceOffer;
import com.example.sportcenter.entity.Visit;
import com.example.sportcenter.entity.Visitor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CriteriaHomeworkRepository {

    /* 1. Получите всех сотрудников из базы данных с использованием критерии */
    public List<Employee> findAllEmployees() {
        try (EntityManager em = JpaConfig.getEmf().createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.select(root).orderBy(cb.asc(root.get("id")));
            return em.createQuery(cq).getResultList();
        }
    }

    /* 2. Найдите активность (спорт)  у которого самая маленькая стоимость  */
    public Optional<ServiceOffer> findCheapestActivity() {
        try (EntityManager em = JpaConfig.getEmf().createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ServiceOffer> cq = cb.createQuery(ServiceOffer.class);
            Root<ServiceOffer> root = cq.from(ServiceOffer.class);

            Subquery<BigDecimal> minPriceSub = cq.subquery(BigDecimal.class);
            Root<ServiceOffer> s2 = minPriceSub.from(ServiceOffer.class);
            minPriceSub.select(cb.min(s2.get("price")));

            cq.select(root).where(cb.equal(root.get("price"), minPriceSub));

            TypedQuery<ServiceOffer> q = em.createQuery(cq).setMaxResults(1);
            List<ServiceOffer> list = q.getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        }
    }

    /* 3. Посчитать суммарную вместимость помещений комплекса (sum по полю capacity) */
    public long totalFacilityCapacity() {
        try (EntityManager em = JpaConfig.getEmf().createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Facility> root = cq.from(Facility.class);

            cq.select(cb.sumAsLong(root.get("maxCapacity")));

            Long result = em.createQuery(cq).getSingleResult();
            return result == null ? 0L : result;
        }
    }

    /* Посчитайте общее количество человек в помещениях, которые могут находиться одновременно в комплексе (sum) */
    public List<Visitor> findVisitorsByAgeBetween_birthYear(int minAge, int maxAge) {
        try (var em = JpaConfig.getEmf().createEntityManager()) {
            var cb = em.getCriteriaBuilder();
            var cq = cb.createQuery(Visitor.class);
            var root = cq.from(Visitor.class);

            int thisYear = java.time.LocalDate.now().getYear();
            int minBirthYear = thisYear - maxAge;
            int maxBirthYear = thisYear - minAge;

            cq.select(root).where(cb.between(root.get("birthYear"), minBirthYear, maxBirthYear));
            return em.createQuery(cq).getResultList();
        }
    }

}

