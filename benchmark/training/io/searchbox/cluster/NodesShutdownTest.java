package io.searchbox.cluster;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
public class NodesShutdownTest {
    @Test
    public void testBuildURI() throws Exception {
        NodesShutdown action = new NodesShutdown.Builder().build();
        Assert.assertEquals("/_nodes/_all/_shutdown", action.getURI(UNKNOWN));
    }

    @Test
    public void testBuildURIWithDelay() throws Exception {
        NodesShutdown action = new NodesShutdown.Builder().delay("5s").build();
        Assert.assertEquals("/_nodes/_all/_shutdown?delay=5s", action.getURI(UNKNOWN));
    }

    @Test
    public void testBuildURIWithNodes() throws Exception {
        NodesShutdown action = new NodesShutdown.Builder().addNode("_local").build();
        Assert.assertEquals("/_nodes/_local/_shutdown", action.getURI(UNKNOWN));
    }

    @Test
    public void testBuildURIWithNodeAttributeWildcard() throws Exception {
        NodesShutdown action = new NodesShutdown.Builder().addNode("ra*:2*").build();
        Assert.assertEquals("/_nodes/ra*:2*/_shutdown", action.getURI(UNKNOWN));
    }
}

