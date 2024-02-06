package code.infrastructure.configuration;

import code.infrastructure.database.entity.AddressEntity;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.entity.DeadCreatureEntity;
import code.infrastructure.database.entity.DebuffEntity;
import code.infrastructure.database.entity.FoodEntity;
import lombok.SneakyThrows;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;
import java.util.Objects;

import static org.hibernate.cfg.Environment.DIALECT;
import static org.hibernate.cfg.Environment.FORMAT_SQL;
import static org.hibernate.cfg.Environment.HBM2DDL_AUTO;
import static org.hibernate.cfg.Environment.JAKARTA_JDBC_DRIVER;
import static org.hibernate.cfg.Environment.JAKARTA_JDBC_PASSWORD;
import static org.hibernate.cfg.Environment.JAKARTA_JDBC_URL;
import static org.hibernate.cfg.Environment.JAKARTA_JDBC_USER;
import static org.hibernate.cfg.Environment.SHOW_SQL;
import static org.hibernate.cfg.Environment.USE_SQL_COMMENTS;
import static org.hibernate.cfg.MappingSettings.DEFAULT_SCHEMA;


@Configuration
@PropertySource("classpath:hibernate.properties")
public class HibernateUtil {

   private static org.springframework.core.env.Environment env;

   private static final SessionFactory sessionFactory = loadSessionFactory();

   private static final Map<String, Object> SETTINGS = Map.ofEntries(
           Map.entry(JAKARTA_JDBC_DRIVER, Objects.requireNonNull(env.getProperty(JAKARTA_JDBC_DRIVER))),
           Map.entry(JAKARTA_JDBC_URL, Objects.requireNonNull(env.getProperty(JAKARTA_JDBC_URL))),
           Map.entry(JAKARTA_JDBC_USER, Objects.requireNonNull(env.getProperty(JAKARTA_JDBC_USER))),
           Map.entry(JAKARTA_JDBC_PASSWORD, Objects.requireNonNull(env.getProperty(JAKARTA_JDBC_PASSWORD))),
           Map.entry(DEFAULT_SCHEMA, Objects.requireNonNull(env.getProperty(DEFAULT_SCHEMA))),
           Map.entry(DIALECT, Objects.requireNonNull(env.getProperty(DIALECT))),
           Map.entry(HBM2DDL_AUTO, Objects.requireNonNull(env.getProperty(HBM2DDL_AUTO))),
           Map.entry(SHOW_SQL, Objects.requireNonNull(env.getProperty(SHOW_SQL))),
           Map.entry(FORMAT_SQL, Objects.requireNonNull(env.getProperty(FORMAT_SQL))),
           Map.entry(USE_SQL_COMMENTS, Objects.requireNonNull(env.getProperty(USE_SQL_COMMENTS)))
   );

   private static SessionFactory loadSessionFactory() {
      try {
         StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                 .applySettings(SETTINGS)
                 .build();

         Metadata metadata = new MetadataSources(standardRegistry)
                 .addAnnotatedClass(AddressEntity.class)
                 .addAnnotatedClass(CreatureEntity.class)
                 .addAnnotatedClass(DeadCreatureEntity.class)
                 .addAnnotatedClass(DebuffEntity.class)
                 .addAnnotatedClass(FoodEntity.class)
                 .getMetadataBuilder()
                 .build();
         return metadata.buildSessionFactory();
      } catch (Throwable ex) {
         throw new ExceptionInInitializerError(ex);
      }
   }

   @SneakyThrows
   public static Session getSession() {
      Session session = sessionFactory.openSession();
      Objects.requireNonNull(session);
      return session;
   }

   @SneakyThrows
   public static void closeSessionFactory() {
      sessionFactory.close();
   }
}