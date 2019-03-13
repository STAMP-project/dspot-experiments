/**
 * Copyright 2008-2014 the original author or authors.
 *
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
package org.springframework.batch.core.step.item;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.RetryException;
import org.springframework.retry.policy.SimpleRetryPolicy;


public class FaultTolerantChunkProcessorTests {
    private BatchRetryTemplate batchRetryTemplate;

    private List<String> list = new ArrayList<>();

    private List<String> after = new ArrayList<>();

    private List<String> writeError = new ArrayList<>();

    private FaultTolerantChunkProcessor<String, String> processor;

    private StepContribution contribution = createStepContribution();

    @Test
    public void testWrite() throws Exception {
        Chunk<String> inputs = new Chunk(Arrays.asList("1", "2"));
        processor.process(contribution, inputs);
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testTransform() throws Exception {
        processor.setItemProcessor(new org.springframework.batch.item.ItemProcessor<String, String>() {
            @Override
            public String process(String item) throws Exception {
                return item.equals("1") ? null : item;
            }
        });
        Chunk<String> inputs = new Chunk(Arrays.asList("1", "2"));
        processor.process(contribution, inputs);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(1, contribution.getFilterCount());
    }

    @Test
    public void testFilterCountOnSkip() throws Exception {
        processor.setProcessSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processor.setItemProcessor(new org.springframework.batch.item.ItemProcessor<String, String>() {
            @Override
            public String process(String item) throws Exception {
                if (item.equals("1")) {
                    throw new RuntimeException("Skippable");
                }
                if (item.equals("3")) {
                    return null;
                }
                return item;
            }
        });
        Chunk<String> inputs = new Chunk(Arrays.asList("3", "1", "2"));
        try {
            processor.process(contribution, inputs);
            Assert.fail("Expected Exception");
        } catch (Exception e) {
            Assert.assertEquals("Skippable", e.getMessage());
        }
        processor.process(contribution, inputs);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(1, contribution.getSkipCount());
        Assert.assertEquals(1, contribution.getFilterCount());
    }

    // BATCH-2663
    @Test
    public void testFilterCountOnSkipInWriteWithoutRetry() throws Exception {
        processor.setWriteSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processor.setItemProcessor(new org.springframework.batch.item.ItemProcessor<String, String>() {
            @Override
            public String process(String item) throws Exception {
                if (item.equals("1")) {
                    return null;
                }
                return item;
            }
        });
        Chunk<String> inputs = new Chunk(Arrays.asList("fail", "1", "2"));
        processAndExpectPlannedRuntimeException(inputs);// (first attempt) Process fail, 1, 2

        // item 1 is filtered out so it is removed from the chunk => now inputs = [fail, 2]
        // using NeverRetryPolicy by default => now scanning
        processAndExpectPlannedRuntimeException(inputs);// (scanning) Process fail

        processor.process(contribution, inputs);// (scanning) Process 2

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("[2]", list.toString());
        Assert.assertEquals(1, contribution.getWriteSkipCount());
        Assert.assertEquals(1, contribution.getFilterCount());
    }

    // BATCH-2663
    @Test
    public void testFilterCountOnSkipInWriteWithRetry() throws Exception {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        batchRetryTemplate.setRetryPolicy(retryPolicy);
        processor.setWriteSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processor.setItemProcessor(new org.springframework.batch.item.ItemProcessor<String, String>() {
            @Override
            public String process(String item) throws Exception {
                if (item.equals("1")) {
                    return null;
                }
                return item;
            }
        });
        Chunk<String> inputs = new Chunk(Arrays.asList("fail", "1", "2"));
        processAndExpectPlannedRuntimeException(inputs);// (first attempt) Process fail, 1, 2

        // item 1 is filtered out so it is removed from the chunk => now inputs = [fail, 2]
        processAndExpectPlannedRuntimeException(inputs);// (first retry) Process fail, 2

        processAndExpectPlannedRuntimeException(inputs);// (second retry) Process fail, 2

        // retry exhausted (maxAttempts = 3) => now scanning
        processAndExpectPlannedRuntimeException(inputs);// (scanning) Process fail

        processor.process(contribution, inputs);// (scanning) Process 2

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("[2]", list.toString());
        Assert.assertEquals(1, contribution.getWriteSkipCount());
        Assert.assertEquals(3, contribution.getFilterCount());
    }

    /**
     * An Error can be retried or skipped but by default it is just propagated
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWriteSkipOnError() throws Exception {
        processor.setWriteSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processor.setItemWriter(new org.springframework.batch.item.ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                if (items.contains("fail")) {
                    Assert.assertFalse("Expected Error!", true);
                }
            }
        });
        Chunk<String> inputs = new Chunk(Arrays.asList("3", "fail", "2"));
        try {
            processor.process(contribution, inputs);
            Assert.fail("Expected Error");
        } catch (Error e) {
            Assert.assertEquals("Expected Error!", e.getMessage());
        }
        processor.process(contribution, inputs);
    }

    @Test
    public void testWriteSkipOnException() throws Exception {
        processor.setWriteSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processor.setItemWriter(new org.springframework.batch.item.ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                if (items.contains("fail")) {
                    throw new RuntimeException("Expected Exception!");
                }
            }
        });
        Chunk<String> inputs = new Chunk(Arrays.asList("3", "fail", "2"));
        try {
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        processor.process(contribution, inputs);
        try {
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        Assert.assertEquals(1, contribution.getSkipCount());
        Assert.assertEquals(1, contribution.getWriteCount());
        Assert.assertEquals(0, contribution.getFilterCount());
    }

    @Test
    public void testWriteSkipOnExceptionWithTrivialChunk() throws Exception {
        processor.setWriteSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processor.setItemWriter(new org.springframework.batch.item.ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                if (items.contains("fail")) {
                    throw new RuntimeException("Expected Exception!");
                }
            }
        });
        Chunk<String> inputs = new Chunk(Arrays.asList("fail"));
        try {
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        // BATCH-1518: ideally we would not want this to be necessary, but it
        // still is...
        try {
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        processor.process(contribution, inputs);
        Assert.assertEquals(1, contribution.getSkipCount());
        Assert.assertEquals(0, contribution.getWriteCount());
        Assert.assertEquals(0, contribution.getFilterCount());
    }

    @Test
    public void testTransformWithExceptionAndNoRollback() throws Exception {
        processor.setItemProcessor(new org.springframework.batch.item.ItemProcessor<String, String>() {
            @Override
            public String process(String item) throws Exception {
                if (item.equals("1")) {
                    throw new DataIntegrityViolationException("Planned");
                }
                return item;
            }
        });
        processor.setProcessSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processor.setRollbackClassifier(new BinaryExceptionClassifier(Collections.<Class<? extends Throwable>>singleton(DataIntegrityViolationException.class), false));
        Chunk<String> inputs = new Chunk(Arrays.asList("1", "2"));
        processor.process(contribution, inputs);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testAfterWrite() throws Exception {
        Chunk<String> chunk = new Chunk(Arrays.asList("foo", "fail", "bar"));
        processor.setListeners(Arrays.asList(new org.springframework.batch.core.listener.ItemListenerSupport<String, String>() {
            @Override
            public void afterWrite(List<? extends String> item) {
                after.addAll(item);
            }
        }));
        processor.setWriteSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processAndExpectPlannedRuntimeException(chunk);
        processor.process(contribution, chunk);
        Assert.assertEquals(2, chunk.getItems().size());
        processAndExpectPlannedRuntimeException(chunk);
        Assert.assertEquals(1, chunk.getItems().size());
        processor.process(contribution, chunk);
        Assert.assertEquals(0, chunk.getItems().size());
        // foo is written once because it the failure is detected before it is
        // committed the first time
        Assert.assertEquals("[foo, bar]", list.toString());
        // the after listener is called once per successful item, which is
        // important
        Assert.assertEquals("[foo, bar]", after.toString());
    }

    @Test
    public void testAfterWriteAllPassedInRecovery() throws Exception {
        Chunk<String> chunk = new Chunk(Arrays.asList("foo", "bar"));
        processor = new FaultTolerantChunkProcessor(new org.springframework.batch.item.support.PassThroughItemProcessor(), new org.springframework.batch.item.ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                // Fail if there is more than one item
                if ((items.size()) > 1) {
                    throw new RuntimeException("Planned failure!");
                }
                list.addAll(items);
            }
        }, batchRetryTemplate);
        processor.setListeners(Arrays.asList(new org.springframework.batch.core.listener.ItemListenerSupport<String, String>() {
            @Override
            public void afterWrite(List<? extends String> item) {
                after.addAll(item);
            }
        }));
        processor.setWriteSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processAndExpectPlannedRuntimeException(chunk);
        processor.process(contribution, chunk);
        processor.process(contribution, chunk);
        Assert.assertEquals("[foo, bar]", list.toString());
        Assert.assertEquals("[foo, bar]", after.toString());
    }

    @Test
    public void testOnErrorInWrite() throws Exception {
        Chunk<String> chunk = new Chunk(Arrays.asList("foo", "fail"));
        processor.setListeners(Arrays.asList(new org.springframework.batch.core.listener.ItemListenerSupport<String, String>() {
            @Override
            public void onWriteError(Exception e, List<? extends String> item) {
                writeError.addAll(item);
            }
        }));
        processor.setWriteSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processAndExpectPlannedRuntimeException(chunk);// Process foo, fail

        processor.process(contribution, chunk);// Process foo

        processAndExpectPlannedRuntimeException(chunk);// Process fail

        Assert.assertEquals("[foo, fail, fail]", writeError.toString());
    }

    @Test
    public void testOnErrorInWriteAllItemsFail() throws Exception {
        Chunk<String> chunk = new Chunk(Arrays.asList("foo", "bar"));
        processor = new FaultTolerantChunkProcessor(new org.springframework.batch.item.support.PassThroughItemProcessor(), new org.springframework.batch.item.ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                // Always fail in writer
                throw new RuntimeException("Planned failure!");
            }
        }, batchRetryTemplate);
        processor.setListeners(Arrays.asList(new org.springframework.batch.core.listener.ItemListenerSupport<String, String>() {
            @Override
            public void onWriteError(Exception e, List<? extends String> item) {
                writeError.addAll(item);
            }
        }));
        processor.setWriteSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processAndExpectPlannedRuntimeException(chunk);// Process foo, bar

        processAndExpectPlannedRuntimeException(chunk);// Process foo

        processAndExpectPlannedRuntimeException(chunk);// Process bar

        Assert.assertEquals("[foo, bar, foo, bar]", writeError.toString());
    }

    @Test
    public void testWriteRetryOnException() throws Exception {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(2);
        batchRetryTemplate.setRetryPolicy(retryPolicy);
        processor.setWriteSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processor.setItemWriter(new org.springframework.batch.item.ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                if (items.contains("fail")) {
                    throw new IllegalArgumentException("Expected Exception!");
                }
            }
        });
        Chunk<String> inputs = new Chunk(Arrays.asList("3", "fail", "2"));
        try {
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        try {
            // first retry
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        // retry exhausted, now scanning
        processor.process(contribution, inputs);
        try {
            // skip on this attempt
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        // finish chunk
        processor.process(contribution, inputs);
        Assert.assertEquals(1, contribution.getSkipCount());
        Assert.assertEquals(2, contribution.getWriteCount());
        Assert.assertEquals(0, contribution.getFilterCount());
    }

    @Test
    public void testWriteRetryOnTwoExceptions() throws Exception {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(2);
        batchRetryTemplate.setRetryPolicy(retryPolicy);
        processor.setWriteSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processor.setItemWriter(new org.springframework.batch.item.ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                if (items.contains("fail")) {
                    throw new IllegalArgumentException("Expected Exception!");
                }
            }
        });
        Chunk<String> inputs = new Chunk(Arrays.asList("3", "fail", "fail", "4"));
        try {
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        try {
            // first retry
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        // retry exhausted, now scanning
        processor.process(contribution, inputs);
        try {
            // skip on this attempt
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        try {
            // 2nd exception detected
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        // still scanning
        processor.process(contribution, inputs);
        Assert.assertEquals(2, contribution.getSkipCount());
        Assert.assertEquals(2, contribution.getWriteCount());
        Assert.assertEquals(0, contribution.getFilterCount());
    }

    // BATCH-1804
    @Test
    public void testWriteRetryOnNonSkippableException() throws Exception {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(2);
        batchRetryTemplate.setRetryPolicy(retryPolicy);
        processor.setWriteSkipPolicy(new LimitCheckingItemSkipPolicy(1, Collections.<Class<? extends Throwable>, Boolean>singletonMap(IllegalArgumentException.class, true)));
        processor.setItemWriter(new org.springframework.batch.item.ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                if (items.contains("fail")) {
                    throw new IllegalArgumentException("Expected Exception!");
                }
                if (items.contains("2")) {
                    throw new RuntimeException("Expected Non-Skippable Exception!");
                }
            }
        });
        Chunk<String> inputs = new Chunk(Arrays.asList("3", "fail", "2"));
        try {
            processor.process(contribution, inputs);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        try {
            // first retry
            processor.process(contribution, inputs);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        // retry exhausted, now scanning
        processor.process(contribution, inputs);
        try {
            // skip on this attempt
            processor.process(contribution, inputs);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Expected Exception!", e.getMessage());
        }
        try {
            // should retry
            processor.process(contribution, inputs);
            Assert.fail("Expected RuntimeException");
        } catch (RetryException e) {
            throw e;
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected Non-Skippable Exception!", e.getMessage());
        }
        Assert.assertEquals(1, contribution.getSkipCount());
        Assert.assertEquals(1, contribution.getWriteCount());
        Assert.assertEquals(0, contribution.getFilterCount());
    }

    // BATCH-2036
    @Test
    public void testProcessFilterAndSkippableException() throws Exception {
        final List<String> processedItems = new ArrayList<>();
        processor.setProcessorTransactional(false);
        processor.setProcessSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processor.setItemProcessor(new org.springframework.batch.item.ItemProcessor<String, String>() {
            @Override
            public String process(String item) throws Exception {
                processedItems.add(item);
                if (item.contains("fail")) {
                    throw new IllegalArgumentException("Expected Skippable Exception!");
                }
                if (item.contains("skip")) {
                    return null;
                }
                return item;
            }
        });
        processor.afterPropertiesSet();
        Chunk<String> inputs = new Chunk(Arrays.asList("1", "2", "skip", "skip", "3", "fail", "fail", "4", "5"));
        try {
            processor.process(contribution, inputs);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Expected Skippable Exception!", e.getMessage());
        }
        try {
            processor.process(contribution, inputs);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Expected Skippable Exception!", e.getMessage());
        }
        processor.process(contribution, inputs);
        Assert.assertEquals(5, list.size());
        Assert.assertEquals("[1, 2, 3, 4, 5]", list.toString());
        Assert.assertEquals(2, contribution.getFilterCount());
        Assert.assertEquals(2, contribution.getProcessSkipCount());
        Assert.assertEquals(9, processedItems.size());
        Assert.assertEquals("[1, 2, skip, skip, 3, fail, fail, 4, 5]", processedItems.toString());
    }

    // BATCH-2036
    @Test
    public void testProcessFilterAndSkippableExceptionNoRollback() throws Exception {
        final List<String> processedItems = new ArrayList<>();
        processor.setProcessorTransactional(false);
        processor.setProcessSkipPolicy(new AlwaysSkipItemSkipPolicy());
        processor.setItemProcessor(new org.springframework.batch.item.ItemProcessor<String, String>() {
            @Override
            public String process(String item) throws Exception {
                processedItems.add(item);
                if (item.contains("fail")) {
                    throw new IllegalArgumentException("Expected Skippable Exception!");
                }
                if (item.contains("skip")) {
                    return null;
                }
                return item;
            }
        });
        processor.setRollbackClassifier(new BinaryExceptionClassifier(Collections.<Class<? extends Throwable>>singleton(IllegalArgumentException.class), false));
        processor.afterPropertiesSet();
        Chunk<String> inputs = new Chunk(Arrays.asList("1", "2", "skip", "skip", "3", "fail", "fail", "4", "5"));
        processor.process(contribution, inputs);
        Assert.assertEquals(5, list.size());
        Assert.assertEquals("[1, 2, 3, 4, 5]", list.toString());
        Assert.assertEquals(2, contribution.getFilterCount());
        Assert.assertEquals(2, contribution.getProcessSkipCount());
        Assert.assertEquals(9, processedItems.size());
        Assert.assertEquals("[1, 2, skip, skip, 3, fail, fail, 4, 5]", processedItems.toString());
    }
}

