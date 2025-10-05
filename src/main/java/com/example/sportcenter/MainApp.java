package com.example.sportcenter;

import com.example.sportcenter.entity.*;
import com.example.sportcenter.repository.session.ClientSessionRepository;
import com.example.sportcenter.repository.session.FacilitySessionRepository;
import com.example.sportcenter.repository.session.ServiceOfferSessionRepository;
import com.example.sportcenter.service.ClientService;
import com.example.sportcenter.config.JpaConfig;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        ClientService service = new ClientService();

        List<Client> seeds = List.of(
                Client.builder().firstName("Виктор").lastName("Николаюк").age(33)
                        .phone("+375259760761").lastVisit(LocalDate.now().minusDays(2))
                        .status(ClientStatus.ACTIVE).totalSpent(new BigDecimal("1200.00")).build(),
                Client.builder().firstName("Инна").lastName("Мелешко").age(31)
                        .phone("+375259760762").lastVisit(LocalDate.now().minusDays(8))
                        .status(ClientStatus.PREMIUM).totalSpent(new BigDecimal("9500.50")).build(),
                Client.builder().firstName("Сергей").lastName("Коноплев").age(22)
                        .phone("+375259760763").lastVisit(LocalDate.now().minusDays(1))
                        .status(ClientStatus.ACTIVE).totalSpent(new BigDecimal("300.00")).build(),
                Client.builder().firstName("Игорь").lastName("Бородачев").age(26)
                        .phone("+375259760764").lastVisit(LocalDate.now().minusDays(30))
                        .status(ClientStatus.BLOCKED).totalSpent(new BigDecimal("50.00")).build(),
                Client.builder().firstName("Дмитрий").lastName("Барковский").age(35)
                        .phone("+375259760765").lastVisit(LocalDate.now().minusDays(5))
                        .status(ClientStatus.PREMIUM).totalSpent(new BigDecimal("4200.00")).build()
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
        service.getAll().forEach(c -> System.out.printf(
                "%d | %s %s | статус=%s | возраст=%d | телефон=%s | последний визит=%s | потрачено=%.2f%n",
                c.getId(), c.getFirstName(), c.getLastName(), c.getStatus(),
                c.getAge(), c.getPhone(), c.getLastVisit(), c.getTotalSpent()
        ));

        JpaConfig.shutdown();

        var csr = new ClientSessionRepository();
        System.out.println("Клиент #4 через Session: " + csr.findById(4L).map(Client::getLastName).orElse("не найден"));

        var srepo = new ServiceOfferSessionRepository();
        srepo.add(ServiceOffer.builder().name("Теннис").price(new BigDecimal("25.00")).build());
        srepo.add(ServiceOffer.builder().name("Плавание").price(new BigDecimal("18.50")).build());
        srepo.add(ServiceOffer.builder().name("Футбол").price(new BigDecimal("30.00")).build());
        System.out.println("Услуги добавлены через Session");

        var fRepo = new FacilitySessionRepository();

        var hall1 = fRepo.findByIdentityNumber("GZ-003").orElseGet(() ->
                fRepo.add(Facility.builder()
                        .name("Тренажёрный зал")
                        .identityNumber("GZ-003")
                        .maxCapacity(15)
                        .status(FacilityStatus.ACTIVE)
                        .hourRate(new BigDecimal("12.00"))
                        .build())
        );
        System.out.println("Помещение id=" + hall1.getId() + ", инв. номер=" + hall1.getIdentityNumber());

        var copy = fRepo.addCopyWithNewIdentityNumber(hall1.getId(), "GZ-004");
        System.out.println("Скопированное помещение id=" + copy.getId() + ", инв. номер=" + copy.getIdentityNumber());

        fRepo.updateHourRate(hall1.getId(), new BigDecimal("15.00"));
        System.out.println("Стоимость аренды обновлена для id=" + hall1.getId());
    }
}

