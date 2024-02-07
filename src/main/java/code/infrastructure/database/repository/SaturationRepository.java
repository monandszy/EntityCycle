package code.infrastructure.database.repository;

import code.business.dao.SaturationDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public class SaturationRepository implements SaturationDAO {
   @Override
   public void eatIfHungry() {

   }

   @Override
   public void addFoodPoisoningDebuff() {

   }

   @Override
   public void killStarving() {

   }

   @Override
   public void addStarvationDebuff() {

   }
}