package org.apereo.cas.gauth.token;


import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.val;
import org.apereo.cas.authentication.OneTimeToken;
import org.apereo.cas.otp.repository.token.OneTimeTokenRepository;
import org.apereo.cas.util.SchedulingUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;


/**
 * This is {@link BaseOneTimeTokenRepositoryTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@Getter
public abstract class BaseOneTimeTokenRepositoryTests {
    public static final String CASUSER = "casuser";

    @Autowired
    @Qualifier("oneTimeTokenAuthenticatorTokenRepository")
    protected OneTimeTokenRepository oneTimeTokenAuthenticatorTokenRepository;

    @Test
    public void verifyTokenSave() {
        var token = ((OneTimeToken) (new GoogleAuthenticatorToken(1234, BaseOneTimeTokenRepositoryTests.CASUSER)));
        oneTimeTokenAuthenticatorTokenRepository.store(token);
        Assertions.assertTrue(oneTimeTokenAuthenticatorTokenRepository.exists(BaseOneTimeTokenRepositoryTests.CASUSER, 1234));
        token = oneTimeTokenAuthenticatorTokenRepository.get(BaseOneTimeTokenRepositoryTests.CASUSER, 1234);
        Assertions.assertTrue(((token.getId()) > 0));
    }

    @Test
    public void verifyTokensWithUniqueIdsSave() {
        val token = new GoogleAuthenticatorToken(1111, BaseOneTimeTokenRepositoryTests.CASUSER);
        oneTimeTokenAuthenticatorTokenRepository.store(token);
        val token2 = new GoogleAuthenticatorToken(5678, BaseOneTimeTokenRepositoryTests.CASUSER);
        oneTimeTokenAuthenticatorTokenRepository.store(token2);
        val t1 = oneTimeTokenAuthenticatorTokenRepository.get(BaseOneTimeTokenRepositoryTests.CASUSER, 1111);
        val t2 = oneTimeTokenAuthenticatorTokenRepository.get(BaseOneTimeTokenRepositoryTests.CASUSER, 5678);
        Assertions.assertTrue(((t1.getId()) > 0));
        Assertions.assertTrue(((t2.getId()) > 0));
        Assertions.assertNotEquals(token.getId(), token2.getId());
        Assertions.assertEquals(1111, ((int) (t1.getToken())));
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

