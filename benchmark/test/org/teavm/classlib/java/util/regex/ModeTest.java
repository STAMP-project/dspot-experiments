/**
 * Copyright 2014 Alexey Andreev.
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
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.teavm.classlib.java.util.regex;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


/**
 * Tests Pattern compilation modes and modes triggered in pattern strings
 */
@SuppressWarnings("nls")
@RunWith(TeaVMTestRunner.class)
public class ModeTest {
    @Test
    public void testCase() throws PatternSyntaxException {
        Pattern p;
        Matcher m;
        p = Pattern.compile("([a-z]+)[0-9]+");
        m = p.matcher("cAT123#dog345");
        Assert.assertTrue(m.find());
        Assert.assertEquals("dog", m.group(1));
        Assert.assertFalse(m.find());
        p = Pattern.compile("([a-z]+)[0-9]+", Pattern.CASE_INSENSITIVE);
        m = p.matcher("cAt123#doG345");
        Assert.assertTrue(m.find());
        Assert.assertEquals("cAt", m.group(1));
        Assert.assertTrue(m.find());
        Assert.assertEquals("doG", m.group(1));
        Assert.assertFalse(m.find());
        p = Pattern.compile("(?i)([a-z]+)[0-9]+");
        m = p.matcher("cAt123#doG345");
        Assert.assertTrue(m.find());
        Assert.assertEquals("cAt", m.group(1));
        Assert.assertTrue(m.find());
        Assert.assertEquals("doG", m.group(1));
        Assert.assertFalse(m.find());
    }

    @Test
    public void testMultiline() throws PatternSyntaxException {
        Pattern p;
        Matcher m;
        p = Pattern.compile("^foo");
        m = p.matcher("foobar");
        Assert.assertTrue(m.find());
        Assert.assertTrue((((m.start()) == 0) && ((m.end()) == 3)));
        Assert.assertFalse(m.find());
        m = p.matcher("barfoo");
        Assert.assertFalse(m.find());
        p = Pattern.compile("foo$");
        m = p.matcher("foobar");
        Assert.assertFalse(m.find());
        m = p.matcher("barfoo");
        Assert.assertTrue(m.find());
        Assert.assertTrue((((m.start()) == 3) && ((m.end()) == 6)));
        Assert.assertFalse(m.find());
        p = Pattern.compile("^foo([0-9]*)", Pattern.MULTILINE);
        m = p.matcher("foo1bar\nfoo2foo3\nbarfoo4");
        Assert.assertTrue(m.find());
        Assert.assertEquals("1", m.group(1));
        Assert.assertTrue(m.find());
        Assert.assertEquals("2", m.group(1));
        Assert.assertFalse(m.find());
        p = Pattern.compile("foo([0-9]*)$", Pattern.MULTILINE);
        m = p.matcher("foo1bar\nfoo2foo3\nbarfoo4");
        Assert.assertTrue(m.find());
        Assert.assertEquals("3", m.group(1));
        Assert.assertTrue(m.find());
        Assert.assertEquals("4", m.group(1));
        Assert.assertFalse(m.find());
        p = Pattern.compile("(?m)^foo([0-9]*)");
        m = p.matcher("foo1bar\nfoo2foo3\nbarfoo4");
        Assert.assertTrue(m.find());
        Assert.assertEquals("1", m.group(1));
        Assert.assertTrue(m.find());
        Assert.assertEquals("2", m.group(1));
        Assert.assertFalse(m.find());
        p = Pattern.compile("(?m)foo([0-9]*)$");
        m = p.matcher("foo1bar\nfoo2foo3\nbarfoo4");
        Assert.assertTrue(m.find());
        Assert.assertEquals("3", m.group(1));
        Assert.assertTrue(m.find());
        Assert.assertEquals("4", m.group(1));
        Assert.assertFalse(m.find());
    }
}

