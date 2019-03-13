/**
 * Copyright 2002-2009 the original author or authors.
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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


/**
 *
 *
 * @author Mark Fisher
 */
public class SchedulerBeanDefinitionParserTests {
    private ApplicationContext context;

    @Test
    public void defaultScheduler() {
        ThreadPoolTaskScheduler scheduler = ((ThreadPoolTaskScheduler) (this.context.getBean("defaultScheduler")));
        Integer size = ((Integer) (getPropertyValue("poolSize")));
        Assert.assertEquals(new Integer(1), size);
    }

    @Test
    public void customScheduler() {
        ThreadPoolTaskScheduler scheduler = ((ThreadPoolTaskScheduler) (this.context.getBean("customScheduler")));
        Integer size = ((Integer) (getPropertyValue("poolSize")));
        Assert.assertEquals(new Integer(42), size);
    }

    @Test
    public void threadNamePrefix() {
        ThreadPoolTaskScheduler scheduler = ((ThreadPoolTaskScheduler) (this.context.getBean("customScheduler")));
        Assert.assertEquals("customScheduler-", scheduler.getThreadNamePrefix());
    }
}

