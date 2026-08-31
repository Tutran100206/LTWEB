package vn.iotstar.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaConfig {
    private static final String PERSISTENCE_UNIT = "jpa-hibernate-mysql";
    private static volatile EntityManagerFactory emf;

    private JpaConfig() {
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    private static EntityManagerFactory getEntityManagerFactory() {
        EntityManagerFactory local = emf;
        if (local != null) {
            return local;
        }
        synchronized (JpaConfig.class) {
            local = emf;
            if (local == null) {
                try {
                    local = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
                    emf = local;
                } catch (RuntimeException e) {
                    throw new IllegalStateException("Khong the khoi tao ket noi CSDL qua JPA.", e);
                }
            }
            return local;
        }
    }
}
