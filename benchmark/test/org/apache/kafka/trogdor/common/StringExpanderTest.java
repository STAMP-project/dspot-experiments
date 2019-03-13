/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.trogdor.common;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class StringExpanderTest {
    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    @Test
    public void testNoExpansionNeeded() throws Exception {
        Assert.assertEquals(Collections.singleton("foo"), StringExpander.expand("foo"));
        Assert.assertEquals(Collections.singleton("bar"), StringExpander.expand("bar"));
        Assert.assertEquals(Collections.singleton(""), StringExpander.expand(""));
    }

    @Test
    public void testExpansions() throws Exception {
        HashSet<String> expected1 = new HashSet<>(Arrays.asList("foo1", "foo2", "foo3"));
        Assert.assertEquals(expected1, StringExpander.expand("foo[1-3]"));
        HashSet<String> expected2 = new HashSet<>(Arrays.asList("foo bar baz 0"));
        Assert.assertEquals(expected2, StringExpander.expand("foo bar baz [0-0]"));
        HashSet<String> expected3 = new HashSet<>(Arrays.asList("[[ wow50 ]]", "[[ wow51 ]]", "[[ wow52 ]]"));
        Assert.assertEquals(expected3, StringExpander.expand("[[ wow[50-52] ]]"));
        HashSet<String> expected4 = new HashSet<>(Arrays.asList("foo1bar", "foo2bar", "foo3bar"));
        Assert.assertEquals(expected4, StringExpander.expand("foo[1-3]bar"));
        // should expand latest range first
        HashSet<String> expected5 = new HashSet<>(Arrays.asList("start[1-3]middle1epilogue", "start[1-3]middle2epilogue", "start[1-3]middle3epilogue"));
        Assert.assertEquals(expected5, StringExpander.expand("start[1-3]middle[1-3]epilogue"));
    }
}

