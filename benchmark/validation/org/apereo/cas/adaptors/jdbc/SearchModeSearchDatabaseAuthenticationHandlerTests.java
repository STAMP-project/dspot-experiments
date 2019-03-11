package org.apereo.cas.adaptors.jdbc;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;


/**
 * Tests for {@link SearchModeSearchDatabaseAuthenticationHandler}.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@SuppressWarnings("JDBCExecuteWithNonConstantString")
@SpringBootTest(classes = { RefreshAutoConfiguration.class, DatabaseAuthenticationTestConfiguration.class })
@DirtiesContext
public class SearchModeSearchDatabaseAuthenticationHandlerTests {
    private SearchModeSearchDatabaseAuthenticationHandler handler;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Test
    public void verifyNotFoundUser() {
        val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("hello", "world");
        Assertions.assertThrows(FailedLoginException.class, () -> handler.authenticate(c));
    }

    @Test
    @SneakyThrows
    public void verifyFoundUser() {
        val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user3", "psw3");
        Assertions.assertNotNull(handler.authenticate(c));
    }

    @Test
    @SneakyThrows
    public void verifyMultipleUsersFound() {
        val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user0", "psw0");
        Assertions.assertNotNull(this.handler.authenticate(c));
    }

    @SuppressWarnings("unused")
    @Entity(name = "cassearchusers")
    public static class UsersTable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String username;

        private String password;
    }
}

