package org.opentripplanner.graph_builder.module.osm;


import junit.framework.TestCase;
import org.junit.Test;
import org.opentripplanner.openstreetmap.model.OSMWithTags;


/**
 * Test the WayPropertySet
 *
 * @author mattwigway
 */
public class TestWayPropertySet extends TestCase {
    /**
     * Test that car speeds are calculated accurately
     */
    @Test
    public void testCarSpeeds() {
        WayPropertySet wps = new WayPropertySet();
        DefaultWayPropertySetSource source = new DefaultWayPropertySetSource();
        source.populateProperties(wps);
        OSMWithTags way;
        float epsilon = 0.01F;
        way = new OSMWithTags();
        way.addTag("maxspeed", "60");
        TestCase.assertTrue(within(kmhAsMs(60), wps.getCarSpeedForWay(way, false), epsilon));
        TestCase.assertTrue(within(kmhAsMs(60), wps.getCarSpeedForWay(way, true), epsilon));
        way = new OSMWithTags();
        way.addTag("maxspeed:forward", "80");
        way.addTag("maxspeed:reverse", "20");
        way.addTag("maxspeed", "40");
        TestCase.assertTrue(within(kmhAsMs(80), wps.getCarSpeedForWay(way, false), epsilon));
        TestCase.assertTrue(within(kmhAsMs(20), wps.getCarSpeedForWay(way, true), epsilon));
        way = new OSMWithTags();
        way.addTag("maxspeed", "40");
        way.addTag("maxspeed:lanes", "60|80|40");
        TestCase.assertTrue(within(kmhAsMs(80), wps.getCarSpeedForWay(way, false), epsilon));
        TestCase.assertTrue(within(kmhAsMs(80), wps.getCarSpeedForWay(way, true), epsilon));
        way = new OSMWithTags();
        way.addTag("maxspeed", "20");
        way.addTag("maxspeed:motorcar", "80");
        TestCase.assertTrue(within(kmhAsMs(80), wps.getCarSpeedForWay(way, false), epsilon));
        TestCase.assertTrue(within(kmhAsMs(80), wps.getCarSpeedForWay(way, true), epsilon));
        // test with english units
        way = new OSMWithTags();
        way.addTag("maxspeed", "35 mph");
        TestCase.assertTrue(within(kmhAsMs((35 * 1.609F)), wps.getCarSpeedForWay(way, false), epsilon));
        TestCase.assertTrue(within(kmhAsMs((35 * 1.609F)), wps.getCarSpeedForWay(way, true), epsilon));
        // test with no maxspeed tags
        wps = new WayPropertySet();
        wps.addSpeedPicker(getSpeedPicker("highway=motorway", kmhAsMs(100)));
        wps.addSpeedPicker(getSpeedPicker("highway=*", kmhAsMs(35)));
        wps.addSpeedPicker(getSpeedPicker("surface=gravel", kmhAsMs(10)));
        wps.defaultSpeed = kmhAsMs(25);
        way = new OSMWithTags();
        // test default speeds
        TestCase.assertTrue(within(kmhAsMs(25), wps.getCarSpeedForWay(way, false), epsilon));
        TestCase.assertTrue(within(kmhAsMs(25), wps.getCarSpeedForWay(way, true), epsilon));
        way.addTag("highway", "tertiary");
        TestCase.assertTrue(within(kmhAsMs(35), wps.getCarSpeedForWay(way, false), epsilon));
        TestCase.assertTrue(within(kmhAsMs(35), wps.getCarSpeedForWay(way, true), epsilon));
        way = new OSMWithTags();
        way.addTag("surface", "gravel");
        TestCase.assertTrue(within(kmhAsMs(10), wps.getCarSpeedForWay(way, false), epsilon));
        TestCase.assertTrue(within(kmhAsMs(10), wps.getCarSpeedForWay(way, true), epsilon));
        way = new OSMWithTags();
        way.addTag("highway", "motorway");
        TestCase.assertTrue(within(kmhAsMs(100), wps.getCarSpeedForWay(way, false), epsilon));
        TestCase.assertTrue(within(kmhAsMs(100), wps.getCarSpeedForWay(way, true), epsilon));
        // make sure that 0-speed ways can't exist
        way = new OSMWithTags();
        way.addTag("maxspeed", "0");
        TestCase.assertTrue(within(kmhAsMs(25), wps.getCarSpeedForWay(way, false), epsilon));
        TestCase.assertTrue(within(kmhAsMs(25), wps.getCarSpeedForWay(way, true), epsilon));
    }
}

