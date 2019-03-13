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
package org.apache.nifi.controller;


import StandardValidators.NON_EMPTY_VALIDATOR;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.lifecycle.OnStopped;
import org.apache.nifi.annotation.lifecycle.OnUnscheduled;
import org.apache.nifi.bundle.Bundle;
import org.apache.nifi.bundle.BundleCoordinate;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.exception.ControllerServiceInstantiationException;
import org.apache.nifi.controller.exception.ProcessorInstantiationException;
import org.apache.nifi.controller.kerberos.KerberosConfig;
import org.apache.nifi.controller.reporting.ReportingTaskInstantiationException;
import org.apache.nifi.controller.service.ControllerServiceNode;
import org.apache.nifi.engine.FlowEngine;
import org.apache.nifi.nar.ExtensionDiscoveringManager;
import org.apache.nifi.nar.NarCloseable;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.StandardProcessContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.registry.VariableDescriptor;
import org.apache.nifi.registry.VariableRegistry;
import org.apache.nifi.test.processors.ModifiesClasspathNoAnnotationProcessor;
import org.apache.nifi.test.processors.ModifiesClasspathProcessor;
import org.apache.nifi.util.MockVariableRegistry;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.util.SynchronousValidationTrigger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestStandardProcessorNode {
    private MockVariableRegistry variableRegistry;

    private Bundle systemBundle;

    private ExtensionDiscoveringManager extensionManager;

    private NiFiProperties niFiProperties;

    @Test(timeout = 10000)
    public void testStart() throws InterruptedException {
        final TestStandardProcessorNode.ProcessorThatThrowsExceptionOnScheduled processor = new TestStandardProcessorNode.ProcessorThatThrowsExceptionOnScheduled();
        final String uuid = UUID.randomUUID().toString();
        ProcessorInitializationContext initContext = new org.apache.nifi.processor.StandardProcessorInitializationContext(uuid, null, null, null, KerberosConfig.NOT_CONFIGURED);
        processor.initialize(initContext);
        final ReloadComponent reloadComponent = Mockito.mock(ReloadComponent.class);
        final BundleCoordinate coordinate = Mockito.mock(BundleCoordinate.class);
        final LoggableComponent<Processor> loggableComponent = new LoggableComponent(processor, coordinate, null);
        final StandardProcessorNode procNode = new StandardProcessorNode(loggableComponent, uuid, createValidationContextFactory(), null, null, new org.apache.nifi.registry.variable.StandardComponentVariableRegistry(VariableRegistry.EMPTY_REGISTRY), reloadComponent, extensionManager, new SynchronousValidationTrigger());
        final ScheduledExecutorService taskScheduler = new FlowEngine(1, "TestClasspathResources", true);
        final StandardProcessContext processContext = new StandardProcessContext(procNode, null, null, null, () -> false);
        final SchedulingAgentCallback schedulingAgentCallback = new SchedulingAgentCallback() {
            @Override
            public void onTaskComplete() {
            }

            @Override
            public Future<?> scheduleTask(final Callable<?> task) {
                return taskScheduler.submit(task);
            }

            @Override
            public void trigger() {
                Assert.fail("Should not have completed");
            }
        };
        procNode.performValidation();
        procNode.start(taskScheduler, 20000L, 10000L, processContext, schedulingAgentCallback, true);
        Thread.sleep(1000L);
        Assert.assertEquals(1, processor.onScheduledCount);
        Assert.assertEquals(1, processor.onUnscheduledCount);
        Assert.assertEquals(1, processor.onStoppedCount);
    }

    @Test
    public void testSinglePropertyDynamicallyModifiesClasspath() throws MalformedURLException {
        final TestStandardProcessorNode.MockReloadComponent reloadComponent = new TestStandardProcessorNode.MockReloadComponent();
        final PropertyDescriptor classpathProp = new PropertyDescriptor.Builder().name("Classpath Resources").dynamicallyModifiesClasspath(true).addValidator(NON_EMPTY_VALIDATOR).build();
        final ModifiesClasspathProcessor processor = new ModifiesClasspathProcessor(Arrays.asList(classpathProp));
        final StandardProcessorNode procNode = createProcessorNode(processor, reloadComponent);
        try (final NarCloseable narCloseable = NarCloseable.withComponentNarLoader(extensionManager, procNode.getProcessor().getClass(), procNode.getIdentifier())) {
            // Should not have any of the test resources loaded at this point
            final URL[] testResources = getTestResources();
            for (URL testResource : testResources) {
                if (containsResource(reloadComponent.getAdditionalUrls(), testResource)) {
                    Assert.fail("found resource that should not have been loaded");
                }
            }
            // Simulate setting the properties of the processor to point to the test resources directory
            final Map<String, String> properties = new HashMap<>();
            properties.put(classpathProp.getName(), "src/test/resources/TestClasspathResources");
            procNode.setProperties(properties);
            // Should have all of the resources loaded into the InstanceClassLoader now
            for (URL testResource : testResources) {
                Assert.assertTrue(containsResource(reloadComponent.getAdditionalUrls(), testResource));
            }
            Assert.assertEquals(ModifiesClasspathProcessor.class.getCanonicalName(), reloadComponent.getNewType());
            // Should pass validation
            Assert.assertTrue(procNode.computeValidationErrors(procNode.getValidationContext()).isEmpty());
        } finally {
            extensionManager.removeInstanceClassLoader(procNode.getIdentifier());
        }
    }

    @Test
    public void testUpdateOtherPropertyDoesNotImpactClasspath() throws MalformedURLException {
        final TestStandardProcessorNode.MockReloadComponent reloadComponent = new TestStandardProcessorNode.MockReloadComponent();
        final PropertyDescriptor classpathProp = new PropertyDescriptor.Builder().name("Classpath Resources").dynamicallyModifiesClasspath(true).addValidator(NON_EMPTY_VALIDATOR).build();
        final PropertyDescriptor otherProp = new PropertyDescriptor.Builder().name("My Property").addValidator(NON_EMPTY_VALIDATOR).build();
        final ModifiesClasspathProcessor processor = new ModifiesClasspathProcessor(Arrays.asList(classpathProp, otherProp));
        final StandardProcessorNode procNode = createProcessorNode(processor, reloadComponent);
        try (final NarCloseable narCloseable = NarCloseable.withComponentNarLoader(extensionManager, procNode.getProcessor().getClass(), procNode.getIdentifier())) {
            // Should not have any of the test resources loaded at this point
            final URL[] testResources = getTestResources();
            for (URL testResource : testResources) {
                if (containsResource(reloadComponent.getAdditionalUrls(), testResource)) {
                    Assert.fail("found resource that should not have been loaded");
                }
            }
            // Simulate setting the properties of the processor to point to the test resources directory
            final Map<String, String> properties = new HashMap<>();
            properties.put(classpathProp.getName(), "src/test/resources/TestClasspathResources");
            procNode.setProperties(properties);
            // Should have all of the resources loaded into the InstanceClassLoader now
            for (URL testResource : testResources) {
                Assert.assertTrue(containsResource(reloadComponent.getAdditionalUrls(), testResource));
            }
            // Should pass validation
            Assert.assertTrue(procNode.computeValidationErrors(procNode.getValidationContext()).isEmpty());
            // Simulate setting updating the other property which should not change the classpath
            final Map<String, String> otherProperties = new HashMap<>();
            otherProperties.put(otherProp.getName(), "foo");
            procNode.setProperties(otherProperties);
            // Should STILL have all of the resources loaded into the InstanceClassLoader now
            for (URL testResource : testResources) {
                Assert.assertTrue(containsResource(reloadComponent.getAdditionalUrls(), testResource));
            }
            // Should STILL pass validation
            Assert.assertTrue(procNode.computeValidationErrors(procNode.getValidationContext()).isEmpty());
            // Lets update the classpath property and make sure the resources get updated
            final Map<String, String> newClasspathProperties = new HashMap<>();
            newClasspathProperties.put(classpathProp.getName(), "src/test/resources/TestClasspathResources/resource1.txt");
            procNode.setProperties(newClasspathProperties);
            // Should only have resource1 loaded now
            Assert.assertTrue(containsResource(reloadComponent.getAdditionalUrls(), testResources[0]));
            Assert.assertFalse(containsResource(reloadComponent.getAdditionalUrls(), testResources[1]));
            Assert.assertFalse(containsResource(reloadComponent.getAdditionalUrls(), testResources[2]));
            Assert.assertEquals(ModifiesClasspathProcessor.class.getCanonicalName(), reloadComponent.getNewType());
            // Should STILL pass validation
            Assert.assertTrue(procNode.computeValidationErrors(procNode.getValidationContext()).isEmpty());
        } finally {
            extensionManager.removeInstanceClassLoader(procNode.getIdentifier());
        }
    }

    @Test
    public void testMultiplePropertiesDynamicallyModifyClasspathWithExpressionLanguage() throws MalformedURLException {
        final TestStandardProcessorNode.MockReloadComponent reloadComponent = new TestStandardProcessorNode.MockReloadComponent();
        final PropertyDescriptor classpathProp1 = new PropertyDescriptor.Builder().name("Classpath Resource 1").dynamicallyModifiesClasspath(true).addValidator(NON_EMPTY_VALIDATOR).build();
        final PropertyDescriptor classpathProp2 = new PropertyDescriptor.Builder().name("Classpath Resource 2").dynamicallyModifiesClasspath(true).addValidator(NON_EMPTY_VALIDATOR).build();
        final ModifiesClasspathProcessor processor = new ModifiesClasspathProcessor(Arrays.asList(classpathProp1, classpathProp2));
        final StandardProcessorNode procNode = createProcessorNode(processor, reloadComponent);
        try (final NarCloseable narCloseable = NarCloseable.withComponentNarLoader(extensionManager, procNode.getProcessor().getClass(), procNode.getIdentifier())) {
            // Should not have any of the test resources loaded at this point
            final URL[] testResources = getTestResources();
            for (URL testResource : testResources) {
                if (containsResource(reloadComponent.getAdditionalUrls(), testResource)) {
                    Assert.fail("found resource that should not have been loaded");
                }
            }
            // Simulate setting the properties pointing to two of the resources
            final Map<String, String> properties = new HashMap<>();
            properties.put(classpathProp1.getName(), "src/test/resources/TestClasspathResources/resource1.txt");
            properties.put(classpathProp2.getName(), "src/test/resources/TestClasspathResources/${myResource}");
            variableRegistry.setVariable(new VariableDescriptor("myResource"), "resource3.txt");
            procNode.setProperties(properties);
            // Should have resources 1 and 3 loaded into the InstanceClassLoader now
            Assert.assertTrue(containsResource(reloadComponent.getAdditionalUrls(), testResources[0]));
            Assert.assertTrue(containsResource(reloadComponent.getAdditionalUrls(), testResources[2]));
            Assert.assertFalse(containsResource(reloadComponent.getAdditionalUrls(), testResources[1]));
            Assert.assertEquals(ModifiesClasspathProcessor.class.getCanonicalName(), reloadComponent.getNewType());
            // Should pass validation
            Assert.assertTrue(procNode.computeValidationErrors(procNode.getValidationContext()).isEmpty());
        } finally {
            extensionManager.removeInstanceClassLoader(procNode.getIdentifier());
        }
    }

    @Test
    public void testSomeNonExistentPropertiesDynamicallyModifyClasspath() throws MalformedURLException {
        final TestStandardProcessorNode.MockReloadComponent reloadComponent = new TestStandardProcessorNode.MockReloadComponent();
        final PropertyDescriptor classpathProp1 = new PropertyDescriptor.Builder().name("Classpath Resource 1").dynamicallyModifiesClasspath(true).addValidator(NON_EMPTY_VALIDATOR).build();
        final PropertyDescriptor classpathProp2 = new PropertyDescriptor.Builder().name("Classpath Resource 2").dynamicallyModifiesClasspath(true).addValidator(NON_EMPTY_VALIDATOR).build();
        final ModifiesClasspathProcessor processor = new ModifiesClasspathProcessor(Arrays.asList(classpathProp1, classpathProp2));
        final StandardProcessorNode procNode = createProcessorNode(processor, reloadComponent);
        try (final NarCloseable narCloseable = NarCloseable.withComponentNarLoader(extensionManager, procNode.getProcessor().getClass(), procNode.getIdentifier())) {
            // Should not have any of the test resources loaded at this point
            final URL[] testResources = getTestResources();
            for (URL testResource : testResources) {
                if (containsResource(reloadComponent.getAdditionalUrls(), testResource)) {
                    Assert.fail("found resource that should not have been loaded");
                }
            }
            // Simulate setting the properties pointing to two of the resources
            final Map<String, String> properties = new HashMap<>();
            properties.put(classpathProp1.getName(), "src/test/resources/TestClasspathResources/resource1.txt");
            properties.put(classpathProp2.getName(), "src/test/resources/TestClasspathResources/DoesNotExist.txt");
            procNode.setProperties(properties);
            // Should have resources 1 and 3 loaded into the InstanceClassLoader now
            Assert.assertTrue(containsResource(reloadComponent.getAdditionalUrls(), testResources[0]));
            Assert.assertFalse(containsResource(reloadComponent.getAdditionalUrls(), testResources[1]));
            Assert.assertFalse(containsResource(reloadComponent.getAdditionalUrls(), testResources[2]));
            Assert.assertEquals(ModifiesClasspathProcessor.class.getCanonicalName(), reloadComponent.getNewType());
            // Should pass validation
            Assert.assertTrue(procNode.computeValidationErrors(procNode.getValidationContext()).isEmpty());
        } finally {
            extensionManager.removeInstanceClassLoader(procNode.getIdentifier());
        }
    }

    @Test
    public void testPropertyModifiesClasspathWhenProcessorMissingAnnotation() throws MalformedURLException {
        final TestStandardProcessorNode.MockReloadComponent reloadComponent = new TestStandardProcessorNode.MockReloadComponent();
        final ModifiesClasspathNoAnnotationProcessor processor = new ModifiesClasspathNoAnnotationProcessor();
        final StandardProcessorNode procNode = createProcessorNode(processor, reloadComponent);
        try (final NarCloseable narCloseable = NarCloseable.withComponentNarLoader(extensionManager, procNode.getProcessor().getClass(), procNode.getIdentifier())) {
            final Map<String, String> properties = new HashMap<>();
            properties.put(ModifiesClasspathNoAnnotationProcessor.CLASSPATH_RESOURCE.getName(), "src/test/resources/TestClasspathResources/resource1.txt");
            procNode.setProperties(properties);
            final URL[] testResources = getTestResources();
            Assert.assertTrue(containsResource(reloadComponent.getAdditionalUrls(), testResources[0]));
            Assert.assertFalse(containsResource(reloadComponent.getAdditionalUrls(), testResources[1]));
            Assert.assertFalse(containsResource(reloadComponent.getAdditionalUrls(), testResources[2]));
            Assert.assertEquals(ModifiesClasspathNoAnnotationProcessor.class.getCanonicalName(), reloadComponent.getNewType());
            // Should pass validation
            Assert.assertTrue(procNode.computeValidationErrors(procNode.getValidationContext()).isEmpty());
        } finally {
            extensionManager.removeInstanceClassLoader(procNode.getIdentifier());
        }
    }

    @Test
    public void testVerifyCanUpdateBundle() {
        final ReloadComponent reloadComponent = new TestStandardProcessorNode.MockReloadComponent();
        final ModifiesClasspathNoAnnotationProcessor processor = new ModifiesClasspathNoAnnotationProcessor();
        final StandardProcessorNode procNode = createProcessorNode(processor, reloadComponent);
        final BundleCoordinate existingCoordinate = procNode.getBundleCoordinate();
        // should be allowed to update when the bundle is the same
        procNode.verifyCanUpdateBundle(existingCoordinate);
        // should be allowed to update when the group and id are the same but version is different
        final BundleCoordinate diffVersion = new BundleCoordinate(existingCoordinate.getGroup(), existingCoordinate.getId(), "v2");
        Assert.assertTrue((!(existingCoordinate.getVersion().equals(diffVersion.getVersion()))));
        procNode.verifyCanUpdateBundle(diffVersion);
        // should not be allowed to update when the bundle id is different
        final BundleCoordinate diffId = new BundleCoordinate(existingCoordinate.getGroup(), "different-id", existingCoordinate.getVersion());
        Assert.assertTrue((!(existingCoordinate.getId().equals(diffId.getId()))));
        try {
            procNode.verifyCanUpdateBundle(diffId);
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
        }
        // should not be allowed to update when the bundle group is different
        final BundleCoordinate diffGroup = new BundleCoordinate("different-group", existingCoordinate.getId(), existingCoordinate.getVersion());
        Assert.assertTrue((!(existingCoordinate.getGroup().equals(diffGroup.getGroup()))));
        try {
            procNode.verifyCanUpdateBundle(diffGroup);
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
        }
    }

    private static class MockReloadComponent implements ReloadComponent {
        private String newType;

        private final Set<URL> additionalUrls = new LinkedHashSet<>();

        public Set<URL> getAdditionalUrls() {
            return this.additionalUrls;
        }

        public String getNewType() {
            return newType;
        }

        @Override
        public void reload(ProcessorNode existingNode, String newType, BundleCoordinate bundleCoordinate, Set<URL> additionalUrls) throws ProcessorInstantiationException {
            reload(newType, additionalUrls);
        }

        @Override
        public void reload(ControllerServiceNode existingNode, String newType, BundleCoordinate bundleCoordinate, Set<URL> additionalUrls) throws ControllerServiceInstantiationException {
            reload(newType, additionalUrls);
        }

        @Override
        public void reload(ReportingTaskNode existingNode, String newType, BundleCoordinate bundleCoordinate, Set<URL> additionalUrls) throws ReportingTaskInstantiationException {
            reload(newType, additionalUrls);
        }

        private void reload(String newType, Set<URL> additionalUrls) {
            this.newType = newType;
            this.additionalUrls.clear();
            if (additionalUrls != null) {
                this.additionalUrls.addAll(additionalUrls);
            }
        }
    }

    public static class ProcessorThatThrowsExceptionOnScheduled extends AbstractProcessor {
        private int onScheduledCount = 0;

        private int onUnscheduledCount = 0;

        private int onStoppedCount = 0;

        @Override
        public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        }

        @OnScheduled
        public void onScheduled() {
            (onScheduledCount)++;
            throw new ProcessException("OnScheduled called - Unit Test throws Exception intentionally");
        }

        @OnUnscheduled
        public void onUnscheduled() {
            (onUnscheduledCount)++;
        }

        @OnStopped
        public void onStopped() {
            (onStoppedCount)++;
        }
    }
}

