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
package zipkin2.server.internal;


import SpringBootTest.WebEnvironment;
import com.linecorp.armeria.server.Server;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zipkin.server.ZipkinServer;


/**
 * Query-only builds should be able to disable the HTTP collector, so that associated assets 404
 * instead of allowing creation of spans.
 */
@SpringBootTest(classes = ZipkinServer.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "zipkin.storage.type="// cheat and test empty storage type
, "spring.config.name=zipkin-server", "zipkin.collector.http.enabled=false" })
@RunWith(SpringRunner.class)
public class ITZipkinServerHttpCollectorDisabled {
    @Autowired
    Server server;

    OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();

    @Test
    public void httpCollectorEndpointReturns404() throws Exception {
        Response response = client.newCall(new Request.Builder().url(ITZipkinServer.url(server, "/api/v2/spans")).post(RequestBody.create(null, "[]")).build()).execute();
        assertThat(response.code()).isEqualTo(404);
    }

    /**
     * Shows the same http path still works for GET
     */
    @Test
    public void getOnSpansEndpointReturnsOK() throws Exception {
        Response response = client.newCall(new Request.Builder().url(ITZipkinServer.url(server, "/api/v2/spans?serviceName=unknown")).build()).execute();
        assertThat(response.isSuccessful()).isTrue();
    }
}

