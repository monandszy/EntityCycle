package code.infrastructure.configuration;

import code._ComponentScanMarker;
import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.TimeZone;

@Configuration
@ComponentScan(basePackageClasses = {_ComponentScanMarker.class})
@PropertySource(value = "classpath:database.properties")
public class TestApplicationConfiguration {

   private Environment env;

   @PostConstruct
   public void init() {
      TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
   }

   @Bean
   public DataSource dataSource() {
      final DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("jdbc.driverClassName")));
      dataSource.setUrl(env.getProperty("jdbc.url"));
      dataSource.setUsername(env.getProperty("jdbc.user"));
      dataSource.setPassword(env.getProperty("jdbc.pass"));
      dataSource.setSchema(env.getProperty("jdbc.schema"));
      return dataSource;
   }

   @Bean(initMethod = "migrate")
   @DependsOn("dataSource")
   Flyway flyway() {
      ClassicConfiguration configuration = new ClassicConfiguration();
      configuration.setBaselineOnMigrate(true);
      configuration.setLocations(new Location("filesystem:src/main/resources/database/migrations"));
      configuration.setDataSource(dataSource());
      return new Flyway(configuration);
   }
}