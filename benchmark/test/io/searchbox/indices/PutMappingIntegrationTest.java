package io.searchbox.indices;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import RootObjectMapper.Builder;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.PutMapping;
import java.io.IOException;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.KeywordFieldMapper;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.mapper.RootObjectMapper;
import org.junit.Test;


/**
 *
 *
 * @author ferhat
 * @author cihat keser
 */
@ClusterScope(scope = Scope.TEST, numDataNodes = 1)
public class PutMappingIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX_NAME = "mapping_index";

    private static final String INDEX_TYPE = "document";

    @Test
    public void testPutMapping() throws IOException {
        PutMapping putMapping = new PutMapping.Builder(PutMappingIntegrationTest.INDEX_NAME, PutMappingIntegrationTest.INDEX_TYPE, "{ \"document\" : { \"properties\" : { \"message_1\" : {\"type\" : \"text\", \"store\" : \"true\"} } } }").build();
        JestResult result = client.execute(putMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testPutMappingWithDocumentMapperBuilder() throws Exception {
        RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder(PutMappingIntegrationTest.INDEX_TYPE).add(new KeywordFieldMapper.Builder("message_2").store(true));
        MapperService mapperService = getMapperService();
        DocumentMapper documentMapper = new DocumentMapper.Builder(rootObjectMapperBuilder, mapperService).build(mapperService);
        String expectedMappingSource = documentMapper.mappingSource().toString();
        PutMapping putMapping = new PutMapping.Builder(PutMappingIntegrationTest.INDEX_NAME, PutMappingIntegrationTest.INDEX_TYPE, expectedMappingSource).build();
        JestResult result = client.execute(putMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }
}

