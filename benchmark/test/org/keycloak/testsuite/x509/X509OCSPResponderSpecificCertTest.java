/**
 * Copyright 2018 Analytical Graphics, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.x509;


import OAuthClient.AccessTokenResponse;
import Response.Status.UNAUTHORIZED;
import io.undertow.Undertow;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authentication.authenticators.x509.X509AuthenticatorConfigModel;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.OAuthClient;


/**
 * Verifies Certificate revocation using OCSP responder but specifying specific
 * responder cert.
 * The tests rely on an OCSP responder service listening
 * for OCSP requests on http://localhost:8888
 *
 * @author rmartinc
 * @version $Revision: 1 $
 * @since 11/2/2016
 */
public class X509OCSPResponderSpecificCertTest extends AbstractX509AuthenticationTest {
    private static final String OCSP_RESPONDER_HOST = "localhost";

    private static final int OCSP_RESPONDER_PORT = 8888;

    private Undertow ocspResponder;

    @Test
    public void loginFailedInvalidResponderOnOCSPResponderRevocationCheck() throws Exception {
        X509AuthenticatorConfigModel config = new X509AuthenticatorConfigModel().setOCSPEnabled(true).setMappingSourceType(SUBJECTDN_EMAIL).setUserIdentityMapperType(USERNAME_EMAIL);
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-directgrant-config", config.getConfig());
        String cfgId = createConfig(directGrantExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "", "", null);
        Assert.assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatusCode());
        Assert.assertEquals("invalid_request", response.getError());
        Assert.assertThat(response.getErrorDescription(), Matchers.containsString("Responder's certificate is not authorized to sign OCSP responses"));
    }

    @Test
    public void loginFailedOnOCSPResponderRevocationCheck() throws Exception {
        X509AuthenticatorConfigModel config = new X509AuthenticatorConfigModel().setOCSPEnabled(true).setOCSPResponder((((("http://" + (X509OCSPResponderSpecificCertTest.OCSP_RESPONDER_HOST)) + ":") + (X509OCSPResponderSpecificCertTest.OCSP_RESPONDER_PORT)) + "/oscp")).setOCSPResponderCertificate(("MIIDSDCCAjACCQDutBlh01xKxDANBgkqhkiG9w0BAQsFADBmMQswCQYDVQQGEwJV\n" + (((((((((((((((("UzELMAkGA1UECBMCTUExETAPBgNVBAcTCFdlc3R3b3JkMRAwDgYDVQQKEwdSZWQg\n" + "SGF0MREwDwYDVQQLEwhLZXljbG9hazESMBAGA1UEAxMJbG9jYWxob3N0MCAXDTE4\n") + "MTEyOTE1MzYxNFoYDzMwMTgwNDAxMTUzNjE0WjBkMQswCQYDVQQGEwJVUzELMAkG\n") + "A1UECAwCTUExDzANBgNVBAcMBkJvc3RvbjEQMA4GA1UECgwHUmVkIEhhdDERMA8G\n") + "A1UECwwIS2V5Y2xvYWsxEjAQBgNVBAMMCUtleWNsb2FrMjCCASIwDQYJKoZIhvcN\n") + "AQEBBQADggEPADCCAQoCggEBALxnRdlqot+p3fAZ8BPt/ZeytVy2ZFXd7zG8jVCu\n") + "j/u/IqrN9fE7esdaiZYEwMvaTPKG7pAxb5NlRgWsE8UfNNN9a0GCp3wPJsmj4Lfx\n") + "K7LmH9QLtq+K7Ap5UGXXRNU0BZqcDMznMaIz04N4DimX5uGAwFQCy/NM5yUP2iOa\n") + "dPlB6ECpLavHDT09rMSVl5RgLQLx/TeX7pT4IW7kbpdTVI02rzE90O72riK61c6P\n") + "Q9Zb6bGSaNZwfNGIVQ8u6AVimLJx66p3BNP+3kEfg7xvXkw6UcaXM5LlMVcxi7cr\n") + "Se1k2UR95gPwJC1AVVPUPqHVb3Ix/wLl7GGhHcuBLPODF0cCAwEAATANBgkqhkiG\n") + "9w0BAQsFAAOCAQEADX2znEyqJZHGWLazrSHFn9Rn1mREqH+OCRq38ymz3tCNyVhs\n") + "OTSO1t6Fo1PP4RvlxB6gd4BYH7/cSCsO00s/OjPf8ptqz59TQAmCIM0+dwQuyxKO\n") + "gq55nWpy5gbqf/zqQiWsMXW5nkMVEMUvf1qbKx6xYP61B83vZh+t65LZh7meG6S5\n") + "B5qT6nDGKN7C8AHuxHHJjpgvYL8kNb47fASTYdHzW57Yi92NrkmAq4PCEt6FQTkX\n") + "WybC/Il6hS0jPdR2ExNV9ykKJrNGGhiwg3C8sf97/Kf+qQgRK8wQIdT88g81aJaG\n") + "qpwfJXd9AZO7DdDJdZ75lR9N1YSnhSq3Ur6IJg=="))).setMappingSourceType(SUBJECTDN_EMAIL).setUserIdentityMapperType(USERNAME_EMAIL);
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-directgrant-config", config.getConfig());
        String cfgId = createConfig(directGrantExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "", "", null);
        Assert.assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatusCode());
        Assert.assertEquals("invalid_request", response.getError());
        Assert.assertThat(response.getErrorDescription(), Matchers.containsString("Certificate's been revoked."));
    }
}

