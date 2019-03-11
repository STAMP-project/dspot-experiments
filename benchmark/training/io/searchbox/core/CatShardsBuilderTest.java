package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class CatShardsBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.ShardsBuilder().build();
        Assert.assertEquals("application/json", cat.getHeader("accept"));
        Assert.assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.ShardsBuilder().build();
        Assert.assertEquals("_cat/shards", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexGiven() {
        Cat cat = new Cat.ShardsBuilder().addIndex("testIndex").build();
        Assert.assertEquals("_cat/shards/testIndex", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.ShardsBuilder().setParameter("v", "true").build();
        Assert.assertEquals("_cat/shards?v=true", cat.getURI(UNKNOWN));
    }
}

