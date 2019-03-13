/**
 * Copyright 2015-2019 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */
package org.junit.platform.launcher.listeners;


import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.fakes.TestDescriptorStub;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;


/**
 *
 *
 * @since 1.0
 */
class SummaryGenerationTests {
    private final SummaryGeneratingListener listener = new SummaryGeneratingListener();

    private final TestPlan testPlan = TestPlan.from(Collections.emptyList());

    @Test
    public void reportingCircularFailure() {
        IllegalArgumentException iaeCausedBy = new IllegalArgumentException("Illegal Argument Exception");
        RuntimeException failedException = new RuntimeException("Runtime Exception", iaeCausedBy);
        NullPointerException npeSuppressed = new NullPointerException("Null Pointer Exception");
        failedException.addSuppressed(npeSuppressed);
        npeSuppressed.addSuppressed(iaeCausedBy);
        TestDescriptorStub testDescriptor = new TestDescriptorStub(UniqueId.root("root", "2"), "failingTest") {
            @Override
            public Optional<TestSource> getSource() {
                return Optional.of(ClassSource.from(Object.class));
            }
        };
        TestIdentifier failed = TestIdentifier.from(testDescriptor);
        listener.testPlanExecutionStarted(testPlan);
        listener.executionStarted(failed);
        listener.executionFinished(failed, TestExecutionResult.failed(failedException));
        listener.testPlanExecutionFinished(testPlan);
        Assertions.assertEquals(1, listener.getSummary().getTestsFailedCount());
        String failuresString = failuresAsString();
        // 
        // 
        // 
        // 
        Assertions.assertAll("failures", () -> Assertions.assertTrue(failuresString.contains(("Suppressed: " + npeSuppressed)), "Suppressed exception"), () -> Assertions.assertTrue(failuresString.contains(("Circular reference: " + iaeCausedBy)), "Circular reference"), () -> Assertions.assertFalse(failuresString.contains("Caused by: "), "'Caused by: ' omitted because of Circular reference"));
    }
}

