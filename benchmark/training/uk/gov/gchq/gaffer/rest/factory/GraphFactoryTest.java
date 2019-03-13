/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.rest.factory;


import SystemProperty.GRAPH_CONFIG_PATH;
import SystemProperty.GRAPH_FACTORY_CLASS;
import SystemProperty.SCHEMA_PATHS;
import SystemProperty.STORE_PROPERTIES_PATH;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.hook.AddOperationsToChain;
import uk.gov.gchq.gaffer.graph.hook.NamedOperationResolver;
import uk.gov.gchq.gaffer.graph.hook.NamedViewResolver;
import uk.gov.gchq.gaffer.graph.hook.OperationAuthoriser;
import uk.gov.gchq.gaffer.graph.hook.OperationChainLimiter;


public class GraphFactoryTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    @Test
    public void shouldCreateDefaultGraphFactoryWhenNoSystemProperty() {
        // Given
        System.clearProperty(GRAPH_FACTORY_CLASS);
        // When
        final GraphFactory graphFactory = GraphFactory.createGraphFactory();
        // Then
        Assert.assertEquals(DefaultGraphFactory.class, graphFactory.getClass());
    }

    @Test
    public void shouldCreateGraphFactoryFromSystemPropertyClassName() {
        // Given
        System.setProperty(GRAPH_FACTORY_CLASS, GraphFactoryForTest.class.getName());
        // When
        final GraphFactory graphFactory = GraphFactory.createGraphFactory();
        // Then
        Assert.assertEquals(GraphFactoryForTest.class, graphFactory.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionFromInvalidSystemPropertyClassName() {
        // Given
        System.setProperty(GRAPH_FACTORY_CLASS, "InvalidClassName");
        // When
        final GraphFactory graphFactory = GraphFactory.createGraphFactory();
        // Then
        Assert.fail();
    }

    @Test
    public void shouldNotAddGraphConfigWhenSystemPropertyNotSet() throws IOException {
        // Given
        System.setProperty(STORE_PROPERTIES_PATH, "store.properties");
        final File schemaFile = testFolder.newFile("schema.json");
        FileUtils.writeLines(schemaFile, IOUtils.readLines(StreamUtil.openStream(getClass(), "/schema/schema.json")));
        System.setProperty(SCHEMA_PATHS, schemaFile.getAbsolutePath());
        System.clearProperty(GRAPH_CONFIG_PATH);
        final GraphFactory factory = new DefaultGraphFactory();
        // When / Then
        try {
            factory.createGraph();
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("graphId is required", e.getMessage());
        }
    }

    @Test
    public void shouldAddGraphConfigHooksWhenSystemPropertySet() throws IOException {
        // Given
        final File storePropertiesFile = testFolder.newFile("store.properties");
        FileUtils.writeLines(storePropertiesFile, IOUtils.readLines(StreamUtil.openStream(getClass(), "store.properties")));
        System.setProperty(STORE_PROPERTIES_PATH, storePropertiesFile.getAbsolutePath());
        final File schemaFile = testFolder.newFile("schema.json");
        FileUtils.writeLines(schemaFile, IOUtils.readLines(StreamUtil.openStream(getClass(), "/schema/schema.json")));
        System.setProperty(SCHEMA_PATHS, schemaFile.getAbsolutePath());
        final File graphConfigFile = testFolder.newFile("graphConfig.json");
        FileUtils.writeLines(graphConfigFile, IOUtils.readLines(StreamUtil.openStream(getClass(), "graphConfigWithHooks.json")));
        System.setProperty(GRAPH_CONFIG_PATH, graphConfigFile.getAbsolutePath());
        final GraphFactory factory = new DefaultGraphFactory();
        // When
        final Graph graph = factory.createGraph();
        // Then
        Assert.assertEquals(Arrays.asList(NamedOperationResolver.class, NamedViewResolver.class, OperationChainLimiter.class, AddOperationsToChain.class, OperationAuthoriser.class), graph.getGraphHooks());
    }

    @Test
    public void shouldDefaultToSingletonGraph() {
        // Given
        final DefaultGraphFactory factory = new DefaultGraphFactory();
        // When
        final boolean isSingleton = factory.isSingletonGraph();
        // Then
        Assert.assertTrue(isSingleton);
    }
}

