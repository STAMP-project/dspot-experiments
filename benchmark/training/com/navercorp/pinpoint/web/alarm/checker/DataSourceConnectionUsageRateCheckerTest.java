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
package com.navercorp.pinpoint.web.alarm.checker;


import CheckerCategory.ERROR_COUNT;
import DataCollectorFactory.DataCollectorCategory;
import com.navercorp.pinpoint.common.server.bo.stat.DataSourceListBo;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.web.alarm.collector.DataSourceDataCollector;
import com.navercorp.pinpoint.web.alarm.vo.Rule;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.dao.stat.AgentStatDao;
import com.navercorp.pinpoint.web.vo.Application;
import java.util.List;
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
public class DataSourceConnectionUsageRateCheckerTest {
    private static final String APPLICATION_NAME = "local_service";

    private static final String AGENT_ID = "local_tomcat";

    private static final String SERVICE_TYPE = "tomcat";

    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    private static final long INTERVAL_MILLIS = 300000;

    private static final long START_TIME_MILLIS = (DataSourceConnectionUsageRateCheckerTest.CURRENT_TIME_MILLIS) - (DataSourceConnectionUsageRateCheckerTest.INTERVAL_MILLIS);

    private static final long TIMESTAMP_INTERVAL = 5000L;

    @Mock
    private AgentStatDao<DataSourceListBo> mockDataSourceDao;

    @Mock
    private ApplicationIndexDao mockApplicationIndexDao;

    @Test
    public void checkTest1() {
        Rule rule = new Rule(DataSourceConnectionUsageRateCheckerTest.APPLICATION_NAME, DataSourceConnectionUsageRateCheckerTest.SERVICE_TYPE, ERROR_COUNT.getName(), 50, "testGroup", false, false, "");
        Application application = new Application(DataSourceConnectionUsageRateCheckerTest.APPLICATION_NAME, ServiceType.STAND_ALONE);
        DataSourceDataCollector collector = new DataSourceDataCollector(DataCollectorCategory.DATA_SOURCE_STAT, application, mockDataSourceDao, mockApplicationIndexDao, DataSourceConnectionUsageRateCheckerTest.CURRENT_TIME_MILLIS, DataSourceConnectionUsageRateCheckerTest.INTERVAL_MILLIS);
        DataSourceConnectionUsageRateChecker checker = new DataSourceConnectionUsageRateChecker(collector, rule);
        checker.check();
        Assert.assertTrue(checker.isDetected());
        String emailMessage = checker.getEmailMessage();
        Assert.assertTrue(StringUtils.hasLength(emailMessage));
        List<String> smsMessage = checker.getSmsMessage();
        Assert.assertTrue(((smsMessage.size()) == 2));
    }

    @Test
    public void checkTest2() {
        Rule rule = new Rule(DataSourceConnectionUsageRateCheckerTest.APPLICATION_NAME, DataSourceConnectionUsageRateCheckerTest.SERVICE_TYPE, ERROR_COUNT.getName(), 80, "testGroup", false, false, "");
        Application application = new Application(DataSourceConnectionUsageRateCheckerTest.APPLICATION_NAME, ServiceType.STAND_ALONE);
        DataSourceDataCollector collector = new DataSourceDataCollector(DataCollectorCategory.DATA_SOURCE_STAT, application, mockDataSourceDao, mockApplicationIndexDao, DataSourceConnectionUsageRateCheckerTest.CURRENT_TIME_MILLIS, DataSourceConnectionUsageRateCheckerTest.INTERVAL_MILLIS);
        DataSourceConnectionUsageRateChecker checker = new DataSourceConnectionUsageRateChecker(collector, rule);
        checker.check();
        Assert.assertFalse(checker.isDetected());
        String emailMessage = checker.getEmailMessage();
        Assert.assertTrue(StringUtils.isEmpty(emailMessage));
        List<String> smsMessage = checker.getSmsMessage();
        Assert.assertTrue(CollectionUtils.isEmpty(smsMessage));
    }
}

