package code.infrastructure.database.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@ToString(exclude = {"creatures", "timeCreated"})
@EqualsAndHashCode(exclude = {"creatures", "timeCreated"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address", schema = "entity_cycle")
public class AddressEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "address_id", unique = true, nullable = false)
   private Integer id;

   @Column(name = "city")
   private String city;

   @Column(name = "postal_code")
   private String postalCode;

   @Column(name = "time_created")
   private OffsetDateTime timeCreated;

   @Column(name = "street")
   private String street;

   @OneToMany(fetch = FetchType.LAZY, mappedBy = "address", cascade = CascadeType.ALL)
   private Set<CreatureEntity> creatures;
}