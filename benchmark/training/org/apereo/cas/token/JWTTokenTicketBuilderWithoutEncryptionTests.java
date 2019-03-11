package org.apereo.cas.token;


import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.util.EncodingUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JWTTokenTicketBuilderWithoutEncryptionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@TestPropertySource(properties = "cas.authn.token.crypto.encryptionEnabled=false")
public class JWTTokenTicketBuilderWithoutEncryptionTests extends BaseJWTTokenTicketBuilderTests {
    @Test
    public void verifyJwtForServiceTicket() throws ParseException {
        val jwt = tokenTicketBuilder.build("ST-123456", CoreAuthenticationTestUtils.getService());
        Assertions.assertNotNull(jwt);
        val result = tokenCipherExecutor.decode(jwt);
        val claims = JWTClaimsSet.parse(result.toString());
        Assertions.assertEquals("casuser", claims.getSubject());
    }

    @Test
    public void verifyJwtForServiceTicketEncoding() {
        val jwt = tokenTicketBuilder.build("ST-123456", CoreAuthenticationTestUtils.getService());
        Assertions.assertNotNull(jwt);
        val jwtDec = EncodingUtils.decodeBase64ToString(jwt);
        Assertions.assertNotNull(jwtDec);
    }
}

