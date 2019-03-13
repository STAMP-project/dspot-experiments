package org.apereo.cas.adaptors.yubikey;


import YubiKeyRestHttpRequestCredentialFactory.PARAMETER_NAME_YUBIKEY_OTP;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link YubiKeyRestHttpRequestCredentialFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class YubiKeyRestHttpRequestCredentialFactoryTests {
    @Test
    public void verifyAction() {
        val f = new YubiKeyRestHttpRequestCredentialFactory();
        val body = new org.springframework.util.LinkedMultiValueMap<String, String>();
        body.add(PARAMETER_NAME_YUBIKEY_OTP, "token");
        Assertions.assertFalse(f.fromRequest(null, body).isEmpty());
    }
}

