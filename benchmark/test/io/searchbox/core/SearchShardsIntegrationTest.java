package io.searchbox.core;


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
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class SearchShardsIntegrationTest extends AbstractIntegrationTest {
    static final String INDEX = "twitter";

    static final String TYPE = "tweet";

    @Test
    public void testBasicFlow() throws IOException {
        SearchShards searchShards = new SearchShards.Builder().addIndex(SearchShardsIntegrationTest.INDEX).build();
        JestResult result = client.execute(searchShards);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        Map source = result.getSourceAsObject(Map.class);
        assertTrue(source.containsKey("nodes"));
        assertTrue(source.containsKey("shards"));
    }
}

