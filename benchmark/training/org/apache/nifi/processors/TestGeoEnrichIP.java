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


import GeoEnrichIP.GEO_DATABASE_FILE;
import GeoEnrichIP.IP_ADDRESS_ATTRIBUTE;
import GeoEnrichIP.REL_FOUND;
import GeoEnrichIP.REL_NOT_FOUND;
import com.maxmind.geoip2.model.CityResponse;
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
@PrepareForTest({ GeoEnrichIP.class })
@SuppressWarnings("WeakerAccess")
public class TestGeoEnrichIP {
    DatabaseReader databaseReader;

    GeoEnrichIP geoEnrichIP;

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
        Mockito.verify(databaseReader).city(InetAddress.getByName(null));
    }

    @Test
    public void successfulMaxMindResponseShouldFlowToFoundRelationship() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "ip");
        final CityResponse cityResponse = getFullCityResponse();
        Mockito.when(databaseReader.city(InetAddress.getByName("1.2.3.4"))).thenReturn(cityResponse);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("ip", "1.2.3.4");
        testRunner.enqueue(new byte[0], attributes);
        testRunner.run();
        List<MockFlowFile> notFound = testRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        List<MockFlowFile> found = testRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertEquals(0, notFound.size());
        Assert.assertEquals(1, found.size());
        FlowFile finishedFound = found.get(0);
        Assert.assertNotNull(finishedFound.getAttribute("ip.geo.lookup.micros"));
        Assert.assertEquals("Minneapolis", finishedFound.getAttribute("ip.geo.city"));
        Assert.assertEquals("44.98", finishedFound.getAttribute("ip.geo.latitude"));
        Assert.assertEquals("93.2636", finishedFound.getAttribute("ip.geo.longitude"));
        Assert.assertEquals("Minnesota", finishedFound.getAttribute("ip.geo.subdivision.0"));
        Assert.assertEquals("MN", finishedFound.getAttribute("ip.geo.subdivision.isocode.0"));
        Assert.assertNull(finishedFound.getAttribute("ip.geo.subdivision.1"));
        Assert.assertEquals("TT", finishedFound.getAttribute("ip.geo.subdivision.isocode.1"));
        Assert.assertEquals("United States of America", finishedFound.getAttribute("ip.geo.country"));
        Assert.assertEquals("US", finishedFound.getAttribute("ip.geo.country.isocode"));
        Assert.assertEquals("55401", finishedFound.getAttribute("ip.geo.postalcode"));
    }

    @Test
    public void successfulMaxMindResponseShouldFlowToFoundRelationshipWhenLatAndLongAreNotSet() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "ip");
        final CityResponse cityResponse = getNullLatAndLongCityResponse();
        Mockito.when(databaseReader.city(InetAddress.getByName("1.2.3.4"))).thenReturn(cityResponse);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("ip", "1.2.3.4");
        testRunner.enqueue(new byte[0], attributes);
        testRunner.run();
        List<MockFlowFile> notFound = testRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        List<MockFlowFile> found = testRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertEquals(0, notFound.size());
        Assert.assertEquals(1, found.size());
        FlowFile finishedFound = found.get(0);
        Assert.assertNotNull(finishedFound.getAttribute("ip.geo.lookup.micros"));
        Assert.assertEquals("Minneapolis", finishedFound.getAttribute("ip.geo.city"));
        Assert.assertNull(finishedFound.getAttribute("ip.geo.latitude"));
        Assert.assertNull(finishedFound.getAttribute("ip.geo.longitude"));
        Assert.assertEquals("Minnesota", finishedFound.getAttribute("ip.geo.subdivision.0"));
        Assert.assertEquals("MN", finishedFound.getAttribute("ip.geo.subdivision.isocode.0"));
        Assert.assertNull(finishedFound.getAttribute("ip.geo.subdivision.1"));
        Assert.assertEquals("TT", finishedFound.getAttribute("ip.geo.subdivision.isocode.1"));
        Assert.assertEquals("United States of America", finishedFound.getAttribute("ip.geo.country"));
        Assert.assertEquals("US", finishedFound.getAttribute("ip.geo.country.isocode"));
        Assert.assertEquals("55401", finishedFound.getAttribute("ip.geo.postalcode"));
    }

    @Test
    public void evaluatingExpressionLanguageShouldAndFindingIpFieldWithSuccessfulLookUpShouldFlowToFoundRelationship() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "${ip.fields:substringBefore(',')}");
        final CityResponse cityResponse = getNullLatAndLongCityResponse();
        Mockito.when(databaseReader.city(InetAddress.getByName("1.2.3.4"))).thenReturn(cityResponse);
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
        Assert.assertNotNull(finishedFound.getAttribute("ip0.geo.lookup.micros"));
        Assert.assertEquals("Minneapolis", finishedFound.getAttribute("ip0.geo.city"));
        Assert.assertNull(finishedFound.getAttribute("ip0.geo.latitude"));
        Assert.assertNull(finishedFound.getAttribute("ip0.geo.longitude"));
        Assert.assertEquals("Minnesota", finishedFound.getAttribute("ip0.geo.subdivision.0"));
        Assert.assertEquals("MN", finishedFound.getAttribute("ip0.geo.subdivision.isocode.0"));
        Assert.assertNull(finishedFound.getAttribute("ip0.geo.subdivision.1"));
        Assert.assertEquals("TT", finishedFound.getAttribute("ip0.geo.subdivision.isocode.1"));
        Assert.assertEquals("United States of America", finishedFound.getAttribute("ip0.geo.country"));
        Assert.assertEquals("US", finishedFound.getAttribute("ip0.geo.country.isocode"));
        Assert.assertEquals("55401", finishedFound.getAttribute("ip0.geo.postalcode"));
    }

    @Test
    public void shouldFlowToNotFoundWhenNullResponseFromMaxMind() throws Exception {
        testRunner.setProperty(GEO_DATABASE_FILE, "./");
        testRunner.setProperty(IP_ADDRESS_ATTRIBUTE, "ip");
        Mockito.when(databaseReader.city(InetAddress.getByName("1.2.3.4"))).thenReturn(null);
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
        Mockito.when(databaseReader.city(InetAddress.getByName("1.2.3.4"))).thenThrow(IOException.class);
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
        Mockito.when(databaseReader.city(InetAddress.getByName("1.2.3.4"))).thenThrow(IOException.class);
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

    class TestableGeoEnrichIP extends GeoEnrichIP {
        @OnScheduled
        @Override
        public void onScheduled(ProcessContext context) throws IOException {
            databaseReaderRef.set(databaseReader);
        }
    }
}

