package org.apereo.cas.support.saml.web.idp.audit;


import lombok.val;
import org.apereo.cas.util.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultActor;
import org.opensaml.soap.soap11.FaultString;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link SamlResponseAuditResourceResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = RefreshAutoConfiguration.class)
public class SamlResponseAuditResourceResolverTests {
    @Test
    public void verifyAction() {
        val r = new SamlResponseAuditResourceResolver();
        val response = Mockito.mock(Response.class);
        val issuer = Mockito.mock(Issuer.class);
        Mockito.when(issuer.getValue()).thenReturn("https://idp.example.org");
        Mockito.when(response.getIssuer()).thenReturn(issuer);
        Mockito.when(response.getDestination()).thenReturn("https://sp.example.org");
        var result = r.resolveFrom(Mockito.mock(JoinPoint.class), response);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(((result.length) > 0));
        val envelope = Mockito.mock(Envelope.class);
        val body = Mockito.mock(Body.class);
        Mockito.when(body.getUnknownXMLObjects()).thenReturn(CollectionUtils.wrapList(response));
        Mockito.when(envelope.getBody()).thenReturn(body);
        result = r.resolveFrom(Mockito.mock(JoinPoint.class), envelope);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(((result.length) > 0));
        val fault = Mockito.mock(Fault.class);
        val actor = Mockito.mock(FaultActor.class);
        Mockito.when(actor.getValue()).thenReturn("actor");
        val msg = Mockito.mock(FaultString.class);
        Mockito.when(msg.getValue()).thenReturn("message");
        Mockito.when(fault.getMessage()).thenReturn(msg);
        Mockito.when(fault.getActor()).thenReturn(actor);
        Mockito.when(body.getUnknownXMLObjects()).thenReturn(CollectionUtils.wrapList(fault));
        result = r.resolveFrom(Mockito.mock(JoinPoint.class), envelope);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(((result.length) > 0));
    }
}

