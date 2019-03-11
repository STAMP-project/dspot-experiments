package io.searchbox.cluster;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class NodesInfoTest {
    @Test
    public void getURIWithoutNodeAndInfo() {
        NodesInfo nodesInfo = new NodesInfo.Builder().build();
        Assert.assertEquals("/_nodes/_all", nodesInfo.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyOneNode() {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("twitter").build();
        Assert.assertEquals("/_nodes/twitter", nodesInfo.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOneNodeAndOneInfo() {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("twitter").withOs().build();
        Assert.assertEquals("/_nodes/twitter/os", nodesInfo.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyOneType() {
        NodesInfo nodesInfo = new NodesInfo.Builder().withOs().build();
        Assert.assertEquals("/_nodes/_all/os", nodesInfo.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleNode() {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("twitter").addNode("searchbox").build();
        Assert.assertEquals("/_nodes/twitter,searchbox", nodesInfo.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        NodesInfo nodesInfo = new NodesInfo.Builder().withOs().withProcess().build();
        Assert.assertEquals("/_nodes/_all/os,process", nodesInfo.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithMultipleNodeAndTypes() {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("twitter").addNode("jest").withOs().withProcess().withSettings().build();
        Assert.assertEquals("/_nodes/twitter,jest/os,process,settings", nodesInfo.getURI(UNKNOWN));
    }
}

