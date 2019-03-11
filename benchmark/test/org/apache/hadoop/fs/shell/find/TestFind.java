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
package org.apache.hadoop.fs.shell.find;


import Path.CUR_DIR;
import Result.PASS;
import Result.STOP;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.shell.PathData;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class TestFind {
    @Rule
    public Timeout timeout = new Timeout(10000);

    private static FileSystem mockFs;

    private static Configuration conf;

    // check follow link option is recognized
    @Test
    public void processOptionsFollowLink() throws IOException {
        Find find = new Find();
        String args = "-L path";
        find.processOptions(getArgs(args));
        Assert.assertTrue(find.getOptions().isFollowLink());
        Assert.assertFalse(find.getOptions().isFollowArgLink());
    }

    // check follow arg link option is recognized
    @Test
    public void processOptionsFollowArgLink() throws IOException {
        Find find = new Find();
        String args = "-H path";
        find.processOptions(getArgs(args));
        Assert.assertFalse(find.getOptions().isFollowLink());
        Assert.assertTrue(find.getOptions().isFollowArgLink());
    }

    // check follow arg link option is recognized
    @Test
    public void processOptionsFollowLinkFollowArgLink() throws IOException {
        Find find = new Find();
        String args = "-L -H path";
        find.processOptions(getArgs(args));
        Assert.assertTrue(find.getOptions().isFollowLink());
        // follow link option takes precedence over follow arg link
        Assert.assertFalse(find.getOptions().isFollowArgLink());
    }

    // check options and expressions are stripped from args leaving paths
    @Test
    public void processOptionsExpression() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String paths = "path1 path2 path3";
        String args = ("-L -H " + paths) + " -print -name test";
        LinkedList<String> argsList = getArgs(args);
        find.processOptions(argsList);
        LinkedList<String> pathList = getArgs(paths);
        Assert.assertEquals(pathList, argsList);
    }

    // check print is used as the default expression
    @Test
    public void processOptionsNoExpression() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String args = "path";
        String expected = "Print(;)";
        find.processOptions(getArgs(args));
        Expression expression = find.getRootExpression();
        Assert.assertEquals(expected, expression.toString());
    }

    // check unknown options are rejected
    @Test
    public void processOptionsUnknown() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String args = "path -unknown";
        try {
            find.processOptions(getArgs(args));
            Assert.fail("Unknown expression not caught");
        } catch (IOException e) {
        }
    }

    // check unknown options are rejected when mixed with known options
    @Test
    public void processOptionsKnownUnknown() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String args = "path -print -unknown -print";
        try {
            find.processOptions(getArgs(args));
            Assert.fail("Unknown expression not caught");
        } catch (IOException e) {
        }
    }

    // check no path defaults to current working directory
    @Test
    public void processOptionsNoPath() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String args = "-print";
        LinkedList<String> argsList = getArgs(args);
        find.processOptions(argsList);
        Assert.assertEquals(Collections.singletonList(CUR_DIR), argsList);
    }

    // check -name is handled correctly
    @Test
    public void processOptionsName() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String args = "path -name namemask";
        String expected = "And(;Name(namemask;),Print(;))";
        find.processOptions(getArgs(args));
        Expression expression = find.getRootExpression();
        Assert.assertEquals(expected, expression.toString());
    }

    // check -iname is handled correctly
    @Test
    public void processOptionsIname() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String args = "path -iname namemask";
        String expected = "And(;Iname-Name(namemask;),Print(;))";
        find.processOptions(getArgs(args));
        Expression expression = find.getRootExpression();
        Assert.assertEquals(expected, expression.toString());
    }

    // check -print is handled correctly
    @Test
    public void processOptionsPrint() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String args = "path -print";
        String expected = "Print(;)";
        find.processOptions(getArgs(args));
        Expression expression = find.getRootExpression();
        Assert.assertEquals(expected, expression.toString());
    }

    // check -print0 is handled correctly
    @Test
    public void processOptionsPrint0() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String args = "path -print0";
        String expected = "Print0-Print(;)";
        find.processOptions(getArgs(args));
        Expression expression = find.getRootExpression();
        Assert.assertEquals(expected, expression.toString());
    }

    // check an implicit and is handled correctly
    @Test
    public void processOptionsNoop() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String args = "path -name one -name two -print";
        String expected = "And(;And(;Name(one;),Name(two;)),Print(;))";
        find.processOptions(getArgs(args));
        Expression expression = find.getRootExpression();
        Assert.assertEquals(expected, expression.toString());
    }

    // check -a is handled correctly
    @Test
    public void processOptionsA() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String args = "path -name one -a -name two -a -print";
        String expected = "And(;And(;Name(one;),Name(two;)),Print(;))";
        find.processOptions(getArgs(args));
        Expression expression = find.getRootExpression();
        Assert.assertEquals(expected, expression.toString());
    }

    // check -and is handled correctly
    @Test
    public void processOptionsAnd() throws IOException {
        Find find = new Find();
        find.setConf(TestFind.conf);
        String args = "path -name one -and -name two -and -print";
        String expected = "And(;And(;Name(one;),Name(two;)),Print(;))";
        find.processOptions(getArgs(args));
        Expression expression = find.getRootExpression();
        Assert.assertEquals(expected, expression.toString());
    }

    // check expressions are called in the correct order
    @Test
    public void processArguments() throws IOException {
        LinkedList<PathData> items = createDirectories();
        Find find = new Find();
        find.setConf(TestFind.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        find.getOptions().setOut(out);
        PrintStream err = Mockito.mock(PrintStream.class);
        find.getOptions().setErr(err);
        Expression expr = Mockito.mock(Expression.class);
        Mockito.when(expr.apply(((PathData) (ArgumentMatchers.any())), ArgumentMatchers.anyInt())).thenReturn(PASS);
        TestFind.FileStatusChecker fsCheck = Mockito.mock(TestFind.FileStatusChecker.class);
        Expression test = new TestFind.TestExpression(expr, fsCheck);
        find.setRootExpression(test);
        find.processArguments(items);
        InOrder inOrder = Mockito.inOrder(expr);
        inOrder.verify(expr).setOptions(find.getOptions());
        inOrder.verify(expr).prepare();
        inOrder.verify(expr).apply(item1, 0);
        inOrder.verify(expr).apply(item1a, 1);
        inOrder.verify(expr).apply(item1aa, 2);
        inOrder.verify(expr).apply(item1b, 1);
        inOrder.verify(expr).apply(item2, 0);
        inOrder.verify(expr).apply(item3, 0);
        inOrder.verify(expr).apply(item4, 0);
        inOrder.verify(expr).apply(item5, 0);
        inOrder.verify(expr).apply(item5a, 1);
        inOrder.verify(expr).apply(item5b, 1);
        inOrder.verify(expr).apply(item5c, 1);
        inOrder.verify(expr).apply(item5ca, 2);
        inOrder.verify(expr).apply(item5d, 1);
        inOrder.verify(expr).apply(item5e, 1);
        inOrder.verify(expr).finish();
        Mockito.verifyNoMoreInteractions(expr);
        InOrder inOrderFsCheck = Mockito.inOrder(fsCheck);
        inOrderFsCheck.verify(fsCheck).check(item1.stat);
        inOrderFsCheck.verify(fsCheck).check(item1a.stat);
        inOrderFsCheck.verify(fsCheck).check(item1aa.stat);
        inOrderFsCheck.verify(fsCheck).check(item1b.stat);
        inOrderFsCheck.verify(fsCheck).check(item2.stat);
        inOrderFsCheck.verify(fsCheck).check(item3.stat);
        inOrderFsCheck.verify(fsCheck).check(item4.stat);
        inOrderFsCheck.verify(fsCheck).check(item5.stat);
        inOrderFsCheck.verify(fsCheck).check(item5a.stat);
        inOrderFsCheck.verify(fsCheck).check(item5b.stat);
        inOrderFsCheck.verify(fsCheck).check(item5c.stat);
        inOrderFsCheck.verify(fsCheck).check(item5ca.stat);
        inOrderFsCheck.verify(fsCheck).check(item5d.stat);
        inOrderFsCheck.verify(fsCheck).check(item5e.stat);
        Mockito.verifyNoMoreInteractions(fsCheck);
        Mockito.verifyNoMoreInteractions(out);
        Mockito.verifyNoMoreInteractions(err);
    }

    // check that directories are descended correctly when -depth is specified
    @Test
    public void processArgumentsDepthFirst() throws IOException {
        LinkedList<PathData> items = createDirectories();
        Find find = new Find();
        find.getOptions().setDepthFirst(true);
        find.setConf(TestFind.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        find.getOptions().setOut(out);
        PrintStream err = Mockito.mock(PrintStream.class);
        find.getOptions().setErr(err);
        Expression expr = Mockito.mock(Expression.class);
        Mockito.when(expr.apply(((PathData) (ArgumentMatchers.any())), ArgumentMatchers.anyInt())).thenReturn(PASS);
        TestFind.FileStatusChecker fsCheck = Mockito.mock(TestFind.FileStatusChecker.class);
        Expression test = new TestFind.TestExpression(expr, fsCheck);
        find.setRootExpression(test);
        find.processArguments(items);
        InOrder inOrder = Mockito.inOrder(expr);
        inOrder.verify(expr).setOptions(find.getOptions());
        inOrder.verify(expr).prepare();
        inOrder.verify(expr).apply(item1aa, 2);
        inOrder.verify(expr).apply(item1a, 1);
        inOrder.verify(expr).apply(item1b, 1);
        inOrder.verify(expr).apply(item1, 0);
        inOrder.verify(expr).apply(item2, 0);
        inOrder.verify(expr).apply(item3, 0);
        inOrder.verify(expr).apply(item4, 0);
        inOrder.verify(expr).apply(item5a, 1);
        inOrder.verify(expr).apply(item5b, 1);
        inOrder.verify(expr).apply(item5ca, 2);
        inOrder.verify(expr).apply(item5c, 1);
        inOrder.verify(expr).apply(item5d, 1);
        inOrder.verify(expr).apply(item5e, 1);
        inOrder.verify(expr).apply(item5, 0);
        inOrder.verify(expr).finish();
        Mockito.verifyNoMoreInteractions(expr);
        InOrder inOrderFsCheck = Mockito.inOrder(fsCheck);
        inOrderFsCheck.verify(fsCheck).check(item1aa.stat);
        inOrderFsCheck.verify(fsCheck).check(item1a.stat);
        inOrderFsCheck.verify(fsCheck).check(item1b.stat);
        inOrderFsCheck.verify(fsCheck).check(item1.stat);
        inOrderFsCheck.verify(fsCheck).check(item2.stat);
        inOrderFsCheck.verify(fsCheck).check(item3.stat);
        inOrderFsCheck.verify(fsCheck).check(item4.stat);
        inOrderFsCheck.verify(fsCheck).check(item5a.stat);
        inOrderFsCheck.verify(fsCheck).check(item5b.stat);
        inOrderFsCheck.verify(fsCheck).check(item5ca.stat);
        inOrderFsCheck.verify(fsCheck).check(item5c.stat);
        inOrderFsCheck.verify(fsCheck).check(item5d.stat);
        inOrderFsCheck.verify(fsCheck).check(item5e.stat);
        inOrderFsCheck.verify(fsCheck).check(item5.stat);
        Mockito.verifyNoMoreInteractions(fsCheck);
        Mockito.verifyNoMoreInteractions(out);
        Mockito.verifyNoMoreInteractions(err);
    }

    // check symlinks given as path arguments are processed correctly with the
    // follow arg option set
    @Test
    public void processArgumentsOptionFollowArg() throws IOException {
        LinkedList<PathData> items = createDirectories();
        Find find = new Find();
        find.getOptions().setFollowArgLink(true);
        find.setConf(TestFind.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        find.getOptions().setOut(out);
        PrintStream err = Mockito.mock(PrintStream.class);
        find.getOptions().setErr(err);
        Expression expr = Mockito.mock(Expression.class);
        Mockito.when(expr.apply(((PathData) (ArgumentMatchers.any())), ArgumentMatchers.anyInt())).thenReturn(PASS);
        TestFind.FileStatusChecker fsCheck = Mockito.mock(TestFind.FileStatusChecker.class);
        Expression test = new TestFind.TestExpression(expr, fsCheck);
        find.setRootExpression(test);
        find.processArguments(items);
        InOrder inOrder = Mockito.inOrder(expr);
        inOrder.verify(expr).setOptions(find.getOptions());
        inOrder.verify(expr).prepare();
        inOrder.verify(expr).apply(item1, 0);
        inOrder.verify(expr).apply(item1a, 1);
        inOrder.verify(expr).apply(item1aa, 2);
        inOrder.verify(expr).apply(item1b, 1);
        inOrder.verify(expr).apply(item2, 0);
        inOrder.verify(expr).apply(item3, 0);
        inOrder.verify(expr).apply(item4, 0);
        inOrder.verify(expr).apply(item5, 0);
        inOrder.verify(expr).apply(item5a, 1);
        inOrder.verify(expr).apply(item5b, 1);
        inOrder.verify(expr).apply(item5c, 1);
        inOrder.verify(expr).apply(item5ca, 2);
        inOrder.verify(expr).apply(item5d, 1);
        inOrder.verify(expr).apply(item5e, 1);
        inOrder.verify(expr).finish();
        Mockito.verifyNoMoreInteractions(expr);
        InOrder inOrderFsCheck = Mockito.inOrder(fsCheck);
        inOrderFsCheck.verify(fsCheck).check(item1.stat);
        inOrderFsCheck.verify(fsCheck).check(item1a.stat);
        inOrderFsCheck.verify(fsCheck).check(item1aa.stat);
        inOrderFsCheck.verify(fsCheck).check(item1b.stat);
        inOrderFsCheck.verify(fsCheck).check(item2.stat);
        inOrderFsCheck.verify(fsCheck, Mockito.times(2)).check(item3.stat);
        inOrderFsCheck.verify(fsCheck).check(item5.stat);
        inOrderFsCheck.verify(fsCheck).check(item5a.stat);
        inOrderFsCheck.verify(fsCheck).check(item5b.stat);
        inOrderFsCheck.verify(fsCheck).check(item5c.stat);
        inOrderFsCheck.verify(fsCheck).check(item5ca.stat);
        inOrderFsCheck.verify(fsCheck).check(item5d.stat);
        inOrderFsCheck.verify(fsCheck).check(item5e.stat);
        Mockito.verifyNoMoreInteractions(fsCheck);
        Mockito.verifyNoMoreInteractions(out);
        Mockito.verifyNoMoreInteractions(err);
    }

    // check symlinks given as path arguments are processed correctly with the
    // follow option
    @Test
    public void processArgumentsOptionFollow() throws IOException {
        LinkedList<PathData> items = createDirectories();
        Find find = new Find();
        find.getOptions().setFollowLink(true);
        find.setConf(TestFind.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        find.getOptions().setOut(out);
        PrintStream err = Mockito.mock(PrintStream.class);
        find.getOptions().setErr(err);
        Expression expr = Mockito.mock(Expression.class);
        Mockito.when(expr.apply(((PathData) (ArgumentMatchers.any())), ArgumentMatchers.anyInt())).thenReturn(PASS);
        TestFind.FileStatusChecker fsCheck = Mockito.mock(TestFind.FileStatusChecker.class);
        Expression test = new TestFind.TestExpression(expr, fsCheck);
        find.setRootExpression(test);
        find.processArguments(items);
        InOrder inOrder = Mockito.inOrder(expr);
        inOrder.verify(expr).setOptions(find.getOptions());
        inOrder.verify(expr).prepare();
        inOrder.verify(expr).apply(item1, 0);
        inOrder.verify(expr).apply(item1a, 1);
        inOrder.verify(expr).apply(item1aa, 2);
        inOrder.verify(expr).apply(item1b, 1);
        inOrder.verify(expr).apply(item2, 0);
        inOrder.verify(expr).apply(item3, 0);
        inOrder.verify(expr).apply(item4, 0);
        inOrder.verify(expr).apply(item5, 0);
        inOrder.verify(expr).apply(item5a, 1);
        inOrder.verify(expr).apply(item5b, 1);// triggers infinite loop message

        inOrder.verify(expr).apply(item5c, 1);
        inOrder.verify(expr).apply(item5ca, 2);
        inOrder.verify(expr).apply(item5d, 1);
        inOrder.verify(expr).apply(item5ca, 2);// following item5d symlink

        inOrder.verify(expr).apply(item5e, 1);
        inOrder.verify(expr).finish();
        Mockito.verifyNoMoreInteractions(expr);
        InOrder inOrderFsCheck = Mockito.inOrder(fsCheck);
        inOrderFsCheck.verify(fsCheck).check(item1.stat);
        inOrderFsCheck.verify(fsCheck).check(item1a.stat);
        inOrderFsCheck.verify(fsCheck).check(item1aa.stat);
        inOrderFsCheck.verify(fsCheck).check(item1b.stat);
        inOrderFsCheck.verify(fsCheck).check(item2.stat);
        inOrderFsCheck.verify(fsCheck, Mockito.times(2)).check(item3.stat);
        inOrderFsCheck.verify(fsCheck).check(item5.stat);
        inOrderFsCheck.verify(fsCheck).check(item1b.stat);
        inOrderFsCheck.verify(fsCheck).check(item5.stat);
        inOrderFsCheck.verify(fsCheck).check(item5c.stat);
        inOrderFsCheck.verify(fsCheck).check(item5ca.stat);
        inOrderFsCheck.verify(fsCheck).check(item5c.stat);
        inOrderFsCheck.verify(fsCheck, Mockito.times(2)).check(item5ca.stat);
        Mockito.verifyNoMoreInteractions(fsCheck);
        Mockito.verifyNoMoreInteractions(out);
        Mockito.verify(err).println(((("Infinite loop ignored: " + (item5b.toString())) + " -> ") + (item5.toString())));
        Mockito.verifyNoMoreInteractions(err);
    }

    // check minimum depth is handledfollowLink
    @Test
    public void processArgumentsMinDepth() throws IOException {
        LinkedList<PathData> items = createDirectories();
        Find find = new Find();
        find.getOptions().setMinDepth(1);
        find.setConf(TestFind.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        find.getOptions().setOut(out);
        PrintStream err = Mockito.mock(PrintStream.class);
        find.getOptions().setErr(err);
        Expression expr = Mockito.mock(Expression.class);
        Mockito.when(expr.apply(((PathData) (ArgumentMatchers.any())), ArgumentMatchers.anyInt())).thenReturn(PASS);
        TestFind.FileStatusChecker fsCheck = Mockito.mock(TestFind.FileStatusChecker.class);
        Expression test = new TestFind.TestExpression(expr, fsCheck);
        find.setRootExpression(test);
        find.processArguments(items);
        InOrder inOrder = Mockito.inOrder(expr);
        inOrder.verify(expr).setOptions(find.getOptions());
        inOrder.verify(expr).prepare();
        inOrder.verify(expr).apply(item1a, 1);
        inOrder.verify(expr).apply(item1aa, 2);
        inOrder.verify(expr).apply(item1b, 1);
        inOrder.verify(expr).apply(item5a, 1);
        inOrder.verify(expr).apply(item5b, 1);
        inOrder.verify(expr).apply(item5c, 1);
        inOrder.verify(expr).apply(item5ca, 2);
        inOrder.verify(expr).apply(item5d, 1);
        inOrder.verify(expr).apply(item5e, 1);
        inOrder.verify(expr).finish();
        Mockito.verifyNoMoreInteractions(expr);
        InOrder inOrderFsCheck = Mockito.inOrder(fsCheck);
        inOrderFsCheck.verify(fsCheck).check(item1a.stat);
        inOrderFsCheck.verify(fsCheck).check(item1aa.stat);
        inOrderFsCheck.verify(fsCheck).check(item1b.stat);
        inOrderFsCheck.verify(fsCheck).check(item5a.stat);
        inOrderFsCheck.verify(fsCheck).check(item5b.stat);
        inOrderFsCheck.verify(fsCheck).check(item5c.stat);
        inOrderFsCheck.verify(fsCheck).check(item5ca.stat);
        inOrderFsCheck.verify(fsCheck).check(item5d.stat);
        inOrderFsCheck.verify(fsCheck).check(item5e.stat);
        Mockito.verifyNoMoreInteractions(fsCheck);
        Mockito.verifyNoMoreInteractions(out);
        Mockito.verifyNoMoreInteractions(err);
    }

    // check maximum depth is handled
    @Test
    public void processArgumentsMaxDepth() throws IOException {
        LinkedList<PathData> items = createDirectories();
        Find find = new Find();
        find.getOptions().setMaxDepth(1);
        find.setConf(TestFind.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        find.getOptions().setOut(out);
        PrintStream err = Mockito.mock(PrintStream.class);
        find.getOptions().setErr(err);
        Expression expr = Mockito.mock(Expression.class);
        Mockito.when(expr.apply(((PathData) (ArgumentMatchers.any())), ArgumentMatchers.anyInt())).thenReturn(PASS);
        TestFind.FileStatusChecker fsCheck = Mockito.mock(TestFind.FileStatusChecker.class);
        Expression test = new TestFind.TestExpression(expr, fsCheck);
        find.setRootExpression(test);
        find.processArguments(items);
        InOrder inOrder = Mockito.inOrder(expr);
        inOrder.verify(expr).setOptions(find.getOptions());
        inOrder.verify(expr).prepare();
        inOrder.verify(expr).apply(item1, 0);
        inOrder.verify(expr).apply(item1a, 1);
        inOrder.verify(expr).apply(item1b, 1);
        inOrder.verify(expr).apply(item2, 0);
        inOrder.verify(expr).apply(item3, 0);
        inOrder.verify(expr).apply(item4, 0);
        inOrder.verify(expr).apply(item5, 0);
        inOrder.verify(expr).apply(item5a, 1);
        inOrder.verify(expr).apply(item5b, 1);
        inOrder.verify(expr).apply(item5c, 1);
        inOrder.verify(expr).apply(item5d, 1);
        inOrder.verify(expr).apply(item5e, 1);
        inOrder.verify(expr).finish();
        Mockito.verifyNoMoreInteractions(expr);
        InOrder inOrderFsCheck = Mockito.inOrder(fsCheck);
        inOrderFsCheck.verify(fsCheck).check(item1.stat);
        inOrderFsCheck.verify(fsCheck).check(item1a.stat);
        inOrderFsCheck.verify(fsCheck).check(item1b.stat);
        inOrderFsCheck.verify(fsCheck).check(item2.stat);
        inOrderFsCheck.verify(fsCheck).check(item3.stat);
        inOrderFsCheck.verify(fsCheck).check(item4.stat);
        inOrderFsCheck.verify(fsCheck).check(item5.stat);
        inOrderFsCheck.verify(fsCheck).check(item5a.stat);
        inOrderFsCheck.verify(fsCheck).check(item5b.stat);
        inOrderFsCheck.verify(fsCheck).check(item5c.stat);
        inOrderFsCheck.verify(fsCheck).check(item5d.stat);
        inOrderFsCheck.verify(fsCheck).check(item5e.stat);
        Mockito.verifyNoMoreInteractions(fsCheck);
        Mockito.verifyNoMoreInteractions(out);
        Mockito.verifyNoMoreInteractions(err);
    }

    // check min depth is handled when -depth is specified
    @Test
    public void processArgumentsDepthFirstMinDepth() throws IOException {
        LinkedList<PathData> items = createDirectories();
        Find find = new Find();
        find.getOptions().setDepthFirst(true);
        find.getOptions().setMinDepth(1);
        find.setConf(TestFind.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        find.getOptions().setOut(out);
        PrintStream err = Mockito.mock(PrintStream.class);
        find.getOptions().setErr(err);
        Expression expr = Mockito.mock(Expression.class);
        Mockito.when(expr.apply(((PathData) (ArgumentMatchers.any())), ArgumentMatchers.anyInt())).thenReturn(PASS);
        TestFind.FileStatusChecker fsCheck = Mockito.mock(TestFind.FileStatusChecker.class);
        Expression test = new TestFind.TestExpression(expr, fsCheck);
        find.setRootExpression(test);
        find.processArguments(items);
        InOrder inOrder = Mockito.inOrder(expr);
        inOrder.verify(expr).setOptions(find.getOptions());
        inOrder.verify(expr).prepare();
        inOrder.verify(expr).apply(item1aa, 2);
        inOrder.verify(expr).apply(item1a, 1);
        inOrder.verify(expr).apply(item1b, 1);
        inOrder.verify(expr).apply(item5a, 1);
        inOrder.verify(expr).apply(item5b, 1);
        inOrder.verify(expr).apply(item5ca, 2);
        inOrder.verify(expr).apply(item5c, 1);
        inOrder.verify(expr).apply(item5d, 1);
        inOrder.verify(expr).apply(item5e, 1);
        inOrder.verify(expr).finish();
        Mockito.verifyNoMoreInteractions(expr);
        InOrder inOrderFsCheck = Mockito.inOrder(fsCheck);
        inOrderFsCheck.verify(fsCheck).check(item1aa.stat);
        inOrderFsCheck.verify(fsCheck).check(item1a.stat);
        inOrderFsCheck.verify(fsCheck).check(item1b.stat);
        inOrderFsCheck.verify(fsCheck).check(item5a.stat);
        inOrderFsCheck.verify(fsCheck).check(item5b.stat);
        inOrderFsCheck.verify(fsCheck).check(item5ca.stat);
        inOrderFsCheck.verify(fsCheck).check(item5c.stat);
        inOrderFsCheck.verify(fsCheck).check(item5d.stat);
        inOrderFsCheck.verify(fsCheck).check(item5e.stat);
        Mockito.verifyNoMoreInteractions(fsCheck);
        Mockito.verifyNoMoreInteractions(out);
        Mockito.verifyNoMoreInteractions(err);
    }

    // check max depth is handled when -depth is specified
    @Test
    public void processArgumentsDepthFirstMaxDepth() throws IOException {
        LinkedList<PathData> items = createDirectories();
        Find find = new Find();
        find.getOptions().setDepthFirst(true);
        find.getOptions().setMaxDepth(1);
        find.setConf(TestFind.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        find.getOptions().setOut(out);
        PrintStream err = Mockito.mock(PrintStream.class);
        find.getOptions().setErr(err);
        Expression expr = Mockito.mock(Expression.class);
        Mockito.when(expr.apply(((PathData) (ArgumentMatchers.any())), ArgumentMatchers.anyInt())).thenReturn(PASS);
        TestFind.FileStatusChecker fsCheck = Mockito.mock(TestFind.FileStatusChecker.class);
        Expression test = new TestFind.TestExpression(expr, fsCheck);
        find.setRootExpression(test);
        find.processArguments(items);
        InOrder inOrder = Mockito.inOrder(expr);
        inOrder.verify(expr).setOptions(find.getOptions());
        inOrder.verify(expr).prepare();
        inOrder.verify(expr).apply(item1a, 1);
        inOrder.verify(expr).apply(item1b, 1);
        inOrder.verify(expr).apply(item1, 0);
        inOrder.verify(expr).apply(item2, 0);
        inOrder.verify(expr).apply(item3, 0);
        inOrder.verify(expr).apply(item4, 0);
        inOrder.verify(expr).apply(item5a, 1);
        inOrder.verify(expr).apply(item5b, 1);
        inOrder.verify(expr).apply(item5c, 1);
        inOrder.verify(expr).apply(item5d, 1);
        inOrder.verify(expr).apply(item5e, 1);
        inOrder.verify(expr).apply(item5, 0);
        inOrder.verify(expr).finish();
        Mockito.verifyNoMoreInteractions(expr);
        InOrder inOrderFsCheck = Mockito.inOrder(fsCheck);
        inOrderFsCheck.verify(fsCheck).check(item1a.stat);
        inOrderFsCheck.verify(fsCheck).check(item1b.stat);
        inOrderFsCheck.verify(fsCheck).check(item1.stat);
        inOrderFsCheck.verify(fsCheck).check(item2.stat);
        inOrderFsCheck.verify(fsCheck).check(item3.stat);
        inOrderFsCheck.verify(fsCheck).check(item4.stat);
        inOrderFsCheck.verify(fsCheck).check(item5a.stat);
        inOrderFsCheck.verify(fsCheck).check(item5b.stat);
        inOrderFsCheck.verify(fsCheck).check(item5c.stat);
        inOrderFsCheck.verify(fsCheck).check(item5d.stat);
        inOrderFsCheck.verify(fsCheck).check(item5e.stat);
        inOrderFsCheck.verify(fsCheck).check(item5.stat);
        Mockito.verifyNoMoreInteractions(fsCheck);
        Mockito.verifyNoMoreInteractions(out);
        Mockito.verifyNoMoreInteractions(err);
    }

    // check expressions are called in the correct order
    @Test
    public void processArgumentsNoDescend() throws IOException {
        LinkedList<PathData> items = createDirectories();
        Find find = new Find();
        find.setConf(TestFind.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        find.getOptions().setOut(out);
        PrintStream err = Mockito.mock(PrintStream.class);
        find.getOptions().setErr(err);
        Expression expr = Mockito.mock(Expression.class);
        Mockito.when(expr.apply(((PathData) (ArgumentMatchers.any())), ArgumentMatchers.anyInt())).thenReturn(PASS);
        Mockito.when(expr.apply(ArgumentMatchers.eq(item1a), ArgumentMatchers.anyInt())).thenReturn(STOP);
        TestFind.FileStatusChecker fsCheck = Mockito.mock(TestFind.FileStatusChecker.class);
        Expression test = new TestFind.TestExpression(expr, fsCheck);
        find.setRootExpression(test);
        find.processArguments(items);
        InOrder inOrder = Mockito.inOrder(expr);
        inOrder.verify(expr).setOptions(find.getOptions());
        inOrder.verify(expr).prepare();
        inOrder.verify(expr).apply(item1, 0);
        inOrder.verify(expr).apply(item1a, 1);
        inOrder.verify(expr).apply(item1b, 1);
        inOrder.verify(expr).apply(item2, 0);
        inOrder.verify(expr).apply(item3, 0);
        inOrder.verify(expr).apply(item4, 0);
        inOrder.verify(expr).apply(item5, 0);
        inOrder.verify(expr).apply(item5a, 1);
        inOrder.verify(expr).apply(item5b, 1);
        inOrder.verify(expr).apply(item5c, 1);
        inOrder.verify(expr).apply(item5ca, 2);
        inOrder.verify(expr).apply(item5d, 1);
        inOrder.verify(expr).apply(item5e, 1);
        inOrder.verify(expr).finish();
        Mockito.verifyNoMoreInteractions(expr);
        InOrder inOrderFsCheck = Mockito.inOrder(fsCheck);
        inOrderFsCheck.verify(fsCheck).check(item1.stat);
        inOrderFsCheck.verify(fsCheck).check(item1a.stat);
        inOrderFsCheck.verify(fsCheck).check(item1b.stat);
        inOrderFsCheck.verify(fsCheck).check(item2.stat);
        inOrderFsCheck.verify(fsCheck).check(item3.stat);
        inOrderFsCheck.verify(fsCheck).check(item4.stat);
        inOrderFsCheck.verify(fsCheck).check(item5.stat);
        inOrderFsCheck.verify(fsCheck).check(item5a.stat);
        inOrderFsCheck.verify(fsCheck).check(item5b.stat);
        inOrderFsCheck.verify(fsCheck).check(item5c.stat);
        inOrderFsCheck.verify(fsCheck).check(item5ca.stat);
        inOrderFsCheck.verify(fsCheck).check(item5d.stat);
        inOrderFsCheck.verify(fsCheck).check(item5e.stat);
        Mockito.verifyNoMoreInteractions(fsCheck);
        Mockito.verifyNoMoreInteractions(out);
        Mockito.verifyNoMoreInteractions(err);
    }

    private interface FileStatusChecker {
        public void check(FileStatus fileStatus);
    }

    private class TestExpression extends BaseExpression implements Expression {
        private Expression expr;

        private TestFind.FileStatusChecker checker;

        public TestExpression(Expression expr, TestFind.FileStatusChecker checker) {
            this.expr = expr;
            this.checker = checker;
        }

        @Override
        public Result apply(PathData item, int depth) throws IOException {
            FileStatus fileStatus = getFileStatus(item, depth);
            checker.check(fileStatus);
            return expr.apply(item, depth);
        }

        @Override
        public void setOptions(FindOptions options) throws IOException {
            super.setOptions(options);
            expr.setOptions(options);
        }

        @Override
        public void prepare() throws IOException {
            expr.prepare();
        }

        @Override
        public void finish() throws IOException {
            expr.finish();
        }
    }

    // creates a directory structure for traversal
    // item1 (directory)
    // \- item1a (directory)
    // \- item1aa (file)
    // \- item1b (file)
    // item2 (directory)
    // item3 (file)
    // item4 (link) -> item3
    // item5 (directory)
    // \- item5a (link) -> item1b
    // \- item5b (link) -> item5 (infinite loop)
    // \- item5c (directory)
    // \- item5ca (file)
    // \- item5d (link) -> item5c
    // \- item5e (link) -> item5c/item5ca
    private PathData item1 = null;

    private PathData item1a = null;

    private PathData item1aa = null;

    private PathData item1b = null;

    private PathData item2 = null;

    private PathData item3 = null;

    private PathData item4 = null;

    private PathData item5 = null;

    private PathData item5a = null;

    private PathData item5b = null;

    private PathData item5c = null;

    private PathData item5ca = null;

    private PathData item5d = null;

    private PathData item5e = null;
}

