package code.infrastructure.database.mapper;

import code.business.domain.Creature;
import code.infrastructure.database.entity.CreatureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreatureEntityMapper {

   Creature mapToEntity(CreatureEntity creatureEntity);

   CreatureEntity mapFromEntity(Creature creature);
}