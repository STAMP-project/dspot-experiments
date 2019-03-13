package org.opentripplanner.common.model;


import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;


public class GenericLocationTest {
    @Test
    public void testEmpty() {
        GenericLocation loc = new GenericLocation();
        Assert.assertEquals("", loc.name);
        Assert.assertEquals("", loc.place);
        NamedPlace np = loc.getNamedPlace();
        Assert.assertEquals("", np.name);
        Assert.assertEquals("", np.place);
        Assert.assertNull(loc.lat);
        Assert.assertNull(loc.lng);
        Assert.assertNull(loc.getCoordinate());
        Assert.assertFalse(loc.hasName());
        Assert.assertFalse(loc.hasPlace());
    }

    @Test
    public void testFromNamePlace() {
        GenericLocation loc = new GenericLocation("name", "12345");
        Assert.assertEquals("name", loc.name);
        Assert.assertEquals("12345", loc.place);
        NamedPlace np = loc.getNamedPlace();
        Assert.assertEquals("name", np.name);
        Assert.assertEquals("12345", np.place);
        Assert.assertFalse(loc.hasCoordinate());
        Assert.assertFalse(loc.hasHeading());
        Assert.assertTrue(loc.hasName());
        Assert.assertTrue(loc.hasPlace());
        Assert.assertNull(loc.lat);
        Assert.assertNull(loc.lng);
        Assert.assertNull(loc.getCoordinate());
    }

    @Test
    public void testFromNamePlaceWithCoord() {
        GenericLocation loc = new GenericLocation("name", "-1.0,2.5");
        Assert.assertEquals("name", loc.name);
        Assert.assertEquals("-1.0,2.5", loc.place);
        NamedPlace np = loc.getNamedPlace();
        Assert.assertEquals("name", np.name);
        Assert.assertEquals("-1.0,2.5", np.place);
        Assert.assertTrue(loc.hasName());
        Assert.assertTrue(loc.hasPlace());
        Assert.assertTrue(loc.hasCoordinate());
        Assert.assertFalse(loc.hasHeading());
        Assert.assertEquals(new Double((-1.0)), loc.lat);
        Assert.assertEquals(new Double(2.5), loc.lng);
        Assert.assertEquals(new Coordinate(2.5, (-1.0)), loc.getCoordinate());
        loc = new GenericLocation("name", "1.0,-2.5");
        Assert.assertEquals(new Double(1.0), loc.lat);
        Assert.assertEquals(new Double((-2.5)), loc.lng);
        Assert.assertEquals(new Coordinate((-2.5), 1.0), loc.getCoordinate());
    }

    @Test
    public void testFromOldStyleString() {
        GenericLocation loc = GenericLocation.fromOldStyleString("name::12345");
        Assert.assertEquals("name", loc.name);
        Assert.assertEquals("12345", loc.place);
        NamedPlace np = loc.getNamedPlace();
        Assert.assertEquals("name", np.name);
        Assert.assertEquals("12345", np.place);
        Assert.assertTrue(loc.hasName());
        Assert.assertTrue(loc.hasPlace());
        Assert.assertFalse(loc.hasCoordinate());
        Assert.assertFalse(loc.hasHeading());
        Assert.assertNull(loc.lat);
        Assert.assertNull(loc.lng);
        Assert.assertNull(loc.getCoordinate());
    }

    @Test
    public void testFromStringWithEdgeAndHeading() {
        String s = "40.75542978896869,-73.97618338000376 heading=29.028895183287617 edgeId=2767";
        GenericLocation loc = GenericLocation.fromOldStyleString(s);
        Assert.assertEquals(29.028895183287617, loc.heading, 1.0E-5);
        Assert.assertEquals(2767, loc.edgeId.intValue());
        Assert.assertEquals(40.75542978896869, loc.lat, 1.0E-5);
        Assert.assertEquals((-73.97618338000376), loc.lng, 1.0E-5);
    }

