/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2017 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.docker.client;


import com.google.common.base.Optional;
import com.spotify.docker.client.DockerCertificates.SslContextFactory;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class DockerCertificatesTest {
    private static final boolean TRAVIS = "true".equals(System.getenv("TRAVIS"));

    private SslContextFactory factory = Mockito.mock(SslContextFactory.class);

    private ArgumentCaptor<KeyStore> keyStore = ArgumentCaptor.forClass(KeyStore.class);

    private ArgumentCaptor<KeyStore> trustStore = ArgumentCaptor.forClass(KeyStore.class);

    private ArgumentCaptor<char[]> password = ArgumentCaptor.forClass(char[].class);

    @Test(expected = DockerCertificateException.class)
    public void testBadDockerCertificates() throws Exception {
        // try building a DockerCertificates with specifying a cert path to something that
        // isn't a cert
        DockerCertificates.builder().dockerCertPath(getResourceFile("dockerInvalidSslDirectory")).build();
    }

    @Test
    public void testNoDockerCertificatesInDir() throws Exception {
        final Path certDir = Paths.get(System.getProperty("java.io.tmpdir"));
        final Optional<DockerCertificatesStore> result = DockerCertificates.builder().dockerCertPath(certDir).build();
        Assert.assertThat(result.isPresent(), Matchers.is(false));
    }

    @Test
    public void testDefaultDockerCertificates() throws Exception {
        DockerCertificates.builder().dockerCertPath(getCertPath()).sslFactory(factory).build();
        Mockito.verify(factory).newSslContext(keyStore.capture(), password.capture(), trustStore.capture());
        final KeyStore.PrivateKeyEntry pkEntry = ((KeyStore.PrivateKeyEntry) (keyStore.getValue().getEntry("key", new KeyStore.PasswordProtection(password.getValue()))));
        final KeyStore caKeyStore = trustStore.getValue();
        Assert.assertNotNull(pkEntry);
        Assert.assertNotNull(pkEntry.getCertificate());
        Assert.assertNotNull(caKeyStore.getCertificate("o=boot2docker"));
    }

    @Test
    public void testDockerCertificatesWithMultiCa() throws Exception {
        DockerCertificates.builder().dockerCertPath(getCertPath()).caCertPath(getVariant("ca-multi.pem")).sslFactory(factory).build();
        Mockito.verify(factory).newSslContext(keyStore.capture(), password.capture(), trustStore.capture());
        final KeyStore.PrivateKeyEntry pkEntry = ((KeyStore.PrivateKeyEntry) (keyStore.getValue().getEntry("key", new KeyStore.PasswordProtection(password.getValue()))));
        Assert.assertNotNull(pkEntry);
        Assert.assertNotNull(pkEntry.getCertificate());
        Assert.assertNotNull(trustStore.getValue().getCertificate("cn=ca-test,o=internet widgits pty ltd,st=some-state,c=cr"));
        Assert.assertNotNull(trustStore.getValue().getCertificate("cn=ca-test-2,o=internet widgits pty ltd,st=some-state,c=cr"));
    }

    @Test
    public void testReadPrivateKeyPkcs1() throws Exception {
        DockerCertificates.builder().dockerCertPath(getCertPath()).clientKeyPath(getVariant("key-pkcs1.pem")).sslFactory(factory).build();
        Mockito.verify(factory).newSslContext(keyStore.capture(), password.capture(), trustStore.capture());
        final KeyStore.PrivateKeyEntry pkEntry = ((KeyStore.PrivateKeyEntry) (keyStore.getValue().getEntry("key", new KeyStore.PasswordProtection(password.getValue()))));
        Assert.assertNotNull(pkEntry.getPrivateKey());
    }

    @Test
    public void testReadPrivateKeyPkcs8() throws Exception {
        DockerCertificates.builder().dockerCertPath(getCertPath()).clientKeyPath(getVariant("key-pkcs8.pem")).sslFactory(factory).build();
        Mockito.verify(factory).newSslContext(keyStore.capture(), password.capture(), trustStore.capture());
        final KeyStore.PrivateKeyEntry pkEntry = ((KeyStore.PrivateKeyEntry) (keyStore.getValue().getEntry("key", new KeyStore.PasswordProtection(password.getValue()))));
        Assert.assertNotNull(pkEntry.getPrivateKey());
    }

    @Test
    public void testReadEllipticCurvePrivateKey() throws Exception {
        Assume.assumeFalse("Travis' openjdk7 doesn't support the elliptic curve algorithm", DockerCertificatesTest.TRAVIS);
        DockerCertificates.builder().dockerCertPath(getResourceFile("dockerSslDirectoryWithEcKey")).sslFactory(factory).build();
        Mockito.verify(factory).newSslContext(keyStore.capture(), password.capture(), trustStore.capture());
        final KeyStore.PrivateKeyEntry pkEntry = ((KeyStore.PrivateKeyEntry) (keyStore.getValue().getEntry("key", new KeyStore.PasswordProtection(password.getValue()))));
        Assert.assertNotNull(pkEntry.getPrivateKey());
    }
}

