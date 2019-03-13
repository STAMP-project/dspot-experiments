package org.apereo.cas.authentication.support.password;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link PasswordExpiringWarningMessageDescriptorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class PasswordExpiringWarningMessageDescriptorTests {
    @Test
    public void verifyOperation() {
        val d = new PasswordExpiringWarningMessageDescriptor("DefaultMessage", 30);
        Assertions.assertEquals(30, d.getDaysToExpiration());
        Assertions.assertEquals("DefaultMessage", d.getDefaultMessage());
    }
}

