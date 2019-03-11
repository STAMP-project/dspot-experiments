package org.apereo.cas.adaptors.jdbc;


import java.util.Collections;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.CoreAuthenticationUtils;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.apereo.cas.util.serialization.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;


/**
 * This is postgres tests for {@link QueryDatabaseAuthenticationHandler}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, DatabaseAuthenticationTestConfiguration.class })
@DirtiesContext
@EnabledIfPortOpen(port = 5432)
@EnabledIfContinuousIntegration
@Tag("Postgres")
@TestPropertySource(properties = { "database.user=postgres", "database.password=password", "database.driverClass=org.postgresql.Driver", "database.name=postgres", "database.url=jdbc:postgresql://localhost:5432/", "database.dialect=org.hibernate.dialect.PostgreSQL95Dialect" })
public class QueryDatabaseAuthenticationHandlerPostgresTests {
    private static final String SQL = "SELECT * FROM caspgusers where username=?";

    private static final String PASSWORD_FIELD = "password";

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Test
    @SneakyThrows
    public void verifySuccess() {
        val map = CoreAuthenticationUtils.transformPrincipalAttributesListIntoMultiMap(Collections.singletonList("locations"));
        val q = new QueryDatabaseAuthenticationHandler("DbHandler", null, PrincipalFactoryUtils.newPrincipalFactory(), 0, this.dataSource, QueryDatabaseAuthenticationHandlerPostgresTests.SQL, QueryDatabaseAuthenticationHandlerPostgresTests.PASSWORD_FIELD, null, null, CollectionUtils.wrap(map));
        val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casuser", "Mellon");
        val result = q.authenticate(c);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getPrincipal());
        Assertions.assertTrue(result.getPrincipal().getAttributes().containsKey("locations"));
        Assertions.assertNotNull(SerializationUtils.serialize(result));
    }

    @SuppressWarnings("unused")
    @Entity(name = "caspgusers")
    public static class UsersTable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column
        private String username;

        @Column
        private String password;

        // @Type(type = "string-array")
        @Column(name = "locations", columnDefinition = "text[]")
        private String[] locations;
    }
}

