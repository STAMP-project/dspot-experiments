/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.shell;


import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FilterFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 * JUnit test class for {@link org.apache.hadoop.fs.shell.Ls}
 */
public class TestLs {
    private static Configuration conf;

    private static FileSystem mockFs;

    private static final Date NOW = new Date();

    // check that default options are correct
    @Test
    public void processOptionsNone() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertFalse(ls.isPathOnly());
        Assert.assertTrue(ls.isDirRecurse());
        Assert.assertFalse(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());
        Assert.assertFalse(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());
        Assert.assertFalse(ls.isOrderTime());
        Assert.assertFalse(ls.isUseAtime());
        Assert.assertFalse(ls.isDisplayECPolicy());
    }

    // check the -C option is recognised
    @Test
    public void processOptionsPathOnly() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-C");
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertTrue(ls.isPathOnly());
        Assert.assertTrue(ls.isDirRecurse());
        Assert.assertFalse(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());
        Assert.assertFalse(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());
        Assert.assertFalse(ls.isOrderTime());
        Assert.assertFalse(ls.isUseAtime());
        Assert.assertFalse(ls.isDisplayECPolicy());
    }

    // check the -d option is recognised
    @Test
    public void processOptionsDirectory() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-d");
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertFalse(ls.isPathOnly());
        Assert.assertFalse(ls.isDirRecurse());
        Assert.assertFalse(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());
        Assert.assertFalse(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());
        Assert.assertFalse(ls.isOrderTime());
        Assert.assertFalse(ls.isUseAtime());
        Assert.assertFalse(ls.isDisplayECPolicy());
    }

    // check the -h option is recognised
    @Test
    public void processOptionsHuman() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-h");
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertFalse(ls.isPathOnly());
        Assert.assertTrue(ls.isDirRecurse());
        Assert.assertTrue(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());
        Assert.assertFalse(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());
        Assert.assertFalse(ls.isOrderTime());
        Assert.assertFalse(ls.isUseAtime());
        Assert.assertFalse(ls.isDisplayECPolicy());
    }

    // check the -R option is recognised
    @Test
    public void processOptionsRecursive() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-R");
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertFalse(ls.isPathOnly());
        Assert.assertTrue(ls.isDirRecurse());
        Assert.assertFalse(ls.isHumanReadable());
        Assert.assertTrue(ls.isRecursive());
        Assert.assertFalse(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());
        Assert.assertFalse(ls.isOrderTime());
        Assert.assertFalse(ls.isUseAtime());
        Assert.assertFalse(ls.isDisplayECPolicy());
    }

    // check the -r option is recognised
    @Test
    public void processOptionsReverse() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-r");
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertFalse(ls.isPathOnly());
        Assert.assertTrue(ls.isDirRecurse());
        Assert.assertFalse(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());
        Assert.assertTrue(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());
        Assert.assertFalse(ls.isOrderTime());
        Assert.assertFalse(ls.isUseAtime());
        Assert.assertFalse(ls.isDisplayECPolicy());
    }

    // check the -S option is recognised
    @Test
    public void processOptionsSize() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-S");
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertFalse(ls.isPathOnly());
        Assert.assertTrue(ls.isDirRecurse());
        Assert.assertFalse(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());
        Assert.assertFalse(ls.isOrderReverse());
        Assert.assertTrue(ls.isOrderSize());
        Assert.assertFalse(ls.isOrderTime());
        Assert.assertFalse(ls.isUseAtime());
        Assert.assertFalse(ls.isDisplayECPolicy());
    }

    // check the -t option is recognised
    @Test
    public void processOptionsMtime() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-t");
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertFalse(ls.isPathOnly());
        Assert.assertTrue(ls.isDirRecurse());
        Assert.assertFalse(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());
        Assert.assertFalse(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());
        Assert.assertTrue(ls.isOrderTime());
        Assert.assertFalse(ls.isUseAtime());
        Assert.assertFalse(ls.isDisplayECPolicy());
    }

    // check the precedence of the -t and -S options
    @Test
    public void processOptionsMtimeSize() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-t");
        options.add("-S");
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertFalse(ls.isPathOnly());
        Assert.assertTrue(ls.isDirRecurse());
        Assert.assertFalse(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());
        Assert.assertFalse(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());
        Assert.assertTrue(ls.isOrderTime());
        Assert.assertFalse(ls.isUseAtime());
        Assert.assertFalse(ls.isDisplayECPolicy());
    }

    // check the precedence of the -t, -S and -r options
    @Test
    public void processOptionsMtimeSizeReverse() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-t");
        options.add("-S");
        options.add("-r");
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertFalse(ls.isPathOnly());
        Assert.assertTrue(ls.isDirRecurse());
        Assert.assertFalse(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());
        Assert.assertTrue(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());
        Assert.assertTrue(ls.isOrderTime());
        Assert.assertFalse(ls.isUseAtime());
        Assert.assertFalse(ls.isDisplayECPolicy());
    }

    // chheck the -u option is recognised
    @Test
    public void processOptionsAtime() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-u");
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertFalse(ls.isPathOnly());
        Assert.assertTrue(ls.isDirRecurse());
        Assert.assertFalse(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());
        Assert.assertFalse(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());
        Assert.assertFalse(ls.isOrderTime());
        Assert.assertTrue(ls.isUseAtime());
        Assert.assertFalse(ls.isDisplayECPolicy());
    }

    // chheck the -e option is recognised
    @Test
    public void processOptionsDisplayECPolicy() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-e");
        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertFalse(ls.isPathOnly());
        Assert.assertTrue(ls.isDirRecurse());
        Assert.assertFalse(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());
        Assert.assertFalse(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());
        Assert.assertFalse(ls.isOrderTime());
        Assert.assertFalse(ls.isUseAtime());
        Assert.assertTrue(ls.isDisplayECPolicy());
    }

    // check all options is handled correctly
    @Test
    public void processOptionsAll() throws IOException {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-C");// show file path only

        options.add("-d");// directory

        options.add("-h");// human readable

        options.add("-R");// recursive

        options.add("-r");// reverse order

        options.add("-t");// time order

        options.add("-S");// size order

        options.add("-u");// show atime

        options.add("-e");// show EC policies

        Ls ls = new Ls();
        ls.processOptions(options);
        Assert.assertTrue(ls.isPathOnly());
        Assert.assertFalse(ls.isDirRecurse());
        Assert.assertTrue(ls.isHumanReadable());
        Assert.assertFalse(ls.isRecursive());// -d overrules -R

        Assert.assertTrue(ls.isOrderReverse());
        Assert.assertFalse(ls.isOrderSize());// -t overrules -S

        Assert.assertTrue(ls.isOrderTime());
        Assert.assertTrue(ls.isUseAtime());
        Assert.assertTrue(ls.isDisplayECPolicy());
    }

    // check listing of a single file
    @Test
    public void processPathFile() throws IOException {
        TestLs.TestFile testfile = new TestLs.TestFile("testDir", "testFile");
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testfile.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println(testfile.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check listing of multiple files
    @Test
    public void processPathFiles() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDir01", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDir02", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDir03", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDir04", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDir05", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDir06", "testFile06");
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testfile01.getPathData());
        pathData.add(testfile02.getPathData());
        pathData.add(testfile03.getPathData());
        pathData.add(testfile04.getPathData());
        pathData.add(testfile05.getPathData());
        pathData.add(testfile06.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check listing of a single directory
    @Test
    public void processPathDirectory() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testfile01, testfile02, testfile03, testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check listing of multiple directories
    @Test
    public void processPathDirectories() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory01", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory01", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory01", "testFile03");
        TestLs.TestFile testDir01 = new TestLs.TestFile("", "testDirectory01");
        testDir01.setIsDir(true);
        testDir01.addContents(testfile01, testfile02, testfile03);
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory02", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory02", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory02", "testFile06");
        TestLs.TestFile testDir02 = new TestLs.TestFile("", "testDirectory02");
        testDir02.setIsDir(true);
        testDir02.addContents(testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir01.getPathData());
        pathData.add(testDir02.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 3 items");
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println("Found 3 items");
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check the default ordering
    @Test
    public void processPathDirOrderDefault() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        // add contents in non-lexigraphic order to show they get sorted
        testDir.addContents(testfile01, testfile03, testfile05, testfile02, testfile04, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check reverse default ordering
    @Test
    public void processPathDirOrderDefaultReverse() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        // add contents in non-lexigraphic order to show they get sorted
        testDir.addContents(testfile01, testfile03, testfile05, testfile02, testfile04, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-r");
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check mtime ordering (-t option); most recent first in line with unix
    // convention
    @Test
    public void processPathDirOrderMtime() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        // set file mtime in different order to file names
        testfile01.setMtime(((TestLs.NOW.getTime()) + 10));
        testfile02.setMtime(((TestLs.NOW.getTime()) + 30));
        testfile03.setMtime(((TestLs.NOW.getTime()) + 20));
        testfile04.setMtime(((TestLs.NOW.getTime()) + 60));
        testfile05.setMtime(((TestLs.NOW.getTime()) + 50));
        testfile06.setMtime(((TestLs.NOW.getTime()) + 40));
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testfile01, testfile02, testfile03, testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-t");
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check reverse mtime ordering (-t -r options)
    @Test
    public void processPathDirOrderMtimeReverse() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        // set file mtime in different order to file names
        testfile01.setMtime(((TestLs.NOW.getTime()) + 10));
        testfile02.setMtime(((TestLs.NOW.getTime()) + 30));
        testfile03.setMtime(((TestLs.NOW.getTime()) + 20));
        testfile04.setMtime(((TestLs.NOW.getTime()) + 60));
        testfile05.setMtime(((TestLs.NOW.getTime()) + 50));
        testfile06.setMtime(((TestLs.NOW.getTime()) + 40));
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testfile01, testfile02, testfile03, testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-t");
        options.add("-r");
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check multiple directories are order independently
    @Test
    public void processPathDirsOrderMtime() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory01", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory01", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory01", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory02", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory02", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory02", "testFile06");
        // set file mtime in different order to file names
        testfile01.setMtime(((TestLs.NOW.getTime()) + 10));
        testfile02.setMtime(((TestLs.NOW.getTime()) + 30));
        testfile03.setMtime(((TestLs.NOW.getTime()) + 20));
        testfile04.setMtime(((TestLs.NOW.getTime()) + 60));
        testfile05.setMtime(((TestLs.NOW.getTime()) + 40));
        testfile06.setMtime(((TestLs.NOW.getTime()) + 50));
        TestLs.TestFile testDir01 = new TestLs.TestFile("", "testDirectory01");
        testDir01.setIsDir(true);
        testDir01.addContents(testfile01, testfile02, testfile03);
        TestLs.TestFile testDir02 = new TestLs.TestFile("", "testDirectory02");
        testDir02.setIsDir(true);
        testDir02.addContents(testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir01.getPathData());
        pathData.add(testDir02.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-t");
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 3 items");
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        inOrder.verify(out).println("Found 3 items");
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check mtime ordering with large time gaps between files (checks integer
    // overflow issues)
    @Test
    public void processPathDirOrderMtimeYears() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        // set file mtime in different order to file names
        testfile01.setMtime(((TestLs.NOW.getTime()) + (Integer.MAX_VALUE)));
        testfile02.setMtime(((TestLs.NOW.getTime()) + (Integer.MIN_VALUE)));
        testfile03.setMtime(((TestLs.NOW.getTime()) + 0));
        testfile04.setMtime((((TestLs.NOW.getTime()) + (Integer.MAX_VALUE)) + (Integer.MAX_VALUE)));
        testfile05.setMtime(((TestLs.NOW.getTime()) + 0));
        testfile06.setMtime((((TestLs.NOW.getTime()) + (Integer.MIN_VALUE)) + (Integer.MIN_VALUE)));
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testfile01, testfile02, testfile03, testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-t");
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check length order (-S option)
    @Test
    public void processPathDirOrderLength() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        // set file length in different order to file names
        long length = 1234567890;
        testfile01.setLength((length + 10));
        testfile02.setLength((length + 30));
        testfile03.setLength((length + 20));
        testfile04.setLength((length + 60));
        testfile05.setLength((length + 50));
        testfile06.setLength((length + 40));
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testfile01, testfile02, testfile03, testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-S");
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check reverse length order (-S -r options)
    @Test
    public void processPathDirOrderLengthReverse() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        // set file length in different order to file names
        long length = 1234567890;
        testfile01.setLength((length + 10));
        testfile02.setLength((length + 30));
        testfile03.setLength((length + 20));
        testfile04.setLength((length + 60));
        testfile05.setLength((length + 50));
        testfile06.setLength((length + 40));
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testfile01, testfile02, testfile03, testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-S");
        options.add("-r");
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check length ordering with large size gaps between files (checks integer
    // overflow issues)
    @Test
    public void processPathDirOrderLengthLarge() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        // set file length in different order to file names
        long length = 1234567890;
        testfile01.setLength((length + (3L * (Integer.MAX_VALUE))));
        testfile02.setLength((length + (Integer.MAX_VALUE)));
        testfile03.setLength((length + (2L * (Integer.MAX_VALUE))));
        testfile04.setLength((length + (4L * (Integer.MAX_VALUE))));
        testfile05.setLength((length + (2L * (Integer.MAX_VALUE))));
        testfile06.setLength((length + 0));
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testfile01, testfile02, testfile03, testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-S");
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile04.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile01.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineMtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineMtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check access time display (-u option)
    @Test
    public void processPathDirectoryAtime() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testfile01, testfile02, testfile03, testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-u");
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile01.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile04.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineAtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check access time order (-u -t options)
    @Test
    public void processPathDirOrderAtime() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        // set file atime in different order to file names
        testfile01.setAtime(((TestLs.NOW.getTime()) + 10));
        testfile02.setAtime(((TestLs.NOW.getTime()) + 30));
        testfile03.setAtime(((TestLs.NOW.getTime()) + 20));
        testfile04.setAtime(((TestLs.NOW.getTime()) + 60));
        testfile05.setAtime(((TestLs.NOW.getTime()) + 50));
        testfile06.setAtime(((TestLs.NOW.getTime()) + 40));
        // set file mtime in different order to atime
        testfile01.setMtime(((TestLs.NOW.getTime()) + 60));
        testfile02.setMtime(((TestLs.NOW.getTime()) + 50));
        testfile03.setMtime(((TestLs.NOW.getTime()) + 20));
        testfile04.setMtime(((TestLs.NOW.getTime()) + 30));
        testfile05.setMtime(((TestLs.NOW.getTime()) + 10));
        testfile06.setMtime(((TestLs.NOW.getTime()) + 40));
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testfile01, testfile02, testfile03, testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-t");
        options.add("-u");
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile04.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile01.formatLineAtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check reverse access time order (-u -t -r options)
    @Test
    public void processPathDirOrderAtimeReverse() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        // set file atime in different order to file names
        testfile01.setAtime(((TestLs.NOW.getTime()) + 10));
        testfile02.setAtime(((TestLs.NOW.getTime()) + 30));
        testfile03.setAtime(((TestLs.NOW.getTime()) + 20));
        testfile04.setAtime(((TestLs.NOW.getTime()) + 60));
        testfile05.setAtime(((TestLs.NOW.getTime()) + 50));
        testfile06.setAtime(((TestLs.NOW.getTime()) + 40));
        // set file mtime in different order to atime
        testfile01.setMtime(((TestLs.NOW.getTime()) + 60));
        testfile02.setMtime(((TestLs.NOW.getTime()) + 50));
        testfile03.setMtime(((TestLs.NOW.getTime()) + 20));
        testfile04.setMtime(((TestLs.NOW.getTime()) + 30));
        testfile05.setMtime(((TestLs.NOW.getTime()) + 10));
        testfile06.setMtime(((TestLs.NOW.getTime()) + 40));
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testfile01, testfile02, testfile03, testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-t");
        options.add("-u");
        options.add("-r");
        ls.processOptions(options);
        String lineFormat = TestLs.TestFile.computeLineFormat(pathData);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println("Found 6 items");
        inOrder.verify(out).println(testfile01.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile03.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile02.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile06.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile05.formatLineAtime(lineFormat));
        inOrder.verify(out).println(testfile04.formatLineAtime(lineFormat));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check path only display (-C option)
    @Test
    public void processPathDirectoryPathOnly() throws IOException {
        TestLs.TestFile testfile01 = new TestLs.TestFile("testDirectory", "testFile01");
        TestLs.TestFile testfile02 = new TestLs.TestFile("testDirectory", "testFile02");
        TestLs.TestFile testfile03 = new TestLs.TestFile("testDirectory", "testFile03");
        TestLs.TestFile testfile04 = new TestLs.TestFile("testDirectory", "testFile04");
        TestLs.TestFile testfile05 = new TestLs.TestFile("testDirectory", "testFile05");
        TestLs.TestFile testfile06 = new TestLs.TestFile("testDirectory", "testFile06");
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testfile01, testfile02, testfile03, testfile04, testfile05, testfile06);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        PrintStream out = Mockito.mock(PrintStream.class);
        Ls ls = new Ls();
        ls.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-C");
        ls.processOptions(options);
        ls.processArguments(pathData);
        InOrder inOrder = Mockito.inOrder(out);
        inOrder.verify(out).println(testfile01.getPath().toString());
        inOrder.verify(out).println(testfile02.getPath().toString());
        inOrder.verify(out).println(testfile03.getPath().toString());
        inOrder.verify(out).println(testfile04.getPath().toString());
        inOrder.verify(out).println(testfile05.getPath().toString());
        inOrder.verify(out).println(testfile06.getPath().toString());
        Mockito.verifyNoMoreInteractions(out);
    }

    @Test
    public void displayWarningsOnLocalFileSystem() throws IOException {
        // Display warnings.
        TestLs.displayWarningOnLocalFileSystem(true);
        // Does not display warnings.
        TestLs.displayWarningOnLocalFileSystem(false);
    }

    // check the deprecated flag isn't set
    @Test
    public void isDeprecated() {
        Ls ls = new Ls();
        boolean actual = ls.isDeprecated();
        boolean expected = false;
        Assert.assertEquals("Ls.isDeprecated", expected, actual);
    }

    // check there's no replacement command
    @Test
    public void getReplacementCommand() {
        Ls ls = new Ls();
        String actual = ls.getReplacementCommand();
        String expected = null;
        Assert.assertEquals("Ls.getReplacementCommand", expected, actual);
    }

    // check the correct name is returned
    @Test
    public void getName() {
        Ls ls = new Ls();
        String actual = ls.getName();
        String expected = "ls";
        Assert.assertEquals("Ls.getName", expected, actual);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void processPathFileDisplayECPolicyWhenUnsupported() throws IOException {
        TestLs.TestFile testFile = new TestLs.TestFile("testDirectory", "testFile");
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testFile.getPathData());
        Ls ls = new Ls();
        LinkedList<String> options = new LinkedList<String>();
        options.add("-e");
        ls.processOptions(options);
        ls.processArguments(pathData);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void processPathDirDisplayECPolicyWhenUnsupported() throws IOException {
        TestLs.TestFile testFile = new TestLs.TestFile("testDirectory", "testFile");
        TestLs.TestFile testDir = new TestLs.TestFile("", "testDirectory");
        testDir.setIsDir(true);
        testDir.addContents(testFile);
        LinkedList<PathData> pathData = new LinkedList<PathData>();
        pathData.add(testDir.getPathData());
        Ls ls = new Ls();
        LinkedList<String> options = new LinkedList<String>();
        options.add("-e");
        ls.processOptions(options);
        ls.processArguments(pathData);
    }

    // test class representing a file to be listed
    static class TestFile {
        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        private static final boolean DEFAULT_ISDIR = false;

        private static final String DEFAULT_MODE = "750";

        private static final int DEFAULT_REPLICATION = 3;

        private static final String DEFAULT_OWNER = "test_owner";

        private static final String DEFAULT_GROUP = "test_group";

        private static final long DEFAULT_LENGTH = 1234567890L;

        private static final long DEFAULT_MTIME = (TestLs.NOW.getTime()) - 86400000;

        private static final long DEFAULT_ATIME = (TestLs.NOW.getTime()) + 86400000;

        private static final long DEFAULT_BLOCKSIZE = (64L * 1024) * 1024;

        private String dirname;

        private String filename;

        private boolean isDir;

        private FsPermission permission;

        private int replication;

        private String owner;

        private String group;

        private long length;

        private long mtime;

        private long atime;

        private long blocksize;

        private ArrayList<FileStatus> contents = new ArrayList<FileStatus>();

        private Path path = null;

        private FileStatus fileStatus = null;

        private PathData pathData = null;

        public TestFile(String dirname, String filename) {
            setDirname(dirname);
            setFilename(filename);
            setIsDir(TestLs.TestFile.DEFAULT_ISDIR);
            setPermission(TestLs.TestFile.DEFAULT_MODE);
            setReplication(TestLs.TestFile.DEFAULT_REPLICATION);
            setOwner(TestLs.TestFile.DEFAULT_OWNER);
            setGroup(TestLs.TestFile.DEFAULT_GROUP);
            setLength(TestLs.TestFile.DEFAULT_LENGTH);
            setMtime(TestLs.TestFile.DEFAULT_MTIME);
            setAtime(TestLs.TestFile.DEFAULT_ATIME);
            setBlocksize(TestLs.TestFile.DEFAULT_BLOCKSIZE);
        }

        public void setDirname(String dirname) {
            this.dirname = dirname;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public void setIsDir(boolean isDir) {
            this.isDir = isDir;
        }

        public void setPermission(String mode) {
            setPermission(new FsPermission(mode));
        }

        public void setPermission(FsPermission permission) {
            this.permission = permission;
        }

        public void setReplication(int replication) {
            this.replication = replication;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public void setMtime(long mtime) {
            this.mtime = mtime;
        }

        public void setAtime(long atime) {
            this.atime = atime;
        }

        public void setBlocksize(long blocksize) {
            this.blocksize = blocksize;
        }

        public void addContents(TestLs.TestFile... contents) {
            for (TestLs.TestFile testFile : contents) {
                this.contents.add(testFile.getFileStatus());
            }
        }

        private String getDirname() {
            return this.dirname;
        }

        private String getFilename() {
            return this.filename;
        }

        private String getPathname() {
            return ((getDirname()) + "/") + (getFilename());
        }

        private boolean isDir() {
            return this.isDir;
        }

        private boolean isFile() {
            return !(this.isDir());
        }

        private FsPermission getPermission() {
            return this.permission;
        }

        private int getReplication() {
            return this.replication;
        }

        private String getOwner() {
            return this.owner;
        }

        private String getGroup() {
            return this.group;
        }

        private long getLength() {
            return this.length;
        }

        private long getMtime() {
            return this.mtime;
        }

        private long getAtime() {
            return this.atime;
        }

        private long getBlocksize() {
            return this.blocksize;
        }

        private FileStatus[] getContents() {
            return this.contents.toArray(new FileStatus[0]);
        }

        /**
         * Returns a formated output line based on the given format mask, file
         * status and file name.
         *
         * @param lineFormat
         * 		format mask
         * @param fileStatus
         * 		file status
         * @param fileName
         * 		file name
         * @return formated line
         */
        private String formatLineMtime(String lineFormat) {
            return String.format(lineFormat, (isDir() ? "d" : "-"), getPermission(), (isFile() ? getReplication() : "-"), getOwner(), getGroup(), String.valueOf(getLength()), TestLs.TestFile.DATE_FORMAT.format(new Date(getMtime())), getPathname());
        }

        /**
         * Returns a formated output line based on the given format mask, file
         * status and file name.
         *
         * @param lineFormat
         * 		format mask
         * @param fileStatus
         * 		file status
         * @param fileName
         * 		file name
         * @return formated line
         */
        private String formatLineAtime(String lineFormat) {
            return String.format(lineFormat, (isDir() ? "d" : "-"), getPermission(), (isFile() ? getReplication() : "-"), getOwner(), getGroup(), String.valueOf(getLength()), TestLs.TestFile.DATE_FORMAT.format(new Date(getAtime())), getPathname());
        }

        public FileStatus getFileStatus() {
            if ((fileStatus) == null) {
                Path path = getPath();
                fileStatus = new FileStatus(getLength(), isDir(), getReplication(), getBlocksize(), getMtime(), getAtime(), getPermission(), getOwner(), getGroup(), path);
            }
            return fileStatus;
        }

        public Path getPath() {
            if ((path) == null) {
                if (((getDirname()) != null) && (!(getDirname().equals("")))) {
                    path = new Path(getDirname(), getFilename());
                } else {
                    path = new Path(getFilename());
                }
            }
            return path;
        }

        public PathData getPathData() throws IOException {
            if ((pathData) == null) {
                FileStatus fileStatus = getFileStatus();
                Path path = getPath();
                Mockito.when(TestLs.mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(fileStatus);
                pathData = new PathData(path.toString(), TestLs.conf);
                if ((getContents().length) != 0) {
                    Mockito.when(TestLs.mockFs.listStatus(ArgumentMatchers.eq(path))).thenReturn(getContents());
                }
            }
            return pathData;
        }

        /**
         * Compute format string based on maximum column widths. Copied from
         * Ls.adjustColumnWidths as these tests are more interested in proving
         * regression rather than absolute format.
         *
         * @param items
         * 		to find the max field width for each column
         */
        public static String computeLineFormat(LinkedList<PathData> items) {
            int maxRepl = 3;
            int maxLen = 10;
            int maxOwner = 0;
            int maxGroup = 0;
            for (PathData item : items) {
                FileStatus stat = item.stat;
                maxRepl = TestLs.TestFile.maxLength(maxRepl, stat.getReplication());
                maxLen = TestLs.TestFile.maxLength(maxLen, stat.getLen());
                maxOwner = TestLs.TestFile.maxLength(maxOwner, stat.getOwner());
                maxGroup = TestLs.TestFile.maxLength(maxGroup, stat.getGroup());
            }
            StringBuilder fmt = new StringBuilder();
            fmt.append("%s%s ");// permission string

            fmt.append((("%" + maxRepl) + "s "));
            // Do not use '%-0s' as a formatting conversion, since it will throw a
            // a MissingFormatWidthException if it is used in String.format().
            // http://docs.oracle.com/javase/1.5.0/docs/api/java/util/Formatter.html#intFlags
            fmt.append((maxOwner > 0 ? ("%-" + maxOwner) + "s " : "%s"));
            fmt.append((maxGroup > 0 ? ("%-" + maxGroup) + "s " : "%s"));
            fmt.append((("%" + maxLen) + "s "));
            fmt.append("%s %s");// mod time & path

            return fmt.toString();
        }

        /**
         * Return the maximum of two values, treating null as 0
         *
         * @param n
         * 		integer to be compared
         * @param value
         * 		value to be compared
         * @return maximum of the two inputs
         */
        private static int maxLength(int n, Object value) {
            return Math.max(n, (value != null ? String.valueOf(value).length() : 0));
        }
    }

    static class MockFileSystem extends FilterFileSystem {
        Configuration conf;

        MockFileSystem() {
            super(TestLs.mockFs);
        }

        @Override
        public void initialize(URI uri, Configuration conf) {
            this.conf = conf;
        }

        @Override
        public Path makeQualified(Path path) {
            return path;
        }

        @Override
        public Configuration getConf() {
            return conf;
        }
    }
}

