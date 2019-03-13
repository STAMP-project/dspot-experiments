package io.searchbox.indices;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
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
public class IndicesExistsIntegrationTest extends AbstractIntegrationTest {
    static final String INDEX_1_NAME = "osman";

    static final String INDEX_2_NAME = "john";

    @Test
    public void multiIndexNotExists() throws IOException {
        Action action = new IndicesExists.Builder("qwe").addIndex("asd").build();
        JestResult result = client.execute(action);
        assertFalse(result.isSucceeded());
    }

    @Test
    public void multiIndexExists() throws IOException {
        Action action = new IndicesExists.Builder(IndicesExistsIntegrationTest.INDEX_1_NAME).addIndex(IndicesExistsIntegrationTest.INDEX_2_NAME).build();
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void indexExists() throws IOException {
        Action action = new IndicesExists.Builder(IndicesExistsIntegrationTest.INDEX_1_NAME).build();
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void indexNotExists() throws IOException {
        Action action = new IndicesExists.Builder("nope").build();
        JestResult result = client.execute(action);
        assertFalse(result.isSucceeded());
    }
}

