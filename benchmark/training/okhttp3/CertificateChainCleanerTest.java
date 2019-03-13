/**
 * Copyright (C) 2016 Square, Inc.
 *
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
 */
package okhttp3;


import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.X509TrustManager;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.tls.HandshakeCertificates;
import okhttp3.tls.HeldCertificate;
import org.junit.Assert;
import org.junit.Test;


public final class CertificateChainCleanerTest {
    @Test
    public void equalsFromCertificate() {
        HeldCertificate rootA = new HeldCertificate.Builder().serialNumber(1L).build();
        HeldCertificate rootB = new HeldCertificate.Builder().serialNumber(2L).build();
        Assert.assertEquals(CertificateChainCleaner.get(rootA.certificate(), rootB.certificate()), CertificateChainCleaner.get(rootB.certificate(), rootA.certificate()));
    }

    @Test
    public void equalsFromTrustManager() {
        HandshakeCertificates handshakeCertificates = new HandshakeCertificates.Builder().build();
        X509TrustManager x509TrustManager = handshakeCertificates.trustManager();
        Assert.assertEquals(CertificateChainCleaner.get(x509TrustManager), CertificateChainCleaner.get(x509TrustManager));
    }

    @Test
    public void normalizeSingleSelfSignedCertificate() throws Exception {
        HeldCertificate root = new HeldCertificate.Builder().serialNumber(1L).build();
        CertificateChainCleaner cleaner = CertificateChainCleaner.get(root.certificate());
        Assert.assertEquals(list(root), cleaner.clean(list(root), "hostname"));
    }

    @Test
    public void normalizeUnknownSelfSignedCertificate() {
        HeldCertificate root = new HeldCertificate.Builder().serialNumber(1L).build();
        CertificateChainCleaner cleaner = CertificateChainCleaner.get();
        try {
            cleaner.clean(list(root), "hostname");
            Assert.fail();
        } catch (SSLPeerUnverifiedException expected) {
        }
    }

    @Test
    public void orderedChainOfCertificatesWithRoot() throws Exception {
        HeldCertificate root = new HeldCertificate.Builder().serialNumber(1L).build();
        HeldCertificate certA = new HeldCertificate.Builder().serialNumber(2L).signedBy(root).build();
        HeldCertificate certB = new HeldCertificate.Builder().serialNumber(3L).signedBy(certA).build();
        CertificateChainCleaner cleaner = CertificateChainCleaner.get(root.certificate());
        Assert.assertEquals(list(certB, certA, root), cleaner.clean(list(certB, certA, root), "hostname"));
    }

    @Test
    public void orderedChainOfCertificatesWithoutRoot() throws Exception {
        HeldCertificate root = new HeldCertificate.Builder().serialNumber(1L).build();
        HeldCertificate certA = new HeldCertificate.Builder().serialNumber(2L).signedBy(root).build();
        HeldCertificate certB = new HeldCertificate.Builder().serialNumber(3L).signedBy(certA).build();
        CertificateChainCleaner cleaner = CertificateChainCleaner.get(root.certificate());
        Assert.assertEquals(list(certB, certA, root), cleaner.clean(list(certB, certA), "hostname"));// Root is added!

    }

    @Test
    public void unorderedChainOfCertificatesWithRoot() throws Exception {
        HeldCertificate root = new HeldCertificate.Builder().serialNumber(1L).build();
        HeldCertificate certA = new HeldCertificate.Builder().serialNumber(2L).signedBy(root).build();
        HeldCertificate certB = new HeldCertificate.Builder().serialNumber(3L).signedBy(certA).build();
        HeldCertificate certC = new HeldCertificate.Builder().serialNumber(4L).signedBy(certB).build();
        CertificateChainCleaner cleaner = CertificateChainCleaner.get(root.certificate());
        Assert.assertEquals(list(certC, certB, certA, root), cleaner.clean(list(certC, certA, root, certB), "hostname"));
    }

    @Test
    public void unorderedChainOfCertificatesWithoutRoot() throws Exception {
        HeldCertificate root = new HeldCertificate.Builder().serialNumber(1L).build();
        HeldCertificate certA = new HeldCertificate.Builder().serialNumber(2L).signedBy(root).build();
        HeldCertificate certB = new HeldCertificate.Builder().serialNumber(3L).signedBy(certA).build();
        HeldCertificate certC = new HeldCertificate.Builder().serialNumber(4L).signedBy(certB).build();
        CertificateChainCleaner cleaner = CertificateChainCleaner.get(root.certificate());
        Assert.assertEquals(list(certC, certB, certA, root), cleaner.clean(list(certC, certA, certB), "hostname"));
    }

