package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class DeleteByQueryTest {
    @Test
    public void getURIWithoutIndexAndType() {
        Assert.assertEquals("_all/_delete_by_query", new DeleteByQuery.Builder(null).build().getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        Assert.assertEquals("twitter/_delete_by_query", new DeleteByQuery.Builder(null).addIndex("twitter").build().getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        Assert.assertEquals("_all/tweet%2Cjest/_delete_by_query", new DeleteByQuery.Builder(null).addType("tweet").addType("jest").build().getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        Assert.assertEquals("twitter/tweet/_delete_by_query", new DeleteByQuery.Builder(null).addIndex("twitter").addType("tweet").build().getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        Assert.assertEquals("twitter%2Csearchbox/_delete_by_query", new DeleteByQuery.Builder(null).addIndex("twitter").addIndex("searchbox").build().getURI(UNKNOWN));
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        Assert.assertEquals("twitter%2Csearchbox/tweet%2Cjest/_delete_by_query", new DeleteByQuery.Builder(null).addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build().getURI(UNKNOWN));
    }

    @Test
    public void equals() {
        DeleteByQuery deleteUserKramer = new DeleteByQuery.Builder("{\"user\":\"kramer\"}").addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build();
        DeleteByQuery deleteUserKramerDuplicate = new DeleteByQuery.Builder("{\"user\":\"kramer\"}").addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build();
        Assert.assertEquals(deleteUserKramer, deleteUserKramerDuplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries() {
        DeleteByQuery deleteUserKramer = new DeleteByQuery.Builder("{\"user\":\"kramer\"}").addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build();
        DeleteByQuery deleteUserJerry = new DeleteByQuery.Builder("{\"user\":\"jerry\"}").addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build();
        Assert.assertNotEquals(deleteUserKramer, deleteUserJerry);
    }
}

