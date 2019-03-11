package io.searchbox.core;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import com.google.common.collect.ImmutableMap;
import io.searchbox.client.AbstractJestClient;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.elasticsearch.action.get.GetResponse;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class IndexIntegrationTest extends AbstractIntegrationTest {
    static final String INDEX = "twitter";

    static final String TYPE = "tweet";

    @Test
    public void indexDocumentWithValidParametersAndWithoutSettings() throws IOException {
        String id = "1000";
        Map<String, String> source = ImmutableMap.of("test_name", "indexDocumentWithValidParametersAndWithoutSettings");
        DocumentResult result = client.execute(new Index.Builder(source).index(IndexIntegrationTest.INDEX).type(IndexIntegrationTest.TYPE).id(id).refresh(true).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(IndexIntegrationTest.INDEX, result.getIndex());
        assertEquals(IndexIntegrationTest.TYPE, result.getType());
        assertEquals(id, result.getId());
        GetResponse getResponse = get(IndexIntegrationTest.INDEX, IndexIntegrationTest.TYPE, id);
        assertTrue(getResponse.isExists());
        assertFalse(getResponse.isSourceEmpty());
        assertEquals(source, getResponse.getSource());
    }

    @Test
    public void automaticIdGeneration() throws IOException {
        Map<String, String> source = ImmutableMap.of("test_name", "automaticIdGeneration");
        DocumentResult result = client.execute(new Index.Builder(source).index(IndexIntegrationTest.INDEX).type(IndexIntegrationTest.TYPE).refresh(true).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        String id = result.getId();
        GetResponse getResponse = get(IndexIntegrationTest.INDEX, IndexIntegrationTest.TYPE, id);
        assertTrue(getResponse.isExists());
        assertFalse(getResponse.isSourceEmpty());
        assertEquals(source, getResponse.getSource());
    }

    @Test
    public void indexDocumentWithDateField() throws Exception {
        SimpleDateFormat defaultDateFormat = new SimpleDateFormat(AbstractJestClient.ELASTIC_SEARCH_DATE_FORMAT);
        Date creationDate = new Date(1356998400000L);// Tue, 01 Jan 2013 00:00:00 GMT

        String id = "1008";
        Map source = ImmutableMap.of("user", "jest", "creationDate", creationDate);
        String mapping = "{ \"properties\" : { \"creationDate\" : {\"type\" : \"date\"} } }";
        createIndex(IndexIntegrationTest.INDEX);
        assertTrue(client().admin().indices().putMapping(new org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest(IndexIntegrationTest.INDEX).type(IndexIntegrationTest.TYPE).source(mapping, XContentType.JSON)).actionGet().isAcknowledged());
        assertConcreteMappingsOnAll(IndexIntegrationTest.INDEX, IndexIntegrationTest.TYPE, "creationDate");
        DocumentResult result = client.execute(new Index.Builder(source).index(IndexIntegrationTest.INDEX).type(IndexIntegrationTest.TYPE).id(id).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        GetResponse getResponse = get(IndexIntegrationTest.INDEX, IndexIntegrationTest.TYPE, id);
        assertTrue(getResponse.isExists());
        assertFalse(getResponse.isSourceEmpty());
        Map actualSource = getResponse.getSource();
        assertEquals("jest", actualSource.get("user"));
        assertEquals(creationDate, defaultDateFormat.parse(((String) (actualSource.get("creationDate")))));
    }
}

