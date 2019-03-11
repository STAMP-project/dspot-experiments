package io.searchbox.core;


import DocWriteResponse.Result.CREATED;
import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import org.junit.FixMethodOrder;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
@ClusterScope(scope = Scope.TEST, numDataNodes = 1)
@FixMethodOrder
public class CountIntegrationTest extends AbstractIntegrationTest {
    private static final double DELTA = 1.0E-15;

    private static final String INDEX_1 = "cvbank";

    private static final String INDEX_2 = "office_docs";

    @Test
    public void countWithMultipleIndices() throws IOException {
        String query = "{\n" + ((("    \"query\" : {\n" + "        \"term\" : { \"user\" : \"kimchy\" }\n") + "    }\n") + "}");
        ensureSearchable(CountIntegrationTest.INDEX_1, CountIntegrationTest.INDEX_2);
        CountResult result = client.execute(new Count.Builder().query(query).addIndex(CountIntegrationTest.INDEX_1).addIndex(CountIntegrationTest.INDEX_2).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(0.0, result.getCount(), CountIntegrationTest.DELTA);
        assertEquals("0", result.getSourceAsString());
    }

    @Test
    public void countWithValidTermQueryOnAllIndices() throws IOException {
        String query = "{\n" + ((("    \"query\" : {\n" + "        \"term\" : { \"user\" : \"kimchy\" }\n") + "    }\n") + "}");
        ensureSearchable(CountIntegrationTest.INDEX_1, CountIntegrationTest.INDEX_2);
        CountResult result = client.execute(new Count.Builder().query(query).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(0.0, result.getCount(), CountIntegrationTest.DELTA);
        assertEquals("0", result.getSourceAsString());
    }

    @Test
    public void countWithValidTermQueryOnSingleIndex() throws IOException {
        String type = "candidate";
        String query = "{\n" + ((("    \"query\" : {\n" + "        \"term\" : { \"user\" : \"kimchy\" }\n") + "    }\n") + "}");
        assertTrue(index(CountIntegrationTest.INDEX_1, type, "aaa1", "{ \"user\":\"kimchy\" }").getResult().equals(CREATED));
        refresh();
        ensureSearchable(CountIntegrationTest.INDEX_1);
        Count count = new Count.Builder().query(query).addIndex(CountIntegrationTest.INDEX_1).addType("candidate").build();
        CountResult result = client.execute(count);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1.0, result.getCount(), CountIntegrationTest.DELTA);
        assertEquals("1", result.getSourceAsString());
    }
}

