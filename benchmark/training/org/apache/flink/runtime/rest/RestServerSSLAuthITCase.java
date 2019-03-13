/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.rest;


import RestOptions.ADDRESS;
import RestOptions.BIND_PORT;
import SecurityOptions.SSL_ALGORITHMS;
import SecurityOptions.SSL_REST_AUTHENTICATION_ENABLED;
import SecurityOptions.SSL_REST_ENABLED;
import SecurityOptions.SSL_REST_KEYSTORE;
import SecurityOptions.SSL_REST_KEYSTORE_PASSWORD;
import SecurityOptions.SSL_REST_KEY_PASSWORD;
import SecurityOptions.SSL_REST_TRUSTSTORE;
import SecurityOptions.SSL_REST_TRUSTSTORE_PASSWORD;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLHandshakeException;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.rest.messages.EmptyMessageParameters;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.runtime.rest.messages.EmptyResponseBody;
import org.apache.flink.runtime.rpc.RpcUtils;
import org.apache.flink.runtime.webmonitor.RestfulGateway;
import org.apache.flink.runtime.webmonitor.TestingRestfulGateway;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flink.runtime.rest.RestServerEndpointITCase.TestVersionHeaders.INSTANCE;


/**
 * This test validates that connections are failing when mutual auth is enabled but untrusted
 * keys are used.
 */
public class RestServerSSLAuthITCase extends TestLogger {
    private static final String KEY_STORE_FILE = RestServerSSLAuthITCase.class.getResource("/local127.keystore").getFile();

    private static final String TRUST_STORE_FILE = RestServerSSLAuthITCase.class.getResource("/local127.truststore").getFile();

    private static final String UNTRUSTED_KEY_STORE_FILE = RestServerSSLAuthITCase.class.getResource("/untrusted.keystore").getFile();

    private static final Time timeout = Time.seconds(10L);

    private RestfulGateway restfulGateway;

    @Test
    public void testConnectFailure() throws Exception {
        RestClient restClient = null;
        RestServerEndpoint serverEndpoint = null;
        try {
            final Configuration baseConfig = new Configuration();
            baseConfig.setString(BIND_PORT, "0");
            baseConfig.setString(ADDRESS, "localhost");
            baseConfig.setBoolean(SSL_REST_ENABLED, true);
            baseConfig.setBoolean(SSL_REST_AUTHENTICATION_ENABLED, true);
            baseConfig.setString(SSL_ALGORITHMS, "TLS_RSA_WITH_AES_128_CBC_SHA");
            Configuration serverConfig = new Configuration(baseConfig);
            serverConfig.setString(SSL_REST_TRUSTSTORE, RestServerSSLAuthITCase.TRUST_STORE_FILE);
            serverConfig.setString(SSL_REST_TRUSTSTORE_PASSWORD, "password");
            serverConfig.setString(SSL_REST_KEYSTORE, RestServerSSLAuthITCase.KEY_STORE_FILE);
            serverConfig.setString(SSL_REST_KEYSTORE_PASSWORD, "password");
            serverConfig.setString(SSL_REST_KEY_PASSWORD, "password");
            Configuration clientConfig = new Configuration(baseConfig);
            clientConfig.setString(SSL_REST_TRUSTSTORE, RestServerSSLAuthITCase.UNTRUSTED_KEY_STORE_FILE);
            clientConfig.setString(SSL_REST_TRUSTSTORE_PASSWORD, "password");
            clientConfig.setString(SSL_REST_KEYSTORE, RestServerSSLAuthITCase.KEY_STORE_FILE);
            clientConfig.setString(SSL_REST_KEYSTORE_PASSWORD, "password");
            clientConfig.setString(SSL_REST_KEY_PASSWORD, "password");
            RestServerEndpointConfiguration restServerConfig = RestServerEndpointConfiguration.fromConfiguration(serverConfig);
            RestClientConfiguration restClientConfig = RestClientConfiguration.fromConfiguration(clientConfig);
            RestfulGateway restfulGateway = TestingRestfulGateway.newBuilder().build();
            RestServerEndpointITCase.TestVersionHandler testVersionHandler = new RestServerEndpointITCase.TestVersionHandler(() -> CompletableFuture.completedFuture(restfulGateway), RpcUtils.INF_TIMEOUT);
            serverEndpoint = new RestServerEndpointITCase.TestRestServerEndpoint(restServerConfig, Arrays.asList(Tuple2.of(getMessageHeaders(), testVersionHandler)));
            restClient = new RestServerEndpointITCase.TestRestClient(restClientConfig);
            serverEndpoint.start();
            CompletableFuture<EmptyResponseBody> response = restClient.sendRequest(serverEndpoint.getServerAddress().getHostName(), serverEndpoint.getServerAddress().getPort(), INSTANCE, EmptyMessageParameters.getInstance(), EmptyRequestBody.getInstance(), Collections.emptyList());
            response.get(60, TimeUnit.SECONDS);
            Assert.fail("should never complete normally");
        } catch (ExecutionException exception) {
            // that is what we want
            Assert.assertTrue(ExceptionUtils.findThrowable(exception, SSLHandshakeException.class).isPresent());
        } finally {
            if (restClient != null) {
                restClient.shutdown(RestServerSSLAuthITCase.timeout);
            }
            if (serverEndpoint != null) {
                serverEndpoint.close();
            }
        }
    }
}

