package code.business.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Food {
   Integer id;
   Integer nutritionalValue;
   String description;
   Creature creature;
}