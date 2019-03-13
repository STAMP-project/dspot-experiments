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
import java.io.IOException;
import java.util.Deque;
import org.apache.hadoop.fs.shell.PathData;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.Mockito;


public class TestFilterExpression {
    private Expression expr;

    private FilterExpression test;

    @Rule
    public Timeout globalTimeout = new Timeout(10000);

    // test that the child expression is correctly set
    @Test
    public void expression() throws IOException {
        Assert.assertEquals(expr, test.expression);
    }

    // test that setOptions method is called
    @Test
    public void setOptions() throws IOException {
        FindOptions options = Mockito.mock(FindOptions.class);
        test.setOptions(options);
        Mockito.verify(expr).setOptions(options);
        Mockito.verifyNoMoreInteractions(expr);
    }

    // test the apply method is called and the result returned
    @Test
    public void apply() throws IOException {
        PathData item = Mockito.mock(PathData.class);
        Mockito.when(expr.apply(item, (-1))).thenReturn(PASS).thenReturn(FAIL);
        Assert.assertEquals(PASS, test.apply(item, (-1)));
        Assert.assertEquals(FAIL, test.apply(item, (-1)));
        Mockito.verify(expr, Mockito.times(2)).apply(item, (-1));
        Mockito.verifyNoMoreInteractions(expr);
    }

    // test that the finish method is called
    @Test
    public void finish() throws IOException {
        test.finish();
        Mockito.verify(expr).finish();
        Mockito.verifyNoMoreInteractions(expr);
    }

    // test that the getUsage method is called
    @Test
    public void getUsage() {
        String[] usage = new String[]{ "Usage 1", "Usage 2", "Usage 3" };
        Mockito.when(expr.getUsage()).thenReturn(usage);
        Assert.assertArrayEquals(usage, test.getUsage());
        Mockito.verify(expr).getUsage();
        Mockito.verifyNoMoreInteractions(expr);
    }

    // test that the getHelp method is called
    @Test
    public void getHelp() {
        String[] help = new String[]{ "Help 1", "Help 2", "Help 3" };
        Mockito.when(expr.getHelp()).thenReturn(help);
        Assert.assertArrayEquals(help, test.getHelp());
        Mockito.verify(expr).getHelp();
        Mockito.verifyNoMoreInteractions(expr);
    }

    // test that the isAction method is called
    @Test
    public void isAction() {
        Mockito.when(expr.isAction()).thenReturn(true).thenReturn(false);
        Assert.assertTrue(test.isAction());
        Assert.assertFalse(test.isAction());
        Mockito.verify(expr, Mockito.times(2)).isAction();
        Mockito.verifyNoMoreInteractions(expr);
    }

    // test that the isOperator method is called
    @Test
    public void isOperator() {
        Mockito.when(expr.isAction()).thenReturn(true).thenReturn(false);
        Assert.assertTrue(test.isAction());
        Assert.assertFalse(test.isAction());
        Mockito.verify(expr, Mockito.times(2)).isAction();
        Mockito.verifyNoMoreInteractions(expr);
    }

    // test that the getPrecedence method is called
    @Test
    public void getPrecedence() {
        int precedence = 12345;
        Mockito.when(expr.getPrecedence()).thenReturn(precedence);
        Assert.assertEquals(precedence, test.getPrecedence());
        Mockito.verify(expr).getPrecedence();
        Mockito.verifyNoMoreInteractions(expr);
    }

    // test that the addChildren method is called
    @Test
    public void addChildren() {
        @SuppressWarnings("unchecked")
        Deque<Expression> expressions = Mockito.mock(Deque.class);
        test.addChildren(expressions);
        Mockito.verify(expr).addChildren(expressions);
        Mockito.verifyNoMoreInteractions(expr);
    }

    // test that the addArguments method is called
    @Test
    public void addArguments() {
        @SuppressWarnings("unchecked")
        Deque<String> args = Mockito.mock(Deque.class);
        test.addArguments(args);
        Mockito.verify(expr).addArguments(args);
        Mockito.verifyNoMoreInteractions(expr);
    }
}

