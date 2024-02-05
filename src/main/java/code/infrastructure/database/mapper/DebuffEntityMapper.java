package code.infrastructure.database.mapper;

import code.business.domain.Debuff;
import code.infrastructure.database.entity.DebuffEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DebuffEntityMapper {

   Debuff mapFromEntity(DebuffEntity debuffEntity);

   DebuffEntity mapToEntity(Debuff debuff);
}