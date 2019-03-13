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
package org.pentaho.di.trans.steps.fieldsplitter;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.QueueRowSet;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.SingleRowRowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Tests for FieldSplitter step
 *
 * @author Pavel Sakun
 * @see FieldSplitter
 */
public class FieldSplitterTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    StepMockHelper<FieldSplitterMeta, FieldSplitterData> smh;

    @Test
    public void testSplitFields() throws KettleException {
        FieldSplitter step = new FieldSplitter(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        step.init(smh.initStepMetaInterface, smh.stepDataInterface);
        step.setInputRowMeta(getInputRowMeta());
        step.addRowSetToInputRowSets(mockInputRowSet());
        step.addRowSetToOutputRowSets(new QueueRowSet());
        boolean hasMoreRows;
        do {
            hasMoreRows = step.processRow(mockProcessRowMeta(), smh.processRowsStepDataInterface);
        } while (hasMoreRows );
        RowSet outputRowSet = step.getOutputRowSets().get(0);
        Object[] actualRow = outputRowSet.getRow();
        Object[] expectedRow = new Object[]{ "before", null, "b=b", "after" };
        Assert.assertEquals("Output row is of an unexpected length", expectedRow.length, outputRowSet.getRowMeta().size());
        for (int i = 0; i < (expectedRow.length); i++) {
            Assert.assertEquals(("Unexpected output value at index " + i), expectedRow[i], actualRow[i]);
        }
    }

    @Test
    public void testSplitFieldsDup() throws Exception {
        FieldSplitterMeta meta = new FieldSplitterMeta();
        meta.allocate(2);
        meta.setDelimiter(" ");
        meta.setEnclosure("");
        meta.setSplitField("split");
        meta.setFieldName(new String[]{ "key", "val" });
        meta.setFieldType(new int[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_STRING });
        FieldSplitter step = new FieldSplitter(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        step.init(meta, smh.stepDataInterface);
        RowMetaInterface rowMeta = new RowMeta();
        rowMeta.addValueMeta(new ValueMetaString("key"));
        rowMeta.addValueMeta(new ValueMetaString("val"));
        rowMeta.addValueMeta(new ValueMetaString("split"));
        step.setInputRowMeta(rowMeta);
        step.addRowSetToInputRowSets(smh.getMockInputRowSet(new Object[]{ "key", "string", "part1 part2" }));
        step.addRowSetToOutputRowSets(new SingleRowRowSet());
        Assert.assertTrue(step.processRow(meta, smh.stepDataInterface));
        RowSet rs = step.getOutputRowSets().get(0);
        Object[] row = rs.getRow();
        RowMetaInterface rm = rs.getRowMeta();
        Assert.assertArrayEquals(new Object[]{ "key", "string", "part1", "part2" }, Arrays.copyOf(row, 4));
        Assert.assertArrayEquals(new Object[]{ "key", "val", "key_1", "val_1" }, rm.getFieldNames());
    }
}

