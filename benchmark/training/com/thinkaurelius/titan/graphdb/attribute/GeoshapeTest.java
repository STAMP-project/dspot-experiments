package com.thinkaurelius.titan.graphdb.attribute;


import Geoshape.GeoshapeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class GeoshapeTest {
    @Test
    public void testDistance() {
        Geoshape p1 = Geoshape.point(37.759, (-122.536));
        Geoshape p2 = Geoshape.point(35.714, (-105.938));
        double distance = 1496;
        Assert.assertEquals(distance, p1.getPoint().distance(p2.getPoint()), 5.0);
        p1 = Geoshape.point(0.0, 0.0);
        p2 = Geoshape.point(10.0, 10.0);
        // System.out.println(p1.getPoint().distance(p2.getPoint()));
    }

    @Test
    public void testIntersection() {
        for (int i = 0; i < 50; i++) {
            Geoshape point = Geoshape.point(i, i);
            Geoshape circle = Geoshape.circle(0.0, 0.0, ((point.getPoint().distance(Geoshape.point(0, 0).getPoint())) + 10));
            Assert.assertTrue(circle.intersect(point));
            Assert.assertTrue(point.intersect(circle));
            Assert.assertTrue(circle.intersect(circle));
        }
    }

    @Test
    public void testEquality() {
        Geoshape c = Geoshape.circle(10.0, 12.5, 100);
        Geoshape b = Geoshape.box(20.0, 22.5, 40.5, 60.5);
        Assert.assertEquals(Geoshape.circle(10.0, 12.5, 100), c);
        Assert.assertEquals(Geoshape.box(20.0, 22.5, 40.5, 60.5), b);
        Assert.assertEquals(Geoshape.circle(10.0, 12.5, 100).hashCode(), c.hashCode());
        Assert.assertEquals(Geoshape.box(20.0, 22.5, 40.5, 60.5).hashCode(), b.hashCode());
        Assert.assertNotSame(c.hashCode(), b.hashCode());
        Assert.assertNotSame(c, b);
        System.out.println(c);
        System.out.println(b);
    }

    @Test
    public void testParseCollection() {
        Geoshape.GeoshapeSerializer serializer = new Geoshape.GeoshapeSerializer();
        Assert.assertEquals(Geoshape.point(10, 20), serializer.convert(Arrays.asList(10, 20)));
        Assert.assertEquals(Geoshape.circle(10, 20, 30), serializer.convert(Arrays.asList(10, 20, 30)));
        Assert.assertEquals(Geoshape.box(10, 20, 30, 40), serializer.convert(Arrays.asList(10, 20, 30, 40)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailParseCollection() {
        Geoshape.GeoshapeSerializer serializer = new Geoshape.GeoshapeSerializer();
        serializer.convert(Arrays.asList(10, "Foo"));
    }

    @Test
    public void testGeoJsonPoint() throws IOException {
        Geoshape.GeoshapeSerializer s = new Geoshape.GeoshapeSerializer();
        Map json = new ObjectMapper().readValue(("{\n" + (((((((("  \"type\": \"Feature\",\n" + "  \"geometry\": {\n") + "    \"type\": \"Point\",\n") + "    \"coordinates\": [20.5, 10.5]\n") + "  },\n") + "  \"properties\": {\n") + "    \"name\": \"Dinagat Islands\"\n") + "  }\n") + "}")), HashMap.class);
        Assert.assertEquals(Geoshape.point(10.5, 20.5), s.convert(json));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeoJsonPointUnparseable() throws IOException {
        Geoshape.GeoshapeSerializer s = new Geoshape.GeoshapeSerializer();
        Map json = new ObjectMapper().readValue(("{\n" + (((((((("  \"type\": \"Feature\",\n" + "  \"geometry\": {\n") + "    \"type\": \"Point\",\n") + "    \"coordinates\": [20.5, \"10.5\"]\n") + "  },\n") + "  \"properties\": {\n") + "    \"name\": \"Dinagat Islands\"\n") + "  }\n") + "}")), HashMap.class);
        s.convert(json);
    }

    @Test
    public void testGeoJsonCircle() throws IOException {
        Geoshape.GeoshapeSerializer s = new Geoshape.GeoshapeSerializer();
        Map json = new ObjectMapper().readValue(("{\n" + ((((((((("  \"type\": \"Feature\",\n" + "  \"geometry\": {\n") + "    \"type\": \"Circle\",\n") + "    \"radius\": 30.5, ") + "    \"coordinates\": [20.5, 10.5]\n") + "  },\n") + "  \"properties\": {\n") + "    \"name\": \"Dinagat Islands\"\n") + "  }\n") + "}")), HashMap.class);
        Assert.assertEquals(Geoshape.circle(10.5, 20.5, 30.5), s.convert(json));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeoJsonCircleMissingRadius() throws IOException {
        Geoshape.GeoshapeSerializer s = new Geoshape.GeoshapeSerializer();
        Map json = new ObjectMapper().readValue(("{\n" + (((((((("  \"type\": \"Feature\",\n" + "  \"geometry\": {\n") + "    \"type\": \"Circle\",\n") + "    \"coordinates\": [20.5, 10.5]\n") + "  },\n") + "  \"properties\": {\n") + "    \"name\": \"Dinagat Islands\"\n") + "  }\n") + "}")), HashMap.class);
        s.convert(json);
    }

    @Test
    public void testGeoJsonPolygon() throws IOException {
        Geoshape.GeoshapeSerializer s = new Geoshape.GeoshapeSerializer();
        Map json = new ObjectMapper().readValue(("{\n" + (((((((("  \"type\": \"Feature\",\n" + "  \"geometry\": {\n") + "    \"type\": \"Polygon\",\n") + "    \"coordinates\": [[20.5, 10.5],[22.5, 10.5],[22.5, 12.5],[20.5, 12.5]]\n") + "  },\n") + "  \"properties\": {\n") + "    \"name\": \"Dinagat Islands\"\n") + "  }\n") + "}")), HashMap.class);
        Assert.assertEquals(Geoshape.box(10.5, 20.5, 12.5, 22.5), s.convert(json));
        // Try the reverse order points
        json = new ObjectMapper().readValue(("{\n" + (((((((("  \"type\": \"Feature\",\n" + "  \"geometry\": {\n") + "    \"type\": \"Polygon\",\n") + "    \"coordinates\": [[20.5, 12.5],[22.5, 12.5],[22.5, 10.5],[20.5, 10.5]]\n") + "  },\n") + "  \"properties\": {\n") + "    \"name\": \"Dinagat Islands\"\n") + "  }\n") + "}")), HashMap.class);
        Assert.assertEquals(Geoshape.box(10.5, 20.5, 12.5, 22.5), s.convert(json));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeoJsonPolygonNotBox1() throws IOException {
        Geoshape.GeoshapeSerializer s = new Geoshape.GeoshapeSerializer();
        Map json = new ObjectMapper().readValue(("{\n" + (((((((("  \"type\": \"Feature\",\n" + "  \"geometry\": {\n") + "    \"type\": \"Polygon\",\n") + "    \"coordinates\": [[20.5, 12.5],[22.5, 12.5],[22.5, 10.5],[20.5, 10.6]]\n") + "  },\n") + "  \"properties\": {\n") + "    \"name\": \"Dinagat Islands\"\n") + "  }\n") + "}")), HashMap.class);
        s.convert(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeoJsonPolygonNotBox2() throws IOException {
        Geoshape.GeoshapeSerializer s = new Geoshape.GeoshapeSerializer();
        Map json = new ObjectMapper().readValue(("{\n" + (((((((("  \"type\": \"Feature\",\n" + "  \"geometry\": {\n") + "    \"type\": \"Polygon\",\n") + "    \"coordinates\": [[20.5, 10.5],[22.5, 10.5],[22.5, 12.5]]\n") + "  },\n") + "  \"properties\": {\n") + "    \"name\": \"Dinagat Islands\"\n") + "  }\n") + "}")), HashMap.class);
        s.convert(json);
    }

    @Test
    public void testGeoJsonGeometry() throws IOException {
        Geoshape.GeoshapeSerializer s = new Geoshape.GeoshapeSerializer();
        Map json = new ObjectMapper().readValue(("{\n" + (("    \"type\": \"Point\",\n" + "    \"coordinates\": [20.5, 10.5]\n") + "}")), HashMap.class);
        Assert.assertEquals(Geoshape.point(10.5, 20.5), s.convert(json));
    }

    @Test
    public void testGeoJsonSerialization() throws IOException {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new Geoshape.GeoshapeGsonSerializer());
        final ObjectMapper om = new ObjectMapper();
        om.registerModule(module);
        Assert.assertEquals("{\"type\":\"Point\",\"coordinates\":[20.5,10.5]}", om.writeValueAsString(Geoshape.point(10.5, 20.5)));
        Assert.assertEquals("{\"type\":\"Polygon\",\"coordinates\":[[20.5,10.5],[22.5,10.5],[22.5,12.5],[20.5,12.5]]}", om.writeValueAsString(Geoshape.box(10.5, 20.5, 12.5, 22.5)));
        Assert.assertEquals("{\"type\":\"Circle\",\"radius\":30.5,\"coordinates\":[20.5,10.5]}", om.writeValueAsString(Geoshape.circle(10.5, 20.5, 30.5)));
    }
}

