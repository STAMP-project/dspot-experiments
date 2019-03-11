package io.searchbox.indices.settings;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class UpdateSettingsTest {
    @Test
    public void testDefaultBehaviour() {
        String expectedUri = "_all/_settings";
        UpdateSettings updateSettings = new UpdateSettings.Builder("").build();
        Assert.assertEquals(expectedUri, updateSettings.getURI(UNKNOWN));
        Assert.assertEquals("", updateSettings.getData(null));
        Assert.assertEquals("PUT", updateSettings.getRestMethodName());
    }

    @Test
    public void equalsReturnsTrueForSameSource() {
        UpdateSettings updateSettings1 = new UpdateSettings.Builder("source 1").build();
        UpdateSettings updateSettings1Duplicate = new UpdateSettings.Builder("source 1").build();
        Assert.assertEquals(updateSettings1, updateSettings1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSource() {
        UpdateSettings updateSettings1 = new UpdateSettings.Builder("source 1").build();
        UpdateSettings updateSettings2 = new UpdateSettings.Builder("source 2").build();
        Assert.assertNotEquals(updateSettings1, updateSettings2);
    }
}

