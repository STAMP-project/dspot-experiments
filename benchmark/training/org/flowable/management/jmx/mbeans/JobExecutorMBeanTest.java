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
package org.flowable.management.jmx.mbeans;


import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.management.jmx.DefaultManagementMBeanAssembler;
import org.flowable.management.jmx.ManagementMBeanAssembler;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Saeid Mirzaei
 */
public class JobExecutorMBeanTest {
    protected JobExecutorMBean jobExecutorMbean;

    @Mock
    protected ProcessEngineConfiguration processEngineConfiguration;

    @Mock
    protected AsyncExecutor jobExecutor;

    @Test
    public void TestIsJobExecutorActivatedFalse() {
        Mockito.when(jobExecutor.isActive()).thenReturn(false);
        boolean result = jobExecutorMbean.isJobExecutorActivated();
        Mockito.verify(jobExecutor).isActive();
        Assert.assertFalse(result);
    }

    @Test
    public void TestIsJobExecutorActivatedTrue() {
        Mockito.when(jobExecutor.isActive()).thenReturn(true);
        boolean result = jobExecutorMbean.isJobExecutorActivated();
        Mockito.verify(jobExecutor).isActive();
        Assert.assertTrue(result);
    }

    @Test
    public void setJobExecutorActivateTrue() {
        jobExecutorMbean.setJobExecutorActivate(true);
        Mockito.verify(jobExecutor).start();
        jobExecutorMbean.setJobExecutorActivate(false);
        Mockito.verify(jobExecutor).shutdown();
    }

    ManagementMBeanAssembler assembler = new DefaultManagementMBeanAssembler();

    @Test
    public void testAnnotations() throws JMException, MalformedObjectNameException {
        ModelMBean modelBean = assembler.assemble(jobExecutorMbean, new ObjectName("domain", "key", "value"));
        Assert.assertNotNull(modelBean);
        MBeanInfo beanInfo = modelBean.getMBeanInfo();
        Assert.assertNotNull(beanInfo);
        Assert.assertNotNull(beanInfo.getOperations());
        Assert.assertEquals(2, beanInfo.getOperations().length);
        int counter = 0;
        for (MBeanOperationInfo op : beanInfo.getOperations()) {
            if (op.getName().equals("setJobExecutorActivate")) {
                counter++;
                Assert.assertEquals("set job executor activate", op.getDescription());
                Assert.assertEquals("void", op.getReturnType());
                Assert.assertEquals(1, op.getSignature().length);
                Assert.assertEquals("java.lang.Boolean", op.getSignature()[0].getType());
            }
        }
        Assert.assertEquals(1, counter);
        // check attributes
        Assert.assertNotNull(beanInfo.getAttributes());
        Assert.assertEquals(1, beanInfo.getAttributes().length);
        counter = 0;
        for (MBeanAttributeInfo attr : beanInfo.getAttributes()) {
            if (attr.getName().equals("JobExecutorActivated")) {
                counter++;
                Assert.assertEquals("check if the job executor is activated", attr.getDescription());
                Assert.assertEquals("boolean", attr.getType());
            }
        }
        Assert.assertEquals(1, counter);
    }
}

