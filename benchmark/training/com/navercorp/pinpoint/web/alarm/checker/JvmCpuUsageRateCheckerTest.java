/**
 * Copyright 2014 NAVER Corp.
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


import CheckerCategory.JVM_CPU_USAGE_RATE;
import com.navercorp.pinpoint.common.server.bo.stat.CpuLoadBo;
import com.navercorp.pinpoint.common.server.bo.stat.JvmGcBo;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.alarm.DataCollectorFactory;
import com.navercorp.pinpoint.web.alarm.DataCollectorFactory.DataCollectorCategory;
import com.navercorp.pinpoint.web.alarm.collector.AgentStatDataCollector;
import com.navercorp.pinpoint.web.alarm.vo.Rule;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.dao.stat.AgentStatDao;
import com.navercorp.pinpoint.web.vo.Application;
import org.junit.Assert;
import org.junit.Test;


public class JvmCpuUsageRateCheckerTest {
    private static final String SERVICE_NAME = "local_service";

    private static final String SERVICE_TYPE = "tomcat";

    private static ApplicationIndexDao applicationIndexDao;

    private static AgentStatDao<JvmGcBo> jvmGcDao;

    private static AgentStatDao<CpuLoadBo> cpuLoadDao;

    @Test
    public void checkTest1() {
        Rule rule = new Rule(JvmCpuUsageRateCheckerTest.SERVICE_NAME, JvmCpuUsageRateCheckerTest.SERVICE_TYPE, JVM_CPU_USAGE_RATE.getName(), 60, "testGroup", false, false, "");
        Application application = new Application(JvmCpuUsageRateCheckerTest.SERVICE_NAME, ServiceType.STAND_ALONE);
        AgentStatDataCollector collector = new AgentStatDataCollector(DataCollectorCategory.AGENT_STAT, application, JvmCpuUsageRateCheckerTest.jvmGcDao, JvmCpuUsageRateCheckerTest.cpuLoadDao, JvmCpuUsageRateCheckerTest.applicationIndexDao, System.currentTimeMillis(), DataCollectorFactory.SLOT_INTERVAL_FIVE_MIN);
        AgentChecker checker = new JvmCpuUsageRateChecker(collector, rule);
        checker.check();
        Assert.assertTrue(checker.isDetected());
    }

    @Test
    public void checkTest2() {
        Rule rule = new Rule(JvmCpuUsageRateCheckerTest.SERVICE_NAME, JvmCpuUsageRateCheckerTest.SERVICE_TYPE, JVM_CPU_USAGE_RATE.getName(), 61, "testGroup", false, false, "");
        Application application = new Application(JvmCpuUsageRateCheckerTest.SERVICE_NAME, ServiceType.STAND_ALONE);
        AgentStatDataCollector collector = new AgentStatDataCollector(DataCollectorCategory.AGENT_STAT, application, JvmCpuUsageRateCheckerTest.jvmGcDao, JvmCpuUsageRateCheckerTest.cpuLoadDao, JvmCpuUsageRateCheckerTest.applicationIndexDao, System.currentTimeMillis(), DataCollectorFactory.SLOT_INTERVAL_FIVE_MIN);
        AgentChecker checker = new JvmCpuUsageRateChecker(collector, rule);
        checker.check();
        Assert.assertFalse(checker.isDetected());
    }
}

