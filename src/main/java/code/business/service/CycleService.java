package code.business.service;

import code.business.dao.CreatureDAO;
import code.business.domain.Creature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CycleService {

   @Getter
   @Setter
   private static Integer currentCycle = 0;

   CreatureDAO creatureDAO;
   DataCreationService dataCreationService;


   public void createCreatures() {
      Integer toBeCreatedCounter = creatureDAO.getOffspringNumber(); // remove 3 food if above threshold
      List<Creature> creatures = dataCreationService.getRandomCreatureList(toBeCreatedCounter);
      creatureDAO.addAll(creatures);
   }

   public void assignFood() {
      List<Creature> prioritized = creatureDAO.getPrioritized(); // calculate prioritization
      creatureDAO.createFood(prioritized); // add piece of food to prioritized
   }

   public void calculateSaturation() {
      creatureDAO.eatIfHungry(); // remove food, recalculate saturation
      creatureDAO.addFoodPoisoningDebuff(); // random
      creatureDAO.killStarving(); // if saturation <= 0 and starving -> kill
      creatureDAO.addStarvationDebuff(); // one chance to survive starving kill
   }

   public void advanceAge() {
      creatureDAO.advanceSaturation();
      creatureDAO.advanceAge(); // move age by one
      creatureDAO.assignAgeDebuff(); // random
   }

   public void runCycles(int cycleNumber) {
      for (int i = 0; i < cycleNumber; i++) {
         createCreatures();
         assignFood();
         calculateSaturation();
         advanceAge();
         currentCycle++;
      }
   }

}