package code.infrastructure.database.repository;

import code.business.dao.AddressDAO;
import code.business.domain.Address;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AddressRepository implements AddressDAO {
   @Override
   public Optional<Address> getRandomExistingAddress() {
      return Optional.empty();
   }
}