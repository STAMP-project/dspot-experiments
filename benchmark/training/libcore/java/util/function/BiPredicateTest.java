/**
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.util.function;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import junit.framework.TestCase;


public class BiPredicateTest extends TestCase {
    public void testAnd() throws Exception {
        Object arg1 = "one";
        Object arg2 = "two";
        AtomicBoolean alwaysTrueInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysTrue2Invoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalseInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalse2Invoked = new AtomicBoolean(false);
        AtomicBoolean[] invocationState = new AtomicBoolean[]{ alwaysTrueInvoked, alwaysTrue2Invoked, alwaysFalseInvoked, alwaysFalse2Invoked };
        BiPredicate<Object, Object> alwaysTrue = ( x, y) -> {
            alwaysTrueInvoked.set(true);
            TestCase.assertSame(arg1, x);
            TestCase.assertSame(arg2, y);
            return true;
        };
        BiPredicate<Object, Object> alwaysTrue2 = ( x, y) -> {
            alwaysTrue2Invoked.set(true);
            TestCase.assertSame(arg1, x);
            TestCase.assertSame(arg2, y);
            return true;
        };
        BiPredicate<Object, Object> alwaysFalse = ( x, y) -> {
            alwaysFalseInvoked.set(true);
            TestCase.assertSame(arg1, x);
            TestCase.assertSame(arg2, y);
            return false;
        };
        BiPredicate<Object, Object> alwaysFalse2 = ( x, y) -> {
            alwaysFalse2Invoked.set(true);
            TestCase.assertSame(arg1, x);
            TestCase.assertSame(arg2, y);
            return false;
        };
        // true && true
        BiPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.and(alwaysTrue2).test(arg1, arg2));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (alwaysTrue2Invoked.get())));
        // true && false
        BiPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysTrue.and(alwaysFalse).test(arg1, arg2));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (alwaysFalseInvoked.get())));
        // false && false
        BiPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.and(alwaysFalse2).test(arg1, arg2));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (!(alwaysFalse2Invoked.get()))));
        // false && true
        BiPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.and(alwaysTrue).test(arg1, arg2));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (!(alwaysTrueInvoked.get()))));
    }

    public void testAnd_null() throws Exception {
        BiPredicate<Object, Object> alwaysTrue = ( x, y) -> true;
        try {
            alwaysTrue.and(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testNegate() throws Exception {
        Object arg1 = "one";
        Object arg2 = "two";
        BiPredicate<Object, Object> alwaysTrue = ( x, y) -> {
            TestCase.assertSame(arg1, x);
            TestCase.assertSame(arg2, y);
            return true;
        };
        TestCase.assertFalse(alwaysTrue.negate().test(arg1, arg2));
        BiPredicate<Object, Object> alwaysFalse = ( x, y) -> {
            TestCase.assertSame(arg1, x);
            TestCase.assertSame(arg2, y);
            return false;
        };
        TestCase.assertTrue(alwaysFalse.negate().test(arg1, arg2));
    }

    public void testOr() throws Exception {
        Object arg1 = "one";
        Object arg2 = "two";
        AtomicBoolean alwaysTrueInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysTrue2Invoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalseInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalse2Invoked = new AtomicBoolean(false);
        AtomicBoolean[] invocationState = new AtomicBoolean[]{ alwaysTrueInvoked, alwaysTrue2Invoked, alwaysFalseInvoked, alwaysFalse2Invoked };
        BiPredicate<Object, Object> alwaysTrue = ( x, y) -> {
            alwaysTrueInvoked.set(true);
            TestCase.assertSame(arg1, x);
            TestCase.assertSame(arg2, y);
            return true;
        };
        BiPredicate<Object, Object> alwaysTrue2 = ( x, y) -> {
            alwaysTrue2Invoked.set(true);
            TestCase.assertSame(arg1, x);
            TestCase.assertSame(arg2, y);
            return true;
        };
        BiPredicate<Object, Object> alwaysFalse = ( x, y) -> {
            alwaysFalseInvoked.set(true);
            TestCase.assertSame(arg1, x);
            TestCase.assertSame(arg2, y);
            return false;
        };
        BiPredicate<Object, Object> alwaysFalse2 = ( x, y) -> {
            alwaysFalse2Invoked.set(true);
            TestCase.assertSame(arg1, x);
            TestCase.assertSame(arg2, y);
            return false;
        };
        // true || true
        BiPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.or(alwaysTrue2).test(arg1, arg2));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (!(alwaysTrue2Invoked.get()))));
        // true || false
        BiPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.or(alwaysFalse).test(arg1, arg2));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (!(alwaysFalseInvoked.get()))));
        // false || false
        BiPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.or(alwaysFalse2).test(arg1, arg2));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (alwaysFalse2Invoked.get())));
        // false || true
        BiPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysFalse.or(alwaysTrue).test(arg1, arg2));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (alwaysTrueInvoked.get())));
    }

    public void testOr_null() throws Exception {
        BiPredicate<Object, Object> alwaysTrue = ( x, y) -> true;
        try {
            alwaysTrue.or(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }
}

