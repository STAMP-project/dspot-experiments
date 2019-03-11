package org.gridkit.jvmtool.stacktrace.analytics;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.gridkit.jvmtool.stacktrace.StackFrame;
import org.gridkit.jvmtool.stacktrace.StackFrameList;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import static java.lang.Thread.State.RUNNABLE;


@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FilterParserMatchingTest {
    static class Trace {
        String name;

        List<StackFrame> frame = new ArrayList<StackFrame>();

        Thread.State state = null;

        public Trace(String name) {
            this.name = name;
        }

        public Trace(String name, Thread.State state) {
            this.name = name;
            this.state = state;
        }

        public FilterParserMatchingTest.Trace t(String trace) {
            if ((trace.indexOf('(')) > 0) {
                frame.add(StackFrame.parseFrame(trace));
            } else {
                frame.add(StackFrame.parseFrame((trace + "(X.java)")));
            }
            return this;
        }

        public StackFrameList frameList() {
            ArrayList<StackFrame> list = new ArrayList<StackFrame>(frame);
            Collections.reverse(list);
            StackFrame[] array = list.toArray(new StackFrame[0]);
            return new org.gridkit.jvmtool.stacktrace.StackFrameArray(array);
        }

        public String toString() {
            return name;
        }
    }

    private static List<Object[]> cases = new ArrayList<Object[]>();

    public static final FilterParserMatchingTest.Trace TRACE_A = new FilterParserMatchingTest.Trace("TRACE_A", RUNNABLE).t("com.acme.MyClass").t("test.MyBean.init(MyBean.java:100)").t("com.acme.MyClass$1");

    public static final FilterParserMatchingTest.Trace TRACE_B1 = new FilterParserMatchingTest.Trace("TRACE_B1").t("test.server.Handler.run").t("test.server.Handler.process(X.java:123)").t("test.myapp.security.Filter.process").t("test.framework.Rederer.execute").t("test.framework.Bijector.invoke").t("test.framework.Bijector.proceed").t("test.myapp.app.MyBean.doStuff").t("test.framework.Bijector.invoke").t("test.framework.Bijector.doChecks");

    public static final FilterParserMatchingTest.Trace TRACE_B2 = new FilterParserMatchingTest.Trace("TRACE_B2").t("test.server.Handler.run").t("test.server.Handler.process(X.java:123)").t("test.myapp.security.Filter.process").t("test.framework.Rederer.execute").t("test.framework.Bijector.invoke").t("test.framework.Bijector.proceed").t("test.myapp.app.MyBean.doStuff").t("test.framework.Bijector.invoke").t("test.framework.Bijector.proceed").t("test.myapp.app.MyBean.doStuff");

    public static final FilterParserMatchingTest.Trace TRACE_B3 = new FilterParserMatchingTest.Trace("TRACE_B3").t("test.server.Handler.run").t("test.server.Handler.process(X.java:123)").t("test.myapp.security.Filter.process").t("test.framework.Rederer.execute").t("test.framework.Bijector.invoke").t("test.framework.Bijector.proceed").t("test.myapp.app.MyBean.doStuff").t("test.framework.Bijector.invoke").t("test.framework.Bijector.doChecks").t("javax.jdbc.Something");

    public static final FilterParserMatchingTest.Trace TRACE_B4 = new FilterParserMatchingTest.Trace("TRACE_B4").t("test.server.Handler.run").t("test.server.Handler.process(X.java:123)").t("test.myapp.security.Filter.process").t("test.framework.Rederer.execute").t("test.framework.Bijector.invoke").t("test.framework.Bijector.proceed").t("test.myapp.app.MyBean.doStuff").t("test.framework.Syncjector.invoke").t("test.framework.Syncjector.doChecks");

    public static final FilterParserMatchingTest.Trace TRACE_B5 = // different line number
    new FilterParserMatchingTest.Trace("TRACE_B5").t("test.server.Handler.run").t("test.server.Handler.process(X.java:128)").t("test.myapp.security.Filter.process").t("test.framework.Rederer.execute").t("test.framework.Bijector.invoke").t("test.framework.Bijector.proceed").t("test.myapp.app.MyBean.doStuff").t("test.framework.Syncjector.invoke").t("test.framework.Syncjector.doChecks");

    private String expression;

    private FilterParserMatchingTest.Trace trace;

    private boolean match;

    public FilterParserMatchingTest(String expression, FilterParserMatchingTest.Trace trace, boolean match) {
        this.expression = expression;
        this.trace = trace;
        this.match = match;
    }

    @Test
    public void match() {
        ThreadSnapshotFilter f = TraceFilterPredicateParser.parseFilter(expression, new BasicFilterFactory());
        if (match) {
            Assert.assertTrue("Should match", f.evaluate(trace()));
        } else {
            Assert.assertFalse("Should not match", f.evaluate(trace()));
        }
    }
}

