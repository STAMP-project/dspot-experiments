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


/**
 * JUnit tests.
 *
 * @see MutableShort
 */
public class MutableObjectTest {
    // ----------------------------------------------------------------
    @Test
    public void testConstructors() {
        Assertions.assertNull(new MutableObject<String>().getValue());
        final Integer i = Integer.valueOf(6);
        Assertions.assertSame(i, new MutableObject<>(i).getValue());
        Assertions.assertSame("HI", new MutableObject<>("HI").getValue());
        Assertions.assertSame(null, new MutableObject<>(null).getValue());
    }

    @Test
    public void testGetSet() {
        final MutableObject<String> mutNum = new MutableObject<>();
        Assertions.assertNull(new MutableObject<>().getValue());
        mutNum.setValue("HELLO");
        Assertions.assertSame("HELLO", mutNum.getValue());
        mutNum.setValue(null);
        Assertions.assertSame(null, mutNum.getValue());
    }

    @Test
    public void testEquals() {
        final MutableObject<String> mutNumA = new MutableObject<>("ALPHA");
        final MutableObject<String> mutNumB = new MutableObject<>("ALPHA");
        final MutableObject<String> mutNumC = new MutableObject<>("BETA");
        final MutableObject<String> mutNumD = new MutableObject<>(null);
        Assertions.assertEquals(mutNumA, mutNumA);
        Assertions.assertEquals(mutNumA, mutNumB);
        Assertions.assertEquals(mutNumB, mutNumA);
        Assertions.assertEquals(mutNumB, mutNumB);
        Assertions.assertNotEquals(mutNumA, mutNumC);
        Assertions.assertNotEquals(mutNumB, mutNumC);
        Assertions.assertEquals(mutNumC, mutNumC);
        Assertions.assertNotEquals(mutNumA, mutNumD);
        Assertions.assertEquals(mutNumD, mutNumD);
        Assertions.assertNotEquals(null, mutNumA);
        Assertions.assertNotEquals(mutNumA, new Object());
        Assertions.assertNotEquals("0", mutNumA);
    }

    @Test
    public void testHashCode() {
        final MutableObject<String> mutNumA = new MutableObject<>("ALPHA");
        final MutableObject<String> mutNumB = new MutableObject<>("ALPHA");
        final MutableObject<String> mutNumC = new MutableObject<>("BETA");
        final MutableObject<String> mutNumD = new MutableObject<>(null);
        Assertions.assertEquals(mutNumA.hashCode(), mutNumA.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), mutNumB.hashCode());
        Assertions.assertNotEquals(mutNumA.hashCode(), mutNumC.hashCode());
        Assertions.assertNotEquals(mutNumA.hashCode(), mutNumD.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), "ALPHA".hashCode());
        Assertions.assertEquals(0, mutNumD.hashCode());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("HI", new MutableObject<>("HI").toString());
        Assertions.assertEquals("10.0", new MutableObject<>(Double.valueOf(10)).toString());
        Assertions.assertEquals("null", new MutableObject<>(null).toString());
    }
}

