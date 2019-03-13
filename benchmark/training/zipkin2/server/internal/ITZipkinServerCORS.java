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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zipkin.server.ZipkinServer;


/**
 * Integration test suite for CORS configuration.
 *
 * Verifies that allowed-origins can be configured via properties (zipkin.query.allowed-origins).
 */
@SpringBootTest(classes = ZipkinServer.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "spring.config.name=zipkin-server", "zipkin.query.allowed-origins=" + (ITZipkinServerCORS.ALLOWED_ORIGIN) })
@RunWith(SpringRunner.class)
public class ITZipkinServerCORS {
    static final String ALLOWED_ORIGIN = "http://foo.example.com";

    static final String DISALLOWED_ORIGIN = "http://bar.example.com";

    @Autowired
    Server server;

    OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();

    /**
     * Notably, javascript makes pre-flight requests, and won't POST spans if disallowed!
     */
    @Test
    public void shouldAllowConfiguredOrigin_preflight() throws Exception {
        ITZipkinServerCORS.shouldPermitPreflight(optionsForOrigin("/api/v2/traces", ITZipkinServerCORS.ALLOWED_ORIGIN));
        ITZipkinServerCORS.shouldPermitPreflight(optionsForOrigin("/api/v2/spans", ITZipkinServerCORS.ALLOWED_ORIGIN));
    }

    @Test
    public void shouldAllowConfiguredOrigin() throws Exception {
        ITZipkinServerCORS.shouldAllowConfiguredOrigin(getTracesFromOrigin(ITZipkinServerCORS.ALLOWED_ORIGIN));
        ITZipkinServerCORS.shouldAllowConfiguredOrigin(postSpansFromOrigin(ITZipkinServerCORS.ALLOWED_ORIGIN));
    }

    @Test
    public void shouldDisallowOrigin() throws Exception {
        ITZipkinServerCORS.shouldDisallowOrigin(getTracesFromOrigin(ITZipkinServerCORS.DISALLOWED_ORIGIN));
        ITZipkinServerCORS.shouldDisallowOrigin(postSpansFromOrigin(ITZipkinServerCORS.DISALLOWED_ORIGIN));
    }
}

