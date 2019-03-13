/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.web.service;


import Filter.NONE;
import ResponseTimeViewModel.TimeCount;
import com.navercorp.pinpoint.common.server.bo.SpanBo;
import com.navercorp.pinpoint.common.server.bo.SpanEventBo;
import com.navercorp.pinpoint.common.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.common.trace.HistogramSchema;
import com.navercorp.pinpoint.web.TestTraceUtils;
import com.navercorp.pinpoint.web.applicationmap.ApplicationMap;
import com.navercorp.pinpoint.web.applicationmap.ApplicationMapBuilderFactory;
import com.navercorp.pinpoint.web.applicationmap.appender.histogram.NodeHistogramAppenderFactory;
import com.navercorp.pinpoint.web.applicationmap.appender.server.ServerInfoAppenderFactory;
import com.navercorp.pinpoint.web.applicationmap.histogram.Histogram;
import com.navercorp.pinpoint.web.applicationmap.histogram.NodeHistogram;
import com.navercorp.pinpoint.web.applicationmap.link.Link;
import com.navercorp.pinpoint.web.applicationmap.nodes.Node;
import com.navercorp.pinpoint.web.dao.ApplicationTraceIndexDao;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.util.TimeWindowDownSampler;
import com.navercorp.pinpoint.web.view.AgentResponseTimeViewModelList;
import com.navercorp.pinpoint.web.view.ResponseTimeViewModel;
import com.navercorp.pinpoint.web.vo.Application;
import com.navercorp.pinpoint.web.vo.Range;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author HyunGil Jeong
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class FilteredMapServiceImplTest {
    private static final Random RANDOM = new Random();

    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    @Mock
    private AgentInfoService agentInfoService;

    @Mock
    private TraceDao traceDao;

    @Mock
    private ApplicationTraceIndexDao applicationTraceIndexDao;

    @Mock
    private ApplicationFactory applicationFactory;

    // Mocked
    private ServiceTypeRegistryService registry = TestTraceUtils.mockServiceTypeRegistryService();

    @Spy
    private ApplicationMapBuilderFactory applicationMapBuilderFactory = new ApplicationMapBuilderFactory(new NodeHistogramAppenderFactory(executor), new ServerInfoAppenderFactory(executor));

    @InjectMocks
    private FilteredMapService filteredMapService = new FilteredMapServiceImpl();

    /**
     * USER -> ROOT_APP -> APP_A -> CACHE
     */
    @Test
    public void twoTier() {
        // Given
        Range originalRange = new Range(1000, 2000);
        Range scanRange = new Range(1000, 2000);
        final TimeWindow timeWindow = new TimeWindow(originalRange, TimeWindowDownSampler.SAMPLER);
        // root app span
        long rootSpanId = FilteredMapServiceImplTest.RANDOM.nextLong();
        long rootSpanStartTime = 1000L;
        long rootSpanCollectorAcceptTime = 1210L;
        int rootSpanElapsed = 200;
        SpanBo rootSpan = new TestTraceUtils.SpanBuilder("ROOT_APP", "root-agent").spanId(rootSpanId).startTime(rootSpanStartTime).collectorAcceptTime(rootSpanCollectorAcceptTime).elapsed(rootSpanElapsed).build();
        // app A span
        long appASpanId = FilteredMapServiceImplTest.RANDOM.nextLong();
        long appASpanStartTime = 1020L;
        long appASpanCollectorAcceptTime = 1090L;
        int appASpanElapsed = 160;
        SpanBo appASpan = new TestTraceUtils.SpanBuilder("APP_A", "app-a").spanId(appASpanId).parentSpan(rootSpan).startTime(appASpanStartTime).collectorAcceptTime(appASpanCollectorAcceptTime).elapsed(appASpanElapsed).build();
        // root app -> app A rpc span event
        SpanEventBo rootRpcSpanEvent = new TestTraceUtils.RpcSpanEventBuilder("www.foo.com/bar", 10, 190).nextSpanId(appASpanId).build();
        rootSpan.addSpanEvent(rootRpcSpanEvent);
        // app A -> cache span event
        int cacheStartElapsed = 20;
        int cacheEndElapsed = 130;
        SpanEventBo appACacheSpanEvent = new TestTraceUtils.CacheSpanEventBuilder("CacheName", "1.1.1.1", cacheStartElapsed, cacheEndElapsed).build();
        appASpan.addSpanEvent(appACacheSpanEvent);
        Mockito.when(traceDao.selectAllSpans(ArgumentMatchers.anyList())).thenReturn(Collections.singletonList(Arrays.asList(rootSpan, appASpan)));
        // When
        ApplicationMap applicationMap = filteredMapService.selectApplicationMapWithScatterData(Collections.emptyList(), originalRange, scanRange, 1, 1, NONE, 0);
        // Then
        Collection<Node> nodes = applicationMap.getNodes();
        Assert.assertEquals(4, nodes.size());
        for (Node node : nodes) {
            Application application = node.getApplication();
            if ((application.getName().equals("ROOT_APP")) && ((application.getServiceType().getCode()) == (TestTraceUtils.USER_TYPE_CODE))) {
                // USER node
                NodeHistogram nodeHistogram = node.getNodeHistogram();
                // histogram
                Histogram applicationHistogram = nodeHistogram.getApplicationHistogram();
                assertHistogram(applicationHistogram, 1, 0, 0, 0, 0);
                Map<String, Histogram> agentHistogramMap = nodeHistogram.getAgentHistogramMap();
                Assert.assertTrue(agentHistogramMap.isEmpty());
                // time histogram
                HistogramSchema histogramSchema = node.getServiceType().getHistogramSchema();
                List<ResponseTimeViewModel.TimeCount> expectedTimeCounts = Collections.singletonList(new ResponseTimeViewModel.TimeCount(timeWindow.refineTimestamp(rootSpanCollectorAcceptTime), 1));
                List<ResponseTimeViewModel> applicationTimeHistogram = nodeHistogram.getApplicationTimeHistogram();
                assertTimeHistogram(applicationTimeHistogram, histogramSchema.getFastSlot(), expectedTimeCounts);
                AgentResponseTimeViewModelList agentTimeHistogram = nodeHistogram.getAgentTimeHistogram();
                Assert.assertTrue(agentTimeHistogram.getAgentResponseTimeViewModelList().isEmpty());
            } else
                if ((application.getName().equals("ROOT_APP")) && ((application.getServiceType().getCode()) == (TestTraceUtils.TEST_STAND_ALONE_TYPE_CODE))) {
                    // ROOT_APP node
                    NodeHistogram nodeHistogram = node.getNodeHistogram();
                    // histogram
                    Histogram applicationHistogram = nodeHistogram.getApplicationHistogram();
                    assertHistogram(applicationHistogram, 1, 0, 0, 0, 0);
                    Map<String, Histogram> agentHistogramMap = nodeHistogram.getAgentHistogramMap();
                    assertAgentHistogram(agentHistogramMap, "root-agent", 1, 0, 0, 0, 0);
                    // time histogram
                    HistogramSchema histogramSchema = node.getServiceType().getHistogramSchema();
                    List<ResponseTimeViewModel.TimeCount> expectedTimeCounts = Collections.singletonList(new ResponseTimeViewModel.TimeCount(timeWindow.refineTimestamp(rootSpanCollectorAcceptTime), 1));
                    List<ResponseTimeViewModel> applicationTimeHistogram = nodeHistogram.getApplicationTimeHistogram();
                    assertTimeHistogram(applicationTimeHistogram, histogramSchema.getFastSlot(), expectedTimeCounts);
                    AgentResponseTimeViewModelList agentTimeHistogram = nodeHistogram.getAgentTimeHistogram();
                    assertAgentTimeHistogram(agentTimeHistogram.getAgentResponseTimeViewModelList(), "root-agent", histogramSchema.getFastSlot(), expectedTimeCounts);
                } else
                    if ((application.getName().equals("APP_A")) && ((application.getServiceType().getCode()) == (TestTraceUtils.TEST_STAND_ALONE_TYPE_CODE))) {
                        // APP_A node
                        NodeHistogram nodeHistogram = node.getNodeHistogram();
                        // histogram
                        Histogram applicationHistogram = nodeHistogram.getApplicationHistogram();
                        assertHistogram(applicationHistogram, 1, 0, 0, 0, 0);
                        Map<String, Histogram> agentHistogramMap = nodeHistogram.getAgentHistogramMap();
                        assertAgentHistogram(agentHistogramMap, "app-a", 1, 0, 0, 0, 0);
                        // time histogram
                        HistogramSchema histogramSchema = node.getServiceType().getHistogramSchema();
                        List<ResponseTimeViewModel.TimeCount> expectedTimeCounts = Collections.singletonList(new ResponseTimeViewModel.TimeCount(timeWindow.refineTimestamp(appASpanCollectorAcceptTime), 1));
                        List<ResponseTimeViewModel> applicationTimeHistogram = nodeHistogram.getApplicationTimeHistogram();
                        assertTimeHistogram(applicationTimeHistogram, histogramSchema.getFastSlot(), expectedTimeCounts);
                        AgentResponseTimeViewModelList agentTimeHistogram = nodeHistogram.getAgentTimeHistogram();
                        assertAgentTimeHistogram(agentTimeHistogram.getAgentResponseTimeViewModelList(), "app-a", histogramSchema.getFastSlot(), expectedTimeCounts);
                    } else
                        if ((application.getName().equals("CacheName")) && ((application.getServiceType().getCode()) == (TestTraceUtils.CACHE_TYPE_CODE))) {
                            // CACHE node
                            NodeHistogram nodeHistogram = node.getNodeHistogram();
                            // histogram
                            Histogram applicationHistogram = nodeHistogram.getApplicationHistogram();
                            assertHistogram(applicationHistogram, 0, 1, 0, 0, 0);
                            Map<String, Histogram> agentHistogramMap = nodeHistogram.getAgentHistogramMap();
                            assertAgentHistogram(agentHistogramMap, "1.1.1.1", 0, 1, 0, 0, 0);
                            // time histogram
                            HistogramSchema histogramSchema = node.getServiceType().getHistogramSchema();
                            List<ResponseTimeViewModel.TimeCount> expectedTimeCounts = Collections.singletonList(new ResponseTimeViewModel.TimeCount(timeWindow.refineTimestamp((appASpanStartTime + cacheStartElapsed)), 1));
                            List<ResponseTimeViewModel> applicationTimeHistogram = nodeHistogram.getApplicationTimeHistogram();
                            assertTimeHistogram(applicationTimeHistogram, histogramSchema.getNormalSlot(), expectedTimeCounts);
                            AgentResponseTimeViewModelList agentTimeHistogram = nodeHistogram.getAgentTimeHistogram();
                            assertAgentTimeHistogram(agentTimeHistogram.getAgentResponseTimeViewModelList(), "1.1.1.1", histogramSchema.getNormalSlot(), expectedTimeCounts);
                        } else {
                            Assert.fail(("Unexpected node : " + node));
                        }



        }
        Collection<Link> links = applicationMap.getLinks();
        Assert.assertEquals(3, links.size());
        for (Link link : links) {
            Application fromApplication = link.getFrom().getApplication();
            Application toApplication = link.getTo().getApplication();
            if (((fromApplication.getName().equals("ROOT_APP")) && ((fromApplication.getServiceType().getCode()) == (TestTraceUtils.USER_TYPE_CODE))) && ((toApplication.getName().equals("ROOT_APP")) && ((toApplication.getServiceType().getCode()) == (TestTraceUtils.TEST_STAND_ALONE_TYPE_CODE)))) {
                // histogram
                Histogram histogram = link.getHistogram();
                assertHistogram(histogram, 1, 0, 0, 0, 0);
                // time histogram
                List<ResponseTimeViewModel> linkApplicationTimeSeriesHistogram = link.getLinkApplicationTimeSeriesHistogram();
                HistogramSchema targetHistogramSchema = toApplication.getServiceType().getHistogramSchema();
                List<ResponseTimeViewModel.TimeCount> expectedTimeCounts = Collections.singletonList(new ResponseTimeViewModel.TimeCount(timeWindow.refineTimestamp(rootSpanCollectorAcceptTime), 1));
                assertTimeHistogram(linkApplicationTimeSeriesHistogram, targetHistogramSchema.getFastSlot(), expectedTimeCounts);
            } else
                if (((fromApplication.getName().equals("ROOT_APP")) && ((fromApplication.getServiceType().getCode()) == (TestTraceUtils.TEST_STAND_ALONE_TYPE_CODE))) && ((toApplication.getName().equals("APP_A")) && ((toApplication.getServiceType().getCode()) == (TestTraceUtils.TEST_STAND_ALONE_TYPE_CODE)))) {
                    // histogram
                    Histogram histogram = link.getHistogram();
                    assertHistogram(histogram, 1, 0, 0, 0, 0);
                    // time histogram
                    List<ResponseTimeViewModel> linkApplicationTimeSeriesHistogram = link.getLinkApplicationTimeSeriesHistogram();
                    HistogramSchema targetHistogramSchema = toApplication.getServiceType().getHistogramSchema();
                    List<ResponseTimeViewModel.TimeCount> expectedTimeCounts = Collections.singletonList(new ResponseTimeViewModel.TimeCount(timeWindow.refineTimestamp(appASpanCollectorAcceptTime), 1));
                    assertTimeHistogram(linkApplicationTimeSeriesHistogram, targetHistogramSchema.getFastSlot(), expectedTimeCounts);
                } else
                    if (((fromApplication.getName().equals("APP_A")) && ((fromApplication.getServiceType().getCode()) == (TestTraceUtils.TEST_STAND_ALONE_TYPE_CODE))) && ((toApplication.getName().equals("CacheName")) && ((toApplication.getServiceType().getCode()) == (TestTraceUtils.CACHE_TYPE_CODE)))) {
                        // histogram
                        Histogram histogram = link.getHistogram();
                        assertHistogram(histogram, 0, 1, 0, 0, 0);
                        // time histogram
                        List<ResponseTimeViewModel> linkApplicationTimeSeriesHistogram = link.getLinkApplicationTimeSeriesHistogram();
                        HistogramSchema targetHistogramSchema = toApplication.getServiceType().getHistogramSchema();
                        List<ResponseTimeViewModel.TimeCount> expectedTimeCounts = Collections.singletonList(new ResponseTimeViewModel.TimeCount(timeWindow.refineTimestamp((appASpanStartTime + cacheStartElapsed)), 1));
                        assertTimeHistogram(linkApplicationTimeSeriesHistogram, targetHistogramSchema.getNormalSlot(), expectedTimeCounts);
                    } else {
                        Assert.fail(("Unexpected link : " + link));
                    }


        }
    }
}

