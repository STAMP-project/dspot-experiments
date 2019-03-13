/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.concurrent;


import org.apache.commons.lang3.exception.AbstractExceptionTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * JUnit tests for {@link CircuitBreakingException}.
 */
public class CircuitBreakingExceptionTest extends AbstractExceptionTest {
    @Test
    public void testThrowingInformativeException() {
        Assertions.assertThrows(CircuitBreakingException.class, () -> {
            throw new CircuitBreakingException(AbstractExceptionTest.EXCEPTION_MESSAGE, generateCause());
        });
    }

    @Test
    public void testThrowingExceptionWithMessage() {
        Assertions.assertThrows(CircuitBreakingException.class, () -> {
            throw new CircuitBreakingException(AbstractExceptionTest.EXCEPTION_MESSAGE);
        });
    }

    @Test
    public void testThrowingExceptionWithCause() {
        Assertions.assertThrows(CircuitBreakingException.class, () -> {
            throw new CircuitBreakingException(generateCause());
        });
    }

    @Test
    public void testThrowingEmptyException() {
        Assertions.assertThrows(CircuitBreakingException.class, () -> {
            throw new CircuitBreakingException();
        });
    }

    @Test
    public void testWithCauseAndMessage() {
        final Exception exception = new CircuitBreakingException(AbstractExceptionTest.EXCEPTION_MESSAGE, generateCause());
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AbstractExceptionTest.EXCEPTION_MESSAGE, exception.getMessage(), AbstractExceptionTest.WRONG_EXCEPTION_MESSAGE);
        final Throwable cause = exception.getCause();
        Assertions.assertNotNull(cause);
        Assertions.assertEquals(AbstractExceptionTest.CAUSE_MESSAGE, cause.getMessage(), AbstractExceptionTest.WRONG_CAUSE_MESSAGE);
    }

    @Test
    public void testWithoutCause() {
        final Exception exception = new CircuitBreakingException(AbstractExceptionTest.EXCEPTION_MESSAGE);
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AbstractExceptionTest.EXCEPTION_MESSAGE, exception.getMessage(), AbstractExceptionTest.WRONG_EXCEPTION_MESSAGE);
        final Throwable cause = exception.getCause();
        Assertions.assertNull(cause);
    }

    @Test
    public void testWithoutMessage() {
        final Exception exception = new CircuitBreakingException(generateCause());
        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getMessage());
        final Throwable cause = exception.getCause();
        Assertions.assertNotNull(cause);
        Assertions.assertEquals(AbstractExceptionTest.CAUSE_MESSAGE, cause.getMessage(), AbstractExceptionTest.WRONG_CAUSE_MESSAGE);
    }
}

