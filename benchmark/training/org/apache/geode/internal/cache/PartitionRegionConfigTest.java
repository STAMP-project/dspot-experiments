/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import java.util.Set;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.Scope;
import org.apache.geode.internal.util.BlobHelper;
import org.junit.Test;


public class PartitionRegionConfigTest {
    private int prId;

    private String path;

    private PartitionAttributes partitionAttributes;

    private Scope scope;

    private EvictionAttributes evictionAttributes;

    private ExpirationAttributes regionIdleTimeout;

    private ExpirationAttributes regionTimeToLive;

    private ExpirationAttributes entryIdleTimeout;

    private ExpirationAttributes entryTimeToLive;

    private Set<String> gatewaySenderIds;

    @Test
    public void dataSerializes() throws Exception {
        PartitionRegionConfig config = new PartitionRegionConfig(prId, path, partitionAttributes, scope, evictionAttributes, regionIdleTimeout, regionTimeToLive, entryIdleTimeout, entryTimeToLive, gatewaySenderIds);
        byte[] bytes = BlobHelper.serializeToBlob(config);
        assertThat(bytes).isNotNull().isNotEmpty();
        assertThat(BlobHelper.deserializeBlob(bytes)).isNotSameAs(config).isInstanceOf(PartitionRegionConfig.class);
    }
}

