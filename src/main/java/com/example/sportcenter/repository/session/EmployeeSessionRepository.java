package com.example.sportcenter.repository.session;

import com.example.sportcenter.config.HibernateSessionConfig;
import com.example.sportcenter.entity.Employee;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class EmployeeSessionRepository {

    public Employee add(Employee e) {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var tx = session.beginTransaction();
            session.persist(e);
            tx.commit();
            return e;
        }
    }

    public Optional<Employee> findById(Long id) {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Employee.class, id));
        }
    }

    public List<Employee> findAll() {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            return session.createQuery("from Employee", Employee.class).getResultList();
        }
    }

    public List<Employee> findActive() {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var q = session.createQuery(
                    "from Employee e where e.firedDate is null", Employee.class);
            return q.getResultList();
        }
    }

    public void updateSalary(Long id, BigDecimal newSalary) {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var tx = session.beginTransaction();
            var e = session.get(Employee.class, id);
            if (e != null) {
                e.setSalary(newSalary);
            }
            tx.commit();
        }
    }
}

