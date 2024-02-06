package code.business.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

@Builder
@With
@Value
public class DeadCreature {
   String id;
   Integer cyclesLived;
   String name;
   Integer birthCycle;
   Integer causeOfDeath;
}