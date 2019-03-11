/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.diskstorage.es;


import BasicConfiguration.Restriction;
import ElasticSearchSetup.REST_CLIENT;
import JanusGraphFactory.Builder;
import KeyInformation.IndexRetriever;
import TimestampProviders.MILLI;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.diskstorage.BackendException;
import org.janusgraph.diskstorage.BaseTransactionConfig;
import org.janusgraph.diskstorage.PermanentBackendException;
import org.janusgraph.diskstorage.configuration.Configuration;
import org.janusgraph.diskstorage.configuration.ModifiableConfiguration;
import org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration;
import org.janusgraph.diskstorage.util.StandardBaseTransactionConfig;
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


/**
 * Test behavior JanusGraph ConfigOptions governing ES client setup.
 */
@Testcontainers
public class ElasticsearchConfigTest {
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchConfigTest.class);

    @Container
    public static JanusGraphElasticsearchContainer esr = new JanusGraphElasticsearchContainer();

    private static final String INDEX_NAME = "escfg";

    private static final String ANALYZER_KEYWORD = "keyword";

    private static final String ANALYZER_ENGLISH = "english";

    private static final String ANALYZER_STANDARD = "standard";

    private HttpHost host;

    private CloseableHttpClient httpClient;

    private ObjectMapper objectMapper;

    @Test
    public void testJanusGraphFactoryBuilder() {
        final JanusGraphFactory.Builder builder = JanusGraphFactory.build();
        builder.set("storage.backend", "inmemory");
        builder.set((("index." + (ElasticsearchConfigTest.INDEX_NAME)) + ".hostname"), (((ElasticsearchConfigTest.esr.getHostname()) + ":") + (ElasticsearchConfigTest.esr.getPort())));
        final JanusGraph graph = builder.open();// Must not throw an exception

        Assertions.assertTrue(graph.isOpen());
        graph.close();
    }

    @Test
    public void testClientThrowsExceptionIfServerNotReachable() throws InterruptedException, BackendException {
        final ModifiableConfiguration config = ElasticsearchConfigTest.esr.setConfiguration(GraphDatabaseConfiguration.buildGraphConfiguration(), ElasticsearchConfigTest.INDEX_NAME);
        Configuration indexConfig = config.restrictTo(ElasticsearchConfigTest.INDEX_NAME);
        IndexProvider idx = open(indexConfig);
        simpleWriteAndQuery(idx);
        idx.close();
        config.set(INDEX_HOSTS, new String[]{ ("localhost:" + (ElasticsearchConfigTest.esr.getPort())) + 1 }, ElasticsearchConfigTest.INDEX_NAME);
        final Configuration wrongHostConfig = config.restrictTo(ElasticsearchConfigTest.INDEX_NAME);
        Assertions.assertThrows(Exception.class, () -> new ElasticSearchIndex.ElasticSearchIndex(wrongHostConfig));
    }

    @Test
    public void testIndexCreationOptions() throws IOException, InterruptedException, BackendException {
        final int shards = 7;
        final CommonsConfiguration cc = new CommonsConfiguration(new BaseConfiguration());
        cc.set((("index." + (ElasticsearchConfigTest.INDEX_NAME)) + ".elasticsearch.create.ext.number_of_shards"), String.valueOf(shards));
        final ModifiableConfiguration config = new ModifiableConfiguration(GraphDatabaseConfiguration.ROOT_NS, cc, Restriction.NONE);
        ElasticsearchConfigTest.esr.setConfiguration(config, ElasticsearchConfigTest.INDEX_NAME);
        final Configuration indexConfig = config.restrictTo(ElasticsearchConfigTest.INDEX_NAME);
        final IndexProvider idx = open(indexConfig);
        simpleWriteAndQuery(idx);
        idx.close();
        final ElasticSearchClient client = REST_CLIENT.connect(indexConfig).getClient();
        Assertions.assertEquals(String.valueOf(shards), client.getIndexSettings("janusgraph_jvmlocal_test_store").get("number_of_shards"));
        client.close();
    }

    @Test
    public void testExternalMappingsViaMapping() throws Exception {
        final Duration maxWrite = Duration.ofMillis(2000L);
        final String storeName = "test_mapping";
        final Configuration indexConfig = ElasticsearchConfigTest.esr.setConfiguration(GraphDatabaseConfiguration.buildGraphConfiguration(), ElasticsearchConfigTest.INDEX_NAME).set(USE_EXTERNAL_MAPPINGS, true, ElasticsearchConfigTest.INDEX_NAME).restrictTo(ElasticsearchConfigTest.INDEX_NAME);
        final IndexProvider idx = open(indexConfig);
        final ElasticMajorVersion version = getVersion();
        // Test that the "date" property throws an exception.
        final KeyInformation.IndexRetriever indexRetriever = IndexProviderTest.getIndexRetriever(IndexProviderTest.getMapping(idx.getFeatures(), ElasticsearchConfigTest.ANALYZER_ENGLISH, ElasticsearchConfigTest.ANALYZER_KEYWORD));
        final BaseTransactionConfig txConfig = StandardBaseTransactionConfig.of(MILLI);
        final IndexTransaction itx = new IndexTransaction(idx, indexRetriever, txConfig, maxWrite);
        try {
            idx.register(storeName, "date", IndexProviderTest.getMapping(idx.getFeatures(), ElasticsearchConfigTest.ANALYZER_ENGLISH, ElasticsearchConfigTest.ANALYZER_KEYWORD).get("date"), itx);
            Assertions.fail("should fail");
        } catch (final PermanentBackendException e) {
            ElasticsearchConfigTest.log.debug(e.getMessage(), e);
        }
        final HttpPut newMapping = new HttpPut(("janusgraph_" + storeName));
        newMapping.setEntity(new StringEntity(objectMapper.writeValueAsString(readMapping(version, "/strict_mapping.json")), Charset.forName("UTF-8")));
        executeRequest(newMapping);
        // Test that the "date" property works well.
        idx.register(storeName, "date", IndexProviderTest.getMapping(idx.getFeatures(), ElasticsearchConfigTest.ANALYZER_ENGLISH, ElasticsearchConfigTest.ANALYZER_KEYWORD).get("date"), itx);
        // Test that the "weight" property throws an exception.
        try {
            idx.register(storeName, "weight", IndexProviderTest.getMapping(idx.getFeatures(), ElasticsearchConfigTest.ANALYZER_ENGLISH, ElasticsearchConfigTest.ANALYZER_KEYWORD).get("weight"), itx);
            Assertions.fail("should fail");
        } catch (final BackendException e) {
            ElasticsearchConfigTest.log.debug(e.getMessage(), e);
        }
        itx.rollback();
        idx.close();
    }

    @Test
    public void testExternalDynamic() throws Exception {
        testExternalDynamic(false);
    }

    @Test
    public void testUpdateExternalDynamicMapping() throws Exception {
        testExternalDynamic(true);
    }

    @Test
    public void testExternalMappingsViaTemplate() throws Exception {
        final Duration maxWrite = Duration.ofMillis(2000L);
        final String storeName = "test_mapping";
        final Configuration indexConfig = ElasticsearchConfigTest.esr.setConfiguration(GraphDatabaseConfiguration.buildGraphConfiguration(), ElasticsearchConfigTest.INDEX_NAME).set(USE_EXTERNAL_MAPPINGS, true, ElasticsearchConfigTest.INDEX_NAME).restrictTo(ElasticsearchConfigTest.INDEX_NAME);
        final IndexProvider idx = open(indexConfig);
        final ElasticMajorVersion version = getVersion();
        final HttpPut newTemplate = new HttpPut("_template/template_1");
        final Map<String, Object> content = ImmutableMap.of("template", "janusgraph_test_mapping*", "mappings", readMapping(version, "/strict_mapping.json").getMappings());
        newTemplate.setEntity(new StringEntity(objectMapper.writeValueAsString(content), Charset.forName("UTF-8")));
        executeRequest(newTemplate);
        final HttpPut newMapping = new HttpPut(("janusgraph_" + storeName));
        executeRequest(newMapping);
        final KeyInformation.IndexRetriever indexRetriever = IndexProviderTest.getIndexRetriever(IndexProviderTest.getMapping(idx.getFeatures(), ElasticsearchConfigTest.ANALYZER_ENGLISH, ElasticsearchConfigTest.ANALYZER_KEYWORD));
        final BaseTransactionConfig txConfig = StandardBaseTransactionConfig.of(MILLI);
        final IndexTransaction itx = new IndexTransaction(idx, indexRetriever, txConfig, maxWrite);
        // Test that the "date" property works well.
        idx.register(storeName, "date", IndexProviderTest.getMapping(idx.getFeatures(), ElasticsearchConfigTest.ANALYZER_ENGLISH, ElasticsearchConfigTest.ANALYZER_KEYWORD).get("date"), itx);
        // Test that the "weight" property throws an exception.
        try {
            idx.register(storeName, "weight", IndexProviderTest.getMapping(idx.getFeatures(), ElasticsearchConfigTest.ANALYZER_ENGLISH, ElasticsearchConfigTest.ANALYZER_KEYWORD).get("weight"), itx);
            Assertions.fail("should fail");
        } catch (final BackendException e) {
            ElasticsearchConfigTest.log.debug(e.getMessage(), e);
        }
        itx.rollback();
        idx.close();
    }

    @Test
    public void testSplitIndexToMultiType() throws IOException, InterruptedException, BackendException {
        final ModifiableConfiguration config = ElasticsearchConfigTest.esr.setConfiguration(GraphDatabaseConfiguration.buildGraphConfiguration(), ElasticsearchConfigTest.INDEX_NAME);
        config.set(USE_DEPRECATED_MULTITYPE_INDEX, false, ElasticsearchConfigTest.INDEX_NAME);
        Configuration indexConfig = config.restrictTo(ElasticsearchConfigTest.INDEX_NAME);
        final IndexProvider idx = open(indexConfig);
        simpleWriteAndQuery(idx);
        try {
            config.set(USE_DEPRECATED_MULTITYPE_INDEX, true, ElasticsearchConfigTest.INDEX_NAME);
            indexConfig = config.restrictTo(ElasticsearchConfigTest.INDEX_NAME);
            open(indexConfig);
            Assertions.fail("should fail");
        } catch (final IllegalArgumentException e) {
            ElasticsearchConfigTest.log.debug(e.getMessage(), e);
        }
        idx.close();
    }

    @Test
    public void testMultiTypeToSplitIndex() throws IOException, InterruptedException, BackendException {
        final ModifiableConfiguration config = ElasticsearchConfigTest.esr.setConfiguration(GraphDatabaseConfiguration.buildGraphConfiguration(), ElasticsearchConfigTest.INDEX_NAME);
        config.set(USE_DEPRECATED_MULTITYPE_INDEX, true, ElasticsearchConfigTest.INDEX_NAME);
        Configuration indexConfig = config.restrictTo(ElasticsearchConfigTest.INDEX_NAME);
        final IndexProvider idx = open(indexConfig);
        simpleWriteAndQuery(idx);
        try {
            config.set(USE_DEPRECATED_MULTITYPE_INDEX, false, ElasticsearchConfigTest.INDEX_NAME);
            indexConfig = config.restrictTo(ElasticsearchConfigTest.INDEX_NAME);
            open(indexConfig);
            Assertions.fail("should fail");
        } catch (final IllegalArgumentException e) {
            ElasticsearchConfigTest.log.debug(e.getMessage(), e);
        }
        idx.close();
    }

    @Test
    public void testMultiTypeUpgrade() throws IOException, InterruptedException, BackendException {
        // create multi-type index
        final ModifiableConfiguration config = ElasticsearchConfigTest.esr.setConfiguration(GraphDatabaseConfiguration.buildGraphConfiguration(), ElasticsearchConfigTest.INDEX_NAME);
        config.set(USE_DEPRECATED_MULTITYPE_INDEX, true, ElasticsearchConfigTest.INDEX_NAME);
        Configuration indexConfig = config.restrictTo(ElasticsearchConfigTest.INDEX_NAME);
        IndexProvider idx = open(indexConfig);
        simpleWriteAndQuery(idx);
        idx.close();
        // should be able to open multi-type index if USE_DEPRECATED_MULTITYPE_INDEX is unset
        config.remove(USE_DEPRECATED_MULTITYPE_INDEX, ElasticsearchConfigTest.INDEX_NAME);
        indexConfig = config.restrictTo(ElasticsearchConfigTest.INDEX_NAME);
        idx = open(indexConfig);
        idx.close();
    }
}

