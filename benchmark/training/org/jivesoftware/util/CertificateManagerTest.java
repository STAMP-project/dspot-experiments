package org.jivesoftware.util;


import Extension.subjectAlternativeName;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.junit.Assert;
import org.junit.Test;
import sun.security.x509.GeneralNameInterface;


/**
 * Unit test to validate the functionality of @{link CertificateManager}.
 *
 * @author Guus der Kinderen, guus@goodbytes.nl
 */
public class CertificateManagerTest {
    public static final ASN1ObjectIdentifier XMPP_ADDR_OID = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.8.5");

    public static final ASN1ObjectIdentifier DNS_SRV_OID = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.8.7");

    private static KeyPairGenerator keyPairGenerator;

    private static KeyPair subjectKeyPair;

    private static KeyPair issuerKeyPair;

    private static ContentSigner contentSigner;

    /**
     * {@link CertificateManager#getServerIdentities(X509Certificate)} should return:
     * <ul>
     *     <li>the Common Name</li>
     * </ul>
     *
     * when a certificate contains:
     * <ul>
     *     <li>no other identifiers than its CommonName</li>
     * </ul>
     */
    @Test
    public void testServerIdentitiesCommonNameOnly() throws Exception {
        // Setup fixture.
        final String subjectCommonName = "MySubjectCommonName";
        final X509v3CertificateBuilder builder = // Issuer
        // Random serial number
        // Not before 30 days ago
        // Not after 99 days from now
        // Subject
        new org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder(new X500Name("CN=MyIssuer"), BigInteger.valueOf(Math.abs(new SecureRandom().nextInt())), new Date(((System.currentTimeMillis()) - ((((1000L * 60) * 60) * 24) * 30))), new Date(((System.currentTimeMillis()) + ((((1000L * 60) * 60) * 24) * 99))), new X500Name(("CN=" + subjectCommonName)), CertificateManagerTest.subjectKeyPair.getPublic());
        final X509CertificateHolder certificateHolder = builder.build(CertificateManagerTest.contentSigner);
        final X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certificateHolder);
        // Execute system under test
        final List<String> serverIdentities = CertificateManager.getServerIdentities(cert);
        // Verify result
        Assert.assertEquals(1, serverIdentities.size());
        Assert.assertEquals(subjectCommonName, serverIdentities.get(0));
    }

    /**
     * {@link CertificateManager#getServerIdentities(X509Certificate)} should return:
     * <ul>
     *     <li>the 'xmppAddr' subjectAltName value</li>
     *     <li>explicitly not the Common Name</li>
     * </ul>
     *
     * when a certificate contains:
     * <ul>
     *     <li>a subjectAltName entry of type otherName with an ASN.1 Object Identifier of "id-on-xmppAddr"</li>
     * </ul>
     */
    @Test
    public void testServerIdentitiesXmppAddr() throws Exception {
        // Setup fixture.
        final String subjectCommonName = "MySubjectCommonName";
        final String subjectAltNameXmppAddr = "MySubjectAltNameXmppAddr";
        final X509v3CertificateBuilder builder = // Issuer
        // Random serial number
        // Not before 30 days ago
        // Not after 99 days from now
        // Subject
        new org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder(new X500Name("CN=MyIssuer"), BigInteger.valueOf(Math.abs(new SecureRandom().nextInt())), new Date(((System.currentTimeMillis()) - ((((1000L * 60) * 60) * 24) * 30))), new Date(((System.currentTimeMillis()) + ((((1000L * 60) * 60) * 24) * 99))), new X500Name(("CN=" + subjectCommonName)), CertificateManagerTest.subjectKeyPair.getPublic());
        final DERSequence otherName = new DERSequence(new ASN1Encodable[]{ CertificateManagerTest.XMPP_ADDR_OID, new DERUTF8String(subjectAltNameXmppAddr) });
        final GeneralNames subjectAltNames = new GeneralNames(new GeneralName(GeneralName.otherName, otherName));
        builder.addExtension(subjectAlternativeName, true, subjectAltNames);
        final X509CertificateHolder certificateHolder = builder.build(CertificateManagerTest.contentSigner);
        final X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certificateHolder);
        // Execute system under test
        final List<String> serverIdentities = CertificateManager.getServerIdentities(cert);
        // Verify result
        Assert.assertEquals(1, serverIdentities.size());
        Assert.assertTrue(serverIdentities.contains(subjectAltNameXmppAddr));
        Assert.assertFalse(serverIdentities.contains(subjectCommonName));
    }

    /**
     * {@link CertificateManager#getServerIdentities(X509Certificate)} should return:
     * <ul>
     *     <li>the 'DNS SRV' subjectAltName value</li>
     *     <li>explicitly not the Common Name</li>
     * </ul>
     *
     * when a certificate contains:
     * <ul>
     *     <li>a subjectAltName entry of type otherName with an ASN.1 Object Identifier of "id-on-dnsSRV"</li>
     * </ul>
     */
    @Test
    public void testServerIdentitiesDnsSrv() throws Exception {
        // Setup fixture.
        final String subjectCommonName = "MySubjectCommonName";
        final String subjectAltNameDnsSrv = "MySubjectAltNameXmppAddr";
        final X509v3CertificateBuilder builder = // Issuer
        // Random serial number
        // Not before 30 days ago
        // Not after 99 days from now
        // Subject
        new org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder(new X500Name("CN=MyIssuer"), BigInteger.valueOf(Math.abs(new SecureRandom().nextInt())), new Date(((System.currentTimeMillis()) - ((((1000L * 60) * 60) * 24) * 30))), new Date(((System.currentTimeMillis()) + ((((1000L * 60) * 60) * 24) * 99))), new X500Name(("CN=" + subjectCommonName)), CertificateManagerTest.subjectKeyPair.getPublic());
        final DERSequence otherName = new DERSequence(new ASN1Encodable[]{ CertificateManagerTest.DNS_SRV_OID, new DERUTF8String(("_xmpp-server." + subjectAltNameDnsSrv)) });
        final GeneralNames subjectAltNames = new GeneralNames(new GeneralName(GeneralName.otherName, otherName));
        builder.addExtension(subjectAlternativeName, true, subjectAltNames);
        final X509CertificateHolder certificateHolder = builder.build(CertificateManagerTest.contentSigner);
        final X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certificateHolder);
        // Execute system under test
        final List<String> serverIdentities = CertificateManager.getServerIdentities(cert);
        // Verify result
        Assert.assertEquals(1, serverIdentities.size());
        Assert.assertTrue(serverIdentities.contains(subjectAltNameDnsSrv));
        Assert.assertFalse(serverIdentities.contains(subjectCommonName));
    }

    /**
     * {@link CertificateManager#getServerIdentities(X509Certificate)} should return:
     * <ul>
     *     <li>the DNS subjectAltName value</li>
     *     <li>explicitly not the Common Name</li>
     * </ul>
     *
     * when a certificate contains:
     * <ul>
     *     <li>a subjectAltName entry of type DNS </li>
     * </ul>
     */
    @Test
    public void testServerIdentitiesDNS() throws Exception {
        // Setup fixture.
        final String subjectCommonName = "MySubjectCommonName";
        final String subjectAltNameDNS = "MySubjectAltNameDNS";
        final X509v3CertificateBuilder builder = // Issuer
        // Random serial number
        // Not before 30 days ago
        // Not after 99 days from now
        // Subject
        new org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder(new X500Name("CN=MyIssuer"), BigInteger.valueOf(Math.abs(new SecureRandom().nextInt())), new Date(((System.currentTimeMillis()) - ((((1000L * 60) * 60) * 24) * 30))), new Date(((System.currentTimeMillis()) + ((((1000L * 60) * 60) * 24) * 99))), new X500Name(("CN=" + subjectCommonName)), CertificateManagerTest.subjectKeyPair.getPublic());
        final GeneralNames generalNames = new GeneralNames(new GeneralName(GeneralName.dNSName, subjectAltNameDNS));
        builder.addExtension(subjectAlternativeName, false, generalNames);
        final X509CertificateHolder certificateHolder = builder.build(CertificateManagerTest.contentSigner);
        final X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certificateHolder);
        // Execute system under test
        final List<String> serverIdentities = CertificateManager.getServerIdentities(cert);
        // Verify result
        Assert.assertEquals(1, serverIdentities.size());
        Assert.assertTrue(serverIdentities.contains(subjectAltNameDNS));
        Assert.assertFalse(serverIdentities.contains(subjectCommonName));
    }

    /**
     * {@link CertificateManager#getServerIdentities(X509Certificate)} should return:
     * <ul>
     *     <li>the DNS subjectAltName value</li>
     *     <li>the 'xmppAddr' subjectAltName value</li>
     *     <li>explicitly not the Common Name</li>
     * </ul>
     *
     * when a certificate contains:
     * <ul>
     *     <li>a subjectAltName entry of type DNS </li>
     *     <li>a subjectAltName entry of type otherName with an ASN.1 Object Identifier of "id-on-xmppAddr"</li>
     * </ul>
     */
    @Test
    public void testServerIdentitiesXmppAddrAndDNS() throws Exception {
        // Setup fixture.
        final String subjectCommonName = "MySubjectCommonName";
        final String subjectAltNameXmppAddr = "MySubjectAltNameXmppAddr";
        final String subjectAltNameDNS = "MySubjectAltNameDNS";
        final X509v3CertificateBuilder builder = // Issuer
        // Random serial number
        // Not before 30 days ago
        // Not after 99 days from now
        // Subject
        new org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder(new X500Name("CN=MyIssuer"), BigInteger.valueOf(Math.abs(new SecureRandom().nextInt())), new Date(((System.currentTimeMillis()) - ((((1000L * 60) * 60) * 24) * 30))), new Date(((System.currentTimeMillis()) + ((((1000L * 60) * 60) * 24) * 99))), new X500Name(("CN=" + subjectCommonName)), CertificateManagerTest.subjectKeyPair.getPublic());
        final DERSequence otherName = new DERSequence(new ASN1Encodable[]{ CertificateManagerTest.XMPP_ADDR_OID, new DERUTF8String(subjectAltNameXmppAddr) });
        final GeneralNames subjectAltNames = new GeneralNames(new GeneralName[]{ new GeneralName(GeneralName.otherName, otherName), new GeneralName(GeneralName.dNSName, subjectAltNameDNS) });
        builder.addExtension(subjectAlternativeName, true, subjectAltNames);
        final X509CertificateHolder certificateHolder = builder.build(CertificateManagerTest.contentSigner);
        final X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certificateHolder);
        // Execute system under test
        final List<String> serverIdentities = CertificateManager.getServerIdentities(cert);
        // Verify result
        Assert.assertEquals(2, serverIdentities.size());
        Assert.assertTrue(serverIdentities.contains(subjectAltNameXmppAddr));
        Assert.assertFalse(serverIdentities.contains(subjectCommonName));
    }

    /**
     * Asserts that {@link CertificateManager#parsePrivateKey(InputStream, String)} can parse a password-less private
     * key PEM file.
     */
    @Test
    public void testParsePrivateKey() throws Exception {
        // Setup fixture.
        try (final InputStream stream = getClass().getResourceAsStream("/privatekey.pem")) {
            // Execute system under test.
            final PrivateKey result = CertificateManager.parsePrivateKey(stream, "");
            // Verify result.
            Assert.assertNotNull(result);
        }
    }

    /**
     * Asserts that {@link CertificateManager#parseCertificates(InputStream)} can parse a PEM file that contains a
     * certificate chain
     */
    @Test
    public void testParseFullChain() throws Exception {
        // Setup fixture.
        try (final InputStream stream = getClass().getResourceAsStream("/fullchain.pem")) {
            // Execute system under test.
            final Collection<X509Certificate> result = CertificateManager.parseCertificates(stream);
            // Verify result.
            Assert.assertNotNull(result);
            Assert.assertEquals(2, result.size());
        }
    }

    /**
     * Asserts the date-based validity constraints in a certificate that is generated by {@link org.jivesoftware.util.CertificateManager#createX509V3Certificate(java.security.KeyPair, int, org.bouncycastle.asn1.x500.X500NameBuilder, org.bouncycastle.asn1.x500.X500NameBuilder, java.lang.String, java.lang.String, java.util.Set<java.lang.String>)}
     */
    @Test
    public void testGenerateCertificateDateValidity() throws Exception {
        // Setup fixture.
        final KeyPair keyPair = CertificateManagerTest.subjectKeyPair;
        final int days = 2;
        final String issuerCommonName = "issuer common name";
        final String subjectCommonName = "subject common name";
        final String domain = "domain.example.org";
        final String signAlgoritm = "SHA256WITHRSAENCRYPTION";
        final Set<String> sanDnsNames = Stream.of("alternative-a.example.org", "alternative-b.example.org").collect(Collectors.toSet());
        // Execute system under test.
        final X509Certificate result = CertificateManager.createX509V3Certificate(keyPair, days, issuerCommonName, subjectCommonName, domain, signAlgoritm, sanDnsNames);
        // Verify results.
        Assert.assertNotNull(result);
        CertificateManagerTest.assertCertificateDateValid("The generated certificate is expected to be valid immediately (but is not).", result, new Date());
        CertificateManagerTest.assertCertificateDateValid("The generated certificate is expected to be valid half was during its maximum validity period (but is not).", result, CertificateManagerTest.addDays((days / 2)));
        CertificateManagerTest.assertCertificateDateNotValid("The generated certificate is not expected to be valid on a date before it was created (but is).", result, CertificateManagerTest.addDays((-1)));
        CertificateManagerTest.assertCertificateDateNotValid("The generated certificate is not expected to be valid after its maximum validity period has ended (but is).", result, CertificateManagerTest.addDays((days * 2)));
    }

    /**
     * Asserts that the provided common name of the issuer is returned as part of the issuer distinguished name in a certificate that is generated by {@link org.jivesoftware.util.CertificateManager#createX509V3Certificate(java.security.KeyPair, int, org.bouncycastle.asn1.x500.X500NameBuilder, org.bouncycastle.asn1.x500.X500NameBuilder, java.lang.String, java.lang.String, java.util.Set<java.lang.String>)}
     */
    @Test
    public void testGenerateCertificateIssuer() throws Exception {
        // Setup fixture.
        final KeyPair keyPair = CertificateManagerTest.subjectKeyPair;
        final int days = 2;
        final String issuerCommonName = "issuer common name";
        final String subjectCommonName = "subject common name";
        final String domain = "domain.example.org";
        final String signAlgoritm = "SHA256WITHRSAENCRYPTION";
        final Set<String> sanDnsNames = Stream.of("alternative-a.example.org", "alternative-b.example.org").collect(Collectors.toSet());
        // Execute system under test.
        final X509Certificate result = CertificateManager.createX509V3Certificate(keyPair, days, issuerCommonName, subjectCommonName, domain, signAlgoritm, sanDnsNames);
        // Verify results.
        Assert.assertNotNull(result);
        final Set<String> foundIssuerCNs = CertificateManagerTest.parse(result.getIssuerX500Principal().getName(), "CN");
        Assert.assertEquals(1, foundIssuerCNs.size());
        Assert.assertEquals(issuerCommonName, foundIssuerCNs.iterator().next());
    }

    /**
     * Asserts that the provided common name of the subject is returned as part of the subject distinguished name in a certificate that is generated by {@link org.jivesoftware.util.CertificateManager#createX509V3Certificate(java.security.KeyPair, int, org.bouncycastle.asn1.x500.X500NameBuilder, org.bouncycastle.asn1.x500.X500NameBuilder, java.lang.String, java.lang.String, java.util.Set<java.lang.String>)}
     */
    @Test
    public void testGenerateCertificateSubject() throws Exception {
        // Setup fixture.
        final KeyPair keyPair = CertificateManagerTest.subjectKeyPair;
        final int days = 2;
        final String issuerCommonName = "issuer common name";
        final String subjectCommonName = "subject common name";
        final String domain = "domain.example.org";
        final String signAlgoritm = "SHA256WITHRSAENCRYPTION";
        final Set<String> sanDnsNames = Stream.of("alternative-a.example.org", "alternative-b.example.org").collect(Collectors.toSet());
        // Execute system under test.
        final X509Certificate result = CertificateManager.createX509V3Certificate(keyPair, days, issuerCommonName, subjectCommonName, domain, signAlgoritm, sanDnsNames);
        // Verify results.
        Assert.assertNotNull(result);
        final Set<String> foundSubjectCNs = CertificateManagerTest.parse(result.getSubjectX500Principal().getName(), "CN");
        Assert.assertEquals(1, foundSubjectCNs.size());
        Assert.assertEquals(subjectCommonName, foundSubjectCNs.iterator().next());
    }

    /**
     * Asserts that the provided subject alternative DNS names are returned as part of the subject alternative names in a certificate that is generated by {@link org.jivesoftware.util.CertificateManager#createX509V3Certificate(java.security.KeyPair, int, org.bouncycastle.asn1.x500.X500NameBuilder, org.bouncycastle.asn1.x500.X500NameBuilder, java.lang.String, java.lang.String, java.util.Set<java.lang.String>)}
     */
    @Test
    public void testGenerateCertificateSubjectAlternativeNames() throws Exception {
        // Setup fixture.
        final KeyPair keyPair = CertificateManagerTest.subjectKeyPair;
        final int days = 2;
        final String issuerCommonName = "issuer common name";
        final String subjectCommonName = "subject common name";
        final String domain = "domain.example.org";
        final String signAlgoritm = "SHA256WITHRSAENCRYPTION";
        final Set<String> sanDnsNames = Stream.of("alternative-a.example.org", "alternative-b.example.org").collect(Collectors.toSet());
        // Execute system under test.
        final X509Certificate result = CertificateManager.createX509V3Certificate(keyPair, days, issuerCommonName, subjectCommonName, domain, signAlgoritm, sanDnsNames);
        // Verify results.
        Assert.assertNotNull(result);
        final ArrayList<List<?>> expectedNames = new ArrayList<>();
        for (final String sanDnsName : sanDnsNames) {
            expectedNames.add(Arrays.asList(GeneralNameInterface.NAME_DNS, sanDnsName));
        }
        Assert.assertThat("Expected to find all 'alternative names' as DNS entries in the subject alternative names (but does not).", result.getSubjectAlternativeNames(), both(hasSize(sanDnsNames.size())).and(containsInAnyOrder(expectedNames.toArray())));
    }
}

