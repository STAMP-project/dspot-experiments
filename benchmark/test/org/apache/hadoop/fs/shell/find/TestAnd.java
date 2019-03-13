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


import Result.FAIL;
import Result.PASS;
import Result.STOP;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import org.apache.hadoop.fs.shell.PathData;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.Mockito;


public class TestAnd {
    @Rule
    public Timeout globalTimeout = new Timeout(10000);

    // test all expressions passing
    @Test
    public void testPass() throws IOException {
        And and = new And();
        PathData pathData = Mockito.mock(PathData.class);
        Expression first = Mockito.mock(Expression.class);
        Mockito.when(first.apply(pathData, (-1))).thenReturn(PASS);
        Expression second = Mockito.mock(Expression.class);
        Mockito.when(second.apply(pathData, (-1))).thenReturn(PASS);
        Deque<Expression> children = new LinkedList<Expression>();
        children.add(second);
        children.add(first);
        and.addChildren(children);
        Assert.assertEquals(PASS, and.apply(pathData, (-1)));
        Mockito.verify(first).apply(pathData, (-1));
        Mockito.verify(second).apply(pathData, (-1));
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }

    // test the first expression failing
    @Test
    public void testFailFirst() throws IOException {
        And and = new And();
        PathData pathData = Mockito.mock(PathData.class);
        Expression first = Mockito.mock(Expression.class);
        Mockito.when(first.apply(pathData, (-1))).thenReturn(FAIL);
        Expression second = Mockito.mock(Expression.class);
        Mockito.when(second.apply(pathData, (-1))).thenReturn(PASS);
        Deque<Expression> children = new LinkedList<Expression>();
        children.add(second);
        children.add(first);
        and.addChildren(children);
        Assert.assertEquals(FAIL, and.apply(pathData, (-1)));
        Mockito.verify(first).apply(pathData, (-1));
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }

    // test the second expression failing
    @Test
    public void testFailSecond() throws IOException {
        And and = new And();
        PathData pathData = Mockito.mock(PathData.class);
        Expression first = Mockito.mock(Expression.class);
        Mockito.when(first.apply(pathData, (-1))).thenReturn(PASS);
        Expression second = Mockito.mock(Expression.class);
        Mockito.when(second.apply(pathData, (-1))).thenReturn(FAIL);
        Deque<Expression> children = new LinkedList<Expression>();
        children.add(second);
        children.add(first);
        and.addChildren(children);
        Assert.assertEquals(FAIL, and.apply(pathData, (-1)));
        Mockito.verify(first).apply(pathData, (-1));
        Mockito.verify(second).apply(pathData, (-1));
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }

    // test both expressions failing
    @Test
    public void testFailBoth() throws IOException {
        And and = new And();
        PathData pathData = Mockito.mock(PathData.class);
        Expression first = Mockito.mock(Expression.class);
        Mockito.when(first.apply(pathData, (-1))).thenReturn(FAIL);
        Expression second = Mockito.mock(Expression.class);
        Mockito.when(second.apply(pathData, (-1))).thenReturn(FAIL);
        Deque<Expression> children = new LinkedList<Expression>();
        children.add(second);
        children.add(first);
        and.addChildren(children);
        Assert.assertEquals(FAIL, and.apply(pathData, (-1)));
        Mockito.verify(first).apply(pathData, (-1));
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }

    // test the first expression stopping
    @Test
    public void testStopFirst() throws IOException {
        And and = new And();
        PathData pathData = Mockito.mock(PathData.class);
        Expression first = Mockito.mock(Expression.class);
        Mockito.when(first.apply(pathData, (-1))).thenReturn(STOP);
        Expression second = Mockito.mock(Expression.class);
        Mockito.when(second.apply(pathData, (-1))).thenReturn(PASS);
        Deque<Expression> children = new LinkedList<Expression>();
        children.add(second);
        children.add(first);
        and.addChildren(children);
        Assert.assertEquals(STOP, and.apply(pathData, (-1)));
        Mockito.verify(first).apply(pathData, (-1));
        Mockito.verify(second).apply(pathData, (-1));
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }

    // test the second expression stopping
    @Test
    public void testStopSecond() throws IOException {
        And and = new And();
        PathData pathData = Mockito.mock(PathData.class);
        Expression first = Mockito.mock(Expression.class);
        Mockito.when(first.apply(pathData, (-1))).thenReturn(PASS);
        Expression second = Mockito.mock(Expression.class);
        Mockito.when(second.apply(pathData, (-1))).thenReturn(STOP);
        Deque<Expression> children = new LinkedList<Expression>();
        children.add(second);
        children.add(first);
        and.addChildren(children);
        Assert.assertEquals(STOP, and.apply(pathData, (-1)));
        Mockito.verify(first).apply(pathData, (-1));
        Mockito.verify(second).apply(pathData, (-1));
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }

    // test first expression stopping and second failing
    @Test
    public void testStopFail() throws IOException {
        And and = new And();
        PathData pathData = Mockito.mock(PathData.class);
        Expression first = Mockito.mock(Expression.class);
        Mockito.when(first.apply(pathData, (-1))).thenReturn(STOP);
        Expression second = Mockito.mock(Expression.class);
        Mockito.when(second.apply(pathData, (-1))).thenReturn(FAIL);
        Deque<Expression> children = new LinkedList<Expression>();
        children.add(second);
        children.add(first);
        and.addChildren(children);
        Assert.assertEquals(STOP.combine(FAIL), and.apply(pathData, (-1)));
        Mockito.verify(first).apply(pathData, (-1));
        Mockito.verify(second).apply(pathData, (-1));
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }

    // test setOptions is called on child
    @Test
    public void testSetOptions() throws IOException {
        And and = new And();
        Expression first = Mockito.mock(Expression.class);
        Expression second = Mockito.mock(Expression.class);
        Deque<Expression> children = new LinkedList<Expression>();
        children.add(second);
        children.add(first);
        and.addChildren(children);
        FindOptions options = Mockito.mock(FindOptions.class);
        and.setOptions(options);
        Mockito.verify(first).setOptions(options);
        Mockito.verify(second).setOptions(options);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }

    // test prepare is called on child
    @Test
    public void testPrepare() throws IOException {
        And and = new And();
        Expression first = Mockito.mock(Expression.class);
        Expression second = Mockito.mock(Expression.class);
        Deque<Expression> children = new LinkedList<Expression>();
        children.add(second);
        children.add(first);
        and.addChildren(children);
        and.prepare();
        Mockito.verify(first).prepare();
        Mockito.verify(second).prepare();
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }

    // test finish is called on child
    @Test
    public void testFinish() throws IOException {
        And and = new And();
        Expression first = Mockito.mock(Expression.class);
        Expression second = Mockito.mock(Expression.class);
        Deque<Expression> children = new LinkedList<Expression>();
        children.add(second);
        children.add(first);
        and.addChildren(children);
        and.finish();
        Mockito.verify(first).finish();
        Mockito.verify(second).finish();
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }
}

