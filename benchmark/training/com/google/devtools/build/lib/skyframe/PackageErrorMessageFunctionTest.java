/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.skyframe;


import Result.ERROR;
import Result.NO_ERROR;
import Result.NO_SUCH_PACKAGE_EXCEPTION;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PackageErrorMessageFunction}.
 */
@RunWith(JUnit4.class)
public class PackageErrorMessageFunctionTest extends BuildViewTestCase {
    private SkyframeExecutor skyframeExecutor;

    @Test
    public void testNoErrorMessage() throws Exception {
        scratch.file("a/BUILD");
        assertThat(/* keepGoing= */
        getPackageErrorMessageValue(false).getResult()).isEqualTo(NO_ERROR);
    }

    @Test
    public void testPackageWithErrors() throws Exception {
        // Opt out of failing fast on an error event.
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("a/BUILD", "imaginary_macro(name = 'this macro is not defined')");
        assertThat(/* keepGoing= */
        getPackageErrorMessageValue(false).getResult()).isEqualTo(ERROR);
    }

    @Test
    public void testNoSuchPackageException() throws Exception {
        scratch.file("a/BUILD", "load('//a:does_not_exist.bzl', 'imaginary_macro')");
        PackageErrorMessageValue packageErrorMessageValue = /* keepGoing= */
        getPackageErrorMessageValue(true);
        assertThat(packageErrorMessageValue.getResult()).isEqualTo(NO_SUCH_PACKAGE_EXCEPTION);
        assertThat(packageErrorMessageValue.getNoSuchPackageExceptionMessage()).isEqualTo(("error loading package 'a': Unable to load file " + "'//a:does_not_exist.bzl': file doesn't exist"));
    }
}

