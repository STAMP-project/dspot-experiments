package org.apereo.cas.services;


import UnauthorizedProxyingException.CODE;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class UnauthorizedProxyingExceptionTests {
    private static final String MESSAGE = "GG";

    @Test
    public void verifyGetCode() {
        val e = new UnauthorizedProxyingException();
        Assertions.assertEquals(CODE, e.getMessage());
    }

    @Test
    public void verifyCodeConstructor() {
        val e = new UnauthorizedProxyingException(UnauthorizedProxyingExceptionTests.MESSAGE);
        Assertions.assertEquals(UnauthorizedProxyingExceptionTests.MESSAGE, e.getMessage());
    }

    @Test
    public void verifyThrowableConstructorWithCode() {
        val r = new RuntimeException();
        val e = new UnauthorizedProxyingException(UnauthorizedProxyingExceptionTests.MESSAGE, r);
        Assertions.assertEquals(UnauthorizedProxyingExceptionTests.MESSAGE, e.getMessage());
        Assertions.assertEquals(r, e.getCause());
    }
}

