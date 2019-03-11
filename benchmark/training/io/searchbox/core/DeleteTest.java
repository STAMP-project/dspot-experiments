package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import Parameters.VERSION;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class DeleteTest {
    @Test
    public void equals() {
        Delete deleteTweet = new Delete.Builder("1").index("twitter").type("tweet").build();
        Delete deleteTweetDuplicate = new Delete.Builder("1").index("twitter").type("tweet").build();
        Assert.assertEquals(deleteTweet, deleteTweetDuplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIds() {
        Delete deleteFirstTweet = new Delete.Builder("1").index("twitter").type("tweet").build();
        Delete deleteThirdTweet = new Delete.Builder("3").index("twitter").type("tweet").build();
        Assert.assertNotEquals(deleteFirstTweet, deleteThirdTweet);
    }

    @Test
    public void deleteDocument() {
        Delete delete = new Delete.Builder("1").index("twitter").type("tweet").build();
        Assert.assertEquals("DELETE", delete.getRestMethodName());
        Assert.assertEquals("twitter/tweet/1", delete.getURI(UNKNOWN));
    }

    @Test
    public void deleteDocumentWithVersion() {
        Delete delete = new Delete.Builder("1").index("twitter").type("tweet").setParameter(VERSION, 1).build();
        Assert.assertEquals("DELETE", delete.getRestMethodName());
        Assert.assertEquals("twitter/tweet/1?version=1", delete.getURI(UNKNOWN));
    }
}

