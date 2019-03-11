package io.searchbox.indices.script;


import ScriptLanguage.PAINLESS;
import XContentType.JSON;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptResponse;
import org.elasticsearch.common.bytes.BytesArray;
import org.junit.Test;


public class GetStoredScriptIntegrationTest extends AbstractIntegrationTest {
    private static final String lang = PAINLESS.pathParameterName;

    private static final String SCRIPT = (((("{" + ("    \"script\": {" + "       \"lang\": \"")) + (GetStoredScriptIntegrationTest.lang)) + "\",") + "       \"source\": \"return 42;\"}") + "}";

    @Test
    public void createStoredScript() throws IOException {
        String name = "mylilscript";
        PutStoredScriptResponse response = client().admin().cluster().preparePutStoredScript().setId(name).setContent(new BytesArray(GetStoredScriptIntegrationTest.SCRIPT), JSON).get();
        assertTrue("could not create stored script on server", response.isAcknowledged());
        GetStoredScript getStoredScript = new GetStoredScript.Builder(name).setLanguage(PAINLESS).build();
        JestResult result = client.execute(getStoredScript);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }
}

