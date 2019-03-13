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
package org.apache.drill.exec.physical.impl.xsort.managed;


import AbstractBase.MAX_ALLOCATION;
import CoreOperatorType.EXTERNAL_SORT_VALUE;
import Direction.ASCENDING;
import Direction.DESCENDING;
import ExternalSort.DEFAULT_SORT_ALLOCATION;
import NullDirection.FIRST;
import NullDirection.LAST;
import NullDirection.UNSPECIFIED;
import Ordering.NULLS_FIRST;
import Ordering.NULLS_LAST;
import Ordering.NULLS_UNSPECIFIED;
import Ordering.ORDER_ASC;
import Ordering.ORDER_ASCENDING;
import Ordering.ORDER_DESC;
import Ordering.ORDER_DESCENDING;
import SelectionVectorMode.FOUR_BYTE;
import Types.LATE_BIND_TYPE;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.common.exceptions.DrillRuntimeException;
import org.apache.drill.common.expression.FieldReference;
import org.apache.drill.common.logical.data.Order.Ordering;
import org.apache.drill.exec.physical.config.ExternalSort;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.DrillTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(OperatorTest.class)
public class TestExternalSortExec extends DrillTest {
    @Test
    public void testFieldReference() {
        // Misnomer: the reference must be unquoted.
        FieldReference expr = FieldReference.getWithQuotedRef("foo");
        Assert.assertEquals(LATE_BIND_TYPE, expr.getMajorType());
        Assert.assertTrue(expr.isSimplePath());
        Assert.assertEquals("foo", expr.getRootSegment().getPath());
        Assert.assertEquals("`foo`", expr.toExpr());
    }

