/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.s3a;


import Invoker.Retried;
import MarshalledCredentials.CredentialTypeRequired.AnyNonEmpty;
import STSClientFactory.STSClient;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.Credentials;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.auth.MarshalledCredentialBinding;
import org.apache.hadoop.fs.s3a.auth.MarshalledCredentials;
import org.apache.hadoop.fs.s3a.auth.RoleTestUtils;
import org.apache.hadoop.fs.s3a.auth.STSClientFactory;
import org.apache.hadoop.fs.s3a.auth.delegation.SessionTokenBinding;
import org.apache.hadoop.fs.s3a.auth.delegation.SessionTokenIdentifier;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.LambdaTestUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Invoker.LOG_EVENT;
import static TemporaryAWSCredentialsProvider.NAME;


/**
 * Tests use of temporary credentials (for example, AWS STS & S3).
 *
 * The property {@link Constants#ASSUMED_ROLE_STS_ENDPOINT} can be set to
 * point this at different STS endpoints.
 * This test will use the AWS credentials (if provided) for
 * S3A tests to request temporary credentials, then attempt to use those
 * credentials instead.
 */
public class ITestS3ATemporaryCredentials extends AbstractS3ATestBase {
    private static final Logger LOG = LoggerFactory.getLogger(ITestS3ATemporaryCredentials.class);

    private static final String TEMPORARY_AWS_CREDENTIALS = NAME;

    private static final long TEST_FILE_SIZE = 1024;

    public static final String STS_LONDON = "sts.eu-west-2.amazonaws.com";

    public static final String EU_IRELAND = "eu-west-1";

    private AWSCredentialProviderList credentials;

    /**
     * Test use of STS for requesting temporary credentials.
     *
     * The property test.sts.endpoint can be set to point this at different
     * STS endpoints. This test will use the AWS credentials (if provided) for
     * S3A tests to request temporary credentials, then attempt to use those
     * credentials instead.
     *
     * @throws IOException
     * 		failure
     */
    @Test
    public void testSTS() throws IOException {
        Configuration conf = getContract().getConf();
        S3AFileSystem testFS = getFileSystem();
        credentials = testFS.shareCredentials("testSTS");
        String bucket = testFS.getBucket();
        AWSSecurityTokenServiceClientBuilder builder = STSClientFactory.builder(conf, bucket, credentials, getStsEndpoint(conf), getStsRegion(conf));
        STSClientFactory.STSClient clientConnection = STSClientFactory.createClientConnection(builder.build(), new Invoker(new S3ARetryPolicy(conf), LOG_EVENT));
        Credentials sessionCreds = clientConnection.requestSessionCredentials(S3ATestConstants.TEST_SESSION_TOKEN_DURATION_SECONDS, TimeUnit.SECONDS);
        // clone configuration so changes here do not affect the base FS.
        Configuration conf2 = new Configuration(conf);
        S3AUtils.clearBucketOption(conf2, bucket, AWS_CREDENTIALS_PROVIDER);
        S3AUtils.clearBucketOption(conf2, bucket, ACCESS_KEY);
        S3AUtils.clearBucketOption(conf2, bucket, SECRET_KEY);
        S3AUtils.clearBucketOption(conf2, bucket, SESSION_TOKEN);
        MarshalledCredentials mc = MarshalledCredentialBinding.fromSTSCredentials(sessionCreds);
        updateConfigWithSessionCreds(conf2, mc);
        conf2.set(AWS_CREDENTIALS_PROVIDER, ITestS3ATemporaryCredentials.TEMPORARY_AWS_CREDENTIALS);
        // with valid credentials, we can set properties.
        try (S3AFileSystem fs = S3ATestUtils.createTestFileSystem(conf2)) {
            createAndVerifyFile(fs, path("testSTS"), ITestS3ATemporaryCredentials.TEST_FILE_SIZE);
        }
        // now create an invalid set of credentials by changing the session
        // token
        conf2.set(SESSION_TOKEN, ("invalid-" + (sessionCreds.getSessionToken())));
        try (S3AFileSystem fs = S3ATestUtils.createTestFileSystem(conf2)) {
            createAndVerifyFile(fs, path("testSTSInvalidToken"), ITestS3ATemporaryCredentials.TEST_FILE_SIZE);
            fail(((("Expected an access exception, but file access to " + (fs.getUri())) + " was allowed: ") + fs));
        } catch (AWSS3IOException | AWSBadRequestException ex) {
            ITestS3ATemporaryCredentials.LOG.info("Expected Exception: {}", ex.toString());
            ITestS3ATemporaryCredentials.LOG.debug("Expected Exception: {}", ex, ex);
        }
    }

