/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this    except in compliance with the License.
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


import java.util.ArrayList;
import java.util.List;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.persistence.entity.DeploymentEntity;
import org.flowable.engine.impl.persistence.entity.DeploymentEntityImpl;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
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
public class ProcessDefinitionsTest {
    protected ProcessDefinitionsMBean processDefinitionsMBean;

    @Mock
    protected ProcessEngineConfiguration processEngineConfiguration;

    @Mock
    protected RepositoryService repositoryService;

    @Mock
    protected ProcessDefinitionQuery processDefinitionQuery;

    @Mock
    protected DeploymentQuery deploymentQuery;

    @Mock
    protected DeploymentBuilder deploymentBuilder;

    protected ManagementMBeanAssembler assembler = new DefaultManagementMBeanAssembler();

    @Test
    public void testGetProcessDefinitions() {
        Mockito.when(repositoryService.createProcessDefinitionQuery()).thenReturn(processDefinitionQuery);
        List<ProcessDefinition> processDefinitionList = new ArrayList<>();
        ProcessDefinitionEntity pd = new ProcessDefinitionEntityImpl();
        pd.setId("testId");
        pd.setName("testName");
        pd.setVersion(175);
        pd.setSuspensionState(1);
        pd.setDescription("testDescription");
        processDefinitionList.add(pd);
        Mockito.when(processDefinitionQuery.list()).thenReturn(processDefinitionList);
        List<List<String>> result = processDefinitionsMBean.getProcessDefinitions();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(5, result.get(0).size());
        Assert.assertEquals("testId", result.get(0).get(0));
        Assert.assertEquals("testName", result.get(0).get(1));
        Assert.assertEquals("175", result.get(0).get(2));
        Assert.assertEquals("false", result.get(0).get(3));
        Assert.assertEquals("testDescription", result.get(0).get(4));
        pd.setSuspensionState(2);
        result = processDefinitionsMBean.getProcessDefinitions();
        Assert.assertEquals("true", result.get(0).get(3));
    }

    @Test
    public void testDeployments() {
        Mockito.when(repositoryService.createDeploymentQuery()).thenReturn(deploymentQuery);
        DeploymentEntity deployment = new DeploymentEntityImpl();
        List<Deployment> deploymentList = new ArrayList<>();
        deployment.setId("testDeploymentId");
        deployment.setName("testDeploymentName");
        deployment.setTenantId("tenantId");
        deploymentList.add(deployment);
        Mockito.when(deploymentQuery.list()).thenReturn(deploymentList);
        List<List<String>> result = processDefinitionsMBean.getDeployments();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(3, result.get(0).size());
        Assert.assertEquals("testDeploymentId", result.get(0).get(0));
        Assert.assertEquals("testDeploymentName", result.get(0).get(1));
        Assert.assertEquals("tenantId", result.get(0).get(2));
    }

    @Test
    public void testDeleteDeployment() {
        processDefinitionsMBean.deleteDeployment("id");
        Mockito.verify(repositoryService).deleteDeployment("id");
    }

    @Test
    public void testSuspendProcessDefinitionById() {
        processDefinitionsMBean.suspendProcessDefinitionById("id");
        Mockito.verify(repositoryService).suspendProcessDefinitionById("id");
    }

    @Test
    public void testActivatedProcessDefinitionById() {
        processDefinitionsMBean.activatedProcessDefinitionById("id");
        Mockito.verify(repositoryService).activateProcessDefinitionById("id");
    }

    @Test
    public void testSuspendProcessDefinitionByKey() {
        processDefinitionsMBean.suspendProcessDefinitionByKey("id");
        Mockito.verify(repositoryService).suspendProcessDefinitionByKey("id");
    }

    @Test
    public void testActivatedProcessDefinitionByKey() {
        processDefinitionsMBean.activatedProcessDefinitionByKey("id");
        Mockito.verify(repositoryService).activateProcessDefinitionByKey("id");
    }

    @Test
    public void testAnnotations() throws JMException, MalformedObjectNameException {
        ModelMBean modelBean = assembler.assemble(processDefinitionsMBean, new ObjectName("domain", "key", "value"));
        Assert.assertNotNull(modelBean);
        MBeanInfo beanInfo = modelBean.getMBeanInfo();
        Assert.assertNotNull(beanInfo);
        Assert.assertNotNull(beanInfo.getOperations());
        Assert.assertEquals(9, beanInfo.getOperations().length);
        int counter = 0;
        for (MBeanOperationInfo op : beanInfo.getOperations()) {
            if (op.getName().equals("deleteDeployment")) {
                counter++;
                Assert.assertEquals("delete deployment", op.getDescription());
                Assert.assertEquals("void", op.getReturnType());
                Assert.assertEquals(1, op.getSignature().length);
                Assert.assertEquals("java.lang.String", op.getSignature()[0].getType());
            } else
                if (op.getName().equals("suspendProcessDefinitionById")) {
                    counter++;
                    Assert.assertEquals("Suspend given process ID", op.getDescription());
                    Assert.assertEquals("void", op.getReturnType());
                    Assert.assertEquals(1, op.getSignature().length);
                    Assert.assertEquals("java.lang.String", op.getSignature()[0].getType());
                } else
                    if (op.getName().equals("activatedProcessDefinitionById")) {
                        counter++;
                        Assert.assertEquals("Activate given process ID", op.getDescription());
                        Assert.assertEquals("void", op.getReturnType());
                        Assert.assertEquals(1, op.getSignature().length);
                        Assert.assertEquals("java.lang.String", op.getSignature()[0].getType());
                    } else
                        if (op.getName().equals("suspendProcessDefinitionByKey")) {
                            counter++;
                            Assert.assertEquals("Suspend given process ID", op.getDescription());
                            Assert.assertEquals("void", op.getReturnType());
                            Assert.assertEquals(1, op.getSignature().length);
                            Assert.assertEquals("java.lang.String", op.getSignature()[0].getType());
                        } else
                            if (op.getName().equals("activatedProcessDefinitionByKey")) {
                                counter++;
                                Assert.assertEquals("Activate given process ID", op.getDescription());
                                Assert.assertEquals("void", op.getReturnType());
                                Assert.assertEquals(1, op.getSignature().length);
                                Assert.assertEquals("java.lang.String", op.getSignature()[0].getType());
                            } else
                                if (op.getName().equals("deployProcessDefinition")) {
                                    counter++;
                                    Assert.assertEquals("Deploy Process Definition", op.getDescription());
                                    Assert.assertEquals("void", op.getReturnType());
                                    Assert.assertEquals(2, op.getSignature().length);
                                    Assert.assertEquals("java.lang.String", op.getSignature()[0].getType());
                                    Assert.assertEquals("java.lang.String", op.getSignature()[1].getType());
                                }





        }
        Assert.assertEquals(6, counter);
        // check attributes
        Assert.assertNotNull(beanInfo.getAttributes());
        Assert.assertEquals(2, beanInfo.getAttributes().length);
        counter = 0;
        for (MBeanAttributeInfo attr : beanInfo.getAttributes()) {
            if (attr.getName().equals("ProcessDefinitions")) {
                counter++;
                Assert.assertEquals("List of Process definitions", attr.getDescription());
                Assert.assertEquals("java.util.List", attr.getType());
            } else
                if (attr.getName().equals("Deployments")) {
                    counter++;
                    Assert.assertEquals("List of deployed Processes", attr.getDescription());
                    Assert.assertEquals("java.util.List", attr.getType());
                }

        }
        Assert.assertEquals(2, counter);
    }
}

