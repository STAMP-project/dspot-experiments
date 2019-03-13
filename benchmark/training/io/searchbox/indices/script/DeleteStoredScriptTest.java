package io.searchbox.indices.script;


import ElasticsearchVersion.UNKNOWN;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DeleteStoredScriptTest {
    private static final String A_NAME = "a_name";

    private DeleteStoredScript script;

    @Test
    public void methodIsDelete() {
        Assert.assertEquals("DELETE", script.getRestMethodName());
    }

    @Test
    public void scriptingLanguageIsSetIntoPath() {
        Assert.assertThat(script.buildURI(UNKNOWN), CoreMatchers.containsString("/_scripts/"));
    }

    @Test
    public void nameOfTheScriptIsSetIntoPath() {
        Assert.assertThat(script.buildURI(UNKNOWN), CoreMatchers.containsString(("/_scripts/" + (DeleteStoredScriptTest.A_NAME))));
    }
}

