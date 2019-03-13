package org.apereo.cas.adaptors.jdbc;


import java.util.Collections;
import java.util.LinkedHashMap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.CoreAuthenticationUtils;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.util.CollectionUtils;
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
@SpringBootTest(classes = { RefreshAutoConfiguration.class, DatabaseAuthenticationTestConfiguration.class })
@DirtiesContext
public class NamedQueryDatabaseAuthenticationHandlerTests {
    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Test
    @SneakyThrows
    public void verifySuccess() {
        val sql = "SELECT * FROM cas_named_users where username=:username";
        val map = CoreAuthenticationUtils.transformPrincipalAttributesListIntoMultiMap(Collections.singletonList("phone:phoneNumber"));
        val q = new QueryDatabaseAuthenticationHandler("namedHandler", null, PrincipalFactoryUtils.newPrincipalFactory(), 0, this.dataSource, sql, "password", null, null, CollectionUtils.wrap(map));
        val result = q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user0", "psw0"));
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getPrincipal());
        Assertions.assertTrue(result.getPrincipal().getAttributes().containsKey("phoneNumber"));
    }

    @Test
    @SneakyThrows
    public void verifySuccessWithCount() {
        val sql = "SELECT count(*) as total FROM cas_named_users where username=:username AND password=:password";
        val map = CoreAuthenticationUtils.transformPrincipalAttributesListIntoMultiMap(Collections.singletonList("phone:phoneNumber"));
        val q = new QueryDatabaseAuthenticationHandler("namedHandler", null, PrincipalFactoryUtils.newPrincipalFactory(), 0, this.dataSource, sql, null, null, null, CollectionUtils.wrap(map));
        val result = q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user0", "psw0"));
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getPrincipal());
        Assertions.assertFalse(result.getPrincipal().getAttributes().containsKey("phoneNumber"));
    }

    @Test
    public void verifyFailsWithMissingTotalField() {
        val sql = "SELECT count(*) FROM cas_named_users where username=:username AND password=:password";
        val q = new QueryDatabaseAuthenticationHandler("namedHandler", null, PrincipalFactoryUtils.newPrincipalFactory(), 0, this.dataSource, sql, null, null, null, new LinkedHashMap());
        Assertions.assertThrows(FailedLoginException.class, () -> q.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("whatever", "psw0")));
    }

    @Entity(name = "cas_named_users")
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

