package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
public class CloseIndexTest {
    @Test
    public void testBasicUrlGeneration() {
        CloseIndex closeIndex = new CloseIndex.Builder("twitter").build();
        Assert.assertEquals("POST", closeIndex.getRestMethodName());
        Assert.assertEquals("twitter/_close", closeIndex.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndices() {
        CloseIndex closeIndex1 = new CloseIndex.Builder("twitter").build();
        CloseIndex closeIndex1Duplicate = new CloseIndex.Builder("twitter").build();
        Assert.assertEquals(closeIndex1, closeIndex1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndices() {
        CloseIndex closeIndex1 = new CloseIndex.Builder("twitter").build();
        CloseIndex closeIndex2 = new CloseIndex.Builder("myspace").build();
        Assert.assertNotEquals(closeIndex1, closeIndex2);
    }
}

