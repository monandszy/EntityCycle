package code.business.service;

import code.business.domain.Address;
import code.business.domain.Creature;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Random;

@Service
@AllArgsConstructor
public class DataCreationService {

   private final AddressDAO addressDAO;

   public Creature getRandomCreature() {
      return Creature.builder()
              .name(getRandomString(5))
              .address(getRandomAddress())
              .birthCycle(CycleService.getCurrentCycle())
              .saturation(getRandomNumber(10))
              .age(getRandomNumber(3))
              .build();
   }

   private Address getRandomAddress() {
      if (Math.random() > 0.3) {
         return Address.builder()
                 .city(getRandomString(7))
                 .street(getRandomString(20))
                 .postalCode(getRandomString(11))
                 .timeCreated(OffsetDateTime.now())
                 .build();
      } else {
         return addressDAO.getRandomExisitngAddress();
      }
   }

   public String getRandomString(int length) {
      byte[] array = new byte[length];
      new Random().nextBytes(array);
      return new String(array, StandardCharsets.UTF_8);
   }

   public int getRandomNumber(int top) {
      int i = Double.valueOf(Math.random()).intValue();
      return top * i;
   }


}