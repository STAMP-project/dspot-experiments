package io.searchbox.core;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import org.junit.Test;


@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class PingIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void simplePing() throws IOException {
        Ping ping = new Ping.Builder().build();
        JestResult result = client.execute(ping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        final JsonObject responseJson = result.getJsonObject();
        assertNotNull(responseJson.getAsJsonPrimitive("name"));
        assertNotNull(responseJson.getAsJsonPrimitive("cluster_name"));
        assertNotNull(responseJson.getAsJsonPrimitive("cluster_uuid"));
        assertNotNull(responseJson.getAsJsonObject("version"));
        assertEquals("You Know, for Search", responseJson.get("tagline").getAsString());
    }
}

