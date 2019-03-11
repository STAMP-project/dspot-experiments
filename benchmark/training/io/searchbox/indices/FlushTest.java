package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class FlushTest {
    @Test
    public void testBasicUriGeneration() {
        Flush flush = new Flush.Builder().addIndex("twitter").addIndex("myspace").build();
        Assert.assertEquals("POST", flush.getRestMethodName());
        Assert.assertEquals("twitter%2Cmyspace/_flush", flush.getURI(UNKNOWN));
    }

    @Test
    public void testBasicUriGenerationWithForce() {
        Flush flush = new Flush.Builder().addIndex("twitter").force().build();
        Assert.assertEquals("POST", flush.getRestMethodName());
        Assert.assertEquals("twitter/_flush?force=true", flush.getURI(UNKNOWN));
    }

    @Test
    public void testBasicUriGenerationWithWaitIfOngoing() {
        Flush flush = new Flush.Builder().addIndex("twitter").waitIfOngoing().build();
        Assert.assertEquals("POST", flush.getRestMethodName());
        Assert.assertEquals("twitter/_flush?wait_if_ongoing=true", flush.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndices() {
        Flush flush1 = new Flush.Builder().addIndex("twitter").addIndex("myspace").build();
        Flush flush1Duplicate = new Flush.Builder().addIndex("twitter").addIndex("myspace").build();
        Assert.assertEquals(flush1, flush1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndices() {
        Flush flush1 = new Flush.Builder().addIndex("twitter").addIndex("myspace").build();
        Flush flush2 = new Flush.Builder().addIndex("myspace").build();
        Assert.assertNotEquals(flush1, flush2);
    }
}

