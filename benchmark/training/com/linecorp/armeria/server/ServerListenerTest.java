/**
 * Copyright 2018 LINE Corporation
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
package com.linecorp.armeria.server;


import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.metric.PrometheusMeterRegistries;
import com.linecorp.armeria.testing.server.ServerRule;
import org.assertj.core.util.Lists;
import org.junit.ClassRule;
import org.junit.Test;


public class ServerListenerTest {
    private static long STARTING_AT;

    private static long STARTED_AT;

    private static long STOPPING_AT;

    private static long STOPPED_AT;

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) {
            sb.meterRegistry(PrometheusMeterRegistries.newRegistry()).service("/", ( req, ctx) -> HttpResponse.of("Hello!"));
            // Record when the method triggered
            final ServerListener sl = // add multiple callbacks at once, with iterable api.
            // add multiple callbacks at once, with vargs api.
            // add multiple callbacks, one by one.
            // add a callback.
            new ServerListenerBuilder().addStartingCallback((Server server) -> ServerListenerTest.STARTING_AT = System.currentTimeMillis()).addStartedCallback((Server server) -> ServerListenerTest.STARTED_AT = -1).addStartedCallback((Server server) -> ServerListenerTest.STARTED_AT = System.currentTimeMillis()).addStoppingCallbacks((Server server) -> ServerListenerTest.STOPPING_AT = System.currentTimeMillis(), (Server server) -> ServerListenerTest.STARTING_AT = 0L).addStoppedCallbacks(Lists.newArrayList((Server server) -> ServerListenerTest.STOPPED_AT = System.currentTimeMillis(), (Server server) -> ServerListenerTest.STARTED_AT = 0L)).build();
            sb.serverListener(sl);
        }
    };

    @Test
    public void testServerListener() throws Exception {
        // Before stop
        assertThat(ServerListenerTest.STARTING_AT).isGreaterThan(0L);
        assertThat(ServerListenerTest.STARTED_AT).isGreaterThanOrEqualTo(ServerListenerTest.STARTING_AT);
        assertThat(ServerListenerTest.STOPPING_AT).isEqualTo(0L);
        assertThat(ServerListenerTest.STOPPED_AT).isEqualTo(0L);
        final Server server = ServerListenerTest.server.server();
        server.stop().get();
        // After stop
        assertThat(ServerListenerTest.STARTING_AT).isEqualTo(0L);
        assertThat(ServerListenerTest.STARTED_AT).isEqualTo(0L);
        assertThat(ServerListenerTest.STOPPING_AT).isGreaterThanOrEqualTo(0L);
        assertThat(ServerListenerTest.STOPPED_AT).isGreaterThanOrEqualTo(ServerListenerTest.STOPPING_AT);
    }
}

