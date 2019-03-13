package org.apereo.cas.authentication;


import java.util.Optional;
import lombok.val;
import org.apereo.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * This is {@link SurrogatePrincipalResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class SurrogatePrincipalResolverTests {
    @Test
    public void verifyResolverDefault() {
        val resolver = new SurrogatePrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());
        val credential = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val p = resolver.resolve(credential);
        Assertions.assertNotNull(p);
        Assertions.assertEquals(p.getId(), credential.getId());
    }

    @Test
    public void verifyResolverAttribute() {
        val resolver = new SurrogatePrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository(), "cn");
        val credential = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val p = resolver.resolve(credential);
        Assertions.assertNotNull(p);
        Assertions.assertEquals("TEST", p.getId());
    }

    @Test
    public void verifyResolverSurrogateWithoutPrincipal() {
        val resolver = new SurrogatePrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository(), "cn");
        val credential = new SurrogateUsernamePasswordCredential();
        Assertions.assertThrows(IllegalArgumentException.class, () -> resolver.resolve(credential));
    }

    @Test
    public void verifyResolverSurrogate() {
        val resolver = new SurrogatePrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());
        val credential = new SurrogateUsernamePasswordCredential();
        credential.setSurrogateUsername("surrogate");
        credential.setUsername("username");
        val p = resolver.resolve(credential, Optional.of(CoreAuthenticationTestUtils.getPrincipal("casuser")), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        Assertions.assertNotNull(p);
        Assertions.assertEquals("casuser", p.getId());
    }
}

