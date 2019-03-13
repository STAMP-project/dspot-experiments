/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.step.jms.context;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.activemq.artemis.junit.EmbeddedJMSResource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogChannelInterfaceFactory;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.jms.JmsDelegate;


@RunWith(MockitoJUnitRunner.class)
public class ActiveMQProviderTest {
    @Mock
    LogChannelInterfaceFactory logChannelFactory;

    @Mock
    LogChannelInterface logChannel;

    @Rule
    public EmbeddedJMSResource resource = new EmbeddedJMSResource(0);

    private static final String AMQ_URL_BASE = "tcp://AMQ_URL_BASE:1234";

    private static final String AMQ_USERNAME_VAL = "AMQ_USERNAME_VAL";

    private static final String AMQ_PASSWORD_VAL = "AMQ_PASSWORD_VAL";

    private static final String TRUST_STORE_PATH_VAL = "TRUST_STORE_PATH_VAL";

    private static final String TRUST_STORE_PASS_VAL = "TRUST_STORE_PASS_VAL";

    private static final String KEY_STORE_PATH_VAL = "KEY_STORE_PATH_VAL";

    private static final String KEY_STORE_PASS_VAL = "KEY_STORE_PASS_VAL";

    private static final String ENABLED_CIPHER_SUITES_VAL = "ENABLED_CIPHER_SUITES_VAL";

    private static final String ENABLED_PROTOCOLS_VAL = "ENABLED_PROTOCOLS_VAL";

    private static final String VERIFY_HOST_VAL = "VERIFY_HOST_VAL";

    private static final String TRUST_ALL_VAL = "TRUST_ALL_VAL";

    private static final String SSL_PROVIDER_VAL = "SSL_PROVIDER_VAL";

