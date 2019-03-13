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
package uk.gov.gchq.gaffer.graph;


import TestGroups.ENTITY;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.JSONSerialisationTest;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.graph.hook.AddOperationsToChain;
import uk.gov.gchq.gaffer.graph.hook.Log4jLogger;
import uk.gov.gchq.gaffer.graph.hook.NamedOperationResolver;


public class GraphConfigTest extends JSONSerialisationTest<GraphConfig> {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldJsonDeserialiseFromHookPaths() throws IOException {
        // Given
        final File hook1Path = folder.newFile();
        final File hook2Path = folder.newFile();
        FileUtils.write(hook1Path, (("{\"class\": \"" + (Log4jLogger.class.getName())) + "\"}"));
        FileUtils.write(hook2Path, (("{\"class\": \"" + (AddOperationsToChain.class.getName())) + "\"}"));
        final String json = (((((((((((((("{" + (((("  \"graphId\": \"graphId1\"," + "  \"hooks\": [") + "    {") + "      \"class\": \"uk.gov.gchq.gaffer.graph.hook.GraphHookPath\",") + "      \"path\": \"")) + (hook1Path.getAbsolutePath())) + "\"") + "    }, ") + "    {") + "      \"class\": \"uk.gov.gchq.gaffer.graph.hook.GraphHookPath\",") + "      \"path\": \"") + (hook2Path.getAbsolutePath())) + "\"") + "    },") + "    {") + "      \"class\": \"uk.gov.gchq.gaffer.graph.hook.NamedOperationResolver\"") + "    }") + "  ]") + "}";
        // When
        final GraphConfig deserialisedObj = fromJson(json.getBytes());
        // Then
        Assert.assertNotNull(deserialisedObj);
        Assert.assertEquals(Arrays.asList(Log4jLogger.class, AddOperationsToChain.class, NamedOperationResolver.class), ((List) (deserialisedObj.getHooks().stream().map(GraphHook::getClass).collect(Collectors.toList()))));
    }

    @Test
    public void shouldReturnClonedView() throws Exception {
        // Given
        final String graphId = "graphId";
        final View view = new View.Builder().entity(ENTITY).build();
        // When
        final GraphConfig config = new GraphConfig.Builder().graphId(graphId).view(view).build();
        // Then
        Assert.assertEquals(view, config.getView());
        Assert.assertNotSame(view, config.getView());
    }
}

