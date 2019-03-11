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
package org.flowable.management.jmx;


import java.net.URL;
import java.util.List;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Saeid Mirzaei
 */
public class DeploymentsJMXClientTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testDeploymentsJmxClient() throws Exception {
        String hostName = Utils.getHostName();
        JMXServiceURL url = new JMXServiceURL((((("service:jmx:rmi://" + hostName) + ":10111/jndi/rmi://") + hostName) + ":1099/jmxrmi/flowable"));
        ProcessEngineConfiguration processEngineConfig = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("flowable.cfg.xml");
        ProcessEngine processEngine = processEngineConfig.buildProcessEngine();
        // wait for jmx server to come up
        Thread.sleep(500);
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        ObjectName deploymentsBeanName = new ObjectName("org.flowable.jmx.Mbeans:type=Deployments");
        Thread.sleep(500);
        // no process deployed yet
        List<List<String>> deployments = ((List<List<String>>) (mbsc.getAttribute(deploymentsBeanName, "Deployments")));
        Assert.assertEquals(0, deployments.size());
        // deploy process remotely
        URL fileName = Thread.currentThread().getContextClassLoader().getResource("org/flowable/management/jmx/trivialProcess.bpmn");
        mbsc.invoke(deploymentsBeanName, "deployProcessDefinition", new String[]{ "trivialProcess.bpmn", fileName.getFile() }, new String[]{ String.class.getName(), String.class.getName() });
        // one process is there now, test remote deployments
        deployments = ((List<List<String>>) (mbsc.getAttribute(deploymentsBeanName, "Deployments")));
        Assert.assertNotNull(deployments);
        Assert.assertEquals(1, deployments.size());
        Assert.assertEquals(3, deployments.get(0).size());
        String firstDeploymentId = deployments.get(0).get(0);
        // test remote process definition
        List<List<String>> pdList = ((List<List<String>>) (mbsc.getAttribute(deploymentsBeanName, "ProcessDefinitions")));
        Assert.assertNotNull(pdList);
        Assert.assertEquals(1, pdList.size());
        Assert.assertEquals(5, pdList.get(0).size());
        Assert.assertNotNull(pdList.get(0).get(0));
        Assert.assertEquals("My process", pdList.get(0).get(1));
        Assert.assertEquals("1", pdList.get(0).get(2));// version

        Assert.assertEquals("false", pdList.get(0).get(3));// not suspended

        Assert.assertEquals("This process to test JMX", pdList.get(0).get(4));
        // redeploy the same process
        mbsc.invoke(deploymentsBeanName, "deployProcessDefinition", new String[]{ "trivialProcess.bpmn", fileName.getFile() }, new String[]{ String.class.getName(), String.class.getName() });
        // now there should be two deployments
        deployments = ((List<List<String>>) (mbsc.getAttribute(deploymentsBeanName, "Deployments")));
        Assert.assertNotNull(deployments);
        Assert.assertEquals(2, deployments.size());
        Assert.assertEquals(3, deployments.get(0).size());
        Assert.assertEquals(3, deployments.get(1).size());
        // there should be two process definitions, one with version equals to
        // two
        pdList = ((List<List<String>>) (mbsc.getAttribute(deploymentsBeanName, "ProcessDefinitions")));
        Assert.assertNotNull(pdList);
        Assert.assertEquals(2, pdList.size());
        Assert.assertEquals(5, pdList.get(0).size());
        Assert.assertEquals(5, pdList.get(1).size());
        // check there is one with version= = 1 and another one with version ==
        // 2, other attributed are the same
        String pidV2 = null;
        String pidV1 = null;
        if ((pdList.get(0).get(2).equals("1")) && (pdList.get(1).get(2).equals("2"))) {
            pidV2 = pdList.get(1).get(0);
            pidV1 = pdList.get(0).get(0);
        } else
            if ((pdList.get(1).get(2).equals("1")) && (pdList.get(0).get(2).equals("2"))) {
                pidV2 = pdList.get(0).get(0);
                pidV1 = pdList.get(1).get(0);
            } else
                Assert.fail("there should one process definition with version == 1 and another one with version == 2. It is not the case");


