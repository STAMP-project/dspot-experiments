package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
public class ClearCacheTest {
    @Test
    public void testBasicUrlGeneration() {
        ClearCache clearCache = new ClearCache.Builder().build();
        Assert.assertEquals("_all/_cache/clear", clearCache.getURI(UNKNOWN));
    }

    @Test
    public void testBasicUrlGenerationWithParameters() {
        ClearCache clearCache = new ClearCache.Builder().bloom(true).fieldData(false).build();
        Assert.assertEquals("_all/_cache/clear?bloom=true&field_data=false", clearCache.getURI(UNKNOWN));
    }

    @Test
    public void testMultiIndexUrlGenerationWithParameters() {
        ClearCache clearCache = new ClearCache.Builder().addIndex("tom").addIndex("jerry").bloom(true).build();
        Assert.assertEquals("tom%2Cjerry/_cache/clear?bloom=true", clearCache.getURI(UNKNOWN));
    }

    @Test
    public void equals() {
        ClearCache clearCache1 = new ClearCache.Builder().addIndex("twitter").bloom(true).fieldData(false).build();
        ClearCache clearCache1Duplicate = new ClearCache.Builder().addIndex("twitter").bloom(true).fieldData(false).build();
        Assert.assertEquals(clearCache1, clearCache1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentParameters() {
        ClearCache clearCache1 = new ClearCache.Builder().addIndex("twitter").bloom(true).fieldData(false).build();
        ClearCache clearCache2 = new ClearCache.Builder().addIndex("twitter").bloom(false).fieldData(true).build();
        Assert.assertNotEquals(clearCache1, clearCache2);
    }
}

