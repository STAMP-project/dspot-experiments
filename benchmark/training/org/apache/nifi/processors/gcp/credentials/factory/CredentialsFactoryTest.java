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
package org.apache.nifi.processors.gcp.credentials.factory;


import CredentialPropertyDescriptors.SERVICE_ACCOUNT_JSON;
import CredentialPropertyDescriptors.SERVICE_ACCOUNT_JSON_FILE;
import CredentialPropertyDescriptors.USE_APPLICATION_DEFAULT_CREDENTIALS;
import CredentialPropertyDescriptors.USE_COMPUTE_ENGINE_CREDENTIALS;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.http.HttpTransportFactory;
import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests of the validation and credentials provider capabilities of CredentialsFactory.
 */
public class CredentialsFactoryTest {
    private static final HttpTransport TRANSPORT = new NetHttpTransport();

    private static final HttpTransportFactory TRANSPORT_FACTORY = () -> TRANSPORT;

    @Test
    public void testCredentialPropertyDescriptorClassCannotBeInvoked() throws Exception {
        Constructor constructor = CredentialPropertyDescriptors.class.getDeclaredConstructor();
        Assert.assertTrue("Constructor of CredentialPropertyDescriptors should be private", Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testImplicitApplicationDefaultCredentials() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(MockCredentialsFactoryProcessor.class);
        runner.assertValid();
        Map<PropertyDescriptor, String> properties = runner.getProcessContext().getProperties();
        final CredentialsFactory factory = new CredentialsFactory();
        final GoogleCredentials credentials = factory.getGoogleCredentials(properties, CredentialsFactoryTest.TRANSPORT_FACTORY);
        Assert.assertNotNull(credentials);
    }

    @Test
    public void testExplicitApplicationDefaultCredentials() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(MockCredentialsFactoryProcessor.class);
        runner.setProperty(USE_APPLICATION_DEFAULT_CREDENTIALS, "true");
        runner.assertValid();
        Map<PropertyDescriptor, String> properties = runner.getProcessContext().getProperties();
        final CredentialsFactory factory = new CredentialsFactory();
        final GoogleCredentials credentials = factory.getGoogleCredentials(properties, CredentialsFactoryTest.TRANSPORT_FACTORY);
        Assert.assertNotNull(credentials);
    }

    @Test
    public void testExplicitApplicationDefaultCredentialsExclusive() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(MockCredentialsFactoryProcessor.class);
        runner.setProperty(USE_APPLICATION_DEFAULT_CREDENTIALS, "true");
        runner.setProperty(USE_COMPUTE_ENGINE_CREDENTIALS, "true");
        runner.assertNotValid();
    }

    @Test
    public void testJsonFileCredentials() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(MockCredentialsFactoryProcessor.class);
        runner.setProperty(SERVICE_ACCOUNT_JSON_FILE, "src/test/resources/mock-gcp-service-account.json");
        runner.assertValid();
        Map<PropertyDescriptor, String> properties = runner.getProcessContext().getProperties();
        final CredentialsFactory factory = new CredentialsFactory();
        final GoogleCredentials credentials = factory.getGoogleCredentials(properties, CredentialsFactoryTest.TRANSPORT_FACTORY);
        Assert.assertNotNull(credentials);
        Assert.assertEquals("credentials class should be equal", ServiceAccountCredentials.class, credentials.getClass());
    }

    @Test
    public void testBadJsonFileCredentials() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(MockCredentialsFactoryProcessor.class);
        runner.setProperty(SERVICE_ACCOUNT_JSON_FILE, "src/test/resources/bad-mock-gcp-service-account.json");
        runner.assertNotValid();
    }

    @Test
    public void testJsonStringCredentials() throws Exception {
        final String jsonRead = new String(Files.readAllBytes(Paths.get("src/test/resources/mock-gcp-service-account.json")));
        final TestRunner runner = TestRunners.newTestRunner(MockCredentialsFactoryProcessor.class);
        runner.setProperty(SERVICE_ACCOUNT_JSON, jsonRead);
        runner.assertValid();
        Map<PropertyDescriptor, String> properties = runner.getProcessContext().getProperties();
        final CredentialsFactory factory = new CredentialsFactory();
        final GoogleCredentials credentials = factory.getGoogleCredentials(properties, CredentialsFactoryTest.TRANSPORT_FACTORY);
        Assert.assertNotNull(credentials);
        Assert.assertEquals("credentials class should be equal", ServiceAccountCredentials.class, credentials.getClass());
    }

    @Test
    public void testComputeEngineCredentials() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(MockCredentialsFactoryProcessor.class);
        runner.setProperty(USE_COMPUTE_ENGINE_CREDENTIALS, "true");
        runner.assertValid();
        Map<PropertyDescriptor, String> properties = runner.getProcessContext().getProperties();
        final CredentialsFactory factory = new CredentialsFactory();
        final GoogleCredentials credentials = factory.getGoogleCredentials(properties, CredentialsFactoryTest.TRANSPORT_FACTORY);
        Assert.assertNotNull(credentials);
        Assert.assertEquals("credentials class should be equal", ComputeEngineCredentials.class, credentials.getClass());
    }
}

