package org.apereo.cas;


import org.apereo.cas.config.AmazonS3BucketsCloudConfigBootstrapConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link AmazonS3BucketsCloudConfigBootstrapConfigurationTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, AmazonS3BucketsCloudConfigBootstrapConfiguration.class })
@EnabledIfPortOpen(port = 4572)
@EnabledIfContinuousIntegration
@Tag("AmazonWebServicesS3")
@TestPropertySource(properties = { "cas.spring.cloud.aws.s3.bucketName=" + (AmazonS3BucketsCloudConfigBootstrapConfigurationTests.BUCKET_NAME), "cas.spring.cloud.aws.s3.endpoint=" + (AmazonS3BucketsCloudConfigBootstrapConfigurationTests.ENDPOINT), "cas.spring.cloud.aws.s3.credentialAccessKey=" + (AmazonS3BucketsCloudConfigBootstrapConfigurationTests.CREDENTIAL_ACCESS_KEY), "cas.spring.cloud.aws.s3.credentialSecretKey=" + (AmazonS3BucketsCloudConfigBootstrapConfigurationTests.CREDENTIAL_SECRET_KEY) })
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class AmazonS3BucketsCloudConfigBootstrapConfigurationTests {
    static final String BUCKET_NAME = "config-bucket";

    static final String ENDPOINT = "http://127.0.0.1:4572";

    static final String CREDENTIAL_SECRET_KEY = "test";

    static final String CREDENTIAL_ACCESS_KEY = "test";

    private static final String STATIC_AUTHN_USERS = "casuser::WHATEVER";

    @Autowired
    private CasConfigurationProperties casProperties;

    @Test
    public void verifyOperation() {
        Assertions.assertEquals(AmazonS3BucketsCloudConfigBootstrapConfigurationTests.STATIC_AUTHN_USERS, casProperties.getAuthn().getAccept().getUsers());
    }
}

