package io.searchbox.cluster;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
public class NodesHotThreadsTest {
    @Test
    public void testUriGenerationWithAllNodes() {
        NodesHotThreads action = new NodesHotThreads.Builder().build();
        Assert.assertEquals("/_nodes/_all/hot_threads", action.getURI(UNKNOWN));
    }

    @Test
    public void testUriGenerationWithSingleNode() {
        NodesHotThreads action = new NodesHotThreads.Builder().addNode("Pony").build();
        Assert.assertEquals("/_nodes/Pony/hot_threads", action.getURI(UNKNOWN));
    }

    @Test
    public void testUriGenerationWithSingleNodeAndParameter() {
        NodesHotThreads action = new NodesHotThreads.Builder().addNode("Pony").interval("100ms").build();
        Assert.assertEquals("/_nodes/Pony/hot_threads?interval=100ms", action.getURI(UNKNOWN));
    }
}

