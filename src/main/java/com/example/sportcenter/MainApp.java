package com.example.sportcenter;

import com.example.sportcenter.config.JpaConfig;
import com.example.sportcenter.config.HibernateSessionConfig;
import com.example.sportcenter.entity.*;
import com.example.sportcenter.repository.criteria.CriteriaHomeworkRepository;
import com.example.sportcenter.repository.session.EmployeeSessionRepository;
import com.example.sportcenter.repository.session.FacilitySessionRepository;
import com.example.sportcenter.service.ClientService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MainApp {

    private static int ageFromBirthYear(Integer birthYear) {
        return (birthYear == null) ? 0 : LocalDate.now().getYear() - birthYear;
    }

    public static void main(String[] args) {
        ClientService service = new ClientService();

        // ---------------------------------------------
        // ДАННЫЕ ДЛЯ НАПОЛНЕНИЯ
        // ---------------------------------------------
        List<Visitor> visitors = List.of(
                Visitor.builder()
                        .firstName("Виктор").lastName("Николаюк").birthYear(1992)
                        .phone("+3752597605669").firstVisit(LocalDate.now().minusMonths(6)).lastVisit(LocalDate.now().minusDays(2))
                        .status(ClientStatus.ACTIVE).totalSpent(new BigDecimal("1200.00"))
                        .address(Address.builder().city("Минск").street("Победителей").houseNumber("10Б").postalCode("220030").build())
                        .build(),
                Visitor.builder()
                        .firstName("Инна").lastName("Мелешко").birthYear(1994)
                        .phone("+334569760766").firstVisit(LocalDate.now().minusMonths(12)).lastVisit(LocalDate.now().minusDays(8))
                        .status(ClientStatus.PREMIUM).totalSpent(new BigDecimal("9500.50"))
                        .address(Address.builder().city("Минск").street("Независимости").houseNumber("15").postalCode("220005").build())
                        .build(),
                Visitor.builder()
                        .firstName("Сергей").lastName("Коноплёв").birthYear(2003)
                        .phone("+375569780764").firstVisit(LocalDate.now().minusMonths(2)).lastVisit(LocalDate.now().minusDays(1))
                        .status(ClientStatus.ACTIVE).totalSpent(new BigDecimal("300.00"))
                        .address(Address.builder().city("Минск").street("Кальварийская").houseNumber("3").postalCode("220004").build())
                        .build(),
                Visitor.builder()
                        .firstName("Игорь").lastName("Бородачев").birthYear(1999)
                        .phone("+37525667768").firstVisit(LocalDate.now().minusYears(1)).lastVisit(LocalDate.now().minusDays(30))
                        .status(ClientStatus.BLOCKED).totalSpent(new BigDecimal("50.00"))
                        .address(Address.builder().city("Минск").street("Сурганова").houseNumber("24").postalCode("220072").build())
                        .build()
        );

        List<Employee> employees = List.of(
                Employee.builder()
                        .firstName("Анна").lastName("Иванова").birthYear(1990)
                        .position("Тренер по плаванию").salary(new BigDecimal("2500.00"))
                        .hiredDate(LocalDate.now().minusYears(2))
                        .address(Address.builder().city("Минск").street("Мележа").houseNumber("7").postalCode("220113").build())
                        .build(),
                Employee.builder()
                        .firstName("Павел").lastName("Сидоров").birthYear(1988)
                        .position("Инструктор тренажёрного зала").salary(new BigDecimal("2300.00"))
                        .hiredDate(LocalDate.now().minusYears(1))
                        .build(),
                Employee.builder()
                        .firstName("Елена").lastName("Орлова").birthYear(1993)
                        .position("Администратор").salary(new BigDecimal("1800.00"))
                        .hiredDate(LocalDate.now().minusMonths(10))
                        .build(),
                Employee.builder()
                        .firstName("Денис").lastName("Кузьмин").birthYear(1985)
                        .position("Тренер по теннису").salary(new BigDecimal("2700.00"))
                        .hiredDate(LocalDate.now().minusYears(3))
                        .build()
        );

        // ---------------------------------------------
        // СОХРАНЯЕМ КЛИЕНТОВ/СОТРУДНИКОВ
        // ---------------------------------------------
        visitors.forEach(v -> {
            try {
                service.add(v); // persist Visitor как Client — ок
                System.out.println("Сохранён VISITOR id=" + v.getId() + " (" + v.getFirstName() + " " + v.getLastName() + ")");
            } catch (Exception e) {
                System.out.println("Ошибка при сохранении VISITOR (возможно, дубликат телефона): " + v.getPhone());
            }
        });

        employees.forEach(e -> {
            try {
                service.add(e); // persist Employee как Client — ок
                System.out.println("Сохранён EMPLOYEE id=" + e.getId() + " (" + e.getFirstName() + " " + e.getLastName() + ")");
            } catch (Exception ex) {
                System.out.println("Ошибка при сохранении EMPLOYEE: " + e.getFirstName() + " " + e.getLastName());
            }
        });

        // ---------------------------------------------
        // ПРОСМОТР ВСЕХ КЛИЕНТОВ
        // ---------------------------------------------
        System.out.println("\n=== ВСЕ КЛИЕНТЫ (полиморфно) ===");
        service.getAll().forEach(c -> {
            int age = ageFromBirthYear(c.getBirthYear());
            String base = String.format("%d | %s %s | возраст=%d",
                    c.getId(), c.getFirstName(), c.getLastName(), age);

            if (c instanceof Visitor v) {
                System.out.printf("%s | [VISITOR] статус=%s | телефон=%s | последний визит=%s | потрачено=%s%n",
                        base, v.getStatus(), v.getPhone(), v.getLastVisit(), v.getTotalSpent());
            } else if (c instanceof Employee e) {
                System.out.printf("%s | [EMPLOYEE] должность=%s | зарплата=%s | принят=%s | уволен=%s%n",
                        base, e.getPosition(), e.getSalary(), e.getHiredDate(), e.getFiredDate());
            } else {
                System.out.printf("%s | [CLIENT-BASE]%n", base);
            }
        });

        // ---------------------------------------------
        // ПОДГОТОВИМ ТРЕНАЖЁРНЫЙ ЗАЛ ДЛЯ П.5
        // ---------------------------------------------
        var fRepo = new FacilitySessionRepository();
        var hall1 = fRepo.findByIdentityNumber("GZ-456").orElseGet(() ->
                fRepo.add(Facility.builder()
                        .name("Тренажёрный зал")
                        .identityNumber("GZ-456")
                        .maxCapacity(15)            // capacity
                        .status(FacilityStatus.ACTIVE)
                        .hourRate(new BigDecimal("15.00")) // цена за час (hourRate)
                        .build())
        );
        System.out.println("Помещение id=" + hall1.getId() + ", инв. номер=" + hall1.getIdentityNumber());

        // ---------------------------------------------
        // №10 ДЗ (1–5)
        // ---------------------------------------------

        // 1)Выполнить поиск клиента по имени
        System.out.println("\n=== [ДЗ-1] Поиск клиента по имени (LIKE) ===");
        try (var session = HibernateSessionConfig.getSessionFactory().openSession()) {
            var namePart = "op";
            var found = session.createQuery(
                            "from Client c " +
                                    "where lower(c.firstName) like :q or lower(c.lastName) like :q " +
                                    "order by c.lastName, c.firstName", Client.class)
                    .setParameter("q", "%" + namePart.toLowerCase() + "%")
                    .list();
            found.forEach(c ->
                    System.out.printf("Найден: %d | %s %s%n", c.getId(), c.getFirstName(), c.getLastName()));
            if (found.isEmpty()) System.out.println("Ничего не найдено.");
        }

        // 2) Найти самого высокооплачиваемого сотрудника
        // 3) Найти сотрудника у которого самая низкая зарплата
        var empRepo = new EmployeeSessionRepository();
        System.out.println("\n=== [ДЗ-2] Самый высокооплачиваемый сотрудник ===");
        empRepo.findMaxSalaryEmployee().ifPresentOrElse(
                e -> System.out.printf("MAX: %s %s | %s%n", e.getFirstName(), e.getLastName(), e.getSalary()),
                () -> System.out.println("Сотрудники не найдены")
        );

        System.out.println("\n=== [ДЗ-3] Самый низкооплачиваемый сотрудник ===");
        empRepo.findMinSalaryEmployee().ifPresentOrElse(
                e -> System.out.printf("MIN: %s %s | %s%n", e.getFirstName(), e.getLastName(), e.getSalary()),
                () -> System.out.println("Сотрудники не найдены")
        );

        // 4) Реализуйте метод, который будет подсчитывать расходы за период на персонал. (по зарплате в месяц у сотрудников)
        System.out.println("\n=== [ДЗ-4] Расходы на персонал за период ===");
        var from = LocalDate.of(LocalDate.now().getYear(), 10, 1); // c 1 октября
        var to   = LocalDate.of(LocalDate.now().getYear(), 12, 1); // по 1 декабря (полуоткрытый период [from, to) -> 2 месяца)
        var total = empRepo.payrollForPeriod(from, to);
        System.out.printf("Период: %s .. %s | Итого: %s%n", from, to, total);

        // 5) Реализовать метод для тренажёрных залов, который будет выводить стоимость за один час на 1 человека.
        System.out.println("\n=== [ДЗ-5] Цена за 1 час на 1 человека (GYM) ===");
        fRepo.gymPricePerHourPerPerson(hall1.getId()).ifPresentOrElse(
                p -> System.out.printf("Facility #%d: %s BYN/чел·час%n", hall1.getId(), p),
                () -> System.out.println("Не удалось рассчитать (нет capacity или цены)")
        );

        // =====================================================================
        // === №11 ДЗ (Criteria API)                                         ===
        // =====================================================================
                var criteriaRepo = new CriteriaHomeworkRepository();

        // [CR-1] Все сотрудники (Criteria)
                System.out.println("\n=== [CR-1] Все сотрудники (Criteria) ===");
                criteriaRepo.findAllEmployees()
                        .forEach(e -> System.out.printf("%d | %s %s | должность=%s | зарплата=%s%n",
                                e.getId(), e.getFirstName(), e.getLastName(), e.getPosition(), e.getSalary()));

        // [CR-2] Самая дешёвая активность (Criteria)
                System.out.println("\n=== [CR-2] Самая дешёвая активность (Criteria) ===");
                criteriaRepo.findCheapestActivity().ifPresentOrElse(
                        a -> System.out.printf("Минимальная цена: %s — %s%n", a.getName(), a.getPrice()),
                        () -> System.out.println("Активности не найдены")
                );

        // [CR-3] Суммарная вместимость помещений (Criteria)
                System.out.println("\n=== [CR-3] Суммарная вместимость помещений (Criteria) ===");
                long totalCap = criteriaRepo.totalFacilityCapacity();
                System.out.println("Итого мест одновременно в комплексе: " + totalCap);

        // [CR-4] Пользователи по диапазону возраста (Criteria)
                System.out.println("\n=== [CR-4] Посетители по возрасту (Criteria) ===");
                int minAge = 25;
                int maxAge = 35;

        var visitorsByAge = criteriaRepo.findVisitorsByAgeBetween_birthYear(minAge, maxAge);

        if (visitorsByAge.isEmpty()) {
            System.out.println("Никого в заданном диапазоне.");
        } else {
            visitorsByAge.forEach(v ->
                    System.out.printf("%d | %s %s | возраст=%d%n",
                            v.getId(), v.getFirstName(), v.getLastName(),
                            (v.getBirthYear() == null ? 0 : LocalDate.now().getYear() - v.getBirthYear()))
            );
        }

        // =====================================================================

        JpaConfig.shutdown();
    }
}
