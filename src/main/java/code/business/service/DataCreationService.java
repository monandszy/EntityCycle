package code.business.service;

import code.business.dao.AddressDAO;
import code.business.domain.Address;
import code.business.domain.Creature;
import code.business.domain.Food;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class DataCreationService {

   private static final int FOOD_NUTRITIONAL_VALUE_THRESHOLD = 10;
   private final AddressDAO addressDAO;

   public void addFood(List<Creature> prioritized) {
      for (Creature creature : prioritized) {
         creature.getFoods().add(getRandomFood());
      }
   }

   private Food getRandomFood() {
      return Food.builder()
              .description(getRandomString(20))
              .nutritionalValue(getRandomNumber(FOOD_NUTRITIONAL_VALUE_THRESHOLD))
              .build();
   }

   public List<Creature> getRandomCreatureList(int amount) {
      ArrayList<Creature> creatures = new ArrayList<>(amount);
      for (int i = 0; i < amount; i++) {
         creatures.add(getRandomCreature());
      }
      return creatures;
   }

   public Creature getRandomCreature() {
      return Creature.builder()
              .name(getRandomString(5))
              .address(getRandomAddress())
              .birthCycle(CycleService.getCurrentCycle())
              .saturation(getRandomNumber(10))
              .age(getRandomNumber(3))
              .build();
   }

   private Address getRandomAddress() {
      if (Math.random() > 0.2) {
         return Address.builder()
                 .city(getRandomString(7))
                 .street(getRandomString(20))
                 .postalCode(getRandomString(11))
                 .timeCreated(OffsetDateTime.now())
                 .build();
      } else {
         return addressDAO.getRandomExisitngAddress();
      }
   }

   public String getRandomString(int length) {
      byte[] array = new byte[length];
      new Random().nextBytes(array);
      return new String(array, StandardCharsets.UTF_8);
   }

   public int getRandomNumber(int top) {
      int i = Double.valueOf(Math.random()).intValue();
      return top * i;
   }

}