/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.metadata;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class MetadataStorageConnectorConfigTest {
    @Test
    public void testEquals() throws IOException {
        MetadataStorageConnectorConfig metadataStorageConnectorConfig = createMetadataStorageConfig(true, "testHost", 4000, "url", "user", "\"nothing\"");
        MetadataStorageConnectorConfig metadataStorageConnectorConfig2 = createMetadataStorageConfig(true, "testHost", 4000, "url", "user", "\"nothing\"");
        Assert.assertTrue(metadataStorageConnectorConfig.equals(metadataStorageConnectorConfig2));
        Assert.assertTrue(((metadataStorageConnectorConfig.hashCode()) == (metadataStorageConnectorConfig2.hashCode())));
    }

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void testMetadataStorageConnectionConfigSimplePassword() throws Exception {
        testMetadataStorageConnectionConfig(true, "host", 1234, "connectURI", "user", "\"nothing\"", "nothing");
    }

    @Test
    public void testMetadataStorageConnectionConfigWithDefaultProviderPassword() throws Exception {
        testMetadataStorageConnectionConfig(true, "host", 1234, "connectURI", "user", "{\"type\":\"default\",\"password\":\"nothing\"}", "nothing");
    }

    @Test
    public void testDbcpProperties() throws Exception {
        testDbcpPropertiesFile(true, "host", 1234, "connectURI", "user", "{\"type\":\"default\",\"password\":\"nothing\"}", "nothing");
    }
}

