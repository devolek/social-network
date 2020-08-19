package com.skillbox.socialnetwork.main.dto.GeoIP;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeoIP {
    private String ipAddress;
    private String city;
    private String country;
    private String latitude;
    private String longitude;
    // constructors, getters and setters...
}
