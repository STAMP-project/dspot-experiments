package org.apereo.cas.tokens;


import HttpStatus.CREATED;
import TokenConstants.PARAMETER_NAME_TOKEN;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * This is {@link JWTTicketGrantingTicketResourceEntityResponseFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class JWTTicketGrantingTicketResourceEntityResponseFactoryTests extends BaseTicketResourceEntityResponseFactoryTests {
    @Test
    public void verifyTicketGrantingTicketAsDefault() throws Exception {
        val result = CoreAuthenticationTestUtils.getAuthenticationResult(authenticationSystemSupport);
        val tgt = centralAuthenticationService.createTicketGrantingTicket(result);
        val response = ticketGrantingTicketResourceEntityResponseFactory.build(tgt, new MockHttpServletRequest());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(CREATED, response.getStatusCode());
    }

    @Test
    public void verifyTicketGrantingTicketAsJwt() throws Exception {
        val result = CoreAuthenticationTestUtils.getAuthenticationResult(authenticationSystemSupport, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("casuser"));
        val tgt = centralAuthenticationService.createTicketGrantingTicket(result);
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_NAME_TOKEN, Boolean.TRUE.toString());
        val response = ticketGrantingTicketResourceEntityResponseFactory.build(tgt, request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(CREATED, response.getStatusCode());
        val jwt = this.tokenCipherExecutor.decode(response.getBody());
        val claims = JWTClaimsSet.parse(jwt.toString());
        Assertions.assertEquals(claims.getSubject(), tgt.getAuthentication().getPrincipal().getId());
    }

    @Test
    public void verifyTicketGrantingTicketAsJwtWithHeader() throws Exception {
        val result = CoreAuthenticationTestUtils.getAuthenticationResult(authenticationSystemSupport, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("casuser"));
        val tgt = centralAuthenticationService.createTicketGrantingTicket(result);
        val request = new MockHttpServletRequest();
        request.addHeader(PARAMETER_NAME_TOKEN, Boolean.TRUE.toString());
        val response = ticketGrantingTicketResourceEntityResponseFactory.build(tgt, request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(CREATED, response.getStatusCode());
        val jwt = this.tokenCipherExecutor.decode(response.getBody());
        val claims = JWTClaimsSet.parse(jwt.toString());
        Assertions.assertEquals(claims.getSubject(), tgt.getAuthentication().getPrincipal().getId());
    }
}

