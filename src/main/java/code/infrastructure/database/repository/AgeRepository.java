package code.infrastructure.database.repository;

import code.business.dao.AgeDAO;
import code.business.domain.Creature;
import code.business.domain.DebuffType;
import code.infrastructure.configuration.HibernateUtil;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.mapper.DebuffEntityMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class AgeRepository implements AgeDAO {

   private final HibernateUtil hibernateUtil;
   private final DebuffEntityMapper debuffEntityMapper;

   @Override
   public void advanceSaturation() {
      try (Session session = hibernateUtil.getSession()) {
         session.beginTransaction();
         String hql = "Update CreatureEntity cr SET cr.saturation = cr.saturation - 2";
         Query query = session.createQuery(hql);
         query.executeUpdate();
         session.getTransaction().commit();
      }
   }

   @Override
   public void advanceAge() {
      try (Session session = hibernateUtil.getSession()) {
         session.beginTransaction();
         String hql = "Update CreatureEntity cr SET cr.age = cr.age + 1";
         Query query = session.createQuery(hql);
         query.executeUpdate();
         session.getTransaction().commit();
      }
   }

   @Override
   public List<Creature> getAged() {
      try (Session session = hibernateUtil.getSession()) {
         session.beginTransaction();
         String hql = "SELECT cr From CreatureEntity cr ORDER BY age Limit 50";
         Query<CreatureEntity> query = session.createQuery(hql, CreatureEntity.class);
         List<Creature> list = query.getResultStream().map(debuffEntityMapper::mapFromEntityWithDebuff).toList();
         session.getTransaction().commit();
         return list;
      }
   }

}