    @Test
    public void testOrdering() {
        Assert.assertEquals(ASCENDING, Ordering.getOrderingSpecFromString(null));
        Assert.assertEquals(ASCENDING, Ordering.getOrderingSpecFromString(ORDER_ASC));
        Assert.assertEquals(DESCENDING, Ordering.getOrderingSpecFromString(ORDER_DESC));
        Assert.assertEquals(ASCENDING, Ordering.getOrderingSpecFromString(ORDER_ASCENDING));
        Assert.assertEquals(DESCENDING, Ordering.getOrderingSpecFromString(ORDER_DESCENDING));
        Assert.assertEquals(ASCENDING, Ordering.getOrderingSpecFromString(ORDER_ASC.toLowerCase()));
        Assert.assertEquals(DESCENDING, Ordering.getOrderingSpecFromString(ORDER_DESC.toLowerCase()));
        Assert.assertEquals(ASCENDING, Ordering.getOrderingSpecFromString(ORDER_ASCENDING.toLowerCase()));
        Assert.assertEquals(DESCENDING, Ordering.getOrderingSpecFromString(ORDER_DESCENDING.toLowerCase()));
        try {
            Ordering.getOrderingSpecFromString("");
            Assert.fail();
        } catch (DrillRuntimeException e) {
        }
        try {
            Ordering.getOrderingSpecFromString("foo");
            Assert.fail();
        } catch (DrillRuntimeException e) {
        }
        Assert.assertEquals(UNSPECIFIED, Ordering.getNullOrderingFromString(null));
        Assert.assertEquals(FIRST, Ordering.getNullOrderingFromString(NULLS_FIRST));
        Assert.assertEquals(LAST, Ordering.getNullOrderingFromString(NULLS_LAST));
        Assert.assertEquals(UNSPECIFIED, Ordering.getNullOrderingFromString(NULLS_UNSPECIFIED));
        Assert.assertEquals(FIRST, Ordering.getNullOrderingFromString(NULLS_FIRST.toLowerCase()));
        Assert.assertEquals(LAST, Ordering.getNullOrderingFromString(NULLS_LAST.toLowerCase()));
        Assert.assertEquals(UNSPECIFIED, Ordering.getNullOrderingFromString(NULLS_UNSPECIFIED.toLowerCase()));
        try {
            Ordering.getNullOrderingFromString("");
            Assert.fail();
        } catch (DrillRuntimeException e) {
        }
        try {
            Ordering.getNullOrderingFromString("foo");
            Assert.fail();
        } catch (DrillRuntimeException e) {
        }
        FieldReference expr = FieldReference.getWithQuotedRef("foo");
        // Test all getters
        Ordering ordering = new Ordering(((String) (null)), expr, ((String) (null)));
        Assert.assertEquals(ASCENDING, ordering.getDirection());
        Assert.assertEquals(UNSPECIFIED, ordering.getNullDirection());
        Assert.assertSame(expr, ordering.getExpr());
        Assert.assertTrue(ordering.nullsSortHigh());
        // Test all ordering strings
        ordering = new Ordering(((String) (Ordering.ORDER_ASC)), expr, ((String) (null)));
        Assert.assertEquals(ASCENDING, ordering.getDirection());
        Assert.assertEquals(UNSPECIFIED, ordering.getNullDirection());
        ordering = new Ordering(((String) (ORDER_ASC.toLowerCase())), expr, ((String) (null)));
        Assert.assertEquals(ASCENDING, ordering.getDirection());
        Assert.assertEquals(UNSPECIFIED, ordering.getNullDirection());
        ordering = new Ordering(((String) (Ordering.ORDER_ASCENDING)), expr, ((String) (null)));
        Assert.assertEquals(ASCENDING, ordering.getDirection());
        Assert.assertEquals(UNSPECIFIED, ordering.getNullDirection());
        ordering = new Ordering(((String) (Ordering.ORDER_DESC)), expr, ((String) (null)));
        Assert.assertEquals(DESCENDING, ordering.getDirection());
        Assert.assertEquals(UNSPECIFIED, ordering.getNullDirection());
        ordering = new Ordering(((String) (Ordering.ORDER_DESCENDING)), expr, ((String) (null)));
        Assert.assertEquals(DESCENDING, ordering.getDirection());
        Assert.assertEquals(UNSPECIFIED, ordering.getNullDirection());
        // Test all null ordering strings
        ordering = new Ordering(((String) (null)), expr, Ordering.NULLS_FIRST);
        Assert.assertEquals(ASCENDING, ordering.getDirection());
        Assert.assertEquals(FIRST, ordering.getNullDirection());
        Assert.assertFalse(ordering.nullsSortHigh());
        ordering = new Ordering(((String) (null)), expr, Ordering.NULLS_FIRST);
        Assert.assertEquals(ASCENDING, ordering.getDirection());
        Assert.assertEquals(FIRST, ordering.getNullDirection());
        Assert.assertFalse(ordering.nullsSortHigh());
        ordering = new Ordering(((String) (null)), expr, Ordering.NULLS_LAST);
        Assert.assertEquals(ASCENDING, ordering.getDirection());
        Assert.assertEquals(LAST, ordering.getNullDirection());
        Assert.assertTrue(ordering.nullsSortHigh());
        ordering = new Ordering(((String) (null)), expr, Ordering.NULLS_UNSPECIFIED);
        Assert.assertEquals(ASCENDING, ordering.getDirection());
        Assert.assertEquals(UNSPECIFIED, ordering.getNullDirection());
        Assert.assertTrue(ordering.nullsSortHigh());
        // Unspecified order is always nulls high
        ordering = new Ordering(Ordering.ORDER_DESC, expr, Ordering.NULLS_UNSPECIFIED);
        Assert.assertEquals(DESCENDING, ordering.getDirection());
        Assert.assertEquals(UNSPECIFIED, ordering.getNullDirection());
        Assert.assertTrue(ordering.nullsSortHigh());
        // Null sort direction reverses with a Desc sort.
        ordering = new Ordering(Ordering.ORDER_DESC, expr, Ordering.NULLS_FIRST);
        Assert.assertEquals(DESCENDING, ordering.getDirection());
        Assert.assertEquals(FIRST, ordering.getNullDirection());
        Assert.assertTrue(ordering.nullsSortHigh());
        ordering = new Ordering(Ordering.ORDER_DESC, expr, Ordering.NULLS_LAST);
        Assert.assertEquals(DESCENDING, ordering.getDirection());
        Assert.assertEquals(LAST, ordering.getNullDirection());
        Assert.assertFalse(ordering.nullsSortHigh());
    }

    @Test
    public void testSortSpec() {
        FieldReference expr = FieldReference.getWithQuotedRef("foo");
        Ordering ordering = new Ordering(Ordering.ORDER_ASC, expr, Ordering.NULLS_FIRST);
        // Basics
        ExternalSort popConfig = new ExternalSort(null, Lists.newArrayList(ordering), false);
        Assert.assertSame(ordering, popConfig.getOrderings().get(0));
        Assert.assertFalse(popConfig.getReverse());
        Assert.assertEquals(FOUR_BYTE, popConfig.getSVMode());
        Assert.assertEquals(EXTERNAL_SORT_VALUE, popConfig.getOperatorType());
        Assert.assertEquals(DEFAULT_SORT_ALLOCATION, popConfig.getInitialAllocation());
        Assert.assertEquals(MAX_ALLOCATION, popConfig.getMaxAllocation());
        Assert.assertTrue(popConfig.isExecutable());
        // Non-default settings
        popConfig = new ExternalSort(null, Lists.newArrayList(ordering), true);
        Assert.assertTrue(popConfig.getReverse());
        long maxAlloc = 50000000;
        popConfig.setMaxAllocation(maxAlloc);
        Assert.assertEquals(DEFAULT_SORT_ALLOCATION, popConfig.getInitialAllocation());
        Assert.assertEquals(maxAlloc, popConfig.getMaxAllocation());
    }
}

