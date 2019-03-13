package io.searchbox.indices.script;


import ElasticsearchVersion.UNKNOWN;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
public class GetStoredScriptTest {
    private static final String A_NAME = "a_name";

    private GetStoredScript script;

    @Test
    public void methodIsGet() {
        Assert.assertEquals("GET", script.getRestMethodName());
    }

    @Test
    public void scriptingLanguageIsSetIntoPath() {
        Assert.assertThat(script.buildURI(UNKNOWN), CoreMatchers.containsString("/_scripts/"));
    }

    @Test
    public void nameOfTheScriptIsSetIntoPath() {
        Assert.assertThat(script.buildURI(UNKNOWN), CoreMatchers.containsString(("/_scripts/" + (GetStoredScriptTest.A_NAME))));
    }
}

