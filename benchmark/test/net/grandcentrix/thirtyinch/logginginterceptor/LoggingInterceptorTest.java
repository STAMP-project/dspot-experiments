/**
 * Copyright (C) 2017 grandcentrix GmbH
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.grandcentrix.thirtyinch.logginginterceptor;


import TiLog.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.grandcentrix.thirtyinch.TiLog;
import net.grandcentrix.thirtyinch.TiView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class LoggingInterceptorTest {
    private class BaseActivity implements LoggingInterceptorTest.MyView {}

    private class MyActivity extends LoggingInterceptorTest.BaseActivity {}

    private class TestViewImpl implements LoggingInterceptorTest.TestView {
        @Override
        public void doSomething() {
            // stub
        }

        @Override
        public void singleArg(final Object arg) {
            // stub
        }

        @Override
        public void throwUnexpected() {
            throw new RuntimeException("Unexpected");
        }

        @Override
        public void twoArgs(final Object arg1, final Object arg2) {
            // stub
        }

        @Override
        public void varargs(final Object... args) {
            // stub
        }
    }

    private interface MyView extends TiView {}

    private interface TestView extends TiView {
        void doSomething();

        void singleArg(Object arg);

        void throwUnexpected();

        void twoArgs(Object arg1, Object arg2);

        void varargs(Object... args);
    }

    @Test
    public void testCropLongParams() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        final LoggingInterceptor loggingInterceptor = new LoggingInterceptor(logger);
        final LoggingInterceptorTest.TestView view = loggingInterceptor.intercept(new LoggingInterceptorTest.TestViewImpl());
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        String maxArg = "";
        while ((maxArg.length()) < 240) {
            maxArg += "0123456789";
        } 
        view.twoArgs((maxArg + "too long"), "B");
        Mockito.verify(logger).log(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), msgCaptor.capture());
        assertThat(msgCaptor.getValue()).doesNotContain("too long").isEqualTo((("twoArgs(" + maxArg) + "?, B)"));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testDontLogObjectInvocations() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        final LoggingInterceptor loggingInterceptor = new LoggingInterceptor(logger);
        final LoggingInterceptorTest.TestView view = loggingInterceptor.intercept(new LoggingInterceptorTest.TestViewImpl());
        view.hashCode();
        view.toString();
        view.getClass();
        Mockito.verify(logger, Mockito.never()).log(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void testFindTiViewInterfaceInComplexStructure() throws Exception {
        final LoggingInterceptor interceptor = new LoggingInterceptor();
        final TiView interceptView = interceptor.intercept(new LoggingInterceptorTest.MyActivity());
        assertThat(interceptView).isInstanceOf(TiView.class).isInstanceOf(LoggingInterceptorTest.MyView.class).isNotInstanceOf(LoggingInterceptorTest.MyActivity.class).isNotInstanceOf(LoggingInterceptorTest.BaseActivity.class);
    }

    @Test
    public void testLogArray() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        final LoggingInterceptor loggingInterceptor = new LoggingInterceptor(logger);
        final LoggingInterceptorTest.TestView view = loggingInterceptor.intercept(new LoggingInterceptorTest.TestViewImpl());
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        final String[] array = new String[]{ "Buenos Aires", "C?rdoba", "La Plata" };
        view.twoArgs(array, "B");
        Mockito.verify(logger).log(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), msgCaptor.capture());
        assertThat(msgCaptor.getValue()).matches(("twoArgs\\(\\{String\\[\\]\\[3\\]@[\\da-f]{1,8}\\} \\" + "[Buenos Aires, C\u00f3rdoba, La Plata\\], B\\)"));
    }

    @Test
    public void testLogEmptyList() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        final LoggingInterceptor loggingInterceptor = new LoggingInterceptor(logger);
        final LoggingInterceptorTest.TestView view = loggingInterceptor.intercept(new LoggingInterceptorTest.TestViewImpl());
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        view.twoArgs(new ArrayList(), "B");
        Mockito.verify(logger).log(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), msgCaptor.capture());
        assertThat(msgCaptor.getValue()).matches(("twoArgs\\(" + (("\\{ArrayList\\[0\\]@[\\da-f]{1,8}\\} \\[\\], " + "B") + "\\)")));
    }

    @Test
    public void testLogLists() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        final LoggingInterceptor loggingInterceptor = new LoggingInterceptor(logger);
        final LoggingInterceptorTest.TestView view = loggingInterceptor.intercept(new LoggingInterceptorTest.TestViewImpl());
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        final List list = Arrays.asList("Buenos Aires", "C?rdoba", "La Plata");
        view.twoArgs(list, "B");
        Mockito.verify(logger).log(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), msgCaptor.capture());
        assertThat(msgCaptor.getValue()).matches(("twoArgs\\(" + (("\\{ArrayList\\[3\\]@[\\da-f]{1,8}\\} \\[Buenos Aires, C\u00f3rdoba, La Plata\\], " + "B") + "\\)")));
    }

    @Test
    public void testLogMultipleArguments() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        final LoggingInterceptor loggingInterceptor = new LoggingInterceptor(logger);
        final LoggingInterceptorTest.TestView view = loggingInterceptor.intercept(new LoggingInterceptorTest.TestViewImpl());
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        view.twoArgs("A", "B");
        Mockito.verify(logger).log(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), msgCaptor.capture());
        assertThat(msgCaptor.getValue()).isEqualTo("twoArgs(A, B)");
    }

    @Test
    public void testLogNull() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        final LoggingInterceptor loggingInterceptor = new LoggingInterceptor(logger);
        final LoggingInterceptorTest.TestView view = loggingInterceptor.intercept(new LoggingInterceptorTest.TestViewImpl());
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        view.singleArg(null);
        Mockito.verify(logger).log(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), msgCaptor.capture());
        assertThat(msgCaptor.getValue()).isEqualTo("singleArg(null)");
    }

    @Test
    public void testLogNullVarargs() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        final LoggingInterceptor loggingInterceptor = new LoggingInterceptor(logger);
        final LoggingInterceptorTest.TestView view = loggingInterceptor.intercept(new LoggingInterceptorTest.TestViewImpl());
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        view.varargs(((Object[]) (null)));
        Mockito.verify(logger).log(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), msgCaptor.capture());
        assertThat(msgCaptor.getValue()).isEqualTo("varargs(null)");
    }

    @Test
    public void testLogVarargs() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        final LoggingInterceptor loggingInterceptor = new LoggingInterceptor(logger);
        final LoggingInterceptorTest.TestView view = loggingInterceptor.intercept(new LoggingInterceptorTest.TestViewImpl());
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        view.varargs("Buenos Aires", "C?rdoba", "La Plata");
        Mockito.verify(logger).log(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), msgCaptor.capture());
        assertThat(msgCaptor.getValue()).matches(("varargs\\(\\{Object\\[\\]\\[3\\]@[\\da-f]{1,8}\\} \\" + "[Buenos Aires, C\u00f3rdoba, La Plata\\]\\)"));
    }

    @Test
    public void testLogVoidMethods() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        final LoggingInterceptor loggingInterceptor = new LoggingInterceptor(logger);
        final LoggingInterceptorTest.TestView view = loggingInterceptor.intercept(new LoggingInterceptorTest.TestViewImpl());
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        view.doSomething();
        Mockito.verify(logger).log(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), msgCaptor.capture());
        assertThat(msgCaptor.getValue()).isEqualTo("doSomething()");
    }

    @Test
    public void testLoggerNoop_dontWrap() throws Exception {
        final LoggingInterceptor interceptor = new LoggingInterceptor(TiLog.NOOP);
        final TiView view = Mockito.mock(TiView.class);
        final TiView interceptView = interceptor.intercept(view);
        assertThat(interceptView).isEqualTo(view);
    }

    @Test
    public void testLoggerNull_dontWrap() throws Exception {
        final LoggingInterceptor interceptor = new LoggingInterceptor(null);
        final TiView view = Mockito.mock(TiView.class);
        final TiView interceptView = interceptor.intercept(view);
        assertThat(interceptView).isEqualTo(view);
    }

    @Test
    public void testLoggingDisabled_wrap() throws Exception {
        final LoggingInterceptor interceptor = new LoggingInterceptor();
        final TiView view = Mockito.mock(TiView.class);
        final TiView interceptView = interceptor.intercept(view);
        assertThat(interceptView).isNotEqualTo(view).isNotSameAs(view);
    }

    @Test
    public void testReportErrorsCorrectly() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        final LoggingInterceptor loggingInterceptor = new LoggingInterceptor(logger);
        final LoggingInterceptorTest.TestView view = loggingInterceptor.intercept(new LoggingInterceptorTest.TestViewImpl());
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        try {
            view.throwUnexpected();
            fail("did not throw");
        } catch (RuntimeException e) {
            assertThat(e).hasMessage("Unexpected");
        }
        Mockito.verify(logger).log(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), msgCaptor.capture());
        // make sure logging happened before the method was called
        assertThat(msgCaptor.getValue()).isEqualTo("throwUnexpected()");
    }
}

