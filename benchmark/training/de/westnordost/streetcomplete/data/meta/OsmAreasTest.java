package de.westnordost.streetcomplete.data.meta;


import de.westnordost.osmapi.map.data.OsmRelation;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class OsmAreasTest {
    @Test
    public void relation() {
        Assert.assertFalse(OsmAreas.isArea(new OsmRelation(0, 0, null, null)));
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "multipolygon");
        Assert.assertTrue(OsmAreas.isArea(new OsmRelation(0, 0, null, tags)));
    }

    @Test
    public void wayNoTags() {
        Assert.assertFalse(OsmAreas.isArea(createWay(false, null)));
        Assert.assertFalse(OsmAreas.isArea(createWay(true, null)));
    }

    @Test
    public void waySimple() {
        Map<String, String> tags = new HashMap<>();
        tags.put("area", "yes");
        Assert.assertFalse(OsmAreas.isArea(createWay(false, tags)));
        Assert.assertTrue(OsmAreas.isArea(createWay(true, tags)));
    }

    @Test
    public void wayRuleException() {
        Map<String, String> tags = new HashMap<>();
        tags.put("railway", "something");
        Assert.assertFalse(OsmAreas.isArea(createWay(true, tags)));
        tags.put("railway", "station");
        Assert.assertTrue(OsmAreas.isArea(createWay(true, tags)));
    }

    @Test
    public void wayRuleRegex() {
        Map<String, String> tags = new HashMap<>();
        tags.put("waterway", "duck");
        Assert.assertFalse(OsmAreas.isArea(createWay(true, tags)));
        tags.put("waterway", "dock");
        Assert.assertTrue(OsmAreas.isArea(createWay(true, tags)));
    }
}

