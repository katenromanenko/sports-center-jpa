// repository/view/PremiumClientRepository.java
package com.example.sportcenter.repository.view;

import com.example.sportcenter.config.JpaConfig;
import com.example.sportcenter.entity.view.PremiumClient;
import java.util.List;

public class PremiumClientRepository {
    public List<PremiumClient> findAll() {
        var em = JpaConfig.getEmf().createEntityManager();
        try {
            return em.createQuery("select p from PremiumClient p order by p.id", PremiumClient.class)
                    .getResultList();
        } finally { em.close(); }
    }
}

