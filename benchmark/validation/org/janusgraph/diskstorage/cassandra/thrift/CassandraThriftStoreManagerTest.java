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
package org.janusgraph.diskstorage.cassandra.thrift;


import CassandraThriftStoreManager.THRIFT_FRAME_SIZE_MB;
import org.janusgraph.CassandraStorageSetup;
import org.janusgraph.diskstorage.configuration.ModifiableConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class CassandraThriftStoreManagerTest {
    @Test
    public void configOptionFrameSizeMbShouldErrorOnLowValue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ModifiableConfiguration config = CassandraStorageSetup.getCassandraThriftConfiguration("janusgraph");
            config.set(THRIFT_FRAME_SIZE_MB, 0);
            config.get(THRIFT_FRAME_SIZE_MB);
        });
    }

    @Test
    public void configOptionFrameSizeMbShouldBeHappy() {
        ModifiableConfiguration config = CassandraStorageSetup.getCassandraThriftConfiguration("janusgraph");
        config.set(THRIFT_FRAME_SIZE_MB, 1);
        Integer result = config.get(THRIFT_FRAME_SIZE_MB);
        Assertions.assertEquals(1, result.intValue());
    }

    @Test
    public void configOptionFrameSizeMbShouldErrorOnHighValue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ModifiableConfiguration config = CassandraStorageSetup.getCassandraThriftConfiguration("janusgraph");
            config.set(THRIFT_FRAME_SIZE_MB, 2048);
            config.get(THRIFT_FRAME_SIZE_MB);
        });
    }
}

