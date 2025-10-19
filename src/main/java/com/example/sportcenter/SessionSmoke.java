package com.example.sportcenter;

import com.example.sportcenter.config.HibernateSessionConfig;
import com.example.sportcenter.entity.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SessionSmoke {

    public static void main(String[] args) {
        try (Session session = HibernateSessionConfig.getSessionFactory().openSession()) {

            System.out.println("\n=== SMOKE: подготовка данных ===");
            Long visitorId;
            Long hallAId;
            Long hallBId;
            Long serviceId;

            // 1)
            Transaction tx = session.beginTransaction();
            try {
                // 1.1 Услуга
                ServiceOffer yoga = ServiceOffer.builder()
                        .name("Йога")
                        .price(new BigDecimal("20.00"))
                        .build();
                session.persist(yoga);

                // 1.2
                // Помещения под эту услугу
                Facility hallA = Facility.builder()
                        .name("Зал A")
                        .identityNumber("H-A")
                        .maxCapacity(12)
                        .status(FacilityStatus.ACTIVE)
                        .hourRate(new BigDecimal("14.00"))
                        .serviceOffer(yoga)
                        .build();
                session.persist(hallA);

                Facility hallB = Facility.builder()
                        .name("Зал B")
                        .identityNumber("H-B")
                        .maxCapacity(10)
                        .status(FacilityStatus.ACTIVE)
                        .hourRate(new BigDecimal("12.00"))
                        .serviceOffer(yoga)
                        .build();
                session.persist(hallB);

                // 1.3 Посетитель
                Visitor v = Visitor.builder()
                        .firstName("Максим")
                        .lastName("Ярушкин")
                        .birthYear(2001)
                        .phone("100-200")
                        .status(ClientStatus.PREMIUM)
                        .totalSpent(new BigDecimal("0.00"))
                        .build();
                session.persist(v);

                // 1.4 Посещение
                Visit visit1 = Visit.builder()
                        .visitor(v)
                        .visitDate(LocalDate.now().minusDays(1))
                        .amountSpent(new BigDecimal("15.50"))
                        .build();
                session.persist(visit1);

                // 1.5 Две записи
                Reservation r1 = Reservation.builder()
                        .client(v)
                        .facility(hallA)
                        .bookingDate(LocalDate.now())
                        .bookingTime(LocalTime.of(10, 0))
                        .build();
                session.persist(r1);

                Reservation r2 = Reservation.builder()
                        .client(v)
                        .facility(hallB)
                        .bookingDate(LocalDate.now())
                        .bookingTime(LocalTime.of(12, 0))
                        .build();
                session.persist(r2);

                tx.commit();

                visitorId = v.getId();
                hallAId   = hallA.getId();
                hallBId   = hallB.getId();
                serviceId = yoga.getId();

                System.out.printf("Создан Visitor id=%d; hallA id=%d; hallB id=%d; ServiceOffer id=%d%n",
                        visitorId, hallAId, hallBId, serviceId);
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }

            // 2) Загрузка Client
            tx = session.beginTransaction();
            try {
                Client c = session.get(Client.class, visitorId);
                System.out.println("\n=== Загрузка Client ===");
                if (c == null) {
                    System.out.println("Client not found");
                } else {
                    String who = (c instanceof Visitor) ? "[Visitor]" :
                            (c instanceof Employee ? "[Employee]" : "[Client]");
                    System.out.printf("id=%d %s %s %s%n", c.getId(), who, c.getFirstName(), c.getLastName());
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }

            // 3) 1 ServiceOffer -> N Facility
            tx = session.beginTransaction();
            try {
                System.out.println("\n=== ServiceOffer -> Facilities (1->N) ===");
                List<Facility> ofs = session.createQuery(
                                "from Facility f where f.serviceOffer.id = :sid order by f.id", Facility.class)
                        .setParameter("sid", serviceId)
                        .list();
                System.out.printf("Услуга id=%d имеет помещений: %d%n", serviceId, ofs.size());
                ofs.forEach(f -> System.out.printf("  #%d %s (inv=%s)%n",
                        f.getId(), f.getName(), f.getIdentityNumber()));
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }

            // 4) 1 Visitor -> N Visit
            tx = session.beginTransaction();
            try {
                System.out.println("\n=== Visitor -> Visits (1->N) ===");
                List<Visit> visits = session.createQuery(
                                "from Visit v where v.visitor.id = :vid order by v.visitDate desc", Visit.class)
                        .setParameter("vid", visitorId)
                        .list();
                System.out.printf("У visitor id=%d посещений: %d%n", visitorId, visits.size());
                visits.forEach(vv -> System.out.printf("  %s — потрачено %s%n",
                        vv.getVisitDate(), vv.getAmountSpent()));
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }

            // 5) Кто куда записан
            tx = session.beginTransaction();
            try {
                System.out.println("\n=== Reservations (Client <-> Facility) ===");
                List<Reservation> rs = session.createQuery(
                                "select r from Reservation r " +
                                        "join fetch r.client c " +
                                        "join fetch r.facility f " +
                                        "where c.id = :vid order by r.bookingDate, r.bookingTime", Reservation.class)
                        .setParameter("vid", visitorId)
                        .list();
                rs.forEach(r -> System.out.printf("Бронь #%d: %s %s -> %s %s%n",
                        r.getId(), r.getBookingDate(), r.getBookingTime(),
                        r.getFacility().getName(), r.getFacility().getIdentityNumber()));
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }

            // 6) Каскадное удаление
            tx = session.beginTransaction();
            try {
                System.out.println("\n=== Каскадное удаление: удаляем hallA ===");
                Facility hallA = session.get(Facility.class, hallAId);
                if (hallA != null) {
                    session.remove(hallA);
                }
                tx.commit();
                System.out.println("Удалили hallA");
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }

            // 6.1 Резервации на hallA исчезли, а на hallB остались
            tx = session.beginTransaction();
            try {
                Long cntHallA = session.createQuery(
                                "select count(r) from Reservation r where r.facility.id = :fid", Long.class)
                        .setParameter("fid", hallAId)
                        .uniqueResult();
                Long cntHallB = session.createQuery(
                                "select count(r) from Reservation r where r.facility.id = :fid", Long.class)
                        .setParameter("fid", hallBId)
                        .uniqueResult();

                System.out.printf("Бронирований на hallA(id=%d): %d (должно быть 0)%n", hallAId, cntHallA);
                System.out.printf("Бронирований на hallB(id=%d): %d (должно быть >0)%n", hallBId, cntHallB);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }

            // 7) Общий список клиентов
            tx = session.beginTransaction();
            try {
                System.out.println("\n=== Все клиенты ===");
                List<Client> all = session.createQuery(
                                "select c from Client c order by c.id", Client.class)
                        .list();
                all.forEach(c -> {
                    String who = (c instanceof Visitor) ? "[Visitor]" :
                            (c instanceof Employee ? "[Employee]" : "[Client]");
                    System.out.printf("%d | %s %s %s%n", c.getId(), who, c.getFirstName(), c.getLastName());
                });
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        } finally {
            HibernateSessionConfig.shutdown();
        }
    }
}
