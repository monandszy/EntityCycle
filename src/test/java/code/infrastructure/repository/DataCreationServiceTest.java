package code.infrastructure.repository;

import code.business.domain.Creature;
import code.business.service.DataCreationService;
import code.infrastructure.configuration.ApplicationConfiguration;
import code.infrastructure.configuration.HibernateUtil;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.mapper.CreatureEntityMapper;
import code.infrastructure.database.repository.CreatureRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
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
   private final HibernateUtil hibernateUtil;

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
      try (EntityManager entityManger = hibernateUtil.getEntityManager()) {
         //given
         entityManger.getTransaction().begin();
         Creature randomPrioritizedCreature = dataCreationService.getRandomCreature();
         CreatureEntity entity = creatureEntityMapper.mapToEntity(randomPrioritizedCreature);
         entityManger.persist(entity);
         entityManger.flush();
         Creature creature = creatureEntityMapper.mapFromEntity(entity);

         //when
         dataCreationService.addFood(List.of(creature));
         creatureRepository.updateFood(List.of(creature));
         entityManger.flush();

         // then
         Assertions.assertFalse(creature.getFoods().isEmpty());
         Assertions.assertFalse(entity.getFoods().isEmpty());
         entityManger.getTransaction().commit();
      }
   }

   @Test
   void testCreateCreatures() {
      try (EntityManager entityManger = hibernateUtil.getEntityManager()) {
         entityManger.getTransaction().begin();
         //given
         int CREATURE_NUMBER = 3;
         List<Creature> creatures = dataCreationService.getRandomCreatureList(CREATURE_NUMBER);

         //when
         creatureRepository.addAll(creatures);
         entityManger.flush();

         //then
         TypedQuery<CreatureEntity> query = entityManger.createQuery("FROM CreatureEntity cr", CreatureEntity.class);
         List<CreatureEntity> resultList = query.getResultList();
         Assertions.assertEquals(CREATURE_NUMBER, resultList.size());
         entityManger.getTransaction().commit();
      }
   }
}