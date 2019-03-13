/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.cache.inspectors.condition;


import org.drools.verifier.core.index.model.Field;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StringConditionInspectorOverlapTest {
    @Mock
    private Field field;

    @Test
    public void test001() throws Exception {
        StringConditionInspector a = getCondition(new org.drools.verifier.core.index.keys.Values("Toni"), "==");
        StringConditionInspector b = getCondition(new org.drools.verifier.core.index.keys.Values("Toni"), "!=");
        Assert.assertFalse(a.overlaps(b));
        Assert.assertFalse(b.overlaps(a));
    }

    @Test
    public void test002() throws Exception {
        StringConditionInspector a = getCondition(new org.drools.verifier.core.index.keys.Values("Toni"), "==");
        StringConditionInspector b = getCondition(new org.drools.verifier.core.index.keys.Values("Eder"), "!=");
        Assert.assertTrue(a.overlaps(b));
        Assert.assertTrue(b.overlaps(a));
    }

    @Test
    public void test003() throws Exception {
        StringConditionInspector a = getCondition(new org.drools.verifier.core.index.keys.Values("Toni", "Michael", "Eder"), "in");
        StringConditionInspector b = getCondition(new org.drools.verifier.core.index.keys.Values("Toni"), "!=");
        Assert.assertTrue(a.overlaps(b));
        Assert.assertTrue(b.overlaps(a));
    }

    @Test
    public void test004() throws Exception {
        StringConditionInspector a = getCondition(new org.drools.verifier.core.index.keys.Values("Toni", "Michael", "Eder"), "in");
        StringConditionInspector b = getCondition(new org.drools.verifier.core.index.keys.Values("Toni"), "==");
        Assert.assertTrue(a.overlaps(b));
        Assert.assertTrue(b.overlaps(a));
    }

    @Test
    public void test005() throws Exception {
        StringConditionInspector a = getCondition(new org.drools.verifier.core.index.keys.Values("Toni", "Michael"), "in");
        StringConditionInspector b = getCondition(new org.drools.verifier.core.index.keys.Values("Eder"), "==");
        Assert.assertFalse(a.overlaps(b));
        Assert.assertFalse(b.overlaps(a));
    }

    @Test
    public void test006() throws Exception {
        StringConditionInspector a = getCondition(new org.drools.verifier.core.index.keys.Values("Toni", "Michael"), "in");
        StringConditionInspector b = getCondition(new org.drools.verifier.core.index.keys.Values("Eder"), "!=");
        Assert.assertTrue(a.overlaps(b));
        Assert.assertTrue(b.overlaps(a));
    }

    @Test
    public void test007() throws Exception {
        StringConditionInspector a = getCondition(new org.drools.verifier.core.index.keys.Values("Toni", "Michael"), "in");
        StringConditionInspector b = getCondition(new org.drools.verifier.core.index.keys.Values("Eder", "John"), "in");
        Assert.assertFalse(a.overlaps(b));
        Assert.assertFalse(b.overlaps(a));
    }

    @Test
    public void test008() throws Exception {
        StringConditionInspector a = getCondition(new org.drools.verifier.core.index.keys.Values("Toni", "Michael"), "in");
        StringConditionInspector b = getCondition(new org.drools.verifier.core.index.keys.Values("Toni", "Eder"), "in");
        Assert.assertTrue(a.overlaps(b));
        Assert.assertTrue(b.overlaps(a));
    }

    @Test
    public void test009() throws Exception {
        StringConditionInspector a = getCondition(new org.drools.verifier.core.index.keys.Values("Toni"), "in");
        StringConditionInspector b = getCondition(new org.drools.verifier.core.index.keys.Values("Eder", "Toni"), "in");
        Assert.assertTrue(a.overlaps(b));
        Assert.assertTrue(b.overlaps(a));
    }

    @Test
    public void test010() throws Exception {
        StringConditionInspector a = getCondition(new org.drools.verifier.core.index.keys.Values(""), "==");
        StringConditionInspector b = getCondition(new org.drools.verifier.core.index.keys.Values(""), "==");
        Assert.assertFalse(a.overlaps(b));
        Assert.assertFalse(b.overlaps(a));
    }

    @Test
    public void test011() throws Exception {
        StringConditionInspector a = getCondition(new org.drools.verifier.core.index.keys.Values("Toni"), "==");
        StringConditionInspector b = getCondition(new org.drools.verifier.core.index.keys.Values("Toni"), "==");
        Assert.assertTrue(a.overlaps(b));
        Assert.assertTrue(b.overlaps(a));
    }
}

