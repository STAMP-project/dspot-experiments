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
package com.navercorp.pinpoint.profiler.monitor.metric.datasource;


import com.navercorp.pinpoint.bootstrap.plugin.monitor.DataSourceMonitor;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.profiler.context.monitor.DataSourceMonitorRegistryService;
import com.navercorp.pinpoint.profiler.context.monitor.DefaultDataSourceMonitorRegistryService;
import com.navercorp.pinpoint.profiler.context.monitor.JdbcUrlParsingService;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Taejin Koo
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultDataSourceMetricTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Mock
    private JdbcUrlParsingService jdbcUrlParsingService;

    @Test
    public void collectTest() {
        int createMockObjectSize = 10;
        DataSourceMonitorRegistryService dataSourceMonitorRegistryService = new DefaultDataSourceMonitorRegistryService(createMockObjectSize);
        DefaultDataSourceMetricTest.MockDataSourceMonitor[] mockDataSourceMonitors = createMockDataSourceMonitor(dataSourceMonitorRegistryService, dataSourceMonitorRegistryService.getRemainingIdNumber());
        logger.debug("JdbcUrlParsingService:{}", jdbcUrlParsingService);
        DataSourceMetric dataSourceMetric = new DefaultDataSourceMetric(dataSourceMonitorRegistryService, jdbcUrlParsingService);
        List<DataSource> collect = dataSourceMetric.dataSourceList();
        assertIdIsUnique(collect);
        for (DefaultDataSourceMetricTest.MockDataSourceMonitor dataSourceMonitor : mockDataSourceMonitors) {
            assertContainsAndEquals(dataSourceMonitor, collect);
        }
    }

    private static class MockDataSourceMonitor implements DataSourceMonitor {
        private static final Random RANDOM = new Random(System.currentTimeMillis());

        private static final int MIN_VALUE_OF_MAX_CONNECTION_SIZE = 20;

        private static final ServiceType[] SERVICE_TYPE_LIST = new ServiceType[]{ ServiceType.UNKNOWN, ServiceType.UNDEFINED, ServiceType.TEST };

        private final int id;

        private final ServiceType serviceType;

        private final int activeConnectionSize;

        private final int maxConnectionSize;

        private boolean closed = false;

        public MockDataSourceMonitor(int index) {
            this.id = index;
            this.serviceType = DefaultDataSourceMetricTest.MockDataSourceMonitor.SERVICE_TYPE_LIST[RandomUtils.nextInt(0, DefaultDataSourceMetricTest.MockDataSourceMonitor.SERVICE_TYPE_LIST.length)];
            this.maxConnectionSize = RandomUtils.nextInt(DefaultDataSourceMetricTest.MockDataSourceMonitor.MIN_VALUE_OF_MAX_CONNECTION_SIZE, ((DefaultDataSourceMetricTest.MockDataSourceMonitor.MIN_VALUE_OF_MAX_CONNECTION_SIZE) * 2));
            this.activeConnectionSize = RandomUtils.nextInt(0, ((maxConnectionSize) + 1));
        }

        @Override
        public String getUrl() {
            return "url" + (id);
        }

        @Override
        public int getActiveConnectionSize() {
            return activeConnectionSize;
        }

        @Override
        public int getMaxConnectionSize() {
            return maxConnectionSize;
        }

        @Override
        public ServiceType getServiceType() {
            return serviceType;
        }

        @Override
        public boolean isDisabled() {
            return closed;
        }

        public void close() {
            closed = true;
        }
    }
}

