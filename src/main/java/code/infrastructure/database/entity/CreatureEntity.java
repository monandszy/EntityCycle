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

@Entity
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "creature", schema = "entity_cycle")
public class CreatureEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "creature_id")
   private Integer id;

   @Column(name = "age")
   private Integer age;

   @Column(name = "name")
   private String name;

   @Column(name = "saturation")
   private Integer saturation;

   @Column(name = "birth_cycle")
   private Integer birthCycle;
   
//   private AddressEntity addressEntity;
}