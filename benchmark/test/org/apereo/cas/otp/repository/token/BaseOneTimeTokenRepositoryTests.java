package org.apereo.cas.otp.repository.token;


import lombok.val;
import org.apereo.cas.authentication.OneTimeToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link BaseOneTimeTokenRepositoryTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
public abstract class BaseOneTimeTokenRepositoryTests {
    public static final String CASUSER = "casuser";

    @Test
    public void verifyTokenSave() {
        val token = new OneTimeToken(1234, BaseOneTimeTokenRepositoryTests.CASUSER);
        val repository = getRepository();
        repository.store(token);
        repository.store(token);
        Assertions.assertEquals(2, repository.count(BaseOneTimeTokenRepositoryTests.CASUSER));
        repository.clean();
        Assertions.assertTrue(repository.exists(BaseOneTimeTokenRepositoryTests.CASUSER, 1234));
        repository.remove(BaseOneTimeTokenRepositoryTests.CASUSER);
        repository.remove(1234);
        repository.remove(BaseOneTimeTokenRepositoryTests.CASUSER, 1234);
        Assertions.assertNull(repository.get(BaseOneTimeTokenRepositoryTests.CASUSER, 1234));
        Assertions.assertEquals(0, repository.count());
    }
}

