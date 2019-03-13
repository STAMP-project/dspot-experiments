package com.codahale.metrics.jvm;


import java.io.ByteArrayOutputStream;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import org.junit.Test;
import org.mockito.Mockito;


// TODO: 3/12/13 <coda> -- improve test coverage for ThreadDump
public class ThreadDumpTest {
    private final ThreadMXBean threadMXBean = Mockito.mock(ThreadMXBean.class);

    private final ThreadDump threadDump = new ThreadDump(threadMXBean);

    private final ThreadInfo runnable = Mockito.mock(ThreadInfo.class);

    @Test
    public void dumpsAllThreads() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        threadDump.dump(output);
        assertThat(output.toString()).isEqualTo(String.format(("\"runnable\" id=100 state=RUNNABLE%n" + (("    at Blah.blee(Blah.java:100)%n" + "%n") + "%n"))));
    }
}

