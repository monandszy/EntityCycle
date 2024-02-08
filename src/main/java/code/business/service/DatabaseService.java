package code.business.service;

import code.infrastructure.configuration.HibernateUtil;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DatabaseService {

   private final HibernateUtil hibernateUtil;

   public void deleteAll() {
      try (Session session = hibernateUtil.getSession()) {
         session.beginTransaction();
         NativeQuery nativeQuery = session.createNativeQuery("""
                 DELETE FROM  entity_cycle.food WHERE 1=1;
                 DELETE FROM  entity_cycle.injury WHERE 1=1;
                 DELETE FROM  entity_cycle.creature WHERE 1=1;
                 DELETE FROM  entity_cycle.debuff WHERE 1=1;
                 DELETE FROM  entity_cycle.dead_creature WHERE 1=1;
                 DELETE FROM  entity_cycle.address WHERE 1=1;
                 """);
         nativeQuery.executeUpdate();
         session.getTransaction().commit();
      }
   }
}