package org.apereo.cas;


import org.apereo.cas.config.JdbcCloudConfigBootstrapConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jpa.AbstractJpaProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link JdbcCloudConfigBootstrapConfigurationTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@EnableConfigurationProperties(CasConfigurationProperties.class)
@SpringBootTest(classes = { RefreshAutoConfiguration.class, JdbcCloudConfigBootstrapConfiguration.class })
public class JdbcCloudConfigBootstrapConfigurationTests {
    private static final String STATIC_AUTHN_USERS = "casuser::WHATEVER";

    @Autowired
    private CasConfigurationProperties casProperties;

    @Test
    public void verifyOperation() {
        Assertions.assertEquals(JdbcCloudConfigBootstrapConfigurationTests.STATIC_AUTHN_USERS, casProperties.getAuthn().getAccept().getUsers());
    }

    public static class Jpa extends AbstractJpaProperties {
        private static final long serialVersionUID = 1210163210567513705L;
    }
}

