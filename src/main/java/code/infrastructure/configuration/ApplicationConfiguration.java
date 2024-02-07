package code.infrastructure.configuration;

import code._ComponentScanMarker;
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
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.TimeZone;

@Configuration
@AllArgsConstructor
@EnableTransactionManagement
@ComponentScan(basePackageClasses = {_ComponentScanMarker.class})
@PropertySource(value = "classpath:data.properties")
public class ApplicationConfiguration {

   private Environment env;

   @PostConstruct
   public void init() {
      TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
   }

   @Bean
   public DataSource dataSource() {
      final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
      try {
         Class.forName("org.postgresql.Driver");
         System.out.println("CLASS FOUND!");
         //on classpath
      } catch(ClassNotFoundException e) {
         // not on classpath
         throw new RuntimeException(e);
      }
//      dataSource.setDriverClassName();
//      dataSource.setDriverClassName((Objects.requireNonNull(env.getProperty("jakarta.persistence.jdbc.driver"))));
      dataSource.setDriver(new Driver());
      dataSource.setUrl(env.getProperty("jakarta.persistence.jdbc.url"));
      dataSource.setUsername(env.getProperty("jakarta.persistence.jdbc.user"));
      dataSource.setPassword(env.getProperty("jakarta.persistence.jdbc.password"));
      dataSource.setSchema(env.getProperty("hibernate.default_schema"));
      return dataSource;
   }

   @Bean(initMethod = "migrate")
   @DependsOn("dataSource")
   Flyway flyway() {
      ClassicConfiguration configuration = new ClassicConfiguration();
//      configuration.setDriver(env.getProperty("jakarta.persistence.jdbc.driver"));
//      configuration.setUrl(env.getProperty("jakarta.persistence.jdbc.url"));
//      configuration.setUser(env.getProperty("jakarta.persistence.jdbc.user"));
//      configuration.setPassword(env.getProperty("jakarta.persistence.jdbc.password"));
//      configuration.setSchemas(new String[] {env.getProperty("hibernate.default_schema")});
      configuration.setDataSource(dataSource());
      configuration.setBaselineOnMigrate(true);
      configuration.setLocations(new Location("filesystem:src/main/resources/database/migrations"));
      return new Flyway(configuration);
   }
}