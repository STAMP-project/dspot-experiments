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
package org.apache.commons.lang3.mutable;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * JUnit tests.
 *
 * @since 2.2
 * @see MutableBoolean
 */
public class MutableBooleanTest {
    @Test
    public void testCompareTo() {
        final MutableBoolean mutBool = new MutableBoolean(false);
        Assertions.assertEquals(0, mutBool.compareTo(new MutableBoolean(false)));
        Assertions.assertEquals((-1), mutBool.compareTo(new MutableBoolean(true)));
        mutBool.setValue(true);
        Assertions.assertEquals((+1), mutBool.compareTo(new MutableBoolean(false)));
        Assertions.assertEquals(0, mutBool.compareTo(new MutableBoolean(true)));
    }

    @Test
    public void testCompareToNull() {
        final MutableBoolean mutBool = new MutableBoolean(false);
        Assertions.assertThrows(NullPointerException.class, () -> mutBool.compareTo(null));
    }

    // ----------------------------------------------------------------
    @Test
    public void testConstructors() {
        Assertions.assertFalse(new MutableBoolean().booleanValue());
        Assertions.assertTrue(new MutableBoolean(true).booleanValue());
        Assertions.assertFalse(new MutableBoolean(false).booleanValue());
        Assertions.assertTrue(new MutableBoolean(Boolean.TRUE).booleanValue());
        Assertions.assertFalse(new MutableBoolean(Boolean.FALSE).booleanValue());
    }

    @Test
    public void testConstructorNull() {
        Assertions.assertThrows(NullPointerException.class, () -> new MutableBoolean(null));
    }

    @Test
    public void testEquals() {
        final MutableBoolean mutBoolA = new MutableBoolean(false);
        final MutableBoolean mutBoolB = new MutableBoolean(false);
        final MutableBoolean mutBoolC = new MutableBoolean(true);
        Assertions.assertEquals(mutBoolA, mutBoolA);
        Assertions.assertEquals(mutBoolA, mutBoolB);
        Assertions.assertEquals(mutBoolB, mutBoolA);
        Assertions.assertEquals(mutBoolB, mutBoolB);
        Assertions.assertNotEquals(mutBoolA, mutBoolC);
        Assertions.assertNotEquals(mutBoolB, mutBoolC);
        Assertions.assertEquals(mutBoolC, mutBoolC);
        Assertions.assertNotEquals(null, mutBoolA);
        Assertions.assertNotEquals(mutBoolA, Boolean.FALSE);
        Assertions.assertNotEquals("false", mutBoolA);
    }

    @Test
    public void testGetSet() {
        Assertions.assertFalse(new MutableBoolean().booleanValue());
        Assertions.assertEquals(Boolean.FALSE, new MutableBoolean().getValue());
        final MutableBoolean mutBool = new MutableBoolean(false);
        Assertions.assertEquals(Boolean.FALSE, mutBool.toBoolean());
        Assertions.assertFalse(mutBool.booleanValue());
        Assertions.assertTrue(mutBool.isFalse());
        Assertions.assertFalse(mutBool.isTrue());
        mutBool.setValue(Boolean.TRUE);
        Assertions.assertEquals(Boolean.TRUE, mutBool.toBoolean());
        Assertions.assertTrue(mutBool.booleanValue());
        Assertions.assertFalse(mutBool.isFalse());
        Assertions.assertTrue(mutBool.isTrue());
        mutBool.setValue(false);
        Assertions.assertFalse(mutBool.booleanValue());
        mutBool.setValue(true);
        Assertions.assertTrue(mutBool.booleanValue());
        mutBool.setFalse();
        Assertions.assertFalse(mutBool.booleanValue());
        mutBool.setTrue();
        Assertions.assertTrue(mutBool.booleanValue());
    }

    @Test
    public void testSetNull() {
        final MutableBoolean mutBool = new MutableBoolean(false);
        Assertions.assertThrows(NullPointerException.class, () -> mutBool.setValue(null));
    }

    @Test
    public void testHashCode() {
        final MutableBoolean mutBoolA = new MutableBoolean(false);
        final MutableBoolean mutBoolB = new MutableBoolean(false);
        final MutableBoolean mutBoolC = new MutableBoolean(true);
        Assertions.assertEquals(mutBoolA.hashCode(), mutBoolA.hashCode());
        Assertions.assertEquals(mutBoolA.hashCode(), mutBoolB.hashCode());
        Assertions.assertNotEquals(mutBoolA.hashCode(), mutBoolC.hashCode());
        Assertions.assertEquals(mutBoolA.hashCode(), Boolean.FALSE.hashCode());
        Assertions.assertEquals(mutBoolC.hashCode(), Boolean.TRUE.hashCode());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals(Boolean.FALSE.toString(), new MutableBoolean(false).toString());
        Assertions.assertEquals(Boolean.TRUE.toString(), new MutableBoolean(true).toString());
    }
}

