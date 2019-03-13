/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link TernaryBoolean} class.
 */
public class TernaryBooleanTest {
    @Test
    public void testWithDefault() {
        Assert.assertTrue(TernaryBoolean.TRUE.getOrDefault(true));
        Assert.assertTrue(TernaryBoolean.TRUE.getOrDefault(false));
        Assert.assertFalse(TernaryBoolean.FALSE.getOrDefault(true));
        Assert.assertFalse(TernaryBoolean.FALSE.getOrDefault(false));
        Assert.assertTrue(TernaryBoolean.UNDEFINED.getOrDefault(true));
        Assert.assertFalse(TernaryBoolean.UNDEFINED.getOrDefault(false));
    }

    @Test
    public void testResolveUndefined() {
        Assert.assertEquals(TernaryBoolean.TRUE, TernaryBoolean.TRUE.resolveUndefined(true));
        Assert.assertEquals(TernaryBoolean.TRUE, TernaryBoolean.TRUE.resolveUndefined(false));
        Assert.assertEquals(TernaryBoolean.FALSE, TernaryBoolean.FALSE.resolveUndefined(true));
        Assert.assertEquals(TernaryBoolean.FALSE, TernaryBoolean.FALSE.resolveUndefined(false));
        Assert.assertEquals(TernaryBoolean.TRUE, TernaryBoolean.UNDEFINED.resolveUndefined(true));
        Assert.assertEquals(TernaryBoolean.FALSE, TernaryBoolean.UNDEFINED.resolveUndefined(false));
    }

    @Test
    public void testToBoolean() {
        Assert.assertTrue(((Boolean.TRUE) == (TernaryBoolean.TRUE.getAsBoolean())));
        Assert.assertTrue(((Boolean.FALSE) == (TernaryBoolean.FALSE.getAsBoolean())));
        Assert.assertNull(TernaryBoolean.UNDEFINED.getAsBoolean());
    }

    @Test
    public void testFromBoolean() {
        Assert.assertEquals(TernaryBoolean.TRUE, TernaryBoolean.fromBoolean(true));
        Assert.assertEquals(TernaryBoolean.FALSE, TernaryBoolean.fromBoolean(false));
    }

    @Test
    public void testFromBoxedBoolean() {
        Assert.assertEquals(TernaryBoolean.TRUE, TernaryBoolean.fromBoxedBoolean(Boolean.TRUE));
        Assert.assertEquals(TernaryBoolean.FALSE, TernaryBoolean.fromBoxedBoolean(Boolean.FALSE));
        Assert.assertEquals(TernaryBoolean.UNDEFINED, TernaryBoolean.fromBoxedBoolean(null));
    }
}

