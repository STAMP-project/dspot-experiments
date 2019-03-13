/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.mapstore;


import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.GraphConfig;
import uk.gov.gchq.gaffer.graph.GraphSerialisable;
import uk.gov.gchq.gaffer.serialisation.implementation.JavaSerialiser;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class MapStorePropertiesGraphSerialisableTest {
    private GraphConfig config;

    private Schema schema;

    private Properties properties;

    private GraphSerialisable expected;

    @Test
    public void shouldSerialiseAndDeserialise() throws Exception {
        final JavaSerialiser javaSerialiser = new JavaSerialiser();
        final byte[] serialise = javaSerialiser.serialise(expected);
        final GraphSerialisable result = ((GraphSerialisable) (javaSerialiser.deserialise(serialise)));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void shouldConsumeGraph() throws Exception {
        final MapStoreProperties mapStoreProperties = new MapStoreProperties();
        mapStoreProperties.setProperties(properties);
        final Graph graph = new Graph.Builder().addSchema(schema).addStoreProperties(mapStoreProperties).config(config).build();
        final GraphSerialisable result = new GraphSerialisable.Builder().graph(graph).build();
        Assert.assertEquals(expected, result);
    }
}

