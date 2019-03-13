package io.searchbox.cluster;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
@ClusterScope(scope = Scope.TEST, numDataNodes = 1)
public class UpdateSettingsIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void nullSourceShouldFailOnServer() throws IOException {
        UpdateSettings updateSettings = new UpdateSettings.Builder(null).build();
        JestResult result = client.execute(updateSettings);
        assertFalse(result.isSucceeded());
    }

    @Test
    public void transientSettingShouldBeUpdated() throws IOException {
        String source = "{\n" + ((("    \"transient\" : {\n" + "        \"indices.recovery.max_bytes_per_sec\" : \"20mb\"\n") + "    }\n") + "}");
        UpdateSettings updateSettings = new UpdateSettings.Builder(source).build();
        JestResult result = client.execute(updateSettings);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        Map updatedSettings = result.getSourceAsObject(Map.class);
        assertTrue(((Boolean) (updatedSettings.get("acknowledged"))));
        Map transientSettings = ((Map) (updatedSettings.get("transient")));
        assertEquals(1, transientSettings.size());
        Map persistentSettings = ((Map) (updatedSettings.get("persistent")));
        assertTrue(persistentSettings.isEmpty());
    }
}

