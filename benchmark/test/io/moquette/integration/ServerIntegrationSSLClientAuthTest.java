/**
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.moquette.integration;


import io.moquette.broker.Server;
import javax.net.ssl.SSLSocketFactory;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Check that Moquette could also handle SSL with client authentication.
 *
 * <p>
 * Create certificates needed for client authentication
 *
 * <pre>
 * # generate integration certificate chain (valid for 10000 days)
 * keytool -genkeypair -alias signedtestserver -dname cn=moquette.eclipse.org -keyalg RSA -keysize 2048 \
 * -keystore signedserverkeystore.jks -keypass passw0rdsrv -storepass passw0rdsrv -validity 10000
 * keytool -genkeypair -alias signedtestserver_sub -dname cn=moquette.eclipse.org -keyalg RSA -keysize 2048 \
 * -keystore signedserverkeystore.jks -keypass passw0rdsrv -storepass passw0rdsrv
 *
 * # sign integration subcertificate with integration certificate (valid for 10000 days)
 * keytool -certreq -alias signedtestserver_sub -keystore signedserverkeystore.jks -keypass passw0rdsrv \
 * -storepass passw0rdsrv | \
 * keytool -gencert -alias signedtestserver -keystore signedserverkeystore.jks -keypass passw0rdsrv \
 * -storepass passw0rdsrv -validity 10000 | \
 * keytool -importcert -alias signedtestserver_sub -keystore signedserverkeystore.jks -keypass passw0rdsrv \
 * -storepass passw0rdsrv
 *
 * # generate client keypair
 * keytool -genkeypair -alias signedtestclient -dname cn=moquette.eclipse.org -keyalg RSA -keysize 2048 \
 * -keystore signedclientkeystore.jks -keypass passw0rd -storepass passw0rd
 *
 * # create signed client certificate with integration subcertificate and import to client keystore (valid for 10000 days)
 * keytool -certreq -alias signedtestclient -keystore signedclientkeystore.jks -keypass passw0rd -storepass passw0rd | \
 * keytool -gencert -alias signedtestserver_sub -keystore signedserverkeystore.jks -keypass passw0rdsrv \
 * -storepass passw0rdsrv -validity 10000 | \
 * keytool -importcert -alias signedtestclient -keystore signedclientkeystore.jks -keypass passw0rd \
 * -storepass passw0rd -noprompt
 *
 * # import integration certificates into signed truststore
 * keytool -exportcert -alias signedtestserver -keystore signedserverkeystore.jks -keypass passw0rdsrv \
 * -storepass passw0rdsrv | \
 * keytool -importcert -trustcacerts -noprompt -alias signedtestserver -keystore signedclientkeystore.jks \
 * -keypass passw0rd -storepass passw0rd
 * keytool -exportcert -alias signedtestserver_sub -keystore signedserverkeystore.jks -keypass passw0rdsrv \
 * -storepass passw0rdsrv | \
 * keytool -importcert -trustcacerts -noprompt -alias signedtestserver_sub -keystore signedclientkeystore.jks \
 * -keypass passw0rd -storepass passw0rd
 *
 * # create unsigned client certificate (valid for 10000 days)
 * keytool -genkeypair -alias unsignedtestclient -dname cn=moquette.eclipse.org -validity 10000 -keyalg RSA \
 * -keysize 2048 -keystore unsignedclientkeystore.jks -keypass passw0rd -storepass passw0rd
 *
 * # import integration certificates into unsigned truststore
 * keytool -exportcert -alias signedtestserver -keystore signedserverkeystore.jks -keypass passw0rdsrv \
 * -storepass passw0rdsrv | \
 * keytool -importcert -trustcacerts -noprompt -alias signedtestserver -keystore unsignedclientkeystore.jks \
 * -keypass passw0rd -storepass passw0rd
 * keytool -exportcert -alias signedtestserver_sub -keystore signedserverkeystore.jks -keypass passw0rdsrv \
 * -storepass passw0rdsrv | \
 * keytool -importcert -trustcacerts -noprompt -alias signedtestserver_sub -keystore unsignedclientkeystore.jks \
 * -keypass passw0rd -storepass passw0rd
 * </pre>
 * </p>
 */
public class ServerIntegrationSSLClientAuthTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationSSLClientAuthTest.class);

    Server m_server;

    static MqttClientPersistence s_dataStore;

    IMqttClient m_client;

    MessageCollector m_callback;

    static String backup;

    @Test
    public void checkClientAuthentication() throws Exception {
        ServerIntegrationSSLClientAuthTest.LOG.info("*** checkClientAuthentication ***");
        SSLSocketFactory ssf = configureSSLSocketFactory("signedclientkeystore.jks");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setSocketFactory(ssf);
        m_client.connect(options);
        m_client.subscribe("/topic", 0);
        m_client.disconnect();
    }

    @Test(expected = MqttException.class)
    public void checkClientAuthenticationFail() throws Exception {
        ServerIntegrationSSLClientAuthTest.LOG.info("*** checkClientAuthenticationFail ***");
        SSLSocketFactory ssf = configureSSLSocketFactory("unsignedclientkeystore.jks");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setSocketFactory(ssf);
        // actual a "Broken pipe" is thrown, this is not very specific.
        try {
            m_client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
            throw e;
        }
    }
}

