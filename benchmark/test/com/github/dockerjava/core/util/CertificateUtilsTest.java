package com.github.dockerjava.core.util;


import java.security.KeyStore;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class CertificateUtilsTest {
    private static final String baseDir = CertificateUtilsTest.class.getResource(((CertificateUtilsTest.class.getSimpleName()) + "/")).getFile();

    @Test
    public void allFilesExist() {
        MatcherAssert.assertThat(CertificateUtils.verifyCertificatesExist(((CertificateUtilsTest.baseDir) + "allFilesExist")), Is.is(true));
    }

    @Test
    public void caAndCertAndKeyMissing() {
        MatcherAssert.assertThat(CertificateUtils.verifyCertificatesExist(((CertificateUtilsTest.baseDir) + "caAndCertAndKeyMissing")), Is.is(false));
    }

    @Test
    public void caAndCertMissing() {
        MatcherAssert.assertThat(CertificateUtils.verifyCertificatesExist(((CertificateUtilsTest.baseDir) + "caAndCertMissing")), Is.is(false));
    }

    @Test
    public void caAndKeyMissing() {
        MatcherAssert.assertThat(CertificateUtils.verifyCertificatesExist(((CertificateUtilsTest.baseDir) + "caAndKeyMissing")), Is.is(false));
    }

    @Test
    public void caMissing() {
        MatcherAssert.assertThat(CertificateUtils.verifyCertificatesExist(((CertificateUtilsTest.baseDir) + "caMissing")), Is.is(false));
    }

    @Test
    public void certAndKeyMissing() {
        MatcherAssert.assertThat(CertificateUtils.verifyCertificatesExist(((CertificateUtilsTest.baseDir) + "certAndKeyMissing")), Is.is(false));
    }

    @Test
    public void certMissing() {
        MatcherAssert.assertThat(CertificateUtils.verifyCertificatesExist(((CertificateUtilsTest.baseDir) + "certMissing")), Is.is(false));
    }

    @Test
    public void keyMissing() {
        MatcherAssert.assertThat(CertificateUtils.verifyCertificatesExist(((CertificateUtilsTest.baseDir) + "keyMissing")), Is.is(false));
    }

    @Test
    public void readCaCert() throws Exception {
        String capem = readFileAsString("caTest/single_ca.pem");
        KeyStore keyStore = CertificateUtils.createTrustStore(capem);
        MatcherAssert.assertThat(keyStore.size(), Is.is(1));
        MatcherAssert.assertThat(keyStore.isCertificateEntry("ca-1"), Is.is(true));
    }

    @Test
    public void readMultipleCaCerts() throws Exception {
        String capem = readFileAsString("caTest/multiple_ca.pem");
        KeyStore keyStore = CertificateUtils.createTrustStore(capem);
        MatcherAssert.assertThat(keyStore.size(), Is.is(2));
        MatcherAssert.assertThat(keyStore.isCertificateEntry("ca-1"), Is.is(true));
        MatcherAssert.assertThat(keyStore.isCertificateEntry("ca-2"), Is.is(true));
    }
}

