package org.apereo.cas.support.oauth.web.audit;


import lombok.val;
import org.apereo.cas.audit.AuditableExecutionResult;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.ticket.OAuthToken;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * This is {@link AccessTokenGrantRequestAuditResourceResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class AccessTokenGrantRequestAuditResourceResolverTests {
    @Test
    public void verifyAction() {
        val r = new AccessTokenGrantRequestAuditResourceResolver();
        val token = Mockito.mock(OAuthToken.class);
        Mockito.when(token.getId()).thenReturn("CODE");
        Mockito.when(token.getService()).thenReturn(RegisteredServiceTestUtils.getService());
        val service = new OAuthRegisteredService();
        service.setClientId("CLIENTID");
        service.setName("OAUTH");
        service.setId(123);
        val holder = org.apereo.cas.support.oauth.web.response.accesstoken.ext.AccessTokenRequestDataHolder.builder().scopes(org.apereo.cas.util.CollectionUtils.wrapSet("email")).service(token.getService()).authentication(token.getAuthentication()).registeredService(service).grantType(OAuth20GrantTypes.AUTHORIZATION_CODE).token(token).ticketGrantingTicket((token != null ? token.getTicketGrantingTicket() : null)).build();
        val result = AuditableExecutionResult.builder().executionResult(holder).build();
        Assertions.assertTrue(((r.resolveFrom(Mockito.mock(JoinPoint.class), result).length) > 0));
    }
}

