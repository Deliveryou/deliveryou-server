package com.delix.deliveryou.api.locationiq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LocationIQData {
//    String place_id;
//    String licence;
//    String osm_type;
//    String osm_id;
//    String[] boundingbox;
    public double lat;
    public double lon;
    public String display_name;
//    String importance;
//    String icon;
    public LocationIQAddress address;
//    String extratags;
//    String namedetails;
//    String geojson;
//    String geokml;
//    String svg;
//    String geotext;
}
