package org.apereo.cas.adaptors.jdbc;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.util.transforms.PrefixSuffixPrincipalNameTransformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@SuppressWarnings("JDBCExecuteWithNonConstantString")
@SpringBootTest(classes = { RefreshAutoConfiguration.class, DatabaseAuthenticationTestConfiguration.class })
@DirtiesContext
public class QueryAndEncodeDatabaseAuthenticationHandlerTests {
    private static final String ALG_NAME = "SHA-512";

    private static final String SQL = "SELECT * FROM users where %s";

    private static final int NUM_ITERATIONS = 5;

    private static final String STATIC_SALT = "STATIC_SALT";

    private static final String PASSWORD_FIELD_NAME = "password";

    private static final String EXPIRED_FIELD_NAME = "expired";

    private static final String DISABLED_FIELD_NAME = "disabled";

    private static final String NUM_ITERATIONS_FIELD_NAME = "numIterations";

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Test
    public void verifyAuthenticationFailsToFindUser() {
        val q = new QueryAndEncodeDatabaseAuthenticationHandler("", null, null, null, dataSource, QueryAndEncodeDatabaseAuthenticationHandlerTests.ALG_NAME, QueryAndEncodeDatabaseAuthenticationHandlerTests.buildSql(), QueryAndEncodeDatabaseAuthenticationHandlerTests.PASSWORD_FIELD_NAME, "salt", null, null, "ops", 0, "");
        Assertions.assertThrows(AccountNotFoundException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    public void verifyAuthenticationInvalidSql() {
        val q = new QueryAndEncodeDatabaseAuthenticationHandler("", null, null, null, dataSource, QueryAndEncodeDatabaseAuthenticationHandlerTests.ALG_NAME, QueryAndEncodeDatabaseAuthenticationHandlerTests.buildSql("makesNoSenseInSql"), QueryAndEncodeDatabaseAuthenticationHandlerTests.PASSWORD_FIELD_NAME, "salt", null, null, "ops", 0, "");
        Assertions.assertThrows(PreventedException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    public void verifyAuthenticationMultipleAccounts() {
        val q = new QueryAndEncodeDatabaseAuthenticationHandler("", null, null, null, dataSource, QueryAndEncodeDatabaseAuthenticationHandlerTests.ALG_NAME, QueryAndEncodeDatabaseAuthenticationHandlerTests.buildSql(), QueryAndEncodeDatabaseAuthenticationHandlerTests.PASSWORD_FIELD_NAME, "salt", null, null, "ops", 0, "");
        Assertions.assertThrows(FailedLoginException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user0", "password0")));
    }

    @Test
    @SneakyThrows
    public void verifyAuthenticationSuccessful() {
        val q = new QueryAndEncodeDatabaseAuthenticationHandler("", null, null, null, dataSource, QueryAndEncodeDatabaseAuthenticationHandlerTests.ALG_NAME, QueryAndEncodeDatabaseAuthenticationHandlerTests.buildSql(), QueryAndEncodeDatabaseAuthenticationHandlerTests.PASSWORD_FIELD_NAME, "salt", null, null, QueryAndEncodeDatabaseAuthenticationHandlerTests.NUM_ITERATIONS_FIELD_NAME, 0, QueryAndEncodeDatabaseAuthenticationHandlerTests.STATIC_SALT);
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("user1");
        val r = q.authenticate(c);
        Assertions.assertNotNull(r);
        Assertions.assertEquals("user1", r.getPrincipal().getId());
    }

    @Test
    public void verifyAuthenticationWithExpiredField() {
        val q = new QueryAndEncodeDatabaseAuthenticationHandler("", null, null, null, dataSource, QueryAndEncodeDatabaseAuthenticationHandlerTests.ALG_NAME, QueryAndEncodeDatabaseAuthenticationHandlerTests.buildSql(), QueryAndEncodeDatabaseAuthenticationHandlerTests.PASSWORD_FIELD_NAME, "salt", QueryAndEncodeDatabaseAuthenticationHandlerTests.EXPIRED_FIELD_NAME, null, QueryAndEncodeDatabaseAuthenticationHandlerTests.NUM_ITERATIONS_FIELD_NAME, 0, QueryAndEncodeDatabaseAuthenticationHandlerTests.STATIC_SALT);
        Assertions.assertThrows(AccountPasswordMustChangeException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("user20")));
    }

    @Test
    public void verifyAuthenticationWithDisabledField() {
        val q = new QueryAndEncodeDatabaseAuthenticationHandler("", null, null, null, dataSource, QueryAndEncodeDatabaseAuthenticationHandlerTests.ALG_NAME, QueryAndEncodeDatabaseAuthenticationHandlerTests.buildSql(), QueryAndEncodeDatabaseAuthenticationHandlerTests.PASSWORD_FIELD_NAME, "salt", null, QueryAndEncodeDatabaseAuthenticationHandlerTests.DISABLED_FIELD_NAME, QueryAndEncodeDatabaseAuthenticationHandlerTests.NUM_ITERATIONS_FIELD_NAME, 0, QueryAndEncodeDatabaseAuthenticationHandlerTests.STATIC_SALT);
        Assertions.assertThrows(AccountDisabledException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("user21")));
    }

    @Test
    @SneakyThrows
    public void verifyAuthenticationSuccessfulWithAPasswordEncoder() {
        val q = new QueryAndEncodeDatabaseAuthenticationHandler("", null, null, null, dataSource, QueryAndEncodeDatabaseAuthenticationHandlerTests.ALG_NAME, QueryAndEncodeDatabaseAuthenticationHandlerTests.buildSql(), QueryAndEncodeDatabaseAuthenticationHandlerTests.PASSWORD_FIELD_NAME, "salt", null, null, QueryAndEncodeDatabaseAuthenticationHandlerTests.NUM_ITERATIONS_FIELD_NAME, 0, QueryAndEncodeDatabaseAuthenticationHandlerTests.STATIC_SALT);
        q.setPasswordEncoder(new PasswordEncoder() {
            @Override
            public String encode(final CharSequence password) {
                return password.toString().concat("1");
            }

            @Override
            public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
                return true;
            }
        });
        q.setPrincipalNameTransformer(new PrefixSuffixPrincipalNameTransformer("user", null));
        val r = q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("1", "user"));
        Assertions.assertNotNull(r);
        Assertions.assertEquals("user1", r.getPrincipal().getId());
    }

    @SuppressWarnings("unused")
    @Entity(name = "users")
    public static class UsersTable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String username;

        private String password;

        private String salt;

        private String expired;

        private String disabled;

        private long numIterations;
    }
}

