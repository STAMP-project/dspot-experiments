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
package org.apache.commons.lang3.exception;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * JUnit tests for {@link CloneFailedExceptionTest}.
 */
public class CloneFailedExceptionTest extends AbstractExceptionTest {
    @Test
    public void testThrowingInformativeException() {
        Assertions.assertThrows(CloneFailedException.class, () -> {
            throw new CloneFailedException(AbstractExceptionTest.EXCEPTION_MESSAGE, generateCause());
        });
    }

    @Test
    public void testThrowingExceptionWithMessage() {
        Assertions.assertThrows(CloneFailedException.class, () -> {
            throw new CloneFailedException(AbstractExceptionTest.EXCEPTION_MESSAGE);
        });
    }

    @Test
    public void testThrowingExceptionWithCause() {
        Assertions.assertThrows(CloneFailedException.class, () -> {
            throw new CloneFailedException(generateCause());
        });
    }

    @Test
    public void testWithCauseAndMessage() {
        final Exception exception = new CloneFailedException(AbstractExceptionTest.EXCEPTION_MESSAGE, generateCause());
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AbstractExceptionTest.EXCEPTION_MESSAGE, exception.getMessage(), AbstractExceptionTest.WRONG_EXCEPTION_MESSAGE);
        final Throwable cause = exception.getCause();
        Assertions.assertNotNull(cause);
        Assertions.assertEquals(AbstractExceptionTest.CAUSE_MESSAGE, cause.getMessage(), AbstractExceptionTest.WRONG_CAUSE_MESSAGE);
    }

    @Test
    public void testWithoutCause() {
        final Exception exception = new CloneFailedException(AbstractExceptionTest.EXCEPTION_MESSAGE);
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AbstractExceptionTest.EXCEPTION_MESSAGE, exception.getMessage(), AbstractExceptionTest.WRONG_EXCEPTION_MESSAGE);
        final Throwable cause = exception.getCause();
        Assertions.assertNull(cause);
    }

    @Test
    public void testWithoutMessage() {
        final Exception exception = new CloneFailedException(generateCause());
        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getMessage());
        final Throwable cause = exception.getCause();
        Assertions.assertNotNull(cause);
        Assertions.assertEquals(AbstractExceptionTest.CAUSE_MESSAGE, cause.getMessage(), AbstractExceptionTest.WRONG_CAUSE_MESSAGE);
    }
}

