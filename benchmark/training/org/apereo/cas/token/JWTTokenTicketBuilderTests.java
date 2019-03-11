package org.apereo.cas.token;


import com.nimbusds.jwt.JWTClaimsSet;
import java.util.Optional;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.token.cipher.RegisteredServiceJWTTicketCipherExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link JWTTokenTicketBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class JWTTokenTicketBuilderTests extends BaseJWTTokenTicketBuilderTests {
    @Test
    public void verifyJwtForServiceTicket() throws Exception {
        val jwt = tokenTicketBuilder.build("ST-123455", CoreAuthenticationTestUtils.getService());
        Assertions.assertNotNull(jwt);
        val result = tokenCipherExecutor.decode(jwt);
        val claims = JWTClaimsSet.parse(result.toString());
        Assertions.assertEquals("casuser", claims.getSubject());
    }

    @Test
    public void verifyJwtForServiceTicketWithOwnKeys() throws Exception {
        val service = CoreAuthenticationTestUtils.getService("https://jwt.example.org/cas");
        val jwt = tokenTicketBuilder.build("ST-123455", service);
        Assertions.assertNotNull(jwt);
        val result = tokenCipherExecutor.decode(jwt);
        Assertions.assertNull(result);
        val registeredService = servicesManager.findServiceBy(service);
        val cipher = new RegisteredServiceJWTTicketCipherExecutor();
        Assertions.assertTrue(cipher.supports(registeredService));
        val decoded = cipher.decode(jwt, Optional.of(registeredService));
        val claims = JWTClaimsSet.parse(decoded);
        Assertions.assertEquals("casuser", claims.getSubject());
    }

    @Test
    public void verifyJwtForTicketGrantingTicket() throws Exception {
        val tgt = new MockTicketGrantingTicket("casuser");
        val jwt = tokenTicketBuilder.build(tgt);
        Assertions.assertNotNull(jwt);
        val result = tokenCipherExecutor.decode(jwt);
        val claims = JWTClaimsSet.parse(result.toString());
        Assertions.assertEquals(claims.getSubject(), tgt.getAuthentication().getPrincipal().getId());
    }
}

