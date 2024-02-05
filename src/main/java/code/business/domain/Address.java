package code.business.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Builder
@With
@Value
public class Address {
   String id;
   String city;
   String postalCode;
   String street;
}