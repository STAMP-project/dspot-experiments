package com.sleekbyte.tailor.utils;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link SourceFileUtil}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SourceFileUtilTest {
    @Rule
    public TestName testName = new TestName();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private static final String INPUT_FILE = "inputFile.swift";

    private static final String NORMAL_LINE = "This is data for a file";

    private static final String NAME = "variableName";

    private File inputFile;

    private PrintWriter writer;

    @Mock
    private ParserRuleContext context;

    @Mock
    private Token startToken;

    @Mock
    private Token stopToken;

    @Test
    public void testFileTooLongMaxLengthZeroOrNegativeEmptyFile() {
        Assert.assertFalse(SourceFileUtil.fileTooLong(0, 0));
        Assert.assertFalse(SourceFileUtil.fileTooLong((-1), (-1)));
    }

    @Test
    public void testFileTooLongMaxLengthZeroOrNegative() {
        Assert.assertFalse(SourceFileUtil.fileTooLong(1, 0));
        Assert.assertFalse(SourceFileUtil.fileTooLong(1, (-1)));
    }

    @Test
    public void testFileTooLongMaxLengthValidEmptyFile() {
        Assert.assertFalse(SourceFileUtil.fileTooLong(0, 2));
    }

    @Test
    public void testFileTooLongMaxLengthValid() {
        Assert.assertTrue(SourceFileUtil.fileTooLong(3, 2));
        Assert.assertFalse(SourceFileUtil.fileTooLong(3, 3));
    }

    @Test
    public void testLineTooLongMaxLengthZeroOrNegative() {
        Assert.assertFalse(SourceFileUtil.lineTooLong(20, 0));
        Assert.assertFalse(SourceFileUtil.lineTooLong(20, (-1)));
    }

    @Test
    public void testLineTooLongMaxLengthValid() {
        Assert.assertFalse(SourceFileUtil.lineTooLong(25, 25));
        Assert.assertTrue(SourceFileUtil.lineTooLong(26, 25));
        Assert.assertTrue(SourceFileUtil.lineTooLong(50, 25));
    }

    @Test
    public void testLineHasTrailingWhitespaceInvalid() {
        String line = "";
        Assert.assertFalse(SourceFileUtil.lineHasTrailingWhitespace(line.length(), line));
        line = SourceFileUtilTest.NORMAL_LINE;
        Assert.assertFalse(SourceFileUtil.lineHasTrailingWhitespace(line.length(), line));
    }

    @Test
    public void testLineHasTrailingWhitespaceValid() {
        String line = "    ";
        Assert.assertTrue(SourceFileUtil.lineHasTrailingWhitespace(line.length(), line));
        line = "\t\t";
        Assert.assertTrue(SourceFileUtil.lineHasTrailingWhitespace(line.length(), line));
        line = (SourceFileUtilTest.NORMAL_LINE) + "    ";
        Assert.assertTrue(SourceFileUtil.lineHasTrailingWhitespace(line.length(), line));
        line = (SourceFileUtilTest.NORMAL_LINE) + "\t\t";
        Assert.assertTrue(SourceFileUtil.lineHasTrailingWhitespace(line.length(), line));
    }

    @Test
    public void testConstructTooLongMaxLengthZeroOrNegative() {
        Assert.assertFalse(SourceFileUtil.constructTooLong(context, 0));
        Assert.assertFalse(SourceFileUtil.constructTooLong(context, (-1)));
    }

    @Test
    public void testConstructTooLongMaxLengthValid() {
        Mockito.when(startToken.getLine()).thenReturn(1);
        Mockito.when(stopToken.getLine()).thenReturn(5);
        Assert.assertFalse(SourceFileUtil.constructTooLong(context, 10));
        Mockito.when(startToken.getLine()).thenReturn(1);
        Mockito.when(stopToken.getLine()).thenReturn(20);
        Assert.assertTrue(SourceFileUtil.constructTooLong(context, 12));
        Assert.assertFalse(SourceFileUtil.constructTooLong(context, 19));
    }

    @Test
    public void testNameTooLongMaxLengthZeroOrNegative() {
        Assert.assertFalse(SourceFileUtil.nameTooLong(context, 0));
        Assert.assertFalse(SourceFileUtil.nameTooLong(context, (-1)));
    }

    @Test
    public void testNameTooLongMaxLengthValid() {
        Mockito.when(context.getText()).thenReturn(SourceFileUtilTest.NAME);
        Assert.assertFalse(SourceFileUtil.nameTooLong(context, SourceFileUtilTest.NAME.length()));
        Assert.assertFalse(SourceFileUtil.nameTooLong(context, ((SourceFileUtilTest.NAME.length()) + 1)));
        Assert.assertTrue(SourceFileUtil.nameTooLong(context, ((SourceFileUtilTest.NAME.length()) - 10)));
        Mockito.when(context.getText()).thenReturn("");
        Assert.assertFalse(SourceFileUtil.nameTooLong(context, SourceFileUtilTest.NAME.length()));
    }

    @Test
    public void testNewlineTerminatedBlankFile() throws IOException {
        Assert.assertTrue(SourceFileUtil.singleNewlineTerminated(inputFile));
    }

    @Test
    public void testNewlineTerminatedNoNewline() throws IOException {
        writer.print("Line without a terminating newline.");
        writer.close();
        Assert.assertFalse(SourceFileUtil.singleNewlineTerminated(inputFile));
    }

    @Test
    public void testNewlineTerminatedOnlyNewline() throws IOException {
        writeNumOfLines(1, "");
        Assert.assertTrue(SourceFileUtil.singleNewlineTerminated(inputFile));
    }

    @Test
    public void testNewlineTerminatedWithNewline() throws IOException {
        writeNumOfLines(3, SourceFileUtilTest.NORMAL_LINE);
        Assert.assertTrue(SourceFileUtil.singleNewlineTerminated(inputFile));
    }

    @Test
    public void testNewlineTerminatedWithNoContentAndMultipleNewlines() throws IOException {
        writeNumOfLines(2, "");
        Assert.assertFalse(SourceFileUtil.singleNewlineTerminated(inputFile));
    }

    @Test
    public void testNewlineTerminatedWithSomeContentAndMultipleNewlines() throws IOException {
        writeNumOfLines(1, ((SourceFileUtilTest.NORMAL_LINE) + "\n"));
        Assert.assertFalse(SourceFileUtil.singleNewlineTerminated(inputFile));
    }

    @Test
    public void testHasLeadingWhitespaceBlankFile() throws IOException {
        Assert.assertFalse(SourceFileUtil.hasLeadingWhitespace(inputFile));
    }

    @Test
    public void testHasLeadingWhitespaceOnlyNewline() throws IOException {
        writeNumOfLines(1, "");
        Assert.assertTrue(SourceFileUtil.hasLeadingWhitespace(inputFile));
    }

    @Test
    public void testHasLeadingWhitespaceWithSingleLine() throws IOException {
        writeNumOfLines(1, SourceFileUtilTest.NORMAL_LINE);
        Assert.assertFalse(SourceFileUtil.hasLeadingWhitespace(inputFile));
    }

    @Test
    public void testHasLeadingWhitespaceWithSingleLineAndPrecedingNewline() throws IOException {
        writeNumOfLines(1, ("\n" + (SourceFileUtilTest.NORMAL_LINE)));
        Assert.assertTrue(SourceFileUtil.hasLeadingWhitespace(inputFile));
    }

    @Test
    public void testHasLeadingWhitespaceWithSingleLineAndPrecedingSpace() throws IOException {
        writeNumOfLines(1, (" " + (SourceFileUtilTest.NORMAL_LINE)));
        Assert.assertTrue(SourceFileUtil.hasLeadingWhitespace(inputFile));
    }

    @Test
    public void testHasLeadingWhitespaceWithSingleLineAndPrecedingTab() throws IOException {
        writeNumOfLines(1, ("\t" + (SourceFileUtilTest.NORMAL_LINE)));
        Assert.assertTrue(SourceFileUtil.hasLeadingWhitespace(inputFile));
    }
}

