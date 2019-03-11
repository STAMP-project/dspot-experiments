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
package org.janusgraph.example;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class GraphAppTest {
    protected static final String CONF_FILE = "conf/jgex-inmemory.properties";

    protected static GraphApp app;

    protected static GraphTraversalSource g;

    @Test
    public void openGraph() throws ConfigurationException {
        Assertions.assertNotNull(GraphAppTest.g);
    }

    @Test
    public void openGraphNullConfig() throws ConfigurationException {
        Assertions.assertThrows(ConfigurationException.class, () -> new GraphApp(null).openGraph());
    }

    @Test
    public void openGraphConfigNotFound() throws ConfigurationException {
        Assertions.assertThrows(ConfigurationException.class, () -> new GraphApp("conf/foobar").openGraph());
    }

    @Test
    public void createElements() throws ConfigurationException {
        GraphAppTest.app.createElements();
        Assertions.assertEquals(12L, ((Long) (GraphAppTest.g.V().count().next())).longValue());
        Assertions.assertEquals(1L, ((Long) (GraphAppTest.g.V().hasLabel("titan").count().next())).longValue());
        Assertions.assertEquals(1L, ((Long) (GraphAppTest.g.V().hasLabel("demigod").count().next())).longValue());
        Assertions.assertEquals(1L, ((Long) (GraphAppTest.g.V().hasLabel("human").count().next())).longValue());
        Assertions.assertEquals(3L, ((Long) (GraphAppTest.g.V().hasLabel("location").count().next())).longValue());
        Assertions.assertEquals(3L, ((Long) (GraphAppTest.g.V().hasLabel("god").count().next())).longValue());
        Assertions.assertEquals(3L, ((Long) (GraphAppTest.g.V().hasLabel("monster").count().next())).longValue());
        Assertions.assertEquals(17L, ((Long) (GraphAppTest.g.E().count().next())).longValue());
        Assertions.assertEquals(2L, ((Long) (GraphAppTest.g.E().hasLabel("father").count().next())).longValue());
        Assertions.assertEquals(1L, ((Long) (GraphAppTest.g.E().hasLabel("mother").count().next())).longValue());
        Assertions.assertEquals(6L, ((Long) (GraphAppTest.g.E().hasLabel("brother").count().next())).longValue());
        Assertions.assertEquals(1L, ((Long) (GraphAppTest.g.E().hasLabel("pet").count().next())).longValue());
        Assertions.assertEquals(4L, ((Long) (GraphAppTest.g.E().hasLabel("lives").count().next())).longValue());
        Assertions.assertEquals(3L, ((Long) (GraphAppTest.g.E().hasLabel("battled").count().next())).longValue());
        final float[] place = ((float[]) (GraphAppTest.g.V().has("name", "hercules").outE("battled").has("time", 12).values("place").next()));
        Assertions.assertNotNull(place);
        Assertions.assertEquals(2, place.length);
        Assertions.assertEquals(Float.valueOf(39.0F), Float.valueOf(place[0]));
        Assertions.assertEquals(Float.valueOf(22.0F), Float.valueOf(place[1]));
    }

    @Test
    public void updateElements() throws ConfigurationException {
        GraphAppTest.app.createElements();
        Assertions.assertFalse(GraphAppTest.g.V().has("name", "jupiter").has("ts").hasNext());
        GraphAppTest.app.updateElements();
        final long ts1 = ((long) (GraphAppTest.g.V().has("name", "jupiter").values("ts").next()));
        GraphAppTest.app.updateElements();
        final long ts2 = ((long) (GraphAppTest.g.V().has("name", "jupiter").values("ts").next()));
        Assertions.assertTrue((ts2 > ts1));
    }

    @Test
    public void deleteElements() {
        GraphAppTest.app.createElements();
        GraphAppTest.app.deleteElements();
        Assertions.assertFalse(GraphAppTest.g.V().has("name", "pluto").hasNext());
        Assertions.assertFalse(GraphAppTest.g.V().has("name", "jupiter").both("brother").has("name", "pluto").hasNext());
    }
}

