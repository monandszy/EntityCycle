package code.business.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

import java.util.Set;

@Builder
@With
@Value
@ToString(exclude = "foods")
@EqualsAndHashCode(exclude = "foods")
public class Creature {
   String id;
   Integer age;
   String name;
   Integer saturation;
   Integer birthCycle;
   Address address;
   Set<Food> foods;
}