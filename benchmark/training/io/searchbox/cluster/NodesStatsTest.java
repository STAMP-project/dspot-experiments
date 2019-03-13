package io.searchbox.cluster;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
public class NodesStatsTest {
    @Test
    public void testUriGeneration() throws Exception {
        NodesStats action = new NodesStats.Builder().build();
        Assert.assertEquals("/_nodes/_all/stats", action.getURI(UNKNOWN));
    }

    @Test
    public void testUriGenerationWithSingleNode() throws Exception {
        NodesStats action = new NodesStats.Builder().addNode("james").withOs().withJvm().build();
        Assert.assertEquals("/_nodes/james/stats/os,jvm", action.getURI(UNKNOWN));
    }
}

