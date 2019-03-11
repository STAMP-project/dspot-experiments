/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.io.mqttembeddedbroker.internal;


import MqttConnectionState.CONNECTED;
import java.io.IOException;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.eclipse.smarthome.io.transport.mqtt.MqttConnectionObserver;
import org.eclipse.smarthome.io.transport.mqtt.MqttConnectionState;
import org.eclipse.smarthome.io.transport.mqtt.MqttService;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests cases for {@link MqttBrokerHandler}. The tests provide mocks for supporting entities using Mockito.
 *
 * @author David Graeff - Initial contribution
 */
public class MqttEmbeddedBrokerServiceTest {
    // Create an accept all trust manager for a client SSLContext
    @SuppressWarnings("unused")
    private static class X509ExtendedTrustManagerEx extends X509ExtendedTrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
        }
    }

    private EmbeddedBrokerServiceImpl subject;

    @Mock
    private MqttService service;

    @Test
    public void connectToEmbeddedServer() throws IOException, InterruptedException {
        ServiceConfiguration config = new ServiceConfiguration();
        config.username = "username";
        config.password = "password";
        config.port = 12345;
        config.secure = false;
        config.persistenceFile = "";
        subject.initialize(config);
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        MqttBrokerConnection c = subject.getConnection();
        MqttConnectionObserver mqttConnectionObserver = ( state, error) -> {
            if (state == MqttConnectionState.CONNECTED) {
                semaphore.release();
            }
        };
        c.addConnectionObserver(mqttConnectionObserver);
        if ((c.connectionState()) == (MqttConnectionState.CONNECTED)) {
            semaphore.release();
        }
        // Start the connection and wait until timeout or connected callback returns.
        semaphore.tryAcquire(3000, TimeUnit.MILLISECONDS);
        c.removeConnectionObserver(mqttConnectionObserver);
        Assert.assertThat(c.getUser(), CoreMatchers.is("username"));
        Assert.assertThat(c.getPassword(), CoreMatchers.is("password"));
        Assert.assertThat(c.connectionState(), CoreMatchers.is(CONNECTED));
        Mockito.verify(service).addBrokerConnection(ArgumentMatchers.anyString(), ArgumentMatchers.eq(c));
    }
}