    @Test
    public void testFromOldStyleStringWithCoord() {
        GenericLocation loc = GenericLocation.fromOldStyleString("name::1.0,2.5");
        Assert.assertEquals("name", loc.name);
        Assert.assertEquals("1.0,2.5", loc.place);
        NamedPlace np = loc.getNamedPlace();
        Assert.assertEquals("name", np.name);
        Assert.assertEquals("1.0,2.5", np.place);
        Assert.assertTrue(loc.hasName());
        Assert.assertTrue(loc.hasPlace());
        Assert.assertTrue(loc.hasCoordinate());
        Assert.assertFalse(loc.hasHeading());
        Assert.assertEquals(new Double(1.0), loc.lat);
        Assert.assertEquals(new Double(2.5), loc.lng);
        Assert.assertEquals(new Coordinate(2.5, 1.0), loc.getCoordinate());
    }

    @Test
    public void testToString() {
        String input = "name::1.0,2.5";
        GenericLocation loc = GenericLocation.fromOldStyleString(input);
        Assert.assertEquals(input, loc.toString());
        Assert.assertTrue(loc.hasCoordinate());
        Assert.assertFalse(loc.hasHeading());
        input = "name::12345";
        loc = GenericLocation.fromOldStyleString(input);
        Assert.assertEquals(input, loc.toString());
        Assert.assertFalse(loc.hasCoordinate());
        Assert.assertFalse(loc.hasHeading());
        input = "name";
        loc = GenericLocation.fromOldStyleString(input);
        Assert.assertEquals(input, loc.toString());
        Assert.assertFalse(loc.hasCoordinate());
        Assert.assertFalse(loc.hasHeading());
    }

    @Test
    public void testFromLatLng() {
        GenericLocation loc = new GenericLocation(1.0, 2.0);
        Coordinate expectedCoord = new Coordinate(2.0, 1.0);
        Assert.assertEquals(expectedCoord, loc.getCoordinate());
        Assert.assertEquals("1.0,2.0", loc.toString());
        Assert.assertTrue(loc.hasCoordinate());
        Assert.assertFalse(loc.hasHeading());
        Assert.assertFalse(loc.hasName());
        Assert.assertFalse(loc.hasPlace());
    }

    @Test
    public void testFromLatLngHeading() {
        GenericLocation loc = new GenericLocation(1.0, 2.0, 137.2);
        Coordinate expectedCoord = new Coordinate(2.0, 1.0);
        Assert.assertEquals(expectedCoord, loc.getCoordinate());
        Assert.assertEquals(137.2, loc.heading.doubleValue(), 0.0);
        Assert.assertEquals("1.0,2.0", loc.toString());
        Assert.assertTrue(loc.hasCoordinate());
        Assert.assertTrue(loc.hasHeading());
        Assert.assertFalse(loc.hasName());
        Assert.assertFalse(loc.hasPlace());
    }

    @Test
    public void testFromCoord() {
        Coordinate expectedCoord = new Coordinate(2.0, 1.0);
        GenericLocation loc = new GenericLocation(expectedCoord);
        Assert.assertEquals(expectedCoord, loc.getCoordinate());
        Assert.assertEquals("1.0,2.0", loc.toString());
        Assert.assertTrue(loc.hasCoordinate());
        Assert.assertFalse(loc.hasHeading());
    }

    @Test
    public void testClone() {
        Coordinate expectedCoord = new Coordinate(2.0, 1.0);
        GenericLocation loc = new GenericLocation(expectedCoord);
        loc.heading = 137.2;
        GenericLocation cloned = loc.clone();
        Assert.assertEquals(expectedCoord, cloned.getCoordinate());
        Assert.assertEquals(loc.heading, cloned.heading);
        Assert.assertEquals(loc.getNamedPlace().name, cloned.getNamedPlace().name);
        Assert.assertEquals(loc.getNamedPlace().place, cloned.getNamedPlace().place);
    }

    @Test
    public void testFromOldStyleStringIncomplete() {
        String input = "0::";
        GenericLocation loc = GenericLocation.fromOldStyleString(input);
        Assert.assertEquals("0", loc.name);
        Assert.assertEquals("", loc.place);
        input = "::1";
        loc = GenericLocation.fromOldStyleString(input);
        Assert.assertEquals("", loc.name);
        Assert.assertEquals("1", loc.place);
        input = "::";
        loc = GenericLocation.fromOldStyleString(input);
        Assert.assertEquals("", loc.name);
        Assert.assertEquals("", loc.place);
    }
}

