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


import java.text.SimpleDateFormat;
import java.util.List;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.Test;


/**
 * Tests for cub-tasks querying
 *
 * @author Ionut Paduraru
 * @see TaskQueryTest
 */
public class SubTaskQueryTest extends PluggableFlowableTestCase {
    private List<String> taskIds;

    /**
     * test for task inclusion/exclusion (no other filters, no sort)
     */
    @Test
    public void testQueryExcludeSubtasks() throws Exception {
        // query all tasks, including subtasks
        TaskQuery query = taskService.createTaskQuery();
        assertEquals(10, query.count());
        assertEquals(10, query.list().size());
        // query only parent tasks (exclude subtasks)
        query = taskService.createTaskQuery().excludeSubtasks();
        assertEquals(3, query.count());
        assertEquals(3, query.list().size());
    }

    /**
     * test for task inclusion/exclusion (no other filters, no sort)
     */
    @Test
    public void testQueryWithPagination() throws Exception {
        // query all tasks, including subtasks
        TaskQuery query = taskService.createTaskQuery();
        assertEquals(10, query.count());
        assertEquals(2, query.listPage(0, 2).size());
        // query only parent tasks (exclude subtasks)
        query = taskService.createTaskQuery().excludeSubtasks();
        assertEquals(3, query.count());
        assertEquals(1, query.listPage(0, 1).size());
    }

    /**
     * test for task inclusion/exclusion (no other filters, order by task assignee )
     */
    @Test
    public void testQueryExcludeSubtasksSorted() throws Exception {
        // query all tasks, including subtasks
        TaskQuery query = taskService.createTaskQuery().orderByTaskAssignee().asc();
        assertEquals(10, query.count());
        assertEquals(10, query.list().size());
        // query only parent tasks (exclude subtasks)
        query = taskService.createTaskQuery().excludeSubtasks().orderByTaskAssignee().desc();
        assertEquals(3, query.count());
        assertEquals(3, query.list().size());
    }

    /**
     * test for task inclusion/exclusion when additional filter is specified (like assignee), no order.
     */
    @Test
    public void testQueryByAssigneeExcludeSubtasks() throws Exception {
        // gonzo has 2 root tasks and 3+2 subtasks assigned
        // include subtasks
        TaskQuery query = taskService.createTaskQuery().taskAssignee("gonzo");
        assertEquals(7, query.count());
        assertEquals(7, query.list().size());
        // exclude subtasks
        query = taskService.createTaskQuery().taskAssignee("gonzo").excludeSubtasks();
        assertEquals(2, query.count());
        assertEquals(2, query.list().size());
        // kermit has no root tasks and no subtasks assigned
        // include subtasks
        query = taskService.createTaskQuery().taskAssignee("kermit");
        assertEquals(0, query.count());
        assertEquals(0, query.list().size());
        assertNull(query.singleResult());
        // exclude subtasks
        query = taskService.createTaskQuery().taskAssignee("kermit").excludeSubtasks();
        assertEquals(0, query.count());
        assertEquals(0, query.list().size());
        assertNull(query.singleResult());
    }

    /**
     * test for task inclusion/exclusion when additional filter is specified (like assignee), no order.
     */
    @Test
    public void testQueryByAssigneeExcludeSubtasksPaginated() throws Exception {
        // gonzo has 2 root tasks and 3+2 subtasks assigned
        // include subtasks
        TaskQuery query = taskService.createTaskQuery().taskAssignee("gonzo");
        assertEquals(7, query.count());
        assertEquals(2, query.listPage(0, 2).size());
        // exclude subtasks
        query = taskService.createTaskQuery().taskAssignee("gonzo").excludeSubtasks();
        assertEquals(2, query.count());
        assertEquals(1, query.listPage(0, 1).size());
        // kermit has no root tasks and no subtasks assigned
        // include subtasks
        query = taskService.createTaskQuery().taskAssignee("kermit");
        assertEquals(0, query.count());
        assertEquals(0, query.listPage(0, 2).size());
        assertNull(query.singleResult());
        // exclude subtasks
        query = taskService.createTaskQuery().taskAssignee("kermit").excludeSubtasks();
        assertEquals(0, query.count());
        assertEquals(0, query.listPage(0, 2).size());
        assertNull(query.singleResult());
    }

