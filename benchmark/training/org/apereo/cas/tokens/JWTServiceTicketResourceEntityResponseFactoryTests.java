package org.apereo.cas.tokens;


import HttpStatus.OK;
import ServiceTicket.PREFIX;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link JWTServiceTicketResourceEntityResponseFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class JWTServiceTicketResourceEntityResponseFactoryTests extends BaseTicketResourceEntityResponseFactoryTests {
    @Test
    public void verifyServiceTicketAsDefault() {
        val result = CoreAuthenticationTestUtils.getAuthenticationResult(authenticationSystemSupport);
        val tgt = centralAuthenticationService.createTicketGrantingTicket(result);
        val service = RegisteredServiceTestUtils.getService("test");
        val response = serviceTicketResourceEntityResponseFactory.build(tgt.getId(), service, result);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void verifyServiceTicketAsJwt() throws Exception {
        val result = CoreAuthenticationTestUtils.getAuthenticationResult(authenticationSystemSupport, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("casuser"));
        val tgt = centralAuthenticationService.createTicketGrantingTicket(result);
        val service = RegisteredServiceTestUtils.getService("jwtservice");
        val response = serviceTicketResourceEntityResponseFactory.build(tgt.getId(), service, result);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(OK, response.getStatusCode());
        Assertions.assertFalse(response.getBody().startsWith(PREFIX));
        val jwt = this.tokenCipherExecutor.decode(response.getBody());
        val claims = JWTClaimsSet.parse(jwt.toString());
        Assertions.assertEquals(claims.getSubject(), tgt.getAuthentication().getPrincipal().getId());
    }
}

