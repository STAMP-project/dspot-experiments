package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lior Knaany
 */
public class UpdateByQueryTest {
    @Test
    public void getURIWithoutIndexAndType() {
        Assert.assertEquals("_all/_update_by_query", new UpdateByQuery.Builder(null).build().getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        Assert.assertEquals("twitter/_update_by_query", new UpdateByQuery.Builder(null).addIndex("twitter").build().getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        Assert.assertEquals("_all/tweet%2Cjest/_update_by_query", new UpdateByQuery.Builder(null).addType("tweet").addType("jest").build().getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        Assert.assertEquals("twitter/tweet/_update_by_query", new UpdateByQuery.Builder(null).addIndex("twitter").addType("tweet").build().getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        Assert.assertEquals("twitter%2Csearchbox/_update_by_query", new UpdateByQuery.Builder(null).addIndex("twitter").addIndex("searchbox").build().getURI(UNKNOWN));
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        Assert.assertEquals("twitter%2Csearchbox/tweet%2Cjest/_update_by_query", new UpdateByQuery.Builder(null).addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build().getURI(UNKNOWN));
    }

    @Test
    public void equals() {
        UpdateByQuery deleteUserKramer = new UpdateByQuery.Builder("{\"user\":\"kramer\"}").addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build();
        UpdateByQuery deleteUserKramerDuplicate = new UpdateByQuery.Builder("{\"user\":\"kramer\"}").addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build();
        Assert.assertEquals(deleteUserKramer, deleteUserKramerDuplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries() {
        UpdateByQuery deleteUserKramer = new UpdateByQuery.Builder("{\"user\":\"kramer\"}").addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build();
        UpdateByQuery deleteUserJerry = new UpdateByQuery.Builder("{\"user\":\"jerry\"}").addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build();
        Assert.assertNotEquals(deleteUserKramer, deleteUserJerry);
    }
}

