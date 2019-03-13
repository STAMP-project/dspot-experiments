package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class CatAllocationBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.AllocationBuilder().build();
        Assert.assertEquals("application/json", cat.getHeader("accept"));
        Assert.assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.AllocationBuilder().build();
        Assert.assertEquals("_cat/allocation", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenSingleNodeGiven() {
        Cat cat = new Cat.AllocationBuilder().addNode("testNode").build();
        Assert.assertEquals("_cat/allocation/testNode", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenNodesGiven() {
        Cat cat = new Cat.AllocationBuilder().addNode("testNode1").addNode("testNode2").build();
        Assert.assertEquals("_cat/allocation/testNode1%2CtestNode2", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.AllocationBuilder().setParameter("v", "true").build();
        Assert.assertEquals("_cat/allocation?v=true", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenHeadersParameterGiven() {
        Cat cat = new Cat.AllocationBuilder().setParameter("h", "shards,disk.indices,disk.used").build();
        Assert.assertEquals("_cat/allocation?h=shards%2Cdisk.indices%2Cdisk.used", cat.getURI(UNKNOWN));
    }
}

