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
package org.apache.nifi.util;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.nifi.annotation.lifecycle.OnStopped;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.reporting.InitializationException;
import org.junit.Assert;
import org.junit.Test;


public class TestStandardProcessorTestRunner {
    @Test
    public void testProcessContextPassedToOnStoppedMethods() {
        final TestStandardProcessorTestRunner.ProcessorWithOnStop proc = new TestStandardProcessorTestRunner.ProcessorWithOnStop();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        Assert.assertEquals(0, proc.getOnStoppedCallsWithContext());
        Assert.assertEquals(0, proc.getOnStoppedCallsWithoutContext());
        runner.run(1, false);
        Assert.assertEquals(0, proc.getOnStoppedCallsWithContext());
        Assert.assertEquals(0, proc.getOnStoppedCallsWithoutContext());
        runner.run(1, true);
        Assert.assertEquals(1, proc.getOnStoppedCallsWithContext());
        Assert.assertEquals(1, proc.getOnStoppedCallsWithoutContext());
    }

    @Test
    public void testAllConditionsMet() {
        TestRunner runner = new StandardProcessorTestRunner(new TestStandardProcessorTestRunner.GoodProcessor());
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("GROUP_ATTRIBUTE_KEY", "1");
        attributes.put("KeyB", "hihii");
        runner.enqueue("1,hello\n1,good-bye".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(TestStandardProcessorTestRunner.GoodProcessor.REL_SUCCESS, 1);
        runner.assertAllConditionsMet("success", ( mff) -> (mff.isAttributeEqual("GROUP_ATTRIBUTE_KEY", "1")) && (mff.isContentEqual("1,hello\n1,good-bye")));
    }

    @Test
    public void testAllConditionsMetComplex() {
        TestRunner runner = new StandardProcessorTestRunner(new TestStandardProcessorTestRunner.GoodProcessor());
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("GROUP_ATTRIBUTE_KEY", "1");
        attributes.put("KeyB", "hihii");
        runner.enqueue("1,hello\n1,good-bye".getBytes(), attributes);
        attributes.clear();
        attributes.put("age", "34");
        runner.enqueue("May Andersson".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(TestStandardProcessorTestRunner.GoodProcessor.REL_SUCCESS, 2);
        Predicate<MockFlowFile> firstPredicate = ( mff) -> mff.isAttributeEqual("GROUP_ATTRIBUTE_KEY", "1");
        Predicate<MockFlowFile> either = firstPredicate.or(( mff) -> mff.isAttributeEqual("age", "34"));
        runner.assertAllConditionsMet("success", either);
    }

    @Test
    public void testNumThreads() {
        final TestStandardProcessorTestRunner.ProcessorWithOnStop proc = new TestStandardProcessorTestRunner.ProcessorWithOnStop();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setThreadCount(5);
        runner.run(1, true);
        Assert.assertEquals(5, runner.getProcessContext().getMaxConcurrentTasks());
    }

    @Test
    public void testFlowFileValidator() {
        final TestStandardProcessorTestRunner.AddAttributeProcessor proc = new TestStandardProcessorTestRunner.AddAttributeProcessor();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.run(5, true);
        runner.assertTransferCount(TestStandardProcessorTestRunner.AddAttributeProcessor.REL_SUCCESS, 3);
        runner.assertTransferCount(TestStandardProcessorTestRunner.AddAttributeProcessor.REL_FAILURE, 2);
        runner.assertAllFlowFilesContainAttribute(TestStandardProcessorTestRunner.AddAttributeProcessor.REL_SUCCESS, TestStandardProcessorTestRunner.AddAttributeProcessor.KEY);
        runner.assertAllFlowFiles(TestStandardProcessorTestRunner.AddAttributeProcessor.REL_SUCCESS, new FlowFileValidator() {
            @Override
            public void assertFlowFile(FlowFile f) {
                Assert.assertEquals("value", f.getAttribute(TestStandardProcessorTestRunner.AddAttributeProcessor.KEY));
            }
        });
    }

    @Test(expected = AssertionError.class)
    public void testFailFlowFileValidator() {
        final TestStandardProcessorTestRunner.AddAttributeProcessor proc = new TestStandardProcessorTestRunner.AddAttributeProcessor();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.run(5, true);
        runner.assertAllFlowFiles(new FlowFileValidator() {
            @Override
            public void assertFlowFile(FlowFile f) {
                Assert.assertEquals("value", f.getAttribute(TestStandardProcessorTestRunner.AddAttributeProcessor.KEY));
            }
        });
    }

    @Test(expected = AssertionError.class)
    public void testFailAllFlowFilesContainAttribute() {
        final TestStandardProcessorTestRunner.AddAttributeProcessor proc = new TestStandardProcessorTestRunner.AddAttributeProcessor();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.run(5, true);
        runner.assertAllFlowFilesContainAttribute(TestStandardProcessorTestRunner.AddAttributeProcessor.KEY);
    }

    @Test
    public void testAllFlowFilesContainAttribute() {
        final TestStandardProcessorTestRunner.AddAttributeProcessor proc = new TestStandardProcessorTestRunner.AddAttributeProcessor();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.run(1, true);
        runner.assertAllFlowFilesContainAttribute(TestStandardProcessorTestRunner.AddAttributeProcessor.KEY);
    }

    @Test
    public void testVariables() {
        final TestStandardProcessorTestRunner.AddAttributeProcessor proc = new TestStandardProcessorTestRunner.AddAttributeProcessor();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        Assert.assertNull(runner.getVariableValue("hello"));
        runner.setVariable("hello", "world");
        Assert.assertEquals("world", runner.getVariableValue("hello"));
        Assert.assertEquals("world", runner.removeVariable("hello"));
        Assert.assertNull(runner.getVariableValue("hello"));
    }

    @Test
    public void testControllerServiceUpdateShouldCallOnSetProperty() {
        // Arrange
        final ControllerService testService = new TestStandardProcessorTestRunner.SimpleTestService();
        final TestStandardProcessorTestRunner.AddAttributeProcessor proc = new TestStandardProcessorTestRunner.AddAttributeProcessor();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        final String serviceIdentifier = "test";
        final String pdName = "name";
        final String pdValue = "exampleName";
        try {
            runner.addControllerService(serviceIdentifier, testService);
        } catch (InitializationException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertFalse("onPropertyModified has been called", ((TestStandardProcessorTestRunner.SimpleTestService) (testService)).isOpmCalled());
        // Act
        ValidationResult vr = runner.setProperty(testService, pdName, pdValue);
        // Assert
        Assert.assertTrue(vr.isValid());
        ControllerServiceConfiguration csConf = getConfiguration(serviceIdentifier);
        PropertyDescriptor propertyDescriptor = testService.getPropertyDescriptor(pdName);
        String retrievedPDValue = csConf.getProperties().get(propertyDescriptor);
        Assert.assertEquals(pdValue, retrievedPDValue);
        Assert.assertTrue("onPropertyModified has not been called", ((TestStandardProcessorTestRunner.SimpleTestService) (testService)).isOpmCalled());
    }

    @Test
    public void testProcessorNameShouldBeSet() {
        final TestStandardProcessorTestRunner.AddAttributeProcessor proc = new TestStandardProcessorTestRunner.AddAttributeProcessor();
        final TestRunner runner = TestRunners.newTestRunner(proc, "TestName");
        Assert.assertEquals("TestName", runner.getProcessContext().getName());
    }

    @Test
    public void testProcessorInvalidWhenControllerServiceDisabled() {
        final ControllerService testService = new TestStandardProcessorTestRunner.RequiredPropertyTestService();
        final TestStandardProcessorTestRunner.AddAttributeProcessor proc = new TestStandardProcessorTestRunner.AddAttributeProcessor();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        final String serviceIdentifier = "test";
        final String pdName = "name";
        final String pdValue = "exampleName";
        try {
            runner.addControllerService(serviceIdentifier, testService);
        } catch (InitializationException e) {
            Assert.fail(e.getMessage());
        }
        // controller service invalid due to no value on required property; processor must also be invalid
        runner.assertNotValid(testService);
        runner.assertNotValid();
        // add required property; controller service valid but not enabled; processor must be invalid
        runner.setProperty(testService, TestStandardProcessorTestRunner.RequiredPropertyTestService.namePropertyDescriptor, pdValue);
        runner.assertValid(testService);
        runner.assertNotValid();
        // enable controller service; processor now valid
        runner.enableControllerService(testService);
        runner.assertValid(testService);
        runner.assertValid();
    }

    private static class ProcessorWithOnStop extends AbstractProcessor {
        private int callsWithContext = 0;

        private int callsWithoutContext = 0;

        @OnStopped
        public void onStoppedWithContext(final ProcessContext procContext) {
            (callsWithContext)++;
        }

        @OnStopped
        public void onStoppedWithoutContext() {
            (callsWithoutContext)++;
        }

        public int getOnStoppedCallsWithContext() {
            return callsWithContext;
        }

        public int getOnStoppedCallsWithoutContext() {
            return callsWithoutContext;
        }

        @Override
        public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        }
    }

    private static class AddAttributeProcessor extends AbstractProcessor {
        public static final Relationship REL_SUCCESS = new Relationship.Builder().name("success").description("success").build();

        public static final Relationship REL_FAILURE = new Relationship.Builder().name("failure").description("failure").build();

        public static final String KEY = "KEY";

        private Set<Relationship> relationships;

        private int counter = 0;

        @Override
        protected void init(final ProcessorInitializationContext context) {
            final Set<Relationship> relationships = new HashSet<>();
            relationships.add(TestStandardProcessorTestRunner.AddAttributeProcessor.REL_SUCCESS);
            relationships.add(TestStandardProcessorTestRunner.AddAttributeProcessor.REL_FAILURE);
            this.relationships = Collections.unmodifiableSet(relationships);
        }

        @Override
        public Set<Relationship> getRelationships() {
            return relationships;
        }

        @Override
        public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
            FlowFile ff = session.create();
            if (((counter) % 2) == 0) {
                ff = session.putAttribute(ff, TestStandardProcessorTestRunner.AddAttributeProcessor.KEY, "value");
                session.transfer(ff, TestStandardProcessorTestRunner.AddAttributeProcessor.REL_SUCCESS);
            } else {
                session.transfer(ff, TestStandardProcessorTestRunner.AddAttributeProcessor.REL_FAILURE);
            }
            (counter)++;
        }
    }

    private static class GoodProcessor extends AbstractProcessor {
        public static final Relationship REL_SUCCESS = new Relationship.Builder().name("success").description("Successfully created FlowFile from ...").build();

        public static final Relationship REL_FAILURE = new Relationship.Builder().name("failure").description("... execution failed. Incoming FlowFile will be penalized and routed to this relationship").build();

        private final Set<Relationship> relationships;

        public GoodProcessor() {
            final Set<Relationship> r = new HashSet<>();
            r.add(TestStandardProcessorTestRunner.GoodProcessor.REL_SUCCESS);
            r.add(TestStandardProcessorTestRunner.GoodProcessor.REL_FAILURE);
            relationships = Collections.unmodifiableSet(r);
        }

        @Override
        public Set<Relationship> getRelationships() {
            return relationships;
        }

        @Override
        public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
            for (FlowFile incoming : session.get(20)) {
                session.transfer(incoming, TestStandardProcessorTestRunner.GoodProcessor.REL_SUCCESS);
            }
        }
    }