    @Test
    public void testFullCircle() throws InterruptedException, ExecutionException, TimeoutException, KettleException {
        TransMeta consumerMeta = new TransMeta(getClass().getResource("/amq-consumer.ktr").getPath());
        Trans consumerTrans = new Trans(consumerMeta);
        consumerTrans.prepareExecution(new String[]{  });
        consumerTrans.startThreads();
        TransMeta producerMeta = new TransMeta(getClass().getResource("/amq-producer.ktr").getPath());
        Trans producerTrans = new Trans(producerMeta);
        producerTrans.prepareExecution(new String[]{  });
        producerTrans.startThreads();
        producerTrans.waitUntilFinished();
        Future<?> future = Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                if ((consumerTrans.getSteps().get(0).step.getLinesWritten()) == 10) {
                    break;
                }
            } 
        });
        future.get(5, TimeUnit.SECONDS);
        consumerTrans.safeStop();
        Assert.assertEquals(10, consumerTrans.getSteps().get(0).step.getLinesWritten());
    }

    /**
     * Verifies URI builder works with SSL off (ignore any values in the table)
     */
    @Test
    public void testUrlBuildSslOptionsNoSsl() {
        ActiveMQProvider provider = new ActiveMQProvider();
        JmsDelegate delegate = new JmsDelegate(Collections.singletonList(provider));
        delegate.amqUrl = ActiveMQProviderTest.AMQ_URL_BASE;
        delegate.sslEnabled = false;
        delegate.sslTruststorePath = ActiveMQProviderTest.TRUST_STORE_PATH_VAL;
        delegate.sslTruststorePassword = ActiveMQProviderTest.TRUST_STORE_PASS_VAL;
        String urlString = provider.buildUrl(delegate);
        try {
            URI url = new URI(urlString);
        } catch (URISyntaxException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertFalse("SSL disabled; should ignore params", urlString.contains(ActiveMQProviderTest.TRUST_STORE_PATH_VAL));
        Assert.assertTrue("URL base incorrect", ((urlString.compareTo(ActiveMQProviderTest.AMQ_URL_BASE)) == 0));
    }

    /**
     * Verifies URI builder works with SSL on and params already exist on the URI
     */
    @Test
    public void testUrlBuildSslOptionsParamsExist() {
        ActiveMQProvider provider = new ActiveMQProvider();
        JmsDelegate delegate = new JmsDelegate(Collections.singletonList(provider));
        delegate.amqUrl = (ActiveMQProviderTest.AMQ_URL_BASE) + "?foo=bar";
        delegate.sslEnabled = true;
        delegate.sslTruststorePath = ActiveMQProviderTest.TRUST_STORE_PATH_VAL;
        delegate.sslTruststorePassword = ActiveMQProviderTest.TRUST_STORE_PASS_VAL;
        String urlString = provider.buildUrl(delegate);
        try {
            URI url = new URI(urlString);
        } catch (URISyntaxException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue("Missing trust store path", urlString.contains(("trustStorePath=" + (ActiveMQProviderTest.TRUST_STORE_PATH_VAL))));
        Assert.assertTrue("Missing trust store password", urlString.contains(("trustStorePassword=" + (ActiveMQProviderTest.TRUST_STORE_PASS_VAL))));
        Assert.assertTrue("URL base incorrect", urlString.startsWith(((ActiveMQProviderTest.AMQ_URL_BASE) + "?")));
        delegate.amqUrl += ";";
        urlString = provider.buildUrl(delegate);
        try {
            URI url = new URI(urlString);
        } catch (URISyntaxException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue("Missing trust store path", urlString.contains(("trustStorePath=" + (ActiveMQProviderTest.TRUST_STORE_PATH_VAL))));
        Assert.assertTrue("Missing trust store password", urlString.contains(("trustStorePassword=" + (ActiveMQProviderTest.TRUST_STORE_PASS_VAL))));
        Assert.assertTrue("URL base incorrect", urlString.startsWith(((ActiveMQProviderTest.AMQ_URL_BASE) + "?")));
    }

    /**
     * Verifies URI builder works with all possible SSL options filled out
     * Note: not a realistic scenario but ensures code coverage
     */
    @Test
    public void testUrlBuildSslOptionsAllParams() {
        ActiveMQProvider provider = new ActiveMQProvider();
        JmsDelegate delegate = new JmsDelegate(Collections.singletonList(provider));
        delegate.amqUrl = ActiveMQProviderTest.AMQ_URL_BASE;
        delegate.sslEnabled = true;
        delegate.sslTruststorePath = ActiveMQProviderTest.TRUST_STORE_PATH_VAL;
        delegate.sslTruststorePassword = ActiveMQProviderTest.TRUST_STORE_PASS_VAL;
        delegate.sslKeystorePath = ActiveMQProviderTest.KEY_STORE_PATH_VAL;
        delegate.sslKeystorePassword = ActiveMQProviderTest.KEY_STORE_PASS_VAL;
        delegate.sslCipherSuite = ActiveMQProviderTest.ENABLED_CIPHER_SUITES_VAL;
        delegate.sslContextAlgorithm = ActiveMQProviderTest.ENABLED_PROTOCOLS_VAL;
        delegate.amqSslVerifyHost = ActiveMQProviderTest.VERIFY_HOST_VAL;
        delegate.amqSslTrustAll = ActiveMQProviderTest.TRUST_ALL_VAL;
        delegate.amqSslProvider = ActiveMQProviderTest.SSL_PROVIDER_VAL;
        delegate.sslUseDefaultContext = false;
        String urlString = provider.buildUrl(delegate);
        try {
            URI url = new URI(urlString);
        } catch (URISyntaxException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue("Missing trust store path", urlString.contains(("trustStorePath=" + (ActiveMQProviderTest.TRUST_STORE_PATH_VAL))));
        Assert.assertTrue("Missing trust store password", urlString.contains(("trustStorePassword=" + (ActiveMQProviderTest.TRUST_STORE_PASS_VAL))));
        Assert.assertTrue("Missing key store path", urlString.contains(("keyStorePath=" + (ActiveMQProviderTest.KEY_STORE_PATH_VAL))));
        Assert.assertTrue("Missing key store password", urlString.contains(("keyStorePassword=" + (ActiveMQProviderTest.KEY_STORE_PASS_VAL))));
        Assert.assertTrue("Missing cipher suite", urlString.contains(("enabledCipherSuites=" + (ActiveMQProviderTest.ENABLED_CIPHER_SUITES_VAL))));
        Assert.assertTrue("Missing protocols", urlString.contains(("enabledProtocols=" + (ActiveMQProviderTest.ENABLED_PROTOCOLS_VAL))));
        Assert.assertTrue("Missing verify host", urlString.contains(("verifyHost=" + (ActiveMQProviderTest.VERIFY_HOST_VAL))));
        Assert.assertTrue("Missing trust all", urlString.contains(("trustAll=" + (ActiveMQProviderTest.TRUST_ALL_VAL))));
        Assert.assertTrue("Missing ssl provider", urlString.contains(("sslProvider=" + (ActiveMQProviderTest.SSL_PROVIDER_VAL))));
        Assert.assertTrue("URL base incorrect", urlString.startsWith(((ActiveMQProviderTest.AMQ_URL_BASE) + "?")));
    }

    /**
     * Verifies URI builder works when user chooses Use Default SSL Context
     */
    @Test
    public void testUseDefaultSslContext() {
        ActiveMQProvider provider = new ActiveMQProvider();
        JmsDelegate delegate = new JmsDelegate(Collections.singletonList(provider));
        delegate.amqUrl = ActiveMQProviderTest.AMQ_URL_BASE;
        delegate.sslEnabled = true;
        delegate.sslTruststorePath = ActiveMQProviderTest.TRUST_STORE_PATH_VAL;
        delegate.sslTruststorePassword = ActiveMQProviderTest.TRUST_STORE_PASS_VAL;
        delegate.sslKeystorePath = ActiveMQProviderTest.KEY_STORE_PATH_VAL;
        delegate.sslKeystorePassword = ActiveMQProviderTest.KEY_STORE_PASS_VAL;
        delegate.sslCipherSuite = ActiveMQProviderTest.ENABLED_CIPHER_SUITES_VAL;
        delegate.sslContextAlgorithm = ActiveMQProviderTest.ENABLED_PROTOCOLS_VAL;
        delegate.amqSslVerifyHost = ActiveMQProviderTest.VERIFY_HOST_VAL;
        delegate.amqSslTrustAll = ActiveMQProviderTest.TRUST_ALL_VAL;
        delegate.amqSslProvider = ActiveMQProviderTest.SSL_PROVIDER_VAL;
        delegate.sslUseDefaultContext = true;
        String urlString = provider.buildUrl(delegate);
        try {
            URI url = new URI(urlString);
        } catch (URISyntaxException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertFalse("Should not include trust store path", urlString.contains(("trustStorePath=" + (ActiveMQProviderTest.TRUST_STORE_PATH_VAL))));
        Assert.assertFalse("Should not include trust store password", urlString.contains(("trustStorePassword=" + (ActiveMQProviderTest.TRUST_STORE_PASS_VAL))));
        Assert.assertFalse("Should not include key store path", urlString.contains(("keyStorePath=" + (ActiveMQProviderTest.KEY_STORE_PATH_VAL))));
        Assert.assertFalse("Should not include key store password", urlString.contains(("keyStorePassword=" + (ActiveMQProviderTest.KEY_STORE_PASS_VAL))));
        Assert.assertFalse("Should not include cipher suite", urlString.contains(("enabledCipherSuites=" + (ActiveMQProviderTest.ENABLED_CIPHER_SUITES_VAL))));
        Assert.assertFalse("Should not include protocols", urlString.contains(("enabledProtocols=" + (ActiveMQProviderTest.ENABLED_PROTOCOLS_VAL))));
        Assert.assertFalse("Should not include verify host", urlString.contains(("verifyHost=" + (ActiveMQProviderTest.VERIFY_HOST_VAL))));
        Assert.assertFalse("Should not include trust all", urlString.contains(("trustAll=" + (ActiveMQProviderTest.TRUST_ALL_VAL))));
        Assert.assertFalse("Should not include ssl provider", urlString.contains(("sslProvider=" + (ActiveMQProviderTest.SSL_PROVIDER_VAL))));
        Assert.assertTrue("Missing Use default SSL context", urlString.contains("useDefaultSslContext=true"));
        Assert.assertTrue("URL base incorrect", urlString.startsWith(((ActiveMQProviderTest.AMQ_URL_BASE) + "?")));
    }

    /**
     * Verifies getConnectionParams works as expected; should return the same URL used to connect.
     */
    @Test
    public void testGetConnectionParams() {
        ActiveMQProvider provider = new ActiveMQProvider();
        JmsDelegate delegate = new JmsDelegate(Collections.singletonList(provider));
        delegate.amqUrl = ActiveMQProviderTest.AMQ_URL_BASE;
        delegate.amqUsername = ActiveMQProviderTest.AMQ_USERNAME_VAL;
        delegate.amqPassword = ActiveMQProviderTest.AMQ_PASSWORD_VAL;
        delegate.sslEnabled = true;
        delegate.sslTruststorePath = ActiveMQProviderTest.TRUST_STORE_PATH_VAL;
        delegate.sslTruststorePassword = ActiveMQProviderTest.TRUST_STORE_PASS_VAL;
        delegate.sslKeystorePath = ActiveMQProviderTest.KEY_STORE_PATH_VAL;
        delegate.sslKeystorePassword = ActiveMQProviderTest.KEY_STORE_PASS_VAL;
        delegate.sslCipherSuite = ActiveMQProviderTest.ENABLED_CIPHER_SUITES_VAL;
        delegate.sslContextAlgorithm = ActiveMQProviderTest.ENABLED_PROTOCOLS_VAL;
        delegate.amqSslVerifyHost = ActiveMQProviderTest.VERIFY_HOST_VAL;
        delegate.amqSslTrustAll = ActiveMQProviderTest.TRUST_ALL_VAL;
        delegate.amqSslProvider = ActiveMQProviderTest.SSL_PROVIDER_VAL;
        String urlString = provider.buildUrl(delegate);
        String paramString = provider.getConnectionDetails(delegate);
        try {
            URI url = new URI(urlString);
        } catch (URISyntaxException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue("Missing trust store path", urlString.contains(("trustStorePath=" + (ActiveMQProviderTest.TRUST_STORE_PATH_VAL))));
        Assert.assertTrue("Missing trust store password", urlString.contains(("trustStorePassword=" + (ActiveMQProviderTest.TRUST_STORE_PASS_VAL))));
        Assert.assertTrue("Missing key store path", urlString.contains(("keyStorePath=" + (ActiveMQProviderTest.KEY_STORE_PATH_VAL))));
        Assert.assertTrue("Missing key store password", urlString.contains(("keyStorePassword=" + (ActiveMQProviderTest.KEY_STORE_PASS_VAL))));
        Assert.assertTrue("Missing cipher suite", urlString.contains(("enabledCipherSuites=" + (ActiveMQProviderTest.ENABLED_CIPHER_SUITES_VAL))));
        Assert.assertTrue("Missing protocols", urlString.contains(("enabledProtocols=" + (ActiveMQProviderTest.ENABLED_PROTOCOLS_VAL))));
        Assert.assertTrue("Missing verify host", urlString.contains(("verifyHost=" + (ActiveMQProviderTest.VERIFY_HOST_VAL))));
        Assert.assertTrue("Missing trust all", urlString.contains(("trustAll=" + (ActiveMQProviderTest.TRUST_ALL_VAL))));
        Assert.assertTrue("Missing ssl provider", urlString.contains(("sslProvider=" + (ActiveMQProviderTest.SSL_PROVIDER_VAL))));
        Assert.assertTrue("URL base incorrect", urlString.startsWith(((ActiveMQProviderTest.AMQ_URL_BASE) + "?")));
        Assert.assertTrue("Connection params missing URL", paramString.contains(("URL: " + urlString)));
        Assert.assertTrue("Connection params missing user name", paramString.contains(("User Name: " + (ActiveMQProviderTest.AMQ_USERNAME_VAL))));
        Assert.assertTrue("Connection params missing password", paramString.contains(("Password: " + (ActiveMQProviderTest.AMQ_PASSWORD_VAL))));
    }
}

