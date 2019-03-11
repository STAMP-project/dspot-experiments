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
package uk.gov.gchq.gaffer.rest;


import SystemProperty.GRAPH_CONFIG_PATH;
import SystemProperty.SCHEMA_PATHS;
import SystemProperty.STORE_PROPERTIES_PATH;
import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.impl.add.AddElementsFromFile;
import uk.gov.gchq.gaffer.rest.factory.DefaultGraphFactory;


public abstract class DisableOperationsTest {
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    protected final Class<? extends Operation>[] disabledOperations;

    protected File graphConfigPath;

    protected File storePropsPath;

    protected File schemaPath;

    public DisableOperationsTest() throws IOException {
        this(AddElementsFromFile.class);
    }

    @SafeVarargs
    protected DisableOperationsTest(final Class<? extends Operation>... disabledOperations) throws IOException {
        this.disabledOperations = disabledOperations;
    }

    @Test
    public void shouldDisableOperationsUsingOperationDeclarations() {
        // Given
        System.setProperty(STORE_PROPERTIES_PATH, storePropsPath.getAbsolutePath());
        System.setProperty(SCHEMA_PATHS, schemaPath.getAbsolutePath());
        System.setProperty(GRAPH_CONFIG_PATH, graphConfigPath.getAbsolutePath());
        final DefaultGraphFactory factory = new DefaultGraphFactory();
        // When
        final Graph graph = factory.createGraph();
        // Then
        for (final Class<? extends Operation> disabledOperation : disabledOperations) {
            assertFalse(((disabledOperation.getSimpleName()) + " should not be supported"), graph.isSupported(disabledOperation));
        }
    }

    @Test
    public void shouldNotDisableOperationsWhenNotUsingRestApi() {
        // Given
        System.setProperty(GRAPH_CONFIG_PATH, graphConfigPath.getAbsolutePath());
        System.setProperty(STORE_PROPERTIES_PATH, storePropsPath.getAbsolutePath());
        System.setProperty(SCHEMA_PATHS, schemaPath.getAbsolutePath());
        // When
        final Graph graph = new Graph.Builder().config(graphConfigPath.toURI()).storeProperties(storePropsPath.toURI()).addSchema(schemaPath.toURI()).build();
        // Then
        for (final Class<? extends Operation> disabledOperation : disabledOperations) {
            assertTrue(((disabledOperation.getSimpleName()) + " should be supported"), graph.isSupported(disabledOperation));
        }
    }
}

