package code.infrastructure.database.repository;

import code.business.dao.CreatureDAO;
import code.business.domain.Creature;
import code.infrastructure.configuration.HibernateUtil;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.mapper.CreatureEntityMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;


@Component
@AllArgsConstructor
public class CreatureRepository implements CreatureDAO {

   public static final int OFFSPRING_FOOD_THRESHOLD = 30;
   public static final int OFFSPRING_FOOD_TAKEN = 5;

   private final CreatureEntityMapper creatureEntityMapper;
   private final HibernateUtil hibernateUtil;

   @Override
   public Integer getOffspringNumber() {
      try (EntityManager entityManager = hibernateUtil.getEntityManager()) {
         entityManager.getTransaction().begin();
         String hql = "SELECT cr FROM CreatureEntity cr WHERE cr.saturation >= :threshold"; // somehow not
         String sql = "SELECT * From entity_cycle.creature as cr WHERE cr.saturation >= 1"; // works
         Query query = entityManager.createQuery(hql, CreatureEntity.class);
         query.setParameter("threshold", OFFSPRING_FOOD_THRESHOLD);
         List<CreatureEntity> resultList = query.getResultList();
         resultList.forEach(c -> c.setSaturation(c.getSaturation() - OFFSPRING_FOOD_TAKEN));
         resultList.forEach(entityManager::merge);
         resultList.forEach(System.out::println);
         entityManager.getTransaction().commit();
         return resultList.size();
      }
   }

   @Override
   public void addAll(List<Creature> creatures) {
      try (EntityManager entityManager = hibernateUtil.getEntityManager()) {
         entityManager.getTransaction().begin();
         creatures.stream().map(creatureEntityMapper::mapToEntity).forEach(entityManager::persist);
         entityManager.getTransaction().commit();
      }
   }

   @Override
   public List<Creature> getPrioritized(int limit) {
      List<CreatureEntity> result;
      try (EntityManager entityManager = hibernateUtil.getEntityManager()) {
         entityManager.getTransaction().begin();
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
         Query query = entityManager.createNativeQuery(sql, CreatureEntity.class);
         query.setParameter("limit", limit);
         result = query.getResultList();
         entityManager.getTransaction().commit();
      }
      return result.stream().map(creatureEntityMapper::mapFromEntity).toList();
   }

   @Override
   public void updateFood(List<Creature> prioritized) {
      List<CreatureEntity> list = prioritized.stream().map(creatureEntityMapper::mapToEntity).toList();
      try (EntityManager entityManager = hibernateUtil.getEntityManager()) {
         entityManager.getTransaction().begin();
         list.forEach(entityManager::merge);
         entityManager.getTransaction().commit();
      }
   }

   public List<Creature> getAll() {
      List<CreatureEntity> result;
      try (EntityManager entityManager = hibernateUtil.getEntityManager()) {
         entityManager.getTransaction().begin();
          result = entityManager.createQuery("FROM CreatureEntity ", CreatureEntity.class).getResultList();
         entityManager.getTransaction().commit();
      }
      return result.stream().map(creatureEntityMapper::mapFromEntity).toList();
   }
}