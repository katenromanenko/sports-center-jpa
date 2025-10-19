package com.example.sportcenter.repository.view;

import com.example.sportcenter.config.JpaConfig;
import com.example.sportcenter.entity.view.FacilitiesUnder15;

import java.util.List;

public class FacilitiesUnder15Repository {
    public List<FacilitiesUnder15> findAll() {
        var em = JpaConfig.getEmf().createEntityManager();
        try {
            return em.createQuery("select v from FacilitiesUnder15 v order by v.id", FacilitiesUnder15.class)
                    .getResultList();
        } finally { em.close(); }
    }
}

