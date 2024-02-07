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
         String hql = "SELECT cr FROM CreatureEntity cr WHERE cr.saturation >= :threshold"; // somehow not
         String sql = "SELECT * From entity_cycle.creature as cr WHERE cr.saturation >= 1"; // works
         Query<CreatureEntity> query = session.createQuery(hql, CreatureEntity.class);
         query.setParameter("threshold", OFFSPRING_FOOD_THRESHOLD);
         List<CreatureEntity> resultList = query.getResultList();
         resultList.forEach(c -> c.setSaturation(c.getSaturation() - OFFSPRING_FOOD_TAKEN));
         resultList.forEach(session::update);
         resultList.forEach(System.out::println);
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
      List<CreatureEntity> result;
      try (Session session = HibernateUtil.getSession()) {
         session.beginTransaction();
         String sql = """
                  SELECT cr.creature_id, cr.age,  cr.name, cr.saturation, cr.birth_cycle, cr.address_id, (-(age) + saturation - (
                 SELECT COALESCE(SUM(de.saturation_drain),0)
                    FROM entity_cycle.injury AS i
                    INNER JOIN entity_cycle.debuff AS de ON i.debuff_id = de.debuff_id
                    WHERE i.creature_id = cr.creature_id
                  )) AS priority
                  FROM entity_cycle.creature AS cr
                  ORDER BY priority DESC
                  LIMIT :limit
                  """;
         Query<CreatureEntity> query = session.createNativeQuery(sql, CreatureEntity.class);
         query.setParameter("limit", limit);
         result = query.getResultList();
         session.getTransaction().commit();
      }
      return result.stream().map(creatureEntityMapper::mapFromEntity).toList();
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

   public List<Creature> getAll() {
      List<CreatureEntity> result;
      try (Session session = HibernateUtil.getSession()) {
         session.beginTransaction();
          result = session.createQuery("FROM CreatureEntity ", CreatureEntity.class).getResultList();
         session.getTransaction().commit();
      }
      return result.stream().map(creatureEntityMapper::mapFromEntity).toList();
   }
}