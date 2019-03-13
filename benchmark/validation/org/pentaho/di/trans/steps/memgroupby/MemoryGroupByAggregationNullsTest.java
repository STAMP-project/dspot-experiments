/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.memgroupby;


import ValueMetaInterface.STORAGE_TYPE_BINARY_STRING;
import ValueMetaInterface.STORAGE_TYPE_NORMAL;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.steps.mock.StepMockHelper;

import static MemoryGroupByMeta.TYPE_GROUP_COUNT_DISTINCT;


public class MemoryGroupByAggregationNullsTest {
    static StepMockHelper<MemoryGroupByMeta, MemoryGroupByData> mockHelper;

    MemoryGroupBy step;

    MemoryGroupByData data;

    static int def = 113;

    Aggregate aggregate;

    private ValueMetaInterface vmi;

    private RowMetaInterface rmi;

    private MemoryGroupByMeta meta;

    /**
     * PDI-10250 - "Group by" step - Minimum aggregation doesn't work
     *
     * KETTLE_AGGREGATION_MIN_NULL_IS_VALUED
     *
     * Set this variable to Y to set the minimum to NULL if NULL is within an aggregate. Otherwise by default NULL is
     * ignored by the MIN aggregate and MIN is set to the minimum value that is not NULL. See also the variable
     * KETTLE_AGGREGATION_ALL_NULLS_ARE_ZERO.
     *
     * @throws KettleException
     * 		
     */
    @Test
    public void calcAggregateResulTestMin_1_Test() throws KettleException {
        step.setMinNullIsValued(true);
        step.addToAggregate(new Object[]{ null });
        Aggregate agg = data.map.get(getHashEntry());
        Assert.assertNotNull("Hash code strategy changed?", agg);
        Assert.assertNull("Value is set", agg.agg[0]);
    }

    @Test
    public void calcAggregateResulTestMin_5_Test() throws KettleException {
        step.setMinNullIsValued(false);
        step.addToAggregate(new Object[]{ null });
        Aggregate agg = data.map.get(getHashEntry());
        Assert.assertNotNull("Hash code strategy changed?", agg);
        Assert.assertEquals("Value is NOT set", MemoryGroupByAggregationNullsTest.def, agg.agg[0]);
    }

    /**
     * Set this variable to Y to return 0 when all values within an aggregate are NULL. Otherwise by default a NULL is
     * returned when all values are NULL.
     *
     * @throws KettleValueException
     * 		
     */
    @Test
    public void getAggregateResulTestMin_0_Test() throws KettleValueException {
        // data.agg[0] is not null - this is the default behavior
        step.setAllNullsAreZero(true);
        Object[] row = step.getAggregateResult(aggregate);
        Assert.assertEquals("Default value is not corrupted", MemoryGroupByAggregationNullsTest.def, row[0]);
    }

    @Test
    public void getAggregateResulTestMin_1_Test() throws KettleValueException {
        aggregate.agg[0] = null;
        step.setAllNullsAreZero(true);
        Object[] row = step.getAggregateResult(aggregate);
        Assert.assertEquals("Returns 0 if aggregation is null", new Long(0), row[0]);
    }

    @Test
    public void getAggregateResulTestMin_3_Test() throws KettleValueException {
        aggregate.agg[0] = null;
        step.setAllNullsAreZero(false);
        Object[] row = step.getAggregateResult(aggregate);
        Assert.assertNull("Returns null if aggregation is null", row[0]);
    }

    @Test
    public void addToAggregateLazyConversionMinTest() throws Exception {
        vmi.setStorageType(STORAGE_TYPE_BINARY_STRING);
        vmi.setStorageMetadata(new ValueMetaString());
        aggregate.agg = new Object[]{ new byte[0] };
        byte[] bytes = new byte[]{ 51 };
        step.addToAggregate(new Object[]{ bytes });
        Aggregate result = data.map.get(getHashEntry());
        Assert.assertEquals("Returns non-null value", bytes, result.agg[0]);
    }

    // PDI-16150
    @Test
    public void addToAggregateBinaryData() throws Exception {
        MemoryGroupByMeta memoryGroupByMeta = Mockito.spy(meta);
        memoryGroupByMeta.setAggregateType(new int[]{ TYPE_GROUP_COUNT_DISTINCT });
        Mockito.when(MemoryGroupByAggregationNullsTest.mockHelper.stepMeta.getStepMetaInterface()).thenReturn(memoryGroupByMeta);
        vmi.setStorageType(STORAGE_TYPE_NORMAL);
        vmi.setStorageMetadata(new ValueMetaString());
        aggregate.counts = new long[]{ 0L };
        Mockito.doReturn(new String[]{ "test" }).when(memoryGroupByMeta).getSubjectField();
        aggregate.agg = new Object[]{ new byte[0] };
        step = new MemoryGroupBy(MemoryGroupByAggregationNullsTest.mockHelper.stepMeta, data, 0, MemoryGroupByAggregationNullsTest.mockHelper.transMeta, MemoryGroupByAggregationNullsTest.mockHelper.trans);
        String binaryData0 = "11011";
        String binaryData1 = "01011";
        step.addToAggregate(new Object[]{ binaryData0.getBytes() });
        step.addToAggregate(new Object[]{ binaryData1.getBytes() });
        Object[] distinctObjs = data.map.get(getHashEntry()).distinctObjs[0].toArray();
        Assert.assertEquals(binaryData0, distinctObjs[1]);
        Assert.assertEquals(binaryData1, distinctObjs[0]);
    }
}

