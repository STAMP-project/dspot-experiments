package org.stagemonitor.core.elasticsearch;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.AbstractElasticsearchTest;
import org.stagemonitor.core.util.JsonUtils;


public class ElasticsearchClientTest extends AbstractElasticsearchTest {
    private ListAppender<ILoggingEvent> testAppender;

    @Test
    public void testGetDashboardForElasticsearch() throws Exception {
        String expected = "{\"user\":\"guest\",\"group\":\"guest\",\"title\":\"Test Title\",\"tags\":[\"jdbc\",\"db\"],\"dashboard\":\"{\\\"title\\\":\\\"Test Title\\\",\\\"editable\\\":false,\\\"failover\\\":false,\\\"panel_hints\\\":true,\\\"style\\\":\\\"dark\\\",\\\"refresh\\\":\\\"1m\\\",\\\"tags\\\":[\\\"jdbc\\\",\\\"db\\\"],\\\"timezone\\\":\\\"browser\\\"}\"}";
        Assert.assertEquals(expected, AbstractElasticsearchTest.elasticsearchClient.getDashboardForElasticsearch("Test Dashboard.json").toString());
    }

    @Test
    public void testRequireBoxTypeHotWhenHotColdActive() throws Exception {
        final String indexTemplate = ElasticsearchClient.modifyIndexTemplate("stagemonitor-elasticsearch-metrics-index-template.json", 2, null, 0);
        Assert.assertTrue(indexTemplate.contains("hot"));
        Assert.assertFalse(indexTemplate.contains("number_of_shards"));
        Assert.assertFalse(indexTemplate.contains("number_of_replicas"));
    }

    @Test
    public void testSetReplicas() throws Exception {
        final String indexTemplate = ElasticsearchClient.modifyIndexTemplate("stagemonitor-elasticsearch-metrics-index-template.json", 0, 0, 0);
        Assert.assertFalse(indexTemplate.contains("hot"));
        Assert.assertEquals(0, JsonUtils.getMapper().readTree(indexTemplate).get("settings").get("index").get("number_of_replicas").asInt());
        Assert.assertFalse(indexTemplate.contains("number_of_shards"));
    }

    @Test
    public void testSetShards() throws Exception {
        final String indexTemplate = ElasticsearchClient.modifyIndexTemplate("stagemonitor-elasticsearch-metrics-index-template.json", 0, (-1), 1);
        Assert.assertFalse(indexTemplate.contains("hot"));
        Assert.assertEquals(1, JsonUtils.getMapper().readTree(indexTemplate).get("settings").get("index").get("number_of_shards").asInt());
        Assert.assertFalse(indexTemplate.contains("number_of_replicas"));
    }

    @Test
    public void modifyIndexTemplateIntegrationTest() throws Exception {
        AbstractElasticsearchTest.elasticsearchClient.sendMappingTemplate(ElasticsearchClient.modifyIndexTemplate("stagemonitor-elasticsearch-metrics-index-template.json", 0, 1, 2), "stagemonitor-elasticsearch-metrics");
        AbstractElasticsearchTest.elasticsearchClient.waitForCompletion();
        AbstractElasticsearchTest.refresh();
        AbstractElasticsearchTest.elasticsearchClient.createEmptyIndex("stagemonitor-metrics-test");
        AbstractElasticsearchTest.elasticsearchClient.waitForCompletion();
        AbstractElasticsearchTest.refresh();
        final JsonNode indexSettings = AbstractElasticsearchTest.elasticsearchClient.getJson("/stagemonitor-metrics-test/_settings").get("stagemonitor-metrics-test").get("settings").get("index");
        Assert.assertEquals(indexSettings.toString(), 1, indexSettings.get("number_of_replicas").asInt());
        Assert.assertEquals(indexSettings.toString(), 2, indexSettings.get("number_of_shards").asInt());
    }

    @Test
    public void testDontRequireBoxTypeHotWhenHotColdInactive() throws Exception {
        Assert.assertFalse(ElasticsearchClient.modifyIndexTemplate("stagemonitor-elasticsearch-metrics-index-template.json", 0, 0, 0).contains("hot"));
        Assert.assertFalse(ElasticsearchClient.modifyIndexTemplate("stagemonitor-elasticsearch-metrics-index-template.json", (-1), 0, 0).contains("hot"));
    }

    @Test
    public void testBulkNoRequest() {
        AbstractElasticsearchTest.elasticsearchClient.sendBulk(( os) -> os.write("".getBytes("UTF-8")));
        assertThat(testAppender.list).hasSize(1);
        final ILoggingEvent event = testAppender.list.get(0);
        assertThat(event.getLevel().toString()).isEqualTo("WARN");
        assertThat(event.getMessage()).startsWith("Error(s) while sending a _bulk request to elasticsearch: {}");
    }

    @Test
    public void testBulkErrorInvalidRequest() {
        AbstractElasticsearchTest.elasticsearchClient.sendBulk(( os) -> os.write(("{ \"update\" : {\"_id\" : \"1\", \"_type\" : \"type1\", \"_index\" : \"index1\", \"_retry_on_conflict\" : 3} }\n" + "{ \"doc\" : {\"field\" : \"value\"} }\n").getBytes("UTF-8")));
        assertThat(testAppender.list).hasSize(1);
        final ILoggingEvent event = testAppender.list.get(0);
        assertThat(event.getLevel().toString()).isEqualTo("WARN");
        assertThat(event.getMessage()).startsWith("Error(s) while sending a _bulk request to elasticsearch: {}");
    }

    @Test
    public void testSuccessfulBulkRequest() {
        AbstractElasticsearchTest.elasticsearchClient.sendBulk(( os) -> os.write(("{ \"index\" : { \"_index\" : \"test\", \"_type\" : \"type1\", \"_id\" : \"1\" } }\n" + "{ \"field1\" : \"value1\" }\n").getBytes("UTF-8")));
        assertThat(testAppender.list).hasSize(0);
    }

    @Test
    public void testCountBulkErrors() {
        AtomicInteger errors = new AtomicInteger();
        AbstractElasticsearchTest.elasticsearchClient.getHttpClient().send("POST", ((AbstractElasticsearchTest.elasticsearchUrl) + "/_bulk"), ElasticsearchClient.CONTENT_TYPE_NDJSON, ( os) -> os.write(("{ \"index\" : { \"_index\" : \"test\", \"_type\" : \"type1\", \"_id\" : \"1\" } }\n" + (("{ \"field1\" : \"value1\" }\n" + "{ \"update\" : {\"_id\" : \"42\", \"_type\" : \"type1\", \"_index\" : \"index1\"} }\n") + "{ \"doc\" : {\"field\" : \"value\"} }\n")).getBytes("UTF-8")), new ElasticsearchClient.BulkErrorCountingResponseHandler() {
            @Override
            public void onBulkError(int errorCount) {
                errors.set(errorCount);
            }
        });
        assertThat(errors.get()).isEqualTo(1);
    }

    @Test
    public void testCreateEmptyIndex() throws Exception {
        AbstractElasticsearchTest.elasticsearchClient.createEmptyIndex("test");
        assertThat(AbstractElasticsearchTest.elasticsearchClient.getJson("/test").get("test")).isNotNull();
    }
}

