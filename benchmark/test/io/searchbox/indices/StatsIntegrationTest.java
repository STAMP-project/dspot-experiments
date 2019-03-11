package io.searchbox.indices;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class StatsIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX_NAME = "flush_test_index";

    private static final String STATS_WITH_OPTIONS_INDEX_NAME = "stats_with_options_index";

    @Test
    public void testDefaultStats() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        createIndex(StatsIntegrationTest.INDEX_NAME);
        ensureSearchable(StatsIntegrationTest.INDEX_NAME);
        Stats stats = new Stats.Builder().build();
        JestResult result = client.execute(stats);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        // confirm that response has all the default stats types
        JsonObject jsonResult = result.getJsonObject();
        JsonObject statsJson = jsonResult.getAsJsonObject("indices").getAsJsonObject(StatsIntegrationTest.INDEX_NAME).getAsJsonObject("total");
        assertNotNull(statsJson);
        assertNotNull(statsJson.getAsJsonObject("docs"));
        assertNotNull(statsJson.getAsJsonObject("store"));
        assertNotNull(statsJson.getAsJsonObject("indexing"));
        assertNotNull(statsJson.getAsJsonObject("get"));
        assertNotNull(statsJson.getAsJsonObject("search"));
    }

    @Test
    public void testStatsWithOptions() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        createIndex(StatsIntegrationTest.STATS_WITH_OPTIONS_INDEX_NAME);
        ensureSearchable(StatsIntegrationTest.STATS_WITH_OPTIONS_INDEX_NAME);
        Stats stats = new Stats.Builder().flush(true).indexing(true).build();
        JestResult result = client.execute(stats);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        // Confirm that response has only flush and indexing stats types
        JsonObject jsonResult = result.getJsonObject();
        JsonObject statsJson = jsonResult.getAsJsonObject("indices").getAsJsonObject(StatsIntegrationTest.STATS_WITH_OPTIONS_INDEX_NAME).getAsJsonObject("total");
        assertNotNull(statsJson);
        assertNotNull(statsJson.getAsJsonObject("flush"));
        assertNotNull(statsJson.getAsJsonObject("indexing"));
        assertEquals("Number of stats received", 2, statsJson.entrySet().size());
    }
}

