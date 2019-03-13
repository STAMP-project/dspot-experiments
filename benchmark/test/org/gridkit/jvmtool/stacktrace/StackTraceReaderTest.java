package org.gridkit.jvmtool.stacktrace;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class StackTraceReaderTest {
    @Rule
    public TestName testName = new TestName();

    @Test
    public void read_dump_v1() throws FileNotFoundException, IOException {
        StackTraceReader reader = StackTraceCodec.newReader(new FileInputStream("src/test/resources/dump_v1.std"));
        if (!(reader.isLoaded())) {
            reader.loadNext();
        }
        int n = 0;
        while (reader.isLoaded()) {
            reader.loadNext();
            ++n;
        } 
        System.out.println((("Read " + n) + " traces from file"));
    }

    @Test
    public void read_dump_v2() throws FileNotFoundException, IOException {
        StackTraceReader reader = StackTraceCodec.newReader(new FileInputStream("src/test/resources/dump_v2.std"));
        if (!(reader.isLoaded())) {
            reader.loadNext();
        }
        int n = 0;
        while (reader.isLoaded()) {
            // System.out.println(reader.getCounters() + " - " + reader.getThreadName());
            reader.loadNext();
            ++n;
        } 
        System.out.println((("Read " + n) + " traces from file"));
    }

    @Test
    public void read_dump_v1_rewrite_and_compare() throws FileNotFoundException, IOException {
        File file = new File((((("target/tmp/" + (testName.getMethodName())) + "-") + (System.currentTimeMillis())) + ".std"));
        file.getParentFile().mkdirs();
        file.delete();
        FileOutputStream fow = new FileOutputStream(file);
        StackTraceWriter writer = StackTraceCodec.newWriter(fow);
        StackTraceReader reader = StackTraceCodec.newReader(new FileInputStream("src/test/resources/dump_v1.std"));
        copyAllTraces(reader, writer);
        writer.close();
        reader = StackTraceCodec.newReader(new FileInputStream(file));
        StackTraceReader origReader = StackTraceCodec.newReader(new FileInputStream("src/test/resources/dump_v1.std"));
        assertEqual(origReader, reader);
    }

    @Test
    public void read_dump_v2_rewrite_and_compare() throws FileNotFoundException, IOException {
        File file = new File((((("target/tmp/" + (testName.getMethodName())) + "-") + (System.currentTimeMillis())) + ".std"));
        file.getParentFile().mkdirs();
        file.delete();
        FileOutputStream fow = new FileOutputStream(file);
        StackTraceWriter writer = StackTraceCodec.newWriter(fow);
        StackTraceReader reader = StackTraceCodec.newReader(new FileInputStream("src/test/resources/dump_v2.std"));
        copyAllTraces(reader, writer);
        writer.close();
        reader = StackTraceCodec.newReader(new FileInputStream(file));
        StackTraceReader origReader = StackTraceCodec.newReader(new FileInputStream("src/test/resources/dump_v2.std"));
        assertEqual(origReader, reader);
    }
}

