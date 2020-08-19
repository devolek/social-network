package com.skillbox.socialnetwork.main.service.impl;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.skillbox.socialnetwork.main.dto.GeoIP.GeoIP;
import com.skillbox.socialnetwork.main.service.GeoIPLocationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Service
public class GeoIPLocationServiceImpl implements GeoIPLocationService {
    private DatabaseReader dbReader;

    public GeoIPLocationServiceImpl(@Value("${path.to.dblocation.city}") String pathToDb)
            throws IOException {
        File database = ResourceUtils.getFile(pathToDb);
        dbReader = new DatabaseReader.Builder(database).build();
    }

    public GeoIP getLocation(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = null;
            response = dbReader.city(ipAddress);
            String cityName = response.getCity().getName();
            String countryName = response.getCountry().getName();
            String latitude =
                    response.getLocation().getLatitude().toString();
            String longitude =
                    response.getLocation().getLongitude().toString();
            return new GeoIP(ip, cityName, countryName, latitude, longitude);
        } catch (GeoIp2Exception | IOException e) {
            return new GeoIP(ip, "", "", "", "");
        }
    }
}
