package org.whispersystems.textsecuregcm.tests.controllers;


import OptionalAccess.UNIDENTIFIED;
import SenderCertificate.Certificate;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import java.io.IOException;
import java.util.Arrays;
import javax.ws.rs.core.Response;
import junit.framework.TestCase;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.whispersystems.textsecuregcm.auth.CertificateGenerator;
import org.whispersystems.textsecuregcm.crypto.Curve;
import org.whispersystems.textsecuregcm.entities.DeliveryCertificate;
import org.whispersystems.textsecuregcm.entities.MessageProtos.SenderCertificate;
import org.whispersystems.textsecuregcm.entities.MessageProtos.ServerCertificate;
import org.whispersystems.textsecuregcm.tests.util.AuthHelper;
import org.whispersystems.textsecuregcm.util.Base64;
import org.whispersystems.textsecuregcm.util.SystemMapper;


public class CertificateControllerTest {
    private static final String caPublicKey = "BWh+UOhT1hD8bkb+MFRvb6tVqhoG8YYGCzOd7mgjo8cV";

    private static final String caPrivateKey = "EO3Mnf0kfVlVnwSaqPoQnAxhnnGL1JTdXqktCKEe9Eo=";

    private static final String signingCertificate = "CiUIDBIhBbTz4h1My+tt+vw+TVscgUe/DeHS0W02tPWAWbTO2xc3EkD+go4bJnU0AcnFfbOLKoiBfCzouZtDYMOVi69rE7r4U9cXREEqOkUmU2WJBjykAxWPCcSTmVTYHDw7hkSp/puG";

    private static final String signingKey = "ABOxG29xrfq4E7IrW11Eg7+HBbtba9iiS0500YoBjn4=";

    private static CertificateGenerator certificateGenerator;

    static {
        try {
            CertificateControllerTest.certificateGenerator = new CertificateGenerator(Base64.decode(CertificateControllerTest.signingCertificate), Curve.decodePrivatePoint(Base64.decode(CertificateControllerTest.signingKey)), 1);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder().addProvider(AuthHelper.getAuthFilter()).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).setMapper(SystemMapper.getMapper()).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addResource(new org.whispersystems.textsecuregcm.controllers.CertificateController(CertificateControllerTest.certificateGenerator)).build();

    @Test
    public void testValidCertificate() throws Exception {
        DeliveryCertificate certificateObject = CertificateControllerTest.resources.getJerseyTest().target("/v1/certificate/delivery").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get(DeliveryCertificate.class);
        SenderCertificate certificateHolder = SenderCertificate.parseFrom(certificateObject.getCertificate());
        SenderCertificate.Certificate certificate = Certificate.parseFrom(certificateHolder.getCertificate());
        ServerCertificate serverCertificateHolder = certificate.getSigner();
        ServerCertificate.Certificate serverCertificate = ServerCertificate.Certificate.parseFrom(serverCertificateHolder.getCertificate());
        TestCase.assertTrue(Curve.verifySignature(Curve.decodePoint(serverCertificate.getKey().toByteArray(), 0), certificateHolder.getCertificate().toByteArray(), certificateHolder.getSignature().toByteArray()));
        TestCase.assertTrue(Curve.verifySignature(Curve.decodePoint(Base64.decode(CertificateControllerTest.caPublicKey), 0), serverCertificateHolder.getCertificate().toByteArray(), serverCertificateHolder.getSignature().toByteArray()));
        Assert.assertEquals(certificate.getSender(), AuthHelper.VALID_NUMBER);
        Assert.assertEquals(certificate.getSenderDevice(), 1L);
        TestCase.assertTrue(Arrays.equals(certificate.getIdentityKey().toByteArray(), Base64.decode(AuthHelper.VALID_IDENTITY)));
    }

    @Test
    public void testBadAuthentication() throws Exception {
        Response response = CertificateControllerTest.resources.getJerseyTest().target("/v1/certificate/delivery").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.INVALID_PASSWORD)).get();
        Assert.assertEquals(response.getStatus(), 401);
    }

    @Test
    public void testNoAuthentication() throws Exception {
        Response response = CertificateControllerTest.resources.getJerseyTest().target("/v1/certificate/delivery").request().get();
        Assert.assertEquals(response.getStatus(), 401);
    }

    @Test
    public void testUnidentifiedAuthentication() throws Exception {
        Response response = CertificateControllerTest.resources.getJerseyTest().target("/v1/certificate/delivery").request().header(UNIDENTIFIED, AuthHelper.getUnidentifiedAccessHeader("1234".getBytes())).get();
        Assert.assertEquals(response.getStatus(), 401);
    }
}

