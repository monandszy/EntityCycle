package code.infrastructure.database.repository;

import code.business.dao.AddressDAO;
import code.business.domain.Address;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Component
public class AddressRepository implements AddressDAO {
   @Override
   public Optional<Address> getRandomExistingAddress() {
      return Optional.empty();
   }
}