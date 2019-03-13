/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.messaging.unitofwork;


import org.junit.Assert;
import org.junit.Test;

import static RollbackConfigurationType.ANY_THROWABLE;
import static RollbackConfigurationType.RUNTIME_EXCEPTIONS;
import static RollbackConfigurationType.UNCHECKED_EXCEPTIONS;


/**
 *
 *
 * @author Allard Buijze
 */
public class RollbackConfigurationTypeTest {
    @Test
    public void testAnyExceptionsRollback() {
        RollbackConfiguration testSubject = ANY_THROWABLE;
        Assert.assertTrue(testSubject.rollBackOn(new RuntimeException()));
        Assert.assertTrue(testSubject.rollBackOn(new Exception()));
        Assert.assertTrue(testSubject.rollBackOn(new AssertionError()));
    }

    @Test
    public void testUncheckedExceptionsRollback() {
        RollbackConfiguration testSubject = UNCHECKED_EXCEPTIONS;
        Assert.assertTrue(testSubject.rollBackOn(new RuntimeException()));
        Assert.assertFalse(testSubject.rollBackOn(new Exception()));
        Assert.assertTrue(testSubject.rollBackOn(new AssertionError()));
    }

    @Test
    public void testRuntimeExceptionsRollback() {
        RollbackConfiguration testSubject = RUNTIME_EXCEPTIONS;
        Assert.assertTrue(testSubject.rollBackOn(new RuntimeException()));
        Assert.assertFalse(testSubject.rollBackOn(new Exception()));
        Assert.assertFalse(testSubject.rollBackOn(new AssertionError()));
    }
}

