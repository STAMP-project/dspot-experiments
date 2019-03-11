package org.apereo.cas.audit;


import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;


/**
 * This is {@link DelegatedAuthenticationAuditResourceResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class DelegatedAuthenticationAuditResourceResolverTests {
    @Test
    public void verifyAction() {
        val r = new DelegatedAuthenticationAuditResourceResolver();
        val result = AuditableExecutionResult.builder().registeredService(CoreAuthenticationTestUtils.getRegisteredService()).service(CoreAuthenticationTestUtils.getService()).build();
        result.addProperty(CasClient.class.getSimpleName(), new CasClient(new CasConfiguration("http://cas.example.org")));
        val outcome = r.resolveFrom(Mockito.mock(JoinPoint.class), result);
        Assertions.assertTrue(((outcome.length) > 0));
    }
}

