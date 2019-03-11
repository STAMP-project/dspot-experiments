package io.searchbox.core;


import DocWriteResponse.Result.CREATED;
import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;


/**
 *
 *
 * @author Lior Knaany
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class UpdateByQueryIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX = "twitter";

    private static final String TYPE = "tweet";

    @Test
    public void update() throws IOException, InterruptedException {
        // create a tweet
        assertTrue(index(UpdateByQueryIntegrationTest.INDEX, UpdateByQueryIntegrationTest.TYPE, "1", "{\"user\":\"lior\",\"num\":1}").getResult().equals(CREATED));
        assertTrue(index(UpdateByQueryIntegrationTest.INDEX, UpdateByQueryIntegrationTest.TYPE, "2", "{\"user\":\"kimchy\",\"num\":2}").getResult().equals(CREATED));
        refresh();
        ensureSearchable(UpdateByQueryIntegrationTest.INDEX);
        // run the search and update
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("user", "lior"));
        final String script = "ctx._source.user = ctx._source.user + '_updated';";
        final XContentBuilder xContentBuilder = jsonBuilder().startObject().field("query", queryBuilder).startObject("script").field("inline", script).endObject().endObject();
        xContentBuilder.flush();
        final String payload = ((ByteArrayOutputStream) (xContentBuilder.getOutputStream())).toString("UTF-8");
        UpdateByQuery updateByQuery = new UpdateByQuery.Builder(payload).addIndex(UpdateByQueryIntegrationTest.INDEX).addType(UpdateByQueryIntegrationTest.TYPE).build();
        UpdateByQueryResult result = client.execute(updateByQuery);
        // Checks
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertFalse(result.didTimeOut());
        assertEquals(0, result.getConflictsCount());
        assertTrue(((result.getMillisTaken()) > 0));
        assertEquals(1, result.getUpdatedCount());
        assertEquals(0, result.getRetryCount());
        assertEquals(0, result.getBulkRetryCount());
        assertEquals(0, result.getSearchRetryCount());
        assertEquals(0, result.getNoopCount());
        assertEquals(0, result.getFailures().size());
    }
}

