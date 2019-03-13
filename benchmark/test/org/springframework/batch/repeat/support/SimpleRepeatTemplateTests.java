/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.repeat.support;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatException;
import org.springframework.batch.repeat.RepeatListener;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.callback.NestedRepeatCallback;
import org.springframework.batch.repeat.context.RepeatContextSupport;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.listener.RepeatListenerSupport;
import org.springframework.batch.repeat.policy.CompletionPolicySupport;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;


/**
 *
 *
 * @author Dave Syer
 */
public class SimpleRepeatTemplateTests extends AbstractTradeBatchTests {
    RepeatTemplate template = getRepeatTemplate();

    int count = 0;

    @Test
    public void testExecute() throws Exception {
        template.iterate(new ItemReaderRepeatCallback(provider, processor));
        Assert.assertEquals(AbstractTradeBatchTests.NUMBER_OF_ITEMS, processor.count);
    }

    /**
     * Check that a dedicated TerminationPolicy can terminate the batch.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEarlyCompletionWithPolicy() throws Exception {
        template.setCompletionPolicy(new SimpleCompletionPolicy(2));
        template.iterate(new ItemReaderRepeatCallback(provider, processor));
        Assert.assertEquals(2, processor.count);
    }

    /**
     * Check that a dedicated TerminationPolicy can terminate the batch.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEarlyCompletionWithException() throws Exception {
        try {
            template.iterate(new RepeatCallback() {
                @Override
                public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                    (count)++;
                    throw new IllegalStateException("foo!");
                }
            });
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertEquals("foo!", e.getMessage());
        }
        Assert.assertEquals(1, count);
        Assert.assertTrue(("Too many attempts: " + (count)), ((count) <= 10));
    }

    /**
     * Check that the context is closed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testContextClosedOnNormalCompletion() throws Exception {
        final List<String> list = new ArrayList<>();
        final RepeatContext context = new RepeatContextSupport(null) {
            @Override
            public void close() {
                super.close();
                list.add("close");
            }
        };
        template.setCompletionPolicy(new CompletionPolicySupport() {
            @Override
            public RepeatContext start(RepeatContext c) {
                return context;
            }
        });
        template.iterate(new RepeatCallback() {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                (count)++;
                return RepeatStatus.continueIf(((count) < 1));
            }
        });
        Assert.assertEquals(1, count);
        Assert.assertEquals(1, list.size());
    }

    /**
     * Check that the context is closed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testContextClosedOnAbnormalCompletion() throws Exception {
        final List<String> list = new ArrayList<>();
        final RepeatContext context = new RepeatContextSupport(null) {
            @Override
            public void close() {
                super.close();
                list.add("close");
            }
        };
        template.setCompletionPolicy(new CompletionPolicySupport() {
            @Override
            public RepeatContext start(RepeatContext c) {
                return context;
            }
        });
        try {
            template.iterate(new RepeatCallback() {
                @Override
                public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                    (count)++;
                    throw new RuntimeException("foo");
                }
            });
        } catch (RuntimeException e) {
            Assert.assertEquals("foo", e.getMessage());
        }
        Assert.assertEquals(1, count);
        Assert.assertEquals(1, list.size());
    }

    /**
     * Check that the exception handler is called.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExceptionHandlerCalledOnAbnormalCompletion() throws Exception {
        final List<Throwable> list = new ArrayList<>();
        template.setExceptionHandler(new ExceptionHandler() {
            @Override
            public void handleException(RepeatContext context, Throwable throwable) throws RuntimeException {
                list.add(throwable);
                throw ((RuntimeException) (throwable));
            }
        });
        try {
            template.iterate(new RepeatCallback() {
                @Override
                public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                    (count)++;
                    throw new RuntimeException("foo");
                }
            });
        } catch (RuntimeException e) {
            Assert.assertEquals("foo", e.getMessage());
        }
        Assert.assertEquals(1, count);
        Assert.assertEquals(1, list.size());
    }

    /**
     * Check that a the context can be used to signal early completion.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEarlyCompletionWithContext() throws Exception {
        RepeatStatus result = template.iterate(new ItemReaderRepeatCallback<Trade>(provider, processor) {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                RepeatStatus result = super.doInIteration(context);
                if ((processor.count) >= 2) {
                    context.setCompleteOnly();
                    // If we return null the batch will terminate anyway
                    // without an exception...
                }
                return result;
            }
        });
        // 2 items were processed before completion signalled
        Assert.assertEquals(2, processor.count);
        // Not all items processed
        Assert.assertTrue(result.isContinuable());
    }

    /**
     * Check that a the context can be used to signal early completion.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEarlyCompletionWithContextTerminated() throws Exception {
        RepeatStatus result = template.iterate(new ItemReaderRepeatCallback<Trade>(provider, processor) {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                RepeatStatus result = super.doInIteration(context);
                if ((processor.count) >= 2) {
                    context.setTerminateOnly();
                    // If we return null the batch will terminate anyway
                    // without an exception...
                }
                return result;
            }
        });
        // 2 items were processed before completion signalled
        Assert.assertEquals(2, processor.count);
        // Not all items processed
        Assert.assertTrue(result.isContinuable());
    }

    @Test
    public void testNestedSession() throws Exception {
        RepeatTemplate outer = getRepeatTemplate();
        RepeatTemplate inner = getRepeatTemplate();
        outer.iterate(new NestedRepeatCallback(inner, new RepeatCallback() {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                (count)++;
                Assert.assertNotNull(context);
                Assert.assertNotSame("Nested batch should have new session", context, context.getParent());
                Assert.assertSame(context, RepeatSynchronizationManager.getContext());
                return RepeatStatus.FINISHED;
            }
        }) {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                (count)++;
                Assert.assertSame(context, RepeatSynchronizationManager.getContext());
                return super.doInIteration(context);
            }
        });
        Assert.assertEquals(2, count);
    }

    @Test
    public void testNestedSessionTerminatesBeforeIteration() throws Exception {
        RepeatTemplate outer = getRepeatTemplate();
        RepeatTemplate inner = getRepeatTemplate();
        outer.iterate(new NestedRepeatCallback(inner, new RepeatCallback() {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                (count)++;
                Assert.assertEquals(2, count);
                Assert.fail("Nested batch should not have been executed");
                return RepeatStatus.FINISHED;
            }
        }) {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                (count)++;
                context.setCompleteOnly();
                return super.doInIteration(context);
            }
        });
        Assert.assertEquals(1, count);
    }

    @Test
    public void testOuterContextPreserved() throws Exception {
        RepeatTemplate outer = getRepeatTemplate();
        outer.setCompletionPolicy(new SimpleCompletionPolicy(2));
        RepeatTemplate inner = getRepeatTemplate();
        outer.iterate(new NestedRepeatCallback(inner, new RepeatCallback() {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                (count)++;
                Assert.assertNotNull(context);
                Assert.assertNotSame("Nested batch should have new session", context, context.getParent());
                Assert.assertSame(context, RepeatSynchronizationManager.getContext());
                return RepeatStatus.FINISHED;
            }
        }) {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                (count)++;
                Assert.assertSame(context, RepeatSynchronizationManager.getContext());
                super.doInIteration(context);
                return RepeatStatus.CONTINUABLE;
            }
        });
        Assert.assertEquals(4, count);
    }

    /**
     * Test that a result is returned from the batch.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testResult() throws Exception {
        RepeatStatus result = template.iterate(new ItemReaderRepeatCallback(provider, processor));
        Assert.assertEquals(AbstractTradeBatchTests.NUMBER_OF_ITEMS, processor.count);
        // We are complete - do not expect to be called again
        Assert.assertFalse(result.isContinuable());
    }

    @Test
    public void testExceptionThrownOnLastItem() throws Exception {
        template.setCompletionPolicy(new SimpleCompletionPolicy(2));
        try {
            template.iterate(new RepeatCallback() {
                @Override
                public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                    (count)++;
                    if ((count) < 2) {
                        return RepeatStatus.CONTINUABLE;
                    }
                    throw new RuntimeException(("Barf second try count=" + (count)));
                }
            });
            Assert.fail("Expected exception on last item in batch");
        } catch (Exception e) {
            // expected
            Assert.assertEquals("Barf second try count=2", e.getMessage());
        }
    }

    /**
     * Check that a the session can be used to signal early completion, but an
     * exception takes precedence.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEarlyCompletionWithSessionAndException() throws Exception {
        template.setCompletionPolicy(new SimpleCompletionPolicy(4));
        RepeatStatus result = RepeatStatus.FINISHED;
        try {
            result = template.iterate(new ItemReaderRepeatCallback<Trade>(provider, processor) {
                @Override
                public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                    RepeatStatus result = super.doInIteration(context);
                    if ((processor.count) >= 2) {
                        context.setCompleteOnly();
                        throw new RuntimeException(("Barf second try count=" + (processor.count)));
                    }
                    return result;
                }
            });
            Assert.fail("Expected exception on last item in batch");
        } catch (RuntimeException e) {
            // expected
            Assert.assertEquals("Barf second try count=2", e.getMessage());
        }
        // 2 items were processed before completion signalled
        Assert.assertEquals(2, processor.count);
        System.err.println(result);
        // An exception was thrown by the template so result is still false
        Assert.assertFalse(result.isContinuable());
    }

    /**
     * Checked exceptions are wrapped into runtime RepeatException.
     * RepeatException should be unwrapped before before it is passed to
     * listeners and exception handler.
     */
    @Test
    public void testExceptionUnwrapping() {
        @SuppressWarnings("serial")
        class TestException extends Exception {
            TestException(String msg) {
                super(msg);
            }
        }
        final TestException exception = new TestException("CRASH!");
        class ExceptionHandlerStub implements ExceptionHandler {
            boolean called = false;

            @Override
            public void handleException(RepeatContext context, Throwable throwable) throws Throwable {
                called = true;
                Assert.assertSame(exception, throwable);
                throw throwable;// re-throw so that repeat template

                // terminates iteration
            }
        }
        ExceptionHandlerStub exHandler = new ExceptionHandlerStub();
        class RepeatListenerStub extends RepeatListenerSupport {
            boolean called = false;

            @Override
            public void onError(RepeatContext context, Throwable throwable) {
                called = true;
                Assert.assertSame(exception, throwable);
            }
        }
        RepeatListenerStub listener = new RepeatListenerStub();
        template.setExceptionHandler(exHandler);
        template.setListeners(new RepeatListener[]{ listener });
        try {
            template.iterate(new RepeatCallback() {
                @Override
                public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                    throw new RepeatException("typically thrown by nested repeat template", exception);
                }
            });
            Assert.fail();
        } catch (RepeatException expected) {
            Assert.assertSame(exception, expected.getCause());
        }
        Assert.assertTrue(listener.called);
        Assert.assertTrue(exHandler.called);
    }
}

