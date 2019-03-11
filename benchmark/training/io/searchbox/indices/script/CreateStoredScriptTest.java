package io.searchbox.indices.script;


import CreateStoredScript.Builder;
import ElasticsearchVersion.UNKNOWN;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CreateStoredScriptTest {
    private static final String A_NAME = "a_name";

    private CreateStoredScript script;

    private Builder builder;

    private String groovysnippet;

    @Test
    public void defaultScriptingLanguageIsPainless() {
        CreateStoredScript script = new CreateStoredScript.Builder(CreateStoredScriptTest.A_NAME).build();
        Assert.assertEquals(ScriptLanguage.PAINLESS, script.getScriptLanguage());
    }

    @Test
    public void scriptingLanguageIsSetCorrectly() {
        CreateStoredScript script = new CreateStoredScript.Builder(CreateStoredScriptTest.A_NAME).setLanguage(ScriptLanguage.JAVASCRIPT).build();
        Assert.assertEquals(ScriptLanguage.JAVASCRIPT, script.getScriptLanguage());
    }

    @Test
    public void methodIsPost() {
        Assert.assertEquals("POST", script.getRestMethodName());
    }

    @Test
    public void scriptingLanguageIsSetIntoPath() {
        Assert.assertThat(script.buildURI(UNKNOWN), CoreMatchers.containsString("/_scripts/"));
    }

    @Test
    public void nameOfTheScriptIsSetIntoPath() {
        Assert.assertThat(script.buildURI(UNKNOWN), CoreMatchers.containsString(("/_scripts/" + (CreateStoredScriptTest.A_NAME))));
    }

    @Test
    public void scriptSourceIsValidJsonString() {
        builder.setSource(groovysnippet);
        script = builder.build();
        JsonObject jsonPayload = parseAsGson(script.getData(new Gson()));
        Assert.assertNotNull(jsonPayload.get("script"));
        Assert.assertEquals(groovysnippet, jsonPayload.get("script").getAsJsonObject().get("source").getAsString());
    }

    @Test
    public void fileSourceIsValidJsonString() throws Exception {
        builder.loadSource(createTempGroovySnippetFile());
        script = builder.build();
        JsonObject jsonPayload = parseAsGson(script.getData(new Gson()));
        Assert.assertNotNull(jsonPayload.get("script"));
        Assert.assertEquals(groovysnippet, jsonPayload.get("script").getAsJsonObject().get("source").getAsString());
    }
}

