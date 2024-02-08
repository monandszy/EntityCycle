package code.infrastructure.database.repository;

import code.business.dao.SaturationDAO;
import code.business.domain.Creature;
import code.business.domain.Debuff;
import code.business.domain.DebuffType;
import code.infrastructure.configuration.HibernateUtil;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.entity.DebuffEntity;
import code.infrastructure.database.entity.FoodEntity;
import code.infrastructure.database.mapper.CreatureEntityMapper;
import code.infrastructure.database.mapper.DebuffEntityMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static code.business.management.InputData.OFFSPRING_FOOD_THRESHOLD;


@Component
@AllArgsConstructor
public class SaturationRepository implements SaturationDAO {

   private final HibernateUtil hibernateUtil;
   private final CreatureEntityMapper creatureEntityMapper;
   private final DebuffEntityMapper debuffEntityMapper;

   @Override
   public List<Creature> eatIfHungry() {
      try (Session session = hibernateUtil.getSession()) {
         session.beginTransaction();
         String hql = "SELECT cr FROM CreatureEntity cr INNER JOIN FETCH cr.foods " +
                 "WHERE cr.saturation < :threshold AND (size(cr.foods) != 0)";
         Query<CreatureEntity> query = session.createQuery(hql, CreatureEntity.class);
         query.setParameter("threshold", OFFSPRING_FOOD_THRESHOLD);
         List<CreatureEntity> resultList = query.getResultList();
         resultList.forEach(e -> {
            FoodEntity foodEntity = e.getFoods().stream().findAny().orElseThrow();
            Integer nutritionalValue = foodEntity.getNutritionalValue();
            e.setSaturation(e.getSaturation() + nutritionalValue);
            e.getFoods().remove(foodEntity);
            session.remove(foodEntity);
            session.merge(e);
         });
         session.getTransaction().commit();
         return resultList.stream().map(debuffEntityMapper::mapFromEntityWithDebuff).toList();
      }
   }

   @Override
   public void updateDebuffs(List<Creature> creatures) {
      try (Session session = hibernateUtil.getSession()) {
         session.beginTransaction();
//         CriteriaBuilder builder = session.getCriteriaBuilder();
//         CriteriaUpdate<CreatureEntity> criteria = builder.createCriteriaUpdate(CreatureEntity.class);
//         creatures.stream().map(debuffEntityMapper::mapToEntityWithDebuff).forEach(e -> {
//            Root<CreatureEntity> root = criteria.from(CreatureEntity.class);
//            criteria.set(root.get("debuffs"), e.getDebuffs());
//            criteria.where(builder.equal(root.get("id"), e.getId()));
//            session.createQuery(criteria).executeUpdate();
//         }); // address and foods are null, how do i update it while creating a injury

//         String hql = "UPDATE CreatureEntity cr SET cr.debuffs = :debuffs WHERE cr.id = :id";
//         Query query = session.createQuery(hql);

//         creatures.stream().map(debuffEntityMapper::mapToEntityWithDebuff).forEach(e -> {
//            query.setParameter("debuffs", e.getDebuffs());
//            query.setParameter("id", e.getId());
//            query.executeUpdate();
//         });
//         creatures.stream().map(debuffEntityMapper::mapToEntityWithDebuff).forEach(e -> {
//            session.merge(e);
//         });
         creatures.forEach(c -> {
            Objects.requireNonNull(c.getId());
            CreatureEntity entity = session.find(CreatureEntity.class, c.getId());
            Set<DebuffEntity> collect = c.getDebuffs().stream()
                    .map(debuffEntityMapper::mapToEntity).collect(Collectors.toSet());
            entity.setDebuffs(collect);
            session.merge(entity);
         });
         session.getTransaction().commit();
      }
   }

   @Override
   public void killStarving() {
      try (Session session = hibernateUtil.getSession()) {
         session.beginTransaction();
         CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
         CriteriaQuery<CreatureEntity> criteriaQuery = criteriaBuilder.createQuery(CreatureEntity.class);
         Root<CreatureEntity> root = criteriaQuery.from(CreatureEntity.class);
         Join<Object, Object> joinTable = root.join("debuffs");
         ParameterExpression<DebuffType> typeParam = criteriaBuilder.parameter(DebuffType.class);
         criteriaQuery.select(root).where(
                 criteriaBuilder.equal(joinTable.get("value"), typeParam),
                 criteriaBuilder.lt(root.get("saturation"), 1)
         );

         Query<CreatureEntity> query = session.createQuery(criteriaQuery);
         query.setParameter(typeParam, DebuffType.starvation);

//         String hql = "SELECT cr FROM CreatureEntity cr Join FETCH cr.debuffs de " +
//                 "WHERE cr.saturation <= 0 AND de.value = :debuffType";
//         Query<CreatureEntity> query = session.createQuery(hql, CreatureEntity.class);
//         query.setParameter("debuffType", DebuffType.starvation.name());
         query.getResultStream().forEach(session::remove);
         session.getTransaction().commit();
      }
   }

   @Override
   public void addStarvationDebuff(Debuff starvationDebuff) {
      try (Session session = hibernateUtil.getSession()) {
         session.beginTransaction();
         String hql = "SELECT cr FROM CreatureEntity cr LEFT JOIN FETCH cr.debuffs de WHERE cr.saturation <= 0";
         Query<CreatureEntity> query = session.createQuery(hql, CreatureEntity.class);
         List<CreatureEntity> resultList = query.getResultList();
         resultList.forEach(e -> {
            e.getDebuffs().add(debuffEntityMapper.mapToEntity(starvationDebuff));
            session.merge(e);
         });
         session.getTransaction().commit();
      }
   }
}