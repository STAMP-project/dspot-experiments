/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.di.repository.pur;


import java.net.ConnectException;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.repository.KettleRepositoryLostException;


public class UnifiedRepositoryInvocationHandlerTest {
    private static interface IFace {
        Object doNotThrowException();

        Object throwSomeException();

        Object throwChainedConnectException();
    }

    private static final Object returnValue = "return-value";

    private static final RuntimeException rte = new RuntimeException("some-exception");

    private static final ConnectException connectException = new ConnectException();

    private static final UnifiedRepositoryInvocationHandlerTest.IFace wrappee = new UnifiedRepositoryInvocationHandlerTest.IFace() {
        @Override
        public Object doNotThrowException() {
            return UnifiedRepositoryInvocationHandlerTest.returnValue;
        }

        @Override
        public Object throwSomeException() {
            throw UnifiedRepositoryInvocationHandlerTest.rte;
        }

        @Override
        public Object throwChainedConnectException() {
            throw new RuntimeException("wrapper-exception", UnifiedRepositoryInvocationHandlerTest.connectException);
        }
    };

    UnifiedRepositoryInvocationHandlerTest.IFace testee;

    @Test
    public void testNormalCall() {
        Assert.assertEquals("the method did not return what was expected", UnifiedRepositoryInvocationHandlerTest.returnValue, testee.doNotThrowException());
    }

    @Test
    public void testThrowingSomeException() {
        try {
            testee.throwSomeException();
        } catch (RuntimeException actual) {
            Assert.assertEquals("did not get the expected runtime exception", UnifiedRepositoryInvocationHandlerTest.rte, actual);
        }
    }

    @Test
    public void testThrowingConnectException() {
        try {
            testee.throwChainedConnectException();
        } catch (KettleRepositoryLostException krle) {
            Throwable found = krle;
            while (found != null) {
                if (UnifiedRepositoryInvocationHandlerTest.connectException.equals(found)) {
                    break;
                }
                found = found.getCause();
            } 
            Assert.assertNotNull("Should have found the original ConnectException");
        } catch (Throwable other) {
            Assert.fail("Should not catch something other than KettleRepositoryLostException");
        }
    }
}

