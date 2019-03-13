package org.apereo.cas.audit.spi;


import lombok.val;
import org.apereo.cas.audit.AuditableExecutionResult;
import org.apereo.cas.audit.spi.resource.ServiceAccessEnforcementAuditResourceResolver;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * This is {@link ServiceAccessEnforcementAuditResourceResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ServiceAccessEnforcementAuditResourceResolverTests {
    @Test
    public void verifyAction() {
        val r = new ServiceAccessEnforcementAuditResourceResolver();
        val result = AuditableExecutionResult.builder().registeredService(CoreAuthenticationTestUtils.getRegisteredService()).service(CoreAuthenticationTestUtils.getService()).authentication(CoreAuthenticationTestUtils.getAuthentication()).build();
        val outcome = r.resolveFrom(Mockito.mock(JoinPoint.class), result);
        Assertions.assertTrue(((outcome.length) > 0));
    }
}

