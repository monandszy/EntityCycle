package code.business.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Builder
@With
@Value
public class Injury {
   String id;
   Debuff debuff;
   Creature creature;
}