package com.example.sportcenter;

import com.example.sportcenter.config.JpaConfig;
import com.example.sportcenter.entity.*;
import com.example.sportcenter.repository.session.ClientSessionRepository;
import com.example.sportcenter.repository.session.FacilitySessionRepository;
import com.example.sportcenter.repository.session.ServiceOfferSessionRepository;
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

        JpaConfig.shutdown();

        var csr = new ClientSessionRepository();
        System.out.println("Клиент #4 через Session: " + csr.findById(4L).map(Client::getLastName).orElse("не найден"));

        var srepo = new ServiceOfferSessionRepository();
        srepo.add(ServiceOffer.builder().name("Теннис").price(new BigDecimal("25.00")).build());
        srepo.add(ServiceOffer.builder().name("Плавание").price(new BigDecimal("18.50")).build());
        srepo.add(ServiceOffer.builder().name("Футбол").price(new BigDecimal("30.00")).build());
        System.out.println("Услуги добавлены через Session");

        var fRepo = new FacilitySessionRepository();
        var hall1 = fRepo.findByIdentityNumber("GZ-949").orElseGet(() ->
                fRepo.add(Facility.builder()
                        .name("Тренажёрный зал")
                        .identityNumber("GZ-098")
                        .maxCapacity(15)
                        .status(FacilityStatus.ACTIVE)
                        .hourRate(new BigDecimal("12.00"))
                        .build())
        );
        System.out.println("Помещение id=" + hall1.getId() + ", инв. номер=" + hall1.getIdentityNumber());

        var copy = fRepo.addCopyWithNewIdentityNumber(hall1.getId(), "GZ-956");
        System.out.println("Скопированное помещение id=" + copy.getId() + ", инв. номер=" + copy.getIdentityNumber());

        fRepo.updateHourRate(hall1.getId(), new BigDecimal("15.00"));
        System.out.println("Стоимость аренды обновлена для id=" + hall1.getId());

        try (var session = com.example.sportcenter.config.HibernateSessionConfig
                .getSessionFactory().openSession()) {

            var premiums = session.createQuery(
                            "from Visitor v where v.status = :st", Visitor.class)
                    .setParameter("st", ClientStatus.PREMIUM)
                    .list();

            System.out.println("\n=== ПРЕМИУМ-КЛИЕНТЫ ===");
            premiums.forEach(p -> {
                var a = p.getAddress();
                var addr = (a == null) ? "-" :
                        String.format("%s, %s, %s, %s",
                                a.getCity(), a.getStreet(), a.getHouseNumber(), a.getPostalCode());

                System.out.printf("%d | %s %s | телефон=%s | потрачено=%s | адрес=%s%n",
                        p.getId(), p.getFirstName(), p.getLastName(), p.getPhone(), p.getTotalSpent(), addr);
            });
        }
    }
}
