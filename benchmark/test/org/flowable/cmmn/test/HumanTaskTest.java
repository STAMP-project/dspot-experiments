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
package org.flowable.cmmn.test;


import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author martin.grofcik
 */
public class HumanTaskTest extends AbstractProcessEngineIntegrationTest {
    protected FormRepositoryService formRepositoryService;

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/CaseTaskTest.testCaseTask.cmmn")
    public void completeHumanTaskWithoutVariables() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Assert.assertNotNull(caseInstance);
        Task caseTask = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertNotNull(caseTask);
        cmmnTaskService.completeTaskWithForm(caseTask.getId(), formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult().getId(), "__COMPLETE", null);
        CaseInstance dbCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertNull(dbCaseInstance);
    }
}

