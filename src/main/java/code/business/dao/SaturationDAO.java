package code.business.dao;

import code.business.domain.Creature;

import java.util.List;

public interface SaturationDAO {
   List<Creature> eatIfHungry();

   void updateDebuffs(List<Creature> creatures);

   void killStarving();

   void addStarvationDebuff();
}