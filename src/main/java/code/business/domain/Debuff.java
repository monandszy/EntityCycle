package code.business.domain;

import code.infrastructure.database.entity.DebuffType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

@Builder
@With
@Value
public class Debuff {
   Integer id;
   String description;
   DebuffType debuffType;
}