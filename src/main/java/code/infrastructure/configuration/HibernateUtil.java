package code.infrastructure.configuration;

import jakarta.persistence.PersistenceUnit;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class HibernateUtil {

   @PersistenceUnit()
   private SessionFactory sessionFactory;

   public Session getSession() {
      Objects.requireNonNull(sessionFactory);
      Session session = sessionFactory.openSession();
      Objects.requireNonNull(session);
      return session;
   }
}