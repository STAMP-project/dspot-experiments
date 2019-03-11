package org.apereo.cas.adaptors.yubikey;


import com.yubico.client.v2.YubicoClient;
import java.util.HashMap;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.adaptors.yubikey.registry.WhitelistYubiKeyAccountRegistry;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Test cases for {@link YubiKeyAuthenticationHandler}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class YubiKeyAuthenticationHandlerTests {
    private static final Integer CLIENT_ID = 18421;

    private static final String SECRET_KEY = "iBIehjui12aK8x82oe5qzGeb0As=";

    private static final String OTP = "cccccccvlidcnlednilgctgcvcjtivrjidfbdgrefcvi";

    @Test
    public void checkDefaultAccountRegistry() {
        val handler = new YubiKeyAuthenticationHandler(YubicoClient.getClient(YubiKeyAuthenticationHandlerTests.CLIENT_ID, YubiKeyAuthenticationHandlerTests.SECRET_KEY));
        Assertions.assertNotNull(handler.getRegistry());
    }

    @Test
    public void checkReplayedAuthn() {
        val handler = new YubiKeyAuthenticationHandler(YubicoClient.getClient(YubiKeyAuthenticationHandlerTests.CLIENT_ID, YubiKeyAuthenticationHandlerTests.SECRET_KEY));
        Assertions.assertThrows(FailedLoginException.class, () -> handler.authenticate(new YubiKeyCredential(YubiKeyAuthenticationHandlerTests.OTP)));
    }

    @Test
    public void checkBadConfigAuthn() {
        val handler = new YubiKeyAuthenticationHandler(YubicoClient.getClient(123456, "123456"));
        Assertions.assertThrows(AccountNotFoundException.class, () -> handler.authenticate(new YubiKeyCredential("casuser")));
    }

    @Test
    public void checkAccountNotFound() {
        val registry = new WhitelistYubiKeyAccountRegistry(new HashMap(), new DefaultYubiKeyAccountValidator(YubicoClient.getClient(YubiKeyAuthenticationHandlerTests.CLIENT_ID, YubiKeyAuthenticationHandlerTests.SECRET_KEY)));
        registry.setCipherExecutor(CipherExecutor.noOpOfSerializableToString());
        val handler = new YubiKeyAuthenticationHandler(StringUtils.EMPTY, null, new DefaultPrincipalFactory(), YubicoClient.getClient(YubiKeyAuthenticationHandlerTests.CLIENT_ID, YubiKeyAuthenticationHandlerTests.SECRET_KEY), registry, null);
        Assertions.assertThrows(AccountNotFoundException.class, () -> handler.authenticate(new YubiKeyCredential(YubiKeyAuthenticationHandlerTests.OTP)));
    }

    @Test
    public void checkEncryptedAccount() {
        val registry = new WhitelistYubiKeyAccountRegistry(new HashMap(), ( uid, token) -> true);
        registry.setCipherExecutor(new YubikeyAccountCipherExecutor("1PbwSbnHeinpkZOSZjuSJ8yYpUrInm5aaV18J2Ar4rM", "szxK-5_eJjs-aUj-64MpUZ-GPPzGLhYPLGl0wrYjYNVAGva2P0lLe6UGKGM7k8dWxsOVGutZWgvmY3l5oVPO3w", 0, 0));
        Assertions.assertTrue(registry.registerAccountFor("encrypteduser", YubiKeyAuthenticationHandlerTests.OTP));
        Assertions.assertTrue(registry.isYubiKeyRegisteredFor("encrypteduser", registry.getAccountValidator().getTokenPublicId(YubiKeyAuthenticationHandlerTests.OTP)));
    }
}

