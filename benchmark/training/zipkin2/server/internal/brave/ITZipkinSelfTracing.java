/**
 * Copyright 2015-2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.server.internal.brave;


import SpringBootTest.WebEnvironment;
import com.linecorp.armeria.server.Server;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zipkin.server.ZipkinServer;


@SpringBootTest(classes = ZipkinServer.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "spring.config.name=zipkin-server", "zipkin.self-tracing.enabled=true" })
@RunWith(SpringRunner.class)
public class ITZipkinSelfTracing {
    @Autowired
    TracingStorageComponent storageComponent;

    @Autowired
    Server server;

    OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();

    @Test
    public void getIsTraced_v2() throws Exception {
        assertThat(get("v2").body().string()).isEqualTo("[]");
        Thread.sleep(1000);
        assertThat(get("v2").body().string()).isEqualTo("[\"zipkin-server\"]");
    }

    @Test
    public void postIsTraced_v1() throws Exception {
        post("v1");
        Thread.sleep(1000);
        assertThat(get("v2").body().string()).isEqualTo("[\"zipkin-server\"]");
    }

    @Test
    public void postIsTraced_v2() throws Exception {
        post("v2");
        Thread.sleep(1000);
        assertThat(get("v2").body().string()).isEqualTo("[\"zipkin-server\"]");
    }
}

