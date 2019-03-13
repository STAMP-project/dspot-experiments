/**
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.scheduling.config;


import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ScheduledTaskRegistrar}.
 *
 * @author Tobias Montagna-Hay
 * @author Juergen Hoeller
 * @since 4.2
 */
public class ScheduledTaskRegistrarTests {
    private final ScheduledTaskRegistrar taskRegistrar = new ScheduledTaskRegistrar();

    @Test
    public void emptyTaskLists() {
        Assert.assertTrue(this.taskRegistrar.getTriggerTaskList().isEmpty());
        Assert.assertTrue(this.taskRegistrar.getCronTaskList().isEmpty());
        Assert.assertTrue(this.taskRegistrar.getFixedRateTaskList().isEmpty());
        Assert.assertTrue(this.taskRegistrar.getFixedDelayTaskList().isEmpty());
    }

    @Test
    public void getTriggerTasks() {
        TriggerTask mockTriggerTask = Mockito.mock(TriggerTask.class);
        List<TriggerTask> triggerTaskList = Collections.singletonList(mockTriggerTask);
        this.taskRegistrar.setTriggerTasksList(triggerTaskList);
        List<TriggerTask> retrievedList = this.taskRegistrar.getTriggerTaskList();
        Assert.assertEquals(1, retrievedList.size());
        Assert.assertEquals(mockTriggerTask, retrievedList.get(0));
    }

    @Test
    public void getCronTasks() {
        CronTask mockCronTask = Mockito.mock(CronTask.class);
        List<CronTask> cronTaskList = Collections.singletonList(mockCronTask);
        this.taskRegistrar.setCronTasksList(cronTaskList);
        List<CronTask> retrievedList = this.taskRegistrar.getCronTaskList();
        Assert.assertEquals(1, retrievedList.size());
        Assert.assertEquals(mockCronTask, retrievedList.get(0));
    }

    @Test
    public void getFixedRateTasks() {
        IntervalTask mockFixedRateTask = Mockito.mock(IntervalTask.class);
        List<IntervalTask> fixedRateTaskList = Collections.singletonList(mockFixedRateTask);
        this.taskRegistrar.setFixedRateTasksList(fixedRateTaskList);
        List<IntervalTask> retrievedList = this.taskRegistrar.getFixedRateTaskList();
        Assert.assertEquals(1, retrievedList.size());
        Assert.assertEquals(mockFixedRateTask, retrievedList.get(0));
    }

    @Test
    public void getFixedDelayTasks() {
        IntervalTask mockFixedDelayTask = Mockito.mock(IntervalTask.class);
        List<IntervalTask> fixedDelayTaskList = Collections.singletonList(mockFixedDelayTask);
        this.taskRegistrar.setFixedDelayTasksList(fixedDelayTaskList);
        List<IntervalTask> retrievedList = this.taskRegistrar.getFixedDelayTaskList();
        Assert.assertEquals(1, retrievedList.size());
        Assert.assertEquals(mockFixedDelayTask, retrievedList.get(0));
    }
}

