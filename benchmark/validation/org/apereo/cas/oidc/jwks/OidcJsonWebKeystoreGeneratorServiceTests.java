package org.apereo.cas.oidc.jwks;


import java.io.File;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link OidcJsonWebKeystoreGeneratorServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OidcJsonWebKeystoreGeneratorServiceTests extends AbstractOidcTests {
    @Test
    public void verifyOperation() {
        val file = new File(FileUtils.getTempDirectoryPath(), "something.jwks");
        file.delete();
        oidcJsonWebKeystoreGeneratorService.generate(new org.springframework.core.io.FileSystemResource(file));
        Assertions.assertTrue(file.exists());
    }
}

