/**
 * Copyright 2009-2013 the original author or authors.
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
package org.springframework.batch.core.step;


import BatchStatus.FAILED;
import BatchStatus.STOPPED;
import BatchStatus.UNKNOWN;
import ExitStatus.COMPLETED;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;


/**
 * Tests for {@link AbstractStep}.
 */
public class NonAbstractStepTests {
    AbstractStep tested = new NonAbstractStepTests.EventTrackingStep();

    StepExecutionListener listener1 = new NonAbstractStepTests.EventTrackingListener("listener1");

    StepExecutionListener listener2 = new NonAbstractStepTests.EventTrackingListener("listener2");

    NonAbstractStepTests.JobRepositoryStub repository = new NonAbstractStepTests.JobRepositoryStub();

    /**
     * Sequence of events encountered during step execution.
     */
    final List<String> events = new ArrayList<>();

    final StepExecution execution = new StepExecution(tested.getName(), new org.springframework.batch.core.JobExecution(new JobInstance(1L, "jobName"), new JobParameters()));

    /**
     * Fills the events list when abstract methods are called.
     */
    private class EventTrackingStep extends AbstractStep {
        public EventTrackingStep() {
            setBeanName("eventTrackingStep");
        }

        @Override
        protected void open(ExecutionContext ctx) throws Exception {
            events.add("open");
        }

        @Override
        protected void doExecute(StepExecution context) throws Exception {
            Assert.assertSame(execution, context);
            events.add("doExecute");
            context.setExitStatus(COMPLETED);
        }

        @Override
        protected void close(ExecutionContext ctx) throws Exception {
            events.add("close");
        }
    }

    /**
     * Fills the events list when listener methods are called, prefixed with the name of the listener.
     */
    private class EventTrackingListener implements StepExecutionListener {
        private String name;

        public EventTrackingListener(String name) {
            this.name = name;
        }

        private String getEvent(String event) {
            return ((name) + "#") + event;
        }

        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
            Assert.assertSame(execution, stepExecution);
            events.add(getEvent((("afterStep(" + (stepExecution.getExitStatus().getExitCode())) + ")")));
            stepExecution.getExecutionContext().putString("afterStep", "afterStep");
            return stepExecution.getExitStatus();
        }

