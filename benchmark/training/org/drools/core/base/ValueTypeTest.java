/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.base;


import ValueType.BOOLEAN_TYPE;
import ValueType.BYTE_TYPE;
import ValueType.DOUBLE_TYPE;
import ValueType.FLOAT_TYPE;
import ValueType.INTEGER_TYPE;
import ValueType.LONG_TYPE;
import ValueType.PBOOLEAN_TYPE;
import ValueType.PBYTE_TYPE;
import ValueType.PDOUBLE_TYPE;
import ValueType.PFLOAT_TYPE;
import ValueType.PINTEGER_TYPE;
import ValueType.PLONG_TYPE;
import ValueType.PSHORT_TYPE;
import ValueType.SHORT_TYPE;
import org.junit.Assert;
import org.junit.Test;


public class ValueTypeTest {
    @Test
    public void testIsBoolean() {
        Assert.assertTrue(BOOLEAN_TYPE.isBoolean());
        Assert.assertTrue(PBOOLEAN_TYPE.isBoolean());
    }

    @Test
    public void testIsNumber() {
        Assert.assertTrue(PBYTE_TYPE.isNumber());
        Assert.assertTrue(PSHORT_TYPE.isNumber());
        Assert.assertTrue(PINTEGER_TYPE.isNumber());
        Assert.assertTrue(PLONG_TYPE.isNumber());
        Assert.assertTrue(PFLOAT_TYPE.isNumber());
        Assert.assertTrue(PDOUBLE_TYPE.isNumber());
        Assert.assertTrue(BYTE_TYPE.isNumber());
        Assert.assertTrue(SHORT_TYPE.isNumber());
        Assert.assertTrue(INTEGER_TYPE.isNumber());
        Assert.assertTrue(LONG_TYPE.isNumber());
        Assert.assertTrue(FLOAT_TYPE.isNumber());
        Assert.assertTrue(DOUBLE_TYPE.isNumber());
    }
}

