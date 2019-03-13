/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.api.stomp;


import com.google.inject.Provider;
import java.util.Optional;
import org.apache.ambari.server.events.listeners.tasks.TaskStatusListener;
import org.junit.Assert;
import org.junit.Test;


public class NamedTasksSubscriptionsTest {
    private static final String SESSION_ID_1 = "fdsg3";

    private static final String SESSION_ID_2 = "idfg6";

    private NamedTasksSubscriptions tasksSubscriptions;

    private Provider<TaskStatusListener> taskStatusListenerProvider;

    private TaskStatusListener taskStatusListener;

    @Test
    public void testMatching() {
        Optional<Long> taskIdOpt = tasksSubscriptions.matchDestination("/events/tasks/1");
        Assert.assertTrue(taskIdOpt.isPresent());
        Assert.assertEquals(1L, taskIdOpt.get().longValue());
        Assert.assertFalse(tasksSubscriptions.matchDestination("/events/topologies").isPresent());
    }

    @Test
    public void testCheckId() {
        Assert.assertTrue(tasksSubscriptions.checkTaskId(1L));
        Assert.assertTrue(tasksSubscriptions.checkTaskId(4L));
        Assert.assertTrue(tasksSubscriptions.checkTaskId(5L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(2L));
    }

    @Test
    public void testRemoveBySessionId() {
        tasksSubscriptions.removeSession(NamedTasksSubscriptionsTest.SESSION_ID_1);
        Assert.assertTrue(tasksSubscriptions.checkTaskId(1L));
        Assert.assertTrue(tasksSubscriptions.checkTaskId(4L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(5L));
        tasksSubscriptions.removeSession(NamedTasksSubscriptionsTest.SESSION_ID_2);
        Assert.assertFalse(tasksSubscriptions.checkTaskId(1L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(4L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(5L));
    }

    @Test
    public void testRemoveById() {
        tasksSubscriptions.removeId(NamedTasksSubscriptionsTest.SESSION_ID_1, "sub-1");
        Assert.assertTrue(tasksSubscriptions.checkTaskId(1L));
        Assert.assertTrue(tasksSubscriptions.checkTaskId(4L));
        Assert.assertTrue(tasksSubscriptions.checkTaskId(5L));
        tasksSubscriptions.removeId(NamedTasksSubscriptionsTest.SESSION_ID_1, "sub-5");
        Assert.assertTrue(tasksSubscriptions.checkTaskId(1L));
        Assert.assertTrue(tasksSubscriptions.checkTaskId(4L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(5L));
        tasksSubscriptions.removeId(NamedTasksSubscriptionsTest.SESSION_ID_2, "sub-1");
        Assert.assertFalse(tasksSubscriptions.checkTaskId(1L));
        Assert.assertTrue(tasksSubscriptions.checkTaskId(4L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(5L));
        tasksSubscriptions.removeId(NamedTasksSubscriptionsTest.SESSION_ID_2, "sub-4");
        Assert.assertFalse(tasksSubscriptions.checkTaskId(1L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(4L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(5L));
    }

    @Test
    public void testAddDestination() {
        tasksSubscriptions = new NamedTasksSubscriptions(taskStatusListenerProvider);
        tasksSubscriptions.addDestination(NamedTasksSubscriptionsTest.SESSION_ID_1, "/events/tasks/1", "sub-1");
        Assert.assertTrue(tasksSubscriptions.checkTaskId(1L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(4L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(5L));
        tasksSubscriptions.addDestination(NamedTasksSubscriptionsTest.SESSION_ID_1, "/events/tasks/5", "sub-5");
        Assert.assertTrue(tasksSubscriptions.checkTaskId(1L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(4L));
        Assert.assertTrue(tasksSubscriptions.checkTaskId(5L));
        tasksSubscriptions.addDestination(NamedTasksSubscriptionsTest.SESSION_ID_2, "/events/tasks/1", "sub-1");
        Assert.assertTrue(tasksSubscriptions.checkTaskId(1L));
        Assert.assertFalse(tasksSubscriptions.checkTaskId(4L));
        Assert.assertTrue(tasksSubscriptions.checkTaskId(5L));
        tasksSubscriptions.addDestination(NamedTasksSubscriptionsTest.SESSION_ID_2, "/events/tasks/4", "sub-4");
        Assert.assertTrue(tasksSubscriptions.checkTaskId(1L));
        Assert.assertTrue(tasksSubscriptions.checkTaskId(4L));
        Assert.assertTrue(tasksSubscriptions.checkTaskId(5L));
    }
}

