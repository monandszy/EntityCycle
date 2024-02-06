package code.infrastructure.database.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@ToString(exclude = {"address", "food", "debuffs"})
@EqualsAndHashCode(exclude = {"address", "food", "debuffs"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "creature", schema = "entity_cycle")
public class CreatureEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "creature_id", unique = true, nullable = false)
   private Integer id;

   @Column(name = "age")
   private Integer age;

   @Column(name = "name")
   private String name;

   @Column(name = "saturation")
   private Integer saturation;

   @Column(name = "birth_cycle")
   private Integer birthCycle;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "address_id")
   private AddressEntity address;

   @OneToOne(fetch = FetchType.LAZY, mappedBy = "creature", cascade = CascadeType.ALL)
   private FoodEntity food;

   @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
   @JoinTable(
           name = "injury",
           joinColumns = {@JoinColumn(name = "creature_id")},
           inverseJoinColumns = {@JoinColumn(name = "debuff_id")}
   )
   private Set<DebuffEntity> debuffs;
}