package org.apereo.cas.adaptors.yubikey;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;


/**
 * This is {@link AbstractYubiKeyAccountRegistryTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
public abstract class AbstractYubiKeyAccountRegistryTests {
    private static final String OTP = "cccccccvlidcnlednilgctgcvcjtivrjidfbdgrefcvi";

    private static final String BAD_TOKEN = "123456";

    private static final String CASUSER = "casuser";

    @Test
    public void verifyAccountNotRegistered() {
        Assertions.assertFalse(getYubiKeyAccountRegistry().isYubiKeyRegisteredFor("missing-user"));
    }

    @Test
    public void verifyAccountNotRegisteredWithBadToken() {
        Assertions.assertFalse(getYubiKeyAccountRegistry().registerAccountFor(AbstractYubiKeyAccountRegistryTests.CASUSER, AbstractYubiKeyAccountRegistryTests.BAD_TOKEN));
        Assertions.assertFalse(getYubiKeyAccountRegistry().isYubiKeyRegisteredFor(AbstractYubiKeyAccountRegistryTests.CASUSER));
    }

    @Test
    public void verifyAccountRegistered() {
        Assertions.assertTrue(getYubiKeyAccountRegistry().registerAccountFor(AbstractYubiKeyAccountRegistryTests.CASUSER, AbstractYubiKeyAccountRegistryTests.OTP));
        Assertions.assertTrue(getYubiKeyAccountRegistry().isYubiKeyRegisteredFor(AbstractYubiKeyAccountRegistryTests.CASUSER));
        Assertions.assertEquals(1, getYubiKeyAccountRegistry().getAccounts().size());
    }

    @Test
    public void verifyEncryptedAccount() {
        Assertions.assertTrue(getYubiKeyAccountRegistry().registerAccountFor("encrypteduser", AbstractYubiKeyAccountRegistryTests.OTP));
        Assertions.assertTrue(getYubiKeyAccountRegistry().isYubiKeyRegisteredFor("encrypteduser", getYubiKeyAccountRegistry().getAccountValidator().getTokenPublicId(AbstractYubiKeyAccountRegistryTests.OTP)));
    }

    @TestConfiguration("YubiKeyAccountRegistryTestConfiguration")
    public static class YubiKeyAccountRegistryTestConfiguration {
        @Bean
        @RefreshScope
        public YubiKeyAccountValidator yubiKeyAccountValidator() {
            return ( uid, token) -> !(token.equals(org.apereo.cas.adaptors.yubikey.BAD_TOKEN));
        }
    }
}

