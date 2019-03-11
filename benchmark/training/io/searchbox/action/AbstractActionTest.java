package io.searchbox.action;


import ElasticsearchVersion.UNKNOWN;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.searchbox.annotations.JestId;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Update;
import io.searchbox.indices.Flush;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class AbstractActionTest {
    @Test
    public void buildRestUrlWithValidParameters() {
        String expected = "twitter/tweet/1";
        final Delete build = new Delete.Builder("1").index("twitter").type("tweet").build();
        String actual = build.buildURI(UNKNOWN);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void buildUrlWithRequestParameterWithMultipleValues() {
        AbstractActionTest.DummyAction dummyAction = setParameter("w", "p").build();
        Assert.assertEquals("?x=y&x=z&x=q&w=p", dummyAction.getURI(UNKNOWN));
    }

    @Test
    public void testEqualsAndHashcode() {
        Action dummyAction1 = setParameter("x", "z").setHeader("X-Custom-Header", "hatsune").build();
        Action dummyAction2 = setParameter("x", "z").setHeader("X-Custom-Header", "hatsune").build();
        Action dummyAction3 = setParameter("x", "z").setHeader("X-Custom_Header", "hatsune").build();
        Action flush = new Flush.Builder().build();
        Assert.assertTrue(dummyAction1.equals(dummyAction2));
        Assert.assertTrue(dummyAction2.equals(dummyAction1));
        Assert.assertEquals(dummyAction1, dummyAction2);
        Assert.assertEquals(dummyAction1.hashCode(), dummyAction2.hashCode());
        Assert.assertFalse(dummyAction3.equals(dummyAction1));
        Assert.assertFalse(dummyAction3.equals(dummyAction2));
        Assert.assertFalse(dummyAction1.equals(dummyAction3));
        Assert.assertFalse(dummyAction2.equals(dummyAction3));
        Assert.assertNotEquals(dummyAction1.hashCode(), dummyAction3.hashCode());
        Assert.assertNotEquals(dummyAction2.hashCode(), dummyAction3.hashCode());
        Assert.assertFalse(dummyAction1.equals(flush));
        Assert.assertFalse(dummyAction2.equals(flush));
        Assert.assertFalse(dummyAction3.equals(flush));
        Assert.assertNotEquals(dummyAction1.hashCode(), flush.hashCode());
        Assert.assertNotEquals(dummyAction2.hashCode(), flush.hashCode());
        Assert.assertNotEquals(dummyAction3.hashCode(), flush.hashCode());
    }

    @Test
    public void restMethodNameMultipleClientRequest() {
        Get get = new Get.Builder("twitter", "1").type("tweet").build();
        Assert.assertEquals("GET", get.getRestMethodName());
        Delete del = new Delete.Builder("1").index("twitter").type("tweet").build();
        Assert.assertEquals("DELETE", del.getRestMethodName());
    }

    @Test
    public void requestDataMultipleClientRequest() {
        Index indexDocument = new Index.Builder("\"indexDocumentData\"").index("index").type("type").id("id").build();
        Update update = new Update.Builder("\"updateData\"").index("indexName").type("indexType").id("1").build();
        Assert.assertEquals("\"updateData\"", update.getData(null).toString());
        Assert.assertEquals("POST", update.getRestMethodName());
        Assert.assertEquals("indexName/indexType/1/_update", update.getURI(UNKNOWN));
        Assert.assertEquals("\"indexDocumentData\"", indexDocument.getData(null).toString());
        Assert.assertEquals("PUT", indexDocument.getRestMethodName());
        Assert.assertEquals("index/type/id", indexDocument.getURI(UNKNOWN));
    }

    @Test
    public void getIdFromNullSource() {
        Assert.assertNull(AbstractAction.getIdFromSource(null));
    }

    @Test
    public void getIdFromSourceWithoutAnnotation() {
        Assert.assertNull(AbstractAction.getIdFromSource("JEST"));
    }

    @Test
    public void getIdFromSourceWithAnnotation() {
        String expected = "jest@searchbox.io";
        String actual = AbstractAction.getIdFromSource(new AbstractActionTest.Source("data", "jest@searchbox.io"));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getIdFromSourceWithAnnotationWithNullId() {
        Assert.assertNull(AbstractAction.getIdFromSource(new AbstractActionTest.Source("data", null)));
    }

    static class DummyAction extends GenericResultAbstractAction {
        public DummyAction(AbstractActionTest.DummyAction.Builder builder) {
            super(builder);
        }

        @Override
        public String getRestMethodName() {
            return "GET";
        }

        public static class Builder extends AbstractAction.Builder<AbstractActionTest.DummyAction, AbstractActionTest.DummyAction.Builder> {
            @Override
            public AbstractActionTest.DummyAction build() {
                return new AbstractActionTest.DummyAction(this);
            }
        }
    }

    @Test
    public void convertJsonStringToMapObject() {
        String json = "{\n" + (((("    \"ok\" : true,\n" + "    \"_index\" : \"twitter\",\n") + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\"\n") + "}");
        JsonObject jsonMap = new AbstractActionTest.DummyAction.Builder().build().parseResponseBody(json);
        Assert.assertNotNull(jsonMap);
        Assert.assertEquals(4, jsonMap.entrySet().size());
        Assert.assertEquals(true, jsonMap.get("ok").getAsBoolean());
        Assert.assertEquals("twitter", jsonMap.get("_index").getAsString());
        Assert.assertEquals("tweet", jsonMap.get("_type").getAsString());
        Assert.assertEquals("1", jsonMap.get("_id").getAsString());
    }

    @Test
    public void convertEmptyJsonStringToMapObject() {
        JsonObject jsonMap = new AbstractActionTest.DummyAction.Builder().build().parseResponseBody("");
        Assert.assertNotNull(jsonMap);
    }

    @Test
    public void convertNullJsonStringToMapObject() {
        JsonObject jsonMap = new AbstractActionTest.DummyAction.Builder().build().parseResponseBody(null);
        Assert.assertNotNull(jsonMap);
    }

    @Test(expected = JsonSyntaxException.class)
    public void propagateExceptionWhenTheResponseIsNotJson1() {
        parseResponseBody("401 Unauthorized");
    }

    @Test(expected = JsonSyntaxException.class)
    public void propagateExceptionWhenTheResponseIsNotJson2() {
        parseResponseBody("banana");
    }

    @Test
    public void getSuccessIndexResult() {
        String jsonString = "{\n" + (((("    \"ok\" : true,\n" + "    \"_index\" : \"twitter\",\n") + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\"\n") + "}\n");
        Index index = new Index.Builder("{\"abc\":\"dce\"}").index("test").build();
        JestResult result = index.createNewElasticSearchResult(jsonString, 200, null, new Gson());
        Assert.assertTrue(result.getErrorMessage(), result.isSucceeded());
        Assert.assertEquals(200, result.getResponseCode());
    }

    @Test
    public void getFailedIndexResult() {
        String jsonString = "{\"error\":\"Invalid index\",\"status\":400}";
        Index index = new Index.Builder("{\"abc\":\"dce\"}").index("test").build();
        JestResult result = index.createNewElasticSearchResult(jsonString, 400, null, new Gson());
        Assert.assertFalse(result.isSucceeded());
        Assert.assertEquals("\"Invalid index\"", result.getErrorMessage());
    }

    @Test
    public void getSuccessDeleteResult() {
        String jsonString = "{\n" + ((((("    \"ok\" : true,\n" + "    \"_index\" : \"twitter\",\n") + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\",\n") + "    \"found\" : true\n") + "}\n");
        Delete delete = new Delete.Builder("1").index("twitter").type("tweet").build();
        JestResult result = delete.createNewElasticSearchResult(jsonString, 200, null, new Gson());
        Assert.assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    // TODO: This cannot be derived from the result anymore
    @Test
    public void getFailedDeleteResult() {
        String jsonString = "{\n" + (((("    \"_index\" : \"twitter\",\n" + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\",\n") + "    \"found\" : false\n") + "}\n");
        Delete delete = new Delete.Builder("1").index("test").type("tweet").build();
        JestResult result = delete.createNewElasticSearchResult(jsonString, 404, null, new Gson());
        Assert.assertFalse(result.isSucceeded());
    }

    @Test
    public void getSuccessGetResult() {
        String jsonString = "{" + (((("    \"_index\" : \"twitter\"," + "    \"_type\" : \"tweet\",") + "    \"_id\" : \"1\",") + "    \"exists\" : true") + "}");
        Get get = new Get.Builder("test", "1").build();
        JestResult result = get.createNewElasticSearchResult(jsonString, 200, null, new Gson());
        Assert.assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    class Source {
        @JestId
        String email;

        String data;

        Source(String data, String email) {
            this.data = data;
            this.email = email;
        }
    }
}

