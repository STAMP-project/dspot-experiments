package io.searchbox.indices.script;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import ScriptLanguage.PAINLESS;
import XContentType.JSON;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptResponse;
import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptResponse;
import org.elasticsearch.common.bytes.BytesArray;
import org.junit.Test;


@ClusterScope(scope = Scope.TEST, numDataNodes = 1)
public class DeleteStoredScriptIntegrationTest extends AbstractIntegrationTest {
    private static final String A_SCRIPT_NAME = "script-test";

    String script = "{\n" + (((("          \"script\": {\n" + "              \"lang\" : \"painless\",\n") + "              \"source\" : \"int aVariable = 1; return aVariable\"\n") + "            }\n") + "        }");

    @Test
    public void delete_a_stored_script_for_Painless() throws Exception {
        PutStoredScriptResponse response = client().admin().cluster().preparePutStoredScript().setId(DeleteStoredScriptIntegrationTest.A_SCRIPT_NAME).setContent(new BytesArray(script), JSON).get();
        assertTrue("could not create stored script on server", response.isAcknowledged());
        DeleteStoredScript deleteStoredScript = new DeleteStoredScript.Builder(DeleteStoredScriptIntegrationTest.A_SCRIPT_NAME).setLanguage(PAINLESS).build();
        JestResult result = client.execute(deleteStoredScript);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        GetStoredScriptResponse getStoredScriptResponse = client().admin().cluster().prepareGetStoredScript().setId(DeleteStoredScriptIntegrationTest.A_SCRIPT_NAME).get();
        assertNull("Script should have been deleted", getStoredScriptResponse.getSource());
    }
}

