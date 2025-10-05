package com.example.sportcenter;

import com.example.sportcenter.config.JpaConfig;
import com.example.sportcenter.entity.Address;
import com.example.sportcenter.entity.Client;
import com.example.sportcenter.entity.ClientStatus;
import com.example.sportcenter.entity.Facility;
import com.example.sportcenter.entity.FacilityStatus;
import com.example.sportcenter.entity.ServiceOffer;
import com.example.sportcenter.repository.session.ClientSessionRepository;
import com.example.sportcenter.repository.session.FacilitySessionRepository;
import com.example.sportcenter.repository.session.ServiceOfferSessionRepository;
import com.example.sportcenter.service.ClientService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        ClientService service = new ClientService();

        List<Client> seeds = List.of(
                Client.builder().firstName("Виктор").lastName("Николаюк").age(33)
                        .phone("+3752597605669").lastVisit(LocalDate.now().minusDays(2))
                        .status(ClientStatus.ACTIVE).totalSpent(new BigDecimal("1200.00"))
                        .address(Address.builder()
                                .city("Минск")
                                .street("Победителей")
                                .houseNumber("10Б")
                                .postalCode("220030")
                                .build())
                        .build(),

                Client.builder().firstName("Инна").lastName("Мелешко").age(31)
                        .phone("+334569760766").lastVisit(LocalDate.now().minusDays(8))
                        .status(ClientStatus.PREMIUM).totalSpent(new BigDecimal("9500.50"))
                        .address(Address.builder()
                                .city("Минск")
                                .street("Независимости")
                                .houseNumber("15")
                                .postalCode("220005")
                                .build())
                        .build(),

                Client.builder().firstName("Сергей").lastName("Коноплев").age(22)
                        .phone("+375569780764").lastVisit(LocalDate.now().minusDays(1))
                        .status(ClientStatus.ACTIVE).totalSpent(new BigDecimal("300.00"))
                        .address(Address.builder()
                                .city("Минск")
                                .street("Кальварийская")
                                .houseNumber("3")
                                .postalCode("220004")
                                .build())
                        .build(),

                Client.builder().firstName("Игорь").lastName("Бородачев").age(26)
                        .phone("+37525667768").lastVisit(LocalDate.now().minusDays(30))
                        .status(ClientStatus.BLOCKED).totalSpent(new BigDecimal("50.00"))
                        .address(Address.builder()
                                .city("Минск")
                                .street("Сурганова")
                                .houseNumber("24")
                                .postalCode("220072")
                                .build())
                        .build(),

                Client.builder().firstName("Дмитрий").lastName("Барковский").age(35)
                        .phone("+375679767866").lastVisit(LocalDate.now().minusDays(5))
                        .status(ClientStatus.PREMIUM).totalSpent(new BigDecimal("4200.00"))
                        .address(Address.builder()
                                .city("Минск")
                                .street("Притыцкого")
                                .houseNumber("89")
                                .postalCode("220140")
                                .build())
                        .build()
        );

        for (Client c : seeds) {
            try {
                service.add(c);
                System.out.println("Сохранён id=" + c.getId() + " (" + c.getFirstName() + " " + c.getLastName() + ")");
            } catch (Exception e) {
                System.out.println("Пропускаю (возможно, дубликат телефона): " + c.getPhone());
            }
        }

        System.out.println("=== ВСЕ КЛИЕНТЫ ===");
        service.getAll().forEach(c -> {
            Address a = c.getAddress();
            String addr = (a == null) ? "-"
                    : a.getCity() + ", " + a.getStreet() + ", " + a.getHouseNumber() + ", " + a.getPostalCode();
            System.out.printf(
                    "%d | %s %s | статус=%s | возраст=%d | телефон=%s | последний визит=%s | потрачено=%.2f | адрес=%s%n",
                    c.getId(), c.getFirstName(), c.getLastName(), c.getStatus(),
                    c.getAge(), c.getPhone(), c.getLastVisit(), c.getTotalSpent(), addr
            );
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

            var smallRooms = session.createQuery(
                    "from com.example.sportcenter.entity.view.FacilitiesUnder15",
                    com.example.sportcenter.entity.view.FacilitiesUnder15.class
            ).list();

            System.out.println("\n=== ПОМЕЩЕНИЯ НА <= 15 ЧЕЛОВЕК ===");
            smallRooms.forEach(r -> System.out.printf(
                    "%d | %s | инв=%s | вместимость=%d | аренда/час=%s%n",
                    r.getId(), r.getName(), r.getIdentityNumber(), r.getMaxCapacity(), r.getHourRate()
            ));
        }

        try (var session = com.example.sportcenter.config.HibernateSessionConfig
                .getSessionFactory().openSession()) {

            var premiums = session.createQuery(
                    "from com.example.sportcenter.entity.view.PremiumClient",
                    com.example.sportcenter.entity.view.PremiumClient.class
            ).list();

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
