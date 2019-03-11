package org.testcontainers.elasticsearch;


import java.io.IOException;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ElasticsearchContainerTest {
    /**
     * Elasticsearch default username, when secured with a license > basic
     */
    private static final String ELASTICSEARCH_USERNAME = "elastic";

    /**
     * Elasticsearch 5.x default password. In 6.x images, there's no security by default as shipped with a basic license.
     */
    private static final String ELASTICSEARCH_PASSWORD = "changeme";

    private RestClient client = null;

    @Test
    public void elasticsearchDefaultTest() throws IOException {
        // dummy env for compiler checking correct generics usage
        try (ElasticsearchContainer container = new ElasticsearchContainer().withEnv("foo", "bar")) {
            container.start();
            Response response = getClient(container).performRequest(new Request("GET", "/"));
            MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            MatcherAssert.assertThat(EntityUtils.toString(response.getEntity()), CoreMatchers.containsString(ElasticsearchContainer.ELASTICSEARCH_DEFAULT_VERSION));
            // The default image is running with the features under Elastic License
            response = getClient(container).performRequest(new Request("GET", "/_xpack/"));
            MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            // For now we test that we have the monitoring feature available
            MatcherAssert.assertThat(EntityUtils.toString(response.getEntity()), CoreMatchers.containsString("monitoring"));
        }
    }

    @Test
    public void elasticsearchVersion() throws IOException {
        try (ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:5.6.12")) {
            container.start();
            Response response = getClient(container).performRequest(new Request("GET", "/"));
            MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            String responseAsString = EntityUtils.toString(response.getEntity());
            MatcherAssert.assertThat(responseAsString, CoreMatchers.containsString("5.6.12"));
        }
    }

    @Test
    public void elasticsearchOssImage() throws IOException {
        try (ElasticsearchContainer container = new ElasticsearchContainer(("docker.elastic.co/elasticsearch/elasticsearch-oss:" + (ElasticsearchContainer.ELASTICSEARCH_DEFAULT_VERSION)))) {
            container.start();
            Response response = getClient(container).performRequest(new Request("GET", "/"));
            MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            // The OSS image does not have any feature under Elastic License
            assertThrows("We should not have /_xpack endpoint with an OSS License", ResponseException.class, () -> getClient(container).performRequest(new Request("GET", "/_xpack/")));
        }
    }

    @Test
    public void transportClientClusterHealth() {
        try (ElasticsearchContainer container = new ElasticsearchContainer()) {
            container.start();
            TransportAddress transportAddress = new TransportAddress(container.getTcpHost());
            String expectedClusterName = "docker-cluster";
            Settings settings = Settings.builder().put("cluster.name", expectedClusterName).build();
            try (TransportClient transportClient = new org.elasticsearch.transport.client.PreBuiltTransportClient(settings).addTransportAddress(transportAddress)) {
                ClusterHealthResponse healths = transportClient.admin().cluster().prepareHealth().get();
                String clusterName = healths.getClusterName();
                MatcherAssert.assertThat(clusterName, CoreMatchers.is(expectedClusterName));
            }
        }
    }
}

