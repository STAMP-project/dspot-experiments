package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class CountTest {
    @Test
    public void getURIWithoutIndexAndType() {
        Count count = new Count.Builder().build();
        Assert.assertEquals("_all/_count", count.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        Count count = new Count.Builder().addIndex("twitter").build();
        Assert.assertEquals("twitter/_count", count.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        Count count = new Count.Builder().addIndex("twitter").addType("tweet").build();
        Assert.assertEquals("twitter/tweet/_count", count.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        Count count = new Count.Builder().addIndex("twitter").addIndex("searchbox").build();
        Assert.assertEquals("twitter%2Csearchbox/_count", count.getURI(UNKNOWN));
    }

    @Test
    public void equals() {
        Count countUserKramer = new Count.Builder().addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").query("{\"user\":\"kramer\"}").build();
        Count countUserKramerDuplicate = new Count.Builder().addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").query("{\"user\":\"kramer\"}").build();
        Assert.assertEquals(countUserKramer, countUserKramerDuplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries() {
        Count countUserKramer = new Count.Builder().addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").query("{\"user\":\"kramer\"}").build();
        Count countUserJerry = new Count.Builder().addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").query("{\"user\":\"jerry\"}").build();
        Assert.assertNotEquals(countUserKramer, countUserJerry);
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        Count count = new Count.Builder().addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build();
        Assert.assertEquals("twitter%2Csearchbox/tweet%2Cjest/_count", count.getURI(UNKNOWN));
    }
}