    private static class SimpleTestService extends AbstractControllerService {
        private final String PD_NAME = "name";

        private PropertyDescriptor namePropertyDescriptor = new PropertyDescriptor.Builder().name(PD_NAME).displayName("Controller Service Name").required(false).sensitive(false).allowableValues("exampleName", "anotherExampleName").build();

        private boolean opmCalled = false;

        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            return Arrays.asList(namePropertyDescriptor);
        }

        public void onPropertyModified(final PropertyDescriptor descriptor, final String oldValue, final String newValue) {
            getLogger().info("onPropertyModified called for PD {} with old value {} and new value {}", new Object[]{ descriptor.getName(), oldValue, newValue });
            opmCalled = true;
        }

        public boolean isOpmCalled() {
            return opmCalled;
        }
    }

    private static class RequiredPropertyTestService extends AbstractControllerService {
        private static final String PD_NAME = "name";

        protected static final PropertyDescriptor namePropertyDescriptor = new PropertyDescriptor.Builder().name(TestStandardProcessorTestRunner.RequiredPropertyTestService.PD_NAME).displayName("Controller Service Name").required(true).sensitive(false).allowableValues("exampleName", "anotherExampleName").build();

        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            return Arrays.asList(TestStandardProcessorTestRunner.RequiredPropertyTestService.namePropertyDescriptor);
        }
    }
}

