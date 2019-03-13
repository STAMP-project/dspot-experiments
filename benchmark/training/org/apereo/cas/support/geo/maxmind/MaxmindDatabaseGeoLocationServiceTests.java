package org.apereo.cas.support.geo.maxmind;


import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.MaxMind;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.RepresentedCountry;
import com.maxmind.geoip2.record.Traits;
import java.net.InetAddress;
import java.util.ArrayList;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * This is {@link MaxmindDatabaseGeoLocationServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class MaxmindDatabaseGeoLocationServiceTests {
    @Test
    public void verifyOperation() throws Exception {
        val city = Mockito.mock(DatabaseReader.class);
        val cityResponse = new com.maxmind.geoip2.model.CityResponse(new City(), new Continent(), new Country(), new Location(), new MaxMind(), new Postal(), new Country(), new RepresentedCountry(), new ArrayList(), new Traits());
        Mockito.when(city.city(ArgumentMatchers.any(InetAddress.class))).thenReturn(cityResponse);
        val country = Mockito.mock(DatabaseReader.class);
        val countryResponse = new com.maxmind.geoip2.model.CountryResponse(new Continent(), new Country(), new MaxMind(), new Country(), new RepresentedCountry(), new Traits());
        Mockito.when(country.country(ArgumentMatchers.any(InetAddress.class))).thenReturn(countryResponse);
        val service = new MaxmindDatabaseGeoLocationService(city, country);
        val response = service.locate("127.0.0.1");
        Assertions.assertNotNull(response);
        val response2 = service.locate(100.0, 100.0);
        Assertions.assertNull(response2);
    }
}

