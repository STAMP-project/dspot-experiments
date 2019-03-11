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
package org.apache.flink.util;


import ExceptionUtils.STRINGIFIED_NULL_EXCEPTION;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the utility methods in {@link ExceptionUtils}.
 */
public class ExceptionUtilsTest extends TestLogger {
    @Test
    public void testStringifyNullException() {
        Assert.assertNotNull(STRINGIFIED_NULL_EXCEPTION);
        Assert.assertEquals(STRINGIFIED_NULL_EXCEPTION, ExceptionUtils.stringifyException(null));
    }

    @Test
    public void testJvmFatalError() {
        // not all errors are fatal
        Assert.assertFalse(ExceptionUtils.isJvmFatalError(new Error()));
        // linkage errors are not fatal
        Assert.assertFalse(ExceptionUtils.isJvmFatalError(new LinkageError()));
        // some errors are fatal
        Assert.assertTrue(ExceptionUtils.isJvmFatalError(new InternalError()));
        Assert.assertTrue(ExceptionUtils.isJvmFatalError(new UnknownError()));
    }

    @Test
    public void testRethrowFatalError() {
        // fatal error is rethrown
        try {
            ExceptionUtils.rethrowIfFatalError(new InternalError());
            Assert.fail();
        } catch (InternalError ignored) {
        }
        // non-fatal error is not rethrown
        ExceptionUtils.rethrowIfFatalError(new NoClassDefFoundError());
    }

    @Test
    public void testFindThrowableByType() {
        Assert.assertTrue(ExceptionUtils.findThrowable(new RuntimeException(new IllegalStateException()), IllegalStateException.class).isPresent());
    }

    @Test
    public void testExceptionStripping() {
        final FlinkException expectedException = new FlinkException("test exception");
        final Throwable strippedException = ExceptionUtils.stripException(new RuntimeException(new RuntimeException(expectedException)), RuntimeException.class);
        Assert.assertThat(strippedException, Matchers.is(Matchers.equalTo(expectedException)));
    }

    @Test
    public void testInvalidExceptionStripping() {
        final FlinkException expectedException = new FlinkException(new RuntimeException(new FlinkException("inner exception")));
        final Throwable strippedException = ExceptionUtils.stripException(expectedException, RuntimeException.class);
        Assert.assertThat(strippedException, Matchers.is(Matchers.equalTo(expectedException)));
    }
}

