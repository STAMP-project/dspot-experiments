package org.apereo.cas.support.saml.web.idp.audit;


import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link SamlResponseAuditPrincipalIdProviderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = RefreshAutoConfiguration.class)
public class SamlResponseAuditPrincipalIdProviderTests {
    @Test
    public void verifyAction() {
        val r = new SamlResponseAuditPrincipalIdProvider();
        val response = Mockito.mock(Response.class);
        val issuer = Mockito.mock(Issuer.class);
        Mockito.when(issuer.getValue()).thenReturn("https://idp.example.org");
        Mockito.when(response.getIssuer()).thenReturn(issuer);
        Mockito.when(response.getDestination()).thenReturn("https://sp.example.org");
        val assertion = Mockito.mock(Assertion.class);
        val subject = Mockito.mock(Subject.class);
        val nameId = Mockito.mock(NameID.class);
        Mockito.when(nameId.getValue()).thenReturn("casuser");
        Mockito.when(subject.getNameID()).thenReturn(nameId);
        Mockito.when(assertion.getSubject()).thenReturn(subject);
        Mockito.when(response.getAssertions()).thenReturn(CollectionUtils.wrapList(assertion));
        val result = r.getPrincipalIdFrom(CoreAuthenticationTestUtils.getAuthentication(), response, null);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("casuser", result);
        Assertions.assertTrue(r.supports(CoreAuthenticationTestUtils.getAuthentication(), response, null));
    }
}

