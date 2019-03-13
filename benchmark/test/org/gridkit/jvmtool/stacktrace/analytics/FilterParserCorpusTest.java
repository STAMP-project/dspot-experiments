package org.gridkit.jvmtool.stacktrace.analytics;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.gridkit.jvmtool.stacktrace.StackTraceCodec;
import org.gridkit.jvmtool.stacktrace.StackTraceReader;
import org.gridkit.jvmtool.stacktrace.ThreadSnapshot;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FilterParserCorpusTest {
    private static List<Object[]> cases = new ArrayList<Object[]>();

    private String filter;

    private int matchCount;

    public FilterParserCorpusTest(String filter, int matchCount) {
        this.filter = filter;
        this.matchCount = matchCount;
    }

    @Test
    public void verify() throws FileNotFoundException, IOException {
        ThreadSnapshotFilter f = TraceFilterPredicateParser.parseFilter(filter, new CachingFilterFactory());
        StackTraceReader reader = StackTraceCodec.newReader(new FileInputStream("src/test/resources/jboss-10k.std"));
        int n = 0;
        if (!(reader.isLoaded())) {
            reader.loadNext();
        }
        ThreadSnapshot readerProxy = new org.gridkit.jvmtool.stacktrace.ReaderProxy(reader);
        while (reader.isLoaded()) {
            if (f.evaluate(readerProxy)) {
                ++n;
            }
            if (!(reader.loadNext())) {
                break;
            }
        } 
        Assert.assertEquals(matchCount, n);
    }
}

