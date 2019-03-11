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
package org.flowable.osgi.blueprint;


import javax.inject.Inject;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;


/**
 * Test class to do basic testing against an OSGi container using the Flowable blueprint functionality
 *
 * @author Tijs Rademakers
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BlueprintBasicTest {
    @Inject
    protected BundleContext ctx;

    @Inject
    protected ProcessEngine processEngine;

    @Inject
    protected RuntimeService runtimeService;

    @Inject
    protected RepositoryService repositoryService;

    @Inject
    protected HistoryService historyService;

    @Test
    public void exportedServices() throws Exception {
        Assert.assertNotNull(processEngine);
        Assert.assertNotNull(repositoryService);
        // wait for deployment to be done
        Thread.sleep(5000);
        Deployment deployment = repositoryService.createDeploymentQuery().singleResult();
        Assert.assertEquals("org.flowable.osgi.example", deployment.getName());
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
        Assert.assertEquals("osgiProcess", processDefinition.getKey());
    }

    @Test
    public void exportJavaDelegate() throws Exception {
        // wait for deployment to be done
        Thread.sleep(5000);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("osgiProcess");
        Assert.assertTrue(processInstance.isEnded());
        HistoricVariableInstance variable = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).variableName("visited").singleResult();
        Assert.assertTrue(((Boolean) (variable.getValue())));
        HistoricVariableInstance activityBehaviourVisited = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).variableName("visitedActivityBehaviour").singleResult();
        Assert.assertTrue(((Boolean) (activityBehaviourVisited.getValue())));
    }
}

