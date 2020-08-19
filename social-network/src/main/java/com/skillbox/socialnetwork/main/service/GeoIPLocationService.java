package com.skillbox.socialnetwork.main.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.skillbox.socialnetwork.main.dto.GeoIP.GeoIP;

import java.io.IOException;

public interface GeoIPLocationService {
    GeoIP getLocation(String ip) throws IOException, GeoIp2Exception;
}
