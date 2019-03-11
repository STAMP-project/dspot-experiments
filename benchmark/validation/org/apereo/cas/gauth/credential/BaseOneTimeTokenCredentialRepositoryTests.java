package org.apereo.cas.gauth.credential;


import com.warrenstrange.googleauth.IGoogleAuthenticator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.authentication.OneTimeTokenAccount;
import org.apereo.cas.util.SchedulingUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;


/**
 * This is {@link BaseOneTimeTokenCredentialRepositoryTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@Getter
public abstract class BaseOneTimeTokenCredentialRepositoryTests {
    public static final String CASUSER = "casusergauth";

    public static final String PLAIN_SECRET = "plain_secret";

    private IGoogleAuthenticator google;

    @Mock
    private CipherExecutor<String, String> cipherExecutor;

    private final Map<Pair<String, String>, OneTimeTokenAccount> accountHashMap = new LinkedHashMap<>();

    @Test
    public void verifyCreate() {
        val acct = getAccount("verifyCreate", BaseOneTimeTokenCredentialRepositoryTests.CASUSER);
        Assertions.assertNotNull(acct);
    }

    @Test
    public void verifySaveAndUpdate() throws Exception {
        val acct = getAccount("verifySaveAndUpdate", BaseOneTimeTokenCredentialRepositoryTests.CASUSER);
        val repo = getRegistry("verifySaveAndUpdate");
        repo.save(acct.getUsername(), acct.getSecretKey(), acct.getValidationCode(), acct.getScratchCodes());
        var s = repo.get(acct.getUsername());
        Assertions.assertNotNull(s, "Account not found");
        Assertions.assertNotNull(s.getRegistrationDate());
        Assertions.assertEquals(acct.getValidationCode(), s.getValidationCode());
        Assertions.assertEquals(acct.getSecretKey(), s.getSecretKey());
        s.setSecretKey("newSecret");
        s.setValidationCode(999666);
        getRegistry("verifySaveAndUpdate").update(s);
        s = getRegistry("verifySaveAndUpdate").get(BaseOneTimeTokenCredentialRepositoryTests.CASUSER);
        Assertions.assertEquals(999666, s.getValidationCode());
        Assertions.assertEquals("newSecret", s.getSecretKey());
    }

    @Test
    public void verifyGet() throws Exception {
        val repo = getRegistry("verifyGet");
        val acct = repo.get(BaseOneTimeTokenCredentialRepositoryTests.CASUSER);
        Assertions.assertNull(acct);
        val acct2 = getAccount("verifyGet", BaseOneTimeTokenCredentialRepositoryTests.CASUSER);
        repo.save(acct2.getUsername(), acct2.getSecretKey(), acct2.getValidationCode(), acct2.getScratchCodes());
        val acct3 = repo.get(BaseOneTimeTokenCredentialRepositoryTests.CASUSER);
        Assertions.assertNotNull(acct3, "Account not found");
        Assertions.assertEquals(acct2.getUsername(), acct3.getUsername());
        Assertions.assertEquals(acct2.getValidationCode(), acct3.getValidationCode());
        Assertions.assertEquals(acct2.getSecretKey(), acct3.getSecretKey());
        Assertions.assertEquals(acct2.getScratchCodes(), acct3.getScratchCodes());
    }

    @Test
    public void verifyGetWithDecodedSecret() throws Exception {
        // given
        Mockito.when(cipherExecutor.encode(BaseOneTimeTokenCredentialRepositoryTests.PLAIN_SECRET)).thenReturn("abc321");
        Mockito.when(cipherExecutor.decode("abc321")).thenReturn(BaseOneTimeTokenCredentialRepositoryTests.PLAIN_SECRET);
        val repo = getRegistry("verifyGetWithDecodedSecret");
        var acct = getAccount("verifyGetWithDecodedSecret", BaseOneTimeTokenCredentialRepositoryTests.CASUSER);
        acct.setSecretKey(BaseOneTimeTokenCredentialRepositoryTests.PLAIN_SECRET);
        repo.save(acct.getUsername(), acct.getSecretKey(), acct.getValidationCode(), acct.getScratchCodes());
        // when
        acct = repo.get(BaseOneTimeTokenCredentialRepositoryTests.CASUSER);
        // then
        Assertions.assertEquals(BaseOneTimeTokenCredentialRepositoryTests.PLAIN_SECRET, acct.getSecretKey());
    }

    @TestConfiguration
    public static class BaseTestConfiguration {
        @Autowired
        protected ApplicationContext applicationContext;

        @PostConstruct
        public void init() {
            SchedulingUtils.prepScheduledAnnotationBeanPostProcessor(applicationContext);
        }
    }
}

