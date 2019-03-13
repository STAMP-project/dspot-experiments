package org.apereo.cas.gauth.rest;


import GoogleAuthenticatorRestHttpRequestCredentialFactory.PARAMETER_NAME_GAUTH_OTP;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link GoogleAuthenticatorRestHttpRequestCredentialFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class GoogleAuthenticatorRestHttpRequestCredentialFactoryTests {
    @Test
    public void verifyNoCredentials() {
        val f = new GoogleAuthenticatorRestHttpRequestCredentialFactory();
        val body = new org.springframework.util.LinkedMultiValueMap<String, String>();
        val results = f.fromRequest(null, body);
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    public void verifyCredentials() {
        val f = new GoogleAuthenticatorRestHttpRequestCredentialFactory();
        val body = new org.springframework.util.LinkedMultiValueMap<String, String>();
        body.add(PARAMETER_NAME_GAUTH_OTP, "132456");
        val results = f.fromRequest(null, body);
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals("132456", results.get(0).getId());
    }
}

