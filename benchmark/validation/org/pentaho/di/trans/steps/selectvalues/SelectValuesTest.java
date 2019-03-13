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
package org.pentaho.di.trans.steps.selectvalues;


import Const.KETTLE_COMPATIBILITY_SELECT_VALUES_TYPE_CHANGE_USES_TYPE_DEFAULTS;
import ValueMetaBase.DEFAULT_BIG_NUMBER_FORMAT_MASK;
import ValueMetaBase.DEFAULT_INTEGER_FORMAT_MASK;
import ValueMetaBase.DEFAULT_NUMBER_FORMAT_MASK;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleConversionException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta.SelectField;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class SelectValuesTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static final String SELECTED_FIELD = "field";

    private final Object[] inputRow = new Object[]{ "a string" };

    private SelectValues step;

    private StepMockHelper<SelectValuesMeta, StepDataInterface> helper;

    @Test
    public void testPDI16368() throws Exception {
        // This tests that the fix for PDI-16388 doesn't get re-broken.
        // 
        SelectValuesTest.SelectValuesHandler step2 = null;
        Object[] inputRow2 = null;
        RowMeta inputRowMeta = null;
        SelectValuesMeta stepMeta = null;
        SelectValuesData stepData = null;
        ValueMetaInterface vmi = null;
        // First, test current behavior (it's worked this way since 5.x or so)
        // 
        step2 = new SelectValuesTest.SelectValuesHandler(helper.stepMeta, helper.stepDataInterface, 1, helper.transMeta, helper.trans);
        step2 = Mockito.spy(step2);
        inputRow2 = new Object[]{ new BigDecimal("589") };// Starting with a BigDecimal (no places)

        getRow();
        putError(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        inputRowMeta = new RowMeta();
        inputRowMeta.addValueMeta(new ValueMetaBigNumber(SelectValuesTest.SELECTED_FIELD));
        step2.setInputRowMeta(inputRowMeta);
        stepMeta = new SelectValuesMeta();
        stepMeta.allocate(1, 0, 1);
        stepMeta.getSelectFields()[0] = new SelectField();
        stepMeta.getSelectFields()[0].setName(SelectValuesTest.SELECTED_FIELD);
        stepMeta.getMeta()[0] = new SelectMetadataChange(stepMeta, SelectValuesTest.SELECTED_FIELD, null, ValueMetaInterface.TYPE_INTEGER, (-2), (-2), ValueMetaInterface.STORAGE_TYPE_NORMAL, null, false, null, null, false, null, null, null);// no specified conversion type so should have default conversion mask.

        stepData = new SelectValuesData();
        stepData.select = true;
        stepData.metadata = true;
        stepData.firstselect = true;
        stepData.firstmetadata = true;
        step2.processRow(stepMeta, stepData);
        vmi = step2.rowMeta.getValueMeta(0);
        Assert.assertEquals(DEFAULT_BIG_NUMBER_FORMAT_MASK, vmi.getConversionMask());
        step2 = new SelectValuesTest.SelectValuesHandler(helper.stepMeta, helper.stepDataInterface, 1, helper.transMeta, helper.trans);
        step2 = Mockito.spy(step2);
        getRow();
        putError(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        inputRowMeta = new RowMeta();
        inputRowMeta.addValueMeta(new ValueMetaBigNumber(SelectValuesTest.SELECTED_FIELD));
        step2.setInputRowMeta(inputRowMeta);
        stepMeta = new SelectValuesMeta();
        stepMeta.allocate(1, 0, 1);
        stepMeta.getSelectFields()[0] = new SelectField();
        stepMeta.getSelectFields()[0].setName(SelectValuesTest.SELECTED_FIELD);
        stepMeta.getMeta()[0] = new SelectMetadataChange(stepMeta, SelectValuesTest.SELECTED_FIELD, null, ValueMetaInterface.TYPE_NUMBER, (-2), (-2), ValueMetaInterface.STORAGE_TYPE_NORMAL, null, false, null, null, false, null, null, null);// no specified conversion type so should have default conversion mask for Double.

        stepData = new SelectValuesData();
        stepData.select = true;
        stepData.metadata = true;
        stepData.firstselect = true;
        stepData.firstmetadata = true;
        step2.processRow(stepMeta, stepData);
        vmi = step2.rowMeta.getValueMeta(0);
        Assert.assertEquals(DEFAULT_BIG_NUMBER_FORMAT_MASK, vmi.getConversionMask());
        step2 = new SelectValuesTest.SelectValuesHandler(helper.stepMeta, helper.stepDataInterface, 1, helper.transMeta, helper.trans);
        step2 = Mockito.spy(step2);
        inputRow2 = new Object[]{ new Long("589") };// Starting with a Long

        getRow();
        putError(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        inputRowMeta = new RowMeta();
        inputRowMeta.addValueMeta(new ValueMetaInteger(SelectValuesTest.SELECTED_FIELD));
        step2.setInputRowMeta(inputRowMeta);
        stepMeta = new SelectValuesMeta();
        stepMeta.allocate(1, 0, 1);
        stepMeta.getSelectFields()[0] = new SelectField();
        stepMeta.getSelectFields()[0].setName(SelectValuesTest.SELECTED_FIELD);
        // no specified conversion type so should have default conversion mask for BigNumber
        stepMeta.getMeta()[0] = new SelectMetadataChange(stepMeta, SelectValuesTest.SELECTED_FIELD, null, ValueMetaInterface.TYPE_BIGNUMBER, (-2), (-2), ValueMetaInterface.STORAGE_TYPE_NORMAL, null, false, null, null, false, null, null, null);
        stepData = new SelectValuesData();
        stepData.select = true;
        stepData.metadata = true;
        stepData.firstselect = true;
        stepData.firstmetadata = true;
        step2.processRow(stepMeta, stepData);
        vmi = step2.rowMeta.getValueMeta(0);
        Assert.assertEquals(DEFAULT_INTEGER_FORMAT_MASK, vmi.getConversionMask());
        // Now, test that setting the variable results in getting the default conversion mask
        step2 = new SelectValuesTest.SelectValuesHandler(helper.stepMeta, helper.stepDataInterface, 1, helper.transMeta, helper.trans);
        step2.setVariable(KETTLE_COMPATIBILITY_SELECT_VALUES_TYPE_CHANGE_USES_TYPE_DEFAULTS, "Y");
        step2 = Mockito.spy(step2);
        inputRow2 = new Object[]{ new BigDecimal("589") };// Starting with a BigDecimal (no places)

        getRow();
        putError(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        inputRowMeta = new RowMeta();
        inputRowMeta.addValueMeta(new ValueMetaBigNumber(SelectValuesTest.SELECTED_FIELD));
        step2.setInputRowMeta(inputRowMeta);
        stepMeta = new SelectValuesMeta();
        stepMeta.allocate(1, 0, 1);
        stepMeta.getSelectFields()[0] = new SelectField();
        stepMeta.getSelectFields()[0].setName(SelectValuesTest.SELECTED_FIELD);
        stepMeta.getMeta()[0] = new SelectMetadataChange(stepMeta, SelectValuesTest.SELECTED_FIELD, null, ValueMetaInterface.TYPE_INTEGER, (-2), (-2), ValueMetaInterface.STORAGE_TYPE_NORMAL, null, false, null, null, false, null, null, null);// no specified conversion type so should have default conversion mask.

        stepData = new SelectValuesData();
        stepData.select = true;
        stepData.metadata = true;
        stepData.firstselect = true;
        stepData.firstmetadata = true;
        step2.processRow(stepMeta, stepData);
        vmi = step2.rowMeta.getValueMeta(0);
        Assert.assertEquals(DEFAULT_INTEGER_FORMAT_MASK, vmi.getConversionMask());
        step2 = new SelectValuesTest.SelectValuesHandler(helper.stepMeta, helper.stepDataInterface, 1, helper.transMeta, helper.trans);
        step2.setVariable(KETTLE_COMPATIBILITY_SELECT_VALUES_TYPE_CHANGE_USES_TYPE_DEFAULTS, "Y");
        step2 = Mockito.spy(step2);
        getRow();
        putError(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        inputRowMeta = new RowMeta();
        inputRowMeta.addValueMeta(new ValueMetaBigNumber(SelectValuesTest.SELECTED_FIELD));
        step2.setInputRowMeta(inputRowMeta);
        stepMeta = new SelectValuesMeta();
        stepMeta.allocate(1, 0, 1);
        stepMeta.getSelectFields()[0] = new SelectField();
        stepMeta.getSelectFields()[0].setName(SelectValuesTest.SELECTED_FIELD);
        stepMeta.getMeta()[0] = new SelectMetadataChange(stepMeta, SelectValuesTest.SELECTED_FIELD, null, ValueMetaInterface.TYPE_NUMBER, (-2), (-2), ValueMetaInterface.STORAGE_TYPE_NORMAL, null, false, null, null, false, null, null, null);// no specified conversion type so should have default conversion mask for Double.

        stepData = new SelectValuesData();
        stepData.select = true;
        stepData.metadata = true;
        stepData.firstselect = true;
        stepData.firstmetadata = true;
        step2.processRow(stepMeta, stepData);
        vmi = step2.rowMeta.getValueMeta(0);
        Assert.assertEquals(DEFAULT_NUMBER_FORMAT_MASK, vmi.getConversionMask());
        step2 = new SelectValuesTest.SelectValuesHandler(helper.stepMeta, helper.stepDataInterface, 1, helper.transMeta, helper.trans);
        step2.setVariable(KETTLE_COMPATIBILITY_SELECT_VALUES_TYPE_CHANGE_USES_TYPE_DEFAULTS, "Y");
        step2 = Mockito.spy(step2);
        inputRow2 = new Object[]{ new Long("589") };// Starting with a Long

        getRow();
        putError(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        inputRowMeta = new RowMeta();
        inputRowMeta.addValueMeta(new ValueMetaInteger(SelectValuesTest.SELECTED_FIELD));
        step2.setInputRowMeta(inputRowMeta);
        stepMeta = new SelectValuesMeta();
        stepMeta.allocate(1, 0, 1);
        stepMeta.getSelectFields()[0] = new SelectField();
        stepMeta.getSelectFields()[0].setName(SelectValuesTest.SELECTED_FIELD);
        // no specified conversion type so should have default conversion mask for BigNumber
        stepMeta.getMeta()[0] = new SelectMetadataChange(stepMeta, SelectValuesTest.SELECTED_FIELD, null, ValueMetaInterface.TYPE_BIGNUMBER, (-2), (-2), ValueMetaInterface.STORAGE_TYPE_NORMAL, null, false, null, null, false, null, null, null);
        stepData = new SelectValuesData();
        stepData.select = true;
        stepData.metadata = true;
        stepData.firstselect = true;
        stepData.firstmetadata = true;
        step2.processRow(stepMeta, stepData);
        vmi = step2.rowMeta.getValueMeta(0);
        Assert.assertEquals(DEFAULT_BIG_NUMBER_FORMAT_MASK, vmi.getConversionMask());
    }

    @Test
    public void errorRowSetObtainsFieldName() throws Exception {
        SelectValuesMeta stepMeta = new SelectValuesMeta();
        stepMeta.allocate(1, 0, 1);
        stepMeta.getSelectFields()[0] = new SelectField();
        stepMeta.getSelectFields()[0].setName(SelectValuesTest.SELECTED_FIELD);
        stepMeta.getMeta()[0] = new SelectMetadataChange(stepMeta, SelectValuesTest.SELECTED_FIELD, null, ValueMetaInterface.TYPE_INTEGER, (-2), (-2), ValueMetaInterface.STORAGE_TYPE_NORMAL, null, false, null, null, false, null, null, null);
        SelectValuesData stepData = new SelectValuesData();
        stepData.select = true;
        stepData.metadata = true;
        stepData.firstselect = true;
        stepData.firstmetadata = true;
        step.processRow(stepMeta, stepData);
        Mockito.verify(step).putError(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(SelectValuesTest.SELECTED_FIELD), ArgumentMatchers.anyString());
        // additionally ensure conversion error causes KettleConversionError
        boolean properException = false;
        try {
            step.metadataValues(step.getInputRowMeta(), inputRow);
        } catch (KettleConversionException e) {
            properException = true;
        }
        Assert.assertTrue(properException);
    }

    public class SelectValuesHandler extends SelectValues {
        private Object[] resultRow;

        private RowMetaInterface rowMeta;

        private RowSet rowset;

        public SelectValuesHandler(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
        }

        @Override
        public void putRow(RowMetaInterface rm, Object[] row) throws KettleStepException {
            resultRow = row;
            rowMeta = rm;
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
}

