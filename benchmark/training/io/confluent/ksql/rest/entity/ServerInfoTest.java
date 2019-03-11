/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.entity;


import JsonMapper.INSTANCE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ServerInfoTest {
    private static final String VERSION = "test-version";

    private static final String KAFKA_CLUSTER_ID = "test-kafka-cluster";

    private static final String KSQL_SERVICE_ID = "test-ksql-service";

    private ServerInfo serverInfo = new ServerInfo(ServerInfoTest.VERSION, ServerInfoTest.KAFKA_CLUSTER_ID, ServerInfoTest.KSQL_SERVICE_ID);

    @Test
    public void testSerializeDeserialize() throws IOException {
        final ObjectMapper mapper = INSTANCE.mapper;
        final byte[] bytes = mapper.writeValueAsBytes(serverInfo);
        final ServerInfo deserializedServerInfo = mapper.readValue(bytes, ServerInfo.class);
        Assert.assertThat(serverInfo, CoreMatchers.equalTo(deserializedServerInfo));
    }
}

