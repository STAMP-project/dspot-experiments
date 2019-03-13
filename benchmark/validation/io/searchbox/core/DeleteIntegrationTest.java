package io.searchbox.core;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class DeleteIntegrationTest extends AbstractIntegrationTest {
    public static final String INDEX = "twitter";

    public static final String TYPE = "tweet";

    public static final String ID = "1";

    @Test
    public void deleteNonExistingDocument() throws IOException {
        DocumentResult result = client.execute(new Delete.Builder(DeleteIntegrationTest.ID).index(DeleteIntegrationTest.INDEX).type(DeleteIntegrationTest.TYPE).id(DeleteIntegrationTest.ID).build());
        assertFalse(result.isSucceeded());
        assertEquals(DeleteIntegrationTest.INDEX, result.getIndex());
        assertEquals("tweet", result.getType());
        assertEquals(DeleteIntegrationTest.ID, result.getId());
    }

    @Test
    public void deleteDocumentAsynchronously() throws IOException, InterruptedException, ExecutionException {
        client.executeAsync(new Delete.Builder(DeleteIntegrationTest.ID).index(DeleteIntegrationTest.INDEX).type(DeleteIntegrationTest.TYPE).build(), new io.searchbox.client.JestResultHandler<DocumentResult>() {
            @Override
            public void completed(DocumentResult result) {
                assertFalse(result.isSucceeded());
            }

            @Override
            public void failed(Exception ex) {
                fail("failed during the asynchronous calling");
            }
        });
        Thread.sleep(500);
    }

    @Test
    public void deleteRealDocument() throws IOException {
        Index index = new Index.Builder("{\"user\":\"kimchy\"}").index("cvbank").type("candidate").id(DeleteIntegrationTest.ID).refresh(true).build();
        client.execute(index);
        DocumentResult result = client.execute(new Delete.Builder(DeleteIntegrationTest.ID).index("cvbank").type("candidate").build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals("cvbank", result.getIndex());
        assertEquals("candidate", result.getType());
        assertEquals(DeleteIntegrationTest.ID, result.getId());
    }
}

