package code.infrastructure.repository;

import code.infrastructure.configuration.TestApplicationConfiguration;
import code.infrastructure.database.entity.AddressEntity;
import code.infrastructure.database.repository.AddressRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.Optional;

@Testcontainers
@SpringJUnitConfig(value = {TestApplicationConfiguration.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CRUDTest {

   @Container
   static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>("postgres:16.1");

   @DynamicPropertySource
   static void postgreSQLProperties(DynamicPropertyRegistry registry) {
      registry.add("jakarta.persistence.jdbc.ur", postgreSQL::getJdbcUrl);
      registry.add("jakarta.persistence.jdbc.user", postgreSQL::getUsername);
      registry.add("jakarta.persistence.jdbc.password", postgreSQL::getPassword);
   }

   private final AddressRepository addressRepository;

   @Test
   void addressTest() {
      // Create
      AddressEntity test1 = AddressEntity.builder()
              .city("test1city")
              .street("test1street")
              .postalCode("test1postalCode")
              .build();

      addressRepository.create(test1);
      Assertions.assertNotNull(test1.getId());

      // Read
      Optional<AddressEntity> addressEntity = addressRepository.get(test1.getId());
      Assertions.assertTrue(addressEntity.isPresent());
      Assertions.assertSame(test1, addressEntity.get());

      // Update
      test1.setCity("newCity");
      addressRepository
   }
}