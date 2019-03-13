package org.apereo.cas.authentication.principal;


import java.util.Collections;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Handles tests for {@link DefaultPrincipalFactory}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class DefaultPrincipalFactoryTests {
    private static final String UID = "uid";

    @Test
    public void checkCreatingSimplePrincipal() {
        val f = new DefaultPrincipalFactory();
        val p = f.createPrincipal(DefaultPrincipalFactoryTests.UID);
        Assertions.assertEquals(DefaultPrincipalFactoryTests.UID, p.getId());
        Assertions.assertTrue(p.getAttributes().isEmpty());
    }

    @Test
    public void checkCreatingSimplePrincipalWithAttributes() {
        val f = new DefaultPrincipalFactory();
        val p = f.createPrincipal(DefaultPrincipalFactoryTests.UID, Collections.singletonMap("mail", "final@example.com"));
        Assertions.assertEquals(DefaultPrincipalFactoryTests.UID, p.getId());
        Assertions.assertEquals(1, p.getAttributes().size());
        Assertions.assertTrue(p.getAttributes().containsKey("mail"));
    }

    @Test
    public void checkCreatingSimplePrincipalWithDefaultRepository() {
        val f = new DefaultPrincipalFactory();
        val p = f.createPrincipal(DefaultPrincipalFactoryTests.UID);
        Assertions.assertEquals(DefaultPrincipalFactoryTests.UID, p.getId());
        Assertions.assertTrue(p.getAttributes().isEmpty());
    }
}

