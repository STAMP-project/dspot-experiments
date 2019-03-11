package org.apereo.cas.audit.spi;


import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.apereo.cas.audit.spi.resource.ShortenedReturnValueAsStringResourceResolver;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * This is {@link ShortenedReturnValueAsStringResourceResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ShortenedReturnValueAsStringResourceResolverTests {
    private final ShortenedReturnValueAsStringResourceResolver r = new ShortenedReturnValueAsStringResourceResolver();

    @Test
    public void verifyActionPassed() {
        val jp = Mockito.mock(JoinPoint.class);
        Assertions.assertTrue(((r.resolveFrom(jp, RandomStringUtils.randomAlphabetic(52)).length) > 0));
    }
}

