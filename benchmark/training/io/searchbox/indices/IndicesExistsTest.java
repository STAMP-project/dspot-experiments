package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ferhat sobay
 */
public class IndicesExistsTest {
    @Test
    public void testBasicUriGeneration() {
        IndicesExists indicesExists = new IndicesExists.Builder("twitter").build();
        Assert.assertEquals("HEAD", indicesExists.getRestMethodName());
        Assert.assertEquals("twitter", indicesExists.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameDestination() {
        IndicesExists indicesExists1 = new IndicesExists.Builder("twitter").build();
        IndicesExists indicesExists1Duplicate = new IndicesExists.Builder("twitter").build();
        Assert.assertEquals(indicesExists1, indicesExists1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentDestination() {
        IndicesExists indicesExists1 = new IndicesExists.Builder("twitter").build();
        IndicesExists indicesExists2 = new IndicesExists.Builder("myspace").build();
        Assert.assertNotEquals(indicesExists1, indicesExists2);
    }
}

