package org.apereo.cas.digest;


import java.util.Collections;
import javax.security.auth.login.AccountNotFoundException;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @author David Rodriguez
 * @since 5.1.0
 */
public class DefaultDigestHashedCredentialRetrieverTests {
    @Test
    @SneakyThrows
    public void verifyCanFindAnExistingUser() {
        val expectedPassword = "password";
        val credentialRetriever = new DefaultDigestHashedCredentialRetriever(Collections.singletonMap("user", expectedPassword));
        val credential = credentialRetriever.findCredential("user", "ignored");
        Assertions.assertEquals(expectedPassword, credential);
    }

    @Test
    public void verifyAnExceptionIsThrownIfUsedDoesNotExist() {
        val username = "user";
        val credentialRetriever = new DefaultDigestHashedCredentialRetriever(Collections.singletonMap("anotherUsername", "password"));
        Assertions.assertThrows(AccountNotFoundException.class, () -> credentialRetriever.findCredential(username, "ignored"));
    }
}

