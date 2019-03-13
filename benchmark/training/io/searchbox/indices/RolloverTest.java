package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import com.google.gson.Gson;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class RolloverTest {
    Map<String, Object> rolloverConditions = new org.elasticsearch.common.collect.MapBuilder<String, Object>().put("max_docs", "10000").put("max_age", "1d").immutableMap();

    Map<String, Object> rolloverSettings = new org.elasticsearch.common.collect.MapBuilder<String, Object>().put("index.number_of_shards", "2").immutableMap();

    @Test
    public void testBasicUriGeneration() {
        Rollover rollover = new Rollover.Builder("twitter").conditions(rolloverConditions).build();
        Assert.assertEquals("POST", rollover.getRestMethodName());
        Assert.assertEquals("twitter/_rollover", rollover.getURI(UNKNOWN));
        Assert.assertEquals("{\"conditions\":{\"max_age\":\"1d\",\"max_docs\":\"10000\"}}", rollover.getData(new Gson()));
    }

    @Test
    public void testBasicUriWithSettingsGeneration() {
        Rollover rollover = new Rollover.Builder("twitter").conditions(rolloverConditions).settings(rolloverSettings).build();
        Assert.assertEquals("POST", rollover.getRestMethodName());
        Assert.assertEquals("twitter/_rollover", rollover.getURI(UNKNOWN));
        Assert.assertEquals("{\"settings\":{\"index.number_of_shards\":\"2\"},\"conditions\":{\"max_age\":\"1d\",\"max_docs\":\"10000\"}}", rollover.getData(new Gson()));
    }

    @Test
    public void testDryRunUriGeneration() {
        Rollover rollover = new Rollover.Builder("twitter").conditions(rolloverConditions).setDryRun(true).build();
        Assert.assertEquals("POST", rollover.getRestMethodName());
        Assert.assertEquals("twitter/_rollover?dry_run", rollover.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameDestination() {
        Rollover indexRollover1 = new Rollover.Builder("twitter").conditions(rolloverConditions).build();
        Rollover indexRollover2 = new Rollover.Builder("twitter").conditions(rolloverConditions).build();
        Assert.assertEquals(indexRollover1, indexRollover2);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        Rollover indexRollover1 = new Rollover.Builder("twitter").conditions(rolloverConditions).build();
        Rollover indexRollover2 = new Rollover.Builder("myspace").conditions(rolloverConditions).build();
        Assert.assertNotEquals(indexRollover1, indexRollover2);
    }
}

