package code.business.dao;

import code.business.domain.Creature;

import java.util.List;

public interface AgeDAO {
   void advanceSaturation();

   void advanceAge();

   List<Creature> getAged();
}