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
public class IndexTest {
    @Test
    public void indexDocument() {
        Index index = new Index.Builder(new Object()).index("twitter").type("tweet").id("1").build();
        Assert.assertEquals("PUT", index.getRestMethodName());
        Assert.assertEquals("twitter/tweet/1", index.getURI(UNKNOWN));
    }

    @Test
    public void indexDocumentWithVersionParameter() {
        Index index = new Index.Builder(new Object()).index("twitter").type("tweet").id("1").setParameter(VERSION, 3).build();
        Assert.assertEquals("PUT", index.getRestMethodName());
        Assert.assertEquals("twitter/tweet/1?version=3", index.getURI(UNKNOWN));
    }

    @Test
    public void indexDocumentWithoutId() {
        Index index = new Index.Builder(new Object()).index("twitter").type("tweet").build();
        Assert.assertEquals("POST", index.getRestMethodName());
        Assert.assertEquals("twitter/tweet", index.getURI(UNKNOWN));
    }

    @Test
    public void equals() {
        Object source = new Object();
        Index index1 = new Index.Builder(source).index("twitter").type("tweet").id("1").build();
        Index index1Duplicate = new Index.Builder(source).index("twitter").type("tweet").id("1").build();
        Assert.assertEquals(index1, index1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSources() {
        Index index1 = new Index.Builder(new Object()).index("twitter").type("tweet").id("1").build();
        Index index2 = new Index.Builder(new Object()).index("twitter").type("tweet").id("1").build();
        Assert.assertNotEquals(index1, index2);
    }
}

