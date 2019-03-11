package org.apereo.cas.support.saml.web.idp.audit;


import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link SamlRequestAuditResourceResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = RefreshAutoConfiguration.class)
public class SamlRequestAuditResourceResolverTests {
    @Test
    public void verifyAction() {
        val r = new SamlRequestAuditResourceResolver();
        val authnRequest = Mockito.mock(AuthnRequest.class);
        val issuer = Mockito.mock(Issuer.class);
        Mockito.when(issuer.getValue()).thenReturn("https://idp.example.org");
        Mockito.when(authnRequest.getIssuer()).thenReturn(issuer);
        Mockito.when(authnRequest.getProtocolBinding()).thenReturn("ProtocolBinding");
        var pair = Pair.of(authnRequest, null);
        var result = r.resolveFrom(Mockito.mock(JoinPoint.class), pair);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(((result.length) > 0));
        val logoutRequest = Mockito.mock(LogoutRequest.class);
        Mockito.when(logoutRequest.getIssuer()).thenReturn(issuer);
        pair = Pair.of(authnRequest, null);
        result = r.resolveFrom(Mockito.mock(JoinPoint.class), pair);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(((result.length) > 0));
    }
}

