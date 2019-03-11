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
package org.apache.flink.optimizer.dataproperties;


import Order.ASCENDING;
import Order.DESCENDING;
import org.apache.flink.api.common.operators.Ordering;
import org.apache.flink.api.common.operators.SingleInputSemanticProperties;
import org.apache.flink.api.common.operators.util.FieldList;
import org.apache.flink.api.common.operators.util.FieldSet;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.functions.SemanticPropUtil;
import org.apache.flink.api.java.tuple.Tuple8;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.types.IntValue;
import org.apache.flink.types.LongValue;
import org.apache.flink.types.StringValue;
import org.junit.Assert;
import org.junit.Test;


public class LocalPropertiesFilteringTest {
    private TupleTypeInfo<Tuple8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> tupleInfo = new TupleTypeInfo<Tuple8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>(BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);

    @Test
    public void testAllErased1() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, null, null, null, tupleInfo, tupleInfo);
        LocalProperties lProps = LocalProperties.forGrouping(new FieldList(0, 1, 2));
        lProps = lProps.addUniqueFields(new FieldSet(3, 4));
        lProps = lProps.addUniqueFields(new FieldSet(5, 6));
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        Assert.assertNull(filtered.getGroupedFields());
        Assert.assertNull(filtered.getOrdering());
        Assert.assertNull(filtered.getUniqueFields());
    }

    @Test
    public void testAllErased2() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "5" }, null, null, tupleInfo, tupleInfo);
        LocalProperties lProps = LocalProperties.forGrouping(new FieldList(0, 1, 2));
        lProps = lProps.addUniqueFields(new FieldSet(3, 4));
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        Assert.assertNull(filtered.getGroupedFields());
        Assert.assertNull(filtered.getOrdering());
        Assert.assertNull(filtered.getUniqueFields());
    }

    @Test
    public void testGroupingPreserved1() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "0;2;3" }, null, null, tupleInfo, tupleInfo);
        LocalProperties lProps = LocalProperties.forGrouping(new FieldList(0, 2, 3));
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        Assert.assertNotNull(filtered.getGroupedFields());
        Assert.assertEquals(3, filtered.getGroupedFields().size());
        Assert.assertTrue(filtered.getGroupedFields().contains(0));
        Assert.assertTrue(filtered.getGroupedFields().contains(2));
        Assert.assertTrue(filtered.getGroupedFields().contains(3));
        Assert.assertNull(filtered.getOrdering());
        Assert.assertNull(filtered.getUniqueFields());
    }

    @Test
    public void testGroupingPreserved2() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "0->4;2->0;3->7" }, null, null, tupleInfo, tupleInfo);
        LocalProperties lProps = LocalProperties.forGrouping(new FieldList(0, 2, 3));
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        Assert.assertNotNull(filtered.getGroupedFields());
        Assert.assertEquals(3, filtered.getGroupedFields().size());
        Assert.assertTrue(filtered.getGroupedFields().contains(4));
        Assert.assertTrue(filtered.getGroupedFields().contains(0));
        Assert.assertTrue(filtered.getGroupedFields().contains(7));
        Assert.assertNull(filtered.getOrdering());
        Assert.assertNull(filtered.getUniqueFields());
    }

    @Test
    public void testGroupingErased() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "0->4;2->0" }, null, null, tupleInfo, tupleInfo);
        LocalProperties lProps = LocalProperties.forGrouping(new FieldList(0, 2, 3));
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        Assert.assertNull(filtered.getGroupedFields());
        Assert.assertNull(filtered.getOrdering());
        Assert.assertNull(filtered.getUniqueFields());
    }

    @Test
    public void testSortingPreserved1() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "0;2;5" }, null, null, tupleInfo, tupleInfo);
        Ordering o = new Ordering();
        o.appendOrdering(2, IntValue.class, ASCENDING);
        o.appendOrdering(0, StringValue.class, DESCENDING);
        o.appendOrdering(5, LongValue.class, DESCENDING);
        LocalProperties lProps = LocalProperties.forOrdering(o);
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        FieldList gFields = filtered.getGroupedFields();
        Ordering order = filtered.getOrdering();
        Assert.assertNotNull(gFields);
        Assert.assertEquals(3, gFields.size());
        Assert.assertTrue(gFields.contains(0));
        Assert.assertTrue(gFields.contains(2));
        Assert.assertTrue(gFields.contains(5));
        Assert.assertNotNull(order);
        Assert.assertEquals(3, order.getNumberOfFields());
        Assert.assertEquals(2, order.getFieldNumber(0).intValue());
        Assert.assertEquals(0, order.getFieldNumber(1).intValue());
        Assert.assertEquals(5, order.getFieldNumber(2).intValue());
        Assert.assertEquals(ASCENDING, order.getOrder(0));
        Assert.assertEquals(DESCENDING, order.getOrder(1));
        Assert.assertEquals(DESCENDING, order.getOrder(2));
        Assert.assertEquals(IntValue.class, order.getType(0));
        Assert.assertEquals(StringValue.class, order.getType(1));
        Assert.assertEquals(LongValue.class, order.getType(2));
        Assert.assertNull(filtered.getUniqueFields());
    }

    @Test
    public void testSortingPreserved2() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "0->3;2->7;5->1" }, null, null, tupleInfo, tupleInfo);
        Ordering o = new Ordering();
        o.appendOrdering(2, IntValue.class, ASCENDING);
        o.appendOrdering(0, StringValue.class, DESCENDING);
        o.appendOrdering(5, LongValue.class, DESCENDING);
        LocalProperties lProps = LocalProperties.forOrdering(o);
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        FieldList gFields = filtered.getGroupedFields();
        Ordering order = filtered.getOrdering();
        Assert.assertNotNull(gFields);
        Assert.assertEquals(3, gFields.size());
        Assert.assertTrue(gFields.contains(3));
        Assert.assertTrue(gFields.contains(7));
        Assert.assertTrue(gFields.contains(1));
        Assert.assertNotNull(order);
        Assert.assertEquals(3, order.getNumberOfFields());
        Assert.assertEquals(7, order.getFieldNumber(0).intValue());
        Assert.assertEquals(3, order.getFieldNumber(1).intValue());
        Assert.assertEquals(1, order.getFieldNumber(2).intValue());
        Assert.assertEquals(ASCENDING, order.getOrder(0));
        Assert.assertEquals(DESCENDING, order.getOrder(1));
        Assert.assertEquals(DESCENDING, order.getOrder(2));
        Assert.assertEquals(IntValue.class, order.getType(0));
        Assert.assertEquals(StringValue.class, order.getType(1));
        Assert.assertEquals(LongValue.class, order.getType(2));
        Assert.assertNull(filtered.getUniqueFields());
    }

    @Test
    public void testSortingPreserved3() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "0;2" }, null, null, tupleInfo, tupleInfo);
        Ordering o = new Ordering();
        o.appendOrdering(2, IntValue.class, ASCENDING);
        o.appendOrdering(0, StringValue.class, DESCENDING);
        o.appendOrdering(5, LongValue.class, DESCENDING);
        LocalProperties lProps = LocalProperties.forOrdering(o);
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        FieldList gFields = filtered.getGroupedFields();
        Ordering order = filtered.getOrdering();
        Assert.assertNotNull(gFields);
        Assert.assertEquals(2, gFields.size());
        Assert.assertTrue(gFields.contains(0));
        Assert.assertTrue(gFields.contains(2));
        Assert.assertNotNull(order);
        Assert.assertEquals(2, order.getNumberOfFields());
        Assert.assertEquals(2, order.getFieldNumber(0).intValue());
        Assert.assertEquals(0, order.getFieldNumber(1).intValue());
        Assert.assertEquals(ASCENDING, order.getOrder(0));
        Assert.assertEquals(DESCENDING, order.getOrder(1));
        Assert.assertEquals(IntValue.class, order.getType(0));
        Assert.assertEquals(StringValue.class, order.getType(1));
        Assert.assertNull(filtered.getUniqueFields());
    }

    @Test
    public void testSortingPreserved4() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "2->7;5" }, null, null, tupleInfo, tupleInfo);
        Ordering o = new Ordering();
        o.appendOrdering(2, IntValue.class, ASCENDING);
        o.appendOrdering(0, StringValue.class, DESCENDING);
        o.appendOrdering(5, LongValue.class, DESCENDING);
        LocalProperties lProps = LocalProperties.forOrdering(o);
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        FieldList gFields = filtered.getGroupedFields();
        Ordering order = filtered.getOrdering();
        Assert.assertNotNull(gFields);
        Assert.assertEquals(1, gFields.size());
        Assert.assertTrue(gFields.contains(7));
        Assert.assertNotNull(order);
        Assert.assertEquals(1, order.getNumberOfFields());
        Assert.assertEquals(7, order.getFieldNumber(0).intValue());
        Assert.assertEquals(ASCENDING, order.getOrder(0));
        Assert.assertEquals(IntValue.class, order.getType(0));
        Assert.assertNull(filtered.getUniqueFields());
    }

    @Test
    public void testSortingErased() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "0;5" }, null, null, tupleInfo, tupleInfo);
        Ordering o = new Ordering();
        o.appendOrdering(2, IntValue.class, ASCENDING);
        o.appendOrdering(0, StringValue.class, DESCENDING);
        o.appendOrdering(5, LongValue.class, DESCENDING);
        LocalProperties lProps = LocalProperties.forOrdering(o);
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        FieldList gFields = filtered.getGroupedFields();
        Ordering order = filtered.getOrdering();
        Assert.assertNull(gFields);
        Assert.assertNull(order);
        Assert.assertNull(filtered.getUniqueFields());
    }

    @Test
    public void testUniqueFieldsPreserved1() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "0;1;2;3;4" }, null, null, tupleInfo, tupleInfo);
        LocalProperties lProps = new LocalProperties();
        lProps = lProps.addUniqueFields(new FieldSet(0, 1, 2));
        lProps = lProps.addUniqueFields(new FieldSet(3, 4));
        lProps = lProps.addUniqueFields(new FieldSet(4, 5, 6));
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        FieldSet expected1 = new FieldSet(0, 1, 2);
        FieldSet expected2 = new FieldSet(3, 4);
        Assert.assertNull(filtered.getGroupedFields());
        Assert.assertNull(filtered.getOrdering());
        Assert.assertNotNull(filtered.getUniqueFields());
        Assert.assertEquals(2, filtered.getUniqueFields().size());
        Assert.assertTrue(filtered.getUniqueFields().contains(expected1));
        Assert.assertTrue(filtered.getUniqueFields().contains(expected2));
    }

    @Test
    public void testUniqueFieldsPreserved2() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "0;1;2;3;4" }, null, null, tupleInfo, tupleInfo);
        LocalProperties lProps = LocalProperties.forGrouping(new FieldList(1, 2));
        lProps = lProps.addUniqueFields(new FieldSet(0, 1, 2));
        lProps = lProps.addUniqueFields(new FieldSet(3, 4));
        lProps = lProps.addUniqueFields(new FieldSet(4, 5, 6));
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        FieldSet expected1 = new FieldSet(0, 1, 2);
        FieldSet expected2 = new FieldSet(3, 4);
        Assert.assertNull(filtered.getOrdering());
        Assert.assertNotNull(filtered.getGroupedFields());
        Assert.assertEquals(2, filtered.getGroupedFields().size());
        Assert.assertTrue(filtered.getGroupedFields().contains(1));
        Assert.assertTrue(filtered.getGroupedFields().contains(2));
        Assert.assertNotNull(filtered.getUniqueFields());
        Assert.assertEquals(2, filtered.getUniqueFields().size());
        Assert.assertTrue(filtered.getUniqueFields().contains(expected1));
        Assert.assertTrue(filtered.getUniqueFields().contains(expected2));
    }

    @Test
    public void testUniqueFieldsPreserved3() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "0->7;1->6;2->5;3->4;4->3" }, null, null, tupleInfo, tupleInfo);
        LocalProperties lProps = new LocalProperties();
        lProps = lProps.addUniqueFields(new FieldSet(0, 1, 2));
        lProps = lProps.addUniqueFields(new FieldSet(3, 4));
        lProps = lProps.addUniqueFields(new FieldSet(4, 5, 6));
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        FieldSet expected1 = new FieldSet(5, 6, 7);
        FieldSet expected2 = new FieldSet(3, 4);
        Assert.assertNull(filtered.getGroupedFields());
        Assert.assertNull(filtered.getOrdering());
        Assert.assertNotNull(filtered.getUniqueFields());
        Assert.assertEquals(2, filtered.getUniqueFields().size());
        Assert.assertTrue(filtered.getUniqueFields().contains(expected1));
        Assert.assertTrue(filtered.getUniqueFields().contains(expected2));
    }

    @Test
    public void testUniqueFieldsErased() {
        SingleInputSemanticProperties sp = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sp, new String[]{ "0;1;4" }, null, null, tupleInfo, tupleInfo);
        LocalProperties lProps = new LocalProperties();
        lProps = lProps.addUniqueFields(new FieldSet(0, 1, 2));
        lProps = lProps.addUniqueFields(new FieldSet(3, 4));
        lProps = lProps.addUniqueFields(new FieldSet(4, 5, 6));
        LocalProperties filtered = lProps.filterBySemanticProperties(sp, 0);
        Assert.assertNull(filtered.getGroupedFields());
        Assert.assertNull(filtered.getOrdering());
        Assert.assertNull(filtered.getUniqueFields());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidInputIndex() {
        SingleInputSemanticProperties sprops = new SingleInputSemanticProperties();
        SemanticPropUtil.getSemanticPropsSingleFromString(sprops, new String[]{ "0;1" }, null, null, tupleInfo, tupleInfo);
        LocalProperties lprops = LocalProperties.forGrouping(new FieldList(0, 1));
        lprops.filterBySemanticProperties(sprops, 1);
    }
}

