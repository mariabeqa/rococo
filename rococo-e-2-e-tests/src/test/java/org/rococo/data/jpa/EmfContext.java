package org.rococo.data.jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.rococo.data.DataBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum EmfContext {
    INSTANCE;

    private final Map<DataBase, EntityManagerFactory> emfContext = new HashMap<>();

    public synchronized EntityManagerFactory getEmf(DataBase dataBase) {
        if (emfContext.get(dataBase) == null) {
            Map<String, String> settings = new HashMap<>();
            settings.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
            settings.put("hibernate.connection.driver_class", "com.p6spy.engine.spy.P6SpyDriver");
            settings.put("hibernate.connection.username", "root");
            settings.put("hibernate.connection.password", "secret");
            settings.put("hibernate.connection.url", dataBase.getUrlForP6Spy());

            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
                "persistence-unit-name", settings);
            this.emfContext.put(dataBase, new ThreadLocalEntityManagerFactory(entityManagerFactory));
        }
        return emfContext.get(dataBase);
    }

    public Collection<EntityManagerFactory> storedEmf() {
        return emfContext.values();
    }
}
