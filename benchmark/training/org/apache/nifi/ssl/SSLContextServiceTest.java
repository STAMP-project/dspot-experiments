/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.ssl;


import ClientAuth.NONE;
import ClientAuth.REQUIRED;
import ClientAuth.WANT;
import StandardSSLContextService.KEYSTORE;
import StandardSSLContextService.KEYSTORE_PASSWORD;
import StandardSSLContextService.KEYSTORE_TYPE;
import StandardSSLContextService.KEY_PASSWORD;
import StandardSSLContextService.TRUSTSTORE;
import StandardSSLContextService.TRUSTSTORE_PASSWORD;
import StandardSSLContextService.TRUSTSTORE_TYPE;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLContext;
import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SSLContextServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(SSLContextServiceTest.class);

    private final String KEYSTORE_PATH = "src/test/resources/keystore.jks";

    private final String KEYSTORE_AND_TRUSTSTORE_PASSWORD = "passwordpassword";

    private final String JKS_TYPE = "JKS";

    private final String TRUSTSTORE_PATH = "src/test/resources/truststore.jks";

    private final String DIFFERENT_PASS_KEYSTORE_PATH = "src/test/resources/keystore-different-password.jks";

    private final String DIFFERENT_KEYSTORE_PASSWORD = "differentpassword";

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder(new File("src/test/resources"));

    @Test
    public void testBad1() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final SSLContextService service = new StandardSSLContextService();
        final Map<String, String> properties = new HashMap<>();
        runner.addControllerService("test-bad1", service, properties);
        runner.assertNotValid(service);
    }

    @Test
    public void testBad2() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final SSLContextService service = new StandardSSLContextService();
        final Map<String, String> properties = new HashMap<>();
        properties.put(KEYSTORE.getName(), KEYSTORE_PATH);
        properties.put(KEYSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.addControllerService("test-bad2", service, properties);
        runner.assertNotValid(service);
    }

    @Test
    public void testBad3() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final SSLContextService service = new StandardSSLContextService();
        final Map<String, String> properties = new HashMap<>();
        properties.put(KEYSTORE.getName(), KEYSTORE_PATH);
        properties.put(KEYSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        properties.put(KEYSTORE_TYPE.getName(), JKS_TYPE);
        properties.put(TRUSTSTORE.getName(), TRUSTSTORE_PATH);
        runner.addControllerService("test-bad3", service, properties);
        runner.assertNotValid(service);
    }

    @Test
    public void testBad4() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final SSLContextService service = new StandardSSLContextService();
        final Map<String, String> properties = new HashMap<>();
        properties.put(KEYSTORE.getName(), KEYSTORE_PATH);
        properties.put(KEYSTORE_PASSWORD.getName(), "wrongpassword");
        properties.put(KEYSTORE_TYPE.getName(), "PKCS12");
        properties.put(TRUSTSTORE.getName(), TRUSTSTORE_PATH);
        properties.put(TRUSTSTORE_PASSWORD.getName(), "wrongpassword");
        properties.put(TRUSTSTORE_TYPE.getName(), JKS_TYPE);
        runner.addControllerService("test-bad4", service, properties);
        runner.assertNotValid(service);
    }

    @Test
    public void testBad5() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final SSLContextService service = new StandardSSLContextService();
        final Map<String, String> properties = new HashMap<>();
        properties.put(KEYSTORE.getName(), "src/test/resources/DOES-NOT-EXIST.jks");
        properties.put(KEYSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        properties.put(KEYSTORE_TYPE.getName(), "PKCS12");
        properties.put(TRUSTSTORE.getName(), TRUSTSTORE_PATH);
        properties.put(TRUSTSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        properties.put(TRUSTSTORE_TYPE.getName(), JKS_TYPE);
        runner.addControllerService("test-bad5", service, properties);
        runner.assertNotValid(service);
    }

    @Test
    public void testGood() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        SSLContextService service = new StandardSSLContextService();
        runner.addControllerService("test-good1", service);
        runner.setProperty(service, KEYSTORE.getName(), KEYSTORE_PATH);
        runner.setProperty(service, KEYSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.setProperty(service, KEYSTORE_TYPE.getName(), JKS_TYPE);
        runner.setProperty(service, TRUSTSTORE.getName(), TRUSTSTORE_PATH);
        runner.setProperty(service, TRUSTSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.setProperty(service, TRUSTSTORE_TYPE.getName(), JKS_TYPE);
        runner.enableControllerService(service);
        runner.setProperty("SSL Context Svc ID", "test-good1");
        runner.assertValid(service);
        service = ((SSLContextService) (runner.getProcessContext().getControllerServiceLookup().getControllerService("test-good1")));
        Assert.assertNotNull(service);
        SSLContextService sslService = service;
        sslService.createSSLContext(REQUIRED);
        sslService.createSSLContext(WANT);
        sslService.createSSLContext(NONE);
    }

    @Test
    public void testWithChanges() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        SSLContextService service = new StandardSSLContextService();
        runner.addControllerService("test-good1", service);
        runner.setProperty(service, KEYSTORE.getName(), KEYSTORE_PATH);
        runner.setProperty(service, KEYSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.setProperty(service, KEYSTORE_TYPE.getName(), JKS_TYPE);
        runner.setProperty(service, TRUSTSTORE.getName(), TRUSTSTORE_PATH);
        runner.setProperty(service, TRUSTSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.setProperty(service, TRUSTSTORE_TYPE.getName(), JKS_TYPE);
        runner.enableControllerService(service);
        runner.setProperty("SSL Context Svc ID", "test-good1");
        runner.assertValid(service);
        runner.disableControllerService(service);
        runner.setProperty(service, KEYSTORE.getName(), "src/test/resources/DOES-NOT-EXIST.jks");
        runner.assertNotValid(service);
        runner.setProperty(service, KEYSTORE.getName(), KEYSTORE_PATH);
        runner.setProperty(service, TRUSTSTORE_PASSWORD.getName(), "badpassword");
        runner.assertNotValid(service);
        runner.setProperty(service, TRUSTSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.enableControllerService(service);
        runner.assertValid(service);
    }

    @Test
    public void testValidationResultsCacheShouldExpire() throws IOException, InitializationException {
        // Arrange
        // Copy the keystore and truststore to a tmp directory so the originals are not modified
        File originalKeystore = new File(KEYSTORE_PATH);
        File originalTruststore = new File(TRUSTSTORE_PATH);
        File tmpKeystore = tmp.newFile("keystore-tmp.jks");
        File tmpTruststore = tmp.newFile("truststore-tmp.jks");
        Files.copy(originalKeystore.toPath(), tmpKeystore.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(originalTruststore.toPath(), tmpTruststore.toPath(), StandardCopyOption.REPLACE_EXISTING);
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        SSLContextService service = new StandardSSLContextService();
        final String serviceIdentifier = "test-should-expire";
        runner.addControllerService(serviceIdentifier, service);
        runner.setProperty(service, KEYSTORE.getName(), tmpKeystore.getAbsolutePath());
        runner.setProperty(service, KEYSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.setProperty(service, KEYSTORE_TYPE.getName(), JKS_TYPE);
        runner.setProperty(service, TRUSTSTORE.getName(), tmpTruststore.getAbsolutePath());
        runner.setProperty(service, TRUSTSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.setProperty(service, TRUSTSTORE_TYPE.getName(), JKS_TYPE);
        runner.enableControllerService(service);
        runner.setProperty("SSL Context Svc ID", serviceIdentifier);
        runner.assertValid(service);
        final StandardSSLContextService sslContextService = ((StandardSSLContextService) (service));
        // Act
        boolean isDeleted = tmpKeystore.delete();
        assert isDeleted;
        assert !(tmpKeystore.exists());
        SSLContextServiceTest.logger.info("Deleted keystore file");
        // Manually validate the service (expecting cached result to be returned)
        final MockProcessContext processContext = ((MockProcessContext) (runner.getProcessContext()));
        // This service does not use the state manager or variable registry
        final ValidationContext validationContext = new org.apache.nifi.util.MockValidationContext(processContext, null, null);
        // Even though the keystore file is no longer present, because no property changed, the cached result is still valid
        Collection<ValidationResult> validationResults = sslContextService.customValidate(validationContext);
        Assert.assertTrue("validation results is not empty", validationResults.isEmpty());
        SSLContextServiceTest.logger.info("(1) StandardSSLContextService#customValidate() returned true even though the keystore file is no longer available");
        // Assert
        // Have to exhaust the cached result by checking n-1 more times
        for (int i = 2; i < (sslContextService.getValidationCacheExpiration()); i++) {
            validationResults = sslContextService.customValidate(validationContext);
            Assert.assertTrue("validation results is not empty", validationResults.isEmpty());
            SSLContextServiceTest.logger.info((("(" + i) + ") StandardSSLContextService#customValidate() returned true even though the keystore file is no longer available"));
        }
        validationResults = sslContextService.customValidate(validationContext);
        Assert.assertFalse("validation results is empty", validationResults.isEmpty());
        SSLContextServiceTest.logger.info((("(" + (sslContextService.getValidationCacheExpiration())) + ") StandardSSLContextService#customValidate() returned false because the cache expired"));
    }

    @Test
    public void testGoodTrustOnly() {
        try {
            TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
            SSLContextService service = new StandardSSLContextService();
            HashMap<String, String> properties = new HashMap<>();
            properties.put(TRUSTSTORE.getName(), TRUSTSTORE_PATH);
            properties.put(TRUSTSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
            properties.put(TRUSTSTORE_TYPE.getName(), JKS_TYPE);
            runner.addControllerService("test-good2", service, properties);
            runner.enableControllerService(service);
            runner.setProperty("SSL Context Svc ID", "test-good2");
            runner.assertValid();
            Assert.assertNotNull(service);
            Assert.assertTrue((service instanceof StandardSSLContextService));
            service.createSSLContext(NONE);
        } catch (InitializationException e) {
        }
    }

    @Test
    public void testGoodKeyOnly() {
        try {
            TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
            SSLContextService service = new StandardSSLContextService();
            HashMap<String, String> properties = new HashMap<>();
            properties.put(KEYSTORE.getName(), KEYSTORE_PATH);
            properties.put(KEYSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
            properties.put(KEYSTORE_TYPE.getName(), JKS_TYPE);
            runner.addControllerService("test-good3", service, properties);
            runner.enableControllerService(service);
            runner.setProperty("SSL Context Svc ID", "test-good3");
            runner.assertValid();
            Assert.assertNotNull(service);
            Assert.assertTrue((service instanceof StandardSSLContextService));
            SSLContextService sslService = service;
            sslService.createSSLContext(NONE);
        } catch (Exception e) {
            System.out.println(e);
            Assert.fail(("Should not have thrown a exception " + (e.getMessage())));
        }
    }

    /**
     * This test asserts that the keystore password and key password are different. This is only
     * true because they were explicitly set that way. Normal keystores that do not have passwords
     * set on individual keys will fail this test.
     */
    @Test
    public void testDifferentKeyPassword() {
        try {
            final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
            final SSLContextService service = new StandardSSLContextService();
            final Map<String, String> properties = new HashMap<>();
            properties.put(KEYSTORE.getName(), DIFFERENT_PASS_KEYSTORE_PATH);
            properties.put(KEYSTORE_PASSWORD.getName(), DIFFERENT_KEYSTORE_PASSWORD);
            properties.put(KEY_PASSWORD.getName(), "keypassword");
            properties.put(KEYSTORE_TYPE.getName(), JKS_TYPE);
            runner.addControllerService("test-diff-keys", service, properties);
            runner.enableControllerService(service);
            runner.setProperty("SSL Context Svc ID", "test-diff-keys");
            runner.assertValid();
            Assert.assertNotNull(service);
            Assert.assertTrue((service instanceof StandardSSLContextService));
            SSLContextService sslService = service;
            sslService.createSSLContext(NONE);
        } catch (Exception e) {
            System.out.println(e);
            Assert.fail(("Should not have thrown a exception " + (e.getMessage())));
        }
    }

    /**
     * This test asserts that the keystore password and key password are different. This is only
     * true because they were explicitly set that way. Normal keystores that do not have passwords
     * set on individual keys will fail this test.
     */
    @Test
    public void testDifferentKeyPasswordWithoutSpecifyingKeyPassword() {
        try {
            final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
            final SSLContextService service = new StandardSSLContextService();
            final Map<String, String> properties = new HashMap<>();
            properties.put(KEYSTORE.getName(), DIFFERENT_PASS_KEYSTORE_PATH);
            properties.put(KEYSTORE_PASSWORD.getName(), DIFFERENT_KEYSTORE_PASSWORD);
            properties.put(KEYSTORE_TYPE.getName(), JKS_TYPE);
            runner.addControllerService("test-diff-keys", service, properties);
            // Assert the service is not valid due to an internal "cannot recover key" because the key password is missing
            runner.assertNotValid(service);
        } catch (Exception e) {
            System.out.println(e);
            Assert.fail(("Should not have thrown a exception " + (e.getMessage())));
        }
    }

    @Test
    public void testSSLAlgorithms() throws NoSuchAlgorithmException {
        final AllowableValue[] allowableValues = SSLContextService.buildAlgorithmAllowableValues();
        // we expect TLS, SSL, and all available configured JVM protocols
        final Set<String> expected = new HashSet<>();
        expected.add("SSL");
        expected.add("TLS");
        final String[] supportedProtocols = SSLContext.getDefault().createSSLEngine().getSupportedProtocols();
        expected.addAll(Arrays.asList(supportedProtocols));
        MatcherAssert.assertThat(allowableValues, IsNull.notNullValue());
        MatcherAssert.assertThat(allowableValues.length, CoreMatchers.equalTo(expected.size()));
        for (final AllowableValue value : allowableValues) {
            Assert.assertTrue(expected.contains(value.getValue()));
        }
    }
}

