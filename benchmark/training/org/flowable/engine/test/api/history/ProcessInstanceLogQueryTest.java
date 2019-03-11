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
package org.flowable.engine.test.api.history;


import HistoryLevel.FULL;
import java.util.List;
import org.flowable.common.engine.api.history.HistoricData;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricVariableUpdate;
import org.flowable.engine.history.ProcessInstanceHistoryLog;
import org.flowable.engine.impl.test.HistoryTestHelper;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class ProcessInstanceLogQueryTest extends PluggableFlowableTestCase {
    protected String processInstanceId;

    @Test
    public void testBaseProperties() {
        ProcessInstanceHistoryLog log = historyService.createProcessInstanceHistoryLogQuery(processInstanceId).singleResult();
        assertNotNull(log.getId());
        assertNotNull(log.getProcessDefinitionId());
        assertNotNull(log.getStartActivityId());
        assertNotNull(log.getDurationInMillis());
        assertNotNull(log.getEndTime());
        assertNotNull(log.getStartTime());
    }

    @Test
    public void testIncludeTasks() {
        ProcessInstanceHistoryLog log = historyService.createProcessInstanceHistoryLogQuery(processInstanceId).includeTasks().singleResult();
        List<HistoricData> events = log.getHistoricData();
        assertEquals(2, events.size());
        for (HistoricData event : events) {
            assertTrue((event instanceof HistoricTaskInstance));
        }
    }

    @Test
    public void testIncludeComments() {
        ProcessInstanceHistoryLog log = historyService.createProcessInstanceHistoryLogQuery(processInstanceId).includeComments().singleResult();
        List<HistoricData> events = log.getHistoricData();
        assertEquals(3, events.size());
        for (HistoricData event : events) {
            assertTrue((event instanceof Comment));
        }
    }

    @Test
    public void testIncludeTasksAndComments() {
        ProcessInstanceHistoryLog log = historyService.createProcessInstanceHistoryLogQuery(processInstanceId).includeTasks().includeComments().singleResult();
        List<HistoricData> events = log.getHistoricData();
        assertEquals(5, events.size());
        int taskCounter = 0;
        int commentCounter = 0;
        for (int i = 0; i < 5; i++) {
            HistoricData event = events.get(i);
            if (event instanceof HistoricTaskInstance) {
                taskCounter++;
            } else
                if (event instanceof Comment) {
                    commentCounter++;
                }

        }
        assertEquals(2, taskCounter);
        assertEquals(3, commentCounter);
    }

    @Test
    public void testIncludeActivities() {
        ProcessInstanceHistoryLog log = historyService.createProcessInstanceHistoryLogQuery(processInstanceId).includeActivities().singleResult();
        List<HistoricData> events = log.getHistoricData();
        assertEquals(9, events.size());
        for (HistoricData event : events) {
            assertTrue((event instanceof HistoricActivityInstance));
        }
    }

    @Test
    public void testIncludeVariables() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(FULL, processEngineConfiguration)) {
            ProcessInstanceHistoryLog log = historyService.createProcessInstanceHistoryLogQuery(processInstanceId).includeVariables().singleResult();
            List<HistoricData> events = log.getHistoricData();
            assertEquals(2, events.size());
            for (HistoricData event : events) {
                assertTrue((event instanceof HistoricVariableInstance));
            }
        }
    }

    @Test
    public void testIncludeVariableUpdates() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(FULL, processEngineConfiguration)) {
            ProcessInstanceHistoryLog log = historyService.createProcessInstanceHistoryLogQuery(processInstanceId).includeVariableUpdates().singleResult();
            List<HistoricData> events = log.getHistoricData();
            assertEquals(3, events.size());
            for (HistoricData event : events) {
                assertTrue((event instanceof HistoricVariableUpdate));
            }
        }
    }

    @Test
    public void testEverything() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(FULL, processEngineConfiguration)) {
            ProcessInstanceHistoryLog log = historyService.createProcessInstanceHistoryLogQuery(processInstanceId).includeTasks().includeActivities().includeComments().includeVariables().includeVariableUpdates().singleResult();
            List<HistoricData> events = log.getHistoricData();
            assertEquals(19, events.size());
        }
    }
}

