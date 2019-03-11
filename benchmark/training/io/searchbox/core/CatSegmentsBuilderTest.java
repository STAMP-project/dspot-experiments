package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Eran Shahar on 16/03/2017.
 */
public class CatSegmentsBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.ShardsBuilder().build();
        Assert.assertEquals("application/json", cat.getHeader("accept"));
        Assert.assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.SegmentsBuilder().build();
        Assert.assertEquals("_cat/segments", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenSingleIndexGiven() {
        Cat cat = new Cat.SegmentsBuilder().addIndex("testIndex").build();
        Assert.assertEquals("_cat/segments/testIndex", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenIndicesGiven() {
        Cat cat = new Cat.SegmentsBuilder().addIndex("testIndex1").addIndex("testIndex2").build();
        Assert.assertEquals("_cat/segments/testIndex1%2CtestIndex2", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.SegmentsBuilder().setParameter("v", "true").build();
        Assert.assertEquals("_cat/segments?v=true", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenHeadersParameterGiven() {
        Cat cat = new Cat.SegmentsBuilder().setParameter("h", "index,shard,prirep,segment,docs.count").build();
        Assert.assertEquals("_cat/segments?h=index%2Cshard%2Cprirep%2Csegment%2Cdocs.count", cat.getURI(UNKNOWN));
    }
}

