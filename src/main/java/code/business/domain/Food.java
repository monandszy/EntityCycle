package code.business.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Food {
   String id;
   Integer nutritionalValue;
   String description;
   Creature creature;
}