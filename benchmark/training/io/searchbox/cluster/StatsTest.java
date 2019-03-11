package io.searchbox.cluster;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class StatsTest {
    @Test
    public void testUriGeneration() {
        Stats action = new Stats.Builder().build();
        Assert.assertEquals("/_cluster/stats/nodes/_all", action.getURI(UNKNOWN));
    }

    @Test
    public void testUriGenerationWithSpecificNodes() {
        Stats action = new Stats.Builder().addNode("test1").addNode("test2").build();
        Assert.assertEquals("/_cluster/stats/nodes/test1,test2", action.getURI(UNKNOWN));
    }
}