    @Test
    public void testTemporaryCredentialValidation() throws Throwable {
        Configuration conf = new Configuration();
        conf.set(ACCESS_KEY, "accesskey");
        conf.set(SECRET_KEY, "secretkey");
        conf.set(SESSION_TOKEN, "");
        LambdaTestUtils.intercept(CredentialInitializationException.class, () -> new TemporaryAWSCredentialsProvider(conf).getCredentials());
    }

    /**
     * Test that session tokens are propagated, with the origin string
     * declaring this.
     */
    @Test
    public void testSessionTokenPropagation() throws Exception {
        Configuration conf = new Configuration(getContract().getConf());
        MarshalledCredentials sc = S3ATestUtils.requestSessionCredentials(conf, getFileSystem().getBucket());
        updateConfigWithSessionCreds(conf, sc);
        conf.set(AWS_CREDENTIALS_PROVIDER, ITestS3ATemporaryCredentials.TEMPORARY_AWS_CREDENTIALS);
        try (S3AFileSystem fs = S3ATestUtils.createTestFileSystem(conf)) {
            createAndVerifyFile(fs, path("testSTS"), ITestS3ATemporaryCredentials.TEST_FILE_SIZE);
            SessionTokenIdentifier identifier = ((SessionTokenIdentifier) (fs.getDelegationToken("").decodeIdentifier()));
            String ids = identifier.toString();
            assertThat(("origin in " + ids), identifier.getOrigin(), Matchers.containsString(SessionTokenBinding.CREDENTIALS_CONVERTED_TO_DELEGATION_TOKEN));
            // and validate the AWS bits to make sure everything has come across.
            RoleTestUtils.assertCredentialsEqual(("Reissued credentials in " + ids), sc, identifier.getMarshalledCredentials());
        }
    }

    /**
     * Examine the returned expiry time and validate it against expectations.
     * Allows for some flexibility in local clock, but not much.
     */
    @Test
    public void testSessionTokenExpiry() throws Exception {
        Configuration conf = new Configuration(getContract().getConf());
        MarshalledCredentials sc = S3ATestUtils.requestSessionCredentials(conf, getFileSystem().getBucket());
        long permittedExpiryOffset = 60;
        OffsetDateTime expirationTimestamp = sc.getExpirationDateTime().get();
        OffsetDateTime localTimestamp = OffsetDateTime.now();
        assertTrue(((("local time of " + localTimestamp) + " is after expiry time of ") + expirationTimestamp), localTimestamp.isBefore(expirationTimestamp));
        // what is the interval
        Duration actualDuration = Duration.between(localTimestamp, expirationTimestamp);
        Duration offset = actualDuration.minus(S3ATestConstants.TEST_SESSION_TOKEN_DURATION);
        assertThat((((("Duration of session " + actualDuration) + " out of expected range of with ") + offset) + " this host's clock may be wrong."), offset.getSeconds(), Matchers.lessThanOrEqualTo(permittedExpiryOffset));
    }

    /**
     * Create an invalid session token and verify that it is rejected.
     */
    @Test
    public void testInvalidSTSBinding() throws Exception {
        Configuration conf = new Configuration(getContract().getConf());
        MarshalledCredentials sc = S3ATestUtils.requestSessionCredentials(conf, getFileSystem().getBucket());
        MarshalledCredentialBinding.toAWSCredentials(sc, AnyNonEmpty, "");
        updateConfigWithSessionCreds(conf, sc);
        conf.set(AWS_CREDENTIALS_PROVIDER, ITestS3ATemporaryCredentials.TEMPORARY_AWS_CREDENTIALS);
        conf.set(SESSION_TOKEN, ("invalid-" + (sc.getSessionToken())));
        S3AFileSystem fs = null;
        try {
            // this may throw an exception, which is an acceptable outcome.
            // it must be in the try/catch clause.
            fs = S3ATestUtils.createTestFileSystem(conf);
            Path path = path("testSTSInvalidToken");
            createAndVerifyFile(fs, path, ITestS3ATemporaryCredentials.TEST_FILE_SIZE);
            // this is a failure path, so fail with a meaningful error
            fail("request to create a file should have failed");
        } catch (AWSBadRequestException expected) {
            // likely at two points in the operation, depending on
            // S3Guard state
        } finally {
            IOUtils.closeStream(fs);
        }
    }

