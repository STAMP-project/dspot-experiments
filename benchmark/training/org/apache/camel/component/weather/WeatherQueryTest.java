/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.weather;


import WeatherApi.Hourly;
import WeatherApi.Station;
import WeatherLanguage.nl;
import WeatherMode.JSON;
import WeatherMode.XML;
import WeatherUnits.IMPERIAL;
import WeatherUnits.METRIC;
import org.apache.camel.component.weather.geolocation.GeoLocationProvider;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class WeatherQueryTest {
    private static final String LONGITUDE = "4.13";

    private static final String LATITUDE = "51.98";

    private static final String APPID = "9162755b2efa555823cfe0451d7fff38";

    @Mock
    private GeoLocationProvider geoLocationProvider;

    @Mock
    private GeoLocationProvider exceptionThrowingGeoLocationProvider;

    @Test
    public void testBoxedQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setLon("4");
        weatherConfiguration.setLat("52");
        weatherConfiguration.setRightLon("6");
        weatherConfiguration.setTopLat("54");
        weatherConfiguration.setZoom(8);
        weatherConfiguration.setUnits(METRIC);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/box/city?bbox=4,52,6,54,8&cluster=yes&lang=en&units=metric&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testBoxedStationQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setLon("4");
        weatherConfiguration.setLat("52");
        weatherConfiguration.setRightLon("6");
        weatherConfiguration.setTopLat("54");
        weatherConfiguration.setZoom(8);
        weatherConfiguration.setUnits(METRIC);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        weatherConfiguration.setWeatherApi(Station);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/box/station?bbox=4,52,6,54,8&cluster=yes&lang=en&units=metric&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testLatLonQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setLon("4");
        weatherConfiguration.setLat("52");
        weatherConfiguration.setMode(XML);
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/weather?lat=52&lon=4&lang=nl&mode=xml&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testZipQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setZip("2493CJ,nl");
        weatherConfiguration.setMode(XML);
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/weather?zip=2493CJ,nl&lang=nl&mode=xml&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testSingleIdQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setIds("524901");
        weatherConfiguration.setMode(XML);
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/weather?id=524901&lang=nl&mode=xml&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testSingleIdDailyForecastQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setIds("524901");
        weatherConfiguration.setMode(XML);
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        weatherConfiguration.setPeriod("20");
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/forecast/daily?id=524901&lang=nl&cnt=20&mode=xml&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testSingleIdHourlyForecastQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setIds("524901");
        weatherConfiguration.setMode(XML);
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        weatherConfiguration.setWeatherApi(Hourly);
        weatherConfiguration.setPeriod("20");
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/forecast?id=524901&lang=nl&cnt=20&mode=xml&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testSingleIdStationQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setIds("52");
        weatherConfiguration.setMode(JSON);
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        weatherConfiguration.setWeatherApi(Station);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/station?id=52&lang=nl&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testMultiIdQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setIds("524901,703448");
        weatherConfiguration.setMode(JSON);
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/group?id=524901,703448&lang=nl&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testFindInCircleQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setLat(WeatherQueryTest.LATITUDE);
        weatherConfiguration.setLon(WeatherQueryTest.LONGITUDE);
        weatherConfiguration.setCnt(25);
        weatherConfiguration.setMode(JSON);
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/find?lat=51.98&lon=4.13&lang=nl&cnt=25&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testFindStationInCircleQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setLat(WeatherQueryTest.LATITUDE);
        weatherConfiguration.setLon(WeatherQueryTest.LONGITUDE);
        weatherConfiguration.setCnt(25);
        weatherConfiguration.setMode(JSON);
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        weatherConfiguration.setWeatherApi(Station);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/station/find?lat=51.98&lon=4.13&lang=nl&cnt=25&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testCurrentLocationQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setMode(XML);
        weatherConfiguration.setPeriod("3");
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setUnits(IMPERIAL);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/forecast/daily?lat=51.98&lon=4.13&lang=nl&cnt=3&units=imperial&mode=xml&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testCurrentLocationHourlyQuery() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setMode(XML);
        weatherConfiguration.setPeriod("3");
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setUnits(IMPERIAL);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        weatherConfiguration.setWeatherApi(Hourly);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/forecast?lat=51.98&lon=4.13&lang=nl&cnt=3&units=imperial&mode=xml&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testCurrentLocationQuery2() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setMode(XML);
        weatherConfiguration.setLocation("current");
        weatherConfiguration.setPeriod("3");
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setUnits(IMPERIAL);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/forecast/daily?lat=51.98&lon=4.13&lang=nl&cnt=3&units=imperial&mode=xml&APPID=9162755b2efa555823cfe0451d7fff38"));
    }

    @Test
    public void testGivenLocation() throws Exception {
        WeatherConfiguration weatherConfiguration = new WeatherConfiguration(new WeatherComponent());
        weatherConfiguration.setMode(XML);
        weatherConfiguration.setLocation("Scheveningen,NL");
        weatherConfiguration.setLanguage(nl);
        weatherConfiguration.setUnits(IMPERIAL);
        weatherConfiguration.setAppid(WeatherQueryTest.APPID);
        WeatherQuery weatherQuery = new WeatherQuery(weatherConfiguration);
        weatherQuery.setGeoLocationProvider(geoLocationProvider);
        String query = weatherQuery.getQuery();
        Assert.assertThat(query, Is.is("http://api.openweathermap.org/data/2.5/weather?q=Scheveningen,NL&lang=nl&units=imperial&mode=xml&APPID=9162755b2efa555823cfe0451d7fff38"));
    }
}

