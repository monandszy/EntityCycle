package code.infrastructure.repository;

import code.business.domain.Creature;
import code.infrastructure.configuration.TestApplicationConfiguration;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@SpringJUnitConfig(value = {TestApplicationConfiguration.class})
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

   @Test
   void testGetOffspringNumber() {
      Integer toBeCreatedCounter = creatureDAO.getOffspringNumber(); // remove 3 food if above threshold
   }

   @Test
   void testCreateCreatures() {
      List<Creature> creatures = dataCreationService.getRandomCreatureList(toBeCreatedCounter);
      creatureDAO.addAll(creatures);
   }

   @Test
   void testPrioritizationCalculation() {
      List<Creature> prioritized = creatureDAO.getPrioritized();
   }

   @Test
   void testFoodAssigment() {
      creatureDAO.createFood(prioritized); // add piece of food to prioritized
   }

   @Test
   void testHungryEating() {
      creatureDAO.eatIfHungry(); // remove food, recalculate saturation
      creatureDAO.addFoodPoisoningDebuff(); // random
   }

   @Test
   void testStarvation() {
      creatureDAO.killStarving(); // if saturation <= 0 and starving -> kill
      creatureDAO.addStarvationDebuff(); // one chance to survive starving kill
   }

   @Test
   void testAdvanceAge() {
      creatureDAO.advanceSaturation();
      creatureDAO.advanceAge(); // move age by one
      creatureDAO.assignAgeDebuff(); // random
   }



}