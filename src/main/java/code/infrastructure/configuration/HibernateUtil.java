package code.infrastructure.configuration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class HibernateUtil {

   @PersistenceUnit()
   private EntityManagerFactory entityManagerFactory;

   public void closeSessionFactory() {
         entityManagerFactory.close();
   }

   public EntityManager getEntityManager() {
      Objects.requireNonNull(entityManagerFactory);
      EntityManager session = entityManagerFactory.createEntityManager();
      Objects.requireNonNull(session);
      return session;
   }
}