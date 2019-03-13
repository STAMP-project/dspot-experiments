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
package org.apache.nifi.attribute.expression.language;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class TestStandardPreparedQuery {
    @Test
    public void testSimpleReference() {
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("xx", "world");
        Assert.assertEquals("world", evaluate("${xx}", attrs));
        Assert.assertEquals("hello, world!", evaluate("hello, ${xx}!", attrs));
    }

    @Test
    public void testEmbeddedReference() {
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("xx", "yy");
        attrs.put("yy", "world");
        Assert.assertEquals("world", evaluate("${${xx}}", attrs));
    }

    @Test
    public void testSeveralSequentialExpressions() {
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("audience", "World");
        attributes.put("comma", ",");
        attributes.put("question", " how are you?");
        Assert.assertEquals("Hello, World, how are you?!", evaluate("Hello, ${audience}${comma}${question}!", attributes));
    }

    @Test
    public void testVariableImpacted() {
        final Set<String> attr = new HashSet<>();
        attr.add("attr");
        final Set<String> attr2 = new HashSet<>();
        attr2.add("attr");
        attr2.add("attr2");
        final Set<String> abc = new HashSet<>();
        abc.add("a");
        abc.add("b");
        abc.add("c");
        Assert.assertTrue(Query.prepare("${attr}").getVariableImpact().isImpacted("attr"));
        Assert.assertFalse(Query.prepare("${attr}").getVariableImpact().isImpacted("attr2"));
        Assert.assertTrue(Query.prepare("${attr:trim():toUpper():equals('abc')}").getVariableImpact().isImpacted("attr"));
        Assert.assertFalse(Query.prepare("${anyAttribute('a', 'b', 'c'):equals('hello')}").getVariableImpact().isImpacted("attr"));
        Assert.assertTrue(Query.prepare("${anyAttribute('a', 'b', 'c'):equals('hello')}").getVariableImpact().isImpacted("a"));
        Assert.assertTrue(Query.prepare("${anyAttribute('a', 'b', 'c'):equals('hello')}").getVariableImpact().isImpacted("b"));
        Assert.assertTrue(Query.prepare("${anyAttribute('a', 'b', 'c'):equals('hello')}").getVariableImpact().isImpacted("c"));
        Assert.assertFalse(Query.prepare("${allAttributes('a', 'b', 'c'):equals('hello')}").getVariableImpact().isImpacted("attr"));
        Assert.assertTrue(Query.prepare("${allAttributes('a', 'b', 'c'):equals('hello')}").getVariableImpact().isImpacted("a"));
        Assert.assertTrue(Query.prepare("${allAttributes('a', 'b', 'c'):equals('hello')}").getVariableImpact().isImpacted("b"));
        Assert.assertTrue(Query.prepare("${allAttributes('a', 'b', 'c'):equals('hello')}").getVariableImpact().isImpacted("c"));
        Assert.assertTrue(Query.prepare("${attr:equals('${attr2}')}").getVariableImpact().isImpacted("attr"));
        Assert.assertTrue(Query.prepare("${attr:equals('${attr2}')}").getVariableImpact().isImpacted("attr2"));
        Assert.assertFalse(Query.prepare("${attr:equals('${attr2}')}").getVariableImpact().isImpacted("attr3"));
        Assert.assertTrue(Query.prepare("${allMatchingAttributes('a.*'):equals('hello')}").getVariableImpact().isImpacted("attr"));
        Assert.assertTrue(Query.prepare("${anyMatchingAttribute('a.*'):equals('hello')}").getVariableImpact().isImpacted("attr"));
    }
}

