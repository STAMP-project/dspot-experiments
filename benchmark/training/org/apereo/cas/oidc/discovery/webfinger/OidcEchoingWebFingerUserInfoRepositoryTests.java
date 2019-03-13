package org.apereo.cas.oidc.discovery.webfinger;


import lombok.val;
import org.apereo.cas.oidc.discovery.webfinger.userinfo.OidcEchoingWebFingerUserInfoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link OidcEchoingWebFingerUserInfoRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class OidcEchoingWebFingerUserInfoRepositoryTests {
    @Test
    public void verifyFindByEmail() {
        val repo = new OidcEchoingWebFingerUserInfoRepository();
        val results = repo.findByEmailAddress("cas@example.org");
        Assertions.assertNotNull(results);
        Assertions.assertTrue(results.containsKey("email"));
        Assertions.assertEquals("cas@example.org", results.get("email"));
    }

    @Test
    public void verifyFindByUsername() {
        val repo = new OidcEchoingWebFingerUserInfoRepository();
        val results = repo.findByUsername("cas");
        Assertions.assertNotNull(results);
        Assertions.assertTrue(results.containsKey("username"));
        Assertions.assertEquals("cas", results.get("username"));
    }
}

