/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android.desugar;


import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtensionTestUtility;
import com.google.devtools.build.android.desugar.testdata.ClassUsingTryWithResources;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * The functional test for desugaring try-with-resources.
 */
@RunWith(JUnit4.class)
public class DesugarTryWithResourcesFunctionalTest {
    @Test
    public void testCheckSuppressedExceptionsReturningEmptySuppressedExceptions() {
        Throwable[] suppressed = ClassUsingTryWithResources.checkSuppressedExceptions(false);
        assertThat(suppressed).isEmpty();
    }

    @Test
    public void testPrintStackTraceOfCaughtException() {
        String trace = ClassUsingTryWithResources.printStackTraceOfCaughtException();
        if (ThrowableExtensionTestUtility.isMimicStrategy()) {
            assertThat(trace.toLowerCase()).contains("suppressed");
        } else
            if (ThrowableExtensionTestUtility.isReuseStrategy()) {
                assertThat(trace.toLowerCase()).contains("suppressed");
            } else
                if (ThrowableExtensionTestUtility.isNullStrategy()) {
                    assertThat(trace.toLowerCase()).doesNotContain("suppressed");
                } else {
                    Assert.fail(("unexpected desugaring strategy " + (ThrowableExtension.getStrategy())));
                }


    }

    @Test
    public void testCheckSuppressedExceptionReturningOneSuppressedException() {
        Throwable[] suppressed = ClassUsingTryWithResources.checkSuppressedExceptions(true);
        if (ThrowableExtensionTestUtility.isMimicStrategy()) {
            assertThat(suppressed).hasLength(1);
        } else
            if (ThrowableExtensionTestUtility.isReuseStrategy()) {
                assertThat(suppressed).hasLength(1);
            } else
                if (ThrowableExtensionTestUtility.isNullStrategy()) {
                    assertThat(suppressed).isEmpty();
                } else {
                    Assert.fail(("unexpected desugaring strategy " + (ThrowableExtension.getStrategy())));
                }


    }

    @Test
    public void testSimpleTryWithResources() {
        try {
            ClassUsingTryWithResources.simpleTryWithResources();
            Assert.fail("Expected RuntimeException");
        } catch (Exception expected) {
            assertThat(expected.getClass()).isEqualTo(RuntimeException.class);
            String expectedStrategyName = ThrowableExtensionTestUtility.getTwrStrategyClassNameSpecifiedInSystemProperty();
            assertThat(ThrowableExtensionTestUtility.getStrategyClassName()).isEqualTo(expectedStrategyName);
            if (ThrowableExtensionTestUtility.isMimicStrategy()) {
                assertThat(expected.getSuppressed()).isEmpty();
                assertThat(ThrowableExtension.getSuppressed(expected)).hasLength(1);
                assertThat(ThrowableExtension.getSuppressed(expected)[0].getClass()).isEqualTo(IOException.class);
            } else
                if (ThrowableExtensionTestUtility.isReuseStrategy()) {
                    assertThat(expected.getSuppressed()).hasLength(1);
                    assertThat(expected.getSuppressed()[0].getClass()).isEqualTo(IOException.class);
                    assertThat(ThrowableExtension.getSuppressed(expected)[0].getClass()).isEqualTo(IOException.class);
                } else
                    if (ThrowableExtensionTestUtility.isNullStrategy()) {
                        assertThat(expected.getSuppressed()).isEmpty();
                        assertThat(ThrowableExtension.getSuppressed(expected)).isEmpty();
                    } else {
                        Assert.fail(("unexpected desugaring strategy " + (ThrowableExtensionTestUtility.getStrategyClassName())));
                    }


        }
    }

    @Test
    public void testInheritanceTryWithResources() {
        try {
            ClassUsingTryWithResources.inheritanceTryWithResources();
            Assert.fail("Expected RuntimeException");
        } catch (Exception expected) {
            assertThat(expected.getClass()).isEqualTo(RuntimeException.class);
            String expectedStrategyName = ThrowableExtensionTestUtility.getTwrStrategyClassNameSpecifiedInSystemProperty();
            assertThat(ThrowableExtensionTestUtility.getStrategyClassName()).isEqualTo(expectedStrategyName);
            if (ThrowableExtensionTestUtility.isMimicStrategy()) {
                assertThat(expected.getSuppressed()).isEmpty();
                assertThat(ThrowableExtension.getSuppressed(expected)).hasLength(1);
                assertThat(ThrowableExtension.getSuppressed(expected)[0].getClass()).isEqualTo(IOException.class);
            } else
                if (ThrowableExtensionTestUtility.isReuseStrategy()) {
                    assertThat(expected.getSuppressed()).hasLength(1);
                    assertThat(expected.getSuppressed()[0].getClass()).isEqualTo(IOException.class);
                    assertThat(ThrowableExtension.getSuppressed(expected)[0].getClass()).isEqualTo(IOException.class);
                } else
                    if (ThrowableExtensionTestUtility.isNullStrategy()) {
                        assertThat(expected.getSuppressed()).isEmpty();
                        assertThat(ThrowableExtension.getSuppressed(expected)).isEmpty();
                    } else {
                        Assert.fail(("unexpected desugaring strategy " + (ThrowableExtensionTestUtility.getStrategyClassName())));
                    }


        }
    }
}

