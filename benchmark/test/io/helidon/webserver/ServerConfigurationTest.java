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
package io.helidon.webserver;


import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.opentracing.util.GlobalTracer;
import java.net.InetAddress;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link ServerConfiguration.Builder}.
 */
public class ServerConfigurationTest {
    @Test
    public void noNegativeValues() throws Exception {
        ServerConfiguration config = ServerConfiguration.builder().port((-1)).backlog((-1)).receiveBufferSize((-1)).timeout((-1)).workersCount((-1)).build();
        MatcherAssert.assertThat(config.backlog(), CoreMatchers.is(1024));
        MatcherAssert.assertThat(config.receiveBufferSize(), CoreMatchers.is(0));
        MatcherAssert.assertThat(config.timeoutMillis(), CoreMatchers.is(0));
        MatcherAssert.assertThat(((config.workersCount()) > 0), CoreMatchers.is(true));
    }

    @Test
    public void expectedDefaults() throws Exception {
        ServerConfiguration config = ServerConfiguration.builder().build();
        MatcherAssert.assertThat(config.port(), CoreMatchers.is(0));
        MatcherAssert.assertThat(config.backlog(), CoreMatchers.is(1024));
        MatcherAssert.assertThat(config.receiveBufferSize(), CoreMatchers.is(0));
        MatcherAssert.assertThat(config.timeoutMillis(), CoreMatchers.is(0));
        MatcherAssert.assertThat(((config.workersCount()) > 0), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.tracer(), IsInstanceOf.instanceOf(GlobalTracer.class));
        MatcherAssert.assertThat(config.bindAddress(), CoreMatchers.nullValue());
    }

    @Test
    public void respectValues() throws Exception {
        ServerConfiguration config = ServerConfiguration.builder().port(10).backlog(20).receiveBufferSize(30).timeout(40).workersCount(50).bindAddress(InetAddress.getLocalHost()).build();
        MatcherAssert.assertThat(config.port(), CoreMatchers.is(10));
        MatcherAssert.assertThat(config.backlog(), CoreMatchers.is(20));
        MatcherAssert.assertThat(config.receiveBufferSize(), CoreMatchers.is(30));
        MatcherAssert.assertThat(config.timeoutMillis(), CoreMatchers.is(40));
        MatcherAssert.assertThat(config.workersCount(), CoreMatchers.is(50));
        MatcherAssert.assertThat(config.bindAddress(), CoreMatchers.is(InetAddress.getLocalHost()));
    }

    @Test
    public void fromConfig() throws Exception {
        Config config = Config.builder().sources(ConfigSources.classpath("config1.conf")).build();
        ServerConfiguration sc = config.get("webserver").as(ServerConfiguration::create).get();
        MatcherAssert.assertThat(sc, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sc.port(), CoreMatchers.is(10));
        MatcherAssert.assertThat(sc.backlog(), CoreMatchers.is(20));
        MatcherAssert.assertThat(sc.receiveBufferSize(), CoreMatchers.is(30));
        MatcherAssert.assertThat(sc.timeoutMillis(), CoreMatchers.is(40));
        MatcherAssert.assertThat(sc.bindAddress(), CoreMatchers.is(InetAddress.getByName("127.0.0.1")));
        MatcherAssert.assertThat(sc.ssl(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(sc.workersCount(), CoreMatchers.is(50));
        MatcherAssert.assertThat(sc.socket("secure").port(), CoreMatchers.is(11));
        MatcherAssert.assertThat(sc.socket("secure").backlog(), CoreMatchers.is(21));
        MatcherAssert.assertThat(sc.socket("secure").receiveBufferSize(), CoreMatchers.is(31));
        MatcherAssert.assertThat(sc.socket("secure").timeoutMillis(), CoreMatchers.is(41));
        MatcherAssert.assertThat(sc.socket("secure").bindAddress(), CoreMatchers.is(InetAddress.getByName("127.0.0.2")));
        MatcherAssert.assertThat(sc.socket("secure").ssl(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(sc.socket("other").port(), CoreMatchers.is(12));
        MatcherAssert.assertThat(sc.socket("other").backlog(), CoreMatchers.is(22));
        MatcherAssert.assertThat(sc.socket("other").receiveBufferSize(), CoreMatchers.is(32));
        MatcherAssert.assertThat(sc.socket("other").timeoutMillis(), CoreMatchers.is(42));
        MatcherAssert.assertThat(sc.socket("other").bindAddress(), CoreMatchers.is(InetAddress.getByName("127.0.0.3")));
        MatcherAssert.assertThat(sc.socket("other").ssl(), CoreMatchers.nullValue());
    }

    @Test
    public void sslFromConfig() throws Exception {
        Config config = Config.builder().sources(ConfigSources.classpath("config-with-ssl.conf")).build();
        ServerConfiguration sc = config.get("webserver").as(ServerConfiguration::create).get();
        MatcherAssert.assertThat(sc, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sc.port(), CoreMatchers.is(10));
        MatcherAssert.assertThat(sc.ssl(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sc.socket("secure").port(), CoreMatchers.is(11));
        MatcherAssert.assertThat(sc.socket("secure").ssl(), CoreMatchers.notNullValue());
    }
}

