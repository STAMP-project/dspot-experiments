/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.toolkit.tls.service.server;


import Response.SC_BAD_REQUEST;
import Response.SC_FORBIDDEN;
import Response.SC_OK;
import TlsCertificateAuthorityServiceHandler.CSR_FIELD_MUST_BE_SET;
import TlsCertificateAuthorityServiceHandler.FORBIDDEN;
import TlsCertificateAuthorityServiceHandler.HMAC_FIELD_MUST_BE_SET;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.X509Certificate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.nifi.toolkit.tls.service.dto.TlsCertificateAuthorityRequest;
import org.apache.nifi.toolkit.tls.util.TlsHelper;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.eclipse.jetty.server.Request;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TlsCertificateAuthorityServiceHandlerTest {
    X509Certificate caCert;

    @Mock
    Request baseRequest;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpServletResponse httpServletResponse;

    JcaPKCS10CertificationRequest jcaPKCS10CertificationRequest;

    KeyPair keyPair;

    String testToken;

    String testPemEncodedCsr;

    String testPemEncodedSignedCertificate;

    ObjectMapper objectMapper;

    TlsCertificateAuthorityServiceHandler tlsCertificateAuthorityServiceHandler;

    TlsCertificateAuthorityRequest tlsCertificateAuthorityRequest;

    int statusCode;

    StringWriter response;

    private byte[] testCaHmac;

    private byte[] testHmac;

    private String requestedDn;

    private KeyPair certificateKeyPair;

    @Test
    public void testSuccess() throws IOException, GeneralSecurityException, ServletException, CRMFException {
        tlsCertificateAuthorityRequest = new TlsCertificateAuthorityRequest(testHmac, testPemEncodedCsr);
        tlsCertificateAuthorityServiceHandler.handle(null, baseRequest, httpServletRequest, httpServletResponse);
        Assert.assertEquals(SC_OK, statusCode);
        Assert.assertArrayEquals(testCaHmac, getResponse().getHmac());
        X509Certificate certificate = TlsHelper.parseCertificate(new StringReader(getResponse().getPemEncodedCertificate()));
        Assert.assertEquals(certificateKeyPair.getPublic(), certificate.getPublicKey());
        Assert.assertEquals(new X500Name(requestedDn), new X500Name(certificate.getSubjectDN().toString()));
        certificate.verify(caCert.getPublicKey());
    }

    @Test
    public void testNoCsr() throws IOException, ServletException {
        tlsCertificateAuthorityRequest = new TlsCertificateAuthorityRequest(testHmac, null);
        tlsCertificateAuthorityServiceHandler.handle(null, baseRequest, httpServletRequest, httpServletResponse);
        Assert.assertEquals(SC_BAD_REQUEST, statusCode);
        Assert.assertEquals(CSR_FIELD_MUST_BE_SET, getResponse().getError());
    }

    @Test
    public void testNoHmac() throws IOException, ServletException {
        tlsCertificateAuthorityRequest = new TlsCertificateAuthorityRequest(null, testPemEncodedCsr);
        tlsCertificateAuthorityServiceHandler.handle(null, baseRequest, httpServletRequest, httpServletResponse);
        Assert.assertEquals(SC_BAD_REQUEST, statusCode);
        Assert.assertEquals(HMAC_FIELD_MUST_BE_SET, getResponse().getError());
    }

    @Test
    public void testForbidden() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, ServletException, CRMFException {
        tlsCertificateAuthorityRequest = new TlsCertificateAuthorityRequest("badHmac".getBytes(StandardCharsets.UTF_8), testPemEncodedCsr);
        tlsCertificateAuthorityServiceHandler.handle(null, baseRequest, httpServletRequest, httpServletResponse);
        Assert.assertEquals(SC_FORBIDDEN, statusCode);
        Assert.assertEquals(FORBIDDEN, getResponse().getError());
    }

    @Test(expected = ServletException.class)
    public void testServletException() throws IOException, ServletException {
        tlsCertificateAuthorityServiceHandler.handle(null, baseRequest, httpServletRequest, httpServletResponse);
    }
}

