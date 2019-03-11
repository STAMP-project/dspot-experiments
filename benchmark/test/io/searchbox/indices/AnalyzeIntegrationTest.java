package io.searchbox.indices;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class AnalyzeIntegrationTest extends AbstractIntegrationTest {
    private static String sample_book;

    @Test
    public void testWithAnalyzer() throws IOException {
        Action action = new Analyze.Builder().analyzer("standard").text(AnalyzeIntegrationTest.sample_book).build();
        expectTokens(action, 22);
    }

    @Test
    public void testWithAnalyzerWithTextFormat() throws IOException {
        Action action = // .format("text")
        new Analyze.Builder().analyzer("standard").text(AnalyzeIntegrationTest.sample_book).build();
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonObject resultObj = result.getJsonObject();
        assertNotNull(resultObj);
        JsonArray tokens = resultObj.getAsJsonArray("tokens");
        assertNotNull(tokens);
    }

    @Test
    public void testWithCustomTransientAnalyzer() throws IOException {
        Action action = new Analyze.Builder().tokenizer("keyword").filter("lowercase").text(AnalyzeIntegrationTest.sample_book).build();
        expectTokens(action, 1);
    }
}

