/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import org.apache.druid.client.FilteredServerInventoryView;
import org.apache.druid.client.TimelineServerView;
import org.apache.druid.query.metadata.SegmentMetadataQueryConfig;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.junit.Assert;
import org.junit.Test;


public class ClientInfoResourceTest {
    private static final String KEY_DIMENSIONS = "dimensions";

    private static final String KEY_METRICS = "metrics";

    private static final DateTime FIXED_TEST_TIME = new DateTime(2015, 9, 14, 0, 0, ISOChronology.getInstanceUTC());/* always use the same current time for unit tests */


    private final String dataSource = "test-data-source";

    private FilteredServerInventoryView serverInventoryView;

    private TimelineServerView timelineServerView;

    private ClientInfoResource resource;

    @Test
    public void testGetDatasourceNonFullWithInterval() {
        Map<String, Object> actual = resource.getDatasource(dataSource, "1975/2015", null);
        Map<String, Object> expected = ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1", "d2"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1", "m2"));
        EasyMock.verify(serverInventoryView);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDatasourceFullWithInterval() {
        Map<String, Object> actual = resource.getDatasource(dataSource, "1975/2015", "true");
        Map<String, Object> expected = ImmutableMap.of("2014-02-13T00:00:00.000Z/2014-02-15T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1")), "2014-02-16T00:00:00.000Z/2014-02-17T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1")), "2014-02-17T00:00:00.000Z/2014-02-18T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d2"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m2")));
        EasyMock.verify(serverInventoryView, timelineServerView);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDatasourceFullWithSmallInterval() {
        Map<String, Object> actual = resource.getDatasource(dataSource, "2014-02-13T09:00:00.000Z/2014-02-17T23:00:00.000Z", "true");
        Map<String, Object> expected = ImmutableMap.of("2014-02-13T09:00:00.000Z/2014-02-15T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1")), "2014-02-16T00:00:00.000Z/2014-02-17T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1")), "2014-02-17T00:00:00.000Z/2014-02-17T23:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d2"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m2")));
        EasyMock.verify(serverInventoryView, timelineServerView);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDatasourceWithDefaultInterval() {
        Map<String, Object> actual = resource.getDatasource(dataSource, null, null);
        Map<String, Object> expected = ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of(), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDatasourceWithConfiguredDefaultInterval() {
        ClientInfoResource defaultResource = getResourceTestHelper(serverInventoryView, timelineServerView, new SegmentMetadataQueryConfig("P100Y"));
        Map<String, Object> expected = ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1", "d2", "d3", "d4", "d5"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1", "m2", "m3", "m4", "m5"));
        Map<String, Object> actual = defaultResource.getDatasource(dataSource, null, null);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDatasourceFullWithOvershadowedSegments1() {
        Map<String, Object> actual = resource.getDatasource(dataSource, "2015-02-02T09:00:00.000Z/2015-02-06T23:00:00.000Z", "true");
        Map<String, Object> expected = ImmutableMap.of("2015-02-02T09:00:00.000Z/2015-02-03T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1")), "2015-02-03T00:00:00.000Z/2015-02-05T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1", "d2", "d3"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1", "m2", "m3")), "2015-02-05T00:00:00.000Z/2015-02-06T23:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1")));
        EasyMock.verify(serverInventoryView, timelineServerView);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDatasourceFullWithOvershadowedSegments2() {
        Map<String, Object> actual = resource.getDatasource(dataSource, "2015-02-09T09:00:00.000Z/2015-02-13T23:00:00.000Z", "true");
        Map<String, Object> expected = ImmutableMap.of("2015-02-09T09:00:00.000Z/2015-02-10T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1", "d3"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1", "m3")), "2015-02-10T00:00:00.000Z/2015-02-11T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1")), "2015-02-11T00:00:00.000Z/2015-02-12T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d3"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m3")), "2015-02-12T00:00:00.000Z/2015-02-13T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1")));
        EasyMock.verify(serverInventoryView, timelineServerView);
        Assert.assertEquals(expected, actual);
    }

    /**
     * Though segments within [2015-03-13, 2015-03-19] have different versions, they all abut with each other and have
     * same dimensions/metrics, so they all should be merged together.
     */
    @Test
    public void testGetDatasourceFullWithOvershadowedSegmentsMerged() {
        Map<String, Object> actual = resource.getDatasource(dataSource, "2015-03-13T02:00:00.000Z/2015-03-19T15:00:00.000Z", "true");
        Map<String, Object> expected = ImmutableMap.of("2015-03-13T02:00:00.000Z/2015-03-19T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1")));
        EasyMock.verify(serverInventoryView, timelineServerView);
        Assert.assertEquals(expected, actual);
    }

    /**
     * If "full" is specified, then dimensions/metrics that exist in an incompelte segment should be ingored
     */
    @Test
    public void testGetDatasourceFullWithIncompleteSegment() {
        Map<String, Object> actual = resource.getDatasource(dataSource, "2015-04-03/2015-04-05", "true");
        Map<String, Object> expected = ImmutableMap.of();
        EasyMock.verify(serverInventoryView, timelineServerView);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDatasourceFullWithLargeInterval() {
        Map<String, Object> actual = resource.getDatasource(dataSource, "1975/2050", "true");
        Map<String, Object> expected = ImmutableMap.<String, Object>builder().put("2014-02-13T00:00:00.000Z/2014-02-15T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1"))).put("2014-02-16T00:00:00.000Z/2014-02-17T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1"))).put("2014-02-17T00:00:00.000Z/2014-02-18T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d2"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m2"))).put("2015-02-01T00:00:00.000Z/2015-02-03T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1"))).put("2015-02-03T00:00:00.000Z/2015-02-05T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1", "d2", "d3"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1", "m2", "m3"))).put("2015-02-05T00:00:00.000Z/2015-02-09T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1"))).put("2015-02-09T00:00:00.000Z/2015-02-10T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1", "d3"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1", "m3"))).put("2015-02-10T00:00:00.000Z/2015-02-11T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1"))).put("2015-02-11T00:00:00.000Z/2015-02-12T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d3"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m3"))).put("2015-02-12T00:00:00.000Z/2015-02-13T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1"))).put("2015-03-13T00:00:00.000Z/2015-03-19T00:00:00.000Z", ImmutableMap.of(ClientInfoResourceTest.KEY_DIMENSIONS, ImmutableSet.of("d1"), ClientInfoResourceTest.KEY_METRICS, ImmutableSet.of("m1"))).build();
        EasyMock.verify(serverInventoryView, timelineServerView);
        Assert.assertEquals(expected, actual);
    }
}

