package code.infrastructure.database.mapper;

import code.business.domain.Creature;
import code.business.domain.Food;
import code.infrastructure.database.entity.CreatureEntity;
import code.infrastructure.database.entity.FoodEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FoodEntityMapper {

   @Mapping(source = "foods", target = "foods", qualifiedByName = "foodToNullMapper")
   @Mapping(target = "address", ignore = true)
   @Mapping(target = "debuffs", ignore = true)
   CreatureEntity mapToEntityWithFood(Creature creature);

   @Mapping(target = "address", ignore = true)
   @Mapping(target = "debuffs", ignore = true)
   @Mapping(source = "foods", target = "foods", qualifiedByName = "foodFromNullMapper")
   Creature mapFromEntityWithFood(CreatureEntity creatureEntity);

   @Named("foodToNullMapper")
   @SuppressWarnings("unused")
   default Set<FoodEntity> foodToNullMapper(Set<Food> foods) {
      if (foods == null) {
         return new HashSet<FoodEntity>();
      } else return foods.stream().map(this::mapToEntity)
              .collect(Collectors.toCollection(HashSet::new));
   }

   @Named("foodFromNullMapper")
   @SuppressWarnings("unused")
   default Set<Food> foodFromNullMapper(Set<FoodEntity> foods) {
      if (foods == null) {
         return new HashSet<Food>();
      } else return foods.stream().map(this::mapFromEntity)
              .collect(Collectors.toCollection(HashSet::new));
   }

   @Mapping(target = "creature", ignore = true)
   Food mapFromEntity(FoodEntity foodEntity);

   @Named("foodToEntity")
   @SuppressWarnings("unused")
   FoodEntity mapToEntity(Food food);
}