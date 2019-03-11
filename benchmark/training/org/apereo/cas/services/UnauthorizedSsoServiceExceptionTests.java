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
public class UnauthorizedSsoServiceExceptionTests {
    private static final String CODE = "service.not.authorized.sso";

    private static final String MESSAGE = "GG";

    @Test
    public void verifyGetCode() {
        val e = new UnauthorizedSsoServiceException();
        Assertions.assertEquals(UnauthorizedSsoServiceExceptionTests.CODE, e.getMessage());
    }

    @Test
    public void verifyCodeConstructor() {
        val e = new UnauthorizedSsoServiceException(UnauthorizedSsoServiceExceptionTests.MESSAGE);
        Assertions.assertEquals(UnauthorizedSsoServiceExceptionTests.MESSAGE, e.getMessage());
    }

    @Test
    public void verifyThrowableConstructorWithCode() {
        val r = new RuntimeException();
        val e = new UnauthorizedSsoServiceException(UnauthorizedSsoServiceExceptionTests.MESSAGE, r);
        Assertions.assertEquals(UnauthorizedSsoServiceExceptionTests.MESSAGE, e.getMessage());
        Assertions.assertEquals(r, e.getCause());
    }
}

