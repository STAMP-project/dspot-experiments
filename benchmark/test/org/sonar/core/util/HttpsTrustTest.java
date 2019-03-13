/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.util;


import HttpsTrust.AlwaysTrustManager;
import HttpsTrust.INSTANCE;
import HttpsTrust.Ssl;
import java.security.KeyManagementException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class HttpsTrustTest {
    @Test
    public void trustAllHosts() throws Exception {
        HttpsURLConnection connection = newHttpsConnection();
        INSTANCE.trust(connection);
        assertThat(connection.getHostnameVerifier()).isNotNull();
        assertThat(connection.getHostnameVerifier().verify("foo", null)).isTrue();
    }

    @Test
    public void singleHostnameVerifier() throws Exception {
        HttpsURLConnection connection1 = newHttpsConnection();
        INSTANCE.trust(connection1);
        HttpsURLConnection connection2 = newHttpsConnection();
        INSTANCE.trust(connection2);
        assertThat(connection1.getHostnameVerifier()).isSameAs(connection2.getHostnameVerifier());
    }

    @Test
    public void trustAllCerts() throws Exception {
        HttpsURLConnection connection1 = newHttpsConnection();
        INSTANCE.trust(connection1);
        assertThat(connection1.getSSLSocketFactory()).isNotNull();
        assertThat(connection1.getSSLSocketFactory().getDefaultCipherSuites()).isNotEmpty();
    }

    @Test
    public void singleSslFactory() throws Exception {
        HttpsURLConnection connection1 = newHttpsConnection();
        INSTANCE.trust(connection1);
        HttpsURLConnection connection2 = newHttpsConnection();
        INSTANCE.trust(connection2);
        assertThat(connection1.getSSLSocketFactory()).isSameAs(connection2.getSSLSocketFactory());
    }

    @Test
    public void testAlwaysTrustManager() {
        HttpsTrust.AlwaysTrustManager manager = new HttpsTrust.AlwaysTrustManager();
        assertThat(manager.getAcceptedIssuers()).isEmpty();
        // does nothing
        manager.checkClientTrusted(null, null);
        manager.checkServerTrusted(null, null);
    }

    @Test
    public void failOnError() throws Exception {
        HttpsTrust.Ssl context = Mockito.mock(Ssl.class);
        KeyManagementException cause = new KeyManagementException("foo");
        Mockito.when(context.newFactory(ArgumentMatchers.any(TrustManager.class))).thenThrow(cause);
        try {
            new HttpsTrust(context);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("Fail to build SSL factory");
            assertThat(e.getCause()).isSameAs(cause);
        }
    }
}

