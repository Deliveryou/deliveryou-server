package com.delix.deliveryou.api.locationiq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LocationIQAddress {
    public String name;
    public String house_number;
    public String road;
    public String neighbourhood;
    public String suburb;
    public String island;
    public String city;
    public String county;
    public String state;
    public String state_code;
    public String postcode;
    public String country;
    public String country_code;
}
