package code.infrastructure.repository;

import code.business.domain.Creature;
import code.business.service.DataCreationService;
import code.business.service.DatabaseService;
import code.infrastructure.configuration.ApplicationConfiguration;
import code.infrastructure.configuration.HibernateUtil;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.mapper.CreatureEntityMapper;
import code.infrastructure.database.mapper.FoodEntityMapper;
import code.infrastructure.database.repository.CreatureRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
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

@SpringJUnitConfig(value = {ApplicationConfiguration.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Testcontainers
public class DataCreationServiceTest {

   private final DataCreationService dataCreationService;
   private final CreatureRepository creatureRepository;
   private final CreatureEntityMapper creatureEntityMapper;
   private final FoodEntityMapper foodEntityMapper;
   private final HibernateUtil hibernateUtil;
   private final DatabaseService databaseService;

   @Container
   static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>("postgres:16.1");

   @DynamicPropertySource
   static void postgreSQLProperties(DynamicPropertyRegistry registry) {
      registry.add("jdbc.url", postgreSQL::getJdbcUrl);
      registry.add("jdbc.user", postgreSQL::getUsername);
      registry.add("jdbc.pass", postgreSQL::getPassword);
   }

   @BeforeEach
   void setUp() {
      databaseService.deleteAll();
   }

   @Test
   void testFoodAssigment() {
      try (Session session = hibernateUtil.getSession()) {
         //given
         session.beginTransaction();
         Creature randomPrioritizedCreature = dataCreationService.getRandomCreature();
         CreatureEntity entity = creatureEntityMapper.mapToEntityWithAddress(randomPrioritizedCreature);
         session.persist(entity);
         session.getTransaction().commit();
         session.clear();
         Creature creature = foodEntityMapper.mapFromEntityWithFood(entity);

         //when
         dataCreationService.addFood(List.of(creature));
         creatureRepository.updateFood(List.of(creature));
         session.clear();
         // then
         Assertions.assertFalse(creature.getFoods().isEmpty());
         session.beginTransaction();
         String hql = "FROM CreatureEntity cr JOIN FETCH cr.foods WHERE cr.id = :id";
         Query<CreatureEntity> query = session.createQuery(hql, CreatureEntity.class);
         query.setParameter("id", entity.getId());
         entity = query.getResultList().getFirst();
         Assertions.assertNotNull(entity.getFoods());
         session.getTransaction().commit();
      }
   }

   @Test
   void testCreateCreatures() {
      try (Session session = hibernateUtil.getSession()) {
         session.beginTransaction();
         //given
         int CREATURE_NUMBER = 3;
         List<Creature> creatures = dataCreationService.getRandomCreatureList(CREATURE_NUMBER);

         //when
         creatureRepository.addAll(creatures);
         session.flush();

         //then
         Query<CreatureEntity> query = session.createQuery("FROM CreatureEntity cr", CreatureEntity.class);
         List<CreatureEntity> resultList = query.getResultList();
         Assertions.assertEquals(CREATURE_NUMBER, resultList.size());
         session.getTransaction().commit();
      }
   }
}