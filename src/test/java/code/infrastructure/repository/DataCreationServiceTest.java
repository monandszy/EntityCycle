package code.infrastructure.repository;

import code.business.domain.Creature;
import code.business.service.DataCreationService;
import code.infrastructure.configuration.ApplicationConfiguration;
import code.infrastructure.configuration.HibernateUtil;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.mapper.CreatureEntityMapper;
import code.infrastructure.database.repository.CreatureRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringJUnitConfig(value = {ApplicationConfiguration.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Testcontainers
public class DataCreationServiceTest {

   private final DataCreationService dataCreationService;
   private final CreatureRepository creatureRepository;
   private final CreatureEntityMapper creatureEntityMapper;
   private final HibernateUtil hibernateUtil; // don't use
   @Container
   static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>("postgres:16.1");

   @DynamicPropertySource
   static void postgreSQLProperties(DynamicPropertyRegistry registry) {
      registry.add("jakarta.persistence.jdbc.url", postgreSQL::getJdbcUrl);
      registry.add("jakarta.persistence.jdbc.user", postgreSQL::getUsername);
      registry.add("jakarta.persistence.jdbc.password", postgreSQL::getPassword);
   }

   @Test
   void testFoodAssigment() {
      try (Session session = HibernateUtil.getSession()) {
         //given
         session.beginTransaction();
         Creature randomPrioritizedCreature = dataCreationService.getRandomCreature();
         CreatureEntity entity = creatureEntityMapper.mapToEntity(randomPrioritizedCreature);
         session.persist(entity);
         session.flush();
         Creature creature = creatureEntityMapper.mapFromEntity(entity);

         //when
         dataCreationService.addFood(List.of(creature));
         creatureRepository.updateFood(List.of(creature));
         session.flush();

         // then
         Assertions.assertFalse(creature.getFoods().isEmpty());
         Assertions.assertFalse(entity.getFoods().isEmpty());
         session.getTransaction().commit();
      }
   }

   @Test
   void testCreateCreatures() {
      try (Session session = HibernateUtil.getSession()) {
         session.beginTransaction();
         //given
         int CREATURE_NUMBER = 3;
         List<Creature> creatures = dataCreationService.getRandomCreatureList(CREATURE_NUMBER);

         //when
         creatureRepository.addAll(creatures);
         session.flush();

         //then
         Query<CreatureEntity> query = session.createQuery("FROM CreatureEntity cr", CreatureEntity.class);
         List<CreatureEntity> resultList = query.getResultList();
         Assertions.assertEquals(CREATURE_NUMBER, resultList.size());
         session.getTransaction().commit();
      }
   }
}