package code.infrastructure.repository;

import code.infrastructure.configuration.TestApplicationConfiguration;
import code.infrastructure.database.repository.AddressRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringJUnitConfig(value = {TestApplicationConfiguration.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ServiceTest {

   @Container
   static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>("postgres:16.1");

   @DynamicPropertySource
   static void postgreSQLProperties(DynamicPropertyRegistry registry) {
      registry.add("jakarta.persistence.jdbc.ur", postgreSQL::getJdbcUrl);
      registry.add("jakarta.persistence.jdbc.user", postgreSQL::getUsername);
      registry.add("jakarta.persistence.jdbc.password", postgreSQL::getPassword);
   }

   private final AddressRepository addressRepository;

}