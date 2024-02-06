package code.business.service;

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

   public void createCreatures() {

   }

   public void calculatePriority() {

   }

   public void assignFood(List<Creature> chosen) {

   }

   public void calculateSaturation() {

   }

   public void advanceAge() {

   }

   public void assignDebuffs() {

   }

}