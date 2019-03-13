package de.westnordost.streetcomplete.data.osm.download;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class NodeWayMapTest {
    @Test
    public void all() {
        List<List<Long>> ways = new ArrayList<>();
        List<Long> way1 = Arrays.asList(1L, 2L, 3L);
        List<Long> way2 = Arrays.asList(3L, 4L, 1L);
        List<Long> ring = Arrays.asList(5L, 1L, 6L, 5L);
        ways.add(way1);
        ways.add(way2);
        ways.add(ring);
        NodeWayMap<Long> map = new NodeWayMap(ways);
        Assert.assertTrue(map.hasNextNode());
        Assert.assertEquals(2, map.getWaysAtNode(1L).size());
        Assert.assertEquals(2, map.getWaysAtNode(3L).size());
        Assert.assertEquals(2, map.getWaysAtNode(5L).size());
        Assert.assertNull(map.getWaysAtNode(2L));
        map.removeWay(way1);
        Assert.assertEquals(1, map.getWaysAtNode(1L).size());
        Assert.assertEquals(1, map.getWaysAtNode(3L).size());
        map.removeWay(way2);
        Assert.assertNull(map.getWaysAtNode(1L));
        Assert.assertNull(map.getWaysAtNode(3L));
        Assert.assertTrue(map.hasNextNode());
        Assert.assertEquals(5L, ((long) (map.getNextNode())));
        map.removeWay(ring);
        Assert.assertFalse(map.hasNextNode());
    }
}

