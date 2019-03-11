package io.searchbox.indices.mapping;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class DeleteMappingTest {
    @Test
    public void testBasicUriGeneration() {
        DeleteMapping deleteMapping = new DeleteMapping.Builder("twitter", "tweet").build();
        Assert.assertEquals("DELETE", deleteMapping.getRestMethodName());
        Assert.assertEquals("twitter/tweet/_mapping", deleteMapping.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        DeleteMapping deleteMapping1 = new DeleteMapping.Builder("twitter", "tweet").build();
        DeleteMapping deleteMapping1Duplicate = new DeleteMapping.Builder("twitter", "tweet").build();
        Assert.assertEquals(deleteMapping1, deleteMapping1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        DeleteMapping deleteMapping1 = new DeleteMapping.Builder("twitter", "tweet").build();
        DeleteMapping deleteMapping2 = new DeleteMapping.Builder("twitter", "myspace").build();
        Assert.assertNotEquals(deleteMapping1, deleteMapping2);
    }
}

