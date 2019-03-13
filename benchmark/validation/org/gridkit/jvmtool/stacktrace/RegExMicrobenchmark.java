package org.gridkit.jvmtool.stacktrace;


import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import java.util.regex.Pattern;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


@Ignore("Benchmark")
public class RegExMicrobenchmark {
    private static StackFrame[] FRAMES;

    private static StackFrame[] INTERNED_FRAMES;

    private static StackTraceElement[] TRACE_ELEMENTS;

    static {
        RegExMicrobenchmark.FRAMES = RegExMicrobenchmark.loadFrames("stack-frame.txt", 1000);
        RegExMicrobenchmark.INTERNED_FRAMES = new StackFrame[RegExMicrobenchmark.FRAMES.length];
        RegExMicrobenchmark.TRACE_ELEMENTS = new StackTraceElement[RegExMicrobenchmark.FRAMES.length];
        for (int i = 0; i != (RegExMicrobenchmark.FRAMES.length); ++i) {
            RegExMicrobenchmark.INTERNED_FRAMES[i] = RegExMicrobenchmark.FRAMES[i].internSymbols();
            RegExMicrobenchmark.TRACE_ELEMENTS[i] = RegExMicrobenchmark.FRAMES[i].toStackTraceElement();
        }
    }

    private static Pattern pattern1 = GlobHelper.translate("org.hibernate.type.AbstractStandardBasicType.getHashCode", ".");

    private static Pattern pattern2 = GlobHelper.translate("**.TagMethodExpression.invoke", ".");

    private static Pattern pattern3 = GlobHelper.translate("org.jboss.seam.**", ".");

    @Rule
    public TestName testName = new TestName();

    @Rule
    public volatile BenchmarkRule benchmark = new BenchmarkRule();

    @Test
    public void frame_pattern1() {
        testFrameMatcher(RegExMicrobenchmark.FRAMES, RegExMicrobenchmark.pattern1);
    }

    @Test
    public void frame_pattern2() {
        testFrameMatcher(RegExMicrobenchmark.FRAMES, RegExMicrobenchmark.pattern2);
    }

    @Test
    public void frame_pattern3() {
        testFrameMatcher(RegExMicrobenchmark.FRAMES, RegExMicrobenchmark.pattern3);
    }

    @Test
    public void frame_patternAll() {
        testFrameMatcher(RegExMicrobenchmark.FRAMES, RegExMicrobenchmark.pattern1, RegExMicrobenchmark.pattern2, RegExMicrobenchmark.pattern3);
    }

    @Test
    public void interned_frame_pattern1() {
        testFrameMatcher(RegExMicrobenchmark.INTERNED_FRAMES, RegExMicrobenchmark.pattern1);
    }

    @Test
    public void interned_frame_pattern2() {
        testFrameMatcher(RegExMicrobenchmark.INTERNED_FRAMES, RegExMicrobenchmark.pattern2);
    }

    @Test
    public void interned_frame_pattern3() {
        testFrameMatcher(RegExMicrobenchmark.INTERNED_FRAMES, RegExMicrobenchmark.pattern3);
    }

    @Test
    public void interned_frame_patternAll() {
        testFrameMatcher(RegExMicrobenchmark.INTERNED_FRAMES, RegExMicrobenchmark.pattern1, RegExMicrobenchmark.pattern2, RegExMicrobenchmark.pattern3);
    }

    @Test
    public void trace_element_pattern1() {
        testFrameMatcher(RegExMicrobenchmark.TRACE_ELEMENTS, RegExMicrobenchmark.pattern1);
    }

    @Test
    public void trace_element_pattern2() {
        testFrameMatcher(RegExMicrobenchmark.TRACE_ELEMENTS, RegExMicrobenchmark.pattern2);
    }

    @Test
    public void trace_element_pattern3() {
        testFrameMatcher(RegExMicrobenchmark.TRACE_ELEMENTS, RegExMicrobenchmark.pattern3);
    }

    @Test
    public void trace_element_patternAll() {
        testFrameMatcher(RegExMicrobenchmark.TRACE_ELEMENTS, RegExMicrobenchmark.pattern1, RegExMicrobenchmark.pattern2, RegExMicrobenchmark.pattern3);
    }
}