        Assert.assertNotNull(pdList.get(0).get(0));
        Assert.assertNotNull(pdList.get(1).get(0));
        Assert.assertEquals("My process", pdList.get(0).get(1));
        Assert.assertEquals("My process", pdList.get(1).get(1));
        Assert.assertEquals("false", pdList.get(0).get(3));// not suspended

        Assert.assertEquals("false", pdList.get(1).get(3));// not suspended

        Assert.assertEquals("This process to test JMX", pdList.get(0).get(4));
        Assert.assertEquals("This process to test JMX", pdList.get(1).get(4));
        // suspend the one with version == 2
        mbsc.invoke(deploymentsBeanName, "suspendProcessDefinitionById", new String[]{ pidV2 }, new String[]{ String.class.getName() });
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // test if it is really suspended and not the other one
        Assert.assertTrue(repositoryService.getProcessDefinition(pidV2).isSuspended());
        Assert.assertFalse(repositoryService.getProcessDefinition(pidV1).isSuspended());
        // test if it is reported as suspended and not the other one
        List<String> pd = ((List<String>) (mbsc.invoke(deploymentsBeanName, "getProcessDefinitionById", new String[]{ pidV2 }, new String[]{ String.class.getName() })));
        Assert.assertNotNull(pd);
        Assert.assertEquals(5, pd.size());
        Assert.assertEquals("true", pd.get(3));
        pd = ((List<String>) (mbsc.invoke(deploymentsBeanName, "getProcessDefinitionById", new String[]{ pidV1 }, new String[]{ String.class.getName() })));
        Assert.assertNotNull(pd);
        Assert.assertEquals(5, pd.size());
        Assert.assertEquals("false", pd.get(3));
        // now reactivate the same suspended process
        mbsc.invoke(deploymentsBeanName, "activatedProcessDefinitionById", new String[]{ pidV2 }, new String[]{ String.class.getName() });
        // test if both processes are active again
        Assert.assertFalse(repositoryService.getProcessDefinition(pidV2).isSuspended());
        Assert.assertFalse(repositoryService.getProcessDefinition(pidV1).isSuspended());
        // test if they are properly reported as activated
        pd = ((List<String>) (mbsc.invoke(deploymentsBeanName, "getProcessDefinitionById", new String[]{ pidV2 }, new String[]{ String.class.getName() })));
        Assert.assertNotNull(pd);
        Assert.assertEquals(5, pd.size());
        Assert.assertEquals("false", pd.get(3));
        pd = ((List<String>) (mbsc.invoke(deploymentsBeanName, "getProcessDefinitionById", new String[]{ pidV1 }, new String[]{ String.class.getName() })));
        Assert.assertNotNull(pd);
        Assert.assertEquals(5, pd.size());
        Assert.assertEquals("false", pd.get(3));
        // now undeploy the one with version == 1
        mbsc.invoke(deploymentsBeanName, "deleteDeployment", new String[]{ firstDeploymentId }, new String[]{ String.class.getName() });
        // now there should be only one deployment and only one process
        // definition with version 2, first check it with API
        Assert.assertEquals(1, repositoryService.createDeploymentQuery().count());
        Assert.assertTrue((!(repositoryService.createDeploymentQuery().singleResult().getId().equals(firstDeploymentId))));
        // check if it is also affected in returned results.
        deployments = ((List<List<String>>) (mbsc.getAttribute(deploymentsBeanName, "Deployments")));
        Assert.assertNotNull(deployments);
        Assert.assertEquals(1, deployments.size());
        Assert.assertEquals(3, deployments.get(0).size());
        Assert.assertTrue((!(deployments.get(0).get(0).equals(firstDeploymentId))));
    }
}

