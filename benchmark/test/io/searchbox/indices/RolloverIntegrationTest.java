package io.searchbox.indices;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;


@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class RolloverIntegrationTest extends AbstractIntegrationTest {
    private final Map<String, Object> rolloverConditions = new org.elasticsearch.common.collect.MapBuilder<String, Object>().put("max_docs", "1").put("max_age", "1d").immutableMap();

    @Test
    public void testRollover() throws IOException {
        String aliasSetting = "{ \"rollover-test-index\": {} }";
        CreateIndex createIndex = new CreateIndex.Builder("rollover-test-index-000001").aliases(aliasSetting).build();
        JestResult result = client.execute(createIndex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        Rollover rollover = new Rollover.Builder("rollover-test-index").conditions(rolloverConditions).build();
        result = client.execute(rollover);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }
}

