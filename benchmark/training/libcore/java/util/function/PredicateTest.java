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
import java.util.function.Predicate;
import junit.framework.TestCase;


public class PredicateTest extends TestCase {
    public void testAnd() throws Exception {
        Object arg = new Object();
        AtomicBoolean alwaysTrueInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysTrue2Invoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalseInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalse2Invoked = new AtomicBoolean(false);
        AtomicBoolean[] invocationState = new AtomicBoolean[]{ alwaysTrueInvoked, alwaysTrue2Invoked, alwaysFalseInvoked, alwaysFalse2Invoked };
        Predicate<Object> alwaysTrue = ( x) -> {
            alwaysTrueInvoked.set(true);
            TestCase.assertSame(x, arg);
            return true;
        };
        Predicate<Object> alwaysTrue2 = ( x) -> {
            alwaysTrue2Invoked.set(true);
            TestCase.assertSame(x, arg);
            return true;
        };
        Predicate<Object> alwaysFalse = ( x) -> {
            alwaysFalseInvoked.set(true);
            TestCase.assertSame(x, arg);
            return false;
        };
        Predicate<Object> alwaysFalse2 = ( x) -> {
            alwaysFalse2Invoked.set(true);
            TestCase.assertSame(x, arg);
            return false;
        };
        // true && true
        PredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.and(alwaysTrue2).test(arg));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (alwaysTrue2Invoked.get())));
        // true && false
        PredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysTrue.and(alwaysFalse).test(arg));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (alwaysFalseInvoked.get())));
        // false && false
        PredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.and(alwaysFalse2).test(arg));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (!(alwaysFalse2Invoked.get()))));
        // false && true
        PredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.and(alwaysTrue).test(arg));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (!(alwaysTrueInvoked.get()))));
    }

    public void testAnd_null() throws Exception {
        Object arg = new Object();
        Predicate<Object> alwaysTrue = ( x) -> {
            TestCase.assertSame(arg, x);
            return true;
        };
        try {
            alwaysTrue.and(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testNegate() throws Exception {
        Predicate<Object> alwaysTrue = ( x) -> true;
        TestCase.assertFalse(alwaysTrue.negate().test(null));
        Predicate<Object> alwaysFalse = ( x) -> false;
        TestCase.assertTrue(alwaysFalse.negate().test(null));
    }

    public void testOr() throws Exception {
        Object arg = new Object();
        AtomicBoolean alwaysTrueInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysTrue2Invoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalseInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalse2Invoked = new AtomicBoolean(false);
        AtomicBoolean[] invocationState = new AtomicBoolean[]{ alwaysTrueInvoked, alwaysTrue2Invoked, alwaysFalseInvoked, alwaysFalse2Invoked };
        Predicate<Object> alwaysTrue = ( x) -> {
            alwaysTrueInvoked.set(true);
            TestCase.assertSame(x, arg);
            return true;
        };
        Predicate<Object> alwaysTrue2 = ( x) -> {
            alwaysTrue2Invoked.set(true);
            TestCase.assertSame(x, arg);
            return true;
        };
        Predicate<Object> alwaysFalse = ( x) -> {
            alwaysFalseInvoked.set(true);
            TestCase.assertSame(x, arg);
            return false;
        };
        Predicate<Object> alwaysFalse2 = ( x) -> {
            alwaysFalse2Invoked.set(true);
            TestCase.assertSame(x, arg);
            return false;
        };
        // true || true
        PredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.or(alwaysTrue2).test(arg));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (!(alwaysTrue2Invoked.get()))));
        // true || false
        PredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.or(alwaysFalse).test(arg));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (!(alwaysFalseInvoked.get()))));
        // false || false
        PredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.or(alwaysFalse2).test(arg));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (alwaysFalse2Invoked.get())));
        // false || true
        PredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysFalse.or(alwaysTrue).test(arg));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (alwaysTrueInvoked.get())));
    }

    public void testOr_null() throws Exception {
        Predicate<Object> alwaysTrue = ( x) -> true;
        try {
            alwaysTrue.or(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }
}

