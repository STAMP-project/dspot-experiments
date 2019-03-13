/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.debugging;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests the verbose logging of invocation on mock methods.
 *
 * BEWARE: These tests rely on mocking the standard output. While in a
 * single-threaded environment the Before/After-contract ensures, that the
 * original output stream is restored, there is no guarantee for this
 * in the parallel setting.
 * Maybe, the test class should be @Ignore'd by default ...
 */
@RunWith(MockitoJUnitRunner.class)
public class VerboseLoggingOfInvocationsOnMockTest {
    private ByteArrayOutputStream output;

    private PrintStream original;

    @Mock
    VerboseLoggingOfInvocationsOnMockTest.UnrelatedClass unrelatedMock;

    @Test
    public void shouldNotPrintInvocationOnMockWithoutSetting() {
        // given
        Foo foo = Mockito.mock(Foo.class, Mockito.withSettings().verboseLogging());
        // when
        foo.giveMeSomeString("Klipsch");
        unrelatedMock.unrelatedMethod("Apple");
        // then
        Assertions.assertThat(printed()).doesNotContain(mockName(unrelatedMock)).doesNotContain("unrelatedMethod").doesNotContain("Apple");
    }

    @Test
    public void shouldPrintUnstubbedInvocationOnMockToStdOut() {
        // given
        Foo foo = Mockito.mock(Foo.class, Mockito.withSettings().verboseLogging());
        // when
        foo.doSomething("Klipsch");
        // then
        Assertions.assertThat(printed()).contains(getClass().getName()).contains(mockName(foo)).contains("doSomething").contains("Klipsch");
    }

    @Test
    public void shouldPrintStubbedInvocationOnMockToStdOut() {
        // given
        Foo foo = Mockito.mock(Foo.class, Mockito.withSettings().verboseLogging());
        BDDMockito.given(foo.giveMeSomeString("Klipsch")).willReturn("earbuds");
        // when
        foo.giveMeSomeString("Klipsch");
        // then
        Assertions.assertThat(printed()).contains(getClass().getName()).contains(mockName(foo)).contains("giveMeSomeString").contains("Klipsch").contains("earbuds");
    }

    @Test
    public void shouldPrintThrowingInvocationOnMockToStdOut() {
        // given
        Foo foo = Mockito.mock(Foo.class, Mockito.withSettings().verboseLogging());
        Mockito.doThrow(new VerboseLoggingOfInvocationsOnMockTest.ThirdPartyException()).when(foo).doSomething("Klipsch");
        try {
            // when
            foo.doSomething("Klipsch");
            Assert.fail("Exception excepted.");
        } catch (VerboseLoggingOfInvocationsOnMockTest.ThirdPartyException e) {
            // then
            Assertions.assertThat(printed()).contains(getClass().getName()).contains(mockName(foo)).contains("doSomething").contains("Klipsch").contains(VerboseLoggingOfInvocationsOnMockTest.ThirdPartyException.class.getName());
        }
    }

    @Test
    public void shouldPrintRealInvocationOnSpyToStdOut() {
        // given
        VerboseLoggingOfInvocationsOnMockTest.FooImpl fooSpy = Mockito.mock(VerboseLoggingOfInvocationsOnMockTest.FooImpl.class, Mockito.withSettings().spiedInstance(new VerboseLoggingOfInvocationsOnMockTest.FooImpl()).verboseLogging());
        Mockito.doCallRealMethod().when(fooSpy).doSomething("Klipsch");
        // when
        fooSpy.doSomething("Klipsch");
        // then
        Assertions.assertThat(printed()).contains(getClass().getName()).contains(mockName(fooSpy)).contains("doSomething").contains("Klipsch");
    }

    @Test
    public void usage() {
        // given
        Foo foo = Mockito.mock(Foo.class, Mockito.withSettings().verboseLogging());
        BDDMockito.given(foo.giveMeSomeString("Apple")).willReturn("earbuds");
        // when
        foo.giveMeSomeString("Shure");
        foo.giveMeSomeString("Apple");
        foo.doSomething("Klipsch");
    }

    private static class UnrelatedClass {
        void unrelatedMethod(String anotherStringValue) {
        }
    }

    /**
     * An exception that isn't defined by Mockito or the JDK and therefore does
     * not appear in the logging result by chance alone.
     */
    static class ThirdPartyException extends RuntimeException {
        private static final long serialVersionUID = 2160445705646210847L;
    }

    static class FooImpl implements Foo {
        public String giveMeSomeString(String param) {
            return null;
        }

        public void doSomething(String param) {
        }
    }
}

