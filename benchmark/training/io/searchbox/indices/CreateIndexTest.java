package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class CreateIndexTest {
    @Test
    public void createIndexWithoutSettings() {
        CreateIndex createIndex = new CreateIndex.Builder("tweet").build();
        Assert.assertEquals("tweet", createIndex.getURI(UNKNOWN));
        Assert.assertEquals("PUT", createIndex.getRestMethodName());
        String settings = new Gson().toJson(createIndex.getData(new Gson()));
        Assert.assertEquals("null", settings);
    }

    @Test
    public void equalsReturnsTrueForSameSettings() {
        Map<String, Object> indexerSettings = new HashMap<>();
        indexerSettings.put("analysis.analyzer.events.type", "custom");
        indexerSettings.put("analysis.analyzer.events.tokenizer", "standard");
        indexerSettings.put("analysis.analyzer.events.filter", "snowball, standard, lowercase");
        CreateIndex createIndex1 = new CreateIndex.Builder("tweet").settings(indexerSettings).build();
        CreateIndex createIndex1Duplicate = new CreateIndex.Builder("tweet").settings(indexerSettings).build();
        Assert.assertEquals(createIndex1, createIndex1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSettings() {
        Map<String, Object> indexerSettings = new HashMap<>();
        indexerSettings.put("analysis.analyzer.events.type", "custom");
        indexerSettings.put("analysis.analyzer.events.tokenizer", "standard");
        Map<String, Object> indexerSettings2 = new HashMap<>();
        indexerSettings.put("analysis.analyzer.events.type", "custom");
        indexerSettings.put("analysis.analyzer.events.tokenizer", "standard");
        CreateIndex createIndex1 = new CreateIndex.Builder("tweet").settings(indexerSettings).build();
        indexerSettings2.put("analysis.analyzer.events.filter", "snowball, standard, lowercase");
        CreateIndex createIndex2 = new CreateIndex.Builder("tweet").settings(indexerSettings2).build();
        Assert.assertNotEquals(createIndex1, createIndex2);
    }
}

