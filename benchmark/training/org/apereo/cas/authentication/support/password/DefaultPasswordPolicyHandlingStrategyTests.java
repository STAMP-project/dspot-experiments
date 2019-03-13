package org.apereo.cas.authentication.support.password;


import java.util.Collections;
import lombok.val;
import org.apereo.cas.DefaultMessageDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link DefaultPasswordPolicyHandlingStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class DefaultPasswordPolicyHandlingStrategyTests {
    @Test
    public void verifyOperation() throws Exception {
        val s = new DefaultPasswordPolicyHandlingStrategy<Object>();
        Assertions.assertTrue(s.handle(new Object(), null).isEmpty());
        val cfg = new PasswordPolicyConfiguration(30);
        cfg.setAccountStateHandler(( o, o2) -> Collections.singletonList(new DefaultMessageDescriptor("bad.password")));
        Assertions.assertFalse(s.handle(new Object(), cfg).isEmpty());
    }
}

