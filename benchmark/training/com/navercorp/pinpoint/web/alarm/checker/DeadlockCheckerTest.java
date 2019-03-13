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


import AgentEventType.AGENT_CLOSED_BY_SERVER;
import AgentEventType.AGENT_DEADLOCK_DETECTED;
import AgentEventType.AGENT_PING;
import AgentEventType.AGENT_SHUTDOWN;
import CheckerCategory.ERROR_COUNT;
import DataCollectorFactory.DataCollectorCategory;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.web.alarm.collector.AgentEventDataCollector;
import com.navercorp.pinpoint.web.alarm.vo.Rule;
import com.navercorp.pinpoint.web.dao.AgentEventDao;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.vo.Application;
import com.navercorp.pinpoint.web.vo.Range;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Taejin Koo
 */
@RunWith(MockitoJUnitRunner.class)
public class DeadlockCheckerTest {
    private static final String APPLICATION_NAME = "local_service";

    private static final String AGENT_ID_1 = "local_tomcat_1";

    private static final String AGENT_ID_2 = "local_tomcat_2";

    private static final String AGENT_ID_3 = "local_tomcat_3";

    private static final String SERVICE_TYPE = "tomcat";

    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    private static final long INTERVAL_MILLIS = 300000;

    private static final long START_TIME_MILLIS = (DeadlockCheckerTest.CURRENT_TIME_MILLIS) - (DeadlockCheckerTest.INTERVAL_MILLIS);

    private static final long TIMESTAMP_INTERVAL = 5000L;

    @Mock
    private AgentEventDao mockAgentEventDao;

    @Mock
    private ApplicationIndexDao mockApplicationIndexDao;

    @Test
    public void checkTest1() {
        Rule rule = new Rule(DeadlockCheckerTest.APPLICATION_NAME, DeadlockCheckerTest.SERVICE_TYPE, ERROR_COUNT.getName(), 50, "testGroup", false, false, "");
        Application application = new Application(DeadlockCheckerTest.APPLICATION_NAME, ServiceType.STAND_ALONE);
        Range range = Range.createUncheckedRange(DeadlockCheckerTest.START_TIME_MILLIS, DeadlockCheckerTest.CURRENT_TIME_MILLIS);
        Mockito.when(mockAgentEventDao.getAgentEvents(DeadlockCheckerTest.AGENT_ID_1, range, Collections.emptySet())).thenReturn(Arrays.asList(createAgentEvent(DeadlockCheckerTest.AGENT_ID_1, createEventTimestamp(), AGENT_CLOSED_BY_SERVER)));
        Mockito.when(mockAgentEventDao.getAgentEvents(DeadlockCheckerTest.AGENT_ID_2, range, Collections.emptySet())).thenReturn(Arrays.asList(createAgentEvent(DeadlockCheckerTest.AGENT_ID_2, createEventTimestamp(), AGENT_DEADLOCK_DETECTED)));
        Mockito.when(mockAgentEventDao.getAgentEvents(DeadlockCheckerTest.AGENT_ID_3, range, Collections.emptySet())).thenReturn(Arrays.asList(createAgentEvent(DeadlockCheckerTest.AGENT_ID_3, createEventTimestamp(), AGENT_PING)));
        AgentEventDataCollector dataCollector = new AgentEventDataCollector(DataCollectorCategory.AGENT_EVENT, application, mockAgentEventDao, mockApplicationIndexDao, DeadlockCheckerTest.CURRENT_TIME_MILLIS, DeadlockCheckerTest.INTERVAL_MILLIS);
        DeadlockChecker checker = new DeadlockChecker(dataCollector, rule);
        checker.check();
        Assert.assertTrue(checker.isDetected());
        String emailMessage = checker.getEmailMessage();
        Assert.assertTrue(StringUtils.hasLength(emailMessage));
        List<String> smsMessage = checker.getSmsMessage();
        Assert.assertTrue(((smsMessage.size()) == 1));
    }

    @Test
    public void checkTest2() {
        Rule rule = new Rule(DeadlockCheckerTest.APPLICATION_NAME, DeadlockCheckerTest.SERVICE_TYPE, ERROR_COUNT.getName(), 50, "testGroup", false, false, "");
        Application application = new Application(DeadlockCheckerTest.APPLICATION_NAME, ServiceType.STAND_ALONE);
        Range range = Range.createUncheckedRange(DeadlockCheckerTest.START_TIME_MILLIS, DeadlockCheckerTest.CURRENT_TIME_MILLIS);
        Mockito.when(mockAgentEventDao.getAgentEvents(DeadlockCheckerTest.AGENT_ID_1, range, Collections.emptySet())).thenReturn(Arrays.asList(createAgentEvent(DeadlockCheckerTest.AGENT_ID_1, createEventTimestamp(), AGENT_CLOSED_BY_SERVER)));
        Mockito.when(mockAgentEventDao.getAgentEvents(DeadlockCheckerTest.AGENT_ID_2, range, Collections.emptySet())).thenReturn(Arrays.asList(createAgentEvent(DeadlockCheckerTest.AGENT_ID_2, createEventTimestamp(), AGENT_SHUTDOWN)));
        Mockito.when(mockAgentEventDao.getAgentEvents(DeadlockCheckerTest.AGENT_ID_3, range, Collections.emptySet())).thenReturn(Arrays.asList(createAgentEvent(DeadlockCheckerTest.AGENT_ID_3, createEventTimestamp(), AGENT_PING)));
        AgentEventDataCollector dataCollector = new AgentEventDataCollector(DataCollectorCategory.AGENT_EVENT, application, mockAgentEventDao, mockApplicationIndexDao, DeadlockCheckerTest.CURRENT_TIME_MILLIS, DeadlockCheckerTest.INTERVAL_MILLIS);
        DeadlockChecker checker = new DeadlockChecker(dataCollector, rule);
        checker.check();
        Assert.assertFalse(checker.isDetected());
        String emailMessage = checker.getEmailMessage();
        Assert.assertTrue(StringUtils.isEmpty(emailMessage));
        List<String> smsMessage = checker.getSmsMessage();
        Assert.assertTrue(smsMessage.isEmpty());
    }
}

