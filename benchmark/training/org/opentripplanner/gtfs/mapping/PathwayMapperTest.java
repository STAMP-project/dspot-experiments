package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Pathway;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.org.opentripplanner.model.Pathway;


public class PathwayMapperTest {
    private static final AgencyAndId AGENCY_AND_ID = new AgencyAndId("A", "1");

    private static final int PATHWAY_TYPE = 2;

    private static final int TRAVERSAL_TIME = 3000;

    private static final int WHEELCHAIR_TRAVERSAL_TIME = 3400;

    private static final Pathway PATHWAY = new Pathway();

    private static final Stop FROM_STOP = new Stop();

    private static final Stop TO_STOP = new Stop();

    static {
        PathwayMapperTest.FROM_STOP.setId(PathwayMapperTest.AGENCY_AND_ID);
        PathwayMapperTest.TO_STOP.setId(PathwayMapperTest.AGENCY_AND_ID);
        PathwayMapperTest.PATHWAY.setId(PathwayMapperTest.AGENCY_AND_ID);
        PathwayMapperTest.PATHWAY.setFromStop(PathwayMapperTest.FROM_STOP);
        PathwayMapperTest.PATHWAY.setToStop(PathwayMapperTest.TO_STOP);
        PathwayMapperTest.PATHWAY.setPathwayType(PathwayMapperTest.PATHWAY_TYPE);
        PathwayMapperTest.PATHWAY.setTraversalTime(PathwayMapperTest.TRAVERSAL_TIME);
        PathwayMapperTest.PATHWAY.setWheelchairTraversalTime(PathwayMapperTest.WHEELCHAIR_TRAVERSAL_TIME);
    }

    private PathwayMapper subject = new PathwayMapper(new StopMapper());

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(subject.map(((Collection<Pathway>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(PathwayMapperTest.PATHWAY)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.Pathway result = subject.map(PathwayMapperTest.PATHWAY);
        Assert.assertEquals("A_1", result.getId().toString());
        Assert.assertNotNull(result.getFromStop());
        Assert.assertNotNull(result.getToStop());
        Assert.assertEquals(PathwayMapperTest.PATHWAY_TYPE, result.getPathwayType());
        Assert.assertEquals(PathwayMapperTest.TRAVERSAL_TIME, result.getTraversalTime());
        Assert.assertEquals(PathwayMapperTest.WHEELCHAIR_TRAVERSAL_TIME, result.getWheelchairTraversalTime());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        Pathway input = new Pathway();
        input.setId(PathwayMapperTest.AGENCY_AND_ID);
        org.opentripplanner.model.Pathway result = subject.map(input);
        Assert.assertNotNull(result.getId());
        Assert.assertNull(result.getFromStop());
        Assert.assertNull(result.getToStop());
        Assert.assertEquals(0, result.getPathwayType());
        Assert.assertEquals(0, result.getTraversalTime());
        Assert.assertFalse(result.isWheelchairTraversalTimeSet());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.Pathway result1 = subject.map(PathwayMapperTest.PATHWAY);
        org.opentripplanner.model.Pathway result2 = subject.map(PathwayMapperTest.PATHWAY);
        Assert.assertTrue((result1 == result2));
    }
}

