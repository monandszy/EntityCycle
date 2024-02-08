package code.infrastructure.repository;

import code.business.domain.Creature;
import code.business.service.DataCreationService;
import code.business.service.DatabaseService;
import code.infrastructure.configuration.ApplicationConfiguration;
import code.infrastructure.configuration.HibernateUtil;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.entity.FoodEntity;
import code.infrastructure.database.mapper.CreatureEntityMapper;
import code.infrastructure.database.mapper.FoodEntityMapper;
import code.infrastructure.database.repository.AgeRepository;
import code.infrastructure.database.repository.CreatureRepository;
import code.infrastructure.database.repository.SaturationRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
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
import java.util.Set;

import static code.business.management.InputData.OFFSPRING_FOOD_TAKEN;
import static code.business.management.InputData.OFFSPRING_FOOD_THRESHOLD;


@Testcontainers
@SpringJUnitConfig(value = {ApplicationConfiguration.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
class CycleServiceTest {

   @Container
   static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>("postgres:16.1");

   @DynamicPropertySource
   static void postgreSQLProperties(DynamicPropertyRegistry registry) {
      registry.add("jdbc.url", postgreSQL::getJdbcUrl);
      registry.add("jdbc.user", postgreSQL::getUsername);
      registry.add("jdbc.pass", postgreSQL::getPassword);
   }

   private final CreatureRepository creatureRepository;
   private final SaturationRepository saturationRepository;
   private final AgeRepository ageRepository;
   private final DataCreationService dataCreationService;
   private final CreatureEntityMapper creatureEntityMapper;
   private final FoodEntityMapper foodEntityMapper;
   private final HibernateUtil hibernateUtil;
   private final DatabaseService databaseService;

   @BeforeEach
   void setUp() {
      databaseService.deleteAll();
   }

   @Test
   void testGetOffspringNumber() {
      int validId;
      try (Session session = hibernateUtil.getSession()) {
         session.beginTransaction();
         //given
         Creature meetingCriteriaCreature = dataCreationService.getRandomCreature().withSaturation(OFFSPRING_FOOD_THRESHOLD);
         CreatureEntity meetingEntity = creatureEntityMapper.mapToEntityWithAddress(meetingCriteriaCreature);
         Creature notMeetingCriteriaCreature = dataCreationService.getRandomCreature().withSaturation(0);
         CreatureEntity notMeetingEntity = creatureEntityMapper.mapToEntityWithAddress(notMeetingCriteriaCreature);
         session.persist(meetingEntity);
         session.persist(notMeetingEntity);
         session.getTransaction().commit();
         validId = meetingEntity.getId();
      }
      // separate because of some cache issue - didn't update
      try (Session session = hibernateUtil.getSession()) {
         //when
         session.beginTransaction();
         Integer toBeCreatedCounter = creatureRepository.getOffspringNumber();
         session.flush();
         Integer saturation = session.get(CreatureEntity.class, validId).getSaturation();
         session.getTransaction().commit();
         //then
         Assertions.assertEquals(1, toBeCreatedCounter);
         Assertions.assertEquals(OFFSPRING_FOOD_THRESHOLD - OFFSPRING_FOOD_TAKEN, saturation);
      }
   }

   @Test
   void testPrioritizationCalculation() {
      try (Session session = hibernateUtil.getSession()) {
         //given
         session.beginTransaction();
         Creature lessPriorityCreature = dataCreationService.getRandomCreature().withAge(100);
         CreatureEntity lessEntity = creatureEntityMapper.mapToEntityWithAddress(lessPriorityCreature);
         Creature priorityCreature = dataCreationService.getRandomCreature().withAge(1);
         CreatureEntity entity = creatureEntityMapper.mapToEntityWithAddress(priorityCreature);
         session.persist(lessEntity);
         session.persist(entity);
         session.getTransaction().commit();
         //when
         session.beginTransaction();
         List<Creature> prioritized = creatureRepository.getPrioritized(1);
         session.getTransaction().commit();

         //then
         Assertions.assertEquals(prioritized.size(), 1);
         Assertions.assertEquals(creatureEntityMapper.mapFromEntity(entity), prioritized.getFirst());
      }
   }

   @Test
   void testHungryEating() {
      Integer id;
      try (Session session = hibernateUtil.getSession()) {
         //given
         session.beginTransaction();
         session.persist(creatureEntityMapper.mapToEntityWithAddress(dataCreationService.getRandomCreature()));
         Creature creature = dataCreationService.getRandomCreature().withSaturation(OFFSPRING_FOOD_THRESHOLD - 1);
         CreatureEntity entity = creatureEntityMapper.mapToEntityWithAddress(creature);
         session.persist(entity);
         FoodEntity foodEntity = foodEntityMapper.mapToEntity(dataCreationService.getRandomFood().withNutritionalValue(10));
         foodEntity.setCreature(entity);
         session.persist(foodEntity);
         session.getTransaction().commit();
         id = entity.getId();
      } // some update issues once again... flush doesn't work, clear doesn't work

      try (Session session = hibernateUtil.getSession()) {
         //when
         saturationRepository.eatIfHungry();
         //then
         session.beginTransaction();
         CreatureEntity entity = session.find(CreatureEntity.class, id);
         session.getTransaction().commit();
         Assertions.assertTrue(entity.getFoods().isEmpty());
         Assertions.assertEquals(OFFSPRING_FOOD_THRESHOLD - 1 + 10, entity.getSaturation());
      }

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