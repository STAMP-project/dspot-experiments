package org.apereo.cas.services;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class UnauthorizedServiceExceptionTests {
    private static final String MESSAGE = "GG";

    @Test
    public void verifyCodeConstructor() {
        val e = new UnauthorizedServiceException(UnauthorizedServiceExceptionTests.MESSAGE);
        Assertions.assertEquals(UnauthorizedServiceExceptionTests.MESSAGE, e.getMessage());
    }

    @Test
    public void verifyThrowableConstructorWithCode() {
        val r = new RuntimeException();
        val e = new UnauthorizedServiceException(UnauthorizedServiceExceptionTests.MESSAGE, r);
        Assertions.assertEquals(UnauthorizedServiceExceptionTests.MESSAGE, e.getMessage());
        Assertions.assertEquals(r, e.getCause());
    }
}

