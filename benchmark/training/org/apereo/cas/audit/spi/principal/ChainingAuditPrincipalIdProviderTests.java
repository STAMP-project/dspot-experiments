package org.apereo.cas.audit.spi.principal;


import java.util.ArrayList;
import lombok.val;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link ChainingAuditPrincipalIdProviderTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class ChainingAuditPrincipalIdProviderTests {
    @Test
    public void verifyOperation() {
        val chain = new ChainingAuditPrincipalIdProvider(new ArrayList());
        chain.addProvider(new DefaultAuditPrincipalIdProvider());
        Assertions.assertTrue(chain.supports(RegisteredServiceTestUtils.getAuthentication(), new Object(), null));
        val principal = chain.getPrincipalIdFrom(RegisteredServiceTestUtils.getAuthentication(), new Object(), null);
        Assertions.assertEquals("test", principal);
    }
}

