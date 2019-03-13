package org.apereo.cas.ticket;


import UnrecognizableServiceForServiceTicketValidationException.CODE;
import lombok.val;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test cases for {@link UnrecognizableServiceForServiceTicketValidationException}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class UnrecognizableServiceForServiceTicketValidationExceptionTests {
    private final Service service = RegisteredServiceTestUtils.getService();

    @Test
    public void verifyThrowableConstructor() {
        val t = new UnrecognizableServiceForServiceTicketValidationException(this.service);
        Assertions.assertSame(CODE, t.getCode());
        Assertions.assertEquals(this.service, t.getService());
    }
}

