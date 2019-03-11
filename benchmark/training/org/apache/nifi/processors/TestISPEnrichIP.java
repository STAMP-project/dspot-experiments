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
package org.apache.nifi.processors;


import ISPEnrichIP.GEO_DATABASE_FILE;
import ISPEnrichIP.IP_ADDRESS_ATTRIBUTE;
import ISPEnrichIP.REL_FOUND;
import ISPEnrichIP.REL_NOT_FOUND;
import com.maxmind.geoip2.model.IspResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processors.maxmind.DatabaseReader;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ ISPEnrichIP.class })
@SuppressWarnings("WeakerAccess")
public class TestISPEnrichIP {
    DatabaseReader databaseReader;

    ISPEnrichIP ispEnrichIP;

    TestRunner testRunner;

    @Test
    public void verifyNonExistentIpFlowsToNotFoundRelationship() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "ip");
        testRunner.enqueue(new byte[0], Collections.emptyMap());
        testRunner.run();
        List<MockFlowFile> notFound = testRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        List<MockFlowFile> found = testRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertEquals(1, notFound.size());
        Assert.assertEquals(0, found.size());
        Mockito.verify(databaseReader).isp(InetAddress.getByName(null));
    }

    @Test
    public void successfulMaxMindResponseShouldFlowToFoundRelationship() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "ip");
        final IspResponse ispResponse = getIspResponse("1.2.3.4");
        Mockito.when(databaseReader.isp(InetAddress.getByName("1.2.3.4"))).thenReturn(ispResponse);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("ip", "1.2.3.4");
        testRunner.enqueue(new byte[0], attributes);
        testRunner.run();
        List<MockFlowFile> notFound = testRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        List<MockFlowFile> found = testRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertEquals(0, notFound.size());
        Assert.assertEquals(1, found.size());
        FlowFile finishedFound = found.get(0);
        Assert.assertNotNull(finishedFound.getAttribute("ip.isp.lookup.micros"));
        Assert.assertEquals("Apache NiFi - Test ISP", finishedFound.getAttribute("ip.isp.name"));
        Assert.assertEquals("Apache NiFi - Test Organization", finishedFound.getAttribute("ip.isp.organization"));
        Assert.assertEquals("1337", finishedFound.getAttribute("ip.isp.asn"));
        Assert.assertEquals("Apache NiFi - Test Chocolate", finishedFound.getAttribute("ip.isp.asn.organization"));
    }

    @Test
    public void successfulMaxMindResponseShouldFlowToFoundRelationshipWhenAsnIsNotSet() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "ip");
        final IspResponse ispResponse = getIspResponseWithoutASNDetail("1.2.3.4");
        Mockito.when(databaseReader.isp(InetAddress.getByName("1.2.3.4"))).thenReturn(ispResponse);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("ip", "1.2.3.4");
        testRunner.enqueue(new byte[0], attributes);
        testRunner.run();
        List<MockFlowFile> notFound = testRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        List<MockFlowFile> found = testRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertEquals(0, notFound.size());
        Assert.assertEquals(1, found.size());
        FlowFile finishedFound = found.get(0);
        Assert.assertNotNull(finishedFound.getAttribute("ip.isp.lookup.micros"));
        Assert.assertNotNull(finishedFound.getAttribute("ip.isp.lookup.micros"));
        Assert.assertEquals("Apache NiFi - Test ISP", finishedFound.getAttribute("ip.isp.name"));
        Assert.assertEquals("Apache NiFi - Test Organization", finishedFound.getAttribute("ip.isp.organization"));
        Assert.assertNull(finishedFound.getAttribute("ip.isp.asn"));
        Assert.assertNull(finishedFound.getAttribute("ip.isp.asn.organization"));
    }

    @Test
    public void evaluatingExpressionLanguageShouldAndFindingIpFieldWithSuccessfulLookUpShouldFlowToFoundRelationship() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "${ip.fields:substringBefore(',')}");
        final IspResponse ispResponse = getIspResponse("1.2.3.4");
        Mockito.when(databaseReader.isp(InetAddress.getByName("1.2.3.4"))).thenReturn(ispResponse);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("ip.fields", "ip0,ip1,ip2");
        attributes.put("ip0", "1.2.3.4");
        testRunner.enqueue(new byte[0], attributes);
        testRunner.run();
        List<MockFlowFile> notFound = testRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        List<MockFlowFile> found = testRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertEquals(0, notFound.size());
        Assert.assertEquals(1, found.size());
        FlowFile finishedFound = found.get(0);
        Assert.assertNotNull(finishedFound.getAttribute("ip0.isp.lookup.micros"));
        Assert.assertEquals("Apache NiFi - Test ISP", finishedFound.getAttribute("ip0.isp.name"));
        Assert.assertEquals("Apache NiFi - Test Organization", finishedFound.getAttribute("ip0.isp.organization"));
        Assert.assertEquals("1337", finishedFound.getAttribute("ip0.isp.asn"));
        Assert.assertEquals("Apache NiFi - Test Chocolate", finishedFound.getAttribute("ip0.isp.asn.organization"));
    }

    @Test
    public void shouldFlowToNotFoundWhenNullResponseFromMaxMind() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "ip");
        Mockito.when(databaseReader.isp(InetAddress.getByName("1.2.3.4"))).thenReturn(null);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("ip", "1.2.3.4");
        testRunner.enqueue(new byte[0], attributes);
        testRunner.run();
        List<MockFlowFile> notFound = testRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        List<MockFlowFile> found = testRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertEquals(1, notFound.size());
        Assert.assertEquals(0, found.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFlowToNotFoundWhenIOExceptionThrownFromMaxMind() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "ip");
        Mockito.when(databaseReader.isp(InetAddress.getByName("1.2.3.4"))).thenThrow(IOException.class);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("ip", "1.2.3.4");
        testRunner.enqueue(new byte[0], attributes);
        testRunner.run();
        List<MockFlowFile> notFound = testRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        List<MockFlowFile> found = testRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertEquals(1, notFound.size());
        Assert.assertEquals(0, found.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFlowToNotFoundWhenExceptionThrownFromMaxMind() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "ip");
        Mockito.when(databaseReader.isp(InetAddress.getByName("1.2.3.4"))).thenThrow(IOException.class);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("ip", "1.2.3.4");
        testRunner.enqueue(new byte[0], attributes);
        testRunner.run();
        List<MockFlowFile> notFound = testRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        List<MockFlowFile> found = testRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertEquals(1, notFound.size());
        Assert.assertEquals(0, found.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void whenInetAddressThrowsUnknownHostFlowFileShouldBeSentToNotFound() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "ip");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("ip", "somenonexistentdomain.comm");
        Mockito.when(InetAddress.getByName("somenonexistentdomain.comm")).thenThrow(UnknownHostException.class);
        testRunner.enqueue(new byte[0], attributes);
        testRunner.run();
        List<MockFlowFile> notFound = testRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        List<MockFlowFile> found = testRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertEquals(1, notFound.size());
        Assert.assertEquals(0, found.size());
        Mockito.verify(databaseReader).close();
        Mockito.verifyNoMoreInteractions(databaseReader);
    }

    class TestableIspEnrichIP extends ISPEnrichIP {
        @OnScheduled
        @Override
        public void onScheduled(ProcessContext context) throws IOException {
            databaseReaderRef.set(databaseReader);
        }
    }
}

