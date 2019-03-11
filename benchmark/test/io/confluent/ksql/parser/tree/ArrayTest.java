/**
 * Copyright 2019 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.parser.tree;


import SqlType.ARRAY;
import SqlType.BOOLEAN;
import SqlType.STRING;
import com.google.common.testing.EqualsTester;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ArrayTest {
    private static final PrimitiveType SOME_TYPE = PrimitiveType.of(STRING);

    @Test
    public void shouldImplementHashCodeAndEqualsProperly() {
        new EqualsTester().addEqualityGroup(Array.of(ArrayTest.SOME_TYPE), Array.of(ArrayTest.SOME_TYPE)).addEqualityGroup(Array.of(PrimitiveType.of(BOOLEAN))).addEqualityGroup(Map.of(PrimitiveType.of(BOOLEAN))).testEquals();
    }

    @Test
    public void shouldReturnSqlType() {
        MatcherAssert.assertThat(Array.of(ArrayTest.SOME_TYPE).getSqlType(), Matchers.is(ARRAY));
    }

    @Test
    public void shouldReturnValueType() {
        MatcherAssert.assertThat(Array.of(ArrayTest.SOME_TYPE).getItemType(), Matchers.is(ArrayTest.SOME_TYPE));
    }
}

