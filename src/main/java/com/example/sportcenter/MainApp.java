package com.example.sportcenter;

import com.example.sportcenter.entity.Client;
import com.example.sportcenter.entity.ClientStatus;
import com.example.sportcenter.service.ClientService;
import com.example.sportcenter.util.JPAUtil;

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
                System.out.println("Saved id=" + c.getId() + " (" + c.getFirstName() + " " + c.getLastName() + ")");
            } catch (Exception e) {
                System.out.println("Skip (maybe duplicate phone): " + c.getPhone());
            }
        }

        System.out.println("=== ALL CLIENTS ===");
        service.getAll().forEach(c -> System.out.printf(
                "%d | %s %s | %s | age=%d | phone=%s | lastVisit=%s | total=%.2f%n",
                c.getId(), c.getFirstName(), c.getLastName(), c.getStatus(),
                c.getAge(), c.getPhone(), c.getLastVisit(), c.getTotalSpent()
        ));

        JPAUtil.shutdown();
    }
}

