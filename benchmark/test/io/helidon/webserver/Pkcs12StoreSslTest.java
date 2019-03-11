/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
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


import io.helidon.common.configurable.Resource;
import io.helidon.common.pki.KeyConfig;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * The Pkcs12StoreSslTest.
 */
public class Pkcs12StoreSslTest {
    private static Client client;

    @Test
    public void testPkcs12() throws Exception {
        SSLContext sslContext = SSLContextBuilder.create(KeyConfig.keystoreBuilder().keystore(Resource.create("ssl/certificate.p12")).keystorePassphrase(new char[]{ 'h', 'e', 'l', 'i', 'd', 'o', 'n' }).build()).build();
        WebServer otherWebServer = WebServer.create(ServerConfiguration.builder().ssl(sslContext), Routing.builder().any(( req, res) -> res.send("It works!")).build()).start().toCompletableFuture().get(10, TimeUnit.SECONDS);
        otherWebServer.start().toCompletableFuture().join();
        try {
            WebTarget target = Pkcs12StoreSslTest.client.target(("https://localhost:" + (otherWebServer.port())));
            Response response = target.request().get();
            MatcherAssert.assertThat(("Unexpected content; returned status code: " + (response.getStatus())), response.readEntity(String.class), CoreMatchers.is("It works!"));
        } finally {
            otherWebServer.shutdown().toCompletableFuture().join();
        }
    }
}

