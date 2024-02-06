package code.infrastructure.configuration;

import code._ComponentScanMarker;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PostConstruct;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.postgresql.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.util.TimeZone;

@Configuration
@AllArgsConstructor
@ComponentScan(basePackageClasses = {_ComponentScanMarker.class})
@PropertySource(value = "classpath:database.properties")
public class ApplicationConfiguration {

   private Environment env;

   @PostConstruct
   public void init() {
      TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
   }

   @Bean
   public SimpleDriverDataSource databaseDataSource() {
      SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
      dataSource.setDriver(new Driver());
      dataSource.setUrl(env.getProperty("jakarta.persistence.jdbc.url"));
      dataSource.setUsername(env.getProperty("jakarta.persistence.jdbc.user"));
      dataSource.setPassword(env.getProperty("jakarta.persistence.jdbc.password"));
      dataSource.setSchema(env.getProperty("hibernate.default_schema"));
      return dataSource;
   }

   @Bean(initMethod = "migrate")
   @DependsOn("databaseDataSource")
   Flyway flyway() {
      ClassicConfiguration configuration = new ClassicConfiguration();
      configuration.setBaselineOnMigrate(true);
      configuration.setLocations(new Location("filesystem:src/main/resources/database/migrations"));
      configuration.setDataSource(databaseDataSource());
      return new Flyway(configuration);
   }
}