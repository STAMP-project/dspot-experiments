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
package com.google.devtools.build.lib.analysis;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for AspectDescriptor.
 */
@RunWith(JUnit4.class)
public class AspectDescriptorTest {
    @Test
    public void serializeDescriptorNoArguments() {
        AspectDescriptorTest.assertDescription("foobar", "foobar");
    }

    @Test
    public void serializeDescriptorArgument() {
        AspectDescriptorTest.assertDescription("foobar[x=\"1\"]", "foobar", "x", "1");
    }

    @Test
    public void serializeDescriptorArgumentEscaped() {
        AspectDescriptorTest.assertDescription("foobar[x=\"\\\"1\\\"\"]", "foobar", "x", "\"1\"");
    }

    @Test
    public void serializeDescriptorTwoArguments() {
        AspectDescriptorTest.assertDescription("foobar[x=\"1\",y=\"2\"]", "foobar", "x", "1", "y", "2");
    }

    @Test
    public void serializeDescriptorTwoArgumentsMulti() {
        AspectDescriptorTest.assertDescription("foobar[x=\"1\",y=\"2\",y=\"3\"]", "foobar", "x", "1", "y", "2", "y", "3");
    }
}

