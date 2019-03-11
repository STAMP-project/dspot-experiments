/**
 * Copyright (C) 2012 RoboVM AB
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
package org.robovm.rt.bro;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Bits}.
 */
public class BitsTest {
    public static final class FlagsWithZero extends Bits<BitsTest.FlagsWithZero> {
        public static final BitsTest.FlagsWithZero V0 = new BitsTest.FlagsWithZero(0);

        public static final BitsTest.FlagsWithZero V1 = new BitsTest.FlagsWithZero((1 << 0));

        public static final BitsTest.FlagsWithZero V2 = new BitsTest.FlagsWithZero((1 << 1));

        public static final BitsTest.FlagsWithZero V4 = new BitsTest.FlagsWithZero((1 << 2));

        public static final BitsTest.FlagsWithZero V8 = new BitsTest.FlagsWithZero((1 << 3));

        private static final BitsTest.FlagsWithZero[] VALUES = _values(BitsTest.FlagsWithZero.class);

        private FlagsWithZero(long value) {
            super(value);
        }

        private FlagsWithZero(long value, long mask) {
            super(value, mask);
        }

        protected BitsTest.FlagsWithZero wrap(long value, long mask) {
            return new BitsTest.FlagsWithZero(value, mask);
        }

        protected BitsTest.FlagsWithZero[] _values() {
            return BitsTest.FlagsWithZero.VALUES;
        }
    }

    public static final class FlagsNoZero extends Bits<BitsTest.FlagsNoZero> {
        public static final BitsTest.FlagsNoZero V1 = new BitsTest.FlagsNoZero((1 << 0));

        public static final BitsTest.FlagsNoZero V2 = new BitsTest.FlagsNoZero((1 << 1));

        public static final BitsTest.FlagsNoZero V4 = new BitsTest.FlagsNoZero((1 << 2));

        public static final BitsTest.FlagsNoZero V8 = new BitsTest.FlagsNoZero((1 << 3));

        private static final BitsTest.FlagsNoZero[] VALUES = _values(BitsTest.FlagsNoZero.class);

        private FlagsNoZero(long value) {
            super(value);
        }

        private FlagsNoZero(long value, long mask) {
            super(value, mask);
        }

        protected BitsTest.FlagsNoZero wrap(long value, long mask) {
            return new BitsTest.FlagsNoZero(value, mask);
        }

        protected BitsTest.FlagsNoZero[] _values() {
            return BitsTest.FlagsNoZero.VALUES;
        }
    }

    public static final class FlagsAndValues extends Bits<BitsTest.FlagsAndValues> {
        public static final BitsTest.FlagsAndValues V1 = new BitsTest.FlagsAndValues((1 << 0));

        public static final BitsTest.FlagsAndValues V2 = new BitsTest.FlagsAndValues((1 << 1));

        public static final BitsTest.FlagsAndValues V4 = new BitsTest.FlagsAndValues((1 << 2));

        public static final BitsTest.FlagsAndValues V8 = new BitsTest.FlagsAndValues((1 << 3));

        public static final BitsTest.FlagsAndValues O0 = new BitsTest.FlagsAndValues((0 << 4), 48);

        public static final BitsTest.FlagsAndValues O1 = new BitsTest.FlagsAndValues((1 << 4), 48);

        public static final BitsTest.FlagsAndValues O2 = new BitsTest.FlagsAndValues((2 << 4), 48);

        public static final BitsTest.FlagsAndValues O3 = new BitsTest.FlagsAndValues((3 << 4), 48);

        public static final BitsTest.FlagsAndValues OMask = new BitsTest.FlagsAndValues(48, 0);

        public static final BitsTest.FlagsAndValues Unknown = new BitsTest.FlagsAndValues(4294967295L);

        private static final BitsTest.FlagsAndValues[] VALUES = _values(BitsTest.FlagsAndValues.class);

        private FlagsAndValues(long value) {
            super(value);
        }

        private FlagsAndValues(long value, long mask) {
            super(value, mask);
        }

        protected BitsTest.FlagsAndValues wrap(long value, long mask) {
            return new BitsTest.FlagsAndValues(value, mask);
        }

        protected BitsTest.FlagsAndValues[] _values() {
            return BitsTest.FlagsAndValues.VALUES;
        }
    }

