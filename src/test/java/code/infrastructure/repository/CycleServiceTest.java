package code.infrastructure.repository;

import code.business.domain.Creature;
import code.business.service.DataCreationService;
import code.infrastructure.configuration.ApplicationConfiguration;
import code.infrastructure.configuration.HibernateUtil;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.mapper.CreatureEntityMapper;
import code.infrastructure.database.repository.AgeRepository;
import code.infrastructure.database.repository.CreatureRepository;
import code.infrastructure.database.repository.SaturationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static code.infrastructure.database.repository.CreatureRepository.OFFSPRING_FOOD_TAKEN;
import static code.infrastructure.database.repository.CreatureRepository.OFFSPRING_FOOD_THRESHOLD;

@Testcontainers
@SpringJUnitConfig(value = {ApplicationConfiguration.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
class CycleServiceTest {

   @Container
   static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>("postgres:16.1");

   @DynamicPropertySource
   static void postgreSQLProperties(DynamicPropertyRegistry registry) {
      registry.add("jakarta.persistence.jdbc.url", postgreSQL::getJdbcUrl);
      registry.add("jakarta.persistence.jdbc.user", postgreSQL::getUsername);
      registry.add("jakarta.persistence.jdbc.password", postgreSQL::getPassword);
   }

   private final CreatureRepository creatureRepository;
   private final SaturationRepository saturationRepository;
   private final AgeRepository ageRepository;
   private final DataCreationService dataCreationService;
   private final CreatureEntityMapper creatureEntityMapper;

   private final HibernateUtil hibernateUtil;

   @Test
   void test() {

   }

   @BeforeEach
   void deleteAll() {
      try (EntityManager session = hibernateUtil.getEntityManager()) {
         session.getTransaction().begin();
         Query nativeQuery = session.createNativeQuery("""
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

   @Test
   void testGetOffspringNumber() {
      int validId;
      try (EntityManager session = hibernateUtil.getEntityManager()) {
         session.getTransaction().begin();
         //given
         Creature meetingCriteriaCreature = dataCreationService.getRandomCreature().withSaturation(OFFSPRING_FOOD_THRESHOLD);
         CreatureEntity meetingEntity = creatureEntityMapper.mapToEntity(meetingCriteriaCreature);
         Creature notMeetingCriteriaCreature = dataCreationService.getRandomCreature().withSaturation(0);
         CreatureEntity notMeetingEntity = creatureEntityMapper.mapToEntity(notMeetingCriteriaCreature);
         session.persist(meetingEntity);
         session.persist(notMeetingEntity);
         session.getTransaction().commit();
         validId = meetingEntity.getId();
      }
      // separate because of some cache issue - didn't update
      try (EntityManager session = hibernateUtil.getEntityManager()) {
         //when
         session.getTransaction().begin();
         Integer toBeCreatedCounter = creatureRepository.getOffspringNumber();
         session.flush();
         Integer saturation = session.find(CreatureEntity.class, validId).getSaturation();
         session.getTransaction().commit();
         //then
         Assertions.assertEquals(1, toBeCreatedCounter);
         Assertions.assertEquals(OFFSPRING_FOOD_THRESHOLD - OFFSPRING_FOOD_TAKEN, saturation);
      }
   }

   @Test
   void testPrioritizationCalculation() {
      try (EntityManager session = hibernateUtil.getEntityManager()) {
         //given
         session.getTransaction().begin();
         Creature lessPriorityCreature = dataCreationService.getRandomCreature().withAge(100);
         CreatureEntity lessEntity = creatureEntityMapper.mapToEntity(lessPriorityCreature);
         Creature priorityCreature = dataCreationService.getRandomCreature().withAge(1);
         CreatureEntity entity = creatureEntityMapper.mapToEntity(priorityCreature);
         session.persist(lessEntity);
         session.persist(entity);
         session.getTransaction().commit();
         //when
         session.getTransaction().begin();
         List<Creature> prioritized = creatureRepository.getPrioritized(1);
         session.getTransaction().commit();

         //then
         Assertions.assertEquals(prioritized.size(), 1);
         Assertions.assertEquals(creatureEntityMapper.mapFromEntity(entity), prioritized.getFirst());
      }
   }


   @Test
   void testHungryEating() {
      saturationRepository.eatIfHungry(); // remove food, recalculate saturation
      saturationRepository.addFoodPoisoningDebuff(); // random
   }

   @Test
   void testStarvation() {
      saturationRepository.killStarving(); // if saturation <= 0 and starving -> kill
      saturationRepository.addStarvationDebuff(); // one chance to survive starving kill
   }

   @Test
   void testAdvanceAge() {
      ageRepository.advanceSaturation();
      ageRepository.advanceAge(); // move age by one
      ageRepository.assignAgeDebuff(); // random
   }


}