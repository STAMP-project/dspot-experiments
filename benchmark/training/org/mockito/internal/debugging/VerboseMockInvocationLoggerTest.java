/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.debugging;


import java.io.ByteArrayOutputStream;
import org.junit.Test;
import org.mockito.internal.handler.NotifiedMethodInvocationReport;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockito.internal.invocation.StubInfoImpl;
import org.mockito.invocation.DescribedInvocation;
import org.mockito.invocation.Invocation;


public class VerboseMockInvocationLoggerTest {
    private VerboseMockInvocationLogger listener;

    private ByteArrayOutputStream output;

    private Invocation invocation = new InvocationBuilder().toInvocation();

    private DescribedInvocation stubbedInvocation = new InvocationBuilder().toInvocation();

    @Test
    public void should_print_to_system_out() {
        assertThat(new VerboseMockInvocationLogger().printStream).isSameAs(System.out);
    }

    @Test
    public void should_print_invocation_with_return_value() {
        // when
        listener.reportInvocation(new NotifiedMethodInvocationReport(invocation, "return value"));
        // then
        assertThat(printed()).contains(invocation.toString()).contains(invocation.getLocation().toString()).contains("return value");
    }

    @Test
    public void should_print_invocation_with_exception() {
        // when
        listener.reportInvocation(new NotifiedMethodInvocationReport(invocation, new VerboseMockInvocationLoggerTest.ThirdPartyException()));
        // then
        assertThat(printed()).contains(invocation.toString()).contains(invocation.getLocation().toString()).contains(VerboseMockInvocationLoggerTest.ThirdPartyException.class.getName());
    }

    @Test
    public void should_print_if_method_has_not_been_stubbed() throws Exception {
        listener.reportInvocation(new NotifiedMethodInvocationReport(invocation, "whatever"));
        assertThat(printed()).doesNotContain("stubbed");
    }

    @Test
    public void should_print_stubbed_info_if_available() throws Exception {
        invocation.markStubbed(new StubInfoImpl(stubbedInvocation));
        listener.reportInvocation(new NotifiedMethodInvocationReport(invocation, "whatever"));
        assertThat(printed()).contains("stubbed").contains(stubbedInvocation.getLocation().toString());
    }

    @Test
    public void should_log_count_of_interactions() {
        // when & then
        listener.reportInvocation(new NotifiedMethodInvocationReport(invocation, new VerboseMockInvocationLoggerTest.ThirdPartyException()));
        assertThat(printed()).contains("#1");
        listener.reportInvocation(new NotifiedMethodInvocationReport(invocation, new VerboseMockInvocationLoggerTest.ThirdPartyException()));
        assertThat(printed()).contains("#2");
        listener.reportInvocation(new NotifiedMethodInvocationReport(invocation, new VerboseMockInvocationLoggerTest.ThirdPartyException()));
        assertThat(printed()).contains("#3");
    }

    private static class ThirdPartyException extends Exception {
        private static final long serialVersionUID = 3022739107688491354L;
    }
}

