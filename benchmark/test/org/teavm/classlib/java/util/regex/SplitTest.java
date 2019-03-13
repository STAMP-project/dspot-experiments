/**
 * Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util.regex;


import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class SplitTest {
    @Test
    public void testSimple() {
        Pattern p = Pattern.compile("/");
        String[] results = p.split("have/you/done/it/right");
        String[] expected = new String[]{ "have", "you", "done", "it", "right" };
        Assert.assertEquals(expected.length, results.length);
        for (int i = 0; i < (expected.length); i++) {
            Assert.assertEquals(results[i], expected[i]);
        }
    }

    @Test
    public void testSplit1() throws PatternSyntaxException {
        Pattern p = Pattern.compile(" ");
        String input = "poodle zoo";
        String[] tokens;
        tokens = p.split(input, 1);
        Assert.assertEquals(1, tokens.length);
        Assert.assertTrue(tokens[0].equals(input));
        tokens = p.split(input, 2);
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals("poodle", tokens[0]);
        Assert.assertEquals("zoo", tokens[1]);
        tokens = p.split(input, 5);
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals("poodle", tokens[0]);
        Assert.assertEquals("zoo", tokens[1]);
        tokens = p.split(input, (-2));
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals("poodle", tokens[0]);
        Assert.assertEquals("zoo", tokens[1]);
        tokens = p.split(input, 0);
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals("poodle", tokens[0]);
        Assert.assertEquals("zoo", tokens[1]);
        tokens = p.split(input);
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals("poodle", tokens[0]);
        Assert.assertEquals("zoo", tokens[1]);
        p = Pattern.compile("d");
        tokens = p.split(input, 1);
        Assert.assertEquals(1, tokens.length);
        Assert.assertTrue(tokens[0].equals(input));
        tokens = p.split(input, 2);
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals("poo", tokens[0]);
        Assert.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input, 5);
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals("poo", tokens[0]);
        Assert.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input, (-2));
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals("poo", tokens[0]);
        Assert.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input, 0);
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals("poo", tokens[0]);
        Assert.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input);
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals("poo", tokens[0]);
        Assert.assertEquals("le zoo", tokens[1]);
        p = Pattern.compile("o");
        tokens = p.split(input, 1);
        Assert.assertEquals(1, tokens.length);
        Assert.assertTrue(tokens[0].equals(input));
        tokens = p.split(input, 2);
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals("p", tokens[0]);
        Assert.assertEquals("odle zoo", tokens[1]);
        tokens = p.split(input, 5);
        Assert.assertEquals(5, tokens.length);
        Assert.assertEquals("p", tokens[0]);
        Assert.assertTrue(tokens[1].equals(""));
        Assert.assertEquals("dle z", tokens[2]);
        Assert.assertTrue(tokens[3].equals(""));
        Assert.assertTrue(tokens[4].equals(""));
        tokens = p.split(input, (-2));
        Assert.assertEquals(5, tokens.length);
        Assert.assertEquals("p", tokens[0]);
        Assert.assertTrue(tokens[1].equals(""));
        Assert.assertEquals("dle z", tokens[2]);
        Assert.assertTrue(tokens[3].equals(""));
        Assert.assertTrue(tokens[4].equals(""));
        tokens = p.split(input, 0);
        Assert.assertEquals(3, tokens.length);
        Assert.assertEquals("p", tokens[0]);
        Assert.assertTrue(tokens[1].equals(""));
        Assert.assertEquals("dle z", tokens[2]);
        tokens = p.split(input);
        Assert.assertEquals(3, tokens.length);
        Assert.assertEquals("p", tokens[0]);
        Assert.assertTrue(tokens[1].equals(""));
        Assert.assertEquals("dle z", tokens[2]);
    }
}

