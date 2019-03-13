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
package org.apache.hadoop.mapreduce.task.reduce;


import java.io.IOException;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.TaskAttemptID;
import org.apache.hadoop.mapred.TaskCompletionEvent;
import org.apache.hadoop.mapred.TaskUmbilicalProtocol;
import org.apache.hadoop.mapreduce.TaskType;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class TestEventFetcher {
    @Test
    public void testConsecutiveFetch() throws IOException, InterruptedException {
        final int MAX_EVENTS_TO_FETCH = 100;
        TaskAttemptID tid = new TaskAttemptID("12345", 1, TaskType.REDUCE, 1, 1);
        TaskUmbilicalProtocol umbilical = Mockito.mock(TaskUmbilicalProtocol.class);
        Mockito.when(umbilical.getMapCompletionEvents(ArgumentMatchers.any(JobID.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(TaskAttemptID.class))).thenReturn(getMockedCompletionEventsUpdate(0, 0));
        Mockito.when(umbilical.getMapCompletionEvents(ArgumentMatchers.any(JobID.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(MAX_EVENTS_TO_FETCH), ArgumentMatchers.eq(tid))).thenReturn(getMockedCompletionEventsUpdate(0, MAX_EVENTS_TO_FETCH));
        Mockito.when(umbilical.getMapCompletionEvents(ArgumentMatchers.any(JobID.class), ArgumentMatchers.eq(MAX_EVENTS_TO_FETCH), ArgumentMatchers.eq(MAX_EVENTS_TO_FETCH), ArgumentMatchers.eq(tid))).thenReturn(getMockedCompletionEventsUpdate(MAX_EVENTS_TO_FETCH, MAX_EVENTS_TO_FETCH));
        Mockito.when(umbilical.getMapCompletionEvents(ArgumentMatchers.any(JobID.class), ArgumentMatchers.eq((MAX_EVENTS_TO_FETCH * 2)), ArgumentMatchers.eq(MAX_EVENTS_TO_FETCH), ArgumentMatchers.eq(tid))).thenReturn(getMockedCompletionEventsUpdate((MAX_EVENTS_TO_FETCH * 2), 3));
        @SuppressWarnings("unchecked")
        ShuffleScheduler<String, String> scheduler = Mockito.mock(ShuffleScheduler.class);
        ExceptionReporter reporter = Mockito.mock(ExceptionReporter.class);
        TestEventFetcher.EventFetcherForTest<String, String> ef = new TestEventFetcher.EventFetcherForTest<String, String>(tid, umbilical, scheduler, reporter, MAX_EVENTS_TO_FETCH);
        ef.getMapCompletionEvents();
        Mockito.verify(reporter, Mockito.never()).reportException(ArgumentMatchers.any(Throwable.class));
        InOrder inOrder = Mockito.inOrder(umbilical);
        inOrder.verify(umbilical).getMapCompletionEvents(ArgumentMatchers.any(JobID.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(MAX_EVENTS_TO_FETCH), ArgumentMatchers.eq(tid));
        inOrder.verify(umbilical).getMapCompletionEvents(ArgumentMatchers.any(JobID.class), ArgumentMatchers.eq(MAX_EVENTS_TO_FETCH), ArgumentMatchers.eq(MAX_EVENTS_TO_FETCH), ArgumentMatchers.eq(tid));
        inOrder.verify(umbilical).getMapCompletionEvents(ArgumentMatchers.any(JobID.class), ArgumentMatchers.eq((MAX_EVENTS_TO_FETCH * 2)), ArgumentMatchers.eq(MAX_EVENTS_TO_FETCH), ArgumentMatchers.eq(tid));
        Mockito.verify(scheduler, Mockito.times(((MAX_EVENTS_TO_FETCH * 2) + 3))).resolve(ArgumentMatchers.any(TaskCompletionEvent.class));
    }

    private static class EventFetcherForTest<K, V> extends EventFetcher<K, V> {
        public EventFetcherForTest(TaskAttemptID reduce, TaskUmbilicalProtocol umbilical, ShuffleScheduler<K, V> scheduler, ExceptionReporter reporter, int maxEventsToFetch) {
            super(reduce, umbilical, scheduler, reporter, maxEventsToFetch);
        }

        @Override
        public int getMapCompletionEvents() throws IOException, InterruptedException {
            return super.getMapCompletionEvents();
        }
    }
}

