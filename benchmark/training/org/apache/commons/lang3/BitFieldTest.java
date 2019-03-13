/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Class to test BitField functionality
 */
public class BitFieldTest {
    private static final BitField bf_multi = new BitField(16256);

    private static final BitField bf_single = new BitField(16384);

    private static final BitField bf_zero = new BitField(0);

    /**
     * test the getValue() method
     */
    @Test
    public void testGetValue() {
        Assertions.assertEquals(BitFieldTest.bf_multi.getValue((-1)), 127);
        Assertions.assertEquals(BitFieldTest.bf_multi.getValue(0), 0);
        Assertions.assertEquals(BitFieldTest.bf_single.getValue((-1)), 1);
        Assertions.assertEquals(BitFieldTest.bf_single.getValue(0), 0);
        Assertions.assertEquals(BitFieldTest.bf_zero.getValue((-1)), 0);
        Assertions.assertEquals(BitFieldTest.bf_zero.getValue(0), 0);
    }

    /**
     * test the getShortValue() method
     */
    @Test
    public void testGetShortValue() {
        Assertions.assertEquals(BitFieldTest.bf_multi.getShortValue(((short) (-1))), ((short) (127)));
        Assertions.assertEquals(BitFieldTest.bf_multi.getShortValue(((short) (0))), ((short) (0)));
        Assertions.assertEquals(BitFieldTest.bf_single.getShortValue(((short) (-1))), ((short) (1)));
        Assertions.assertEquals(BitFieldTest.bf_single.getShortValue(((short) (0))), ((short) (0)));
        Assertions.assertEquals(BitFieldTest.bf_zero.getShortValue(((short) (-1))), ((short) (0)));
        Assertions.assertEquals(BitFieldTest.bf_zero.getShortValue(((short) (0))), ((short) (0)));
    }

    /**
     * test the getRawValue() method
     */
    @Test
    public void testGetRawValue() {
        Assertions.assertEquals(BitFieldTest.bf_multi.getRawValue((-1)), 16256);
        Assertions.assertEquals(BitFieldTest.bf_multi.getRawValue(0), 0);
        Assertions.assertEquals(BitFieldTest.bf_single.getRawValue((-1)), 16384);
        Assertions.assertEquals(BitFieldTest.bf_single.getRawValue(0), 0);
        Assertions.assertEquals(BitFieldTest.bf_zero.getRawValue((-1)), 0);
        Assertions.assertEquals(BitFieldTest.bf_zero.getRawValue(0), 0);
    }

    /**
     * test the getShortRawValue() method
     */
    @Test
    public void testGetShortRawValue() {
        Assertions.assertEquals(BitFieldTest.bf_multi.getShortRawValue(((short) (-1))), ((short) (16256)));
        Assertions.assertEquals(BitFieldTest.bf_multi.getShortRawValue(((short) (0))), ((short) (0)));
        Assertions.assertEquals(BitFieldTest.bf_single.getShortRawValue(((short) (-1))), ((short) (16384)));
        Assertions.assertEquals(BitFieldTest.bf_single.getShortRawValue(((short) (0))), ((short) (0)));
        Assertions.assertEquals(BitFieldTest.bf_zero.getShortRawValue(((short) (-1))), ((short) (0)));
        Assertions.assertEquals(BitFieldTest.bf_zero.getShortRawValue(((short) (0))), ((short) (0)));
    }

    /**
     * test the isSet() method
     */
    @Test
    public void testIsSet() {
        Assertions.assertFalse(BitFieldTest.bf_multi.isSet(0));
        Assertions.assertFalse(BitFieldTest.bf_zero.isSet(0));
        for (int j = 128; j <= 16256; j += 128) {
            Assertions.assertTrue(BitFieldTest.bf_multi.isSet(j));
        }
        for (int j = 128; j <= 16256; j += 128) {
            Assertions.assertFalse(BitFieldTest.bf_zero.isSet(j));
        }
        Assertions.assertFalse(BitFieldTest.bf_single.isSet(0));
        Assertions.assertTrue(BitFieldTest.bf_single.isSet(16384));
    }

    /**
     * test the isAllSet() method
     */
    @Test
    public void testIsAllSet() {
        for (int j = 0; j < 16256; j += 128) {
            Assertions.assertFalse(BitFieldTest.bf_multi.isAllSet(j));
            Assertions.assertTrue(BitFieldTest.bf_zero.isAllSet(j));
        }
        Assertions.assertTrue(BitFieldTest.bf_multi.isAllSet(16256));
        Assertions.assertFalse(BitFieldTest.bf_single.isAllSet(0));
        Assertions.assertTrue(BitFieldTest.bf_single.isAllSet(16384));
    }

