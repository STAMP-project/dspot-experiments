package org.apereo.cas.adaptors.jdbc;


import java.util.Collections;
import java.util.HashMap;
import javax.persistence.Column;
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
import org.apereo.cas.authentication.CoreAuthenticationUtils;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;


/**
 * This is tests for {@link QueryDatabaseAuthenticationHandler}.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@SuppressWarnings("JDBCExecuteWithNonConstantString")
@SpringBootTest(classes = { RefreshAutoConfiguration.class, DatabaseAuthenticationTestConfiguration.class })
@DirtiesContext
public class QueryDatabaseAuthenticationHandlerTests {
    private static final String SQL = "SELECT * FROM casusers where username=?";

    private static final String PASSWORD_FIELD = "password";

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Test
    public void verifyAuthenticationFailsToFindUser() {
        val q = new QueryDatabaseAuthenticationHandler("", null, null, null, this.dataSource, QueryDatabaseAuthenticationHandlerTests.SQL, QueryDatabaseAuthenticationHandlerTests.PASSWORD_FIELD, null, null, new HashMap(0));
        Assertions.assertThrows(AccountNotFoundException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("usernotfound", "psw1")));
    }

    @Test
    public void verifyPasswordInvalid() {
        val q = new QueryDatabaseAuthenticationHandler("", null, null, null, this.dataSource, QueryDatabaseAuthenticationHandlerTests.SQL, QueryDatabaseAuthenticationHandlerTests.PASSWORD_FIELD, null, null, new HashMap(0));
        Assertions.assertThrows(FailedLoginException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user1", "psw11")));
    }

    @Test
    public void verifyMultipleRecords() {
        val q = new QueryDatabaseAuthenticationHandler("", null, null, null, this.dataSource, QueryDatabaseAuthenticationHandlerTests.SQL, QueryDatabaseAuthenticationHandlerTests.PASSWORD_FIELD, null, null, new HashMap(0));
        Assertions.assertThrows(FailedLoginException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user0", "psw0")));
    }

    @Test
    public void verifyBadQuery() {
        val q = new QueryDatabaseAuthenticationHandler("", null, null, null, this.dataSource, QueryDatabaseAuthenticationHandlerTests.SQL.replace("*", "error"), QueryDatabaseAuthenticationHandlerTests.PASSWORD_FIELD, null, null, new HashMap(0));
        Assertions.assertThrows(PreventedException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user0", "psw0")));
    }

    @Test
    @SneakyThrows
    public void verifySuccess() {
        val map = CoreAuthenticationUtils.transformPrincipalAttributesListIntoMultiMap(Collections.singletonList("phone:phoneNumber"));
        val q = new QueryDatabaseAuthenticationHandler("", null, null, null, this.dataSource, QueryDatabaseAuthenticationHandlerTests.SQL, QueryDatabaseAuthenticationHandlerTests.PASSWORD_FIELD, null, null, CollectionUtils.wrap(map));
        val result = q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user3", "psw3"));
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getPrincipal());
        Assertions.assertTrue(result.getPrincipal().getAttributes().containsKey("phoneNumber"));
    }

    @Test
    public void verifyFindUserAndExpired() {
        val q = new QueryDatabaseAuthenticationHandler("", null, null, null, this.dataSource, QueryDatabaseAuthenticationHandlerTests.SQL, QueryDatabaseAuthenticationHandlerTests.PASSWORD_FIELD, "expired", null, new HashMap(0));
        Assertions.assertThrows(AccountPasswordMustChangeException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user20", "psw20")));
    }

    @Test
    public void verifyFindUserAndDisabled() {
        val q = new QueryDatabaseAuthenticationHandler("", null, null, null, this.dataSource, QueryDatabaseAuthenticationHandlerTests.SQL, QueryDatabaseAuthenticationHandlerTests.PASSWORD_FIELD, null, "disabled", new HashMap(0));
        Assertions.assertThrows(AccountDisabledException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user21", "psw21")));
    }

    /**
     * This test proves that in case BCRYPT is used authentication using encoded password always fail
     * with FailedLoginException
     */
    @Test
    public void verifyBCryptFail() {
        val encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(8, RandomUtils.getNativeInstance());
        val sql = QueryDatabaseAuthenticationHandlerTests.SQL.replace("*", (('\'' + (encoder.encode("pswbc1"))) + "' password"));
        val q = new QueryDatabaseAuthenticationHandler("", null, null, null, this.dataSource, sql, QueryDatabaseAuthenticationHandlerTests.PASSWORD_FIELD, null, null, new HashMap(0));
        q.setPasswordEncoder(encoder);
        Assertions.assertThrows(FailedLoginException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user0", "pswbc1")));
    }

    /**
     * This test proves that in case BCRYPT and
     * using raw password test can authenticate
     */
    @Test
    @SneakyThrows
    public void verifyBCryptSuccess() {
        val encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(6, RandomUtils.getNativeInstance());
        val sql = QueryDatabaseAuthenticationHandlerTests.SQL.replace("*", (('\'' + (encoder.encode("pswbc2"))) + "' password"));
        val q = new QueryDatabaseAuthenticationHandler("", null, null, null, this.dataSource, sql, QueryDatabaseAuthenticationHandlerTests.PASSWORD_FIELD, null, null, new HashMap(0));
        q.setPasswordEncoder(encoder);
        Assertions.assertNotNull(q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user3", "pswbc2")));
    }

    @SuppressWarnings("unused")
    @Entity(name = "casusers")
    public static class UsersTable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column
        private String username;

        @Column
        private String password;

        @Column
        private String expired;

        @Column
        private String disabled;

        @Column
        private String phone;
    }
}

