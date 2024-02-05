package code.infrastructure.database.mapper;

import code.business.domain.Address;
import code.infrastructure.database.entity.AddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressEntityMapper {

   Address mapFromEntity(AddressEntity addressEntity);

   AddressEntity mapToEntity(Address address);
}