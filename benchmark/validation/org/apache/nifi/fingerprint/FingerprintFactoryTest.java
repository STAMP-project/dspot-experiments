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
package org.apache.nifi.fingerprint;


import ScheduledState.RUNNING;
import SiteToSiteTransportProtocol.HTTP;
import SiteToSiteTransportProtocol.RAW;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.connectable.Position;
import org.apache.nifi.controller.serialization.ScheduledStateLookup;
import org.apache.nifi.encrypt.StringEncryptor;
import org.apache.nifi.groups.RemoteProcessGroup;
import org.apache.nifi.nar.ExtensionManager;
import org.apache.nifi.remote.RemoteGroupPort;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Element;


/**
 *
 */
public class FingerprintFactoryTest {
    private StringEncryptor encryptor;

    private ExtensionManager extensionManager;

    private FingerprintFactory fingerprinter;

    @Test
    public void testSameFingerprint() throws IOException {
        final String fp1 = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow1a.xml"), null);
        final String fp2 = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow1b.xml"), null);
        Assert.assertEquals(fp1, fp2);
    }

    @Test
    public void testDifferentFingerprint() throws IOException {
        final String fp1 = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow1a.xml"), null);
        final String fp2 = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow2.xml"), null);
        Assert.assertNotEquals(fp1, fp2);
    }

    @Test
    public void testResourceValueInFingerprint() throws IOException {
        final String fingerprint = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow1a.xml"), null);
        Assert.assertEquals(3, StringUtils.countMatches(fingerprint, "success"));
        Assert.assertTrue(fingerprint.contains("In Connection"));
    }

    @Test
    public void testSameFlowWithDifferentBundleShouldHaveDifferentFingerprints() throws IOException {
        final String fp1 = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow3-with-bundle-1.xml"), null);
        Assert.assertTrue(fp1.contains("org.apache.nifinifi-standard-nar1.0"));
        final String fp2 = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow3-with-bundle-2.xml"), null);
        Assert.assertTrue(fp2.contains("org.apache.nifinifi-standard-nar2.0"));
        Assert.assertNotEquals(fp1, fp2);
    }

    @Test
    public void testSameFlowAndOneHasNoBundleShouldHaveDifferentFingerprints() throws IOException {
        final String fp1 = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow3-with-bundle-1.xml"), null);
        Assert.assertTrue(fp1.contains("org.apache.nifinifi-standard-nar1.0"));
        final String fp2 = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow3-with-no-bundle.xml"), null);
        Assert.assertTrue(fp2.contains("MISSING_BUNDLE"));
        Assert.assertNotEquals(fp1, fp2);
    }

    @Test
    public void testSameFlowAndOneHasMissingBundleShouldHaveDifferentFingerprints() throws IOException {
        final String fp1 = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow3-with-bundle-1.xml"), null);
        Assert.assertTrue(fp1.contains("org.apache.nifinifi-standard-nar1.0"));
        final String fp2 = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow3-with-missing-bundle.xml"), null);
        Assert.assertTrue(fp2.contains("missingmissingmissing"));
        Assert.assertNotEquals(fp1, fp2);
    }

    @Test
    public void testConnectionWithMultipleRelationshipsSortedInFingerprint() throws IOException {
        final String fingerprint = fingerprinter.createFingerprint(getResourceBytes("/nifi/fingerprint/flow-connection-with-multiple-rels.xml"), null);
        Assert.assertNotNull(fingerprint);
        Assert.assertTrue(fingerprint.contains("AAABBBCCCDDD"));
    }

    @Test
    public void testSchemaValidation() throws IOException {
        FingerprintFactory fp = new FingerprintFactory(null, getValidatingDocumentBuilder(), extensionManager);
        final String fingerprint = fp.createFingerprint(getResourceBytes("/nifi/fingerprint/validating-flow.xml"), null);
    }

    @Test
    public void testRemoteProcessGroupFingerprintRaw() throws Exception {
        // Fill out every configuration.
        final RemoteProcessGroup component = Mockito.mock(RemoteProcessGroup.class);
        Mockito.when(component.getName()).thenReturn("name");
        Mockito.when(component.getIdentifier()).thenReturn("id");
        Mockito.when(component.getPosition()).thenReturn(new Position(10.5, 20.3));
        Mockito.when(component.getTargetUri()).thenReturn("http://node1:8080/nifi");
        Mockito.when(component.getTargetUris()).thenReturn("http://node1:8080/nifi, http://node2:8080/nifi");
        Mockito.when(component.getNetworkInterface()).thenReturn("eth0");
        Mockito.when(component.getComments()).thenReturn("comment");
        Mockito.when(component.getCommunicationsTimeout()).thenReturn("10 sec");
        Mockito.when(component.getYieldDuration()).thenReturn("30 sec");
        Mockito.when(component.getTransportProtocol()).thenReturn(RAW);
        Mockito.when(component.getProxyHost()).thenReturn(null);
        Mockito.when(component.getProxyPort()).thenReturn(null);
        Mockito.when(component.getProxyUser()).thenReturn(null);
        Mockito.when(component.getProxyPassword()).thenReturn(null);
        Mockito.when(component.getVersionedComponentId()).thenReturn(Optional.empty());
        // Assert fingerprints with expected one.
        final String expected = "id" + ((((((((("NO_VALUE" + "http://node1:8080/nifi, http://node2:8080/nifi") + "eth0") + "10 sec") + "30 sec") + "RAW") + "NO_VALUE") + "NO_VALUE") + "NO_VALUE") + "NO_VALUE");
        final Element rootElement = serializeElement(encryptor, RemoteProcessGroup.class, component, "addRemoteProcessGroup", ScheduledStateLookup.IDENTITY_LOOKUP);
        final Element componentElement = ((Element) (rootElement.getElementsByTagName("remoteProcessGroup").item(0)));
        Assert.assertEquals(expected, fingerprint("addRemoteProcessGroupFingerprint", Element.class, componentElement));
    }

