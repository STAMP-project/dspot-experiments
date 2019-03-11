package org.gridkit.jvmtool.codec.stacktrace;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.gridkit.jvmtool.event.ErrorEvent;
import org.gridkit.jvmtool.event.Event;
import org.gridkit.jvmtool.event.EventReader;
import org.gridkit.jvmtool.event.UniversalEventWriter;
import org.gridkit.jvmtool.stacktrace.ThreadEventCodec;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class ThreadEventReaderTest {
    @Rule
    public TestName testName = new TestName();

    @Test
    public void read_dump_v1() throws FileNotFoundException, IOException {
        EventReader<Event> reader = ThreadEventCodec.createEventReader(new FileInputStream("src/test/resources/dump_v1.std"));
        int n = 0;
        for (Event e : reader) {
            if (e instanceof ErrorEvent) {
                fail("Error", exception());
            }
            ++n;
        }
        System.out.println((("Read " + n) + " traces from file"));
    }

    @Test
    public void read_dump_v2() throws FileNotFoundException, IOException {
        EventReader<Event> reader = ThreadEventCodec.createEventReader(new FileInputStream("src/test/resources/dump_v2.std"));
        int n = 0;
        for (Event e : reader) {
            if (e instanceof ErrorEvent) {
                fail("Error", exception());
            }
            // System.out.println(((MultiCounterEvent)e).counters());
            ++n;
        }
        System.out.println((("Read " + n) + " traces from file"));
    }

    @Test
    public void read_dump_v1_rewrite_and_compare() throws FileNotFoundException, IOException {
        String sourceFile = "src/test/resources/dump_v1.std";
        File file = new File((((("target/tmp/" + (testName.getMethodName())) + "-") + (System.currentTimeMillis())) + ".std"));
        file.getParentFile().mkdirs();
        file.delete();
        FileOutputStream fow = new FileOutputStream(file);
        UniversalEventWriter writer = ThreadEventCodec.createEventWriter(fow);
        EventReader<Event> reader = ThreadEventCodec.createEventReader(new FileInputStream(sourceFile));
        copyAllTraces(reader, writer);
        writer.close();
        System.out.println((((("New file " + (file.length())) + " bytes (original ") + (new File(sourceFile).length())) + " bytes)"));
        reader = ThreadEventCodec.createEventReader(new FileInputStream(file));
        EventReader<Event> origReader = ThreadEventCodec.createEventReader(new FileInputStream("src/test/resources/dump_v1.std"));
        assertThat(((Iterable<Event>) (reader))).is(EventSeqEqualToCondition.exactlyAs(origReader));
    }

    @Test
    public void read_dump_v2_rewrite_and_compare() throws FileNotFoundException, IOException {
        File sourceFile = new File("src/test/resources/dump_v2.std");
        File file = new File((((("target/tmp/" + (testName.getMethodName())) + "-") + (System.currentTimeMillis())) + ".std"));
        file.getParentFile().mkdirs();
        file.delete();
        FileOutputStream fow = new FileOutputStream(file);
        UniversalEventWriter writer = ThreadEventCodec.createEventWriter(fow);
        EventReader<Event> reader = ThreadEventCodec.createEventReader(new FileInputStream(sourceFile));
        copyAllTraces(reader, writer);
        writer.close();
        System.out.println((((("New file " + (file.length())) + " bytes (original ") + (sourceFile.length())) + " bytes)"));
        reader = ThreadEventCodec.createEventReader(new FileInputStream(file));
        EventReader<Event> origReader = ThreadEventCodec.createEventReader(new FileInputStream(sourceFile));
        assertThat(((Iterable<Event>) (reader))).is(EventSeqEqualToCondition.exactlyAs(origReader));
    }

    @Test
    public void read_dump_v4_rewrite_and_compare() throws FileNotFoundException, IOException {
        File sourceFile = new File("src/test/resources/dump_v4.std");
        File file = new File((((("target/tmp/" + (testName.getMethodName())) + "-") + (System.currentTimeMillis())) + ".std"));
        file.getParentFile().mkdirs();
        file.delete();
        FileOutputStream fow = new FileOutputStream(file);
        UniversalEventWriter writer = ThreadEventCodec.createEventWriter(fow);
        EventReader<Event> reader = ThreadEventCodec.createEventReader(new FileInputStream(sourceFile));
        copyAllTraces(reader, writer);
        writer.close();
        System.out.println((((("New file " + (file.length())) + " bytes (original ") + (sourceFile.length())) + " bytes)"));
        reader = ThreadEventCodec.createEventReader(new FileInputStream(file));
        EventReader<Event> origReader = ThreadEventCodec.createEventReader(new FileInputStream(sourceFile));
        assertThat(((Iterable<Event>) (reader))).is(EventSeqEqualToCondition.exactlyAs(origReader));
    }
}

