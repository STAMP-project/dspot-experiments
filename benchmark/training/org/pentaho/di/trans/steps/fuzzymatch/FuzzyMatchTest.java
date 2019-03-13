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
package org.pentaho.di.trans.steps.fuzzymatch;


import ValueMetaInterface.STORAGE_TYPE_BINARY_STRING;
import ValueMetaInterface.TYPE_STRING;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepIOMetaInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * User: Dzmitry Stsiapanau Date: 10/16/13 Time: 6:23 PM
 */
public class FuzzyMatchTest {
    @InjectMocks
    private FuzzyMatchTest.FuzzyMatchHandler fuzzyMatch;

    private StepMockHelper<FuzzyMatchMeta, FuzzyMatchData> mockHelper;

    private Object[] row = new Object[]{ "Catrine" };

    private Object[] rowB = new Object[]{ "Catrine".getBytes() };

    private Object[] row2 = new Object[]{ "John" };

    private Object[] row2B = new Object[]{ "John".getBytes() };

    private Object[] row3 = new Object[]{ "Catriny" };

    private Object[] row3B = new Object[]{ "Catriny".getBytes() };

    private List<Object[]> rows = new ArrayList<Object[]>();

    private List<Object[]> binaryRows = new ArrayList<Object[]>();

    private List<Object[]> lookupRows = new ArrayList<Object[]>();

    private List<Object[]> binaryLookupRows = new ArrayList<Object[]>();

    {
        rows.add(row);
        binaryRows.add(rowB);
        lookupRows.add(row2);
        lookupRows.add(row3);
        binaryLookupRows.add(row2B);
        binaryLookupRows.add(row3B);
    }

    private class FuzzyMatchHandler extends FuzzyMatch {
        private Object[] resultRow = null;

        private RowSet rowset = null;

        public FuzzyMatchHandler(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
        }

        @Override
        public void putRow(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
            resultRow = row;
        }

        /**
         * Find input row set.
         *
         * @param sourceStep
         * 		the source step
         * @return the row set
         * @throws org.pentaho.di.core.exception.KettleStepException
         * 		the kettle step exception
         */
        @Override
        public RowSet findInputRowSet(String sourceStep) throws KettleStepException {
            return rowset;
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcessRow() throws Exception {
        fuzzyMatch = new FuzzyMatchTest.FuzzyMatchHandler(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans);
        fuzzyMatch.init(mockHelper.initStepMetaInterface, mockHelper.initStepDataInterface);
        fuzzyMatch.addRowSetToInputRowSets(mockHelper.getMockInputRowSet(rows));
        fuzzyMatch.addRowSetToInputRowSets(mockHelper.getMockInputRowSet(lookupRows));
        Mockito.when(mockHelper.processRowsStepMetaInterface.getAlgorithmType()).thenReturn(8);
        mockHelper.processRowsStepDataInterface.look = Mockito.mock(HashSet.class);
        Mockito.when(mockHelper.processRowsStepDataInterface.look.iterator()).thenReturn(lookupRows.iterator());
        fuzzyMatch.processRow(mockHelper.processRowsStepMetaInterface, mockHelper.processRowsStepDataInterface);
        Assert.assertEquals(fuzzyMatch.resultRow[0], row3[0]);
    }

    @Test
    public void testReadLookupValues() throws Exception {
        FuzzyMatchData data = Mockito.spy(new FuzzyMatchData());
        data.indexOfCachedFields = new int[2];
        data.minimalDistance = 0;
        data.maximalDistance = 5;
        FuzzyMatchMeta meta = Mockito.spy(new FuzzyMatchMeta());
        meta.setOutputMatchField("I don't want NPE here!");
        data.readLookupValues = true;
        fuzzyMatch = new FuzzyMatchTest.FuzzyMatchHandler(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans);
        fuzzyMatch.init(meta, data);
        RowSet lookupRowSet = mockHelper.getMockInputRowSet(binaryLookupRows);
        fuzzyMatch.addRowSetToInputRowSets(mockHelper.getMockInputRowSet(binaryRows));
        fuzzyMatch.addRowSetToInputRowSets(lookupRowSet);
        fuzzyMatch.rowset = lookupRowSet;
        RowMetaInterface rowMetaInterface = new RowMeta();
        ValueMetaInterface valueMeta = new ValueMetaString("field1");
        valueMeta.setStorageMetadata(new ValueMetaString("field1"));
        valueMeta.setStorageType(STORAGE_TYPE_BINARY_STRING);
        rowMetaInterface.addValueMeta(valueMeta);
        Mockito.when(lookupRowSet.getRowMeta()).thenReturn(rowMetaInterface);
        Mockito.when(meta.getLookupField()).thenReturn("field1");
        Mockito.when(meta.getMainStreamField()).thenReturn("field1");
        fuzzyMatch.setInputRowMeta(rowMetaInterface.clone());
        Mockito.when(meta.getAlgorithmType()).thenReturn(1);
        StepIOMetaInterface stepIOMetaInterface = Mockito.mock(StepIOMetaInterface.class);
        Mockito.when(meta.getStepIOMeta()).thenReturn(stepIOMetaInterface);
        StreamInterface streamInterface = Mockito.mock(StreamInterface.class);
        List<StreamInterface> streamInterfaceList = new ArrayList<StreamInterface>();
        streamInterfaceList.add(streamInterface);
        Mockito.when(streamInterface.getStepMeta()).thenReturn(mockHelper.stepMeta);
        Mockito.when(stepIOMetaInterface.getInfoStreams()).thenReturn(streamInterfaceList);
        fuzzyMatch.processRow(meta, data);
        Assert.assertEquals(rowMetaInterface.getString(row3B, 0), data.outputRowMeta.getString(fuzzyMatch.resultRow, 1));
    }

    @Test
    public void testLookupValuesWhenMainFieldIsNull() throws Exception {
        FuzzyMatchData data = Mockito.spy(new FuzzyMatchData());
        FuzzyMatchMeta meta = Mockito.spy(new FuzzyMatchMeta());
        data.readLookupValues = false;
        fuzzyMatch = new FuzzyMatchTest.FuzzyMatchHandler(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans);
        fuzzyMatch.init(meta, data);
        fuzzyMatch.first = false;
        data.indexOfMainField = 1;
        Object[] inputRow = new Object[]{ "test input", null };
        RowSet lookupRowSet = mockHelper.getMockInputRowSet(new Object[]{ "test lookup" });
        fuzzyMatch.addRowSetToInputRowSets(mockHelper.getMockInputRowSet(inputRow));
        fuzzyMatch.addRowSetToInputRowSets(lookupRowSet);
        fuzzyMatch.rowset = lookupRowSet;
        RowMetaInterface rowMetaInterface = new RowMeta();
        ValueMetaInterface valueMeta = new ValueMetaString("field1");
        valueMeta.setStorageMetadata(new ValueMetaString("field1"));
        valueMeta.setStorageType(TYPE_STRING);
        rowMetaInterface.addValueMeta(valueMeta);
        Mockito.when(lookupRowSet.getRowMeta()).thenReturn(rowMetaInterface);
        fuzzyMatch.setInputRowMeta(rowMetaInterface.clone());
        data.outputRowMeta = rowMetaInterface.clone();
        fuzzyMatch.processRow(meta, data);
        Assert.assertEquals(inputRow[0], fuzzyMatch.resultRow[0]);
        Assert.assertNull(fuzzyMatch.resultRow[1]);
        Assert.assertTrue(Arrays.stream(fuzzyMatch.resultRow, 3, fuzzyMatch.resultRow.length).allMatch(( val) -> val == null));
    }
}