    @Test
    public void testRemoteProcessGroupFingerprintWithProxy() throws Exception {
        // Fill out every configuration.
        final RemoteProcessGroup component = Mockito.mock(RemoteProcessGroup.class);
        Mockito.when(component.getName()).thenReturn("name");
        Mockito.when(component.getIdentifier()).thenReturn("id");
        Mockito.when(component.getPosition()).thenReturn(new Position(10.5, 20.3));
        Mockito.when(component.getTargetUri()).thenReturn("http://node1:8080/nifi");
        Mockito.when(component.getTargetUris()).thenReturn("http://node1:8080/nifi, http://node2:8080/nifi");
        Mockito.when(component.getComments()).thenReturn("comment");
        Mockito.when(component.getCommunicationsTimeout()).thenReturn("10 sec");
        Mockito.when(component.getYieldDuration()).thenReturn("30 sec");
        Mockito.when(component.getTransportProtocol()).thenReturn(HTTP);
        Mockito.when(component.getProxyHost()).thenReturn("proxy-host");
        Mockito.when(component.getProxyPort()).thenReturn(3128);
        Mockito.when(component.getProxyUser()).thenReturn("proxy-user");
        Mockito.when(component.getProxyPassword()).thenReturn("proxy-pass");
        Mockito.when(component.getVersionedComponentId()).thenReturn(Optional.empty());
        // Assert fingerprints with expected one.
        final String expected = "id" + ((((((((("NO_VALUE" + "http://node1:8080/nifi, http://node2:8080/nifi") + "NO_VALUE") + "10 sec") + "30 sec") + "HTTP") + "proxy-host") + "3128") + "proxy-user") + "proxy-pass");
        final Element rootElement = serializeElement(encryptor, RemoteProcessGroup.class, component, "addRemoteProcessGroup", ScheduledStateLookup.IDENTITY_LOOKUP);
        final Element componentElement = ((Element) (rootElement.getElementsByTagName("remoteProcessGroup").item(0)));
        Assert.assertEquals(expected.toString(), fingerprint("addRemoteProcessGroupFingerprint", Element.class, componentElement));
    }

    @Test
    public void testRemotePortFingerprint() throws Exception {
        // Fill out every configuration.
        final RemoteProcessGroup groupComponent = Mockito.mock(RemoteProcessGroup.class);
        Mockito.when(groupComponent.getName()).thenReturn("name");
        Mockito.when(groupComponent.getIdentifier()).thenReturn("id");
        Mockito.when(groupComponent.getPosition()).thenReturn(new Position(10.5, 20.3));
        Mockito.when(groupComponent.getTargetUri()).thenReturn("http://node1:8080/nifi");
        Mockito.when(groupComponent.getTransportProtocol()).thenReturn(RAW);
        Mockito.when(groupComponent.getVersionedComponentId()).thenReturn(Optional.empty());
        final RemoteGroupPort portComponent = Mockito.mock(RemoteGroupPort.class);
        Mockito.when(groupComponent.getInputPorts()).thenReturn(Collections.singleton(portComponent));
        Mockito.when(portComponent.getName()).thenReturn("portName");
        Mockito.when(portComponent.getIdentifier()).thenReturn("portId");
        Mockito.when(portComponent.getPosition()).thenReturn(new Position(10.5, 20.3));
        Mockito.when(portComponent.getComments()).thenReturn("portComment");
        Mockito.when(portComponent.getScheduledState()).thenReturn(RUNNING);
        Mockito.when(portComponent.getMaxConcurrentTasks()).thenReturn(3);
        Mockito.when(portComponent.isUseCompression()).thenReturn(true);
        Mockito.when(portComponent.getBatchCount()).thenReturn(1234);
        Mockito.when(portComponent.getBatchSize()).thenReturn("64KB");
        Mockito.when(portComponent.getBatchDuration()).thenReturn("10sec");
        // Serializer doesn't serialize if a port doesn't have any connection.
        Mockito.when(portComponent.hasIncomingConnection()).thenReturn(true);
        Mockito.when(portComponent.getVersionedComponentId()).thenReturn(Optional.empty());
        // Assert fingerprints with expected one.
        final String expected = "portId" + (((((("NO_VALUE" + "NO_VALUE") + "3") + "true") + "1234") + "64KB") + "10sec");
        final Element rootElement = serializeElement(encryptor, RemoteProcessGroup.class, groupComponent, "addRemoteProcessGroup", ScheduledStateLookup.IDENTITY_LOOKUP);
        final Element componentElement = ((Element) (rootElement.getElementsByTagName("inputPort").item(0)));
        Assert.assertEquals(expected, fingerprint("addRemoteGroupPortFingerprint", Element.class, componentElement));
    }
}

