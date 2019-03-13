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
import java.util.function.IntPredicate;
import junit.framework.TestCase;


public class IntPredicateTest extends TestCase {
    public void testAnd() throws Exception {
        int arg = 5;
        AtomicBoolean alwaysTrueInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysTrue2Invoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalseInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalse2Invoked = new AtomicBoolean(false);
        AtomicBoolean[] invocationState = new AtomicBoolean[]{ alwaysTrueInvoked, alwaysTrue2Invoked, alwaysFalseInvoked, alwaysFalse2Invoked };
        IntPredicate alwaysTrue = ( x) -> {
            alwaysTrueInvoked.set(true);
            TestCase.assertEquals(x, arg);
            return true;
        };
        IntPredicate alwaysTrue2 = ( x) -> {
            alwaysTrue2Invoked.set(true);
            TestCase.assertEquals(x, arg);
            return true;
        };
        IntPredicate alwaysFalse = ( x) -> {
            alwaysFalseInvoked.set(true);
            TestCase.assertEquals(x, arg);
            return false;
        };
        IntPredicate alwaysFalse2 = ( x) -> {
            alwaysFalse2Invoked.set(true);
            TestCase.assertEquals(x, arg);
            return false;
        };
        // true && true
        IntPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.and(alwaysTrue2).test(arg));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (alwaysTrue2Invoked.get())));
        // true && false
        IntPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysTrue.and(alwaysFalse).test(arg));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (alwaysFalseInvoked.get())));
        // false && false
        IntPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.and(alwaysFalse2).test(arg));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (!(alwaysFalse2Invoked.get()))));
        // false && true
        IntPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.and(alwaysTrue).test(arg));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (!(alwaysTrueInvoked.get()))));
    }

    public void testAnd_null() throws Exception {
        IntPredicate alwaysTrue = ( x) -> true;
        try {
            alwaysTrue.and(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testNegate() throws Exception {
        int arg = 5;
        IntPredicate alwaysTrue = ( x) -> {
            TestCase.assertEquals(x, arg);
            return true;
        };
        TestCase.assertFalse(alwaysTrue.negate().test(arg));
        IntPredicate alwaysFalse = ( x) -> {
            TestCase.assertEquals(x, arg);
            return false;
        };
        TestCase.assertTrue(alwaysFalse.negate().test(arg));
    }

    public void testOr() throws Exception {
        int arg = 5;
        AtomicBoolean alwaysTrueInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysTrue2Invoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalseInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalse2Invoked = new AtomicBoolean(false);
        AtomicBoolean[] invocationState = new AtomicBoolean[]{ alwaysTrueInvoked, alwaysTrue2Invoked, alwaysFalseInvoked, alwaysFalse2Invoked };
        IntPredicate alwaysTrue = ( x) -> {
            alwaysTrueInvoked.set(true);
            TestCase.assertEquals(x, arg);
            return true;
        };
        IntPredicate alwaysTrue2 = ( x) -> {
            alwaysTrue2Invoked.set(true);
            TestCase.assertEquals(x, arg);
            return true;
        };
        IntPredicate alwaysFalse = ( x) -> {
            alwaysFalseInvoked.set(true);
            TestCase.assertEquals(x, arg);
            return false;
        };
        IntPredicate alwaysFalse2 = ( x) -> {
            alwaysFalse2Invoked.set(true);
            TestCase.assertEquals(x, arg);
            return false;
        };
        // true || true
        IntPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.or(alwaysTrue2).test(arg));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (!(alwaysTrue2Invoked.get()))));
        // true || false
        IntPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.or(alwaysFalse).test(arg));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (!(alwaysFalseInvoked.get()))));
        // false || false
        IntPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.or(alwaysFalse2).test(arg));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (alwaysFalse2Invoked.get())));
        // false || true
        IntPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysFalse.or(alwaysTrue).test(arg));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (alwaysTrueInvoked.get())));
    }

    public void testOr_null() throws Exception {
        IntPredicate alwaysTrue = ( x) -> true;
        try {
            alwaysTrue.or(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }
}

