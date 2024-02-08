package code.infrastructure.database.entity;

import code.business.domain.DebuffType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
@ToString(exclude = "creatures")
@EqualsAndHashCode(exclude = "creatures")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "debuff", schema = "entity_cycle")
public class DebuffEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "debuff_id")
   private Integer id;

   @Column(name = "saturation_drain")
   private Integer saturationDrain;

   @Column(name = "description")
   private String description;

   @Enumerated(EnumType.STRING)
   @Column(name = "value", unique = true)
   private DebuffType value;

   @ManyToMany(mappedBy = "debuffs")
   private Set<CreatureEntity> creatures;
}