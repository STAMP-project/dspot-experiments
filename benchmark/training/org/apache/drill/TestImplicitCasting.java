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
package org.apache.drill;


import TypeProtos.MinorType;
import TypeProtos.MinorType.TIME;
import TypeProtos.MinorType.TIMESTAMP;
import java.util.List;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.exec.resolver.TypeCastRules;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SqlTest.class)
public class TestImplicitCasting {
    @Test
    public void testTimeStampAndTime() {
        final List<TypeProtos.MinorType> inputTypes = Lists.newArrayList();
        inputTypes.add(TIME);
        inputTypes.add(TIMESTAMP);
        final TypeProtos.MinorType result = TypeCastRules.getLeastRestrictiveType(inputTypes);
        Assert.assertEquals(result, TIME);
    }
}

