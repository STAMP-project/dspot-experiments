package org.apereo.cas.authentication.support.password;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link RejectResultCodePasswordPolicyHandlingStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class RejectResultCodePasswordPolicyHandlingStrategyTests {
    @Test
    public void verifyOperation() {
        val s = new RejectResultCodePasswordPolicyHandlingStrategy<Object>();
        Assertions.assertFalse(s.supports(null));
        val response = new Object();
        Assertions.assertFalse(s.supports(response));
    }
}