        @Override
        public void beforeStep(StepExecution stepExecution) {
            Assert.assertSame(execution, stepExecution);
            events.add(getEvent("beforeStep"));
            stepExecution.getExecutionContext().putString("beforeStep", "beforeStep");
        }
    }

    /**
     * Remembers the last saved values of execution context.
     */
    private static class JobRepositoryStub extends JobRepositorySupport {
        ExecutionContext saved = new ExecutionContext();

        static long counter = 0;

        @Override
        public void updateExecutionContext(StepExecution stepExecution) {
            org.springframework.util.Assert.state(((stepExecution.getId()) != null), "StepExecution must already be saved");
            saved = stepExecution.getExecutionContext();
        }

        @Override
        public void add(StepExecution stepExecution) {
            if ((stepExecution.getId()) == null) {
                stepExecution.setId(NonAbstractStepTests.JobRepositoryStub.counter);
                (NonAbstractStepTests.JobRepositoryStub.counter)++;
            }
        }
    }

    @Test
    public void testBeanName() throws Exception {
        AbstractStep step = new AbstractStep() {
            @Override
            protected void doExecute(StepExecution stepExecution) throws Exception {
            }
        };
        Assert.assertNull(step.getName());
        step.setBeanName("foo");
        Assert.assertEquals("foo", step.getName());
    }

    @Test
    public void testName() throws Exception {
        AbstractStep step = new AbstractStep() {
            @Override
            protected void doExecute(StepExecution stepExecution) throws Exception {
            }
        };
        Assert.assertNull(step.getName());
        step.setName("foo");
        Assert.assertEquals("foo", step.getName());
        step.setBeanName("bar");
        Assert.assertEquals("foo", step.getName());
    }

    /**
     * Typical step execution scenario.
     */
    @Test
    public void testExecute() throws Exception {
        tested.setStepExecutionListeners(new StepExecutionListener[]{ listener1, listener2 });
        tested.execute(execution);
        int i = 0;
        Assert.assertEquals("listener1#beforeStep", events.get((i++)));
        Assert.assertEquals("listener2#beforeStep", events.get((i++)));
        Assert.assertEquals("open", events.get((i++)));
        Assert.assertEquals("doExecute", events.get((i++)));
        Assert.assertEquals("listener2#afterStep(COMPLETED)", events.get((i++)));
        Assert.assertEquals("listener1#afterStep(COMPLETED)", events.get((i++)));
        Assert.assertEquals("close", events.get((i++)));
        Assert.assertEquals(7, events.size());
        Assert.assertEquals(COMPLETED, execution.getExitStatus());
        Assert.assertTrue("Execution context modifications made by listener should be persisted", repository.saved.containsKey("beforeStep"));
        Assert.assertTrue("Execution context modifications made by listener should be persisted", repository.saved.containsKey("afterStep"));
    }

    @Test
    public void testFailure() throws Exception {
        tested = new NonAbstractStepTests.EventTrackingStep() {
            @Override
            protected void doExecute(StepExecution context) throws Exception {
                super.doExecute(context);
                throw new RuntimeException("crash!");
            }
        };
        tested.setJobRepository(repository);
        tested.setStepExecutionListeners(new StepExecutionListener[]{ listener1, listener2 });
        tested.execute(execution);
        Assert.assertEquals(FAILED, execution.getStatus());
        Throwable expected = execution.getFailureExceptions().get(0);
        Assert.assertEquals("crash!", expected.getMessage());
        int i = 0;
        Assert.assertEquals("listener1#beforeStep", events.get((i++)));
        Assert.assertEquals("listener2#beforeStep", events.get((i++)));
        Assert.assertEquals("open", events.get((i++)));
        Assert.assertEquals("doExecute", events.get((i++)));
        Assert.assertEquals("listener2#afterStep(FAILED)", events.get((i++)));
        Assert.assertEquals("listener1#afterStep(FAILED)", events.get((i++)));
        Assert.assertEquals("close", events.get((i++)));
        Assert.assertEquals(7, events.size());
        Assert.assertEquals(ExitStatus.FAILED.getExitCode(), execution.getExitStatus().getExitCode());
        String exitDescription = execution.getExitStatus().getExitDescription();
        Assert.assertTrue(("Wrong message: " + exitDescription), exitDescription.contains("crash"));
        Assert.assertTrue("Execution context modifications made by listener should be persisted", repository.saved.containsKey("afterStep"));
    }

    /**
     * Exception during business processing.
     */
    @Test
    public void testStoppedStep() throws Exception {
        tested = new NonAbstractStepTests.EventTrackingStep() {
            @Override
            protected void doExecute(StepExecution context) throws Exception {
                context.setTerminateOnly();
                super.doExecute(context);
            }
        };
        tested.setJobRepository(repository);
        tested.setStepExecutionListeners(new StepExecutionListener[]{ listener1, listener2 });
        tested.execute(execution);
        Assert.assertEquals(STOPPED, execution.getStatus());
        Throwable expected = execution.getFailureExceptions().get(0);
        Assert.assertEquals("JobExecution interrupted.", expected.getMessage());
        int i = 0;
        Assert.assertEquals("listener1#beforeStep", events.get((i++)));
        Assert.assertEquals("listener2#beforeStep", events.get((i++)));
        Assert.assertEquals("open", events.get((i++)));
        Assert.assertEquals("doExecute", events.get((i++)));
        Assert.assertEquals("listener2#afterStep(STOPPED)", events.get((i++)));
        Assert.assertEquals("listener1#afterStep(STOPPED)", events.get((i++)));
        Assert.assertEquals("close", events.get((i++)));
        Assert.assertEquals(7, events.size());
        Assert.assertEquals("STOPPED", execution.getExitStatus().getExitCode());
        Assert.assertTrue("Execution context modifications made by listener should be persisted", repository.saved.containsKey("afterStep"));
    }

    @Test
    public void testStoppedStepWithCustomStatus() throws Exception {
        tested = new NonAbstractStepTests.EventTrackingStep() {
            @Override
            protected void doExecute(StepExecution context) throws Exception {
                super.doExecute(context);
                context.setTerminateOnly();
                context.setExitStatus(new ExitStatus("FUNNY"));
            }
        };
        tested.setJobRepository(repository);
        tested.setStepExecutionListeners(new StepExecutionListener[]{ listener1, listener2 });
        tested.execute(execution);
        Assert.assertEquals(STOPPED, execution.getStatus());
        Throwable expected = execution.getFailureExceptions().get(0);
        Assert.assertEquals("JobExecution interrupted.", expected.getMessage());
        Assert.assertEquals("FUNNY", execution.getExitStatus().getExitCode());
        Assert.assertTrue("Execution context modifications made by listener should be persisted", repository.saved.containsKey("afterStep"));
    }

    /**
     * Exception during business processing.
     */
    @Test
    public void testFailureInSavingExecutionContext() throws Exception {
        tested = new NonAbstractStepTests.EventTrackingStep() {
            @Override
            protected void doExecute(StepExecution context) throws Exception {
                super.doExecute(context);
            }
        };
        repository = new NonAbstractStepTests.JobRepositoryStub() {
            @Override
            public void updateExecutionContext(StepExecution stepExecution) {
                throw new RuntimeException("Bad context!");
            }
        };
        tested.setJobRepository(repository);
        tested.execute(execution);
        Assert.assertEquals(UNKNOWN, execution.getStatus());
        Throwable expected = execution.getFailureExceptions().get(0);
        Assert.assertEquals("Bad context!", expected.getMessage());
        int i = 0;
        Assert.assertEquals("open", events.get((i++)));
        Assert.assertEquals("doExecute", events.get((i++)));
        Assert.assertEquals("close", events.get((i++)));
        Assert.assertEquals(3, events.size());
        Assert.assertEquals(ExitStatus.UNKNOWN, execution.getExitStatus());
    }

    /**
     * JobRepository is a required property.
     */
    @Test(expected = IllegalStateException.class)
    public void testAfterPropertiesSet() throws Exception {
        tested.setJobRepository(null);
        tested.afterPropertiesSet();
    }
}

