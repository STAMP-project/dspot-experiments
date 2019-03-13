/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.coders;


import Context.NESTED;
import Context.OUTER;
import java.util.Arrays;
import java.util.Collections;
import org.apache.beam.sdk.coders.Coder.Context;
import org.apache.beam.sdk.coders.Coder.NonDeterministicException;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for constructs defined within {@link Coder}.
 */
@RunWith(JUnit4.class)
public class CoderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testContextEqualsAndHashCode() {
        Assert.assertEquals(NESTED, new Context(false));
        Assert.assertEquals(OUTER, new Context(true));
        Assert.assertNotEquals(NESTED, OUTER);
        Assert.assertEquals(NESTED.hashCode(), new Context(false).hashCode());
        Assert.assertEquals(OUTER.hashCode(), new Context(true).hashCode());
        // Even though this isn't strictly required by the hashCode contract,
        // we still want this to be true.
        Assert.assertNotEquals(NESTED.hashCode(), OUTER.hashCode());
    }

    @Test
    public void testContextToString() {
        Assert.assertEquals("Context{NESTED}", NESTED.toString());
        Assert.assertEquals("Context{OUTER}", OUTER.toString());
    }

    @Test
    public void testNonDeterministicExceptionRequiresReason() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Reasons must not be empty");
        new NonDeterministicException(VoidCoder.of(), Collections.emptyList());
    }

    @Test
    public void testNonDeterministicException() {
        NonDeterministicException rootCause = new NonDeterministicException(VoidCoder.of(), "Root Cause");
        NonDeterministicException exception = new NonDeterministicException(StringUtf8Coder.of(), "Problem", rootCause);
        Assert.assertEquals(rootCause, exception.getCause());
        Assert.assertThat(exception.getReasons(), Matchers.contains("Problem"));
        Assert.assertThat(exception.toString(), Matchers.containsString("Problem"));
        Assert.assertThat(exception.toString(), Matchers.containsString("is not deterministic"));
    }

    @Test
    public void testNonDeterministicExceptionMultipleReasons() {
        NonDeterministicException rootCause = new NonDeterministicException(VoidCoder.of(), "Root Cause");
        NonDeterministicException exception = new NonDeterministicException(StringUtf8Coder.of(), Arrays.asList("Problem1", "Problem2"), rootCause);
        String expectedMessage = "StringUtf8Coder is not deterministic because:\n\tProblem1\n\tProblem2";
        Assert.assertThat(exception.getMessage(), Matchers.equalTo(expectedMessage));
    }

    @Test
    public void testTypeIsPreserved() throws Exception {
        Assert.assertThat(VoidCoder.of().getEncodedTypeDescriptor(), Matchers.equalTo(TypeDescriptor.of(Void.class)));
    }
}

