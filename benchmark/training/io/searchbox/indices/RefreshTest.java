package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class RefreshTest {
    @Test
    public void testBasicUriGeneration() {
        Refresh refresh = new Refresh.Builder().addIndex("twitter").addIndex("myspace").build();
        Assert.assertEquals("POST", refresh.getRestMethodName());
        Assert.assertEquals("twitter%2Cmyspace/_refresh", refresh.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndices() {
        Refresh refresh1 = new Refresh.Builder().addIndex("twitter").addIndex("myspace").build();
        Refresh refresh1Duplicate = new Refresh.Builder().addIndex("twitter").addIndex("myspace").build();
        Assert.assertEquals(refresh1, refresh1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndices() {
        Refresh refresh1 = new Refresh.Builder().addIndex("twitter").addIndex("myspace").build();
        Refresh refresh2 = new Refresh.Builder().addIndex("twitter").addIndex("facebook").build();
        Assert.assertNotEquals(refresh1, refresh2);
    }
}

