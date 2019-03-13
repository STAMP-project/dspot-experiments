/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


/**
 * These tests check that ThrowsException#answer throws an instance returned
 * by Throwable#fillInStackTrace of the provided throwable.
 *
 * <p>A well-behaved Throwable implementation must always return a reference to this
 * from #fillInStackTrace according to the method contract.
 * However, Mockito throws the exception returned from #fillInStackTrace for backwards compatibility
 * (or the provided exception if the method returns null).
 *
 * @see Throwable#fillInStackTrace()
 * @see <a href="https://github.com/mockito/mockito/issues/866">#866</a>
 */
public class FillInStackTraceScenariosTest extends TestBase {
    @Mock
    IMethods mock;

    private class SomeException extends RuntimeException {}

    class NullStackTraceException extends RuntimeException {
        public Exception fillInStackTrace() {
            return null;
        }
    }

    class NewStackTraceException extends RuntimeException {
        public Exception fillInStackTrace() {
            return new FillInStackTraceScenariosTest.SomeException();
        }
    }

    // issue 866
    @Test
    public void avoids_NPE() {
        Mockito.when(mock.simpleMethod()).thenThrow(new FillInStackTraceScenariosTest.NullStackTraceException());
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (FillInStackTraceScenariosTest.NullStackTraceException e) {
        }
    }

    @Test
    public void uses_return_value_from_fillInStackTrace() {
        Mockito.when(mock.simpleMethod()).thenThrow(new FillInStackTraceScenariosTest.NewStackTraceException());
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (FillInStackTraceScenariosTest.SomeException e) {
        }
    }
}

