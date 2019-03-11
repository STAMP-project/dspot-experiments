/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.lambda.serving;


import com.google.common.io.Resources;
import com.typesafe.config.Config;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.junit.Test;


public final class SecureAPIConfigIT extends AbstractServingIT {
    @Test
    public void testHTTPS() throws Exception {
        Config config = buildHTTPSConfig();
        startServer(config);
        // Turn off actual checking of the dummy SSL cert
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{ SecureAPIConfigIT.ACCEPT_ALL_TM }, null);
        SSLSocketFactory originalFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        try {
            String response = Resources.toString(new URL((("https://localhost:" + (getHTTPSPort())) + "/helloWorld")), StandardCharsets.UTF_8);
            assertEquals("Hello, World", response);
        } finally {
            // Restore original SSL factory
            HttpsURLConnection.setDefaultSSLSocketFactory(originalFactory);
            Files.delete(Paths.get(config.getString("oryx.serving.api.keystore-file")));
        }
    }

    @Test(expected = IOException.class)
    public void testBadHTTPS() throws Exception {
        Config config = buildHTTPSConfig();
        startServer(config);
        try {
            Resources.toString(new URL((("https://localhost:" + (getHTTPSPort())) + "/helloWorld")), StandardCharsets.UTF_8);
        } finally {
            Files.delete(Paths.get(config.getString("oryx.serving.api.keystore-file")));
        }
    }

    @Test
    public void testUserPassword() throws Exception {
        startServer(buildUserPasswordConfig());
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("oryx", "pass".toCharArray());
            }
        });
        try {
            String response = Resources.toString(new URL((("http://localhost:" + (getHTTPPort())) + "/helloWorld")), StandardCharsets.UTF_8);
            assertEquals("Hello, World", response);
        } finally {
            Authenticator.setDefault(null);
        }
    }

    @Test(expected = IOException.class)
    public void testNoUserPassword() throws Exception {
        startServer(buildUserPasswordConfig());
        Resources.toString(new URL((("http://localhost:" + (getHTTPPort())) + "/helloWorld")), StandardCharsets.UTF_8);
    }

    private static final TrustManager ACCEPT_ALL_TM = new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
            // do nothing
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
            // do nothing
        }
    };
}

