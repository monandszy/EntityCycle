package code.infrastructure.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Entity
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dead_creature", schema = "entity_cycle")
public class DeadCreatureEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "dead_creature_id")
   private String id;

   @Column(name = "cycles_lived")
   private Integer cyclesLived;

   @Column(name = "time_lived")
   private OffsetDateTime timeLived;

   @Column(name = "name")
   private String name;

   @Column(name = "birth_cycle")
   private Integer birthCycle;

   @Column(name = "cause_of_death")
   private Integer causeOfDeath;
}