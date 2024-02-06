package code.business.dao;

import code.business.domain.Creature;

import java.util.List;

public interface CreatureDAO {
   Integer getOffspringNumber();

   void addAll(List<Creature> creatures);

   List<Creature> getPrioritized(int Limit);

   void updateFood(List<Creature> prioritized);

}