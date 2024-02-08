package code.infrastructure.database.repository;

import code.business.dao.DebuffDAO;
import code.business.domain.Debuff;
import code.business.domain.DebuffType;
import code.infrastructure.configuration.HibernateUtil;
import code.infrastructure.database.entity.DebuffEntity;
import code.infrastructure.database.mapper.DebuffEntityMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class DebuffRepository implements DebuffDAO {

   private final HibernateUtil hibernateUtil;
   private final DebuffEntityMapper debuffEntityMapper;

   @Override
   public Optional<Debuff> getRandomDebuff(DebuffType debuffType) {
      try (Session session = hibernateUtil.getSession()) {
         session.beginTransaction();

         CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
         CriteriaQuery<DebuffEntity> criteriaQuery = criteriaBuilder.createQuery(DebuffEntity.class);
         Root<DebuffEntity> root = criteriaQuery.from(DebuffEntity.class);
         ParameterExpression<DebuffType> typeParam = criteriaBuilder.parameter(DebuffType.class);
         criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("value"), typeParam));

         Query<DebuffEntity> query = session.createQuery(criteriaQuery);
         query.setParameter(typeParam, debuffType);
         query.setMaxResults(3);

         session.getTransaction().commit();
         return query.getResultStream().findAny().map(debuffEntityMapper::mapFromEntity);
      }
   }
}