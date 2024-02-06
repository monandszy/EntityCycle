package code.infrastructure.database.repository;

import code.business.dao.CreatureDAO;
import code.business.domain.Creature;
import code.infrastructure.configuration.HibernateUtil;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.mapper.CreatureEntityMapper;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class CreatureRepository implements CreatureDAO {

   public static final int OFFSPRING_FOOD_THRESHOLD = 30;
   public static final int OFFSPRING_FOOD_TAKEN = 5;

   private final CreatureEntityMapper creatureEntityMapper;

   @Override
   public Integer getOffspringNumber() {
      try (Session session = HibernateUtil.getSession()) {
         session.beginTransaction();
         String hql = "SELECT cr FROM CreatureEntity cr WHERE cr.saturation >= :threshold";
         Query<CreatureEntity> query = session.createQuery(hql, CreatureEntity.class);
         query.setParameter("threshold", OFFSPRING_FOOD_THRESHOLD);
         List<CreatureEntity> resultList = query.getResultList();
         resultList.stream()
                 .peek(c -> c.setSaturation(c.getSaturation() - OFFSPRING_FOOD_TAKEN))
                 .forEach(session::persist);
         session.getTransaction().commit();
         return resultList.size();
      }
   }

   @Override
   public void addAll(List<Creature> creatures) {
      try (Session session = HibernateUtil.getSession()) {
         session.beginTransaction();
         creatures.stream().map(creatureEntityMapper::mapToEntity).forEach(session::persist);
         session.getTransaction().commit();
      }
   }

   @Override
   public List<Creature> getPrioritized(int limit) {
      try (Session session = HibernateUtil.getSession()) {
         session.beginTransaction();
         String sql = "SELECT (cr.creature_id, cr.age,  cr.name, cr.saturation, cr.birth_cycle) " +
                 "FROM entity_cycle.creature as cr " +
                 "INNER JOIN entity_cycle.injury i ON cr.creature_id = i.creature_id " +
                 "INNER JOIN entity_cycle.debuff db ON db.debuff_id = i.debuff_id " +
                 "GROUP BY (cr.creature_id, cr.age,  cr.name, cr.saturation, cr.birth_cycle)" +
                 "ORDER BY -age + saturation - sum(db.saturation_drain) LIMIT :limit";
//         String sql = "SELECT (cr.creature_id, cr.age,  cr.name, cr.saturation, cr.birth_cycle) " +
//                 "FROM entity_cycle.injury AS i " +
//                 "INNER JOIN entity_cycle.creature cr ON i.creature_id = cr.creature_id " +
//                 "INNER JOIN entity_cycle.debuff db ON i.debuff_id = db.debuff_id " +
//                 "ORDER BY -cr.age + cr.saturation - SUM(db.saturation_drain) LIMIT :limit";
         Query<CreatureEntity> query = session.createNativeQuery(sql, CreatureEntity.class);
         query.setParameter("limit", limit);
         List<CreatureEntity> list = query.list();
         session.getTransaction().commit();
         return list.stream().map(creatureEntityMapper::mapFromEntity).toList();
      }
   }

   @Override
   public void updateFood(List<Creature> prioritized) {
      List<CreatureEntity> list = prioritized.stream().map(creatureEntityMapper::mapToEntity).toList();
      try (Session session = HibernateUtil.getSession()) {
         session.beginTransaction();
         list.forEach(session::merge);
         session.getTransaction().commit();
      }
   }
}