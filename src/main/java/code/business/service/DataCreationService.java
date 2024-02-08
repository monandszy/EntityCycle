package code.business.service;

import code.business.dao.AddressDAO;
import code.business.dao.DebuffDAO;
import code.business.domain.Address;
import code.business.domain.Creature;
import code.business.domain.Debuff;
import code.business.domain.DebuffType;
import code.business.domain.Food;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static code.business.management.InputData.OFFSPRING_FOOD_THRESHOLD;

@Service
@AllArgsConstructor
public class DataCreationService {

   private static final int FOOD_NUTRITIONAL_VALUE_THRESHOLD = 10;
   private final AddressDAO addressDAO;
   private final DebuffDAO debuffDAO;

   public void addFood(List<Creature> prioritized) {
      for (Creature creature : prioritized) {
         creature.getFoods().add(getRandomFood().withCreature(Creature.builder().id(creature.getId()).build()));
      }
   }

   public Food getRandomFood() {
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
         return getRandomBuilderAddress();
      } else {
         Optional<Address> randomExistingAddress = addressDAO.getRandomExistingAddress();
         if (randomExistingAddress.isEmpty())
            return getRandomBuilderAddress();

         return randomExistingAddress.orElseThrow();
      }
   }

   private Address getRandomBuilderAddress() {
      return Address.builder()
              .city(getRandomString(7))
              .street(getRandomString(20))
              .postalCode(getRandomString(11))
              .timeCreated(OffsetDateTime.now())
              .build();
   }

   static int leftLimit = 97; // letter 'a'
   static int rightLimit = 122; // letter 'z'

   public String getRandomString(int length) {
      Random random = new Random();
      return random.ints(leftLimit, rightLimit + 1)
              .limit(length)
              .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
              .toString();
   }

   public int getRandomNumber(int top) {
      int i = Double.valueOf(Math.random()).intValue();
      return top * i;
   }

   public void addRandomPoisoningDebuffs(List<Creature> creatures) {
      for (Creature creature : creatures) {
         if (OFFSPRING_FOOD_THRESHOLD + creature.getSaturation() * 2 > Math.random() * 1000D) {
            addRandomPoisoningDebuff(creature);
         }
      }
   }

   public void addRandomPoisoningDebuff(Creature creature) {
      if (Math.random() > 0.2) {
         addPoisoningDebuff(creature);
      } else {
         Optional<Debuff> randomExistingDebuff = debuffDAO.getRandomDebuff(DebuffType.poisoning);
         if (randomExistingDebuff.isEmpty())
            addPoisoningDebuff(creature);
         else creature.getDebuffs().add(randomExistingDebuff.orElseThrow());
      }
   }

   public void addPoisoningDebuff(Creature creature) {
      Debuff build = Debuff.builder()
              .debuffType(DebuffType.poisoning)
              .description("you've eaten smth bad, sorry")
              .saturationDrain(getRandomNumber(5))
              .build();
      creature.getDebuffs().add(build);
   }

   public Debuff getRandomStarvationDebuff() {
      return Debuff.builder()
              .debuffType(DebuffType.starvation)
              .description("times have been rough")
              .saturationDrain(getRandomNumber(10))
              .build();
   }

   public void addRandomAgeDebuffs(List<Creature> aged) {
      for (Creature creature : aged) {
         if (Math.random() * 1000D < creature.getAge()*4)
         if (Math.random() > 0.2) {
            addAgeDebuff(creature);
         } else {
            Optional<Debuff> randomExistingDebuff = debuffDAO.getRandomDebuff(DebuffType.fracture);
            if (randomExistingDebuff.isEmpty())
               addAgeDebuff(creature);
            else creature.getDebuffs().add(randomExistingDebuff.orElseThrow());
         }
      }
   }

   public void addAgeDebuff(Creature creature) {
      Debuff build = Debuff.builder()
              .debuffType(DebuffType.starvation)
              .description("you're old enough to die, accept it")
              .saturationDrain(getRandomNumber(20))
              .build();
      creature.getDebuffs().add(build);   }
}