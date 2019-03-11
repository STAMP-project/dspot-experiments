package org.apereo.cas.authentication;


import lombok.val;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link DefaultPrincipalFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class DefaultPrincipalFactoryTests {
    @Test
    public void verifyAction() {
        val factory = PrincipalFactoryUtils.newPrincipalFactory();
        val p = factory.createPrincipal("casuser", CollectionUtils.wrap("name", "CAS"));
        Assertions.assertTrue(p.getId().equals("casuser"));
        Assertions.assertEquals(1, p.getAttributes().size());
    }
}

