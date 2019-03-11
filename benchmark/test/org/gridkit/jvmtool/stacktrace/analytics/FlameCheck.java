package org.gridkit.jvmtool.stacktrace.analytics;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.gridkit.jvmtool.stacktrace.StackTraceReader;
import org.gridkit.jvmtool.stacktrace.analytics.flame.FlameGraphGenerator;
import org.junit.Test;


public class FlameCheck {
    @Test
    public void check() throws IOException {
        FlameGraphGenerator fg = new FlameGraphGenerator();
        StackTraceReader r = read();
        if (!(r.isLoaded())) {
            r.loadNext();
        }
        while (r.isLoaded()) {
            fg.feed(r.getStackTrace());
            r.loadNext();
        } 
        StringWriter sw = new StringWriter();
        fg.renderSVG("Flame Graph", 1200, sw);
        FileWriter fw = new FileWriter(new File("target/flame.svg"));
        fw.append(sw.getBuffer());
        fw.close();
    }
}