    /**
     * test the setValue() method
     */
    @Test
    public void testSetValue() {
        for (int j = 0; j < 128; j++) {
            Assertions.assertEquals(BitFieldTest.bf_multi.getValue(BitFieldTest.bf_multi.setValue(0, j)), j);
            Assertions.assertEquals(BitFieldTest.bf_multi.setValue(0, j), (j << 7));
        }
        for (int j = 0; j < 128; j++) {
            Assertions.assertEquals(BitFieldTest.bf_zero.getValue(BitFieldTest.bf_zero.setValue(0, j)), 0);
            Assertions.assertEquals(BitFieldTest.bf_zero.setValue(0, j), 0);
        }
        // verify that excess bits are stripped off
        Assertions.assertEquals(BitFieldTest.bf_multi.setValue(16256, 128), 0);
        for (int j = 0; j < 2; j++) {
            Assertions.assertEquals(BitFieldTest.bf_single.getValue(BitFieldTest.bf_single.setValue(0, j)), j);
            Assertions.assertEquals(BitFieldTest.bf_single.setValue(0, j), (j << 14));
        }
        // verify that excess bits are stripped off
        Assertions.assertEquals(BitFieldTest.bf_single.setValue(16384, 2), 0);
    }

    /**
     * test the setShortValue() method
     */
    @Test
    public void testSetShortValue() {
        for (int j = 0; j < 128; j++) {
            Assertions.assertEquals(BitFieldTest.bf_multi.getShortValue(BitFieldTest.bf_multi.setShortValue(((short) (0)), ((short) (j)))), ((short) (j)));
            Assertions.assertEquals(BitFieldTest.bf_multi.setShortValue(((short) (0)), ((short) (j))), ((short) (j << 7)));
        }
        for (int j = 0; j < 128; j++) {
            Assertions.assertEquals(BitFieldTest.bf_zero.getShortValue(BitFieldTest.bf_zero.setShortValue(((short) (0)), ((short) (j)))), ((short) (0)));
            Assertions.assertEquals(BitFieldTest.bf_zero.setShortValue(((short) (0)), ((short) (j))), ((short) (0)));
        }
        // verify that excess bits are stripped off
        Assertions.assertEquals(BitFieldTest.bf_multi.setShortValue(((short) (16256)), ((short) (128))), ((short) (0)));
        for (int j = 0; j < 2; j++) {
            Assertions.assertEquals(BitFieldTest.bf_single.getShortValue(BitFieldTest.bf_single.setShortValue(((short) (0)), ((short) (j)))), ((short) (j)));
            Assertions.assertEquals(BitFieldTest.bf_single.setShortValue(((short) (0)), ((short) (j))), ((short) (j << 14)));
        }
        // verify that excess bits are stripped off
        Assertions.assertEquals(BitFieldTest.bf_single.setShortValue(((short) (16384)), ((short) (2))), ((short) (0)));
    }

    @Test
    public void testByte() {
        Assertions.assertEquals(0, new BitField(0).setByteBoolean(((byte) (0)), true));
        Assertions.assertEquals(1, new BitField(1).setByteBoolean(((byte) (0)), true));
        Assertions.assertEquals(2, new BitField(2).setByteBoolean(((byte) (0)), true));
        Assertions.assertEquals(4, new BitField(4).setByteBoolean(((byte) (0)), true));
        Assertions.assertEquals(8, new BitField(8).setByteBoolean(((byte) (0)), true));
        Assertions.assertEquals(16, new BitField(16).setByteBoolean(((byte) (0)), true));
        Assertions.assertEquals(32, new BitField(32).setByteBoolean(((byte) (0)), true));
        Assertions.assertEquals(64, new BitField(64).setByteBoolean(((byte) (0)), true));
        Assertions.assertEquals((-128), new BitField(128).setByteBoolean(((byte) (0)), true));
        Assertions.assertEquals(1, new BitField(0).setByteBoolean(((byte) (1)), false));
        Assertions.assertEquals(0, new BitField(1).setByteBoolean(((byte) (1)), false));
        Assertions.assertEquals(0, new BitField(2).setByteBoolean(((byte) (2)), false));
        Assertions.assertEquals(0, new BitField(4).setByteBoolean(((byte) (4)), false));
        Assertions.assertEquals(0, new BitField(8).setByteBoolean(((byte) (8)), false));
        Assertions.assertEquals(0, new BitField(16).setByteBoolean(((byte) (16)), false));
        Assertions.assertEquals(0, new BitField(32).setByteBoolean(((byte) (32)), false));
        Assertions.assertEquals(0, new BitField(64).setByteBoolean(((byte) (64)), false));
        Assertions.assertEquals(0, new BitField(128).setByteBoolean(((byte) (128)), false));
        Assertions.assertEquals((-2), new BitField(1).setByteBoolean(((byte) (255)), false));
        final byte clearedBit = new BitField(64).setByteBoolean(((byte) (-63)), false);
        Assertions.assertFalse(new BitField(64).isSet(clearedBit));
    }

