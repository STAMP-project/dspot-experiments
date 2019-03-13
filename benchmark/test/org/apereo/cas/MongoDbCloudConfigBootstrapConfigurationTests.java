package org.apereo.cas;


import org.apereo.cas.config.MongoDbCloudConfigBootstrapConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link MongoDbCloudConfigBootstrapConfigurationTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@Tag("MongoDb")
@EnabledIfContinuousIntegration
@EnableConfigurationProperties(CasConfigurationProperties.class)
@SpringBootTest(classes = { RefreshAutoConfiguration.class, MongoDbCloudConfigBootstrapConfiguration.class })
@TestPropertySource(properties = "cas.spring.cloud.mongo.uri=" + (MongoDbCloudConfigBootstrapConfigurationTests.MONGODB_URI))
public class MongoDbCloudConfigBootstrapConfigurationTests {
    static final String MONGODB_URI = "mongodb://root:secret@localhost:27017/admin";

    private static final String STATIC_AUTHN_USERS = "casuser::WHATEVER";

    @Autowired
    private CasConfigurationProperties casProperties;

    @Test
    public void verifyOperation() {
        Assertions.assertEquals(MongoDbCloudConfigBootstrapConfigurationTests.STATIC_AUTHN_USERS, casProperties.getAuthn().getAccept().getUsers());
    }
}

