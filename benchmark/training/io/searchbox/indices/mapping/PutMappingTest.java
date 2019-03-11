package io.searchbox.indices.mapping;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class PutMappingTest {
    @Test
    public void testBasicUriGeneration() {
        PutMapping putMapping = new PutMapping.Builder("twitter", "tweet", "source").build();
        Assert.assertEquals("PUT", putMapping.getRestMethodName());
        Assert.assertEquals("twitter/tweet/_mapping", putMapping.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameSource() {
        PutMapping putMapping1 = new PutMapping.Builder("twitter", "tweet", "source").build();
        PutMapping putMapping1Duplicate = new PutMapping.Builder("twitter", "tweet", "source").build();
        Assert.assertEquals(putMapping1, putMapping1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSource() {
        PutMapping putMapping1 = new PutMapping.Builder("twitter", "tweet", "source 1").build();
        PutMapping putMapping2 = new PutMapping.Builder("twitter", "tweet", "source 2").build();
        Assert.assertNotEquals(putMapping1, putMapping2);
    }
}

