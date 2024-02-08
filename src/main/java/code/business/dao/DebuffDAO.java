package code.business.dao;

import code.business.domain.Debuff;
import code.business.domain.DebuffType;

import java.util.Optional;

public interface DebuffDAO {

   Optional<Debuff> getRandomDebuff(DebuffType debuffType);
}