package org.apereo.cas.authentication;


import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.CassandraAuthenticationConfiguration;
import org.apereo.cas.config.CassandraCoreConfiguration;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link DefaultCassandraRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreServicesConfiguration.class, CasPersonDirectoryTestConfiguration.class, CasCoreUtilConfiguration.class, CasCoreHttpConfiguration.class, CassandraCoreConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CassandraAuthenticationConfiguration.class })
@EnableConfigurationProperties
@TestPropertySource(properties = { "cas.authn.cassandra.tableName=users_table", "cas.authn.cassandra.usernameAttribute=user_attr", "cas.authn.cassandra.passwordAttribute=pwd_attr", "cas.authn.cassandra.keyspace=cas" })
@Tag("Cassandra")
@EnabledIfContinuousIntegration
public class DefaultCassandraRepositoryTests {
    @Autowired
    @Qualifier("cassandraAuthenticationHandler")
    private AuthenticationHandler cassandraAuthenticationHandler;

    @Test
    public void verifyUserNotFound() {
        val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("baduser", "Mellon");
        Assertions.assertThrows(AccountNotFoundException.class, () -> cassandraAuthenticationHandler.authenticate(c));
    }

    @Test
    public void verifyUserBadPassword() {
        val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casuser", "bad");
        Assertions.assertThrows(FailedLoginException.class, () -> cassandraAuthenticationHandler.authenticate(c));
    }

    @Test
    @SneakyThrows
    public void verifyUser() {
        val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casuser", "Mellon");
        val result = cassandraAuthenticationHandler.authenticate(c);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("casuser", result.getPrincipal().getId());
    }
}

