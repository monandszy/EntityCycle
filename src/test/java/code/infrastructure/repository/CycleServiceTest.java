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
import lombok.AllArgsConstructor;
import org.hibernate.Session;
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
   private final HibernateUtil hibernateUtil; // don't use


   @Test
   void testGetOffspringNumber() {
      try (Session session = HibernateUtil.getSession()) {
         //given
         Creature meetingCriteriaCreature = dataCreationService.getRandomCreature().withSaturation(OFFSPRING_FOOD_THRESHOLD);
         CreatureEntity meetingEntity = creatureEntityMapper.mapToEntity(meetingCriteriaCreature);
         Creature notMeetingCriteriaCreature = dataCreationService.getRandomCreature().withSaturation(0);
         CreatureEntity notMeetingEntity = creatureEntityMapper.mapToEntity(notMeetingCriteriaCreature);
         session.persist(meetingEntity);
         session.persist(notMeetingEntity);
         session.flush();

         //when
         Integer toBeCreatedCounter = creatureRepository.getOffspringNumber(); // remove food if above threshold and counter++
         Integer saturation = session.get(CreatureEntity.class, meetingEntity.getId()).getSaturation();
         session.flush();

         //then
         Assertions.assertEquals(toBeCreatedCounter, 1);
         Assertions.assertEquals(saturation, OFFSPRING_FOOD_THRESHOLD - OFFSPRING_FOOD_TAKEN);
      }
   }

   @Test
   void testPrioritizationCalculation() {
      try (Session session = HibernateUtil.getSession()) {
         //given
         Creature lessPriorityCreature = dataCreationService.getRandomCreature().withAge(100);
         CreatureEntity lessEntity = creatureEntityMapper.mapToEntity(lessPriorityCreature);
         Creature priorityCreature = dataCreationService.getRandomCreature().withAge(1);
         CreatureEntity entity = creatureEntityMapper.mapToEntity(priorityCreature);
         session.persist(lessEntity);
         session.persist(entity);
         session.flush();
         //when
         List<Creature> prioritized = creatureRepository.getPrioritized(1);
         session.flush();

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