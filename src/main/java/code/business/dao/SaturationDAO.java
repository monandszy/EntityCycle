package code.business.dao;

import code.business.domain.Creature;
import code.business.domain.Debuff;

import java.util.List;

public interface SaturationDAO {
   List<Creature> eatIfHungry();

   void updateDebuffs(List<Creature> creatures);

   void killStarving();

   void addStarvationDebuff(Debuff starvationDebuff);
}