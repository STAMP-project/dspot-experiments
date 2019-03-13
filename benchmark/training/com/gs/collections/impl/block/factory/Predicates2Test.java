/**
 * Copyright 2014 Goldman Sachs.
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
package com.gs.collections.impl.block.factory;


import Lists.fixedSize;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.ListIterate;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Predicates2Test {
    private static final Predicates2<Object, Object> TRUE = Predicates2.alwaysTrue();

    private static final Predicates2<Object, Object> FALSE = Predicates2.alwaysFalse();

    private static final Object OBJECT = new Object();

    @Test
    public void throwing() {
        Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, () -> Predicates2.throwing(( a, b) -> {
            throw new IOException();
        }).accept(null, null));
    }

    @Test
    public void staticOr() {
        Assert.assertTrue(Predicates2.or(Predicates2Test.TRUE, Predicates2Test.FALSE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertFalse(Predicates2.or(Predicates2Test.FALSE, Predicates2Test.FALSE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertTrue(Predicates2.or(Predicates2Test.TRUE, Predicates2Test.TRUE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertNotNull(Predicates2.or(Predicates2Test.TRUE, Predicates2Test.TRUE).toString());
    }

    @Test
    public void instanceOr() {
        Assert.assertTrue(Predicates2Test.TRUE.or(Predicates2Test.FALSE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertFalse(Predicates2Test.FALSE.or(Predicates2Test.FALSE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertTrue(Predicates2Test.TRUE.or(Predicates2Test.TRUE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertNotNull(Predicates2Test.TRUE.or(Predicates2Test.TRUE).toString());
    }

    @Test
    public void staticAnd() {
        Assert.assertTrue(Predicates2.and(Predicates2Test.TRUE, Predicates2Test.TRUE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertFalse(Predicates2.and(Predicates2Test.TRUE, Predicates2Test.FALSE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertFalse(Predicates2.and(Predicates2Test.FALSE, Predicates2Test.FALSE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertNotNull(Predicates2.and(Predicates2Test.FALSE, Predicates2Test.FALSE).toString());
    }

    @Test
    public void instanceAnd() {
        Assert.assertTrue(Predicates2Test.TRUE.and(Predicates2Test.TRUE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertFalse(Predicates2Test.TRUE.and(Predicates2Test.FALSE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertFalse(Predicates2Test.FALSE.and(Predicates2Test.FALSE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertNotNull(Predicates2Test.FALSE.and(Predicates2Test.FALSE).toString());
    }

    @Test
    public void equal() {
        Assert.assertTrue(Predicates2.equal().accept(1, 1));
        Assert.assertFalse(Predicates2.equal().accept(2, 1));
        Assert.assertFalse(Predicates2.equal().accept(null, 1));
        Assert.assertNotNull(Predicates2.equal().toString());
    }

    @Test
    public void notEqual() {
        Assert.assertFalse(Predicates2.notEqual().accept(1, 1));
        Assert.assertTrue(Predicates2.notEqual().accept(2, 1));
        Assert.assertTrue(Predicates2.notEqual().accept(1, 2));
        Assert.assertTrue(Predicates2.notEqual().accept(null, 1));
        Assert.assertTrue(Predicates2.notEqual().accept(1, null));
        Assert.assertFalse(Predicates2.notEqual().accept(null, null));
        Assert.assertNotNull(Predicates2.notEqual().toString());
    }

    @Test
    public void not() {
        Assert.assertFalse(Predicates2.not(Predicates2Test.TRUE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertTrue(Predicates2.not(Predicates2Test.FALSE).accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertNotNull(Predicates2.not(Predicates2Test.FALSE).toString());
    }

    @Test
    public void testNull() {
        Assert.assertFalse(Predicates2.isNull().accept(Predicates2Test.OBJECT, null));
        Assert.assertTrue(Predicates2.isNull().accept(null, null));
        Assert.assertNotNull(Predicates2.isNull().toString());
    }

    @Test
    public void notNull() {
        Assert.assertTrue(Predicates2.notNull().accept(Predicates2Test.OBJECT, null));
        Assert.assertFalse(Predicates2.notNull().accept(null, null));
        Assert.assertNotNull(Predicates2.notNull().toString());
    }

    @Test
    public void sameAs() {
        Assert.assertTrue(Predicates2.sameAs().accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertFalse(Predicates2.sameAs().accept(Predicates2Test.OBJECT, new Object()));
        Assert.assertNotNull(Predicates2.sameAs().toString());
    }

    @Test
    public void notSameAs() {
        Assert.assertFalse(Predicates2.notSameAs().accept(Predicates2Test.OBJECT, Predicates2Test.OBJECT));
        Assert.assertTrue(Predicates2.notSameAs().accept(Predicates2Test.OBJECT, new Object()));
        Assert.assertNotNull(Predicates2.notSameAs().toString());
    }

    @Test
    public void instanceOf() {
        Assert.assertTrue(Predicates2.instanceOf().accept(1, Integer.class));
        Assert.assertFalse(Predicates2.instanceOf().accept(1.0, Integer.class));
        Assert.assertNotNull(Predicates2.instanceOf().toString());
    }

    @Test
    public void notInstanceOf() {
        Assert.assertFalse(Predicates2.notInstanceOf().accept(1, Integer.class));
        Assert.assertTrue(Predicates2.notInstanceOf().accept(1.0, Integer.class));
        Assert.assertNotNull(Predicates2.notInstanceOf().toString());
    }

    @Test
    public void attributeEqual() {
        Integer one = 1;
        Assert.assertTrue(Predicates2.attributeEqual(Functions.getToString()).accept(one, "1"));
        Assert.assertFalse(Predicates2.attributeEqual(Functions.getToString()).accept(one, "2"));
        Assert.assertNotNull(Predicates2.attributeEqual(Functions.getToString()).toString());
    }

    @Test
    public void attributeNotEqual() {
        Integer one = 1;
        Assert.assertFalse(Predicates2.attributeNotEqual(Functions.getToString()).accept(one, "1"));
        Assert.assertTrue(Predicates2.attributeNotEqual(Functions.getToString()).accept(one, "2"));
        Assert.assertNotNull(Predicates2.attributeNotEqual(Functions.getToString()).toString());
    }

    @Test
    public void attributeLessThan() {
        Integer one = 1;
        Assert.assertFalse(Predicates2.attributeLessThan(Functions.getToString()).accept(one, "1"));
        Assert.assertTrue(Predicates2.attributeLessThan(Functions.getToString()).accept(one, "2"));
        Assert.assertNotNull(Predicates2.attributeLessThan(Functions.getToString()).toString());
    }

    @Test
    public void attributeGreaterThan() {
        Integer one = 1;
        Assert.assertTrue(Predicates2.attributeGreaterThan(Functions.getToString()).accept(one, "0"));
        Assert.assertFalse(Predicates2.attributeGreaterThan(Functions.getToString()).accept(one, "1"));
        Assert.assertNotNull(Predicates2.attributeGreaterThan(Functions.getToString()).toString());
    }

    @Test
    public void attributeGreaterThanOrEqualTo() {
        Integer one = 1;
        Assert.assertTrue(Predicates2.attributeGreaterThanOrEqualTo(Functions.getToString()).accept(one, "0"));
        Assert.assertTrue(Predicates2.attributeGreaterThanOrEqualTo(Functions.getToString()).accept(one, "1"));
        Assert.assertFalse(Predicates2.attributeGreaterThanOrEqualTo(Functions.getToString()).accept(one, "2"));
        Assert.assertNotNull(Predicates2.attributeGreaterThanOrEqualTo(Functions.getToString()).toString());
    }

    @Test
    public void attributeLessThanOrEqualTo() {
        Assert.assertFalse(Predicates2.attributeLessThanOrEqualTo(Functions.getToString()).accept(1, "0"));
        Assert.assertTrue(Predicates2.attributeLessThanOrEqualTo(Functions.getToString()).accept(1, "1"));
        Assert.assertTrue(Predicates2.attributeLessThanOrEqualTo(Functions.getToString()).accept(1, "2"));
        Assert.assertNotNull(Predicates2.attributeLessThanOrEqualTo(Functions.getToString()).toString());
    }

    @Test
    public void in() {
        MutableList<String> list1 = fixedSize.of("1", "3");
        Assert.assertTrue(Predicates2.in().accept("1", list1));
        Assert.assertFalse(Predicates2.in().accept("2", list1));
        Assert.assertNotNull(Predicates2.in().toString());
        MutableList<String> list2 = fixedSize.of("1", "2");
        MutableList<String> newList = ListIterate.selectWith(list2, Predicates2.in(), list1);
        Assert.assertEquals(FastList.newListWith("1"), newList);
    }

    @Test
    public void attributeIn() {
        MutableList<String> upperList = fixedSize.of("A", "B");
        Assert.assertTrue(Predicates2.attributeIn(StringFunctions.toUpperCase()).accept("a", upperList));
        Assert.assertFalse(Predicates2.attributeIn(StringFunctions.toUpperCase()).accept("c", upperList));
        MutableList<String> lowerList = fixedSize.of("a", "c");
        MutableList<String> newList = ListIterate.selectWith(lowerList, Predicates2.attributeIn(StringFunctions.toUpperCase()), upperList);
        Assert.assertEquals(FastList.newListWith("a"), newList);
    }

    @Test
    public void attributeIn_MultiTypes() {
        MutableList<String> stringInts = fixedSize.of("1", "2");
        Assert.assertTrue(Predicates2.attributeIn(Functions.getToString()).accept(1, stringInts));
        Assert.assertFalse(Predicates2.attributeIn(Functions.getToString()).accept(3, stringInts));
        Assert.assertFalse(Predicates2.attributeIn(Functions.getToString()).accept(3, stringInts));
        MutableList<Integer> intList = fixedSize.of(1, 3);
        MutableList<Integer> newList = ListIterate.selectWith(intList, Predicates2.attributeIn(Functions.getToString()), stringInts);
        Assert.assertEquals(FastList.newListWith(1), newList);
    }

    @Test
    public void notIn() {
        MutableList<String> odds = fixedSize.of("1", "3");
        Assert.assertFalse(Predicates2.notIn().accept("1", odds));
        Assert.assertTrue(Predicates2.notIn().accept("2", odds));
        Assert.assertNotNull(Predicates2.notIn().toString());
        MutableList<String> list = fixedSize.of("1", "2");
        MutableList<String> newList = ListIterate.selectWith(list, Predicates2.notIn(), odds);
        Assert.assertEquals(FastList.newListWith("2"), newList);
    }

    @Test
    public void attributeNotIn() {
        Function<String, String> function = StringFunctions.toLowerCase();
        MutableList<String> lowerList = fixedSize.of("a", "b");
        Assert.assertFalse(Predicates2.attributeNotIn(function).accept("A", lowerList));
        Assert.assertTrue(Predicates2.attributeNotIn(function).accept("C", lowerList));
        MutableList<String> upperList = fixedSize.of("A", "C");
        MutableList<String> newList = ListIterate.rejectWith(upperList, Predicates2.attributeNotIn(function), lowerList);
        Assert.assertEquals(FastList.newListWith("A"), newList);
    }

    @Test
    public void lessThanNumber() {
        Assert.assertTrue(Predicates2.<Integer>lessThan().accept((-1), 0));
        Assert.assertTrue(Predicates2.<Double>lessThan().accept((-1.0), 0.0));
        Assert.assertFalse(Predicates2.<Double>lessThan().accept(0.0, (-1.0)));
        Assert.assertNotNull(Predicates2.<Integer>lessThan().toString());
    }

    @Test
    public void greaterThanNumber() {
        Assert.assertFalse(Predicates2.<Integer>greaterThan().accept((-1), 0));
        Assert.assertFalse(Predicates2.<Double>greaterThan().accept((-1.0), 0.0));
        Assert.assertTrue(Predicates2.<Double>greaterThan().accept(0.0, (-1.0)));
        Assert.assertNotNull(Predicates2.<Integer>greaterThan().toString());
    }

    @Test
    public void lessEqualThanNumber() {
        Assert.assertTrue(Predicates2.<Integer>lessThanOrEqualTo().accept((-1), 0));
        Assert.assertTrue(Predicates2.<Double>lessThanOrEqualTo().accept((-1.0), 0.0));
        Assert.assertTrue(Predicates2.<Double>lessThanOrEqualTo().accept((-1.0), (-1.0)));
        Assert.assertFalse(Predicates2.<Double>lessThanOrEqualTo().accept(0.0, (-1.0)));
        Assert.assertNotNull(Predicates2.<Integer>lessThanOrEqualTo().toString());
    }

    @Test
    public void greaterEqualNumber() {
        Assert.assertFalse(Predicates2.<Integer>greaterThanOrEqualTo().accept((-1), 0));
        Assert.assertFalse(Predicates2.<Double>greaterThanOrEqualTo().accept((-1.0), 0.0));
        Assert.assertTrue(Predicates2.<Double>greaterThanOrEqualTo().accept((-1.0), (-1.0)));
        Assert.assertTrue(Predicates2.<Double>greaterThanOrEqualTo().accept(0.0, (-1.0)));
        Assert.assertNotNull(Predicates2.<Integer>greaterThanOrEqualTo().toString());
    }
}

