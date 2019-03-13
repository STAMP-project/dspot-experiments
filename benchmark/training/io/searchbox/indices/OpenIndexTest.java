package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class OpenIndexTest {
    @Test
    public void testBasicUriGeneration() {
        OpenIndex openIndex = new OpenIndex.Builder("twitter").build();
        Assert.assertEquals("POST", openIndex.getRestMethodName());
        Assert.assertEquals("twitter/_open", openIndex.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        OpenIndex openIndex1 = new OpenIndex.Builder("twitter").build();
        OpenIndex openIndex1Duplicate = new OpenIndex.Builder("twitter").build();
        Assert.assertEquals(openIndex1, openIndex1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        OpenIndex openIndex1 = new OpenIndex.Builder("twitter").build();
        OpenIndex openIndex2 = new OpenIndex.Builder("myspace").build();
        Assert.assertNotEquals(openIndex1, openIndex2);
    }
}

