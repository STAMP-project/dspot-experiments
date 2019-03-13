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
package com.navercorp.pinpoint.web.view;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.web.mapper.stat.sampling.sampler.DataSourceSampler;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.stat.SampledDataSource;
import com.navercorp.pinpoint.web.vo.stat.chart.agent.DataSourceChart;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Taejin Koo
 */
@RunWith(MockitoJUnitRunner.class)
public class DataSourceChartSerializerTest {
    private static final int MIN_VALUE_OF_MAX_CONNECTION_SIZE = 20;

    private static final int CREATE_TEST_OBJECT_MAX_SIZE = 10;

    private final DataSourceSampler sampler = new DataSourceSampler();

    private ObjectMapper mapper = new ObjectMapper();

    @Mock
    private ServiceTypeRegistryService serviceTypeRegistryService;

    @Test
    public void serializeTest() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        TimeWindow timeWindow = new TimeWindow(new Range((currentTimeMillis - 300000), currentTimeMillis));
        List<SampledDataSource> sampledDataSourceList = createSampledDataSourceList(timeWindow);
        DataSourceChart dataSourceChartGroup = new DataSourceChart(timeWindow, sampledDataSourceList, serviceTypeRegistryService);
        String jsonValue = mapper.writeValueAsString(dataSourceChartGroup);
        Map map = mapper.readValue(jsonValue, Map.class);
        Assert.assertTrue(map.containsKey("id"));
        Assert.assertTrue(map.containsKey("jdbcUrl"));
        Assert.assertTrue(map.containsKey("databaseName"));
        Assert.assertTrue(map.containsKey("serviceType"));
        Assert.assertTrue(map.containsKey("charts"));
    }
}

