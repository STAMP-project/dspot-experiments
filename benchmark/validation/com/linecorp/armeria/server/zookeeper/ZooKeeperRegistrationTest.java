/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.zookeeper;


import HttpStatus.OK;
import com.linecorp.armeria.client.Endpoint;
import com.linecorp.armeria.client.zookeeper.ZooKeeperTestBase;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServiceRequestContext;
import java.util.List;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;
import zookeeperjunit.CloseableZooKeeper;


public class ZooKeeperRegistrationTest extends ZooKeeperTestBase {
    @Nullable
    private List<Server> servers;

    @Test(timeout = 30000)
    public void testServerNodeCreateAndDelete() {
        // all servers start and with zNode created
        await().untilAsserted(() -> ZooKeeperTestBase.sampleEndpoints.forEach(( endpoint) -> assertExists((((((ZooKeeperTestBase.zNode) + '/') + (endpoint.host())) + '_') + (endpoint.port())))));
        try (CloseableZooKeeper zk = connection()) {
            try {
                ZooKeeperTestBase.sampleEndpoints.forEach(( endpoint) -> {
                    try {
                        assertThat(NodeValueCodec.DEFAULT.decode(zk.getData((((((ZooKeeperTestBase.zNode) + '/') + (endpoint.host())) + '_') + (endpoint.port()))).get())).isEqualTo(endpoint);
                    } catch ( throwable) {
                        fail(throwable.getMessage());
                    }
                });
                // stop one server and check its ZooKeeper node
                if ((servers.size()) > 1) {
                    servers.get(0).stop().get();
                    servers.remove(0);
                    int removed = 0;
                    int remaining = 0;
                    for (Endpoint endpoint : ZooKeeperTestBase.sampleEndpoints) {
                        try {
                            final String key = ((((ZooKeeperTestBase.zNode) + '/') + (endpoint.host())) + '_') + (endpoint.port());
                            if (zk.exists(key).get()) {
                                remaining++;
                            } else {
                                removed++;
                            }
                        } catch (Throwable throwable) {
                            Assert.fail(throwable.getMessage());
                        }
                    }
                    assertThat(removed).isOne();
                    assertThat(remaining).isEqualTo(((ZooKeeperTestBase.sampleEndpoints.size()) - 1));
                }
            } catch (Throwable throwable) {
                Assert.fail(throwable.getMessage());
            }
        }
    }

    private static class EchoService extends AbstractHttpService {
        @Override
        protected final HttpResponse doPost(ServiceRequestContext ctx, HttpRequest req) {
            return HttpResponse.from(req.aggregate().thenApply(this::echo).exceptionally(CompletionActions::log));
        }

        protected HttpResponse echo(AggregatedHttpMessage aReq) {
            return HttpResponse.of(HttpHeaders.of(OK), aReq.content());
        }
    }
}

