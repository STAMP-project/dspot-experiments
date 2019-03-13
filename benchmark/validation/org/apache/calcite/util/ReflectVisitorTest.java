/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.util;


import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;


/**
 * ReflectVisitorTest tests {@link ReflectUtil#invokeVisitor} and
 * {@link ReflectiveVisitor} and provides a contrived example of how to use
 * them.
 */
public class ReflectVisitorTest {
    // ~ Constructors -----------------------------------------------------------
    public ReflectVisitorTest() {
    }

    // ~ Methods ----------------------------------------------------------------
    /**
     * Tests CarelessNumberNegater.
     */
    @Test
    public void testCarelessNegater() {
        ReflectVisitorTest.NumberNegater negater = new ReflectVisitorTest.CarelessNumberNegater();
        Number result;
        // verify that negater is capable of handling integers
        result = negater.negate(5);
        Assert.assertEquals((-5), result.intValue());
    }

    /**
     * Tests CarefulNumberNegater.
     */
    @Test
    public void testCarefulNegater() {
        ReflectVisitorTest.NumberNegater negater = new ReflectVisitorTest.CarefulNumberNegater();
        Number result;
        // verify that negater is capable of handling integers,
        // and that result comes back with same type
        result = negater.negate(5);
        Assert.assertEquals((-5), result.intValue());
        Assert.assertTrue((result instanceof Integer));
        // verify that negater is capable of handling longs;
        // even though it doesn't provide an explicit implementation,
        // it should inherit the one from CarelessNumberNegater
        result = negater.negate(5L);
        Assert.assertEquals((-5L), result.longValue());
    }

    /**
     * Tests CluelessNumberNegater.
     */
    @Test
    public void testCluelessNegater() {
        ReflectVisitorTest.NumberNegater negater = new ReflectVisitorTest.CluelessNumberNegater();
        Number result;
        // verify that negater is capable of handling shorts,
        // and that result comes back with same type
        result = negater.negate(((short) (5)));
        Assert.assertEquals((-5), result.shortValue());
        Assert.assertTrue((result instanceof Short));
        // verify that negater is NOT capable of handling integers
        result = negater.negate(5);
        Assert.assertEquals(null, result);
    }

    /**
     * Tests for ambiguity detection in method lookup.
     */
    @Test
    public void testAmbiguity() {
        ReflectVisitorTest.NumberNegater negater = new ReflectVisitorTest.IndecisiveNumberNegater();
        Number result;
        try {
            result = negater.negate(new ReflectVisitorTest.AmbiguousNumber());
        } catch (IllegalArgumentException ex) {
            // expected
            Assert.assertTrue(ex.getMessage().contains("ambiguity"));
            return;
        }
        Assert.fail("Expected failure due to ambiguity");
    }

    /**
     * Tests that ambiguity detection in method lookup does not kick in when a
     * better match is available.
     */
    @Test
    public void testNonAmbiguity() {
        ReflectVisitorTest.NumberNegater negater = new ReflectVisitorTest.SomewhatIndecisiveNumberNegater();
        Number result;
        result = negater.negate(new ReflectVisitorTest.SomewhatAmbiguousNumber());
        Assert.assertEquals(0.0, result.doubleValue(), 0.001);
    }

    // ~ Inner Interfaces -------------------------------------------------------
    /**
     * An interface for introducing ambiguity into the class hierarchy.
     */
    public interface CrunchableNumber {}

    /**
     * An interface for introducing ambiguity into the class hierarchy.
     */
    public interface FudgeableNumber {}

    /**
     * Sub-interface of {@link FudgeableNumber}.
     */
    public interface DiceyNumber extends ReflectVisitorTest.FudgeableNumber {}

    // ~ Inner Classes ----------------------------------------------------------
    /**
     * NumberNegater defines the abstract base for a computation object capable
     * of negating an arbitrary number. Subclasses implement the computation by
     * publishing methods with the signature "void visit(X x)" where X is a
     * subclass of Number.
     */
    public abstract class NumberNegater implements ReflectiveVisitor {
        protected Number result;

