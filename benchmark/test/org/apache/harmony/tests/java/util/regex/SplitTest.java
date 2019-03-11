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
package org.apache.harmony.tests.java.util.regex;


import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import junit.framework.TestCase;


/**
 * TODO Type description
 */
@SuppressWarnings("nls")
public class SplitTest extends TestCase {
    public void testSimple() {
        Pattern p = Pattern.compile("/");
        String[] results = p.split("have/you/done/it/right");
        String[] expected = new String[]{ "have", "you", "done", "it", "right" };
        TestCase.assertEquals(expected.length, results.length);
        for (int i = 0; i < (expected.length); i++) {
            TestCase.assertEquals(results[i], expected[i]);
        }
    }

    public void testSplit1() throws PatternSyntaxException {
        Pattern p = Pattern.compile(" ");
        String input = "poodle zoo";
        String[] tokens;
        tokens = p.split(input, 1);
        TestCase.assertEquals(1, tokens.length);
        TestCase.assertTrue(tokens[0].equals(input));
        tokens = p.split(input, 2);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poodle", tokens[0]);
        TestCase.assertEquals("zoo", tokens[1]);
        tokens = p.split(input, 5);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poodle", tokens[0]);
        TestCase.assertEquals("zoo", tokens[1]);
        tokens = p.split(input, (-2));
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poodle", tokens[0]);
        TestCase.assertEquals("zoo", tokens[1]);
        tokens = p.split(input, 0);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poodle", tokens[0]);
        TestCase.assertEquals("zoo", tokens[1]);
        tokens = p.split(input);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poodle", tokens[0]);
        TestCase.assertEquals("zoo", tokens[1]);
        p = Pattern.compile("d");
        tokens = p.split(input, 1);
        TestCase.assertEquals(1, tokens.length);
        TestCase.assertTrue(tokens[0].equals(input));
        tokens = p.split(input, 2);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poo", tokens[0]);
        TestCase.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input, 5);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poo", tokens[0]);
        TestCase.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input, (-2));
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poo", tokens[0]);
        TestCase.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input, 0);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poo", tokens[0]);
        TestCase.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poo", tokens[0]);
        TestCase.assertEquals("le zoo", tokens[1]);
        p = Pattern.compile("o");
        tokens = p.split(input, 1);
        TestCase.assertEquals(1, tokens.length);
        TestCase.assertTrue(tokens[0].equals(input));
        tokens = p.split(input, 2);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("p", tokens[0]);
        TestCase.assertEquals("odle zoo", tokens[1]);
        tokens = p.split(input, 5);
        TestCase.assertEquals(5, tokens.length);
        TestCase.assertEquals("p", tokens[0]);
        TestCase.assertTrue(tokens[1].equals(""));
        TestCase.assertEquals("dle z", tokens[2]);
        TestCase.assertTrue(tokens[3].equals(""));
        TestCase.assertTrue(tokens[4].equals(""));
        tokens = p.split(input, (-2));
        TestCase.assertEquals(5, tokens.length);
        TestCase.assertEquals("p", tokens[0]);
        TestCase.assertTrue(tokens[1].equals(""));
        TestCase.assertEquals("dle z", tokens[2]);
        TestCase.assertTrue(tokens[3].equals(""));
        TestCase.assertTrue(tokens[4].equals(""));
        tokens = p.split(input, 0);
        TestCase.assertEquals(3, tokens.length);
        TestCase.assertEquals("p", tokens[0]);
        TestCase.assertTrue(tokens[1].equals(""));
        TestCase.assertEquals("dle z", tokens[2]);
        tokens = p.split(input);
        TestCase.assertEquals(3, tokens.length);
        TestCase.assertEquals("p", tokens[0]);
        TestCase.assertTrue(tokens[1].equals(""));
        TestCase.assertEquals("dle z", tokens[2]);
    }

    public void testSplit2() {
        Pattern p = Pattern.compile("");
        String[] s;
        s = p.split("a", (-1));
        TestCase.assertEquals(3, s.length);
        TestCase.assertEquals("", s[0]);
        TestCase.assertEquals("a", s[1]);
        TestCase.assertEquals("", s[2]);
        s = p.split("", (-1));
        TestCase.assertEquals(1, s.length);
        TestCase.assertEquals("", s[0]);
        s = p.split("abcd", (-1));
        TestCase.assertEquals(6, s.length);
        TestCase.assertEquals("", s[0]);
        TestCase.assertEquals("a", s[1]);
        TestCase.assertEquals("b", s[2]);
        TestCase.assertEquals("c", s[3]);
        TestCase.assertEquals("d", s[4]);
        TestCase.assertEquals("", s[5]);
    }

    public void testSplitSupplementaryWithEmptyString() {
        /* See http://www.unicode.org/reports/tr18/#Supplementary_Characters We
        have to treat text as code points not code units.
         */
        Pattern p = Pattern.compile("");
        String[] s;
        s = p.split("a\ud869\uded6b", (-1));
        TestCase.assertEquals(5, s.length);
        TestCase.assertEquals("", s[0]);
        TestCase.assertEquals("a", s[1]);
        TestCase.assertEquals("\ud869\uded6", s[2]);
        TestCase.assertEquals("b", s[3]);
        TestCase.assertEquals("", s[4]);
    }
}

