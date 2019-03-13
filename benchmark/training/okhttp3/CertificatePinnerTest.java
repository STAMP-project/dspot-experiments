/**
 * Copyright (C) 2014 Square, Inc.
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


import CertificatePinner.Builder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;
import okhttp3.CertificatePinner.Pin;
import okhttp3.tls.HeldCertificate;
import org.junit.Assert;
import org.junit.Test;


public final class CertificatePinnerTest {
    static HeldCertificate certA1;

    static String certA1Sha256Pin;

    static HeldCertificate certB1;

    static String certB1Sha256Pin;

    static HeldCertificate certC1;

    static String certC1Sha256Pin;

    static {
        CertificatePinnerTest.certA1 = new HeldCertificate.Builder().serialNumber(100L).build();
        CertificatePinnerTest.certA1Sha256Pin = "sha256/" + (CertificatePinner.sha256(CertificatePinnerTest.certA1.certificate()).base64());
        CertificatePinnerTest.certB1 = new HeldCertificate.Builder().serialNumber(200L).build();
        CertificatePinnerTest.certB1Sha256Pin = "sha256/" + (CertificatePinner.sha256(CertificatePinnerTest.certB1.certificate()).base64());
        CertificatePinnerTest.certC1 = new HeldCertificate.Builder().serialNumber(300L).build();
        CertificatePinnerTest.certC1Sha256Pin = "sha256/" + (CertificatePinner.sha256(CertificatePinnerTest.certC1.certificate()).base64());
    }

    @Test
    public void malformedPin() throws Exception {
        CertificatePinner.Builder builder = new CertificatePinner.Builder();
        try {
            builder.add("example.com", "md5/DmxUShsZuNiqPQsX2Oi9uv2sCnw=");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void malformedBase64() throws Exception {
        CertificatePinner.Builder builder = new CertificatePinner.Builder();
        try {
            builder.add("example.com", "sha1/DmxUShsZuNiqPQsX2Oi9uv2sCnw*");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * Multiple certificates generated from the same keypair have the same pin.
     */
    @Test
    public void sameKeypairSamePin() throws Exception {
        HeldCertificate heldCertificateA2 = new HeldCertificate.Builder().keyPair(CertificatePinnerTest.certA1.keyPair()).serialNumber(101L).build();
        String keypairACertificate2Pin = CertificatePinner.pin(heldCertificateA2.certificate());
        HeldCertificate heldCertificateB2 = new HeldCertificate.Builder().keyPair(CertificatePinnerTest.certB1.keyPair()).serialNumber(201L).build();
        String keypairBCertificate2Pin = CertificatePinner.pin(heldCertificateB2.certificate());
        Assert.assertEquals(CertificatePinnerTest.certA1Sha256Pin, keypairACertificate2Pin);
        Assert.assertEquals(CertificatePinnerTest.certB1Sha256Pin, keypairBCertificate2Pin);
        Assert.assertNotEquals(CertificatePinnerTest.certA1Sha256Pin, CertificatePinnerTest.certB1Sha256Pin);
    }

    @Test
    public void successfulCheck() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("example.com", CertificatePinnerTest.certA1Sha256Pin).build();
        certificatePinner.check("example.com", CertificatePinnerTest.certA1.certificate());
    }

    @Test
    public void successfulCheckSha1Pin() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("example.com", ("sha1/" + (CertificatePinner.sha1(CertificatePinnerTest.certA1.certificate()).base64()))).build();
        certificatePinner.check("example.com", CertificatePinnerTest.certA1.certificate());
    }

    @Test
    public void successfulMatchAcceptsAnyMatchingCertificate() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("example.com", CertificatePinnerTest.certB1Sha256Pin).build();
        certificatePinner.check("example.com", CertificatePinnerTest.certA1.certificate(), CertificatePinnerTest.certB1.certificate());
    }

    @Test
    public void unsuccessfulCheck() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("example.com", CertificatePinnerTest.certA1Sha256Pin).build();
        try {
            certificatePinner.check("example.com", CertificatePinnerTest.certB1.certificate());
            Assert.fail();
        } catch (SSLPeerUnverifiedException expected) {
        }
    }

    @Test
    public void multipleCertificatesForOneHostname() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("example.com", CertificatePinnerTest.certA1Sha256Pin, CertificatePinnerTest.certB1Sha256Pin).build();
        certificatePinner.check("example.com", CertificatePinnerTest.certA1.certificate());
        certificatePinner.check("example.com", CertificatePinnerTest.certB1.certificate());
    }

    @Test
    public void multipleHostnamesForOneCertificate() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("example.com", CertificatePinnerTest.certA1Sha256Pin).add("www.example.com", CertificatePinnerTest.certA1Sha256Pin).build();
        certificatePinner.check("example.com", CertificatePinnerTest.certA1.certificate());
        certificatePinner.check("www.example.com", CertificatePinnerTest.certA1.certificate());
    }

    @Test
    public void absentHostnameMatches() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().build();
        certificatePinner.check("example.com", CertificatePinnerTest.certA1.certificate());
    }

    @Test
    public void successfulCheckForWildcardHostname() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("*.example.com", CertificatePinnerTest.certA1Sha256Pin).build();
        certificatePinner.check("a.example.com", CertificatePinnerTest.certA1.certificate());
    }

    @Test
    public void successfulMatchAcceptsAnyMatchingCertificateForWildcardHostname() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("*.example.com", CertificatePinnerTest.certB1Sha256Pin).build();
        certificatePinner.check("a.example.com", CertificatePinnerTest.certA1.certificate(), CertificatePinnerTest.certB1.certificate());
    }

    @Test
    public void unsuccessfulCheckForWildcardHostname() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("*.example.com", CertificatePinnerTest.certA1Sha256Pin).build();
        try {
            certificatePinner.check("a.example.com", CertificatePinnerTest.certB1.certificate());
            Assert.fail();
        } catch (SSLPeerUnverifiedException expected) {
        }
    }

    @Test
    public void multipleCertificatesForOneWildcardHostname() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("*.example.com", CertificatePinnerTest.certA1Sha256Pin, CertificatePinnerTest.certB1Sha256Pin).build();
        certificatePinner.check("a.example.com", CertificatePinnerTest.certA1.certificate());
        certificatePinner.check("a.example.com", CertificatePinnerTest.certB1.certificate());
    }

    @Test
    public void successfulCheckForOneHostnameWithWildcardAndDirectCertificate() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("*.example.com", CertificatePinnerTest.certA1Sha256Pin).add("a.example.com", CertificatePinnerTest.certB1Sha256Pin).build();
        certificatePinner.check("a.example.com", CertificatePinnerTest.certA1.certificate());
        certificatePinner.check("a.example.com", CertificatePinnerTest.certB1.certificate());
    }

    @Test
    public void unsuccessfulCheckForOneHostnameWithWildcardAndDirectCertificate() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("*.example.com", CertificatePinnerTest.certA1Sha256Pin).add("a.example.com", CertificatePinnerTest.certB1Sha256Pin).build();
        try {
            certificatePinner.check("a.example.com", CertificatePinnerTest.certC1.certificate());
            Assert.fail();
        } catch (SSLPeerUnverifiedException expected) {
        }
    }

    @Test
    public void successfulFindMatchingPins() {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("first.com", CertificatePinnerTest.certA1Sha256Pin, CertificatePinnerTest.certB1Sha256Pin).add("second.com", CertificatePinnerTest.certC1Sha256Pin).build();
        List<Pin> expectedPins = Arrays.asList(new Pin("first.com", CertificatePinnerTest.certA1Sha256Pin), new Pin("first.com", CertificatePinnerTest.certB1Sha256Pin));
        Assert.assertEquals(expectedPins, certificatePinner.findMatchingPins("first.com"));
    }

    @Test
    public void successfulFindMatchingPinsForWildcardAndDirectCertificates() {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("*.example.com", CertificatePinnerTest.certA1Sha256Pin).add("a.example.com", CertificatePinnerTest.certB1Sha256Pin).add("b.example.com", CertificatePinnerTest.certC1Sha256Pin).build();
        List<Pin> expectedPins = Arrays.asList(new Pin("*.example.com", CertificatePinnerTest.certA1Sha256Pin), new Pin("a.example.com", CertificatePinnerTest.certB1Sha256Pin));
        Assert.assertEquals(expectedPins, certificatePinner.findMatchingPins("a.example.com"));
    }

    @Test
    public void wildcardHostnameShouldNotMatchThroughDot() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("*.example.com", CertificatePinnerTest.certA1Sha256Pin).build();
        Assert.assertEquals(Collections.emptyList(), certificatePinner.findMatchingPins("example.com"));
        Assert.assertEquals(Collections.emptyList(), certificatePinner.findMatchingPins("a.b.example.com"));
    }

    @Test
    public void successfulFindMatchingPinsIgnoresCase() {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("EXAMPLE.com", CertificatePinnerTest.certA1Sha256Pin).add("*.MyExample.Com", CertificatePinnerTest.certB1Sha256Pin).build();
        List<Pin> expectedPin1 = Arrays.asList(new Pin("EXAMPLE.com", CertificatePinnerTest.certA1Sha256Pin));
        Assert.assertEquals(expectedPin1, certificatePinner.findMatchingPins("example.com"));
        List<Pin> expectedPin2 = Arrays.asList(new Pin("*.MyExample.Com", CertificatePinnerTest.certB1Sha256Pin));
        Assert.assertEquals(expectedPin2, certificatePinner.findMatchingPins("a.myexample.com"));
    }

    @Test
    public void successfulFindMatchingPinPunycode() {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("?khttp.com", CertificatePinnerTest.certA1Sha256Pin).build();
        List<Pin> expectedPin = Arrays.asList(new Pin("?khttp.com", CertificatePinnerTest.certA1Sha256Pin));
        Assert.assertEquals(expectedPin, certificatePinner.findMatchingPins("xn--khttp-fde.com"));
    }

    /**
     * https://github.com/square/okhttp/issues/3324
     */
    @Test
    public void checkSubstringMatch() throws Exception {
        CertificatePinner certificatePinner = new CertificatePinner.Builder().add("*.example.com", CertificatePinnerTest.certA1Sha256Pin).build();
        Assert.assertEquals(Collections.emptyList(), certificatePinner.findMatchingPins("a.example.com.notexample.com"));
        Assert.assertEquals(Collections.emptyList(), certificatePinner.findMatchingPins("example.com.notexample.com"));
        Assert.assertEquals(Collections.emptyList(), certificatePinner.findMatchingPins("notexample.com"));
        Assert.assertEquals(Collections.emptyList(), certificatePinner.findMatchingPins("example.com"));
        Assert.assertEquals(Collections.emptyList(), certificatePinner.findMatchingPins("a.b.example.com"));
        Assert.assertEquals(Collections.emptyList(), certificatePinner.findMatchingPins("ple.com"));
        Assert.assertEquals(Collections.emptyList(), certificatePinner.findMatchingPins("com"));
        Pin expectedPin = new Pin("*.example.com", CertificatePinnerTest.certA1Sha256Pin);
        Assert.assertEquals(Collections.singletonList(expectedPin), certificatePinner.findMatchingPins("a.example.com"));
        Assert.assertEquals(Collections.singletonList(expectedPin), certificatePinner.findMatchingPins("example.example.com"));
    }
}