    @Test
    public void testValues() throws Exception {
        Method m = Bits.class.getDeclaredMethod("_values", Class.class);
        m.setAccessible(true);
        BitsTest.FlagsWithZero[] positionValues = ((BitsTest.FlagsWithZero[]) (m.invoke(null, BitsTest.FlagsWithZero.class)));
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsWithZero.V0, BitsTest.FlagsWithZero.V1, BitsTest.FlagsWithZero.V2, BitsTest.FlagsWithZero.V4, BitsTest.FlagsWithZero.V8), BitsTest.set(positionValues));
    }

    @Test
    public void testSimpleFlagsWithZero() {
        BitsTest.FlagsWithZero pos = BitsTest.FlagsWithZero.V0;
        Assert.assertEquals(0, value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsWithZero.V0), asSet());
        Assert.assertEquals("FlagsWithZero(0x0 = V0(0x0))", pos.toString());
        pos = pos.set(BitsTest.FlagsWithZero.V1).set(BitsTest.FlagsWithZero.V4);
        Assert.assertEquals((1 | 4), value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsWithZero.V1, BitsTest.FlagsWithZero.V4), asSet());
        Assert.assertEquals("FlagsWithZero(0x5 = V1(0x1) | V4(0x4))", pos.toString());
        pos = pos.clear(BitsTest.FlagsWithZero.V1);
        Assert.assertEquals(4, value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsWithZero.V4), asSet());
        Assert.assertEquals("FlagsWithZero(0x4 = V4(0x4))", pos.toString());
        pos = pos.clear(BitsTest.FlagsWithZero.V4);
        Assert.assertEquals(0, value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsWithZero.V0), asSet());
        Assert.assertEquals("FlagsWithZero(0x0 = V0(0x0))", pos.toString());
        pos = pos.set(BitsTest.FlagsWithZero.V8);
        Assert.assertEquals(8, value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsWithZero.V8), asSet());
        Assert.assertEquals("FlagsWithZero(0x8 = V8(0x8))", pos.toString());
        pos = pos.clear(BitsTest.FlagsWithZero.V4);
        Assert.assertEquals(8, value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsWithZero.V8), asSet());
        Assert.assertEquals("FlagsWithZero(0x8 = V8(0x8))", pos.toString());
        pos = pos.clear(BitsTest.FlagsWithZero.V0);
        Assert.assertEquals(8, value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsWithZero.V8), asSet());
        Assert.assertEquals("FlagsWithZero(0x8 = V8(0x8))", pos.toString());
        pos = pos.set(BitsTest.FlagsWithZero.V0);
        Assert.assertEquals(0, value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsWithZero.V0), asSet());
        Assert.assertEquals("FlagsWithZero(0x0 = V0(0x0))", pos.toString());
    }

    @Test
    public void testFlagsNoZero() {
        BitsTest.FlagsNoZero pos = BitsTest.FlagsNoZero.V1;
        pos = pos.set(BitsTest.FlagsNoZero.V1).set(BitsTest.FlagsNoZero.V4);
        Assert.assertEquals((1 | 4), value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsNoZero.V1, BitsTest.FlagsNoZero.V4), asSet());
        Assert.assertEquals("FlagsNoZero(0x5 = V1(0x1) | V4(0x4))", pos.toString());
        pos = clear(BitsTest.FlagsNoZero.V1);
        Assert.assertEquals(4, value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsNoZero.V4), asSet());
        Assert.assertEquals("FlagsNoZero(0x4 = V4(0x4))", pos.toString());
        pos = clear(BitsTest.FlagsNoZero.V4);
        Assert.assertEquals(0, value());
        Assert.assertEquals("FlagsNoZero(0x0 = 0x0)", pos.toString());
        pos = pos.set(BitsTest.FlagsNoZero.V8);
        Assert.assertEquals(8, value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsNoZero.V8), asSet());
        Assert.assertEquals("FlagsNoZero(0x8 = V8(0x8))", pos.toString());
        pos = clear(BitsTest.FlagsNoZero.V4);
        Assert.assertEquals(8, value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsNoZero.V8), asSet());
        Assert.assertEquals("FlagsNoZero(0x8 = V8(0x8))", pos.toString());
    }

    @Test
    public void testFlagsAndValues() {
        BitsTest.FlagsAndValues pos = BitsTest.FlagsAndValues.V1;
        pos = set(BitsTest.FlagsAndValues.V1).set(BitsTest.FlagsAndValues.V4);
        Assert.assertEquals((1 | 4), value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsAndValues.V1, BitsTest.FlagsAndValues.V4, BitsTest.FlagsAndValues.O0), asSet());
        Assert.assertEquals("FlagsAndValues(0x5 = V1(0x1) | V4(0x4) | O0(0x0))", pos.toString());
        pos = pos.set(BitsTest.FlagsAndValues.O1);
        Assert.assertEquals((((1 << 4) | 1) | 4), value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsAndValues.V1, BitsTest.FlagsAndValues.V4, BitsTest.FlagsAndValues.O1), asSet());
        Assert.assertEquals("FlagsAndValues(0x15 = V1(0x1) | V4(0x4) | O1(0x10))", pos.toString());
        pos = pos.set(BitsTest.FlagsAndValues.O2);
        Assert.assertEquals((((2 << 4) | 1) | 4), value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsAndValues.V1, BitsTest.FlagsAndValues.V4, BitsTest.FlagsAndValues.O2), asSet());
        Assert.assertEquals("FlagsAndValues(0x25 = V1(0x1) | V4(0x4) | O2(0x20))", pos.toString());
        pos = pos.set(BitsTest.FlagsAndValues.Unknown);
        Assert.assertEquals(4294967295L, value());
        Assert.assertEquals(BitsTest.set(BitsTest.FlagsAndValues.Unknown), asSet());
        Assert.assertEquals("FlagsAndValues(0xffffffff = Unknown(0xffffffff))", pos.toString());
    }
}

