package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class ForceMergeTest {
    @Test
    public void testBasicUriGeneration() {
        ForceMerge forceMerge = new ForceMerge.Builder().addIndex("twitter").build();
        Assert.assertEquals("POST", forceMerge.getRestMethodName());
        Assert.assertEquals("twitter/_forcemerge", forceMerge.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        ForceMerge forceMerge1 = new ForceMerge.Builder().addIndex("twitter").build();
        ForceMerge forceMerge1Duplicate = new ForceMerge.Builder().addIndex("twitter").build();
        Assert.assertEquals(forceMerge1, forceMerge1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        ForceMerge forceMerge1 = new ForceMerge.Builder().addIndex("twitter").build();
        ForceMerge forceMerge2 = new ForceMerge.Builder().addIndex("myspace").build();
        Assert.assertNotEquals(forceMerge1, forceMerge2);
    }
}

