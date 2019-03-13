/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapreduce.jobhistory;


import EventType.JOB_KILLED;
import EventType.JOB_PRIORITY_CHANGED;
import EventType.JOB_STATUS_CHANGED;
import EventType.REDUCE_ATTEMPT_FINISHED;
import EventType.REDUCE_ATTEMPT_KILLED;
import EventType.REDUCE_ATTEMPT_STARTED;
import EventType.TASK_UPDATED;
import JobPriority.LOW;
import TaskType.REDUCE;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Set;
import org.apache.hadoop.mapred.JobPriority;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEvent;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineMetric;
import org.junit.Assert;
import org.junit.Test;


public class TestEvents {
    private static final String taskId = "task_1_2_r_3";

    /**
     * test a getters of TaskAttemptFinishedEvent and TaskAttemptFinished
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 10000)
    public void testTaskAttemptFinishedEvent() throws Exception {
        JobID jid = new JobID("001", 1);
        TaskID tid = new TaskID(jid, TaskType.REDUCE, 2);
        TaskAttemptID taskAttemptId = new TaskAttemptID(tid, 3);
        Counters counters = new Counters();
        TaskAttemptFinishedEvent test = new TaskAttemptFinishedEvent(taskAttemptId, TaskType.REDUCE, "TEST", 123L, "RAKNAME", "HOSTNAME", "STATUS", counters, 234);
        Assert.assertEquals(test.getAttemptId().toString(), taskAttemptId.toString());
        Assert.assertEquals(test.getCounters(), counters);
        Assert.assertEquals(test.getFinishTime(), 123L);
        Assert.assertEquals(test.getHostname(), "HOSTNAME");
        Assert.assertEquals(test.getRackName(), "RAKNAME");
        Assert.assertEquals(test.getState(), "STATUS");
        Assert.assertEquals(test.getTaskId(), tid);
        Assert.assertEquals(test.getTaskStatus(), "TEST");
        Assert.assertEquals(test.getTaskType(), REDUCE);
        Assert.assertEquals(234, test.getStartTime());
    }

    /**
     * simple test JobPriorityChangeEvent and JobPriorityChange
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 10000)
    public void testJobPriorityChange() throws Exception {
        org.apache.hadoop.mapreduce.JobID jid = new JobID("001", 1);
        JobPriorityChangeEvent test = new JobPriorityChangeEvent(jid, JobPriority.LOW);
        Assert.assertEquals(test.getJobId().toString(), jid.toString());
        Assert.assertEquals(test.getPriority(), LOW);
    }

    @Test(timeout = 10000)
    public void testJobQueueChange() throws Exception {
        org.apache.hadoop.mapreduce.JobID jid = new JobID("001", 1);
        JobQueueChangeEvent test = new JobQueueChangeEvent(jid, "newqueue");
        Assert.assertEquals(test.getJobId().toString(), jid.toString());
        Assert.assertEquals(test.getJobQueueName(), "newqueue");
    }

    /**
     * simple test TaskUpdatedEvent and TaskUpdated
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 10000)
    public void testTaskUpdated() throws Exception {
        JobID jid = new JobID("001", 1);
        TaskID tid = new TaskID(jid, TaskType.REDUCE, 2);
        TaskUpdatedEvent test = new TaskUpdatedEvent(tid, 1234L);
        Assert.assertEquals(test.getTaskId().toString(), tid.toString());
        Assert.assertEquals(test.getFinishTime(), 1234L);
    }

    /* test EventReader EventReader should read the list of events and return
    instance of HistoryEvent Different HistoryEvent should have a different
    datum.
     */
    @Test(timeout = 10000)
    public void testEvents() throws Exception {
        EventReader reader = new EventReader(new DataInputStream(new ByteArrayInputStream(getEvents())));
        HistoryEvent e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(JOB_PRIORITY_CHANGED));
        Assert.assertEquals("ID", getJobid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(JOB_STATUS_CHANGED));
        Assert.assertEquals("ID", getJobid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(TASK_UPDATED));
        Assert.assertEquals("ID", getTaskid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(REDUCE_ATTEMPT_KILLED));
        Assert.assertEquals(TestEvents.taskId, getTaskid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(JOB_KILLED));
        Assert.assertEquals("ID", getJobid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(REDUCE_ATTEMPT_STARTED));
        Assert.assertEquals(TestEvents.taskId, getTaskid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(REDUCE_ATTEMPT_FINISHED));
        Assert.assertEquals(TestEvents.taskId, getTaskid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(REDUCE_ATTEMPT_KILLED));
        Assert.assertEquals(TestEvents.taskId, getTaskid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(REDUCE_ATTEMPT_KILLED));
        Assert.assertEquals(TestEvents.taskId, getTaskid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(REDUCE_ATTEMPT_STARTED));
        Assert.assertEquals(TestEvents.taskId, getTaskid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(REDUCE_ATTEMPT_FINISHED));
        Assert.assertEquals(TestEvents.taskId, getTaskid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(REDUCE_ATTEMPT_KILLED));
        Assert.assertEquals(TestEvents.taskId, getTaskid().toString());
        e = reader.getNextEvent();
        Assert.assertTrue(e.getEventType().equals(REDUCE_ATTEMPT_KILLED));
        Assert.assertEquals(TestEvents.taskId, getTaskid().toString());
        reader.close();
    }

    private class FakeEvent implements HistoryEvent {
        private EventType eventType;

        private Object datum;

        public FakeEvent(EventType eventType) {
            this.eventType = eventType;
        }

        @Override
        public EventType getEventType() {
            return eventType;
        }

        @Override
        public Object getDatum() {
            return datum;
        }

        @Override
        public void setDatum(Object datum) {
            this.datum = datum;
        }

        @Override
        public TimelineEvent toTimelineEvent() {
            return null;
        }

        @Override
        public Set<TimelineMetric> getTimelineMetrics() {
            return null;
        }
    }
}

