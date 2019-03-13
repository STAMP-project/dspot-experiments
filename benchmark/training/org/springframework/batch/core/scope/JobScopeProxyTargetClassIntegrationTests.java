/**
 * Copyright 2013-2014 the original author or authors.
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
package org.springframework.batch.core.scope;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class JobScopeProxyTargetClassIntegrationTests implements BeanFactoryAware {
    @Autowired
    @Qualifier("simple")
    private TestCollaborator simple;

    private JobExecution jobExecution;

    private ListableBeanFactory beanFactory;

    private int beanCount;

    @Test
    public void testSimpleProperty() throws Exception {
        Assert.assertEquals("bar", simple.getName());
        // Once the job context is set up it should be baked into the proxies
        // so changing it now should have no effect
        jobExecution.getExecutionContext().put("foo", "wrong!");
        Assert.assertEquals("bar", simple.getName());
    }
}

