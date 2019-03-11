package com.sleekbyte.tailor.listeners;


import com.sleekbyte.tailor.common.ConstructLengths;
import com.sleekbyte.tailor.common.Rules;
import com.sleekbyte.tailor.format.Formatter;
import com.sleekbyte.tailor.output.Printer;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link FileListener}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FileListenerTest {
    @Rule
    public TestName testName = new TestName();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private static final String INPUT_FILE = "inputFile.swift";

    private static final String NORMAL_LINE = "This is data for a file";

    private File inputFile;

    private PrintWriter writer;

    private Printer printer;

    private Set<Rules> enabledRules;

    private Formatter formatter;

    @Test
    public void testNumLinesInFileZeroLines() throws IOException {
        try (FileListener fileListener = new FileListener(printer, inputFile, new ConstructLengths(), enabledRules)) {
            fileListener.verify();
            Assert.assertEquals(0, fileListener.getNumOfLines());
        }
    }

    @Test
    public void testNumLinesInFileOneLine() throws IOException {
        writeNumOfLines(1, FileListenerTest.NORMAL_LINE);
        try (FileListener fileListener = new FileListener(printer, inputFile, new ConstructLengths(), enabledRules)) {
            fileListener.verify();
            Assert.assertEquals(1, fileListener.getNumOfLines());
        }
    }

    @Test
    public void testNumLinesInFileMultipleLines() throws IOException {
        writeNumOfLines(4, FileListenerTest.NORMAL_LINE);
        try (FileListener fileListener = new FileListener(printer, inputFile, new ConstructLengths(), enabledRules)) {
            fileListener.verify();
            Assert.assertEquals(4, fileListener.getNumOfLines());
        }
    }
}

