package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class OptimizeTest {
    @Test
    public void testBasicUriGeneration() {
        Optimize optimize = new Optimize.Builder().addIndex("twitter").build();
        Assert.assertEquals("POST", optimize.getRestMethodName());
        Assert.assertEquals("twitter/_optimize", optimize.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        Optimize optimize1 = new Optimize.Builder().addIndex("twitter").build();
        Optimize optimize1Duplicate = new Optimize.Builder().addIndex("twitter").build();
        Assert.assertEquals(optimize1, optimize1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        Optimize optimize1 = new Optimize.Builder().addIndex("twitter").build();
        Optimize optimize2 = new Optimize.Builder().addIndex("myspace").build();
        Assert.assertNotEquals(optimize1, optimize2);
    }
}

