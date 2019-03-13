/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.applicationmap;


import com.navercorp.pinpoint.web.applicationmap.appender.histogram.NodeHistogramFactory;
import com.navercorp.pinpoint.web.applicationmap.appender.histogram.datasource.MapResponseNodeHistogramDataSource;
import com.navercorp.pinpoint.web.applicationmap.appender.histogram.datasource.ResponseHistogramsNodeHistogramDataSource;
import com.navercorp.pinpoint.web.applicationmap.appender.server.ServerInstanceListFactory;
import com.navercorp.pinpoint.web.applicationmap.appender.server.datasource.AgentInfoServerInstanceListDataSource;
import com.navercorp.pinpoint.web.applicationmap.rawdata.LinkDataDuplexMap;
import com.navercorp.pinpoint.web.vo.Application;
import com.navercorp.pinpoint.web.vo.Range;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class ApplicationMapBuilderTest {
    private final ExecutorService serialExecutor = Executors.newSingleThreadExecutor();

    private final ExecutorService parallelExecutor = Executors.newFixedThreadPool(8);

    private MapResponseNodeHistogramDataSource mapResponseNodeHistogramDataSource;

    private ResponseHistogramsNodeHistogramDataSource responseHistogramBuilderNodeHistogramDataSource;

    private AgentInfoServerInstanceListDataSource agentInfoServerInstanceListDataSource;

    @Test
    public void testNoCallData() {
        Range range = new Range(0, 1000);
        Application application = ApplicationMapBuilderTestHelper.createApplicationFromDepth(0);
        ServerInstanceListFactory serverInstanceListFactory = new com.navercorp.pinpoint.web.applicationmap.appender.server.DefaultServerInstanceListFactory(agentInfoServerInstanceListDataSource);
        ApplicationMapBuilder applicationMapBuilder = ApplicationMapBuilderTestHelper.createApplicationMapBuilder(range, serialExecutor);
        ApplicationMapBuilder applicationMapBuilder_parallelAppenders = ApplicationMapBuilderTestHelper.createApplicationMapBuilder(range, parallelExecutor);
        ApplicationMap applicationMap = applicationMapBuilder.includeServerInfo(serverInstanceListFactory).build(application);
        ApplicationMap applicationMap_parallelAppenders = applicationMapBuilder_parallelAppenders.includeServerInfo(serverInstanceListFactory).build(application);
        Assert.assertEquals(1, applicationMap.getNodes().size());
        Assert.assertEquals(1, applicationMap.getNodes().size());
        Assert.assertEquals(1, applicationMap_parallelAppenders.getNodes().size());
        Assert.assertEquals(0, applicationMap.getLinks().size());
        Assert.assertEquals(0, applicationMap.getLinks().size());
        Assert.assertEquals(0, applicationMap_parallelAppenders.getLinks().size());
        ApplicationMapVerifier verifier = new ApplicationMapVerifier(applicationMap);
        verifier.verify(applicationMap);
        verifier.verify(applicationMap_parallelAppenders);
    }

    @Test
    public void testEmptyCallData() {
        Range range = new Range(0, 1000);
        LinkDataDuplexMap linkDataDuplexMap = new LinkDataDuplexMap();
        NodeHistogramFactory nodeHistogramFactory = new com.navercorp.pinpoint.web.applicationmap.appender.histogram.DefaultNodeHistogramFactory(mapResponseNodeHistogramDataSource);
        ServerInstanceListFactory serverInstanceListFactory = new com.navercorp.pinpoint.web.applicationmap.appender.server.DefaultServerInstanceListFactory(agentInfoServerInstanceListDataSource);
        ApplicationMapBuilder applicationMapBuilder = ApplicationMapBuilderTestHelper.createApplicationMapBuilder(range, serialExecutor);
        ApplicationMapBuilder applicationMapBuilder_parallelAppenders = ApplicationMapBuilderTestHelper.createApplicationMapBuilder(range, parallelExecutor);
        ApplicationMap applicationMap = applicationMapBuilder.includeNodeHistogram(nodeHistogramFactory).includeServerInfo(serverInstanceListFactory).build(linkDataDuplexMap);
        ApplicationMap applicationMap_parallelAppenders = applicationMapBuilder_parallelAppenders.includeNodeHistogram(nodeHistogramFactory).includeServerInfo(serverInstanceListFactory).build(linkDataDuplexMap);
        Assert.assertTrue(applicationMap.getNodes().isEmpty());
        Assert.assertTrue(applicationMap.getNodes().isEmpty());
        Assert.assertTrue(applicationMap_parallelAppenders.getNodes().isEmpty());
        Assert.assertTrue(applicationMap.getLinks().isEmpty());
        Assert.assertTrue(applicationMap.getLinks().isEmpty());
        Assert.assertTrue(applicationMap_parallelAppenders.getLinks().isEmpty());
        ApplicationMapVerifier verifier = new ApplicationMapVerifier(applicationMap);
        verifier.verify(applicationMap);
        verifier.verify(applicationMap_parallelAppenders);
    }

    /**
     * USER -> WAS(center) -> UNKNOWN
     */
    @Test
    public void testOneDepth() {
        int depth = 1;
        runTest(depth, depth);
    }

    /**
     * USER -> WAS -> WAS(center) -> WAS -> UNKNOWN
     */
    @Test
    public void testTwoDepth() {
        int depth = 2;
        runTest(depth, depth);
    }

    /**
     * USER -> WAS -> WAS -> WAS(center) -> WAS -> WAS -> UNKNOWN
     */
    @Test
    public void testThreeDepth() {
        int depth = 3;
        runTest(depth, depth);
    }

    /**
     * USER -> WAS(center) -> WAS -> WAS -> UNKNOWN
     */
    @Test
    public void test_1_3_depth() {
        int calleeDepth = 1;
        int callerDepth = 3;
        runTest(calleeDepth, callerDepth);
    }

    /**
     * USER -> WAS -> WAS -> WAS(center) -> UNKNOWN
     */
    @Test
    public void test_3_1_depth() {
        int calleeDepth = 3;
        int callerDepth = 1;
        runTest(calleeDepth, callerDepth);
    }

    /**
     * USER -> WAS -> WAS -> WAS(center) -> WAS -> WAS -> WAS -> UNKNOWN
     */
    @Test
    public void test_3_4_depth() {
        int calleeDepth = 3;
        int callerDepth = 4;
        runTest(calleeDepth, callerDepth);
    }

    /**
     * USER -> WAS -> WAS -> WAS -> WAS(center) -> WAS -> WAS -> UNKNOWN
     */
    @Test
    public void test_4_3_depth() {
        int calleeDepth = 4;
        int callerDepth = 3;
        runTest(calleeDepth, callerDepth);
    }
}

