package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Frequency;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.org.opentripplanner.model.Frequency;


public class FrequencyMapperTest {
    private static final AgencyAndId AGENCY_AND_ID = new AgencyAndId("A", "1");

    private static final Integer ID = 45;

    private static final int START_TIME = 1200;

    private static final int END_TIME = 2300;

    private static final int EXACT_TIMES = 1;

    private static final int HEADWAY_SECS = 2;

    private static final int LABEL_ONLY = 1;

    private static final Trip TRIP = new Trip();

    private static final Frequency FREQUENCY = new Frequency();

    static {
        FrequencyMapperTest.TRIP.setId(FrequencyMapperTest.AGENCY_AND_ID);
        FrequencyMapperTest.FREQUENCY.setId(FrequencyMapperTest.ID);
        FrequencyMapperTest.FREQUENCY.setStartTime(FrequencyMapperTest.START_TIME);
        FrequencyMapperTest.FREQUENCY.setEndTime(FrequencyMapperTest.END_TIME);
        FrequencyMapperTest.FREQUENCY.setExactTimes(FrequencyMapperTest.EXACT_TIMES);
        FrequencyMapperTest.FREQUENCY.setHeadwaySecs(FrequencyMapperTest.HEADWAY_SECS);
        FrequencyMapperTest.FREQUENCY.setLabelOnly(FrequencyMapperTest.LABEL_ONLY);
        FrequencyMapperTest.FREQUENCY.setTrip(FrequencyMapperTest.TRIP);
    }

    private FrequencyMapper subject = new FrequencyMapper(new TripMapper(new RouteMapper(new AgencyMapper())));

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(null, subject.map(((Collection<Frequency>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(FrequencyMapperTest.FREQUENCY)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.Frequency result = subject.map(FrequencyMapperTest.FREQUENCY);
        Assert.assertEquals(FrequencyMapperTest.START_TIME, result.getStartTime());
        Assert.assertEquals(FrequencyMapperTest.END_TIME, result.getEndTime());
        Assert.assertEquals(FrequencyMapperTest.EXACT_TIMES, result.getExactTimes());
        Assert.assertEquals(FrequencyMapperTest.HEADWAY_SECS, result.getHeadwaySecs());
        Assert.assertEquals(FrequencyMapperTest.LABEL_ONLY, result.getLabelOnly());
        Assert.assertNotNull(result.getTrip());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        org.opentripplanner.model.Frequency result = subject.map(new Frequency());
        Assert.assertEquals(0, result.getStartTime());
        Assert.assertEquals(0, result.getEndTime());
        Assert.assertEquals(0, result.getExactTimes());
        Assert.assertEquals(0, result.getHeadwaySecs());
        Assert.assertEquals(0, result.getLabelOnly());
        Assert.assertNull(result.getTrip());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.Frequency result1 = subject.map(FrequencyMapperTest.FREQUENCY);
        org.opentripplanner.model.Frequency result2 = subject.map(FrequencyMapperTest.FREQUENCY);
        Assert.assertTrue((result1 == result2));
    }
}

