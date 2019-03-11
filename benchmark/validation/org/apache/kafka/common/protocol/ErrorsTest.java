/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.protocol;


import Errors.INVALID_TOPIC_EXCEPTION;
import Errors.NONE;
import Errors.UNKNOWN_SERVER_ERROR;
import java.util.HashSet;
import java.util.Set;
import org.apache.kafka.common.errors.ApiException;
import org.apache.kafka.common.errors.TimeoutException;
import org.junit.Assert;
import org.junit.Test;

import static Errors.NONE;


public class ErrorsTest {
    @Test
    public void testUniqueErrorCodes() {
        Set<Short> codeSet = new HashSet<>();
        for (Errors error : Errors.values()) {
            codeSet.add(error.code());
        }
        Assert.assertEquals("Error codes must be unique", codeSet.size(), Errors.values().length);
    }

    @Test
    public void testUniqueExceptions() {
        Set<Class> exceptionSet = new HashSet<>();
        for (Errors error : Errors.values()) {
            if (error != (NONE))
                exceptionSet.add(error.exception().getClass());

        }
        Assert.assertEquals("Exceptions must be unique", exceptionSet.size(), ((Errors.values().length) - 1));// Ignore NONE

    }

    @Test
    public void testExceptionsAreNotGeneric() {
        for (Errors error : Errors.values()) {
            if (error != (NONE))
                Assert.assertNotEquals("Generic ApiException should not be used", error.exception().getClass(), ApiException.class);

        }
    }

    @Test
    public void testNoneException() {
        Assert.assertNull("The NONE error should not have an exception", NONE.exception());
    }

    @Test
    public void testForExceptionInheritance() {
        class ExtendedTimeoutException extends TimeoutException {}
        Errors expectedError = Errors.forException(new TimeoutException());
        Errors actualError = Errors.forException(new ExtendedTimeoutException());
        Assert.assertEquals("forException should match super classes", expectedError, actualError);
    }

    @Test
    public void testForExceptionDefault() {
        Errors error = Errors.forException(new ApiException());
        Assert.assertEquals("forException should default to unknown", UNKNOWN_SERVER_ERROR, error);
    }

    @Test
    public void testExceptionName() {
        String exceptionName = UNKNOWN_SERVER_ERROR.exceptionName();
        Assert.assertEquals("org.apache.kafka.common.errors.UnknownServerException", exceptionName);
        exceptionName = NONE.exceptionName();
        Assert.assertNull(exceptionName);
        exceptionName = INVALID_TOPIC_EXCEPTION.exceptionName();
        Assert.assertEquals("org.apache.kafka.common.errors.InvalidTopicException", exceptionName);
    }
}

