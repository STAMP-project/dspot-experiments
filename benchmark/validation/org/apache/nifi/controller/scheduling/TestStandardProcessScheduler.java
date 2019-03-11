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


import ControllerServiceState.DISABLED;
import ControllerServiceState.DISABLING;
import ControllerServiceState.ENABLING;
import ValidationStatus.VALID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.nifi.annotation.lifecycle.OnDisabled;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.bundle.Bundle;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.state.StateManagerProvider;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.controller.FlowController;
import org.apache.nifi.controller.LoggableComponent;
import org.apache.nifi.controller.ProcessorNode;
import org.apache.nifi.controller.ReloadComponent;
import org.apache.nifi.controller.ReportingTaskNode;
import org.apache.nifi.controller.ValidationContextFactory;
import org.apache.nifi.controller.flow.FlowManager;
import org.apache.nifi.controller.kerberos.KerberosConfig;
import org.apache.nifi.controller.scheduling.processors.FailOnScheduledProcessor;
import org.apache.nifi.controller.service.ControllerServiceNode;
import org.apache.nifi.controller.service.ControllerServiceProvider;
import org.apache.nifi.controller.service.ControllerServiceState;
import org.apache.nifi.groups.ProcessGroup;
import org.apache.nifi.nar.ExtensionDiscoveringManager;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.registry.VariableRegistry;
import org.apache.nifi.reporting.AbstractReportingTask;
import org.apache.nifi.reporting.ReportingContext;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.util.SynchronousValidationTrigger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestStandardProcessScheduler {
    private StandardProcessScheduler scheduler = null;

    private ReportingTaskNode taskNode = null;

    private TestStandardProcessScheduler.TestReportingTask reportingTask = null;

    private final StateManagerProvider stateMgrProvider = Mockito.mock(StateManagerProvider.class);

    private VariableRegistry variableRegistry = VariableRegistry.ENVIRONMENT_SYSTEM_REGISTRY;

    private FlowController controller;

    private FlowManager flowManager;

    private ProcessGroup rootGroup;

    private NiFiProperties nifiProperties;

    private Bundle systemBundle;

    private ExtensionDiscoveringManager extensionManager;

    private ControllerServiceProvider serviceProvider;

    private volatile String propsFile = TestStandardProcessScheduler.class.getResource("/standardprocessschedulertest.nifi.properties").getFile();

    /**
     * We have run into an issue where a Reporting Task is scheduled to run but
     * throws an Exception from a method with the @OnScheduled annotation. User
     * stops Reporting Task, updates configuration to fix the issue. Reporting
     * Task then finishes running @OnSchedule method and is then scheduled to
     * run. This unit test is intended to verify that we have this resolved.
     */
    @Test
    public void testReportingTaskDoesntKeepRunningAfterStop() throws InterruptedException {
        taskNode.performValidation();
        scheduler.schedule(taskNode);
        // Let it try to run a few times.
        Thread.sleep(25L);
        scheduler.unschedule(taskNode);
        final int attempts = reportingTask.onScheduleAttempts.get();
        // give it a sec to make sure that it's finished running.
        Thread.sleep(250L);
        final int attemptsAfterStop = (reportingTask.onScheduleAttempts.get()) - attempts;
        // allow 1 extra run, due to timing issues that could call it as it's being stopped.
        Assert.assertTrue((("After unscheduling Reporting Task, task ran an additional " + attemptsAfterStop) + " times"), (attemptsAfterStop <= 1));
    }

    @Test(timeout = 60000)
    public void testDisableControllerServiceWithProcessorTryingToStartUsingIt() throws InterruptedException {
        final String uuid = UUID.randomUUID().toString();
        final Processor proc = new TestStandardProcessScheduler.ServiceReferencingProcessor();
        proc.initialize(new org.apache.nifi.processor.StandardProcessorInitializationContext(uuid, null, null, null, KerberosConfig.NOT_CONFIGURED));
        final ReloadComponent reloadComponent = Mockito.mock(ReloadComponent.class);
        final ControllerServiceNode service = flowManager.createControllerService(NoStartServiceImpl.class.getName(), "service", systemBundle.getBundleDetails().getCoordinate(), null, true, true);
        rootGroup.addControllerService(service);
        final LoggableComponent<Processor> loggableComponent = new LoggableComponent(proc, systemBundle.getBundleDetails().getCoordinate(), null);
        final ValidationContextFactory validationContextFactory = new org.apache.nifi.processor.StandardValidationContextFactory(serviceProvider, variableRegistry);
        final ProcessorNode procNode = new org.apache.nifi.controller.StandardProcessorNode(loggableComponent, uuid, validationContextFactory, scheduler, serviceProvider, new org.apache.nifi.registry.variable.StandardComponentVariableRegistry(VariableRegistry.EMPTY_REGISTRY), reloadComponent, extensionManager, new SynchronousValidationTrigger());
        rootGroup.addProcessor(procNode);
        Map<String, String> procProps = new HashMap<>();
        procProps.put(TestStandardProcessScheduler.ServiceReferencingProcessor.SERVICE_DESC.getName(), service.getIdentifier());
        procNode.setProperties(procProps);
        service.performValidation();
        scheduler.enableControllerService(service);
        procNode.performValidation();
        scheduler.startProcessor(procNode, true);
        Thread.sleep(25L);
        scheduler.stopProcessor(procNode);
        Assert.assertTrue(service.isActive());
        Assert.assertSame(service.getState(), ENABLING);
        scheduler.disableControllerService(service);
        Assert.assertSame(service.getState(), DISABLING);
        Assert.assertFalse(service.isActive());
        while ((service.getState()) != (ControllerServiceState.DISABLED)) {
            Thread.sleep(5L);
        } 
        Assert.assertSame(service.getState(), DISABLED);
    }

    public class TestReportingTask extends AbstractReportingTask {
        private final AtomicBoolean failOnScheduled = new AtomicBoolean(true);

        private final AtomicInteger onScheduleAttempts = new AtomicInteger(0);

        private final AtomicInteger triggerCount = new AtomicInteger(0);

        @OnScheduled
        public void onScheduled() {
            onScheduleAttempts.incrementAndGet();
            if (failOnScheduled.get()) {
                throw new RuntimeException("Intentional Exception for testing purposes");
            }
        }

        @Override
        public void onTrigger(final ReportingContext context) {
            triggerCount.getAndIncrement();
        }
    }

    public static class ServiceReferencingProcessor extends AbstractProcessor {
        static final PropertyDescriptor SERVICE_DESC = new PropertyDescriptor.Builder().name("service").identifiesControllerService(NoStartService.class).required(true).build();

        @Override
        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            final List<PropertyDescriptor> properties = new ArrayList<>();
            properties.add(TestStandardProcessScheduler.ServiceReferencingProcessor.SERVICE_DESC);
            return properties;
        }

        @Override
        public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        }
    }

    /**
     * Validates the atomic nature of ControllerServiceNode.enable() method
     * which must only trigger @OnEnabled once, regardless of how many threads
     * may have a reference to the underlying ProcessScheduler and
     * ControllerServiceNode.
     */
    @Test
    public void validateServiceEnablementLogicHappensOnlyOnce() throws Exception {
        final StandardProcessScheduler scheduler = createScheduler();
        final ControllerServiceNode serviceNode = flowManager.createControllerService(TestStandardProcessScheduler.SimpleTestService.class.getName(), "1", systemBundle.getBundleDetails().getCoordinate(), null, false, true);
        serviceNode.performValidation();
        Assert.assertFalse(serviceNode.isActive());
        final TestStandardProcessScheduler.SimpleTestService ts = ((TestStandardProcessScheduler.SimpleTestService) (serviceNode.getControllerServiceImplementation()));
        final ExecutorService executor = Executors.newCachedThreadPool();
        final AtomicBoolean asyncFailed = new AtomicBoolean();
        for (int i = 0; i < 1000; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        scheduler.enableControllerService(serviceNode);
                        Assert.assertTrue(serviceNode.isActive());
                    } catch (final Exception e) {
                        e.printStackTrace();
                        asyncFailed.set(true);
                    }
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertFalse(asyncFailed.get());
        Assert.assertEquals(1, ts.enableInvocationCount());
    }

    /**
     * Validates the atomic nature of ControllerServiceNode.disable(..) method
     * which must never trigger @OnDisabled, regardless of how many threads may
     * have a reference to the underlying ProcessScheduler and
     * ControllerServiceNode.
     */
    @Test
    public void validateDisabledServiceCantBeDisabled() throws Exception {
        final StandardProcessScheduler scheduler = createScheduler();
        final ControllerServiceNode serviceNode = flowManager.createControllerService(TestStandardProcessScheduler.SimpleTestService.class.getName(), "1", systemBundle.getBundleDetails().getCoordinate(), null, false, true);
        final TestStandardProcessScheduler.SimpleTestService ts = ((TestStandardProcessScheduler.SimpleTestService) (serviceNode.getControllerServiceImplementation()));
        final ExecutorService executor = Executors.newCachedThreadPool();
        final AtomicBoolean asyncFailed = new AtomicBoolean();
        for (int i = 0; i < 1000; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        scheduler.disableControllerService(serviceNode);
                        Assert.assertFalse(serviceNode.isActive());
                    } catch (final Exception e) {
                        e.printStackTrace();
                        asyncFailed.set(true);
                    }
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertFalse(asyncFailed.get());
        Assert.assertEquals(0, ts.disableInvocationCount());
    }

    /**
     * Validates the atomic nature of ControllerServiceNode.disable() method
     * which must only trigger @OnDisabled once, regardless of how many threads
     * may have a reference to the underlying ProcessScheduler and
     * ControllerServiceNode.
     */
    @Test
    public void validateEnabledServiceCanOnlyBeDisabledOnce() throws Exception {
        final StandardProcessScheduler scheduler = createScheduler();
        final ControllerServiceNode serviceNode = flowManager.createControllerService(TestStandardProcessScheduler.SimpleTestService.class.getName(), "1", systemBundle.getBundleDetails().getCoordinate(), null, false, true);
        Assert.assertSame(VALID, serviceNode.performValidation());
        final TestStandardProcessScheduler.SimpleTestService ts = ((TestStandardProcessScheduler.SimpleTestService) (serviceNode.getControllerServiceImplementation()));
        scheduler.enableControllerService(serviceNode).get();
        Assert.assertTrue(serviceNode.isActive());
        final ExecutorService executor = Executors.newCachedThreadPool();
        final AtomicBoolean asyncFailed = new AtomicBoolean();
        for (int i = 0; i < 1000; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        scheduler.disableControllerService(serviceNode);
                        Assert.assertFalse(serviceNode.isActive());
                    } catch (final Exception e) {
                        e.printStackTrace();
                        asyncFailed.set(true);
                    }
                }
            });
        }
        // need to sleep a while since we are emulating async invocations on
        // method that is also internally async
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);// change to seconds.

        Assert.assertFalse(asyncFailed.get());
        Assert.assertEquals(1, ts.disableInvocationCount());
    }

    @Test
    public void validateDisablingOfTheFailedService() throws Exception {
        final StandardProcessScheduler scheduler = createScheduler();
        final ControllerServiceNode serviceNode = flowManager.createControllerService(TestStandardProcessScheduler.FailingService.class.getName(), "1", systemBundle.getBundleDetails().getCoordinate(), null, false, true);
        serviceNode.performValidation();
        final Future<?> future = scheduler.enableControllerService(serviceNode);
        try {
            future.get();
        } catch (final Exception e) {
            // Expected behavior because the FailingService throws Exception when attempting to enable
        }
        scheduler.shutdown();
        /* Because it was never disabled it will remain active since its
        enabling is being retried. This may actually be a bug in the
        scheduler since it probably has to shut down all components (disable
        services, shut down processors etc) before shutting down itself
         */
        Assert.assertTrue(serviceNode.isActive());
        Assert.assertSame(serviceNode.getState(), ENABLING);
    }

    /**
     * Validates that service that is infinitely blocking in @OnEnabled can
     * still have DISABLE operation initiated. The service itself will be set to
     * DISABLING state at which point UI and all will know that such service can
     * not be transitioned any more into any other state until it finishes
     * enabling (which will never happen in our case thus should be addressed by
     * user). However, regardless of user's mistake NiFi will remain
     * functioning.
     */
    @Test
    public void validateNeverEnablingServiceCanStillBeDisabled() throws Exception {
        final StandardProcessScheduler scheduler = createScheduler();
        final ControllerServiceNode serviceNode = flowManager.createControllerService(LongEnablingService.class.getName(), "1", systemBundle.getBundleDetails().getCoordinate(), null, false, true);
        final LongEnablingService ts = ((LongEnablingService) (serviceNode.getControllerServiceImplementation()));
        ts.setLimit(Long.MAX_VALUE);
        serviceNode.performValidation();
        scheduler.enableControllerService(serviceNode);
        Assert.assertTrue(serviceNode.isActive());
        final long maxTime = (System.nanoTime()) + (TimeUnit.SECONDS.toNanos(10));
        while (((ts.enableInvocationCount()) != 1) && ((System.nanoTime()) <= maxTime)) {
            Thread.sleep(1L);
        } 
        Assert.assertEquals(1, ts.enableInvocationCount());
        scheduler.disableControllerService(serviceNode);
        Assert.assertFalse(serviceNode.isActive());
        Assert.assertEquals(DISABLING, serviceNode.getState());
        Assert.assertEquals(0, ts.disableInvocationCount());
    }

    // Test that if processor throws Exception in @OnScheduled, it keeps getting scheduled
    @Test(timeout = 10000)
    public void testProcessorThrowsExceptionOnScheduledRetry() throws InterruptedException {
        final FailOnScheduledProcessor proc = new FailOnScheduledProcessor();
        proc.setDesiredFailureCount(3);
        initialize(new org.apache.nifi.processor.StandardProcessorInitializationContext(UUID.randomUUID().toString(), null, null, null, KerberosConfig.NOT_CONFIGURED));
        final ReloadComponent reloadComponent = Mockito.mock(ReloadComponent.class);
        final LoggableComponent<Processor> loggableComponent = new LoggableComponent(proc, systemBundle.getBundleDetails().getCoordinate(), null);
        final ProcessorNode procNode = new org.apache.nifi.controller.StandardProcessorNode(loggableComponent, UUID.randomUUID().toString(), new org.apache.nifi.processor.StandardValidationContextFactory(serviceProvider, variableRegistry), scheduler, serviceProvider, new org.apache.nifi.registry.variable.StandardComponentVariableRegistry(VariableRegistry.EMPTY_REGISTRY), reloadComponent, extensionManager, new SynchronousValidationTrigger());
        procNode.performValidation();
        rootGroup.addProcessor(procNode);
        scheduler.startProcessor(procNode, true);
        while (!(proc.isSucceess())) {
            Thread.sleep(5L);
        } 
        Assert.assertEquals(3, proc.getOnScheduledInvocationCount());
    }

    // Test that if processor times out in the @OnScheduled but responds to interrupt, it keeps getting scheduled
    @Test(timeout = 10000)
    public void testProcessorTimeOutRespondsToInterrupt() throws InterruptedException {
        final FailOnScheduledProcessor proc = new FailOnScheduledProcessor();
        proc.setDesiredFailureCount(0);
        proc.setOnScheduledSleepDuration(20, TimeUnit.MINUTES, true, 1);
        initialize(new org.apache.nifi.processor.StandardProcessorInitializationContext(UUID.randomUUID().toString(), null, null, null, KerberosConfig.NOT_CONFIGURED));
        final ReloadComponent reloadComponent = Mockito.mock(ReloadComponent.class);
        final LoggableComponent<Processor> loggableComponent = new LoggableComponent(proc, systemBundle.getBundleDetails().getCoordinate(), null);
        final ProcessorNode procNode = new org.apache.nifi.controller.StandardProcessorNode(loggableComponent, UUID.randomUUID().toString(), new org.apache.nifi.processor.StandardValidationContextFactory(serviceProvider, variableRegistry), scheduler, serviceProvider, new org.apache.nifi.registry.variable.StandardComponentVariableRegistry(VariableRegistry.EMPTY_REGISTRY), reloadComponent, extensionManager, new SynchronousValidationTrigger());
        rootGroup.addProcessor(procNode);
        procNode.performValidation();
        scheduler.startProcessor(procNode, true);
        while (!(proc.isSucceess())) {
            Thread.sleep(5L);
        } 
        // The first time that the processor's @OnScheduled method is called, it will sleep for 20 minutes. The scheduler should interrupt
        // that thread and then try again. The second time, the Processor will not sleep because setOnScheduledSleepDuration was called
        // above with iterations = 1
        Assert.assertEquals(2, proc.getOnScheduledInvocationCount());
    }

    // Test that if processor times out in the @OnScheduled and does not respond to interrupt, it is not scheduled again
    @Test(timeout = 10000)
    public void testProcessorTimeOutNoResponseToInterrupt() throws InterruptedException {
        final FailOnScheduledProcessor proc = new FailOnScheduledProcessor();
        proc.setDesiredFailureCount(0);
        proc.setOnScheduledSleepDuration(20, TimeUnit.MINUTES, false, 1);
        initialize(new org.apache.nifi.processor.StandardProcessorInitializationContext(UUID.randomUUID().toString(), null, null, null, KerberosConfig.NOT_CONFIGURED));
        final ReloadComponent reloadComponent = Mockito.mock(ReloadComponent.class);
        final LoggableComponent<Processor> loggableComponent = new LoggableComponent(proc, systemBundle.getBundleDetails().getCoordinate(), null);
        final ProcessorNode procNode = new org.apache.nifi.controller.StandardProcessorNode(loggableComponent, UUID.randomUUID().toString(), new org.apache.nifi.processor.StandardValidationContextFactory(serviceProvider, variableRegistry), scheduler, serviceProvider, new org.apache.nifi.registry.variable.StandardComponentVariableRegistry(VariableRegistry.EMPTY_REGISTRY), reloadComponent, extensionManager, new SynchronousValidationTrigger());
        rootGroup.addProcessor(procNode);
        procNode.performValidation();
        scheduler.startProcessor(procNode, true);
        Thread.sleep(100L);
        Assert.assertEquals(1, proc.getOnScheduledInvocationCount());
        Thread.sleep(100L);
        Assert.assertEquals(1, proc.getOnScheduledInvocationCount());
        // Allow test to complete.
        proc.setAllowSleepInterrupt(true);
    }

    public static class FailingService extends AbstractControllerService {
        @OnEnabled
        public void enable(final ConfigurationContext context) {
            throw new RuntimeException("intentional");
        }
    }

    public static class RandomShortDelayEnablingService extends AbstractControllerService {
        private final Random random = new Random();

        @OnEnabled
        public void enable(final ConfigurationContext context) {
            try {
                Thread.sleep(random.nextInt(20));
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static class SimpleTestService extends AbstractControllerService {
        private final AtomicInteger enableCounter = new AtomicInteger();

        private final AtomicInteger disableCounter = new AtomicInteger();

        @OnEnabled
        public void enable(final ConfigurationContext context) {
            this.enableCounter.incrementAndGet();
        }

        @OnDisabled
        public void disable(final ConfigurationContext context) {
            this.disableCounter.incrementAndGet();
        }

        public int enableInvocationCount() {
            return this.enableCounter.get();
        }

        public int disableInvocationCount() {
            return this.disableCounter.get();
        }
    }
}

