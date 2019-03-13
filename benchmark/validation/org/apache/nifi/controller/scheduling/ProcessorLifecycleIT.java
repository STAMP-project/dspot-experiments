/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller.scheduling;


import NiFiProperties.PROCESSOR_SCHEDULING_TIMEOUT;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.lifecycle.OnStopped;
import org.apache.nifi.annotation.lifecycle.OnUnscheduled;
import org.apache.nifi.bundle.Bundle;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.components.Validator;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.controller.ProcessScheduler;
import org.apache.nifi.controller.ProcessorNode;
import org.apache.nifi.controller.ScheduledState;
import org.apache.nifi.controller.flow.FlowManager;
import org.apache.nifi.controller.service.ControllerServiceNode;
import org.apache.nifi.groups.ProcessGroup;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Validate Processor's life-cycle operation within the context of
 * {@link FlowController} and {@link StandardProcessScheduler}
 */
public class ProcessorLifecycleIT {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorLifecycleIT.class);

    private static final long SHORT_DELAY_TOLERANCE = 10000L;

    private static final long MEDIUM_DELAY_TOLERANCE = 15000L;

    private static final long LONG_DELAY_TOLERANCE = 20000L;

    private FlowManager flowManager;

    private Map<String, String> properties = new HashMap<>();

    private volatile String propsFile = "src/test/resources/lifecycletest.nifi.properties";

    private ProcessScheduler processScheduler;

    @Test
    public void validateEnableOperation() throws Exception {
        final ProcessorLifecycleIT.FlowManagerAndSystemBundle fcsb = this.buildFlowControllerForTest();
        flowManager = fcsb.getFlowManager();
        ProcessGroup testGroup = flowManager.createProcessGroup(UUID.randomUUID().toString());
        final ProcessorNode testProcNode = flowManager.createProcessor(ProcessorLifecycleIT.TestProcessor.class.getName(), UUID.randomUUID().toString(), fcsb.getSystemBundle().getBundleDetails().getCoordinate());
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getScheduledState()));
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getPhysicalScheduledState()));
        // validates idempotency
        for (int i = 0; i < 2; i++) {
            testProcNode.enable();
        }
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getScheduledState()));
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getPhysicalScheduledState()));
        testProcNode.disable();
        assertCondition(() -> (ScheduledState.DISABLED) == (testProcNode.getScheduledState()));
        assertCondition(() -> (ScheduledState.DISABLED) == (testProcNode.getPhysicalScheduledState()));
    }

    @Test
    public void validateDisableOperation() throws Exception {
        final ProcessorLifecycleIT.FlowManagerAndSystemBundle fcsb = this.buildFlowControllerForTest();
        flowManager = fcsb.getFlowManager();
        ProcessGroup testGroup = flowManager.createProcessGroup(UUID.randomUUID().toString());
        final ProcessorNode testProcNode = flowManager.createProcessor(ProcessorLifecycleIT.TestProcessor.class.getName(), UUID.randomUUID().toString(), fcsb.getSystemBundle().getBundleDetails().getCoordinate());
        testProcNode.setProperties(properties);
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getScheduledState()));
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getPhysicalScheduledState()));
        // validates idempotency
        for (int i = 0; i < 2; i++) {
            testProcNode.disable();
        }
        assertCondition(() -> (ScheduledState.DISABLED) == (testProcNode.getScheduledState()));
        assertCondition(() -> (ScheduledState.DISABLED) == (testProcNode.getPhysicalScheduledState()));
        testProcNode.performValidation();
        processScheduler.startProcessor(testProcNode, true);
        assertCondition(() -> (ScheduledState.DISABLED) == (testProcNode.getPhysicalScheduledState()));
    }

    /**
     * Will validate the idempotent nature of processor start operation which
     * can be called multiple times without any side-effects.
     */
    @Test
    public void validateIdempotencyOfProcessorStartOperation() throws Exception {
        final ProcessorLifecycleIT.FlowManagerAndSystemBundle fcsb = this.buildFlowControllerForTest();
        flowManager = fcsb.getFlowManager();
        ProcessGroup testGroup = flowManager.createProcessGroup(UUID.randomUUID().toString());
        final ProcessorNode testProcNode = flowManager.createProcessor(ProcessorLifecycleIT.TestProcessor.class.getName(), UUID.randomUUID().toString(), fcsb.getSystemBundle().getBundleDetails().getCoordinate());
        testProcNode.setProperties(properties);
        ProcessorLifecycleIT.TestProcessor testProcessor = ((ProcessorLifecycleIT.TestProcessor) (testProcNode.getProcessor()));
        // sets the scenario for the processor to run
        this.noop(testProcessor);
        testProcNode.performValidation();
        processScheduler.startProcessor(testProcNode, true);
        processScheduler.startProcessor(testProcNode, true);
        processScheduler.startProcessor(testProcNode, true);
        Thread.sleep(500);
        assertCondition(() -> (testProcessor.operationNames.size()) == 1);
        Assert.assertEquals("@OnScheduled", testProcessor.operationNames.get(0));
    }

    /**
     * Validates that stop calls are harmless and idempotent if processor is not
     * in STARTING or RUNNING state.
     */
    @Test
    public void validateStopCallsAreMeaninglessIfProcessorNotStarted() throws Exception {
        final ProcessorLifecycleIT.FlowManagerAndSystemBundle fcsb = this.buildFlowControllerForTest();
        flowManager = fcsb.getFlowManager();
        ProcessGroup testGroup = flowManager.createProcessGroup(UUID.randomUUID().toString());
        final ProcessorNode testProcNode = flowManager.createProcessor(ProcessorLifecycleIT.TestProcessor.class.getName(), UUID.randomUUID().toString(), fcsb.getSystemBundle().getBundleDetails().getCoordinate());
        testProcNode.setProperties(properties);
        ProcessorLifecycleIT.TestProcessor testProcessor = ((ProcessorLifecycleIT.TestProcessor) (testProcNode.getProcessor()));
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getScheduledState()));
        // sets the scenario for the processor to run
        int randomDelayLimit = 3000;
        this.randomOnTriggerDelay(testProcessor, randomDelayLimit);
        processScheduler.stopProcessor(testProcNode);
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getScheduledState()));
        Assert.assertTrue(testProcessor.operationNames.isEmpty());
    }

    /**
     * Validates that processor can be stopped before start sequence finished.
     */
    @Test
    public void validateProcessorUnscheduledAndStoppedWhenStopIsCalledBeforeProcessorFullyStarted() throws Exception {
        final ProcessorLifecycleIT.FlowManagerAndSystemBundle fcsb = this.buildFlowControllerForTest();
        flowManager = fcsb.getFlowManager();
        ProcessGroup testGroup = flowManager.createProcessGroup(UUID.randomUUID().toString());
        ProcessorNode testProcNode = flowManager.createProcessor(ProcessorLifecycleIT.TestProcessor.class.getName(), UUID.randomUUID().toString(), fcsb.getSystemBundle().getBundleDetails().getCoordinate());
        testProcNode.setProperties(properties);
        ProcessorLifecycleIT.TestProcessor testProcessor = ((ProcessorLifecycleIT.TestProcessor) (testProcNode.getProcessor()));
        // sets the scenario for the processor to run
        int delay = 200;
        this.longRunningOnSchedule(testProcessor, delay);
        testProcNode.performValidation();
        processScheduler.startProcessor(testProcNode, true);
        assertCondition(() -> (ScheduledState.RUNNING) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.MEDIUM_DELAY_TOLERANCE);
        processScheduler.stopProcessor(testProcNode);
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.MEDIUM_DELAY_TOLERANCE);
        assertCondition(() -> (testProcessor.operationNames.size()) == 3, ProcessorLifecycleIT.LONG_DELAY_TOLERANCE);
        Assert.assertEquals("@OnScheduled", testProcessor.operationNames.get(0));
        Assert.assertEquals("@OnUnscheduled", testProcessor.operationNames.get(1));
        Assert.assertEquals("@OnStopped", testProcessor.operationNames.get(2));
    }

    /**
     * Validates that Processor is eventually started once invocation of
     *
     * @unknown stopped throwing exceptions.
     */
    @Test
    public void validateProcessScheduledAfterAdministrativeDelayDueToTheOnScheduledException() throws Exception {
        final ProcessorLifecycleIT.FlowManagerAndSystemBundle fcsb = this.buildFlowControllerForTest();
        flowManager = fcsb.getFlowManager();
        ProcessGroup testGroup = flowManager.createProcessGroup(UUID.randomUUID().toString());
        ProcessorNode testProcNode = flowManager.createProcessor(ProcessorLifecycleIT.TestProcessor.class.getName(), UUID.randomUUID().toString(), fcsb.getSystemBundle().getBundleDetails().getCoordinate());
        testProcNode.setProperties(properties);
        ProcessorLifecycleIT.TestProcessor testProcessor = ((ProcessorLifecycleIT.TestProcessor) (testProcNode.getProcessor()));
        // sets the scenario for the processor to run
        this.noop(testProcessor);
        testProcessor.generateExceptionOnScheduled = true;
        testProcessor.keepFailingOnScheduledTimes = 2;
        testProcNode.performValidation();
        processScheduler.startProcessor(testProcNode, true);
        assertCondition(() -> (ScheduledState.RUNNING) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.LONG_DELAY_TOLERANCE);
        processScheduler.stopProcessor(testProcNode);
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.SHORT_DELAY_TOLERANCE);
    }

    /**
     * Validates that Processor can be stopped when @OnScheduled constantly
     * fails. Basically validates that the re-try loop breaks if user initiated
     * stopProcessor.
     */
    @Test
    public void validateProcessorCanBeStoppedWhenOnScheduledConstantlyFails() throws Exception {
        final ProcessorLifecycleIT.FlowManagerAndSystemBundle fcsb = this.buildFlowControllerForTest();
        flowManager = fcsb.getFlowManager();
        ProcessGroup testGroup = flowManager.createProcessGroup(UUID.randomUUID().toString());
        ProcessorNode testProcNode = flowManager.createProcessor(ProcessorLifecycleIT.TestProcessor.class.getName(), UUID.randomUUID().toString(), fcsb.getSystemBundle().getBundleDetails().getCoordinate());
        testProcNode.setProperties(properties);
        ProcessorLifecycleIT.TestProcessor testProcessor = ((ProcessorLifecycleIT.TestProcessor) (testProcNode.getProcessor()));
        // sets the scenario for the processor to run
        this.longRunningOnUnschedule(testProcessor, 100);
        testProcessor.generateExceptionOnScheduled = true;
        testProcessor.keepFailingOnScheduledTimes = Integer.MAX_VALUE;
        testProcNode.performValidation();
        processScheduler.startProcessor(testProcNode, true);
        assertCondition(() -> (ScheduledState.RUNNING) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.SHORT_DELAY_TOLERANCE);
        processScheduler.stopProcessor(testProcNode);
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.SHORT_DELAY_TOLERANCE);
    }

    /**
     * Validates that the Processor can be stopped when @OnScheduled blocks
     * indefinitely but written to react to thread interrupts
     */
    @Test
    public void validateProcessorCanBeStoppedWhenOnScheduledBlocksIndefinitelyInterruptable() throws Exception {
        final ProcessorLifecycleIT.FlowManagerAndSystemBundle fcsb = this.buildFlowControllerForTest(PROCESSOR_SCHEDULING_TIMEOUT, "5 sec");
        flowManager = fcsb.getFlowManager();
        ProcessGroup testGroup = flowManager.createProcessGroup(UUID.randomUUID().toString());
        ProcessorNode testProcNode = flowManager.createProcessor(ProcessorLifecycleIT.TestProcessor.class.getName(), UUID.randomUUID().toString(), fcsb.getSystemBundle().getBundleDetails().getCoordinate());
        testProcNode.setProperties(properties);
        ProcessorLifecycleIT.TestProcessor testProcessor = ((ProcessorLifecycleIT.TestProcessor) (testProcNode.getProcessor()));
        // sets the scenario for the processor to run
        this.blockingInterruptableOnUnschedule(testProcessor);
        testProcNode.performValidation();
        processScheduler.startProcessor(testProcNode, true);
        assertCondition(() -> (ScheduledState.RUNNING) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.SHORT_DELAY_TOLERANCE);
        processScheduler.stopProcessor(testProcNode);
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.MEDIUM_DELAY_TOLERANCE);
    }

    /**
     * Validates that the Processor can be stopped when @OnScheduled blocks
     * indefinitely and written to ignore thread interrupts
     */
    @Test
    public void validateProcessorCanBeStoppedWhenOnScheduledBlocksIndefinitelyUninterruptable() throws Exception {
        final ProcessorLifecycleIT.FlowManagerAndSystemBundle fcsb = this.buildFlowControllerForTest(PROCESSOR_SCHEDULING_TIMEOUT, "1 sec");
        flowManager = fcsb.getFlowManager();
        ProcessGroup testGroup = flowManager.createProcessGroup(UUID.randomUUID().toString());
        ProcessorNode testProcNode = flowManager.createProcessor(ProcessorLifecycleIT.TestProcessor.class.getName(), UUID.randomUUID().toString(), fcsb.getSystemBundle().getBundleDetails().getCoordinate());
        testProcNode.setProperties(properties);
        ProcessorLifecycleIT.TestProcessor testProcessor = ((ProcessorLifecycleIT.TestProcessor) (testProcNode.getProcessor()));
        // sets the scenario for the processor to run
        this.blockingUninterruptableOnUnschedule(testProcessor);
        testProcNode.performValidation();
        processScheduler.startProcessor(testProcNode, true);
        assertCondition(() -> (ScheduledState.RUNNING) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.MEDIUM_DELAY_TOLERANCE);
        processScheduler.stopProcessor(testProcNode);
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.MEDIUM_DELAY_TOLERANCE);
    }

    /**
     * Validates that processor can be stopped if onTrigger() keeps throwing
     * exceptions.
     */
    @Test
    public void validateProcessorCanBeStoppedWhenOnTriggerThrowsException() throws Exception {
        final ProcessorLifecycleIT.FlowManagerAndSystemBundle fcsb = this.buildFlowControllerForTest();
        flowManager = fcsb.getFlowManager();
        ProcessGroup testGroup = flowManager.createProcessGroup(UUID.randomUUID().toString());
        ProcessorNode testProcNode = flowManager.createProcessor(ProcessorLifecycleIT.TestProcessor.class.getName(), UUID.randomUUID().toString(), fcsb.getSystemBundle().getBundleDetails().getCoordinate());
        testProcNode.setProperties(properties);
        ProcessorLifecycleIT.TestProcessor testProcessor = ((ProcessorLifecycleIT.TestProcessor) (testProcNode.getProcessor()));
        // sets the scenario for the processor to run
        this.noop(testProcessor);
        testProcessor.generateExceptionOnTrigger = true;
        testProcNode.performValidation();
        processScheduler.startProcessor(testProcNode, true);
        assertCondition(() -> (ScheduledState.RUNNING) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.LONG_DELAY_TOLERANCE);
        processScheduler.disableProcessor(testProcNode);
        assertCondition(() -> (ScheduledState.RUNNING) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.LONG_DELAY_TOLERANCE);
        processScheduler.stopProcessor(testProcNode);
        assertCondition(() -> (ScheduledState.STOPPED) == (testProcNode.getScheduledState()), ProcessorLifecycleIT.LONG_DELAY_TOLERANCE);
    }

    /**
     * Validate that processor will not be validated on failing
     * ControllerService validation (not enabled).
     */
    @Test(expected = IllegalStateException.class)
    public void validateStartFailsOnInvalidProcessorWithDisabledService() throws Exception {
        final ProcessorLifecycleIT.FlowManagerAndSystemBundle fcsb = this.buildFlowControllerForTest();
        flowManager = fcsb.getFlowManager();
        ProcessGroup testGroup = flowManager.createProcessGroup(UUID.randomUUID().toString());
        ControllerServiceNode testServiceNode = flowManager.createControllerService(ProcessorLifecycleIT.TestService.class.getName(), "serv", fcsb.getSystemBundle().getBundleDetails().getCoordinate(), null, true, true);
        ProcessorNode testProcNode = flowManager.createProcessor(ProcessorLifecycleIT.TestProcessor.class.getName(), UUID.randomUUID().toString(), fcsb.getSystemBundle().getBundleDetails().getCoordinate());
        properties.put("S", testServiceNode.getIdentifier());
        testProcNode.setProperties(properties);
        ProcessorLifecycleIT.TestProcessor testProcessor = ((ProcessorLifecycleIT.TestProcessor) (testProcNode.getProcessor()));
        testProcessor.withService = true;
        processScheduler.startProcessor(testProcNode, true);
        Assert.fail();
    }

    private static class FlowManagerAndSystemBundle {
        private final FlowManager flowManager;

        private final Bundle systemBundle;

        public FlowManagerAndSystemBundle(FlowManager flowManager, Bundle systemBundle) {
            this.flowManager = flowManager;
            this.systemBundle = systemBundle;
        }

        public FlowManager getFlowManager() {
            return flowManager;
        }

        public Bundle getSystemBundle() {
            return systemBundle;
        }
    }

    /**
     *
     */
    public static class TestProcessor extends AbstractProcessor {
        private static final Runnable NOP = () -> {
        };

        private Runnable onScheduleCallback = ProcessorLifecycleIT.TestProcessor.NOP;

        private Runnable onUnscheduleCallback = ProcessorLifecycleIT.TestProcessor.NOP;

        private Runnable onStopCallback = ProcessorLifecycleIT.TestProcessor.NOP;

        private Runnable onTriggerCallback = ProcessorLifecycleIT.TestProcessor.NOP;

        private boolean generateExceptionOnScheduled;

        private boolean generateExceptionOnTrigger;

        private boolean withService;

        private int keepFailingOnScheduledTimes;

        private int onScheduledExceptionCount;

        private final List<String> operationNames = new LinkedList<>();

        void setScenario(Runnable onScheduleCallback, Runnable onUnscheduleCallback, Runnable onStopCallback, Runnable onTriggerCallback) {
            this.onScheduleCallback = onScheduleCallback;
            this.onUnscheduleCallback = onUnscheduleCallback;
            this.onStopCallback = onStopCallback;
            this.onTriggerCallback = onTriggerCallback;
        }

        @OnScheduled
        public void schedule(ProcessContext ctx) {
            this.operationNames.add("@OnScheduled");
            if ((this.generateExceptionOnScheduled) && (((this.onScheduledExceptionCount)++) < (this.keepFailingOnScheduledTimes))) {
                throw new RuntimeException("Intentional");
            }
            this.onScheduleCallback.run();
        }

        @OnUnscheduled
        public void unschedule() {
            this.operationNames.add("@OnUnscheduled");
            this.onUnscheduleCallback.run();
        }

        @OnStopped
        public void stop() {
            this.operationNames.add("@OnStopped");
            this.onStopCallback.run();
        }

        @Override
        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            PropertyDescriptor PROP = new PropertyDescriptor.Builder().name("P").description("Blah Blah").required(true).addValidator(new Validator() {
                @Override
                public ValidationResult validate(final String subject, final String value, final ValidationContext context) {
                    return new ValidationResult.Builder().subject(subject).input(value).valid(((value != null) && (!(value.isEmpty())))).explanation((subject + " cannot be empty")).build();
                }
            }).build();
            PropertyDescriptor SERVICE = new PropertyDescriptor.Builder().name("S").description("Blah Blah").required(true).identifiesControllerService(ProcessorLifecycleIT.ITestservice.class).build();
            return this.withService ? Arrays.asList(PROP, SERVICE) : Arrays.asList(PROP);
        }

        @Override
        public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
            if (this.generateExceptionOnTrigger) {
                throw new RuntimeException("Intentional");
            }
            this.onTriggerCallback.run();
        }
    }

    public static class TestService extends AbstractControllerService implements ProcessorLifecycleIT.ITestservice {}

    public static interface ITestservice extends ControllerService {}

    private static class EmptyRunnable implements Runnable {
        @Override
        public void run() {
        }
    }

    private static class BlockingInterruptableRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static class BlockingUninterruptableRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    // ignore
                }
            } 
        }
    }

    private static class RandomOrFixedDelayedRunnable implements Runnable {
        private final int delayLimit;

        private final boolean randomDelay;

        public RandomOrFixedDelayedRunnable(int delayLimit, boolean randomDelay) {
            this.delayLimit = delayLimit;
            this.randomDelay = randomDelay;
        }

        Random random = new Random();

        @Override
        public void run() {
            try {
                if (this.randomDelay) {
                    Thread.sleep(random.nextInt(this.delayLimit));
                } else {
                    Thread.sleep(this.delayLimit);
                }
            } catch (InterruptedException e) {
                ProcessorLifecycleIT.logger.warn("Interrupted while sleeping");
                Thread.currentThread().interrupt();
            }
        }
    }
}

