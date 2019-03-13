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
package org.janusgraph.graphdb.cql;


import GraphDatabaseConfiguration.INITIAL_JANUSGRAPH_VERSION;
import GraphDatabaseConfiguration.TITAN_COMPATIBLE_VERSIONS;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.diskstorage.configuration.ConfigElement;
import org.janusgraph.diskstorage.configuration.WriteConfiguration;
import org.janusgraph.diskstorage.cql.CQLConfigOptions;
import org.janusgraph.graphdb.CassandraGraphTest;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CQLGraphTest extends CassandraGraphTest {
    @Test
    public void testTitanGraphBackwardCompatibility() {
        close();
        WriteConfiguration wc = getConfiguration();
        wc.set(ConfigElement.getPath(CQLConfigOptions.KEYSPACE), "titan");
        wc.set(ConfigElement.getPath(TITAN_COMPATIBLE_VERSIONS), "x.x.x");
        Assertions.assertNull(wc.get(ConfigElement.getPath(INITIAL_JANUSGRAPH_VERSION), INITIAL_JANUSGRAPH_VERSION.getDatatype()));
        Assertions.assertFalse(JanusGraphConstants.TITAN_COMPATIBLE_VERSIONS.contains(wc.get(ConfigElement.getPath(TITAN_COMPATIBLE_VERSIONS), TITAN_COMPATIBLE_VERSIONS.getDatatype())));
        wc.set(ConfigElement.getPath(TITAN_COMPATIBLE_VERSIONS), "1.0.0");
        Assertions.assertTrue(JanusGraphConstants.TITAN_COMPATIBLE_VERSIONS.contains(wc.get(ConfigElement.getPath(TITAN_COMPATIBLE_VERSIONS), TITAN_COMPATIBLE_VERSIONS.getDatatype())));
        graph = ((StandardJanusGraph) (JanusGraphFactory.open(wc)));
    }
}