    /**
     * test the clear() method
     */
    @Test
    public void testClear() {
        Assertions.assertEquals(BitFieldTest.bf_multi.clear((-1)), -16257);
        Assertions.assertEquals(BitFieldTest.bf_single.clear((-1)), -16385);
        Assertions.assertEquals(BitFieldTest.bf_zero.clear((-1)), -1);
    }

    /**
     * test the clearShort() method
     */
    @Test
    public void testClearShort() {
        Assertions.assertEquals(BitFieldTest.bf_multi.clearShort(((short) (-1))), ((short) (49279)));
        Assertions.assertEquals(BitFieldTest.bf_single.clearShort(((short) (-1))), ((short) (49151)));
        Assertions.assertEquals(BitFieldTest.bf_zero.clearShort(((short) (-1))), ((short) (65535)));
    }

    /**
     * test the set() method
     */
    @Test
    public void testSet() {
        Assertions.assertEquals(BitFieldTest.bf_multi.set(0), 16256);
        Assertions.assertEquals(BitFieldTest.bf_single.set(0), 16384);
        Assertions.assertEquals(BitFieldTest.bf_zero.set(0), 0);
    }

    /**
     * test the setShort() method
     */
    @Test
    public void testSetShort() {
        Assertions.assertEquals(BitFieldTest.bf_multi.setShort(((short) (0))), ((short) (16256)));
        Assertions.assertEquals(BitFieldTest.bf_single.setShort(((short) (0))), ((short) (16384)));
        Assertions.assertEquals(BitFieldTest.bf_zero.setShort(((short) (0))), ((short) (0)));
    }

    /**
     * test the setBoolean() method
     */
    @Test
    public void testSetBoolean() {
        Assertions.assertEquals(BitFieldTest.bf_multi.set(0), BitFieldTest.bf_multi.setBoolean(0, true));
        Assertions.assertEquals(BitFieldTest.bf_single.set(0), BitFieldTest.bf_single.setBoolean(0, true));
        Assertions.assertEquals(BitFieldTest.bf_zero.set(0), BitFieldTest.bf_zero.setBoolean(0, true));
        Assertions.assertEquals(BitFieldTest.bf_multi.clear((-1)), BitFieldTest.bf_multi.setBoolean((-1), false));
        Assertions.assertEquals(BitFieldTest.bf_single.clear((-1)), BitFieldTest.bf_single.setBoolean((-1), false));
        Assertions.assertEquals(BitFieldTest.bf_zero.clear((-1)), BitFieldTest.bf_zero.setBoolean((-1), false));
    }

    /**
     * test the setShortBoolean() method
     */
    @Test
    public void testSetShortBoolean() {
        Assertions.assertEquals(BitFieldTest.bf_multi.setShort(((short) (0))), BitFieldTest.bf_multi.setShortBoolean(((short) (0)), true));
        Assertions.assertEquals(BitFieldTest.bf_single.setShort(((short) (0))), BitFieldTest.bf_single.setShortBoolean(((short) (0)), true));
        Assertions.assertEquals(BitFieldTest.bf_zero.setShort(((short) (0))), BitFieldTest.bf_zero.setShortBoolean(((short) (0)), true));
        Assertions.assertEquals(BitFieldTest.bf_multi.clearShort(((short) (-1))), BitFieldTest.bf_multi.setShortBoolean(((short) (-1)), false));
        Assertions.assertEquals(BitFieldTest.bf_single.clearShort(((short) (-1))), BitFieldTest.bf_single.setShortBoolean(((short) (-1)), false));
        Assertions.assertEquals(BitFieldTest.bf_zero.clearShort(((short) (-1))), BitFieldTest.bf_zero.setShortBoolean(((short) (-1)), false));
    }
}

