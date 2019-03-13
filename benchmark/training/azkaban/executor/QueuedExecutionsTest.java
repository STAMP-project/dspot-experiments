/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.executor;


import azkaban.utils.Pair;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class QueuedExecutionsTest {
    /* Test enqueue method happy case */
    @Test
    public void testEnqueueHappyCase() throws ExecutorManagerException, IOException {
        final QueuedExecutions queue = new QueuedExecutions(5);
        final List<Pair<ExecutionReference, ExecutableFlow>> dataList = getDummyData();
        for (final Pair<ExecutionReference, ExecutableFlow> pair : dataList) {
            queue.enqueue(pair.getSecond(), pair.getFirst());
        }
        Assert.assertTrue(queue.getAllEntries().containsAll(dataList));
        Assert.assertTrue(dataList.containsAll(queue.getAllEntries()));
    }

    /* Test enqueue duplicate execution ids */
    @Test(expected = ExecutorManagerException.class)
    public void testEnqueueDuplicateExecution() throws ExecutorManagerException, IOException {
        final Pair<ExecutionReference, ExecutableFlow> pair1 = createExecutablePair("exec1", 1);
        final QueuedExecutions queue = new QueuedExecutions(5);
        queue.enqueue(pair1.getSecond(), pair1.getFirst());
        queue.enqueue(pair1.getSecond(), pair1.getFirst());
    }

    /* Test enqueue more than capacity */
    @Test(expected = ExecutorManagerException.class)
    public void testEnqueueOverflow() throws ExecutorManagerException, IOException {
        final Pair<ExecutionReference, ExecutableFlow> pair1 = createExecutablePair("exec1", 1);
        final QueuedExecutions queue = new QueuedExecutions(1);
        queue.enqueue(pair1.getSecond(), pair1.getFirst());
        queue.enqueue(pair1.getSecond(), pair1.getFirst());
    }

    /* Test EnqueueAll method */
    @Test
    public void testEnqueueAll() throws ExecutorManagerException, IOException {
        final QueuedExecutions queue = new QueuedExecutions(5);
        final List<Pair<ExecutionReference, ExecutableFlow>> dataList = getDummyData();
        queue.enqueueAll(dataList);
        Assert.assertTrue(queue.getAllEntries().containsAll(dataList));
        Assert.assertTrue(dataList.containsAll(queue.getAllEntries()));
    }

    /* Test size method */
    @Test
    public void testSize() throws ExecutorManagerException, IOException {
        final QueuedExecutions queue = new QueuedExecutions(5);
        final List<Pair<ExecutionReference, ExecutableFlow>> dataList = getDummyData();
        queue.enqueueAll(dataList);
        Assert.assertEquals(queue.size(), 2);
    }

    /* Test dequeue method */
    @Test
    public void testDequeue() throws ExecutorManagerException, IOException {
        final QueuedExecutions queue = new QueuedExecutions(5);
        final List<Pair<ExecutionReference, ExecutableFlow>> dataList = getDummyData();
        queue.enqueueAll(dataList);
        queue.dequeue(dataList.get(0).getFirst().getExecId());
        Assert.assertEquals(queue.size(), 1);
        Assert.assertTrue(queue.getAllEntries().contains(dataList.get(1)));
    }

    /* Test clear method */
    @Test
    public void testClear() throws ExecutorManagerException, IOException {
        final QueuedExecutions queue = new QueuedExecutions(5);
        final List<Pair<ExecutionReference, ExecutableFlow>> dataList = getDummyData();
        queue.enqueueAll(dataList);
        Assert.assertEquals(queue.size(), 2);
        queue.clear();
        Assert.assertEquals(queue.size(), 0);
    }

    /* Test isEmpty method */
    @Test
    public void testIsEmpty() throws ExecutorManagerException, IOException {
        final QueuedExecutions queue = new QueuedExecutions(5);
        final List<Pair<ExecutionReference, ExecutableFlow>> dataList = getDummyData();
        Assert.assertTrue(queue.isEmpty());
        queue.enqueueAll(dataList);
        Assert.assertEquals(queue.size(), 2);
        queue.clear();
        Assert.assertTrue(queue.isEmpty());
    }

    /* Test fetchHead method */
    @Test
    public void testFetchHead() throws ExecutorManagerException, IOException, InterruptedException {
        final QueuedExecutions queue = new QueuedExecutions(5);
        final List<Pair<ExecutionReference, ExecutableFlow>> dataList = getDummyData();
        Assert.assertTrue(queue.isEmpty());
        queue.enqueueAll(dataList);
        Assert.assertEquals(queue.fetchHead(), dataList.get(0));
        Assert.assertEquals(queue.fetchHead(), dataList.get(1));
    }

    /* Test isFull method */
    @Test
    public void testIsFull() throws ExecutorManagerException, IOException, InterruptedException {
        final QueuedExecutions queue = new QueuedExecutions(2);
        final List<Pair<ExecutionReference, ExecutableFlow>> dataList = getDummyData();
        queue.enqueueAll(dataList);
        Assert.assertTrue(queue.isFull());
    }

    /* Test hasExecution method */
    @Test
    public void testHasExecution() throws ExecutorManagerException, IOException, InterruptedException {
        final QueuedExecutions queue = new QueuedExecutions(2);
        final List<Pair<ExecutionReference, ExecutableFlow>> dataList = getDummyData();
        queue.enqueueAll(dataList);
        for (final Pair<ExecutionReference, ExecutableFlow> pair : dataList) {
            Assert.assertTrue(queue.hasExecution(pair.getFirst().getExecId()));
        }
        Assert.assertFalse(queue.hasExecution(5));
        Assert.assertFalse(queue.hasExecution(7));
        Assert.assertFalse(queue.hasExecution(15));
    }

    /* Test getFlow method */
    @Test
    public void testGetFlow() throws ExecutorManagerException, IOException, InterruptedException {
        final QueuedExecutions queue = new QueuedExecutions(2);
        final List<Pair<ExecutionReference, ExecutableFlow>> dataList = getDummyData();
        queue.enqueueAll(dataList);
        for (final Pair<ExecutionReference, ExecutableFlow> pair : dataList) {
            Assert.assertEquals(pair.getSecond(), queue.getFlow(pair.getFirst().getExecId()));
        }
    }

    /* Test getReferences method */
    @Test
    public void testGetReferences() throws ExecutorManagerException, IOException, InterruptedException {
        final QueuedExecutions queue = new QueuedExecutions(2);
        final List<Pair<ExecutionReference, ExecutableFlow>> dataList = getDummyData();
        queue.enqueueAll(dataList);
        for (final Pair<ExecutionReference, ExecutableFlow> pair : dataList) {
            Assert.assertEquals(pair.getFirst(), queue.getReference(pair.getFirst().getExecId()));
        }
    }
}

