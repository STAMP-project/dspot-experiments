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
package org.flowable.cmmn.test.history;


import PlanItemDefinitionType.HUMAN_TASK;
import PlanItemDefinitionType.STAGE;
import PlanItemInstanceState.ACTIVE;
import PlanItemInstanceState.AVAILABLE;
import PlanItemInstanceState.ENABLED;
import java.util.Arrays;
import java.util.List;
import org.flowable.cmmn.api.history.HistoricPlanItemInstance;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class HistoricPlanItemInstanceQueryTest extends FlowableCmmnTestCase {
    protected String deploymentId;

    protected String caseDefinitionId;

    @Test
    public void testByCaseDefinitionId() {
        startInstances(5);
        Assert.assertEquals(20, cmmnHistoryService.createHistoricPlanItemInstanceQuery().list().size());
    }

    @Test
    public void testByCaseInstanceId() {
        List<String> caseInstanceIds = startInstances(3);
        for (String caseInstanceId : caseInstanceIds) {
            Assert.assertEquals(4, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceCaseInstanceId(caseInstanceId).list().size());
        }
    }

    @Test
    public void testByStageInstanceId() {
        startInstances(1);
        HistoricPlanItemInstance planItemInstance = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceDefinitionType(STAGE).planItemInstanceName("Stage one").singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals(2, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceStageInstanceId(planItemInstance.getId()).count());
    }

    @Test
    public void testByPlanItemInstanceId() {
        startInstances(1);
        List<HistoricPlanItemInstance> planItemInstances = cmmnHistoryService.createHistoricPlanItemInstanceQuery().list();
        for (HistoricPlanItemInstance planItemInstance : planItemInstances) {
            Assert.assertEquals(1L, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceId(planItemInstance.getId()).count());
        }
    }

    @Test
    public void testByElementId() {
        startInstances(4);
        Assert.assertEquals(4, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceElementId("planItem3").list().size());
    }

    @Test
    public void testByName() {
        startInstances(9);
        Assert.assertEquals(9, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceName("B").list().size());
    }

    @Test
    public void testByState() {
        startInstances(1);
        Assert.assertEquals(2, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceState(ACTIVE).list().size());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceState(AVAILABLE).list().size());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceState(ENABLED).list().size());
    }

    @Test
    public void testByPlanItemDefinitionType() {
        startInstances(3);
        Assert.assertEquals(6, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceDefinitionType(HUMAN_TASK).list().size());
        Assert.assertEquals(6, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceDefinitionType(STAGE).list().size());
    }

    @Test
    public void testByPlanItemDefinitionTypes() {
        startInstances(2);
        Assert.assertEquals(8, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceDefinitionTypes(Arrays.asList(STAGE, HUMAN_TASK)).list().size());
    }

    @Test
    public void testByStateAndType() {
        startInstances(3);
        Assert.assertEquals(3, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceState(ACTIVE).planItemInstanceDefinitionType(HUMAN_TASK).list().size());
        Assert.assertEquals(3, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceState(ENABLED).planItemInstanceDefinitionType(HUMAN_TASK).list().size());
    }
}

