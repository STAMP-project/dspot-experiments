package org.stagemonitor.configuration.source;


import java.util.UUID;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stagemonitor.AbstractElasticsearchTest;
import org.stagemonitor.core.configuration.ElasticsearchConfigurationSource;
import org.stagemonitor.junit.ConditionalTravisTestRunner;
import org.stagemonitor.junit.ExcludeOnTravis;


@RunWith(ConditionalTravisTestRunner.class)
public class ElasticsearchConfigurationSourceTest extends AbstractElasticsearchTest {
    private ElasticsearchConfigurationSource configurationSource;

    @Test
    public void testSaveAndGet() throws Exception {
        configurationSource.save("foo.bar", "baz");
        AbstractElasticsearchTest.refresh();
        configurationSource.reload();
        Assert.assertEquals("baz", configurationSource.getValue("foo.bar"));
    }

    @Test
    public void testGetFromNonExistingConfigurationProfile() throws Exception {
        configurationSource = new ElasticsearchConfigurationSource(AbstractElasticsearchTest.elasticsearchClient, UUID.randomUUID().toString());
        configurationSource.reload();
        assertThat(configurationSource.getValue("foo")).isNull();
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals("Elasticsearch (test)", configurationSource.getName());
    }

    @Test
    public void testIsSavingPersistent() throws Exception {
        Assert.assertTrue(configurationSource.isSavingPersistent());
    }

    @Test
    public void testIsSavingPossible() throws Exception {
        Assert.assertTrue(configurationSource.isSavingPossible());
    }

    @Test
    @ExcludeOnTravis
    public void testMapping() throws Exception {
        configurationSource.save("foo", "bar");
        AbstractElasticsearchTest.refresh();
        final GetMappingsResponse mappings = AbstractElasticsearchTest.client.admin().indices().prepareGetMappings("stagemonitor-configuration").setTypes("configuration").get();
        Assert.assertEquals(1, mappings.getMappings().size());
        Assert.assertEquals(("{\"configuration\":{" + ((((("\"_all\":{\"enabled\":false}," + "\"properties\":{\"configuration\":{\"properties\":{") + "\"key\":{\"type\":\"keyword\"},") + "\"value\":{\"type\":\"keyword\"}}}}") + "}") + "}")), mappings.getMappings().get("stagemonitor-configuration").get("configuration").source().toString());
    }
}

