package code.infrastructure.database.mapper;

import code.business.domain.Food;
import code.infrastructure.database.entity.FoodEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FoodEntityMapper {

   Food mapFromEntity(FoodEntity foodEntity);

   FoodEntity mapToEntity(Food food);
}