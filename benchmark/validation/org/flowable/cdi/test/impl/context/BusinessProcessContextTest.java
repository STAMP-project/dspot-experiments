/**
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
package org.flowable.cdi.test.impl.context;


import org.flowable.cdi.BusinessProcess;
import org.flowable.cdi.test.CdiFlowableTestCase;
import org.flowable.cdi.test.impl.beans.CreditCard;
import org.flowable.cdi.test.impl.beans.ProcessScopedMessageBean;
import org.flowable.engine.test.Deployment;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Daniel Meyer
 */
public class BusinessProcessContextTest extends CdiFlowableTestCase {
    @Test
    @Deployment
    public void testResolution() throws Exception {
        BusinessProcess businessProcess = getBeanInstance(BusinessProcess.class);
        businessProcess.startProcessByKey("testResolution").getId();
        Assert.assertNotNull(getBeanInstance(CreditCard.class));
    }

    // no @Deployment for this test
    @Test
    public void testResolutionBeforeProcessStart() throws Exception {
        // assert that @BusinessProcessScoped beans can be resolved in the
        // absence of an underlying process instance:
        Assert.assertNotNull(getBeanInstance(CreditCard.class));
    }

    @Test
    @Deployment
    public void testConversationalBeanStoreFlush() throws Exception {
        getBeanInstance(BusinessProcess.class).setVariable("testVariable", "testValue");
        String pid = getBeanInstance(BusinessProcess.class).startProcessByKey("testConversationalBeanStoreFlush").getId();
        getBeanInstance(BusinessProcess.class).associateExecutionById(pid);
        // assert that the variable assigned on the businessProcess bean is flushed
        Assert.assertEquals("testValue", runtimeService.getVariable(pid, "testVariable"));
        // assert that the value set to the message bean in the first service task is flushed
        Assert.assertEquals("Hello from Flowable", getBeanInstance(ProcessScopedMessageBean.class).getMessage());
        // complete the task to allow the process instance to terminate
        taskService.complete(taskService.createTaskQuery().singleResult().getId());
    }

    @Test
    @Deployment
    public void testChangeProcessScopedBeanProperty() throws Exception {
        // resolve the creditcard bean (@BusinessProcessScoped) and set a value:
        getBeanInstance(CreditCard.class).setCreditcardNumber("123");
        String pid = getBeanInstance(BusinessProcess.class).startProcessByKey("testConversationalBeanStoreFlush").getId();
        getBeanInstance(BusinessProcess.class).startTask(taskService.createTaskQuery().singleResult().getId());
        // assert that the value of creditCardNumber is '123'
        Assert.assertEquals("123", getBeanInstance(CreditCard.class).getCreditcardNumber());
        // set a different value:
        getBeanInstance(CreditCard.class).setCreditcardNumber("321");
        // complete the task
        getBeanInstance(BusinessProcess.class).completeTask();
        getBeanInstance(BusinessProcess.class).associateExecutionById(pid);
        // now assert that the value of creditcard is "321":
        Assert.assertEquals("321", getBeanInstance(CreditCard.class).getCreditcardNumber());
        // complete the task to allow the process instance to terminate
        taskService.complete(taskService.createTaskQuery().singleResult().getId());
    }
}