    @Test
    public void unrelatedCertificatesAreOmitted() throws Exception {
        HeldCertificate root = new HeldCertificate.Builder().serialNumber(1L).build();
        HeldCertificate certA = new HeldCertificate.Builder().serialNumber(2L).signedBy(root).build();
        HeldCertificate certB = new HeldCertificate.Builder().serialNumber(3L).signedBy(certA).build();
        HeldCertificate certUnnecessary = new HeldCertificate.Builder().serialNumber(4L).build();
        CertificateChainCleaner cleaner = CertificateChainCleaner.get(root.certificate());
        Assert.assertEquals(list(certB, certA, root), cleaner.clean(list(certB, certUnnecessary, certA, root), "hostname"));
    }

    @Test
    public void chainGoesAllTheWayToSelfSignedRoot() throws Exception {
        HeldCertificate selfSigned = new HeldCertificate.Builder().serialNumber(1L).build();
        HeldCertificate trusted = new HeldCertificate.Builder().serialNumber(2L).signedBy(selfSigned).build();
        HeldCertificate certA = new HeldCertificate.Builder().serialNumber(3L).signedBy(trusted).build();
        HeldCertificate certB = new HeldCertificate.Builder().serialNumber(4L).signedBy(certA).build();
        CertificateChainCleaner cleaner = CertificateChainCleaner.get(selfSigned.certificate(), trusted.certificate());
        Assert.assertEquals(list(certB, certA, trusted, selfSigned), cleaner.clean(list(certB, certA), "hostname"));
        Assert.assertEquals(list(certB, certA, trusted, selfSigned), cleaner.clean(list(certB, certA, trusted), "hostname"));
        Assert.assertEquals(list(certB, certA, trusted, selfSigned), cleaner.clean(list(certB, certA, trusted, selfSigned), "hostname"));
    }

    @Test
    public void trustedRootNotSelfSigned() throws Exception {
        HeldCertificate unknownSigner = new HeldCertificate.Builder().serialNumber(1L).build();
        HeldCertificate trusted = new HeldCertificate.Builder().signedBy(unknownSigner).serialNumber(2L).build();
        HeldCertificate intermediateCa = new HeldCertificate.Builder().signedBy(trusted).serialNumber(3L).build();
        HeldCertificate certificate = new HeldCertificate.Builder().signedBy(intermediateCa).serialNumber(4L).build();
        CertificateChainCleaner cleaner = CertificateChainCleaner.get(trusted.certificate());
        Assert.assertEquals(list(certificate, intermediateCa, trusted), cleaner.clean(list(certificate, intermediateCa), "hostname"));
        Assert.assertEquals(list(certificate, intermediateCa, trusted), cleaner.clean(list(certificate, intermediateCa, trusted), "hostname"));
    }

    @Test
    public void chainMaxLength() throws Exception {
        List<HeldCertificate> heldCertificates = chainOfLength(10);
        List<Certificate> certificates = new ArrayList<>();
        for (HeldCertificate heldCertificate : heldCertificates) {
            certificates.add(heldCertificate.certificate());
        }
        X509Certificate root = heldCertificates.get(((heldCertificates.size()) - 1)).certificate();
        CertificateChainCleaner cleaner = CertificateChainCleaner.get(root);
        Assert.assertEquals(certificates, cleaner.clean(certificates, "hostname"));
        Assert.assertEquals(certificates, cleaner.clean(certificates.subList(0, 9), "hostname"));
    }

    @Test
    public void chainTooLong() {
        List<HeldCertificate> heldCertificates = chainOfLength(11);
        List<Certificate> certificates = new ArrayList<>();
        for (HeldCertificate heldCertificate : heldCertificates) {
            certificates.add(heldCertificate.certificate());
        }
        X509Certificate root = heldCertificates.get(((heldCertificates.size()) - 1)).certificate();
        CertificateChainCleaner cleaner = CertificateChainCleaner.get(root);
        try {
            cleaner.clean(certificates, "hostname");
            Assert.fail();
        } catch (SSLPeerUnverifiedException expected) {
        }
    }
}

