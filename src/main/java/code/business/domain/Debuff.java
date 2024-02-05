package code.business.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Builder
@With
@Value
public class Debuff {
   Integer id;
   String description;
   Integer value;
}