package code.business.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Builder
@With
@Value
public class Creature {
   String id;
   Integer age;
   String name;
   Integer saturation;
   Integer birthCycle;
   Address address;
}