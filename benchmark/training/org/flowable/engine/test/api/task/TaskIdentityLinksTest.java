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
package org.flowable.engine.test.api.task;


import Event.ACTION_ADD_USER_LINK;
import Event.ACTION_DELETE_GROUP_LINK;
import Event.ACTION_DELETE_USER_LINK;
import HistoryLevel.AUDIT;
import IdentityLinkType.ASSIGNEE;
import IdentityLinkType.OWNER;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.flowable.engine.impl.test.HistoryTestHelper;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.task.Event;
import org.flowable.engine.test.Deployment;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Tom Baeyens
 * @author Falko Menge
 */
public class TaskIdentityLinksTest extends PluggableFlowableTestCase {
    private static final String IDENTITY_LINKS_PROCESS_BPMN20_XML = "org/flowable/engine/test/api/task/IdentityLinksProcess.bpmn20.xml";

    private static final String IDENTITY_LINKS_PROCESS = "IdentityLinksProcess";

    @Test
    @Deployment(resources = "org/flowable/engine/test/api/task/IdentityLinksProcess.bpmn20.xml")
    public void testCandidateUserLink() {
        runtimeService.startProcessInstanceByKey("IdentityLinksProcess");
        String taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.addCandidateUser(taskId, "kermit");
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        IdentityLink identityLink = identityLinks.get(0);
        assertNull(identityLink.getGroupId());
        assertEquals("kermit", identityLink.getUserId());
        assertEquals(IdentityLinkType.CANDIDATE, identityLink.getType());
        assertEquals(taskId, identityLink.getTaskId());
        assertEquals(1, identityLinks.size());
        taskService.deleteCandidateUser(taskId, "kermit");
        assertEquals(0, taskService.getIdentityLinksForTask(taskId).size());
    }

    @Test
    @Deployment(resources = "org/flowable/engine/test/api/task/IdentityLinksProcess.bpmn20.xml")
    public void testCandidateGroupLink() {
        runtimeService.startProcessInstanceByKey("IdentityLinksProcess");
        String taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.addCandidateGroup(taskId, "muppets");
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        IdentityLink identityLink = identityLinks.get(0);
        assertEquals("muppets", identityLink.getGroupId());
        assertNull("kermit", identityLink.getUserId());
        assertEquals(IdentityLinkType.CANDIDATE, identityLink.getType());
        assertEquals(taskId, identityLink.getTaskId());
        assertEquals(1, identityLinks.size());
        if (HistoryTestHelper.isHistoryLevelAtLeast(AUDIT, processEngineConfiguration)) {
            List<Event> taskEvents = taskService.getTaskEvents(taskId);
            assertEquals(1, taskEvents.size());
            Event taskEvent = taskEvents.get(0);
            assertEquals(Event.ACTION_ADD_GROUP_LINK, taskEvent.getAction());
            List<String> taskEventMessageParts = taskEvent.getMessageParts();
            assertEquals("muppets", taskEventMessageParts.get(0));
            assertEquals(IdentityLinkType.CANDIDATE, taskEventMessageParts.get(1));
            assertEquals(2, taskEventMessageParts.size());
        }
        taskService.deleteCandidateGroup(taskId, "muppets");
        if (HistoryTestHelper.isHistoryLevelAtLeast(AUDIT, processEngineConfiguration)) {
            List<Event> taskEvents = taskService.getTaskEvents(taskId);
            Event taskEvent = findTaskEvent(taskEvents, ACTION_DELETE_GROUP_LINK);
            assertEquals(ACTION_DELETE_GROUP_LINK, taskEvent.getAction());
            List<String> taskEventMessageParts = taskEvent.getMessageParts();
            assertEquals("muppets", taskEventMessageParts.get(0));
            assertEquals(IdentityLinkType.CANDIDATE, taskEventMessageParts.get(1));
            assertEquals(2, taskEventMessageParts.size());
            assertEquals(2, taskEvents.size());
        }
        assertEquals(0, taskService.getIdentityLinksForTask(taskId).size());
    }

