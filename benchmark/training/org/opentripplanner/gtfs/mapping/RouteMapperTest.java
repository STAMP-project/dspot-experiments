package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.org.opentripplanner.model.Route;


public class RouteMapperTest {
    private static final AgencyAndId AGENCY_AND_ID = new AgencyAndId("A", "1");

    private static final String SHORT_NAME = "Short Name";

    private static final String LONG_NAME = "Long Name";

    private static final String DESC = "Desc";

    private static final int TYPE = 2;

    private static final String URL = "www.url.me";

    private static final String COLOR = "green";

    private static final String TEXT_COLOR = "red";

    private static final int BIKES_ALLOWED = 2;

    private static final int SORT_ORDER = 1;

    private static final String BRANDING_URL = "www.url.me/brand";

    private static final int ROUTE_BIKES_ALLOWED = 2;

    private static final Agency AGENCY = new Agency();

    private static final Route ROUTE = new Route();

    static {
        RouteMapperTest.AGENCY.setId("A");
        RouteMapperTest.ROUTE.setId(RouteMapperTest.AGENCY_AND_ID);
        RouteMapperTest.ROUTE.setAgency(RouteMapperTest.AGENCY);
        RouteMapperTest.ROUTE.setShortName(RouteMapperTest.SHORT_NAME);
        RouteMapperTest.ROUTE.setLongName(RouteMapperTest.LONG_NAME);
        RouteMapperTest.ROUTE.setDesc(RouteMapperTest.DESC);
        RouteMapperTest.ROUTE.setType(RouteMapperTest.TYPE);
        RouteMapperTest.ROUTE.setUrl(RouteMapperTest.URL);
        RouteMapperTest.ROUTE.setColor(RouteMapperTest.COLOR);
        RouteMapperTest.ROUTE.setTextColor(RouteMapperTest.TEXT_COLOR);
        RouteMapperTest.ROUTE.setBikesAllowed(RouteMapperTest.BIKES_ALLOWED);
        RouteMapperTest.ROUTE.setSortOrder(RouteMapperTest.SORT_ORDER);
        RouteMapperTest.ROUTE.setBrandingUrl(RouteMapperTest.BRANDING_URL);
        RouteMapperTest.ROUTE.setRouteBikesAllowed(RouteMapperTest.ROUTE_BIKES_ALLOWED);
    }

    private RouteMapper subject = new RouteMapper(new AgencyMapper());

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(null, subject.map(((Collection<Route>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(RouteMapperTest.ROUTE)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.Route result = subject.map(RouteMapperTest.ROUTE);
        Assert.assertEquals("A_1", result.getId().toString());
        Assert.assertNotNull(result.getAgency());
        Assert.assertEquals(RouteMapperTest.SHORT_NAME, result.getShortName());
        Assert.assertEquals(RouteMapperTest.LONG_NAME, result.getLongName());
        Assert.assertEquals(RouteMapperTest.DESC, result.getDesc());
        Assert.assertEquals(RouteMapperTest.TYPE, result.getType());
        Assert.assertEquals(RouteMapperTest.URL, result.getUrl());
        Assert.assertEquals(RouteMapperTest.COLOR, result.getColor());
        Assert.assertEquals(RouteMapperTest.TEXT_COLOR, result.getTextColor());
        Assert.assertEquals(RouteMapperTest.BIKES_ALLOWED, result.getBikesAllowed());
        Assert.assertEquals(RouteMapperTest.SORT_ORDER, result.getSortOrder());
        Assert.assertEquals(RouteMapperTest.BRANDING_URL, result.getBrandingUrl());
        Assert.assertEquals(RouteMapperTest.BIKES_ALLOWED, result.getRouteBikesAllowed());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        Route input = new Route();
        input.setId(RouteMapperTest.AGENCY_AND_ID);
        org.opentripplanner.model.Route result = subject.map(input);
        Assert.assertNotNull(result.getId());
        Assert.assertNull(result.getAgency());
        Assert.assertNull(result.getShortName());
        Assert.assertNull(result.getLongName());
        Assert.assertNull(result.getDesc());
        Assert.assertEquals(0, result.getType());
        Assert.assertNull(result.getUrl());
        Assert.assertNull(result.getColor());
        Assert.assertNull(result.getTextColor());
        Assert.assertEquals(0, result.getBikesAllowed());
        Assert.assertFalse(result.isSortOrderSet());
        Assert.assertNull(result.getBrandingUrl());
        Assert.assertEquals(0, result.getRouteBikesAllowed());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.Route result1 = subject.map(RouteMapperTest.ROUTE);
        org.opentripplanner.model.Route result2 = subject.map(RouteMapperTest.ROUTE);
        Assert.assertTrue((result1 == result2));
    }
}

