package io.searchbox.core;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.annotations.JestId;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class GetIntegrationTest extends AbstractIntegrationTest {
    static final String INDEX = "twitter";

    static final String TYPE = "tweet";

    @Test
    public void getWithSpecialCharacterInDocId() throws IOException, JSONException {
        final String indexName = "trial";
        final String typeName = "doc";
        final String documentId = "asd%2fqwe";// asd/qwe -> (encode) -> asd%2fqwe

        IndexResponse indexResponse = client().index(new IndexRequest(indexName, typeName, "asd/qwe").source("user", "tweety")).actionGet();
        assertNotNull(indexResponse);
        refresh();
        ensureSearchable(indexName);
        DocumentResult result = client.execute(new Get.Builder(indexName, documentId).type(typeName).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(indexName, result.getIndex());
        assertEquals(typeName, result.getType());
        assertEquals("asd/qwe", result.getId());
        JSONAssert.assertEquals("{\"user\":\"tweety\"}", result.getSourceAsString(), false);
    }

    @Test
    public void getAsClass() throws IOException, JSONException {
        String id = "900";
        String message = "checkout my lunch guys!";
        GetIntegrationTest.Tweet expectedTweet = new GetIntegrationTest.Tweet();
        expectedTweet.setUserHash(id);
        expectedTweet.setMessage(message);
        DocumentResult result = client.execute(new Index.Builder(expectedTweet).index(GetIntegrationTest.INDEX).type(GetIntegrationTest.TYPE).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        Get get = new Get.Builder(GetIntegrationTest.INDEX, id).type(GetIntegrationTest.TYPE).build();
        result = client.execute(get);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        GetIntegrationTest.Tweet actualTweet = result.getSourceAsObject(GetIntegrationTest.Tweet.class);
        assertEquals(expectedTweet.getMessage(), actualTweet.getMessage());
        assertEquals(expectedTweet.getUserHash(), actualTweet.getUserHash());
        JSONAssert.assertEquals("{\"userHash\":\"900\",\"message\":\"checkout my lunch guys!\"}", result.getSourceAsString(), false);
    }

    @Test
    public void get() throws IOException {
        Get get = new Get.Builder(GetIntegrationTest.INDEX, "1").type(GetIntegrationTest.TYPE).build();
        DocumentResult result = client.execute(get);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void getAsynchronously() throws IOException, InterruptedException, ExecutionException {
        final CountDownLatch completionChecker = new CountDownLatch(1);
        client.executeAsync(new Get.Builder(GetIntegrationTest.INDEX, "1").type(GetIntegrationTest.TYPE).build(), new io.searchbox.client.JestResultHandler<DocumentResult>() {
            @Override
            public void completed(DocumentResult result) {
                assertTrue(result.getErrorMessage(), result.isSucceeded());
                assertEquals(GetIntegrationTest.INDEX, result.getIndex());
                assertEquals(GetIntegrationTest.TYPE, result.getType());
                assertEquals("1", result.getId());
                completionChecker.countDown();
            }

            @Override
            public void failed(Exception ex) {
                fail("failed execution of asynchronous get call");
            }
        });
        boolean finishedAsync = completionChecker.await(2, TimeUnit.SECONDS);
        if (!finishedAsync) {
            fail("Execution took to long to complete");
        }
    }

    @Test
    public void getWithType() throws Exception {
        final String id = "testid1";
        final String type = "article";
        TestArticleModel article = new TestArticleModel();
        article.setId(id);
        article.setName("Jest");
        Index index = new Index.Builder(article).index("articles").type(type).refresh(true).build();
        DocumentResult indexResult = client.execute(index);
        assertTrue(indexResult.getErrorMessage(), indexResult.isSucceeded());
        DocumentResult result = client.execute(new Get.Builder("articles", "testid1").type(type).build());
        assertEquals(type, result.getType());
        TestArticleModel articleResult = result.getSourceAsObject(TestArticleModel.class);
        assertEquals(id, articleResult.getId());
    }

    @Test
    public void getWithoutType() throws Exception {
        final String id = "testid1";
        final String type = "article";
        TestArticleModel article = new TestArticleModel();
        article.setId(id);
        article.setName("Jest");
        Index index = new Index.Builder(article).index("articles").type(type).refresh(true).build();
        DocumentResult indexResult = client.execute(index);
        assertTrue(indexResult.getErrorMessage(), indexResult.isSucceeded());
        DocumentResult result = client.execute(new Get.Builder("articles", "testid1").build());
        assertEquals(type, result.getType());
        TestArticleModel articleResult = result.getSourceAsObject(TestArticleModel.class);
        assertEquals(id, articleResult.getId());
    }

    class Tweet {
        @JestId
        String userHash;

        String message;

        public String getUserHash() {
            return userHash;
        }

        public void setUserHash(String userHash) {
            this.userHash = userHash;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

