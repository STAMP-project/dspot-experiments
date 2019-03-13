package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.FareAttribute;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.org.opentripplanner.model.FareRule;


public class FareRuleMapperTest {
    private static final FareRule FARE_RULE = new FareRule();

    private static final AgencyAndId AGENCY_AND_ID = new AgencyAndId("A", "1");

    private static final Integer ID = 45;

    private static final String CONTAINS_ID = "Contains Id";

    private static final String DESTINATION_ID = "Destination Id";

    private static final FareAttribute FARE_ATTRIBUTE = new FareAttribute();

    private static final String ORIGIN_ID = "Origin Id";

    private static final Route ROUTE = new Route();

    static {
        FareRuleMapperTest.FARE_ATTRIBUTE.setId(FareRuleMapperTest.AGENCY_AND_ID);
        FareRuleMapperTest.ROUTE.setId(FareRuleMapperTest.AGENCY_AND_ID);
        FareRuleMapperTest.FARE_RULE.setId(FareRuleMapperTest.ID);
        FareRuleMapperTest.FARE_RULE.setContainsId(FareRuleMapperTest.CONTAINS_ID);
        FareRuleMapperTest.FARE_RULE.setDestinationId(FareRuleMapperTest.DESTINATION_ID);
        FareRuleMapperTest.FARE_RULE.setFare(FareRuleMapperTest.FARE_ATTRIBUTE);
        FareRuleMapperTest.FARE_RULE.setOriginId(FareRuleMapperTest.ORIGIN_ID);
        FareRuleMapperTest.FARE_RULE.setRoute(FareRuleMapperTest.ROUTE);
    }

    private FareRuleMapper subject = new FareRuleMapper(new RouteMapper(new AgencyMapper()), new FareAttributeMapper());

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(subject.map(((Collection<FareRule>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(FareRuleMapperTest.FARE_RULE)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.FareRule result = subject.map(FareRuleMapperTest.FARE_RULE);
        Assert.assertEquals(FareRuleMapperTest.CONTAINS_ID, result.getContainsId());
        Assert.assertEquals(FareRuleMapperTest.DESTINATION_ID, result.getDestinationId());
        Assert.assertEquals(FareRuleMapperTest.ORIGIN_ID, result.getOriginId());
        Assert.assertNotNull(result.getFare());
        Assert.assertNotNull(result.getRoute());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        org.opentripplanner.model.FareRule result = subject.map(new FareRule());
        Assert.assertNull(result.getContainsId());
        Assert.assertNull(result.getDestinationId());
        Assert.assertNull(result.getOriginId());
        Assert.assertNull(result.getFare());
        Assert.assertNull(result.getRoute());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.FareRule result1 = subject.map(FareRuleMapperTest.FARE_RULE);
        org.opentripplanner.model.FareRule result2 = subject.map(FareRuleMapperTest.FARE_RULE);
        Assert.assertTrue((result1 == result2));
    }
}

