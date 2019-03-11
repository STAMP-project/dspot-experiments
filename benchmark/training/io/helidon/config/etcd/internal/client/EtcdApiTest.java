/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.config.etcd.internal.client;


import EtcdApi.v2;
import EtcdApi.v3;
import io.helidon.config.etcd.internal.client.v2.EtcdV2Client;
import io.helidon.config.etcd.internal.client.v3.EtcdV3Client;
import java.net.URI;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link EtcdApi}.
 */
public class EtcdApiTest {
    @Test
    public void testClientVersion() {
        MatcherAssert.assertThat(v2.clientFactory().createClient(URI.create("http://localhost")), CoreMatchers.instanceOf(EtcdV2Client.class));
        MatcherAssert.assertThat(v3.clientFactory().createClient(URI.create("http://localhost")), CoreMatchers.instanceOf(EtcdV3Client.class));
    }
}

