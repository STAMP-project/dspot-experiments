package org.apereo.cas.audit.spi.resource;


import lombok.val;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * This is {@link ServiceResourceResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class ServiceResourceResolverTests {
    @Test
    public void verifyOperation() {
        val jp = Mockito.mock(JoinPoint.class);
        Mockito.when(jp.getArgs()).thenReturn(new Object[]{ "something", RegisteredServiceTestUtils.getService() });
        val resolver = new ServiceResourceResolver();
        var input = resolver.resolveFrom(jp, null);
        Assertions.assertTrue(((input.length) > 0));
        input = resolver.resolveFrom(jp, new RuntimeException());
        Assertions.assertTrue(((input.length) > 0));
    }
}

