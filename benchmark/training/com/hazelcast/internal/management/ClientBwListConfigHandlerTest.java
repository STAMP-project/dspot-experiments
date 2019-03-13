/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.management;


import Mode.BLACKLIST;
import Mode.DISABLED;
import Mode.WHITELIST;
import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.client.impl.ClientSelectors;
import com.hazelcast.core.Client;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.dto.ClientBwListEntryDTO;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.internal.management.dto.ClientBwListEntryDTO.Type.INSTANCE_NAME;
import static com.hazelcast.internal.management.dto.ClientBwListEntryDTO.Type.IP_ADDRESS;
import static com.hazelcast.internal.management.dto.ClientBwListEntryDTO.Type.LABEL;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientBwListConfigHandlerTest extends HazelcastTestSupport {
    private ClientEngine clientEngine;

    private ClientBwListConfigHandler handler;

    @Test
    public void testHandleLostConnection() {
        clientEngine.applySelector(ClientSelectors.none());
        handler.handleLostConnection();
        Client client = createClient("127.0.0.1", HazelcastTestSupport.randomString());
        Assert.assertTrue(clientEngine.isClientAllowed(client));
    }

    @Test
    public void testHandleWhitelist() {
        JsonObject configJson = createConfig(WHITELIST, new ClientBwListEntryDTO(IP_ADDRESS, "127.0.0.*"), new ClientBwListEntryDTO(IP_ADDRESS, "192.168.0.1"), new ClientBwListEntryDTO(IP_ADDRESS, "192.168.0.42-43"), new ClientBwListEntryDTO(IP_ADDRESS, "fe80:0:0:0:45c5:47ee:fe15:493a"), new ClientBwListEntryDTO(INSTANCE_NAME, "client*"), new ClientBwListEntryDTO(LABEL, "label*"));
        handler.handleConfig(configJson);
        Client[] allowed = new Client[]{ createClient("127.0.0.3", "a_name"), createClient("192.168.0.1", "a_name"), createClient("192.168.0.42", "a_name"), createClient("fe80:0:0:0:45c5:47ee:fe15:493a", "a_name"), createClient("192.168.0.101", "client4"), createClient("192.168.0.101", "a_name", "label") };
        for (Client client : allowed) {
            Assert.assertTrue(clientEngine.isClientAllowed(client));
        }
        Client[] denied = new Client[]{ createClient("192.168.0.101", "a_name", "random"), createClient("fe70:0:0:0:35c5:16ee:fe15:491a", "a_name", "random") };
        for (Client client : denied) {
            Assert.assertFalse(clientEngine.isClientAllowed(client));
        }
    }

    @Test
    public void testHandleBlacklist() {
        JsonObject configJson = createConfig(BLACKLIST, new ClientBwListEntryDTO(IP_ADDRESS, "127.0.0.*"), new ClientBwListEntryDTO(IP_ADDRESS, "192.168.0.1"), new ClientBwListEntryDTO(IP_ADDRESS, "192.168.*.42"), new ClientBwListEntryDTO(IP_ADDRESS, "fe80:0:0:0:45c5:47ee:fe15:*"), new ClientBwListEntryDTO(INSTANCE_NAME, "*_client"), new ClientBwListEntryDTO(LABEL, "test*label"));
        handler.handleConfig(configJson);
        Client[] allowed = new Client[]{ createClient("192.168.0.101", "a_name", "random"), createClient("fe70:0:0:0:35c5:16ee:fe15:491a", "a_name", "random") };
        for (Client client : allowed) {
            Assert.assertTrue(clientEngine.isClientAllowed(client));
        }
        Client[] denied = new Client[]{ createClient("127.0.0.3", "a_name"), createClient("192.168.0.1", "a_name"), createClient("192.168.0.42", "a_name"), createClient("fe80:0:0:0:45c5:47ee:fe15:493a", "a_name"), createClient("192.168.0.101", "java_client"), createClient("192.168.0.101", "a_name", "test_label"), createClient("192.168.0.101", "a_name", "testlabel") };
        for (Client client : denied) {
            Assert.assertFalse(clientEngine.isClientAllowed(client));
        }
    }

    @Test
    public void testHandleEmptyWhitelist() {
        JsonObject configJson = createConfig(WHITELIST);
        handler.handleConfig(configJson);
        Client client = createClient("127.0.0.1", "a_name");
        Assert.assertFalse(clientEngine.isClientAllowed(client));
    }

    @Test
    public void testHandleEmptyBlacklist() {
        clientEngine.applySelector(ClientSelectors.none());
        JsonObject configJson = createConfig(BLACKLIST);
        handler.handleConfig(configJson);
        Client client = createClient("127.0.0.1", "a_name");
        Assert.assertTrue(clientEngine.isClientAllowed(client));
    }

    @Test
    public void testHandleDisabledMode() {
        clientEngine.applySelector(ClientSelectors.none());
        JsonObject configJson = createConfig(DISABLED);
        handler.handleConfig(configJson);
        Client client = createClient("127.0.0.1", HazelcastTestSupport.randomString());
        Assert.assertTrue(clientEngine.isClientAllowed(client));
    }

    @Test
    public void testHandleEmptyConfig() {
        clientEngine.applySelector(ClientSelectors.none());
        JsonObject configJson = new JsonObject();
        handler.handleConfig(configJson);
        Client client = createClient("127.0.0.1", HazelcastTestSupport.randomString());
        Assert.assertFalse(clientEngine.isClientAllowed(client));
    }

    @Test
    public void testHandleInvalidMode() {
        clientEngine.applySelector(ClientSelectors.none());
        JsonObject configJson = createConfig(DISABLED);
        configJson.get("clientBwList").asObject().set("mode", "invalid_mode");
        handler.handleConfig(configJson);
        Client client = createClient("127.0.0.1", HazelcastTestSupport.randomString());
        Assert.assertFalse(clientEngine.isClientAllowed(client));
    }

    @Test
    public void testHandleInvalidEntry() {
        clientEngine.applySelector(ClientSelectors.none());
        JsonObject configJson = createConfig(WHITELIST, new ClientBwListEntryDTO(LABEL, "192.168.0.1"), new ClientBwListEntryDTO(IP_ADDRESS, "192.168.0.2"));
        configJson.get("clientBwList").asObject().get("entries").asArray().get(0).asObject().set("type", "invalid_type");
        handler.handleConfig(configJson);
        Client client1 = createClient("192.168.0.1", HazelcastTestSupport.randomString());
        Assert.assertFalse(clientEngine.isClientAllowed(client1));
        Client client2 = createClient("192.168.0.2", HazelcastTestSupport.randomString());
        Assert.assertFalse(clientEngine.isClientAllowed(client2));
    }
}

