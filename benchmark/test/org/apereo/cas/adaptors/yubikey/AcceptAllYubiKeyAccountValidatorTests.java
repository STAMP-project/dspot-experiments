package org.apereo.cas.adaptors.yubikey;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link AcceptAllYubiKeyAccountValidatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class AcceptAllYubiKeyAccountValidatorTests {
    @Test
    public void verifyAction() {
        val v = new AcceptAllYubiKeyAccountValidator();
        Assertions.assertTrue(v.isValid("anything", "anything"));
    }
}

