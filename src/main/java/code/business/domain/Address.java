package code.business.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.OffsetDateTime;

@Builder
@With
@Value
public class Address {
   String id;
   String city;
   String postalCode;
   String street;
   OffsetDateTime timeCreated;
}