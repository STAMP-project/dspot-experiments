/**
 * Copyright 2018 Confluent Inc.
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
package io.confluent.ksql.util;


import ComparisonExpression.Type.EQUAL;
import Schema.Type.ARRAY;
import Schema.Type.BOOLEAN;
import Schema.Type.FLOAT64;
import Schema.Type.INT32;
import Schema.Type.INT64;
import Schema.Type.MAP;
import Schema.Type.STRING;
import Schema.Type.STRUCT;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Schema.Type;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ComparisonUtilTest {
    private static final List<Type> typesTable = ImmutableList.of(BOOLEAN, INT32, INT64, FLOAT64, STRING, ARRAY, MAP, STRUCT);

    private static final List<List<Boolean>> expectedResults = // Boolean
    // Int
    // BigInt
    // Double
    // String
    // Array
    // Map
    // Struct
    ImmutableList.of(ImmutableList.of(true, false, false, false, false, false, false, false), ImmutableList.of(false, true, true, true, false, false, false, false), ImmutableList.of(false, true, true, true, false, false, false, false), ImmutableList.of(false, true, true, true, false, false, false, false), ImmutableList.of(false, false, false, false, true, false, false, false), ImmutableList.of(false, false, false, false, false, false, false, false), ImmutableList.of(false, false, false, false, false, false, false, false), ImmutableList.of(false, false, false, false, false, false, false, false));

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldAssertTrueForValidComparisons() {
        // When:
        int i = 0;
        int j = 0;
        for (final Schema.Type leftType : ComparisonUtilTest.typesTable) {
            for (final Schema.Type rightType : ComparisonUtilTest.typesTable) {
                if (ComparisonUtilTest.expectedResults.get(i).get(j)) {
                    MatcherAssert.assertThat(ComparisonUtil.isValidComparison(leftType, EQUAL, rightType), CoreMatchers.equalTo(true));
                }
                j++;
            }
            i++;
            j = 0;
        }
    }

    @Test
    public void shouldThrowForInvalidComparisons() {
        // Given:
        expectedException.expect(KsqlException.class);
        // When:
        int i = 0;
        int j = 0;
        for (final Schema.Type leftType : ComparisonUtilTest.typesTable) {
            for (final Schema.Type rightType : ComparisonUtilTest.typesTable) {
                if (!(ComparisonUtilTest.expectedResults.get(i).get(j))) {
                    ComparisonUtil.isValidComparison(leftType, EQUAL, rightType);
                }
                j++;
            }
            i++;
            j = 0;
        }
    }
}

