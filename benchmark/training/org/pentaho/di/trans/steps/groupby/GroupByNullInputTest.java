/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.groupby;


import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class GroupByNullInputTest {
    public static final int NUMBER_OF_COLUMNS = 1;

    public static final String ANY_FIELD_NAME = "anyFieldName";

    static StepMockHelper<GroupByMeta, GroupByData> mockHelper;

    private GroupBy step;

    private GroupByData groupByStepData;

    private GroupByMeta groupByStepMeta;

    private RowMetaInterface rowMetaInterfaceMock;

    /**
     * PMD-1037 NPE error appears when user uses "Data Profile" feature to some tables in Hive 2.
     */
    @Test
    public void testNullInputDataForStandardDeviation() throws KettleValueException {
        setAggregationTypesAndInitData(new int[]{ 15 });
        ValueMetaInterface vmi = new ValueMetaInteger();
        Mockito.when(rowMetaInterfaceMock.getValueMeta(Mockito.anyInt())).thenReturn(vmi);
        Object[] row1 = new Object[GroupByNullInputTest.NUMBER_OF_COLUMNS];
        Arrays.fill(row1, null);
        step.newAggregate(row1);
        step.calcAggregate(row1);
        Object[] aggregateResult = step.getAggregateResult();
        Assert.assertNull("Returns null if aggregation is null", aggregateResult[0]);
    }

    @Test
    public void testNullInputDataForAggregationWithNumbers() throws KettleValueException {
        setAggregationTypesAndInitData(new int[]{ 1, 2, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15 });
        ValueMetaInterface vmi = new ValueMetaInteger();
        Mockito.when(rowMetaInterfaceMock.getValueMeta(Mockito.anyInt())).thenReturn(vmi);
        Object[] row1 = new Object[GroupByNullInputTest.NUMBER_OF_COLUMNS];
        Arrays.fill(row1, null);
        step.newAggregate(row1);
        step.calcAggregate(row1);
        Object[] aggregateResult = step.getAggregateResult();
        Assert.assertNull("Returns null if aggregation is null", aggregateResult[0]);
    }

    @Test
    public void testNullInputDataForAggregationWithNumbersMedianFunction() throws KettleValueException {
        setAggregationTypesAndInitData(new int[]{ 3, 4 });
        ValueMetaInterface vmi = new ValueMetaInteger();
        Mockito.when(rowMetaInterfaceMock.getValueMeta(Mockito.anyInt())).thenReturn(vmi);
        // PERCENTILE set
        groupByStepMeta.setValueField(new String[]{ "3", "3" });
        Object[] row1 = new Object[GroupByNullInputTest.NUMBER_OF_COLUMNS];
        Arrays.fill(row1, null);
        step.newAggregate(row1);
        step.calcAggregate(row1);
        step.getAggregateResult();
    }

    @Test
    public void testNullInputDataForAggregationWithStrings() throws KettleValueException {
        setAggregationTypesAndInitData(new int[]{ 8, 16, 17, 18 });
        groupByStepMeta.setValueField(new String[]{ "," });
        groupByStepMeta.setSubjectField(new String[]{ GroupByNullInputTest.ANY_FIELD_NAME, GroupByNullInputTest.ANY_FIELD_NAME });
        ValueMetaInterface vmi = new ValueMetaString();
        Mockito.when(rowMetaInterfaceMock.getValueMeta(Mockito.anyInt())).thenReturn(vmi);
        Object[] row1 = new Object[GroupByNullInputTest.NUMBER_OF_COLUMNS];
        Arrays.fill(row1, null);
        step.newAggregate(row1);
        step.calcAggregate(row1);
        Object[] row2 = new Object[GroupByNullInputTest.NUMBER_OF_COLUMNS];
        Arrays.fill(row2, null);
        step.calcAggregate(row2);
        Object[] row3 = new Object[GroupByNullInputTest.NUMBER_OF_COLUMNS];
        Arrays.fill(row3, null);
        step.calcAggregate(row3);
        step.getAggregateResult();
    }
}

