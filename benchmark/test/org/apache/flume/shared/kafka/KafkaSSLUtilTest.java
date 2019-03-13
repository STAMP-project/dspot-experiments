/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.shared.kafka;


import CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import SecurityProtocol.PLAINTEXT.name;
import SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG;
import SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG;
import SslConfigs.SSL_KEYSTORE_TYPE_CONFIG;
import SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG;
import SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG;
import SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG;
import java.util.Properties;
import org.junit.Test;


public class KafkaSSLUtilTest {
    @Test
    public void testSecurityProtocol_PLAINTEXT() {
        Properties kafkaProps = new Properties();
        kafkaProps.put(SECURITY_PROTOCOL_CONFIG, name);
        KafkaSSLUtil.addGlobalSSLParameters(kafkaProps);
        assertNoSSLParameters(kafkaProps);
    }

    @Test
    public void testSecurityProtocol_SASL_PLAINTEXT() {
        Properties kafkaProps = new Properties();
        kafkaProps.put(SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name);
        KafkaSSLUtil.addGlobalSSLParameters(kafkaProps);
        assertNoSSLParameters(kafkaProps);
    }

    @Test
    public void testSecurityProtocol_SSL() {
        Properties kafkaProps = new Properties();
        kafkaProps.put(SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SSL.name);
        KafkaSSLUtil.addGlobalSSLParameters(kafkaProps);
        assertGlobalSSLParameters(kafkaProps);
    }

    @Test
    public void testSecurityProtocol_SASL_SSL() {
        Properties kafkaProps = new Properties();
        kafkaProps.put(SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_SSL.name);
        KafkaSSLUtil.addGlobalSSLParameters(kafkaProps);
        assertGlobalSSLParameters(kafkaProps);
    }

    @Test
    public void testComponentParametersNotOverridden() {
        Properties kafkaProps = new Properties();
        kafkaProps.put(SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SSL.name);
        kafkaProps.put(SSL_KEYSTORE_LOCATION_CONFIG, "component-keystore-path");
        kafkaProps.put(SSL_KEYSTORE_PASSWORD_CONFIG, "component-keystore-password");
        kafkaProps.put(SSL_KEYSTORE_TYPE_CONFIG, "component-keystore-type");
        kafkaProps.put(SSL_TRUSTSTORE_LOCATION_CONFIG, "component-truststore-path");
        kafkaProps.put(SSL_TRUSTSTORE_PASSWORD_CONFIG, "component-truststore-password");
        kafkaProps.put(SSL_TRUSTSTORE_TYPE_CONFIG, "component-truststore-type");
        KafkaSSLUtil.addGlobalSSLParameters(kafkaProps);
        assertComponentSSLParameters(kafkaProps);
    }

    @Test
    public void testEmptyGlobalParametersNotAdded() {
        Properties kafkaProps = new Properties();
        kafkaProps.put(SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SSL.name);
        clearSystemProperties();
        KafkaSSLUtil.addGlobalSSLParameters(kafkaProps);
        assertNoSSLParameters(kafkaProps);
    }
}

