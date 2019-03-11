package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class UpdateTest {
    @Test
    public void updateDocumentWithoutDoc() {
        Update update = new Update.Builder(new Object()).index("twitter").type("tweet").id("1").build();
        Assert.assertEquals("POST", update.getRestMethodName());
        Assert.assertEquals("twitter/tweet/1/_update", update.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSamePayload() {
        Update update1 = new Update.Builder("payload1").index("twitter").type("tweet").id("1").build();
        Update update1Duplicate = new Update.Builder("payload1").index("twitter").type("tweet").id("1").build();
        Assert.assertEquals(update1, update1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentPayload() {
        Update update1 = new Update.Builder("payload1").index("twitter").type("tweet").id("1").build();
        Update update2 = new Update.Builder("payload2").index("twitter").type("tweet").id("1").build();
        Assert.assertNotEquals(update1, update2);
    }
}

