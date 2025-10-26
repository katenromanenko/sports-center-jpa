package com.example.sportcenter.repository.session;

import com.example.sportcenter.config.HibernateSessionConfig;
import com.example.sportcenter.entity.Employee;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    // 2) Найти самого высокооплачиваемого сотрудника
    public Optional<Employee> findMaxSalaryEmployee() {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var list = session.createQuery(
                            "from Employee e " +
                                    "where e.firedDate is null " +            // считаем только активных
                                    "order by e.salary desc", Employee.class)
                    .setMaxResults(1)
                    .getResultList();
            return list.stream().findFirst();
        }
    }

    // 3) Найти сотрудника у которого самая низкая зарплата
    public Optional<Employee> findMinSalaryEmployee() {
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var list = session.createQuery(
                            "from Employee e " +
                                    "where e.firedDate is null " +
                                    "order by e.salary asc", Employee.class)
                    .setMaxResults(1)
                    .getResultList();
            return list.stream().findFirst();
        }
    }

    // 4) Реализуйте метод, который будет подсчитывать расходы за период на персонал. (по зарплате в месяц у сотрудников)
    public BigDecimal payrollForPeriod(LocalDate fromInclusive, LocalDate toExclusive) {
        if (fromInclusive == null || toExclusive == null || !toExclusive.isAfter(fromInclusive)) {
            return BigDecimal.ZERO;
        }
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            BigDecimal monthlyTotal = session.createQuery(
                            "select coalesce(sum(e.salary), 0) " +
                                    "from Employee e " +
                                    "where e.firedDate is null", BigDecimal.class)
                    .uniqueResult();

            LocalDate f = fromInclusive.withDayOfMonth(1);
            LocalDate t = toExclusive.withDayOfMonth(1);
            int months = (t.getYear() - f.getYear()) * 12 + (t.getMonthValue() - f.getMonthValue());
            if (months <= 0) months = 1;

            return monthlyTotal.multiply(BigDecimal.valueOf(months));
        }
    }


}

