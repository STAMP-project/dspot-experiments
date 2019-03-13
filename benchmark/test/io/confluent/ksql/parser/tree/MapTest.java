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


import SqlType.BOOLEAN;
import SqlType.DOUBLE;
import SqlType.MAP;
import com.google.common.testing.EqualsTester;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class MapTest {
    private static final PrimitiveType SOME_TYPE = PrimitiveType.of(DOUBLE);

    @Test
    public void shouldImplementHashCodeAndEqualsProperly() {
        new EqualsTester().addEqualityGroup(Map.of(MapTest.SOME_TYPE), Map.of(MapTest.SOME_TYPE)).addEqualityGroup(Map.of(PrimitiveType.of(BOOLEAN))).addEqualityGroup(Array.of(PrimitiveType.of(BOOLEAN))).testEquals();
    }

    @Test
    public void shouldReturnSqlType() {
        MatcherAssert.assertThat(Map.of(MapTest.SOME_TYPE).getSqlType(), Matchers.is(MAP));
    }

    @Test
    public void shouldReturnValueType() {
        MatcherAssert.assertThat(Map.of(MapTest.SOME_TYPE).getValueType(), Matchers.is(MapTest.SOME_TYPE));
    }
}

