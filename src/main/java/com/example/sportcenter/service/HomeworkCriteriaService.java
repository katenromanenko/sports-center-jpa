package com.example.sportcenter.service;

import com.example.sportcenter.entity.Employee;
import com.example.sportcenter.entity.Facility;
import com.example.sportcenter.entity.ServiceOffer;
import com.example.sportcenter.entity.Visitor;
import com.example.sportcenter.repository.criteria.CriteriaHomeworkRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HomeworkCriteriaService {

    private final CriteriaHomeworkRepository repo = new CriteriaHomeworkRepository();

    public List<Employee> employees() {
        return repo.findAllEmployees();
    }

    public Optional<ServiceOffer> cheapestActivity() {
        return repo.findCheapestActivity();
    }

    public long totalCapacity() {
        return repo.totalFacilityCapacity();
    }

    public List<Visitor> visitorsByAge(int min, int max) {
        return repo.findVisitorsByAgeBetween_birthYear(min, max);
    }
}
