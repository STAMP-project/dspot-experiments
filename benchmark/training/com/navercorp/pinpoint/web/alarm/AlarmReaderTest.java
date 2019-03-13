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
package com.navercorp.pinpoint.web.alarm;


import com.navercorp.pinpoint.web.alarm.vo.Rule;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.service.AlarmService;
import com.navercorp.pinpoint.web.service.AlarmServiceImpl;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;


public class AlarmReaderTest {
    private static ApplicationIndexDao applicationIndexDao;

    private static AlarmService alarmService;

    private static DataCollectorFactory dataCollectorFactory;

    private static final String APP_NAME = "app";

    private static final String SERVICE_TYPE = "tomcat";

    @Test
    public void readTest() {
        StepExecution stepExecution = new StepExecution("alarmStep", null);
        ExecutionContext executionContext = new ExecutionContext();
        stepExecution.setExecutionContext(executionContext);
        AlarmReader reader = new AlarmReader(AlarmReaderTest.dataCollectorFactory, AlarmReaderTest.applicationIndexDao, AlarmReaderTest.alarmService);
        reader.beforeStep(stepExecution);
        for (int i = 0; i < 7; i++) {
            Assert.assertNotNull(reader.read());
        }
        Assert.assertNull(reader.read());
    }

    @Test
    public void readTest3() {
        StepExecution stepExecution = new StepExecution("alarmStep", null);
        ExecutionContext executionContext = new ExecutionContext();
        stepExecution.setExecutionContext(executionContext);
        AlarmServiceImpl alarmService = new AlarmServiceImpl() {
            @Override
            public List<Rule> selectRuleByApplicationId(String applicationId) {
                return new LinkedList<Rule>();
            }
        };
        AlarmReader reader = new AlarmReader(AlarmReaderTest.dataCollectorFactory, AlarmReaderTest.applicationIndexDao, alarmService);
        reader.beforeStep(stepExecution);
        Assert.assertNull(reader.read());
    }
}

