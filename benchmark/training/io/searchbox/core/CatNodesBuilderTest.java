package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class CatNodesBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.NodesBuilder().build();
        Assert.assertEquals("application/json", cat.getHeader("accept"));
        Assert.assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.NodesBuilder().build();
        Assert.assertEquals("_cat/nodes", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.NodesBuilder().setParameter("v", "true").build();
        Assert.assertEquals("_cat/nodes?v=true", cat.getURI(UNKNOWN));
    }
}

