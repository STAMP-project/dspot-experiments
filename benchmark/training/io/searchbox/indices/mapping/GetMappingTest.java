package io.searchbox.indices.mapping;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class GetMappingTest {
    @Test
    public void testBasicUriGeneration() {
        GetMapping getMapping = new GetMapping.Builder().addIndex("twitter").build();
        Assert.assertEquals("GET", getMapping.getRestMethodName());
        Assert.assertEquals("twitter/_mapping", getMapping.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        GetMapping getMapping1 = new GetMapping.Builder().addIndex("twitter").build();
        GetMapping getMapping1Duplicate = new GetMapping.Builder().addIndex("twitter").build();
        Assert.assertEquals(getMapping1, getMapping1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        GetMapping getMapping1 = new GetMapping.Builder().addIndex("twitter").build();
        GetMapping getMapping2 = new GetMapping.Builder().addIndex("myspace").build();
        Assert.assertNotEquals(getMapping1, getMapping2);
    }
}

