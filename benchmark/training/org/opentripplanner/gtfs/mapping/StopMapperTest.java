package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.org.opentripplanner.model.Stop;


public class StopMapperTest {
    private static final AgencyAndId AGENCY_AND_ID = new AgencyAndId("A", "1");

    private static final String CODE = "Code";

    private static final String DESC = "Desc";

    private static final String DIRECTION = "Direction";

    private static final double LAT = 60.0;

    private static final double LON = 45.0;

    private static final int LOCATION_TYPE = 1;

    private static final String NAME = "Name";

    private static final String PARENT = "Parent";

    private static final String PLATFORM_CODE = "Platform Code";

    private static final String TIMEZONE = "GMT";

    private static final String URL = "www.url.me";

    private static final int VEHICLE_TYPE = 5;

    private static final int WHEELCHAIR_BOARDING = 1;

    private static final String ZONE_ID = "Zone Id";

    private static final Stop STOP = new Stop();

    static {
        StopMapperTest.STOP.setId(StopMapperTest.AGENCY_AND_ID);
        StopMapperTest.STOP.setCode(StopMapperTest.CODE);
        StopMapperTest.STOP.setDesc(StopMapperTest.DESC);
        StopMapperTest.STOP.setDirection(StopMapperTest.DIRECTION);
        StopMapperTest.STOP.setLat(StopMapperTest.LAT);
        StopMapperTest.STOP.setLon(StopMapperTest.LON);
        StopMapperTest.STOP.setLocationType(StopMapperTest.LOCATION_TYPE);
        StopMapperTest.STOP.setName(StopMapperTest.NAME);
        StopMapperTest.STOP.setParentStation(StopMapperTest.PARENT);
        StopMapperTest.STOP.setPlatformCode(StopMapperTest.PLATFORM_CODE);
        StopMapperTest.STOP.setTimezone(StopMapperTest.TIMEZONE);
        StopMapperTest.STOP.setUrl(StopMapperTest.URL);
        StopMapperTest.STOP.setVehicleType(StopMapperTest.VEHICLE_TYPE);
        StopMapperTest.STOP.setWheelchairBoarding(StopMapperTest.WHEELCHAIR_BOARDING);
        StopMapperTest.STOP.setZoneId(StopMapperTest.ZONE_ID);
    }

    private StopMapper subject = new StopMapper();

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(null, subject.map(((Collection<Stop>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(StopMapperTest.STOP)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.Stop result = subject.map(StopMapperTest.STOP);
        Assert.assertEquals("A_1", result.getId().toString());
        Assert.assertEquals(StopMapperTest.CODE, result.getCode());
        Assert.assertEquals(StopMapperTest.DESC, result.getDesc());
        Assert.assertEquals(StopMapperTest.DIRECTION, result.getDirection());
        Assert.assertEquals(StopMapperTest.LAT, result.getLat(), 1.0E-4);
        Assert.assertEquals(StopMapperTest.LON, result.getLon(), 1.0E-4);
        Assert.assertEquals(StopMapperTest.LOCATION_TYPE, result.getLocationType());
        Assert.assertEquals(StopMapperTest.NAME, result.getName());
        Assert.assertEquals(StopMapperTest.PARENT, result.getParentStation());
        Assert.assertEquals(StopMapperTest.PLATFORM_CODE, result.getPlatformCode());
        Assert.assertEquals(StopMapperTest.TIMEZONE, result.getTimezone());
        Assert.assertEquals(StopMapperTest.URL, result.getUrl());
        Assert.assertEquals(StopMapperTest.VEHICLE_TYPE, result.getVehicleType());
        Assert.assertEquals(StopMapperTest.WHEELCHAIR_BOARDING, result.getWheelchairBoarding());
        Assert.assertEquals(StopMapperTest.ZONE_ID, result.getZoneId());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        Stop input = new Stop();
        input.setId(StopMapperTest.AGENCY_AND_ID);
        org.opentripplanner.model.Stop result = subject.map(input);
        Assert.assertNotNull(result.getId());
        Assert.assertNull(result.getCode());
        Assert.assertNull(result.getDesc());
        Assert.assertNull(result.getDirection());
        Assert.assertEquals(0.0, result.getLat(), 1.0E-4);
        Assert.assertEquals(0.0, result.getLon(), 1.0E-4);
        Assert.assertEquals(0, result.getLocationType());
        Assert.assertNull(result.getName());
        Assert.assertNull(result.getParentStation());
        Assert.assertNull(result.getPlatformCode());
        Assert.assertNull(result.getTimezone());
        Assert.assertNull(result.getUrl());
        Assert.assertFalse(result.isVehicleTypeSet());
        Assert.assertEquals(0, result.getWheelchairBoarding());
        Assert.assertNull(result.getZoneId());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.Stop result1 = subject.map(StopMapperTest.STOP);
        org.opentripplanner.model.Stop result2 = subject.map(StopMapperTest.STOP);
        Assert.assertTrue((result1 == result2));
    }
}