    @Test
    @Deployment(resources = TaskIdentityLinksTest.IDENTITY_LINKS_PROCESS_BPMN20_XML)
    public void testAssigneeIdentityLinkHistory() {
        if (!(HistoryTestHelper.isHistoryLevelAtLeast(AUDIT, processEngineConfiguration))) {
            return;
        }
        runtimeService.startProcessInstanceByKey(TaskIdentityLinksTest.IDENTITY_LINKS_PROCESS);
        String taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.setAssignee(taskId, "kermit");
        assertEquals(1, taskService.getIdentityLinksForTask(taskId).size());
        assertTaskEvent(taskId, 1, ACTION_ADD_USER_LINK, "kermit", ASSIGNEE);
        taskService.setAssignee(taskId, null);
        assertEquals(0, taskService.getIdentityLinksForTask(taskId).size());
        assertTaskEvent(taskId, 2, ACTION_DELETE_USER_LINK, "kermit", ASSIGNEE);
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);
        List<HistoricIdentityLink> history = historyService.getHistoricIdentityLinksForTask(taskId);
        assertEquals(2, history.size());
        Collections.sort(history, new Comparator<HistoricIdentityLink>() {
            @Override
            public int compare(HistoricIdentityLink hi1, HistoricIdentityLink hi2) {
                return hi1.getCreateTime().compareTo(hi2.getCreateTime());
            }
        });
        HistoricIdentityLink assigned = history.get(0);
        assertEquals(ASSIGNEE, assigned.getType());
        assertEquals("kermit", assigned.getUserId());
        HistoricIdentityLink unassigned = history.get(1);
        assertNull(unassigned.getUserId());
        assertEquals(ASSIGNEE, unassigned.getType());
        assertNull(unassigned.getUserId());
    }

    @Test
    @Deployment(resources = TaskIdentityLinksTest.IDENTITY_LINKS_PROCESS_BPMN20_XML)
    public void testClaimingIdentityLinkHistory() {
        if (!(HistoryTestHelper.isHistoryLevelAtLeast(AUDIT, processEngineConfiguration))) {
            return;
        }
        runtimeService.startProcessInstanceByKey(TaskIdentityLinksTest.IDENTITY_LINKS_PROCESS);
        String taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.claim(taskId, "kermit");
        assertEquals(1, taskService.getIdentityLinksForTask(taskId).size());
        assertTaskEvent(taskId, 1, ACTION_ADD_USER_LINK, "kermit", ASSIGNEE);
        taskService.unclaim(taskId);
        assertEquals(0, taskService.getIdentityLinksForTask(taskId).size());
        assertTaskEvent(taskId, 2, ACTION_DELETE_USER_LINK, "kermit", ASSIGNEE);
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);
        List<HistoricIdentityLink> history = historyService.getHistoricIdentityLinksForTask(taskId);
        assertEquals(2, history.size());
        Collections.sort(history, new Comparator<HistoricIdentityLink>() {
            @Override
            public int compare(HistoricIdentityLink hi1, HistoricIdentityLink hi2) {
                return hi1.getCreateTime().compareTo(hi2.getCreateTime());
            }
        });
        HistoricIdentityLink assigned = history.get(0);
        assertEquals(ASSIGNEE, assigned.getType());
        assertEquals("kermit", assigned.getUserId());
        HistoricIdentityLink unassigned = history.get(1);
        assertNull(unassigned.getUserId());
        assertEquals(ASSIGNEE, unassigned.getType());
        assertNull(unassigned.getUserId());
    }

    @Test
    @Deployment(resources = TaskIdentityLinksTest.IDENTITY_LINKS_PROCESS_BPMN20_XML)
    public void testOwnerIdentityLinkHistory() {
        if (!(HistoryTestHelper.isHistoryLevelAtLeast(AUDIT, processEngineConfiguration))) {
            return;
        }
        runtimeService.startProcessInstanceByKey(TaskIdentityLinksTest.IDENTITY_LINKS_PROCESS);
        String taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.setOwner(taskId, "kermit");
        assertEquals(1, taskService.getIdentityLinksForTask(taskId).size());
        assertTaskEvent(taskId, 1, ACTION_ADD_USER_LINK, "kermit", OWNER);
        taskService.setOwner(taskId, null);
        assertEquals(0, taskService.getIdentityLinksForTask(taskId).size());
        assertTaskEvent(taskId, 2, ACTION_DELETE_USER_LINK, "kermit", OWNER);
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);
        List<HistoricIdentityLink> history = historyService.getHistoricIdentityLinksForTask(taskId);
        assertEquals(2, history.size());
        Collections.sort(history, new Comparator<HistoricIdentityLink>() {
            @Override
            public int compare(HistoricIdentityLink hi1, HistoricIdentityLink hi2) {
                return hi1.getCreateTime().compareTo(hi2.getCreateTime());
            }
        });
        HistoricIdentityLink assigned = history.get(0);
        assertEquals(OWNER, assigned.getType());
        assertEquals("kermit", assigned.getUserId());
        HistoricIdentityLink unassigned = history.get(1);
        assertNull(unassigned.getUserId());
        assertEquals(OWNER, unassigned.getType());
        assertNull(unassigned.getUserId());
    }

    @Test
    @Deployment(resources = TaskIdentityLinksTest.IDENTITY_LINKS_PROCESS_BPMN20_XML)
    public void testUnchangedIdentityIdCreatesNoLinks() {
        if (!(HistoryTestHelper.isHistoryLevelAtLeast(AUDIT, processEngineConfiguration))) {
            return;
        }
        runtimeService.startProcessInstanceByKey(TaskIdentityLinksTest.IDENTITY_LINKS_PROCESS);
        String taskId = taskService.createTaskQuery().singleResult().getId();
        // two claims in succession, one comment
        taskService.claim(taskId, "kermit");
        taskService.setAssignee(taskId, "kermit");
        assertEquals(1, taskService.getIdentityLinksForTask(taskId).size());
        assertTaskEvent(taskId, 1, ACTION_ADD_USER_LINK, "kermit", ASSIGNEE);
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);
        List<HistoricIdentityLink> history = historyService.getHistoricIdentityLinksForTask(taskId);
        assertEquals(1, history.size());
        HistoricIdentityLink assigned = history.get(0);
        assertEquals(ASSIGNEE, assigned.getType());
        assertEquals("kermit", assigned.getUserId());
    }

    @Test
    @Deployment(resources = TaskIdentityLinksTest.IDENTITY_LINKS_PROCESS_BPMN20_XML)
    public void testNullIdentityIdCreatesNoLinks() {
        if (!(HistoryTestHelper.isHistoryLevelAtLeast(AUDIT, processEngineConfiguration))) {
            return;
        }
        runtimeService.startProcessInstanceByKey(TaskIdentityLinksTest.IDENTITY_LINKS_PROCESS);
        String taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.claim(taskId, null);
        taskService.setAssignee(taskId, null);
        assertEquals(0, taskService.getIdentityLinksForTask(taskId).size());
        assertTaskEvent(taskId, 0, null, null, null);
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);
        List<HistoricIdentityLink> history = historyService.getHistoricIdentityLinksForTask(taskId);
        assertEquals(0, history.size());
    }

    @Test
    @Deployment(resources = "org/flowable/engine/test/api/task/IdentityLinksProcess.bpmn20.xml")
    public void testCustomTypeUserLink() {
        runtimeService.startProcessInstanceByKey("IdentityLinksProcess");
        String taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.addUserIdentityLink(taskId, "kermit", "interestee");
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        IdentityLink identityLink = identityLinks.get(0);
        assertNull(identityLink.getGroupId());
        assertEquals("kermit", identityLink.getUserId());
        assertEquals("interestee", identityLink.getType());
        assertEquals(taskId, identityLink.getTaskId());
        assertEquals(1, identityLinks.size());
        taskService.deleteUserIdentityLink(taskId, "kermit", "interestee");
        assertEquals(0, taskService.getIdentityLinksForTask(taskId).size());
    }

    @Test
    @Deployment(resources = "org/flowable/engine/test/api/task/IdentityLinksProcess.bpmn20.xml")
    public void testCustomLinkGroupLink() {
        runtimeService.startProcessInstanceByKey("IdentityLinksProcess");
        String taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.addGroupIdentityLink(taskId, "muppets", "playing");
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        IdentityLink identityLink = identityLinks.get(0);
        assertEquals("muppets", identityLink.getGroupId());
        assertNull("kermit", identityLink.getUserId());
        assertEquals("playing", identityLink.getType());
        assertEquals(taskId, identityLink.getTaskId());
        assertEquals(1, identityLinks.size());
        taskService.deleteGroupIdentityLink(taskId, "muppets", "playing");
        assertEquals(0, taskService.getIdentityLinksForTask(taskId).size());
    }

    @Test
    public void testDeleteAssignee() {
        Task task = taskService.newTask();
        task.setAssignee("nonExistingUser");
        taskService.saveTask(task);
        taskService.deleteUserIdentityLink(task.getId(), "nonExistingUser", ASSIGNEE);
        task = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        assertNull(task.getAssignee());
        assertEquals(0, taskService.getIdentityLinksForTask(task.getId()).size());
        // cleanup
        taskService.deleteTask(task.getId(), true);
    }

    @Test
    public void testDeleteOwner() {
        Task task = taskService.newTask();
        task.setOwner("nonExistingUser");
        taskService.saveTask(task);
        taskService.deleteUserIdentityLink(task.getId(), "nonExistingUser", OWNER);
        task = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        assertNull(task.getOwner());
        assertEquals(0, taskService.getIdentityLinksForTask(task.getId()).size());
        // cleanup
        taskService.deleteTask(task.getId(), true);
    }

    @Test
    @Deployment(resources = "org/flowable/engine/test/api/task/TaskIdentityLinksTest.testDeleteCandidateUser.bpmn20.xml")
    public void testDeleteCandidateUser() {
        runtimeService.startProcessInstanceByKey("TaskIdentityLinks");
        String taskId = taskService.createTaskQuery().singleResult().getId();
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        assertEquals(1, identityLinks.size());
        IdentityLink identityLink = identityLinks.get(0);
        assertEquals("user", identityLink.getUserId());
    }

    @Test
    @Deployment(resources = "org/flowable/engine/test/api/task/IdentityLinksProcess.bpmn20.xml")
    public void testEmptyCandidateUserLink() {
        runtimeService.startProcessInstanceByKey("IdentityLinksProcess");
        String taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.addCandidateGroup(taskId, "muppets");
        taskService.deleteCandidateUser(taskId, "kermit");
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        assertNotNull(identityLinks);
        assertEquals(1, identityLinks.size());
        IdentityLink identityLink = identityLinks.get(0);
        assertEquals("muppets", identityLink.getGroupId());
        assertNull(identityLink.getUserId());
        assertEquals(IdentityLinkType.CANDIDATE, identityLink.getType());
        assertEquals(taskId, identityLink.getTaskId());
        taskService.deleteCandidateGroup(taskId, "muppets");
        assertEquals(0, taskService.getIdentityLinksForTask(taskId).size());
    }

    // Test custom identity links
    @Test
    @Deployment
    public void testCustomIdentityLink() {
        runtimeService.startProcessInstanceByKey("customIdentityLink");
        List<Task> tasks = taskService.createTaskQuery().taskInvolvedUser("kermit").list();
        assertEquals(1, tasks.size());
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(tasks.get(0).getId());
        assertEquals(2, identityLinks.size());
        for (IdentityLink idLink : identityLinks) {
            assertEquals("businessAdministrator", idLink.getType());
            String userId = idLink.getUserId();
            if (userId == null) {
                assertEquals("management", idLink.getGroupId());
            } else {
                assertEquals("kermit", userId);
            }
        }
    }
}

