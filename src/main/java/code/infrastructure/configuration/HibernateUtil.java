package code.infrastructure.configuration;

import code.infrastructure.database.entity.AddressEntity;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.entity.DeadCreatureEntity;
import code.infrastructure.database.entity.DebuffEntity;
import code.infrastructure.database.entity.FoodEntity;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Component
@NoArgsConstructor
@PropertySource(value = "classpath:data.properties")
public class HibernateUtil {

   // cannot be static...
   private org.springframework.core.env.Environment env;

   private final Map<String, Object> HIBERNATE_SETTINGS = new HashMap<>(Map.ofEntries(
           Map.entry(Environment.DRIVER, "org.postgresql.Driver"),
           Map.entry(Environment.URL, "jdbc:postgresql://localhost:5432/java_model"),
           Map.entry(Environment.USER, "postgres"),
           Map.entry(Environment.PASS, "postgres"),
           Map.entry(Environment.DEFAULT_SCHEMA, "entity_cycle"),
           Map.entry(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect"), //deprecated (automatic)
           Map.entry(Environment.HBM2DDL_AUTO, "none"),
           Map.entry(Environment.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider"),
           Map.entry(Environment.SHOW_SQL, true),
           Map.entry(Environment.FORMAT_SQL, false),
           Map.entry(Environment.USE_SQL_COMMENTS, false))
   );

//   private final Map<String, Object> HIKARI_CP_SETTING = Map.ofEntries(
//           Map.entry(Environment.JAKARTA_JDBC_DRIVER, "org.postgresql.Driver"),
//           Map.entry("hibernate.hikari.connectionTimeout", "20000"),
//           Map.entry("hibernate.hikari.minimumIdle", "10"),
//           Map.entry("hibernate.hikari.maximumPoolSize", "20"),
//           Map.entry("hibernate.hikari.idleTimeout", "300000")
//   );

   private final Map<String, Object> CACHE_SETTINGS = Map.ofEntries(
           Map.entry(Environment.CACHE_REGION_FACTORY, "jcache"),
           Map.entry("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider"),
           Map.entry("hibernate.javax.cache.uri", "/META-INF/ehcache.xml"),
           Map.entry(Environment.USE_SECOND_LEVEL_CACHE, true)
   );


   private static SessionFactory sessionFactory = null;

   @Autowired
   public HibernateUtil(org.springframework.core.env.Environment env) {
      this.env = env;
   }

   public void loadTestSessionFactory(Map<String, Object> HIBERNATE_SETTINGS_OVERRIDE) {
      if (sessionFactory == null) {
         initializeParameterSource();
      }
      System.out.println(HIBERNATE_SETTINGS);
      HIBERNATE_SETTINGS.putAll(HIBERNATE_SETTINGS_OVERRIDE);
      System.out.println(HIBERNATE_SETTINGS);
      try {
         ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                 .applySettings(HIBERNATE_SETTINGS)
//                 .applySettings(HIKARI_CP_SETTING)
                 .applySettings(CACHE_SETTINGS)
                 .build();

         Metadata metadata = getMetadata(serviceRegistry);

         sessionFactory = metadata.getSessionFactoryBuilder().build();
      } catch (Throwable ex) {
         throw new ExceptionInInitializerError(ex);
      }
   }

   private SessionFactory loadSessionFactory() {
      try {
         ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                 .applySettings(HIBERNATE_SETTINGS)
//                 .applySettings(HIKARI_CP_SETTING)
                 .applySettings(CACHE_SETTINGS)
                 .build();

         Metadata metadata = getMetadata(serviceRegistry);

         return metadata.getSessionFactoryBuilder().build();
      } catch (Throwable ex) {
         throw new ExceptionInInitializerError(ex);
      }
   }

   private static HibernateUtil initializeParameterSource() {
      ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
      return context.getBean(HibernateUtil.class);
   }

   private static Metadata getMetadata(ServiceRegistry serviceRegistry) {
      return new MetadataSources(serviceRegistry)
              .addAnnotatedClass(AddressEntity.class)
              .addAnnotatedClass(CreatureEntity.class)
              .addAnnotatedClass(DeadCreatureEntity.class)
              .addAnnotatedClass(DebuffEntity.class)
              .addAnnotatedClass(FoodEntity.class)
              .getMetadataBuilder()
              .build();
   }

   public static void closeSessionFactory() {
         sessionFactory.close();
   }

   public static Session getSession() {
      if (sessionFactory == null) {
         sessionFactory = initializeParameterSource().loadSessionFactory();
      }
      Objects.requireNonNull(sessionFactory);
      Session session = sessionFactory.openSession();
      Objects.requireNonNull(session);
      return session;
   }
}