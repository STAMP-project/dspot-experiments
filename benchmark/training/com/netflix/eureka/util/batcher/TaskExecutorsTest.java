/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.eureka.util.batcher;


import ProcessingResult.TransientError;
import com.netflix.eureka.util.batcher.TaskProcessor.ProcessingResult;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Tomasz Bak
 */
public class TaskExecutorsTest {
    @SuppressWarnings("unchecked")
    private final AcceptorExecutor<Integer, ProcessingResult> acceptorExecutor = Mockito.mock(AcceptorExecutor.class);

    private final RecordingProcessor processor = new RecordingProcessor();

    private final BlockingQueue<TaskHolder<Integer, ProcessingResult>> taskQueue = new LinkedBlockingDeque<>();

    private final BlockingQueue<List<TaskHolder<Integer, ProcessingResult>>> taskBatchQueue = new LinkedBlockingDeque<>();

    private TaskExecutors<Integer, ProcessingResult> taskExecutors;

    @Test
    public void testSingleItemSuccessfulProcessing() throws Exception {
        taskExecutors = TaskExecutors.singleItemExecutors("TEST", 1, processor, acceptorExecutor);
        taskQueue.add(RecordingProcessor.successfulTaskHolder(1));
        processor.expectSuccesses(1);
    }

    @Test
    public void testBatchSuccessfulProcessing() throws Exception {
        taskExecutors = TaskExecutors.batchExecutors("TEST", 1, processor, acceptorExecutor);
        taskBatchQueue.add(Arrays.asList(RecordingProcessor.successfulTaskHolder(1), RecordingProcessor.successfulTaskHolder(2)));
        processor.expectSuccesses(2);
    }

    @Test
    public void testSingleItemProcessingWithTransientError() throws Exception {
        taskExecutors = TaskExecutors.singleItemExecutors("TEST", 1, processor, acceptorExecutor);
        TaskHolder<Integer, ProcessingResult> taskHolder = RecordingProcessor.transientErrorTaskHolder(1);
        taskQueue.add(taskHolder);
        // Verify that transient task is be re-scheduled
        processor.expectTransientErrors(1);
        Mockito.verify(acceptorExecutor, Mockito.timeout(500).times(1)).reprocess(taskHolder, TransientError);
    }

    @Test
    public void testBatchProcessingWithTransientError() throws Exception {
        taskExecutors = TaskExecutors.batchExecutors("TEST", 1, processor, acceptorExecutor);
        List<TaskHolder<Integer, ProcessingResult>> taskHolderBatch = Arrays.asList(RecordingProcessor.transientErrorTaskHolder(1), RecordingProcessor.transientErrorTaskHolder(2));
        taskBatchQueue.add(taskHolderBatch);
        // Verify that transient task is be re-scheduled
        processor.expectTransientErrors(2);
        Mockito.verify(acceptorExecutor, Mockito.timeout(500).times(1)).reprocess(taskHolderBatch, TransientError);
    }

    @Test
    public void testSingleItemProcessingWithPermanentError() throws Exception {
        taskExecutors = TaskExecutors.singleItemExecutors("TEST", 1, processor, acceptorExecutor);
        TaskHolder<Integer, ProcessingResult> taskHolder = RecordingProcessor.permanentErrorTaskHolder(1);
        taskQueue.add(taskHolder);
        // Verify that transient task is re-scheduled
        processor.expectPermanentErrors(1);
        Mockito.verify(acceptorExecutor, Mockito.never()).reprocess(taskHolder, TransientError);
    }

    @Test
    public void testBatchProcessingWithPermanentError() throws Exception {
        taskExecutors = TaskExecutors.batchExecutors("TEST", 1, processor, acceptorExecutor);
        List<TaskHolder<Integer, ProcessingResult>> taskHolderBatch = Arrays.asList(RecordingProcessor.permanentErrorTaskHolder(1), RecordingProcessor.permanentErrorTaskHolder(2));
        taskBatchQueue.add(taskHolderBatch);
        // Verify that transient task is re-scheduled
        processor.expectPermanentErrors(2);
        Mockito.verify(acceptorExecutor, Mockito.never()).reprocess(taskHolderBatch, TransientError);
    }
}

