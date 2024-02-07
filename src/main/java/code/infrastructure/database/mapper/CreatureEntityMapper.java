package code.infrastructure.database.mapper;

import code.business.domain.Creature;
import code.infrastructure.database.entity.CreatureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreatureEntityMapper {

   @Mapping(target = "address", ignore = true)
   @Mapping(target = "foods", ignore = true)
   Creature mapFromEntity(CreatureEntity creatureEntity);

   CreatureEntity mapToEntity(Creature creature);
}