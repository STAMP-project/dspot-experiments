/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.common.typeinfo;


import BasicTypeInfo.STRING_TYPE_INFO;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.FlinkRuntimeException;
import org.junit.Assert;
import org.junit.Test;

import static BasicTypeInfo.BOOLEAN_TYPE_INFO;
import static BasicTypeInfo.DOUBLE_TYPE_INFO;
import static BasicTypeInfo.STRING_TYPE_INFO;


/**
 * Tests for the {@link TypeHint}.
 */
public class TypeHintTest {
    @Test
    public void testTypeInfoDirect() {
        // simple (non-generic case)
        TypeHint<String> stringInfo1 = new TypeHint<String>() {};
        TypeHint<String> stringInfo2 = new TypeHint<String>() {};
        Assert.assertEquals(STRING_TYPE_INFO, stringInfo1.getTypeInfo());
        Assert.assertTrue(((stringInfo1.hashCode()) == (stringInfo2.hashCode())));
        Assert.assertTrue(stringInfo1.equals(stringInfo2));
        Assert.assertTrue(stringInfo1.toString().equals(stringInfo2.toString()));
        // generic case
        TypeHint<Tuple3<String, Double, Boolean>> generic = new TypeHint<Tuple3<String, Double, Boolean>>() {};
        TypeInformation<Tuple3<String, Double, Boolean>> tupleInfo = new org.apache.flink.api.java.typeutils.TupleTypeInfo(STRING_TYPE_INFO, DOUBLE_TYPE_INFO, BOOLEAN_TYPE_INFO);
        Assert.assertEquals(tupleInfo, generic.getTypeInfo());
    }

    @Test
    public <T> void testWithGenericParameter() {
        try {
            new TypeHint<T>() {};
            Assert.fail();
        } catch (FlinkRuntimeException ignored) {
        }
        // this works, because "List" goes to the GenericType (blackbox) which does
        // not care about generic parametrization
        new TypeHint<java.util.List<T>>() {};
    }
}

