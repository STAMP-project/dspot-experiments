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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link org.apache.commons.lang3.exception.ExceptionUtils}.
 *
 * @since 1.0
 */
public class ExceptionUtilsTest {
    private ExceptionUtilsTest.NestableException nested;

    private Throwable withCause;

    private Throwable withoutCause;

    private Throwable jdkNoCause;

    private ExceptionUtilsTest.ExceptionWithCause cyclicCause;

    private Throwable notVisibleException;

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new ExceptionUtils());
        final Constructor<?>[] cons = ExceptionUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(ExceptionUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(ExceptionUtils.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    // Specifically tests the deprecated methods
    @SuppressWarnings("deprecation")
    @Test
    public void testGetCause_Throwable() {
        Assertions.assertSame(null, ExceptionUtils.getCause(null));
        Assertions.assertSame(null, ExceptionUtils.getCause(withoutCause));
        Assertions.assertSame(withoutCause, ExceptionUtils.getCause(nested));
        Assertions.assertSame(nested, ExceptionUtils.getCause(withCause));
        Assertions.assertSame(null, ExceptionUtils.getCause(jdkNoCause));
        Assertions.assertSame(cyclicCause.getCause(), ExceptionUtils.getCause(cyclicCause));
        Assertions.assertSame(cyclicCause.getCause().getCause(), ExceptionUtils.getCause(cyclicCause.getCause()));
        Assertions.assertSame(cyclicCause.getCause(), ExceptionUtils.getCause(cyclicCause.getCause().getCause()));
        Assertions.assertSame(withoutCause, ExceptionUtils.getCause(notVisibleException));
    }

    // Specifically tests the deprecated methods
    @SuppressWarnings("deprecation")
    @Test
    public void testGetCause_ThrowableArray() {
        Assertions.assertSame(null, ExceptionUtils.getCause(null, null));
        Assertions.assertSame(null, ExceptionUtils.getCause(null, new String[0]));
        // not known type, so match on supplied method names
        Assertions.assertSame(nested, ExceptionUtils.getCause(withCause, null));// default names

        Assertions.assertSame(null, ExceptionUtils.getCause(withCause, new String[0]));
        Assertions.assertSame(null, ExceptionUtils.getCause(withCause, new String[]{ null }));
        Assertions.assertSame(nested, ExceptionUtils.getCause(withCause, new String[]{ "getCause" }));
        // not known type, so match on supplied method names
        Assertions.assertSame(null, ExceptionUtils.getCause(withoutCause, null));
        Assertions.assertSame(null, ExceptionUtils.getCause(withoutCause, new String[0]));
        Assertions.assertSame(null, ExceptionUtils.getCause(withoutCause, new String[]{ null }));
        Assertions.assertSame(null, ExceptionUtils.getCause(withoutCause, new String[]{ "getCause" }));
        Assertions.assertSame(null, ExceptionUtils.getCause(withoutCause, new String[]{ "getTargetException" }));
    }

    @Test
    public void testGetRootCause_Throwable() {
        Assertions.assertSame(null, ExceptionUtils.getRootCause(null));
        Assertions.assertSame(withoutCause, ExceptionUtils.getRootCause(withoutCause));
        Assertions.assertSame(withoutCause, ExceptionUtils.getRootCause(nested));
        Assertions.assertSame(withoutCause, ExceptionUtils.getRootCause(withCause));
        Assertions.assertSame(jdkNoCause, ExceptionUtils.getRootCause(jdkNoCause));
        Assertions.assertSame(cyclicCause.getCause().getCause(), ExceptionUtils.getRootCause(cyclicCause));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetThrowableCount_Throwable() {
        Assertions.assertEquals(0, ExceptionUtils.getThrowableCount(null));
        Assertions.assertEquals(1, ExceptionUtils.getThrowableCount(withoutCause));
        Assertions.assertEquals(2, ExceptionUtils.getThrowableCount(nested));
        Assertions.assertEquals(3, ExceptionUtils.getThrowableCount(withCause));
        Assertions.assertEquals(1, ExceptionUtils.getThrowableCount(jdkNoCause));
        Assertions.assertEquals(3, ExceptionUtils.getThrowableCount(cyclicCause));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetThrowables_Throwable_null() {
        Assertions.assertEquals(0, ExceptionUtils.getThrowables(null).length);
    }

    @Test
    public void testGetThrowables_Throwable_withoutCause() {
        final Throwable[] throwables = ExceptionUtils.getThrowables(withoutCause);
        Assertions.assertEquals(1, throwables.length);
        Assertions.assertSame(withoutCause, throwables[0]);
    }

    @Test
    public void testGetThrowables_Throwable_nested() {
        final Throwable[] throwables = ExceptionUtils.getThrowables(nested);
        Assertions.assertEquals(2, throwables.length);
        Assertions.assertSame(nested, throwables[0]);
        Assertions.assertSame(withoutCause, throwables[1]);
    }

    @Test
    public void testGetThrowables_Throwable_withCause() {
        final Throwable[] throwables = ExceptionUtils.getThrowables(withCause);
        Assertions.assertEquals(3, throwables.length);
        Assertions.assertSame(withCause, throwables[0]);
        Assertions.assertSame(nested, throwables[1]);
        Assertions.assertSame(withoutCause, throwables[2]);
    }

    @Test
    public void testGetThrowables_Throwable_jdkNoCause() {
        final Throwable[] throwables = ExceptionUtils.getThrowables(jdkNoCause);
        Assertions.assertEquals(1, throwables.length);
        Assertions.assertSame(jdkNoCause, throwables[0]);
    }

    @Test
    public void testGetThrowables_Throwable_recursiveCause() {
        final Throwable[] throwables = ExceptionUtils.getThrowables(cyclicCause);
        Assertions.assertEquals(3, throwables.length);
        Assertions.assertSame(cyclicCause, throwables[0]);
        Assertions.assertSame(cyclicCause.getCause(), throwables[1]);
        Assertions.assertSame(cyclicCause.getCause().getCause(), throwables[2]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetThrowableList_Throwable_null() {
        final List<?> throwables = ExceptionUtils.getThrowableList(null);
        Assertions.assertEquals(0, throwables.size());
    }

    @Test
    public void testGetThrowableList_Throwable_withoutCause() {
        final List<?> throwables = ExceptionUtils.getThrowableList(withoutCause);
        Assertions.assertEquals(1, throwables.size());
        Assertions.assertSame(withoutCause, throwables.get(0));
    }

    @Test
    public void testGetThrowableList_Throwable_nested() {
        final List<?> throwables = ExceptionUtils.getThrowableList(nested);
        Assertions.assertEquals(2, throwables.size());
        Assertions.assertSame(nested, throwables.get(0));
        Assertions.assertSame(withoutCause, throwables.get(1));
    }

    @Test
    public void testGetThrowableList_Throwable_withCause() {
        final List<?> throwables = ExceptionUtils.getThrowableList(withCause);
        Assertions.assertEquals(3, throwables.size());
        Assertions.assertSame(withCause, throwables.get(0));
        Assertions.assertSame(nested, throwables.get(1));
        Assertions.assertSame(withoutCause, throwables.get(2));
    }

    @Test
    public void testGetThrowableList_Throwable_jdkNoCause() {
        final List<?> throwables = ExceptionUtils.getThrowableList(jdkNoCause);
        Assertions.assertEquals(1, throwables.size());
        Assertions.assertSame(jdkNoCause, throwables.get(0));
    }

    @Test
    public void testGetThrowableList_Throwable_recursiveCause() {
        final List<?> throwables = ExceptionUtils.getThrowableList(cyclicCause);
        Assertions.assertEquals(3, throwables.size());
        Assertions.assertSame(cyclicCause, throwables.get(0));
        Assertions.assertSame(cyclicCause.getCause(), throwables.get(1));
        Assertions.assertSame(cyclicCause.getCause().getCause(), throwables.get(2));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOf_ThrowableClass() {
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(null, null));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(null, ExceptionUtilsTest.NestableException.class));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withoutCause, null));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withoutCause, ExceptionUtilsTest.ExceptionWithCause.class));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withoutCause, ExceptionUtilsTest.NestableException.class));
        Assertions.assertEquals(0, ExceptionUtils.indexOfThrowable(withoutCause, ExceptionUtilsTest.ExceptionWithoutCause.class));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(nested, null));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(nested, ExceptionUtilsTest.ExceptionWithCause.class));
        Assertions.assertEquals(0, ExceptionUtils.indexOfThrowable(nested, ExceptionUtilsTest.NestableException.class));
        Assertions.assertEquals(1, ExceptionUtils.indexOfThrowable(nested, ExceptionUtilsTest.ExceptionWithoutCause.class));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withCause, null));
        Assertions.assertEquals(0, ExceptionUtils.indexOfThrowable(withCause, ExceptionUtilsTest.ExceptionWithCause.class));
        Assertions.assertEquals(1, ExceptionUtils.indexOfThrowable(withCause, ExceptionUtilsTest.NestableException.class));
        Assertions.assertEquals(2, ExceptionUtils.indexOfThrowable(withCause, ExceptionUtilsTest.ExceptionWithoutCause.class));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withCause, Exception.class));
    }

    @Test
    public void testIndexOf_ThrowableClassInt() {
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(null, null, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(null, ExceptionUtilsTest.NestableException.class, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withoutCause, null));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withoutCause, ExceptionUtilsTest.ExceptionWithCause.class, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withoutCause, ExceptionUtilsTest.NestableException.class, 0));
        Assertions.assertEquals(0, ExceptionUtils.indexOfThrowable(withoutCause, ExceptionUtilsTest.ExceptionWithoutCause.class, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(nested, null, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(nested, ExceptionUtilsTest.ExceptionWithCause.class, 0));
        Assertions.assertEquals(0, ExceptionUtils.indexOfThrowable(nested, ExceptionUtilsTest.NestableException.class, 0));
        Assertions.assertEquals(1, ExceptionUtils.indexOfThrowable(nested, ExceptionUtilsTest.ExceptionWithoutCause.class, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withCause, null));
        Assertions.assertEquals(0, ExceptionUtils.indexOfThrowable(withCause, ExceptionUtilsTest.ExceptionWithCause.class, 0));
        Assertions.assertEquals(1, ExceptionUtils.indexOfThrowable(withCause, ExceptionUtilsTest.NestableException.class, 0));
        Assertions.assertEquals(2, ExceptionUtils.indexOfThrowable(withCause, ExceptionUtilsTest.ExceptionWithoutCause.class, 0));
        Assertions.assertEquals(0, ExceptionUtils.indexOfThrowable(withCause, ExceptionUtilsTest.ExceptionWithCause.class, (-1)));
        Assertions.assertEquals(0, ExceptionUtils.indexOfThrowable(withCause, ExceptionUtilsTest.ExceptionWithCause.class, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withCause, ExceptionUtilsTest.ExceptionWithCause.class, 1));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withCause, ExceptionUtilsTest.ExceptionWithCause.class, 9));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfThrowable(withCause, Exception.class, 0));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOfType_ThrowableClass() {
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(null, null));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(null, ExceptionUtilsTest.NestableException.class));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(withoutCause, null));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(withoutCause, ExceptionUtilsTest.ExceptionWithCause.class));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(withoutCause, ExceptionUtilsTest.NestableException.class));
        Assertions.assertEquals(0, ExceptionUtils.indexOfType(withoutCause, ExceptionUtilsTest.ExceptionWithoutCause.class));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(nested, null));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(nested, ExceptionUtilsTest.ExceptionWithCause.class));
        Assertions.assertEquals(0, ExceptionUtils.indexOfType(nested, ExceptionUtilsTest.NestableException.class));
        Assertions.assertEquals(1, ExceptionUtils.indexOfType(nested, ExceptionUtilsTest.ExceptionWithoutCause.class));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(withCause, null));
        Assertions.assertEquals(0, ExceptionUtils.indexOfType(withCause, ExceptionUtilsTest.ExceptionWithCause.class));
        Assertions.assertEquals(1, ExceptionUtils.indexOfType(withCause, ExceptionUtilsTest.NestableException.class));
        Assertions.assertEquals(2, ExceptionUtils.indexOfType(withCause, ExceptionUtilsTest.ExceptionWithoutCause.class));
        Assertions.assertEquals(0, ExceptionUtils.indexOfType(withCause, Exception.class));
    }

    @Test
    public void testIndexOfType_ThrowableClassInt() {
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(null, null, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(null, ExceptionUtilsTest.NestableException.class, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(withoutCause, null));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(withoutCause, ExceptionUtilsTest.ExceptionWithCause.class, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(withoutCause, ExceptionUtilsTest.NestableException.class, 0));
        Assertions.assertEquals(0, ExceptionUtils.indexOfType(withoutCause, ExceptionUtilsTest.ExceptionWithoutCause.class, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(nested, null, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(nested, ExceptionUtilsTest.ExceptionWithCause.class, 0));
        Assertions.assertEquals(0, ExceptionUtils.indexOfType(nested, ExceptionUtilsTest.NestableException.class, 0));
        Assertions.assertEquals(1, ExceptionUtils.indexOfType(nested, ExceptionUtilsTest.ExceptionWithoutCause.class, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(withCause, null));
        Assertions.assertEquals(0, ExceptionUtils.indexOfType(withCause, ExceptionUtilsTest.ExceptionWithCause.class, 0));
        Assertions.assertEquals(1, ExceptionUtils.indexOfType(withCause, ExceptionUtilsTest.NestableException.class, 0));
        Assertions.assertEquals(2, ExceptionUtils.indexOfType(withCause, ExceptionUtilsTest.ExceptionWithoutCause.class, 0));
        Assertions.assertEquals(0, ExceptionUtils.indexOfType(withCause, ExceptionUtilsTest.ExceptionWithCause.class, (-1)));
        Assertions.assertEquals(0, ExceptionUtils.indexOfType(withCause, ExceptionUtilsTest.ExceptionWithCause.class, 0));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(withCause, ExceptionUtilsTest.ExceptionWithCause.class, 1));
        Assertions.assertEquals((-1), ExceptionUtils.indexOfType(withCause, ExceptionUtilsTest.ExceptionWithCause.class, 9));
        Assertions.assertEquals(0, ExceptionUtils.indexOfType(withCause, Exception.class, 0));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testPrintRootCauseStackTrace_Throwable() {
        ExceptionUtils.printRootCauseStackTrace(null);
        // could pipe system.err to a known stream, but not much point as
        // internally this method calls stream method anyway
    }

    @Test
    public void testPrintRootCauseStackTrace_ThrowableStream() {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        ExceptionUtils.printRootCauseStackTrace(null, ((PrintStream) (null)));
        ExceptionUtils.printRootCauseStackTrace(null, new PrintStream(out));
        Assertions.assertEquals(0, out.toString().length());
        out = new ByteArrayOutputStream(1024);
        Assertions.assertThrows(IllegalArgumentException.class, () -> ExceptionUtils.printRootCauseStackTrace(withCause, ((PrintStream) (null))));
        out = new ByteArrayOutputStream(1024);
        final Throwable cause = createExceptionWithCause();
        ExceptionUtils.printRootCauseStackTrace(cause, new PrintStream(out));
        String stackTrace = out.toString();
        Assertions.assertTrue(stackTrace.contains(ExceptionUtils.WRAPPED_MARKER));
        out = new ByteArrayOutputStream(1024);
        ExceptionUtils.printRootCauseStackTrace(withoutCause, new PrintStream(out));
        stackTrace = out.toString();
        Assertions.assertFalse(stackTrace.contains(ExceptionUtils.WRAPPED_MARKER));
    }

    @Test
    public void testPrintRootCauseStackTrace_ThrowableWriter() {
        StringWriter writer = new StringWriter(1024);
        ExceptionUtils.printRootCauseStackTrace(null, ((PrintWriter) (null)));
        ExceptionUtils.printRootCauseStackTrace(null, new PrintWriter(writer));
        Assertions.assertEquals(0, writer.getBuffer().length());
        writer = new StringWriter(1024);
        Assertions.assertThrows(IllegalArgumentException.class, () -> ExceptionUtils.printRootCauseStackTrace(withCause, ((PrintWriter) (null))));
        writer = new StringWriter(1024);
        final Throwable cause = createExceptionWithCause();
        ExceptionUtils.printRootCauseStackTrace(cause, new PrintWriter(writer));
        String stackTrace = writer.toString();
        Assertions.assertTrue(stackTrace.contains(ExceptionUtils.WRAPPED_MARKER));
        writer = new StringWriter(1024);
        ExceptionUtils.printRootCauseStackTrace(withoutCause, new PrintWriter(writer));
        stackTrace = writer.toString();
        Assertions.assertFalse(stackTrace.contains(ExceptionUtils.WRAPPED_MARKER));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetRootCauseStackTrace_Throwable() {
        Assertions.assertEquals(0, ExceptionUtils.getRootCauseStackTrace(null).length);
        final Throwable cause = createExceptionWithCause();
        String[] stackTrace = ExceptionUtils.getRootCauseStackTrace(cause);
        boolean match = false;
        for (final String element : stackTrace) {
            if (element.startsWith(ExceptionUtils.WRAPPED_MARKER)) {
                match = true;
                break;
            }
        }
        Assertions.assertTrue(match);
        stackTrace = ExceptionUtils.getRootCauseStackTrace(withoutCause);
        match = false;
        for (final String element : stackTrace) {
            if (element.startsWith(ExceptionUtils.WRAPPED_MARKER)) {
                match = true;
                break;
            }
        }
        Assertions.assertFalse(match);
    }

    @Test
    public void testRemoveCommonFrames_ListList() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ExceptionUtils.removeCommonFrames(null, null));
    }

    @Test
    public void test_getMessage_Throwable() {
        Throwable th = null;
        Assertions.assertEquals("", ExceptionUtils.getMessage(th));
        th = new IllegalArgumentException("Base");
        Assertions.assertEquals("IllegalArgumentException: Base", ExceptionUtils.getMessage(th));
        th = new ExceptionUtilsTest.ExceptionWithCause("Wrapper", th);
        Assertions.assertEquals("ExceptionUtilsTest.ExceptionWithCause: Wrapper", ExceptionUtils.getMessage(th));
    }

    @Test
    public void test_getRootCauseMessage_Throwable() {
        Throwable th = null;
        Assertions.assertEquals("", ExceptionUtils.getRootCauseMessage(th));
        th = new IllegalArgumentException("Base");
        Assertions.assertEquals("IllegalArgumentException: Base", ExceptionUtils.getRootCauseMessage(th));
        th = new ExceptionUtilsTest.ExceptionWithCause("Wrapper", th);
        Assertions.assertEquals("IllegalArgumentException: Base", ExceptionUtils.getRootCauseMessage(th));
    }

    // -----------------------------------------------------------------------
    /**
     * Provides a method with a well known chained/nested exception
     * name which matches the full signature (e.g. has a return value
     * of <code>Throwable</code>.
     */
    private static class ExceptionWithCause extends Exception {
        private static final long serialVersionUID = 1L;

        private Throwable cause;

        ExceptionWithCause(final String str, final Throwable cause) {
            super(str);
            setCause(cause);
        }

        ExceptionWithCause(final Throwable cause) {
            super();
            setCause(cause);
        }

        @Override
        public Throwable getCause() {
            return cause;
        }

        public void setCause(final Throwable cause) {
            this.cause = cause;
        }
    }

    /**
     * Provides a method with a well known chained/nested exception
     * name which does not match the full signature (e.g. lacks a
     * return value of <code>Throwable</code>.
     */
    private static class ExceptionWithoutCause extends Exception {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unused")
        public void getTargetException() {
        }
    }

    // Temporary classes to allow the nested exception code to be removed
    // prior to a rewrite of this test class.
    private static class NestableException extends Exception {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unused")
        NestableException() {
            super();
        }

        NestableException(final Throwable t) {
            super(t);
        }
    }

    @Test
    public void testThrow() {
        final Exception expected = new InterruptedException();
        Exception actual = Assertions.assertThrows(Exception.class, () -> ExceptionUtils.rethrow(expected));
        Assertions.assertSame(expected, actual);
    }

    @Test
    public void testCatchTechniques() {
        IOException ioe = Assertions.assertThrows(IOException.class, ExceptionUtilsTest::throwsCheckedException);
        Assertions.assertEquals(1, ExceptionUtils.getThrowableCount(ioe));
        ioe = Assertions.assertThrows(IOException.class, ExceptionUtilsTest::redeclareCheckedException);
        Assertions.assertEquals(1, ExceptionUtils.getThrowableCount(ioe));
    }

    public static class TestThrowable extends Throwable {
        private static final long serialVersionUID = 1L;
    }

    @Test
    public void testWrapAndUnwrapError() {
        Throwable t = Assertions.assertThrows(Throwable.class, () -> ExceptionUtils.wrapAndThrow(new OutOfMemoryError()));
        Assertions.assertTrue(ExceptionUtils.hasCause(t, Error.class));
    }

    @Test
    public void testWrapAndUnwrapRuntimeException() {
        Throwable t = Assertions.assertThrows(Throwable.class, () -> ExceptionUtils.wrapAndThrow(new IllegalArgumentException()));
        Assertions.assertTrue(ExceptionUtils.hasCause(t, RuntimeException.class));
    }

    @Test
    public void testWrapAndUnwrapCheckedException() {
        Throwable t = Assertions.assertThrows(Throwable.class, () -> ExceptionUtils.wrapAndThrow(new IOException()));
        Assertions.assertTrue(ExceptionUtils.hasCause(t, IOException.class));
    }

    @Test
    public void testWrapAndUnwrapThrowable() {
        Throwable t = Assertions.assertThrows(Throwable.class, () -> ExceptionUtils.wrapAndThrow(new ExceptionUtilsTest.TestThrowable()));
        Assertions.assertTrue(ExceptionUtils.hasCause(t, ExceptionUtilsTest.TestThrowable.class));
    }
}

