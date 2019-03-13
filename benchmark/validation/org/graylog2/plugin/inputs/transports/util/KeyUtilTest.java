/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.plugin.inputs.transports.util;


import com.google.common.collect.ImmutableMap;
import io.netty.handler.ssl.SslHandler;
import java.io.File;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Objects;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class KeyUtilTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final ImmutableMap<String, String> CERTIFICATES = ImmutableMap.of("RSA", "server.crt.rsa", "DSA", "server.crt.dsa", "ECDSA", "server.crt.ecdsa");

    private final String keyAlgorithm;

    private final String keyFileName;

    private final String keyPassword;

    private final Class<? extends Exception> exceptionClass;

    private final String exceptionMessage;

    public KeyUtilTest(String keyAlgorithm, String keyFileName, String keyPassword, Class<? extends Exception> exceptionClass, String exceptionMessage) {
        this.keyAlgorithm = Objects.requireNonNull(keyAlgorithm);
        this.keyFileName = Objects.requireNonNull(keyFileName);
        this.keyPassword = keyPassword;
        this.exceptionClass = exceptionClass;
        this.exceptionMessage = exceptionMessage;
    }

    @Test
    public void testLoadCertificates() throws Exception {
        final File certFile = resourceToFile(KeyUtilTest.CERTIFICATES.get(keyAlgorithm));
        final Collection<? extends Certificate> certificates = KeyUtil.loadCertificates(certFile.toPath());
        assertThat(certificates).isNotEmpty().hasOnlyElementsOfType(X509Certificate.class);
    }

    @Test
    public void testLoadPrivateKey() throws Exception {
        if ((exceptionClass) != null) {
            expectedException.expect(exceptionClass);
            expectedException.expectMessage(exceptionMessage);
        }
        final File keyFile = resourceToFile(keyFileName);
        final PrivateKey privateKey = KeyUtil.loadPrivateKey(keyFile, keyPassword);
        assertThat(privateKey).isNotNull();
    }

    @Test
    public void testCreateNettySslHandler() throws Exception {
        if ((exceptionClass) != null) {
            expectedException.expect(exceptionClass);
            expectedException.expectMessage(exceptionMessage);
        }
        final File keyFile = resourceToFile(keyFileName);
        final File certFile = resourceToFile(KeyUtilTest.CERTIFICATES.get(keyAlgorithm));
        final KeyManager[] keyManagers = KeyUtil.initKeyStore(keyFile, certFile, keyPassword);
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, new TrustManager[0], new SecureRandom());
        assertThat(sslContext.getProtocol()).isEqualTo("TLS");
        final SSLEngine sslEngine = sslContext.createSSLEngine();
        assertThat(sslEngine.getEnabledCipherSuites()).isNotEmpty();
        assertThat(sslEngine.getEnabledProtocols()).isNotEmpty();
        final SslHandler sslHandler = new SslHandler(sslEngine);
        assertThat(sslHandler).isNotNull();
    }
}