    /**
     * test for task inclusion/exclusion when additional filter is specified (like assignee), ordered.
     */
    @Test
    public void testQueryByAssigneeExcludeSubtasksOrdered() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        // gonzo has 2 root tasks and 3+2 subtasks assigned
        // include subtasks
        TaskQuery query = taskService.createTaskQuery().taskAssignee("gonzo").orderByTaskCreateTime().desc();
        assertEquals(7, query.count());
        assertEquals(7, query.list().size());
        assertEquals(sdf.parse("02/01/2009 01:01:01.000"), query.list().get(0).getCreateTime());
        // exclude subtasks
        query = taskService.createTaskQuery().taskAssignee("gonzo").excludeSubtasks().orderByTaskCreateTime().asc();
        assertEquals(2, query.count());
        assertEquals(2, query.list().size());
        assertEquals(sdf.parse("01/02/2008 02:02:02.000"), query.list().get(0).getCreateTime());
        assertEquals(sdf.parse("05/02/2008 02:02:02.000"), query.list().get(1).getCreateTime());
        // kermit has no root tasks and no subtasks assigned
        // include subtasks
        query = taskService.createTaskQuery().taskAssignee("kermit").orderByTaskCreateTime().asc();
        assertEquals(0, query.count());
        assertEquals(0, query.list().size());
        assertNull(query.singleResult());
        // exclude subtasks
        query = taskService.createTaskQuery().taskAssignee("kermit").excludeSubtasks().orderByTaskCreateTime().desc();
        assertEquals(0, query.count());
        assertEquals(0, query.list().size());
        assertNull(query.singleResult());
    }

    /**
     * test for task inclusion/exclusion when additional filter is specified (like assignee), ordered.
     */
    @Test
    public void testQueryByAssigneeExcludeSubtasksOrderedAndPaginated() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        // gonzo has 2 root tasks and 3+2 subtasks assigned
        // include subtasks
        TaskQuery query = taskService.createTaskQuery().taskAssignee("gonzo").orderByTaskCreateTime().asc();
        assertEquals(7, query.count());
        assertEquals(1, query.listPage(0, 1).size());
        assertEquals(sdf.parse("01/02/2008 02:02:02.000"), query.listPage(0, 1).get(0).getCreateTime());
        assertEquals(1, query.listPage(1, 1).size());
        assertEquals(sdf.parse("05/02/2008 02:02:02.000"), query.listPage(1, 1).get(0).getCreateTime());
        assertEquals(2, query.listPage(0, 2).size());
        assertEquals(sdf.parse("01/02/2008 02:02:02.000"), query.listPage(0, 2).get(0).getCreateTime());
        assertEquals(sdf.parse("05/02/2008 02:02:02.000"), query.listPage(0, 2).get(1).getCreateTime());
        // exclude subtasks
        query = taskService.createTaskQuery().taskAssignee("gonzo").excludeSubtasks().orderByTaskCreateTime().desc();
        assertEquals(2, query.count());
        assertEquals(1, query.listPage(1, 1).size());
        assertEquals(sdf.parse("01/02/2008 02:02:02.000"), query.listPage(1, 1).get(0).getCreateTime());
        assertEquals(1, query.listPage(0, 1).size());
        assertEquals(sdf.parse("05/02/2008 02:02:02.000"), query.listPage(0, 1).get(0).getCreateTime());
        // kermit has no root tasks and no subtasks assigned
        // include subtasks
        query = taskService.createTaskQuery().taskAssignee("kermit").orderByTaskCreateTime().asc();
        assertEquals(0, query.count());
        assertEquals(0, query.listPage(0, 2).size());
        assertNull(query.singleResult());
        // exclude subtasks
        query = taskService.createTaskQuery().taskAssignee("kermit").excludeSubtasks().orderByTaskCreateTime().desc();
        assertEquals(0, query.count());
        assertEquals(0, query.listPage(0, 2).size());
        assertNull(query.singleResult());
    }
}