    @Test
    public void testSessionCredentialsBadRegion() throws Throwable {
        describe("Create a session with a bad region and expect failure");
        expectedSessionRequestFailure(IllegalArgumentException.class, DEFAULT_DELEGATION_TOKEN_ENDPOINT, "us-west-12", "");
    }

    @Test
    public void testSessionCredentialsWrongRegion() throws Throwable {
        describe("Create a session with the wrong region and expect failure");
        expectedSessionRequestFailure(AccessDeniedException.class, ITestS3ATemporaryCredentials.STS_LONDON, ITestS3ATemporaryCredentials.EU_IRELAND, "");
    }

    @Test
    public void testSessionCredentialsWrongCentralRegion() throws Throwable {
        describe("Create a session sts.amazonaws.com; region='us-west-1'");
        expectedSessionRequestFailure(IllegalArgumentException.class, "sts.amazonaws.com", "us-west-1", "");
    }

    @Test
    public void testSessionCredentialsRegionNoEndpoint() throws Throwable {
        describe("Create a session with a bad region and expect fast failure");
        expectedSessionRequestFailure(IllegalArgumentException.class, "", ITestS3ATemporaryCredentials.EU_IRELAND, ITestS3ATemporaryCredentials.EU_IRELAND);
    }

    @Test
    public void testSessionCredentialsRegionBadEndpoint() throws Throwable {
        describe("Create a session with a bad region and expect fast failure");
        IllegalArgumentException ex = expectedSessionRequestFailure(IllegalArgumentException.class, " ", ITestS3ATemporaryCredentials.EU_IRELAND, "");
        ITestS3ATemporaryCredentials.LOG.info("Outcome: ", ex);
        if (!((ex.getCause()) instanceof URISyntaxException)) {
            throw ex;
        }
    }

    @Test
    public void testSessionCredentialsEndpointNoRegion() throws Throwable {
        expectedSessionRequestFailure(IllegalArgumentException.class, ITestS3ATemporaryCredentials.STS_LONDON, "", ITestS3ATemporaryCredentials.STS_LONDON);
    }

    /**
     * Log retries at debug.
     */
    public static final Retried LOG_AT_ERROR = ( text, exception, retries, idempotent) -> {
        LOG.error("{}", text, exception);
    };

    @Test
    public void testTemporaryCredentialValidationOnLoad() throws Throwable {
        Configuration conf = new Configuration();
        S3ATestUtils.unsetHadoopCredentialProviders(conf);
        conf.set(ACCESS_KEY, "aaa");
        conf.set(SECRET_KEY, "bbb");
        conf.set(SESSION_TOKEN, "");
        final MarshalledCredentials sc = MarshalledCredentialBinding.fromFileSystem(null, conf);
        intercept(IOException.class, MarshalledCredentials.INVALID_CREDENTIALS, () -> {
            sc.validate("", MarshalledCredentials.CredentialTypeRequired.SessionOnly);
            return sc.toString();
        });
    }

    @Test
    public void testEmptyTemporaryCredentialValidation() throws Throwable {
        Configuration conf = new Configuration();
        S3ATestUtils.unsetHadoopCredentialProviders(conf);
        conf.set(ACCESS_KEY, "");
        conf.set(SECRET_KEY, "");
        conf.set(SESSION_TOKEN, "");
        final MarshalledCredentials sc = MarshalledCredentialBinding.fromFileSystem(null, conf);
        intercept(IOException.class, MarshalledCredentialBinding.NO_AWS_CREDENTIALS, () -> {
            sc.validate("", MarshalledCredentials.CredentialTypeRequired.SessionOnly);
            return sc.toString();
        });
    }

    /**
     * Verify that the request mechanism is translating exceptions.
     *
     * @throws Exception
     * 		on a failure
     */
    @Test
    public void testSessionRequestExceptionTranslation() throws Exception {
        intercept(IOException.class, () -> requestSessionCredentials(getConfiguration(), getFileSystem().getBucket(), 10));
    }
}

