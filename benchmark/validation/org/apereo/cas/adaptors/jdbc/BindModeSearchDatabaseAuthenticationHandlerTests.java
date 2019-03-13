package org.apereo.cas.adaptors.jdbc;


import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.services.ServicesManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link BindModeSearchDatabaseAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, DatabaseAuthenticationTestConfiguration.class })
@TestPropertySource(properties = { "database.user=casuser", "database.name:cas-bindmode-authentications", "database.password=Mellon" })
@DirtiesContext
public class BindModeSearchDatabaseAuthenticationHandlerTests {
    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Test
    @SneakyThrows
    public void verifyAction() {
        val h = new BindModeSearchDatabaseAuthenticationHandler(null, Mockito.mock(ServicesManager.class), PrincipalFactoryUtils.newPrincipalFactory(), 0, this.dataSource);
        Assertions.assertNotNull(h.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casuser", "Mellon")));
    }
}

