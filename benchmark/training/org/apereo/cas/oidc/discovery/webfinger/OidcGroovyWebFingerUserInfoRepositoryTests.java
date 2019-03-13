package org.apereo.cas.oidc.discovery.webfinger;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;


/**
 * This is {@link OidcGroovyWebFingerUserInfoRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Tag("Groovy")
public class OidcGroovyWebFingerUserInfoRepositoryTests {
    @Test
    public void verifyFindByEmail() {
        val repo = new org.apereo.cas.oidc.discovery.webfinger.userinfo.OidcGroovyWebFingerUserInfoRepository(new ClassPathResource("webfinger.groovy"));
        val results = repo.findByEmailAddress("cas@example.org");
        Assertions.assertNotNull(results);
        Assertions.assertTrue(results.containsKey("email"));
        Assertions.assertEquals("cas@example.org", results.get("email"));
    }

    @Test
    public void verifyFindByUsername() {
        val repo = new org.apereo.cas.oidc.discovery.webfinger.userinfo.OidcGroovyWebFingerUserInfoRepository(new ClassPathResource("webfinger.groovy"));
        val results = repo.findByUsername("cas");
        Assertions.assertNotNull(results);
        Assertions.assertTrue(results.containsKey("username"));
        Assertions.assertEquals("cas", results.get("username"));
    }
}

