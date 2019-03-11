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
package org.janusgraph.diskstorage.common;


import java.util.List;
import java.util.Map;
import org.janusgraph.diskstorage.BackendException;
import org.janusgraph.diskstorage.BaseTransactionConfig;
import org.janusgraph.diskstorage.configuration.ConfigOption;
import org.janusgraph.diskstorage.configuration.Configuration;
import org.janusgraph.diskstorage.keycolumnvalue.KeyRange;
import org.janusgraph.diskstorage.keycolumnvalue.StoreFeatures;
import org.janusgraph.diskstorage.keycolumnvalue.StoreTransaction;
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class LocalStoreManagerTest {
    public class LocalStoreManagerSampleImplementation extends LocalStoreManager {
        public LocalStoreManagerSampleImplementation(Configuration c) throws BackendException {
            super(c);
        }

        /* The following methods are placeholders to adhere to the StoreManager interface. */
        @Override
        public List<KeyRange> getLocalKeyPartition() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public StoreFeatures getFeatures() {
            return null;
        }

        @Override
        public void clearStorage() {
        }

        @Override
        public void close() {
        }

        @Override
        public StoreTransaction beginTransaction(BaseTransactionConfig config) {
            return null;
        }

        @Override
        public boolean exists() {
            return true;
        }
    }

    @Test
    public void directoryShouldEqualSuppliedDirectory() throws BackendException {
        final Map<ConfigOption, String> map = getBaseConfigurationMap();
        map.put(GraphDatabaseConfiguration.STORAGE_DIRECTORY, "specific/absolute/directory");
        final LocalStoreManager mgr = getStoreManager(map);
        Assertions.assertEquals("specific/absolute/directory", mgr.directory.getPath());
    }

    @Test
    public void directoryShouldEqualStorageRootPlusGraphName() throws BackendException {
        final Map<ConfigOption, String> map = getBaseConfigurationMap();
        map.put(GraphDatabaseConfiguration.STORAGE_ROOT, "temp/root");
        map.put(GraphDatabaseConfiguration.GRAPH_NAME, "randomGraphName");
        final LocalStoreManager mgr = getStoreManager(map);
        Assertions.assertEquals("temp/root/randomGraphName", mgr.directory.getPath());
    }

    @Test
    public void shouldThrowError() {
        final Map<ConfigOption, String> map = getBaseConfigurationMap();
        map.put(GraphDatabaseConfiguration.GRAPH_NAME, "randomGraphName");
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> getStoreManager(map));
        Assertions.assertEquals(("Please supply configuration parameter " + ("\"storage.directory\" or both \"storage.root\" " + "and \"graph.graphname\".")), runtimeException.getMessage());
    }
}

