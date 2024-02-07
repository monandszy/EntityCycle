package code.business.dao;

import code.business.domain.Address;

import java.util.Optional;

public interface AddressDAO {
   Optional<Address> getRandomExistingAddress();
}