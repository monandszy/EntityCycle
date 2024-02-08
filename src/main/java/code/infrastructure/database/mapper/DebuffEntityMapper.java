package code.infrastructure.database.mapper;

import code.business.domain.Creature;
import code.business.domain.Debuff;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.entity.DebuffEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DebuffEntityMapper {

   @Mapping(target = "debuffType", source = "value")
   Debuff mapFromEntity(DebuffEntity entity);

   @Mapping(target = "value", source = "debuffType")
   DebuffEntity mapToEntity(Debuff debuff);

   @Mapping(target = "address", ignore = true)
   @Mapping(target = "foods", ignore = true)
   @Mapping(source = "debuffs", target = "debuffs", qualifiedByName = "debuffToNullMapper")
   CreatureEntity mapToEntityWithDebuff(Creature creature);

   @Mapping(target = "address", ignore = true)
   @Mapping(target = "foods", ignore = true)
   @Mapping(source = "debuffs", target = "debuffs", qualifiedByName = "debuffFromNullMapper")
   Creature mapFromEntityWithDebuff(CreatureEntity creatureEntity);

   @Named("debuffToNullMapper")
   @SuppressWarnings("unused")
   default Set<DebuffEntity> debuffToNullMapper(Set<Debuff> debuffs) {
      if (debuffs == null) {
         return new HashSet<DebuffEntity>();
      } else return debuffs.stream().map(this::mapToEntity)
              .collect(Collectors.toCollection(HashSet::new));
   }

   @Named("debuffFromNullMapper")
   @SuppressWarnings("unused")
   default Set<Debuff> debuffFromNullMapper(Set<DebuffEntity> debuffEntities) {
      if (debuffEntities == null) {
         return new HashSet<Debuff>();
      } else return debuffEntities.stream().map(this::mapFromEntity)
              .collect(Collectors.toCollection(HashSet::new));
   }
}