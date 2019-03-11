package io.searchbox.indices.script;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import org.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptResponse;
import org.junit.Test;


@ClusterScope(scope = Scope.TEST, numDataNodes = 1)
public class CreateStoredScriptIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void createAStoredScript() throws IOException {
        String name = "script-test";
        String script = "int aVariable = 1; return aVariable";
        CreateStoredScript createStoredScript = new CreateStoredScript.Builder(name).setLanguage(ScriptLanguage.PAINLESS).setSource(script).build();
        JestResult result = client.execute(createStoredScript);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        GetStoredScriptResponse getStoredScriptResponse = client().admin().cluster().prepareGetStoredScript().setId(name).get();
        assertNotNull(getStoredScriptResponse.getSource());
        assertEquals(script, getStoredScriptResponse.getSource().getSource());
    }
}

