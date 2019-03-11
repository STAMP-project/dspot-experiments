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
package org.apache.nifi.toolkit.tls.manager.writer;


import NifiPropertiesTlsClientConfigWriter.HOSTNAME_PROPERTIES;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;
import org.apache.nifi.toolkit.tls.configuration.TlsClientConfig;
import org.apache.nifi.toolkit.tls.properties.NiFiPropertiesWriter;
import org.apache.nifi.toolkit.tls.properties.NiFiPropertiesWriterFactory;
import org.apache.nifi.toolkit.tls.util.OutputStreamFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class NifiPropertiesTlsClientConfigWriterTest {
    @Mock
    NiFiPropertiesWriterFactory niFiPropertiesWriterFactory;

    @Mock
    OutputStreamFactory outputStreamFactory;

    private NiFiPropertiesWriter niFiPropertiesWriter;

    private int hostNum;

    private String testHostname;

    private File outputFile;

    private NifiPropertiesTlsClientConfigWriter nifiPropertiesTlsClientConfigWriter;

    private TlsClientConfig tlsClientConfig;

    private ByteArrayOutputStream outputStream;

    private String keyStore;

    private String keyStorePassword;

    private String trustStore;

    private String trustStorePassword;

    private Properties overlayProperties;

    private String keyPassword;

    private String keyStoreType;

    private String trustStoreType;

    @Test
    public void testDefaults() throws IOException {
        nifiPropertiesTlsClientConfigWriter.write(tlsClientConfig, outputStreamFactory);
        testHostnamesAndPorts();
        Assert.assertNotEquals(0, nifiPropertiesTlsClientConfigWriter.getIncrementingPropertyMap().size());
    }

    @Test(expected = NumberFormatException.class)
    public void testBadPortNum() throws IOException {
        nifiPropertiesTlsClientConfigWriter.getOverlayProperties().setProperty(nifiPropertiesTlsClientConfigWriter.getIncrementingPropertyMap().keySet().iterator().next(), "notAnInt");
        nifiPropertiesTlsClientConfigWriter.write(tlsClientConfig, outputStreamFactory);
    }

    @Test
    public void testNoHostnameProperties() throws IOException {
        nifiPropertiesTlsClientConfigWriter.getOverlayProperties().setProperty(HOSTNAME_PROPERTIES, "");
        nifiPropertiesTlsClientConfigWriter.write(tlsClientConfig, outputStreamFactory);
        testHostnamesAndPorts();
        Properties nifiProperties = getNifiProperties();
        nifiProperties.stringPropertyNames().forEach(( s) -> Assert.assertNotEquals(testHostname, nifiProperties.getProperty(s)));
    }
}

