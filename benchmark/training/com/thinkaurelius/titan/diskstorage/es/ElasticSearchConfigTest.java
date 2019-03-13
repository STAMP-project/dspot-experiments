package com.thinkaurelius.titan.diskstorage.es;


import BasicConfiguration.Restriction;
import ElasticSearchSetup.NODE;
import ElasticSearchSetup.TRANSPORT_CLIENT;
import TitanFactory.Builder;
import com.google.common.base.Joiner;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.configuration.Configuration;
import com.thinkaurelius.titan.diskstorage.configuration.ModifiableConfiguration;
import com.thinkaurelius.titan.diskstorage.configuration.backend.CommonsConfiguration;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import java.io.File;
import org.apache.commons.configuration.BaseConfiguration;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test behavior Titan ConfigOptions governing ES client setup.
 *
 * {@link ElasticSearchIndexTest#testConfiguration()} exercises legacy
 * config options using an embedded JVM-local-transport ES instance.  By contrast,
 * this class exercises the new {@link ElasticSearchIndex#INTERFACE} configuration
 * mechanism and uses a network-capable embedded ES instance.
 */
public class ElasticSearchConfigTest {
    private static final String INDEX_NAME = "escfg";

    @Test
    public void testTitanFactoryBuilder() {
        String baseDir = Joiner.on(File.separator).join("target", "es", "titanfactory_jvmlocal_ext");
        TitanFactory.Builder builder = TitanFactory.build();
        builder.set("storage.backend", "inmemory");
        builder.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.interface"), "NODE");
        builder.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.node.data"), "true");
        builder.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.node.client"), "false");
        builder.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.node.local"), "true");
        builder.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.path.data"), ((baseDir + (File.separator)) + "data"));
        builder.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.path.work"), ((baseDir + (File.separator)) + "work"));
        builder.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.path.logs"), ((baseDir + (File.separator)) + "logs"));
        TitanGraph graph = builder.open();// Must not throw an exception

        Assert.assertTrue(graph.isOpen());
        graph.close();
    }

    @Test
    public void testTransportClient() throws BackendException, InterruptedException {
        ElasticsearchRunner esr = new ElasticsearchRunner(".", "transportClient.yml");
        start();
        ModifiableConfiguration config = GraphDatabaseConfiguration.buildGraphConfiguration();
        config.set(INTERFACE, TRANSPORT_CLIENT.toString(), ElasticSearchConfigTest.INDEX_NAME);
        config.set(INDEX_HOSTS, new String[]{ "127.0.0.1" }, ElasticSearchConfigTest.INDEX_NAME);
        Configuration indexConfig = config.restrictTo(ElasticSearchConfigTest.INDEX_NAME);
        IndexProvider idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);
        simpleWriteAndQuery(idx);
        idx.close();
        config = GraphDatabaseConfiguration.buildGraphConfiguration();
        config.set(INTERFACE, TRANSPORT_CLIENT.toString(), ElasticSearchConfigTest.INDEX_NAME);
        config.set(INDEX_HOSTS, new String[]{ "10.11.12.13" }, ElasticSearchConfigTest.INDEX_NAME);
        indexConfig = config.restrictTo(ElasticSearchConfigTest.INDEX_NAME);
        Throwable failure = null;
        try {
            idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);
        } catch (Throwable t) {
            failure = t;
        }
        // idx.close();
        Assert.assertNotNull("ES client failed to throw exception on connection failure", failure);
        stop();
    }

    @Test
    public void testLocalNodeUsingExt() throws BackendException, InterruptedException {
        String baseDir = Joiner.on(File.separator).join("target", "es", "jvmlocal_ext");
        Assert.assertFalse(new File(((baseDir + (File.separator)) + "data")).exists());
        CommonsConfiguration cc = new CommonsConfiguration(new BaseConfiguration());
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.node.data"), "true");
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.node.client"), "false");
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.node.local"), "true");
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.path.data"), ((baseDir + (File.separator)) + "data"));
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.path.work"), ((baseDir + (File.separator)) + "work"));
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.path.logs"), ((baseDir + (File.separator)) + "logs"));
        ModifiableConfiguration config = new ModifiableConfiguration(GraphDatabaseConfiguration.ROOT_NS, cc, Restriction.NONE);
        config.set(INTERFACE, NODE.toString(), ElasticSearchConfigTest.INDEX_NAME);
        Configuration indexConfig = config.restrictTo(ElasticSearchConfigTest.INDEX_NAME);
        IndexProvider idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);
        simpleWriteAndQuery(idx);
        idx.close();
        Assert.assertTrue(new File(((baseDir + (File.separator)) + "data")).exists());
    }

    @Test
    public void testLocalNodeUsingExtAndIndexDirectory() throws BackendException, InterruptedException {
        String baseDir = Joiner.on(File.separator).join("target", "es", "jvmlocal_ext2");
        Assert.assertFalse(new File(((baseDir + (File.separator)) + "data")).exists());
        CommonsConfiguration cc = new CommonsConfiguration(new BaseConfiguration());
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.node.data"), "true");
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.node.client"), "false");
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.node.local"), "true");
        ModifiableConfiguration config = new ModifiableConfiguration(GraphDatabaseConfiguration.ROOT_NS, cc, Restriction.NONE);
        config.set(INTERFACE, NODE.toString(), ElasticSearchConfigTest.INDEX_NAME);
        config.set(INDEX_DIRECTORY, baseDir, ElasticSearchConfigTest.INDEX_NAME);
        Configuration indexConfig = config.restrictTo(ElasticSearchConfigTest.INDEX_NAME);
        IndexProvider idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);
        simpleWriteAndQuery(idx);
        idx.close();
        Assert.assertTrue(new File(((baseDir + (File.separator)) + "data")).exists());
    }

    @Test
    public void testLocalNodeUsingYaml() throws BackendException, InterruptedException {
        String baseDir = Joiner.on(File.separator).join("target", "es", "jvmlocal_yml");
        Assert.assertFalse(new File(((baseDir + (File.separator)) + "data")).exists());
        ModifiableConfiguration config = GraphDatabaseConfiguration.buildGraphConfiguration();
        config.set(INTERFACE, NODE.toString(), ElasticSearchConfigTest.INDEX_NAME);
        config.set(INDEX_CONF_FILE, Joiner.on(File.separator).join("target", "test-classes", "es_jvmlocal.yml"), ElasticSearchConfigTest.INDEX_NAME);
        Configuration indexConfig = config.restrictTo(ElasticSearchConfigTest.INDEX_NAME);
        IndexProvider idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);
        simpleWriteAndQuery(idx);
        idx.close();
        Assert.assertTrue(new File(((baseDir + (File.separator)) + "data")).exists());
    }

    @Test
    public void testNetworkNodeUsingExt() throws BackendException, InterruptedException {
        ElasticsearchRunner esr = new ElasticsearchRunner(".", "networkNodeUsingExt.yml");
        start();
        CommonsConfiguration cc = new CommonsConfiguration(new BaseConfiguration());
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.node.data"), "false");
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.node.client"), "true");
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.cluster.name"), "networkNodeUsingExt");
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.discovery.zen.ping.multicast.enabled"), "false");
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.discovery.zen.ping.unicast.hosts"), "localhost,127.0.0.1:9300");
        ModifiableConfiguration config = new ModifiableConfiguration(GraphDatabaseConfiguration.ROOT_NS, cc, Restriction.NONE);
        config.set(INTERFACE, NODE.toString(), ElasticSearchConfigTest.INDEX_NAME);
        Configuration indexConfig = config.restrictTo(ElasticSearchConfigTest.INDEX_NAME);
        IndexProvider idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);
        simpleWriteAndQuery(idx);
        idx.close();
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.discovery.zen.ping.unicast.hosts"), "10.11.12.13");
        config = new ModifiableConfiguration(GraphDatabaseConfiguration.ROOT_NS, cc, Restriction.NONE);
        config.set(INTERFACE, NODE.toString(), ElasticSearchConfigTest.INDEX_NAME);
        config.set(HEALTH_REQUEST_TIMEOUT, "5s", ElasticSearchConfigTest.INDEX_NAME);
        indexConfig = config.restrictTo(ElasticSearchConfigTest.INDEX_NAME);
        Throwable failure = null;
        try {
            idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);
        } catch (Throwable t) {
            failure = t;
        }
        // idx.close();
        Assert.assertNotNull("ES client failed to throw exception on connection failure", failure);
        stop();
    }

    @Test
    public void testNetworkNodeUsingYaml() throws BackendException, InterruptedException {
        ElasticsearchRunner esr = new ElasticsearchRunner(".", "networkNodeUsingYaml.yml");
        start();
        ModifiableConfiguration config = GraphDatabaseConfiguration.buildGraphConfiguration();
        config.set(INTERFACE, NODE.toString(), ElasticSearchConfigTest.INDEX_NAME);
        config.set(INDEX_CONF_FILE, Joiner.on(File.separator).join("target", "test-classes", "es_cfg_nodeclient.yml"), ElasticSearchConfigTest.INDEX_NAME);
        Configuration indexConfig = config.restrictTo(ElasticSearchConfigTest.INDEX_NAME);
        IndexProvider idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);
        simpleWriteAndQuery(idx);
        idx.close();
        config = GraphDatabaseConfiguration.buildGraphConfiguration();
        config.set(INTERFACE, NODE.toString(), ElasticSearchConfigTest.INDEX_NAME);
        config.set(HEALTH_REQUEST_TIMEOUT, "5s", ElasticSearchConfigTest.INDEX_NAME);
        config.set(INDEX_CONF_FILE, Joiner.on(File.separator).join("target", "test-classes", "es_cfg_bogus_nodeclient.yml"), ElasticSearchConfigTest.INDEX_NAME);
        indexConfig = config.restrictTo(ElasticSearchConfigTest.INDEX_NAME);
        Throwable failure = null;
        try {
            idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);
        } catch (Throwable t) {
            failure = t;
        }
        // idx.close();
        Assert.assertNotNull("ES client failed to throw exception on connection failure", failure);
        stop();
    }

    @Test
    public void testIndexCreationOptions() throws BackendException, InterruptedException {
        final int shards = 77;
        ElasticsearchRunner esr = new ElasticsearchRunner(".", "indexCreationOptions.yml");
        start();
        CommonsConfiguration cc = new CommonsConfiguration(new BaseConfiguration());
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.create.ext.number_of_shards"), String.valueOf(shards));
        cc.set((("index." + (ElasticSearchConfigTest.INDEX_NAME)) + ".elasticsearch.ext.cluster.name"), "indexCreationOptions");
        ModifiableConfiguration config = new ModifiableConfiguration(GraphDatabaseConfiguration.ROOT_NS, cc, Restriction.NONE);
        config.set(INTERFACE, NODE.toString(), ElasticSearchConfigTest.INDEX_NAME);
        Configuration indexConfig = config.restrictTo(ElasticSearchConfigTest.INDEX_NAME);
        IndexProvider idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);
        simpleWriteAndQuery(idx);
        ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();
        settingsBuilder.put("discovery.zen.ping.multicast.enabled", "false");
        settingsBuilder.put("discovery.zen.ping.unicast.hosts", "localhost,127.0.0.1:9300");
        settingsBuilder.put("cluster.name", "indexCreationOptions");
        NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder().settings(settingsBuilder.build());
        nodeBuilder.client(true).data(false).local(false);
        Node n = nodeBuilder.build().start();
        GetSettingsResponse response = n.client().admin().indices().getSettings(new GetSettingsRequest().indices("titan")).actionGet();
        Assert.assertEquals(String.valueOf(shards), response.getSetting("titan", "index.number_of_shards"));
        idx.close();
        n.stop();
        stop();
    }
}

