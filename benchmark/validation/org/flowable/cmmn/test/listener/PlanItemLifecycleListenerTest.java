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
package org.flowable.cmmn.test.listener;


import HistoryLevel.AUDIT;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.cmmn.api.listener.PlanItemInstanceLifecycleListener;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.UserEventListenerInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.test.impl.CustomCmmnConfigurationFlowableTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class PlanItemLifecycleListenerTest extends CustomCmmnConfigurationFlowableTestCase {
    @Test
    @CmmnDeployment
    public void testListeners() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testPlanItemLifecycleListeners").start();
        assertVariable(caseInstance, "classDelegateVariable", "Hello World");
        assertVariable(caseInstance, "variableFromDelegateExpression", "Hello World from delegate expression");
        assertVariable(caseInstance, "expressionVar", "planItemIsActive");
        assertVariable(caseInstance, "stageActive", true);
        assertVariable(caseInstance, "milestoneReached", true);
    }

    @Test
    @CmmnDeployment
    public void testEventListenerPlanItemLifecycleListener() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testEventListenerPlanItemLifecycleListener").start();
        assertThat(((Boolean) (cmmnRuntimeService.getVariable(caseInstance.getId(), "available")))).isTrue();
        assertThat(((Boolean) (cmmnRuntimeService.getVariable(caseInstance.getId(), "completed")))).isNull();
        assertThat(((Boolean) (cmmnRuntimeService.getVariable(caseInstance.getId(), "terminated")))).isNull();
        UserEventListenerInstance userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstance.getId());
        assertThat(((Boolean) (cmmnRuntimeService.getVariable(caseInstance.getId(), "available")))).isTrue();
        assertThat(((Boolean) (cmmnRuntimeService.getVariable(caseInstance.getId(), "completed")))).isTrue();
        assertThat(((Boolean) (cmmnRuntimeService.getVariable(caseInstance.getId(), "terminated")))).isNull();
        // Same, but terminate the case
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testEventListenerPlanItemLifecycleListener").start();
        cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(AUDIT)) {
            assertThat(((Boolean) (cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("available").singleResult().getValue()))).isTrue();
            assertThat(cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("completed").singleResult()).isNull();
            assertThat(((Boolean) (cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("terminate").singleResult().getValue()))).isTrue();
        }
    }

    static class TestDelegateTaskListener implements PlanItemInstanceLifecycleListener {
        @Override
        public String getSourceState() {
            return null;
        }

        @Override
        public String getTargetState() {
            return null;
        }

        @Override
        public void stateChanged(DelegatePlanItemInstance planItemInstance, String oldState, String newState) {
            planItemInstance.setVariable("variableFromDelegateExpression", "Hello World from delegate expression");
        }
    }
}

