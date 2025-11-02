package com.example.sportcenter.service;

import com.example.sportcenter.repository.CacheHomeworkRepository;

public class CacheHomeworkService {

    private final CacheHomeworkRepository repository = new CacheHomeworkRepository();

    public void demoFirstLevelCache() {
        System.out.println("\n=== [CACHE-1] Сценарий А: ожидаем 3 запроса ===");
        repository.runThreeQueries(true);

        System.out.println("\n=== [CACHE-1] Сценарий B: хотим 2 запроса (3-й достанется из кэша 1 уровня) ===");
        repository.runThreeQueries(false);
        System.out.println("Напоминание: второй запуск видит третью активность из контекста EntityManager.");
    }

    public void demoSecondLevelCache(long activityId) {
        System.out.println("\n=== [CACHE-2] Проверяем второй уровень кэша для активности id=" + activityId + " ===");
        repository.checkSecondLevelCache(activityId);
    }

}
