package code.infrastructure.database.mapper;

import code.business.domain.DeadCreature;
import code.infrastructure.database.entity.DeadCreatureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeadCreatureEntityMapper {

   DeadCreature mapFromEntity(DeadCreatureEntity deadCreatureEntity);

   DeadCreatureEntity mapToEntity(DeadCreature deadCreature);
}