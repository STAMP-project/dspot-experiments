/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.kafka;


import KafkaConstants.KAFKA_DEFAULT_SERIALIZER;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class KafkaComponentTest {
    private CamelContext context = Mockito.mock(CamelContext.class);

    @Test
    public void testPropertiesSet() throws Exception {
        String uri = "kafka:mytopic?brokers=broker1:12345,broker2:12566&partitioner=com.class.Party";
        KafkaEndpoint endpoint = ((KafkaEndpoint) (new KafkaComponent(context).createEndpoint(uri)));
        Assert.assertEquals("broker1:12345,broker2:12566", endpoint.getConfiguration().getBrokers());
        Assert.assertEquals("mytopic", endpoint.getConfiguration().getTopic());
        Assert.assertEquals("com.class.Party", endpoint.getConfiguration().getPartitioner());
    }

    @Test
    public void testBrokersOnComponent() throws Exception {
        KafkaComponent kafka = new KafkaComponent(context);
        kafka.setBrokers("broker1:12345,broker2:12566");
        String uri = "kafka:mytopic?partitioner=com.class.Party";
        KafkaEndpoint endpoint = ((KafkaEndpoint) (kafka.createEndpoint(uri)));
        Assert.assertEquals("broker1:12345,broker2:12566", endpoint.getConfiguration().getBrokers());
        Assert.assertEquals("broker1:12345,broker2:12566", endpoint.getComponent().getBrokers());
        Assert.assertEquals("mytopic", endpoint.getConfiguration().getTopic());
        Assert.assertEquals("com.class.Party", endpoint.getConfiguration().getPartitioner());
    }

    @Test
    public void testAllProducerConfigProperty() throws Exception {
        Map<String, Object> params = new HashMap<>();
        setProducerProperty(params);
        String uri = "kafka:mytopic?brokers=dev1:12345,dev2:12566";
        String remaining = "mytopic";
        KafkaEndpoint endpoint = createEndpoint(uri, remaining, params);
        Assert.assertEquals("mytopic", endpoint.getConfiguration().getTopic());
        Assert.assertEquals("1", endpoint.getConfiguration().getRequestRequiredAcks());
        Assert.assertEquals(new Integer(1), endpoint.getConfiguration().getBufferMemorySize());
        Assert.assertEquals(new Integer(10), endpoint.getConfiguration().getProducerBatchSize());
        Assert.assertEquals(new Integer(12), endpoint.getConfiguration().getConnectionMaxIdleMs());
        Assert.assertEquals(new Integer(1), endpoint.getConfiguration().getMaxBlockMs());
        Assert.assertEquals(new Integer(1), endpoint.getConfiguration().getBufferMemorySize());
        Assert.assertEquals("testing", endpoint.getConfiguration().getClientId());
        Assert.assertEquals("none", endpoint.getConfiguration().getCompressionCodec());
        Assert.assertEquals(new Integer(1), endpoint.getConfiguration().getLingerMs());
        Assert.assertEquals(new Integer(100), endpoint.getConfiguration().getMaxRequestSize());
        Assert.assertEquals(100, endpoint.getConfiguration().getRequestTimeoutMs().intValue());
        Assert.assertEquals(new Integer(1029), endpoint.getConfiguration().getMetadataMaxAgeMs());
        Assert.assertEquals(new Integer(23), endpoint.getConfiguration().getReceiveBufferBytes());
        Assert.assertEquals(new Integer(234), endpoint.getConfiguration().getReconnectBackoffMs());
        Assert.assertEquals(new Integer(234), endpoint.getConfiguration().getReconnectBackoffMaxMs());
        Assert.assertEquals(new Integer(0), endpoint.getConfiguration().getRetries());
        Assert.assertEquals(3782, endpoint.getConfiguration().getRetryBackoffMs().intValue());
        Assert.assertEquals(765, endpoint.getConfiguration().getSendBufferBytes().intValue());
        Assert.assertEquals(new Integer(1), endpoint.getConfiguration().getMaxInFlightRequest());
        Assert.assertEquals("org.apache.camel.reporters.TestReport,org.apache.camel.reporters.SampleReport", endpoint.getConfiguration().getMetricReporters());
        Assert.assertEquals(new Integer(3), endpoint.getConfiguration().getNoOfMetricsSample());
        Assert.assertEquals(new Integer(12344), endpoint.getConfiguration().getMetricsSampleWindowMs());
        Assert.assertEquals(KAFKA_DEFAULT_SERIALIZER, endpoint.getConfiguration().getSerializerClass());
        Assert.assertEquals(KAFKA_DEFAULT_SERIALIZER, endpoint.getConfiguration().getKeySerializerClass());
        Assert.assertEquals("testing", endpoint.getConfiguration().getSslKeyPassword());
        Assert.assertEquals("/abc", endpoint.getConfiguration().getSslKeystoreLocation());
        Assert.assertEquals("testing", endpoint.getConfiguration().getSslKeystorePassword());
        Assert.assertEquals("/abc", endpoint.getConfiguration().getSslTruststoreLocation());
        Assert.assertEquals("testing", endpoint.getConfiguration().getSslTruststorePassword());
        Assert.assertEquals("test", endpoint.getConfiguration().getSaslKerberosServiceName());
        Assert.assertEquals("PLAINTEXT", endpoint.getConfiguration().getSecurityProtocol());
        Assert.assertEquals("TLSv1.2", endpoint.getConfiguration().getSslEnabledProtocols());
        Assert.assertEquals("JKS", endpoint.getConfiguration().getSslKeystoreType());
        Assert.assertEquals("TLS", endpoint.getConfiguration().getSslProtocol());
        Assert.assertEquals("test", endpoint.getConfiguration().getSslProvider());
        Assert.assertEquals("JKS", endpoint.getConfiguration().getSslTruststoreType());
        Assert.assertEquals("/usr/bin/kinit", endpoint.getConfiguration().getKerberosInitCmd());
        Assert.assertEquals(new Integer(60000), endpoint.getConfiguration().getKerberosBeforeReloginMinTime());
        Assert.assertEquals(new Double(0.05), endpoint.getConfiguration().getKerberosRenewJitter());
        Assert.assertEquals(new Double(0.8), endpoint.getConfiguration().getKerberosRenewWindowFactor());
        Assert.assertEquals("MAC", endpoint.getConfiguration().getSslCipherSuites());
        Assert.assertEquals("test", endpoint.getConfiguration().getSslEndpointAlgorithm());
        Assert.assertEquals("SunX509", endpoint.getConfiguration().getSslKeymanagerAlgorithm());
        Assert.assertEquals("PKIX", endpoint.getConfiguration().getSslTrustmanagerAlgorithm());
    }

    @Test
    public void testAllProducerKeys() throws Exception {
        Map<String, Object> params = new HashMap<>();
        String uri = "kafka:mytopic?brokers=dev1:12345,dev2:12566";
        String remaining = "mytopic";
        KafkaEndpoint endpoint = createEndpoint(uri, remaining, params);
        Assert.assertEquals(endpoint.getConfiguration().createProducerProperties().keySet(), getProducerKeys().keySet());
    }
}

