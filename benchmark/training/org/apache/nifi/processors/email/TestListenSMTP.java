/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.nifi.processors.email;


import ListenSMTP.CLIENT_AUTH;
import ListenSMTP.REL_SUCCESS;
import ListenSMTP.SMTP_MAXIMUM_CONNECTIONS;
import ListenSMTP.SMTP_MAXIMUM_MSG_SIZE;
import ListenSMTP.SMTP_PORT;
import ListenSMTP.SSL_CONTEXT_SERVICE;
import SSLContextService.ClientAuth.NONE;
import StandardSSLContextService.KEYSTORE;
import StandardSSLContextService.KEYSTORE_PASSWORD;
import StandardSSLContextService.KEYSTORE_TYPE;
import StandardSSLContextService.TRUSTSTORE;
import StandardSSLContextService.TRUSTSTORE_PASSWORD;
import StandardSSLContextService.TRUSTSTORE_TYPE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.nifi.remote.io.socket.NetworkUtils;
import org.apache.nifi.ssl.SSLContextService;
import org.apache.nifi.ssl.StandardRestrictedSSLContextService;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestListenSMTP {
    private ScheduledExecutorService executor;

    @Test
    public void validateSuccessfulInteraction() throws Exception, EmailException {
        int port = NetworkUtils.availablePort();
        TestRunner runner = TestRunners.newTestRunner(ListenSMTP.class);
        runner.setProperty(SMTP_PORT, String.valueOf(port));
        runner.setProperty(SMTP_MAXIMUM_CONNECTIONS, "3");
        runner.assertValid();
        runner.run(5, false);
        final int numMessages = 5;
        CountDownLatch latch = new CountDownLatch(numMessages);
        this.executor.schedule(() -> {
            for (int i = 0; i < numMessages; i++) {
                try {
                    Email email = new SimpleEmail();
                    email.setHostName("localhost");
                    email.setSmtpPort(port);
                    email.setFrom("alice@nifi.apache.org");
                    email.setSubject("This is a test");
                    email.setMsg(("MSG-" + i));
                    email.addTo("bob@nifi.apache.org");
                    email.send();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }
        }, 1500, TimeUnit.MILLISECONDS);
        boolean complete = latch.await(5000, TimeUnit.MILLISECONDS);
        runner.shutdown();
        Assert.assertTrue(complete);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, numMessages);
    }

    @Test
    public void validateSuccessfulInteractionWithTls() throws Exception, EmailException {
        System.setProperty("mail.smtp.ssl.trust", "*");
        System.setProperty("javax.net.ssl.keyStore", "src/test/resources/keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "passwordpassword");
        int port = NetworkUtils.availablePort();
        TestRunner runner = TestRunners.newTestRunner(ListenSMTP.class);
        runner.setProperty(SMTP_PORT, String.valueOf(port));
        runner.setProperty(SMTP_MAXIMUM_CONNECTIONS, "3");
        // Setup the SSL Context
        SSLContextService sslContextService = new StandardRestrictedSSLContextService();
        runner.addControllerService("ssl-context", sslContextService);
        runner.setProperty(sslContextService, TRUSTSTORE, "src/test/resources/truststore.jks");
        runner.setProperty(sslContextService, TRUSTSTORE_PASSWORD, "passwordpassword");
        runner.setProperty(sslContextService, TRUSTSTORE_TYPE, "JKS");
        runner.setProperty(sslContextService, KEYSTORE, "src/test/resources/keystore.jks");
        runner.setProperty(sslContextService, KEYSTORE_PASSWORD, "passwordpassword");
        runner.setProperty(sslContextService, KEYSTORE_TYPE, "JKS");
        runner.enableControllerService(sslContextService);
        // and add the SSL context to the runner
        runner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        runner.setProperty(CLIENT_AUTH, NONE.name());
        runner.assertValid();
        int messageCount = 5;
        CountDownLatch latch = new CountDownLatch(messageCount);
        runner.run(messageCount, false);
        this.executor.schedule(() -> {
            for (int i = 0; i < messageCount; i++) {
                try {
                    Email email = new SimpleEmail();
                    email.setHostName("localhost");
                    email.setSmtpPort(port);
                    email.setFrom("alice@nifi.apache.org");
                    email.setSubject("This is a test");
                    email.setMsg(("MSG-" + i));
                    email.addTo("bob@nifi.apache.org");
                    // Enable STARTTLS but ignore the cert
                    email.setStartTLSEnabled(true);
                    email.setStartTLSRequired(true);
                    email.setSSLCheckServerIdentity(false);
                    email.send();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }
        }, 1500, TimeUnit.MILLISECONDS);
        boolean complete = latch.await(5000, TimeUnit.MILLISECONDS);
        runner.shutdown();
        Assert.assertTrue(complete);
        runner.assertAllFlowFilesTransferred("success", messageCount);
    }

    @Test
    public void validateTooLargeMessage() throws Exception, EmailException {
        int port = NetworkUtils.availablePort();
        TestRunner runner = TestRunners.newTestRunner(ListenSMTP.class);
        runner.setProperty(SMTP_PORT, String.valueOf(port));
        runner.setProperty(SMTP_MAXIMUM_CONNECTIONS, "3");
        runner.setProperty(SMTP_MAXIMUM_MSG_SIZE, "10 B");
        runner.assertValid();
        int messageCount = 1;
        CountDownLatch latch = new CountDownLatch(messageCount);
        runner.run(messageCount, false);
        this.executor.schedule(() -> {
            for (int i = 0; i < messageCount; i++) {
                try {
                    Email email = new SimpleEmail();
                    email.setHostName("localhost");
                    email.setSmtpPort(port);
                    email.setFrom("alice@nifi.apache.org");
                    email.setSubject("This is a test");
                    email.setMsg(("MSG-" + i));
                    email.addTo("bob@nifi.apache.org");
                    email.send();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }
        }, 1000, TimeUnit.MILLISECONDS);
        boolean complete = latch.await(5000, TimeUnit.MILLISECONDS);
        runner.shutdown();
        Assert.assertTrue(complete);
        runner.assertAllFlowFilesTransferred("success", 0);
    }
}

