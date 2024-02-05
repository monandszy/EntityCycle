package code.infrastructure.database.mapper;

import code.business.domain.Injury;
import code.infrastructure.database.entity.InjuryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InjuryEntityMapper {

   Injury mapFromEntity(InjuryEntity injuryEntity);

   Injury mapToEntity(Injury injury);
}