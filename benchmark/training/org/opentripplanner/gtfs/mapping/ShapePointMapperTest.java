package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.org.opentripplanner.model.ShapePoint;


public class ShapePointMapperTest {
    private static final AgencyAndId AGENCY_AND_ID = new AgencyAndId("A", "1");

    private static final Integer ID = 45;

    private static final double DIST_TRAVELED = 2.0;

    private static final double LAT = 60.0;

    private static final double LON = 45.0;

    private static final int SEQUENCE = 3;

    private static final ShapePoint SHAPE_POINT = new ShapePoint();

    static {
        ShapePointMapperTest.SHAPE_POINT.setId(ShapePointMapperTest.ID);
        ShapePointMapperTest.SHAPE_POINT.setDistTraveled(ShapePointMapperTest.DIST_TRAVELED);
        ShapePointMapperTest.SHAPE_POINT.setLat(ShapePointMapperTest.LAT);
        ShapePointMapperTest.SHAPE_POINT.setLon(ShapePointMapperTest.LON);
        ShapePointMapperTest.SHAPE_POINT.setSequence(ShapePointMapperTest.SEQUENCE);
        ShapePointMapperTest.SHAPE_POINT.setShapeId(ShapePointMapperTest.AGENCY_AND_ID);
    }

    private ShapePointMapper subject = new ShapePointMapper();

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(subject.map(((Collection<ShapePoint>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(ShapePointMapperTest.SHAPE_POINT)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.ShapePoint result = subject.map(ShapePointMapperTest.SHAPE_POINT);
        Assert.assertEquals(ShapePointMapperTest.DIST_TRAVELED, result.getDistTraveled(), 1.0E-4);
        Assert.assertEquals(ShapePointMapperTest.LAT, result.getLat(), 1.0E-4);
        Assert.assertEquals(ShapePointMapperTest.LON, result.getLon(), 1.0E-4);
        Assert.assertEquals(ShapePointMapperTest.SEQUENCE, result.getSequence());
        Assert.assertEquals("A_1", result.getShapeId().toString());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        org.opentripplanner.model.ShapePoint result = subject.map(new ShapePoint());
        Assert.assertFalse(result.isDistTraveledSet());
        Assert.assertEquals(0.0, result.getLat(), 1.0E-5);
        Assert.assertEquals(0.0, result.getLon(), 1.0E-5);
        Assert.assertEquals(0.0, result.getSequence(), 1.0E-5);
        Assert.assertNull(result.getShapeId());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.ShapePoint result1 = subject.map(ShapePointMapperTest.SHAPE_POINT);
        org.opentripplanner.model.ShapePoint result2 = subject.map(ShapePointMapperTest.SHAPE_POINT);
        Assert.assertTrue((result1 == result2));
    }
}

