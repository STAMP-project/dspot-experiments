/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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


import com.google.common.base.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for {@link Desugar}
 */
@RunWith(JUnit4.class)
public class DesugarMainClassTest {
    @Test
    public void testVerifyLambdaDumpDirectoryRegistration() throws Exception {
        if (Strings.isNullOrEmpty(System.getProperty(LambdaClassMaker.LAMBDA_METAFACTORY_DUMPER_PROPERTY))) {
            testLambdaDumpDirSpecifiedInProgramFail();
        } else {
            testLambdaDumpDirPassSpecifiedInCmdPass();
        }
    }
}

