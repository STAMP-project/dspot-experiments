package org.embulk.spi.util;


import org.embulk.EmbulkTestRuntime;
import org.embulk.spi.MockFileOutput;
import org.junit.Rule;
import org.junit.Test;


public class TestLineEncoder {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    @Test
    public void testAddLine() throws Exception {
        try (MockFileOutput output = new MockFileOutput()) {
            LineEncoder encoder = newEncoder("utf-8", "LF", output);
            encoder.nextFile();
            for (String line : new String[]{ "abc", "???(Japanese)" }) {
                encoder.addLine(line);
            }
            encoder.finish();
            // TODO
            // Iterator<Buffer> ite = output.getLastBuffers().iterator();
            // assertEquals("abc", bufferToString(ite.next(), "utf-8"));
            // assertEquals("\n", bufferToString(ite.next(), "utf-8"));
            // assertEquals("???(Japanese)", bufferToString(ite.next(), "utf-8"));
            // assertEquals("\n", bufferToString(ite.next(), "utf-8"));
            // assertFalse(ite.hasNext());
        }
    }

    @Test
    public void testAddTextAddNewLine() throws Exception {
        try (MockFileOutput output = new MockFileOutput()) {
            LineEncoder encoder = newEncoder("utf-8", "LF", output);
            encoder.nextFile();
            for (String line : new String[]{ "abc", "???(Japanese)" }) {
                encoder.addText(line);
                encoder.addNewLine();
            }
            encoder.finish();
            // TODO
            // Iterator<Buffer> ite = output.getLastBuffers().iterator();
            // assertEquals("abc", bufferToString(ite.next(), "utf-8"));
            // assertEquals("\n", bufferToString(ite.next(), "utf-8"));
            // assertEquals("???(Japanese)", bufferToString(ite.next(), "utf-8"));
            // assertEquals("\n", bufferToString(ite.next(), "utf-8"));
            // assertFalse(ite.hasNext());
        }
    }

    @Test
    public void testNewLine() throws Exception {
        try (MockFileOutput output = new MockFileOutput()) {
            LineEncoder encoder = newEncoder("utf-8", "CRLF", output);
            encoder.nextFile();
            for (String line : new String[]{ "abc", "???(Japanese)" }) {
                encoder.addLine(line);
            }
            encoder.finish();
            // TODO
            // Iterator<Buffer> ite = output.getLastBuffers().iterator();
            // assertEquals("abc", bufferToString(ite.next(), "utf-8"));
            // assertEquals("\r\n", bufferToString(ite.next(), "utf-8"));
            // assertEquals("???(Japanese)", bufferToString(ite.next(), "utf-8"));
            // assertEquals("\r\n", bufferToString(ite.next(), "utf-8"));
            // assertFalse(ite.hasNext());
        }
    }

    @Test
    public void testCharset() throws Exception {
        try (MockFileOutput output = new MockFileOutput()) {
            LineEncoder encoder = newEncoder("MS932", "CR", output);
            encoder.nextFile();
            for (String line : new String[]{ "abc", "???(Japanese)" }) {
                encoder.addLine(line);
            }
            encoder.finish();
            // TODO
            // Iterator<Buffer> ite = output.getLastBuffers().iterator();
            // assertEquals("abc", bufferToString(ite.next(), "MS932"));
            // assertEquals("\r", bufferToString(ite.next(), "MS932"));
            // assertEquals("???(Japanese)", bufferToString(ite.next(), "MS932"));
            // assertEquals("\r", bufferToString(ite.next(), "MS932"));
            // assertFalse(ite.hasNext());
        }
    }
}

