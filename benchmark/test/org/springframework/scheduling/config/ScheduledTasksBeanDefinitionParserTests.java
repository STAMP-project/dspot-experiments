/**
 * Copyright 2002-2012 the original author or authors.
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


import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.ScheduledMethodRunnable;


/**
 *
 *
 * @author Mark Fisher
 * @author Chris Beams
 */
@SuppressWarnings("unchecked")
public class ScheduledTasksBeanDefinitionParserTests {
    private ApplicationContext context;

    private ScheduledTaskRegistrar registrar;

    private Object testBean;

    @Test
    public void checkScheduler() {
        Object schedulerBean = this.context.getBean("testScheduler");
        Object schedulerRef = getPropertyValue("taskScheduler");
        Assert.assertEquals(schedulerBean, schedulerRef);
    }

    @Test
    public void checkTarget() {
        List<IntervalTask> tasks = ((List<IntervalTask>) (new org.springframework.beans.DirectFieldAccessor(this.registrar).getPropertyValue("fixedRateTasks")));
        Runnable runnable = tasks.get(0).getRunnable();
        Assert.assertEquals(ScheduledMethodRunnable.class, runnable.getClass());
        Object targetObject = getTarget();
        Method targetMethod = getMethod();
        Assert.assertEquals(this.testBean, targetObject);
        Assert.assertEquals("test", targetMethod.getName());
    }

    @Test
    public void fixedRateTasks() {
        List<IntervalTask> tasks = ((List<IntervalTask>) (new org.springframework.beans.DirectFieldAccessor(this.registrar).getPropertyValue("fixedRateTasks")));
        Assert.assertEquals(3, tasks.size());
        Assert.assertEquals(1000L, tasks.get(0).getInterval());
        Assert.assertEquals(2000L, tasks.get(1).getInterval());
        Assert.assertEquals(4000L, tasks.get(2).getInterval());
        Assert.assertEquals(500, tasks.get(2).getInitialDelay());
    }

    @Test
    public void fixedDelayTasks() {
        List<IntervalTask> tasks = ((List<IntervalTask>) (new org.springframework.beans.DirectFieldAccessor(this.registrar).getPropertyValue("fixedDelayTasks")));
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals(3000L, tasks.get(0).getInterval());
        Assert.assertEquals(3500L, tasks.get(1).getInterval());
        Assert.assertEquals(250, tasks.get(1).getInitialDelay());
    }

    @Test
    public void cronTasks() {
        List<CronTask> tasks = ((List<CronTask>) (new org.springframework.beans.DirectFieldAccessor(this.registrar).getPropertyValue("cronTasks")));
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals("*/4 * 9-17 * * MON-FRI", tasks.get(0).getExpression());
    }

    @Test
    public void triggerTasks() {
        List<TriggerTask> tasks = ((List<TriggerTask>) (new org.springframework.beans.DirectFieldAccessor(this.registrar).getPropertyValue("triggerTasks")));
        Assert.assertEquals(1, tasks.size());
        Assert.assertThat(tasks.get(0).getTrigger(), CoreMatchers.instanceOf(ScheduledTasksBeanDefinitionParserTests.TestTrigger.class));
    }

    static class TestBean {
        public void test() {
        }
    }

    static class TestTrigger implements Trigger {
        @Override
        public Date nextExecutionTime(TriggerContext triggerContext) {
            return null;
        }
    }
}

