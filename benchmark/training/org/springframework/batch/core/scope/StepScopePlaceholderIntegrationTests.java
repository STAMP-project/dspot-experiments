/**
 * Copyright 2008-2012 the original author or authors.
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
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class StepScopePlaceholderIntegrationTests implements BeanFactoryAware {
    @Autowired
    @Qualifier("simple")
    private Collaborator simple;

    @Autowired
    @Qualifier("compound")
    private Collaborator compound;

    @Autowired
    @Qualifier("value")
    private Collaborator value;

    @Autowired
    @Qualifier("ref")
    private Collaborator ref;

    @Autowired
    @Qualifier("scopedRef")
    private Collaborator scopedRef;

    @Autowired
    @Qualifier("list")
    private Collaborator list;

    @Autowired
    @Qualifier("bar")
    private Collaborator bar;

    @Autowired
    @Qualifier("nested")
    private Collaborator nested;

    private StepExecution stepExecution;

    private ListableBeanFactory beanFactory;

    private int beanCount;

    @Test
    public void testSimpleProperty() throws Exception {
        Assert.assertEquals("bar", simple.getName());
        // Once the step context is set up it should be baked into the proxies
        // so changing it now should have no effect
        stepExecution.getExecutionContext().put("foo", "wrong!");
        Assert.assertEquals("bar", simple.getName());
    }

    @Test
    public void testCompoundProperty() throws Exception {
        Assert.assertEquals("bar-bar", compound.getName());
    }

    @Test
    public void testCompoundPropertyTwice() throws Exception {
        Assert.assertEquals("bar-bar", compound.getName());
        StepSynchronizationManager.close();
        stepExecution = new StepExecution("foo", new JobExecution(11L), 123L);
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put("foo", "spam");
        stepExecution.setExecutionContext(executionContext);
        StepSynchronizationManager.register(stepExecution);
        Assert.assertEquals("spam-bar", compound.getName());
    }

    @Test
    public void testParentByRef() throws Exception {
        Assert.assertEquals("bar", ref.getParent().getName());
    }

    @Test
    public void testParentByValue() throws Exception {
        Assert.assertEquals("bar", value.getParent().getName());
    }

    @Test
    public void testList() throws Exception {
        Assert.assertEquals("[bar]", list.getList().toString());
    }

    @Test
    public void testNested() throws Exception {
        Assert.assertEquals("bar", nested.getParent().getName());
    }

    @Test
    public void testScopedRef() throws Exception {
        Assert.assertEquals("bar", scopedRef.getParent().getName());
        stop();
        start("spam");
        Assert.assertEquals("spam", scopedRef.getParent().getName());
    }
}

