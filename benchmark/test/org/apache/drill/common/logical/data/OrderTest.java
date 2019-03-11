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
package org.apache.drill.common.logical.data;


import NullDirection.FIRST;
import NullDirection.LAST;
import NullDirection.UNSPECIFIED;
import RelFieldCollation.Direction.ASCENDING;
import RelFieldCollation.Direction.DESCENDING;
import org.apache.calcite.rel.RelFieldCollation.Direction;
import org.apache.calcite.rel.RelFieldCollation.NullDirection;
import org.apache.drill.common.exceptions.DrillRuntimeException;
import org.apache.drill.common.expression.LogicalExpression;
import org.apache.drill.common.logical.data.Order.Ordering;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OrderTest {
    // ////////
    // Order.Ordering tests:
    // "Round trip" tests that strings from output work as input:
    @Test
    public void test_Ordering_roundTripAscAndNullsFirst() {
        Ordering src = new Ordering(Direction.ASCENDING, null, NullDirection.FIRST);
        Ordering reconstituted = new Ordering(src.getDirection(), ((LogicalExpression) (null)), src.getNullDirection());
        Assert.assertThat(reconstituted.getDirection(), CoreMatchers.equalTo(ASCENDING));
        Assert.assertThat(reconstituted.getNullDirection(), CoreMatchers.equalTo(FIRST));
    }

    @Test
    public void test_Ordering_roundTripDescAndNullsLast() {
        Ordering src = new Ordering(Direction.DESCENDING, null, NullDirection.LAST);
        Ordering reconstituted = new Ordering(src.getDirection(), ((LogicalExpression) (null)), src.getNullDirection());
        Assert.assertThat(reconstituted.getDirection(), CoreMatchers.equalTo(DESCENDING));
        Assert.assertThat(reconstituted.getNullDirection(), CoreMatchers.equalTo(LAST));
    }

    @Test
    public void test_Ordering_roundTripDescAndNullsUnspecified() {
        Ordering src = new Ordering(Direction.DESCENDING, null, NullDirection.UNSPECIFIED);
        Ordering reconstituted = new Ordering(src.getDirection(), ((LogicalExpression) (null)), src.getNullDirection());
        Assert.assertThat(reconstituted.getDirection(), CoreMatchers.equalTo(DESCENDING));
        Assert.assertThat(reconstituted.getNullDirection(), CoreMatchers.equalTo(UNSPECIFIED));
    }

    // Basic input validation:
    // (Currently.)
    @Test(expected = DrillRuntimeException.class)
    public void test_Ordering_garbageOrderRejected() {
        new Ordering("AS CE ND IN G", null, null);
    }

    // (Currently.)
    @Test(expected = DrillRuntimeException.class)
    public void test_Ordering_garbageNullOrderingRejected() {
        new Ordering(null, null, "HIGH");
    }

    // Defaults-value/null-strings test:
    @Test
    public void testOrdering_nullStrings() {
        Ordering ordering = new Ordering(((String) (null)), ((LogicalExpression) (null)), null);
        Assert.assertThat(ordering.getDirection(), CoreMatchers.equalTo(ASCENDING));
        Assert.assertThat(ordering.getNullDirection(), CoreMatchers.equalTo(RelFieldCollation.NullDirection.UNSPECIFIED));
        Assert.assertThat(ordering.getOrder(), CoreMatchers.equalTo("ASC"));
    }
}

