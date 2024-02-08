package code.infrastructure.database.mapper;

import code.business.domain.Address;
import code.business.domain.Creature;
import code.infrastructure.database.entity.AddressEntity;
import code.infrastructure.database.entity.CreatureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreatureEntityMapper {

   @Mapping(target = "address", ignore = true)
   @Mapping(target = "foods", ignore = true)
   Creature mapFromEntity(CreatureEntity creatureEntity);

   @Mapping(target = "foods", ignore = true)
   @Mapping(source = "address", target = "address", qualifiedByName = "addressToEntity")
   CreatureEntity mapToEntityWithAddress(Creature creature);

   Address mapFromEntity(AddressEntity addressEntity);

   @Named("addressToEntity")
   @SuppressWarnings("unused")
   AddressEntity mapToEntity(Address address);

}