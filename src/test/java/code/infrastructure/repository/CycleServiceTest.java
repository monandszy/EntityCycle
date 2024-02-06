package code.infrastructure.repository;

import code.business.domain.Creature;
import code.infrastructure.configuration.TestApplicationConfiguration;
import code.infrastructure.database.repository.AddressRepository;
import lombok.AllArgsConstructor;
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

@Testcontainers
@SpringJUnitConfig(value = {TestApplicationConfiguration.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CycleServiceTest {

   @Container
   static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>("postgres:16.1");

   @DynamicPropertySource
   static void postgreSQLProperties(DynamicPropertyRegistry registry) {
      registry.add("jakarta.persistence.jdbc.url", postgreSQL::getJdbcUrl);
      registry.add("jakarta.persistence.jdbc.user", postgreSQL::getUsername);
      registry.add("jakarta.persistence.jdbc.password", postgreSQL::getPassword);
   }

   @BeforeEach
   void cleanDatabase() {

   }

   @Test
   public void createCreatures() {

   }

   @Test
   public void calculatePriority() {

   }

   @Test
   public void assignFood(List<Creature> chosen) {

   }

   @Test
   public void calculateSaturation() {

   }

   @Test
   public void advanceAge() {

   }

   @Test
   public void assignDebuffs() {

   }



}