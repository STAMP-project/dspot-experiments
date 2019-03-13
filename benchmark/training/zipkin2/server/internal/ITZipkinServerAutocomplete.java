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


import SpanBytesEncoder.JSON_V2;
import SpringBootTest.WebEnvironment;
import com.linecorp.armeria.server.Server;
import java.util.Arrays;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zipkin.server.ZipkinServer;
import zipkin2.Span;
import zipkin2.TestObjects;


/**
 * Integration test suite for autocomplete tags.
 *
 * Verifies that the whitelist of key can be configured via "zipkin.storage.autocomplete-keys".
 */
@SpringBootTest(classes = ZipkinServer.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "spring.config.name=zipkin-server", "zipkin.storage.autocomplete-keys=environment,clnt/finagle.version" })
@RunWith(SpringRunner.class)
public class ITZipkinServerAutocomplete {
    @Autowired
    Server server;

    OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();

    @Test
    public void setsCacheControlOnAutocompleteKeysEndpoint() throws Exception {
        assertThat(get("/api/v2/autocompleteKeys").header("Cache-Control")).isEqualTo("max-age=300, must-revalidate");
    }

    @Test
    public void setsCacheControlOnAutocompleteEndpointWhenMoreThan3Values() throws Exception {
        assertThat(get("/api/v2/autocompleteValues?key=environment").header("Cache-Control")).isNull();
        assertThat(get("/api/v2/autocompleteValues?key=clnt/finagle.version").header("Cache-Control")).isNull();
        for (int i = 0; i < 4; i++) {
            post("/api/v2/spans", JSON_V2.encodeList(Arrays.asList(Span.newBuilder().traceId("a").id((i + 1)).timestamp(TestObjects.TODAY).name("whopper").putTag("clnt/finagle.version", ("6.45." + i)).build())));
        }
        assertThat(get("/api/v2/autocompleteValues?key=environment").header("Cache-Control")).isNull();
        assertThat(get("/api/v2/autocompleteValues?key=clnt/finagle.version").header("Cache-Control")).isEqualTo("max-age=300, must-revalidate");
    }
}

