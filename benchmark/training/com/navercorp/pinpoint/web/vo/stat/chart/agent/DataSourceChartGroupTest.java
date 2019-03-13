/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.vo.stat.chart.agent;


import StatChartGroup.ChartType;
import com.navercorp.pinpoint.common.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.web.mapper.stat.sampling.sampler.DataSourceSampler;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.chart.Chart;
import com.navercorp.pinpoint.web.vo.chart.Point;
import com.navercorp.pinpoint.web.vo.stat.SampledDataSource;
import com.navercorp.pinpoint.web.vo.stat.chart.StatChartGroup;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Taejin Koo
 */
public class DataSourceChartGroupTest {
    private static final int MIN_VALUE_OF_MAX_CONNECTION_SIZE = 20;

    private static final int CREATE_TEST_OBJECT_MAX_SIZE = 10;

    private final DataSourceSampler sampler = new DataSourceSampler();

    @Mock
    private ServiceTypeRegistryService serviceTypeRegistryService;

    @Test
    public void basicFunctionTest1() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        TimeWindow timeWindow = new TimeWindow(new Range((currentTimeMillis - 300000), currentTimeMillis));
        List<SampledDataSource> sampledDataSourceList = createSampledDataSourceList(timeWindow);
        StatChartGroup dataSourceChartGroup = DataSourceChart.newDataSourceChartGroup(timeWindow, sampledDataSourceList, serviceTypeRegistryService);
        assertEquals(sampledDataSourceList, dataSourceChartGroup);
    }

    @Test
    public void basicFunctionTest2() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        TimeWindow timeWindow = new TimeWindow(new Range((currentTimeMillis - 300000), currentTimeMillis));
        List<SampledDataSource> sampledDataSourceList = Collections.emptyList();
        DataSourceChart dataSourceChartGroup = new DataSourceChart(timeWindow, sampledDataSourceList, serviceTypeRegistryService);
        Assert.assertEquals((-1), dataSourceChartGroup.getId());
        Assert.assertEquals(null, dataSourceChartGroup.getJdbcUrl());
        Assert.assertEquals(null, dataSourceChartGroup.getDatabaseName());
        Assert.assertEquals(null, dataSourceChartGroup.getServiceType());
        Map<StatChartGroup.ChartType, Chart<? extends Point>> charts = dataSourceChartGroup.getCharts().getCharts();
        Assert.assertEquals(2, charts.size());
        for (Chart<? extends Point> chart : charts.values()) {
            Assert.assertTrue(CollectionUtils.isEmpty(chart.getPoints()));
        }
    }
}