        private final ReflectiveVisitDispatcher<ReflectVisitorTest.NumberNegater, Number> dispatcher = ReflectUtil.createDispatcher(ReflectVisitorTest.NumberNegater.class, Number.class);

        /**
         * Negates the given number.
         *
         * @param n
         * 		the number to be negated
         * @return the negated result; not guaranteed to be the same concrete
        type as n; null is returned if n's type wasn't handled
         */
        public Number negate(Number n) {
            // we specify Number.class as the hierarchy root so
            // that extraneous visit methods are ignored
            result = null;
            dispatcher.invokeVisitor(this, n, "visit");
            return result;
        }

        /**
         * Negates the given number without using a dispatcher object to cache
         * applicable methods. The results should be the same as
         * {@link #negate(Number)}.
         *
         * @param n
         * 		the number to be negated
         * @return the negated result; not guaranteed to be the same concrete
        type as n; null is returned if n's type wasn't handled
         */
        public Number negateWithoutDispatcher(Number n) {
            // we specify Number.class as the hierarchy root so
            // that extraneous visit methods are ignored
            result = null;
            ReflectUtil.invokeVisitor(this, n, Number.class, "visit");
            return result;
        }
    }

    /**
     * CarelessNumberNegater implements NumberNegater in a careless fashion by
     * converting its input to a double and then negating that. This can lose
     * precision for types such as BigInteger.
     */
    public class CarelessNumberNegater extends ReflectVisitorTest.NumberNegater {
        public void visit(Number n) {
            result = -(n.doubleValue());
        }
    }

    /**
     * CarefulNumberNegater implements NumberNegater in a careful fashion by
     * providing overloads for each known subclass of Number and returning the
     * same subclass for the result. Extends CarelessNumberNegater so that it
     * can still handle unknown types of Number.
     */
    // ... imagine implementations for other Number subclasses here ...
    public class CarefulNumberNegater extends ReflectVisitorTest.CarelessNumberNegater {
        public void visit(Integer i) {
            result = -i;
            assert (result) instanceof Integer;
        }

        public void visit(Short s) {
            result = -s;
            assert (result) instanceof Short;
        }
    }

    /**
     * CluelessNumberNegater implements NumberNegater in a very broken fashion;
     * does the right thing for Shorts, but attempts to override visit(Object).
     * This is just here for testing the hierarchyRoot parameter of
     * invokeVisitor.
     */
    public class CluelessNumberNegater extends ReflectVisitorTest.NumberNegater {
        public void visit(Object obj) {
            result = 42;
        }

        public void visit(Short s) {
            result = ((short) (-s));
            assert (result) instanceof Short;
        }
    }

    /**
     * IndecisiveNumberNegater implements NumberNegater in such a way that it
     * doesn't know what to do when presented with an AmbiguousNumber.
     */
    public class IndecisiveNumberNegater extends ReflectVisitorTest.NumberNegater {
        public void visit(ReflectVisitorTest.CrunchableNumber n) {
        }

        public void visit(ReflectVisitorTest.FudgeableNumber n) {
        }
    }

    /**
     * SomewhatIndecisiveNumberNegater implements NumberNegater in such a way
     * that it knows what to do when presented with a SomewhatAmbiguousNumber.
     */
    public class SomewhatIndecisiveNumberNegater extends ReflectVisitorTest.NumberNegater {
        public void visit(ReflectVisitorTest.FudgeableNumber n) {
        }

        public void visit(ReflectVisitorTest.AmbiguousNumber n) {
            result = -(n.doubleValue());
            assert (result) instanceof Double;
        }
    }

    /**
     * A class inheriting two interfaces, leading to ambiguity.
     */
    public class AmbiguousNumber extends BigDecimal implements ReflectVisitorTest.CrunchableNumber , ReflectVisitorTest.FudgeableNumber {
        AmbiguousNumber() {
            super("0");
        }
    }

    /**
     * A class inheriting a root interface (FudgeableNumber) two different ways,
     * which should not lead to ambiguity in some cases.
     */
    public class SomewhatAmbiguousNumber extends ReflectVisitorTest.AmbiguousNumber implements ReflectVisitorTest.DiceyNumber {}
}

/**
 * End ReflectVisitorTest.java
 */
