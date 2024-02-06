package code.infrastructure.database.repository;

import code.business.dao.AddressDAO;
import code.business.domain.Address;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
public class AddressRepository implements AddressDAO {
   @Override
   public Address getRandomExisitngAddress() {
      return null;
   }
}