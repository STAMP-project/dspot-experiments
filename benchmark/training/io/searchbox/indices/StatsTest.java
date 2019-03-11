package io.searchbox.indices;


import AbstractAction.CHARSET;
import ElasticsearchVersion.UNKNOWN;
import java.net.URLDecoder;
import org.junit.Assert;
import org.junit.Test;


public class StatsTest {
    @Test
    public void testBasicUriGeneration() {
        Stats stats = new Stats.Builder().addIndex("twitter").build();
        Assert.assertEquals("GET", stats.getRestMethodName());
        Assert.assertEquals("twitter/_stats", stats.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        Stats stats1 = new Stats.Builder().addIndex("twitter").build();
        Stats stats1Duplicate = new Stats.Builder().addIndex("twitter").build();
        Assert.assertEquals(stats1, stats1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        Stats stats1 = new Stats.Builder().addIndex("twitter").build();
        Stats stats2 = new Stats.Builder().addIndex("myspace").build();
        Assert.assertNotEquals(stats1, stats2);
    }

    @Test
    public void testUriGenerationWithStatsFields() throws Exception {
        Stats action = new Stats.Builder().flush(true).indexing(true).search(true, "group1", "group2").build();
        Assert.assertEquals("_all/_stats/flush,indexing,search?groups=group1,group2", URLDecoder.decode(action.getURI(UNKNOWN), CHARSET));
    }

    @Test
    public void testUriGenerationWhenRemovingStatsFields() throws Exception {
        Stats action = new Stats.Builder().flush(true).indexing(true).indexing(false).build();
        Assert.assertEquals("_all/_stats/flush", URLDecoder.decode(action.getURI(UNKNOWN), CHARSET));
    }
}

