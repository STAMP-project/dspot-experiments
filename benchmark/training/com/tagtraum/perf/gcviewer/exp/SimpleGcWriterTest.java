package com.tagtraum.perf.gcviewer.exp;


import com.tagtraum.perf.gcviewer.exp.impl.SimpleGcWriter;
import com.tagtraum.perf.gcviewer.model.GCModel;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the export format of {@link SimpleGcWriter}.
 *
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a>
<p>created on: 08.12.2012</p>
 */
public class SimpleGcWriterTest {
    private GCModel gcModel;

    @Test
    public void exportLocaleDe() throws Exception {
        Locale.setDefault(new Locale("de", "ch"));
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();SimpleGcWriter writer = new SimpleGcWriter(outputStream)) {
            writer.write(gcModel);
            String[] lines = outputStream.toString().split(System.getProperty("line.separator"));
            Assert.assertEquals("line count", 2, lines.length);
            String[] firstLine = lines[0].split(" ");
            Assert.assertEquals("number of parts in line 1", 3, firstLine.length);
            Assert.assertEquals("name of event", "YoungGC", firstLine[0]);
            Assert.assertEquals("timestamp", "0.677000", firstLine[1]);
            Assert.assertEquals("duration", "0.030063", firstLine[2]);
            String[] secondLine = lines[1].split(" ");
            Assert.assertEquals("number of parts in line 2", 3, secondLine.length);
            Assert.assertEquals("name of event 2", "InitialMarkGC", secondLine[0]);
        }
    }

    @Test
    public void exportLocaleSv() throws Exception {
        Locale.setDefault(new Locale("sv", "se"));
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();SimpleGcWriter writer = new SimpleGcWriter(outputStream)) {
            writer.write(gcModel);
            String[] lines = outputStream.toString().split(System.getProperty("line.separator"));
            Assert.assertEquals("line count", 2, lines.length);
            String[] firstLine = lines[0].split(" ");
            Assert.assertEquals("number of parts in line 1", 3, firstLine.length);
            Assert.assertEquals("timestamp", "0.677000", firstLine[1]);
        }
    }
}

