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
package org.pentaho.di.trans.steps.concatfields;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.pentaho.di.trans.steps.textfileoutput.TextFileField;


/**
 * User: Dzmitry Stsiapanau Date: 2/11/14 Time: 11:00 AM
 */
public class ConcatFieldsTest {
    private class ConcatFieldsHandler extends ConcatFields {
        private Object[] row;

        public ConcatFieldsHandler(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
        }

        /**
         * In case of getRow, we receive data from previous steps through the input rowset. In case we split the stream, we
         * have to copy the data to the alternate splits: rowsets 1 through n.
         */
        @Override
        public Object[] getRow() throws KettleException {
            return row;
        }

        public void setRow(Object[] row) {
            this.row = row;
        }

        @Override
        protected Object[] putRowFastDataDump(Object[] r) throws KettleStepException {
            return null;
        }

        @Override
        protected boolean writeHeader() {
            return true;
        }

        @Override
        Object[] putRowFromStream(Object[] r) throws KettleStepException {
            return prepareOutputRow(r);
        }
    }

    private StepMockHelper<ConcatFieldsMeta, ConcatFieldsData> stepMockHelper;

    private TextFileField textFileField = new TextFileField("Name", 2, "", 10, 20, "", "", "", "");

    private TextFileField textFileField2 = new TextFileField("Surname", 2, "", 10, 20, "", "", "", "");

    private TextFileField[] textFileFields = new TextFileField[]{ textFileField, textFileField2 };

    @Test
    public void testPrepareOutputRow() throws Exception {
        ConcatFieldsTest.ConcatFieldsHandler concatFields = new ConcatFieldsTest.ConcatFieldsHandler(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        Object[] row = new Object[]{ "one", "two" };
        String[] fieldNames = new String[]{ "one", "two" };
        concatFields.setRow(row);
        RowMetaInterface inputRowMeta = Mockito.mock(RowMetaInterface.class);
        Mockito.when(inputRowMeta.clone()).thenReturn(inputRowMeta);
        Mockito.when(inputRowMeta.size()).thenReturn(2);
        Mockito.when(inputRowMeta.getFieldNames()).thenReturn(fieldNames);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getOutputFields()).thenReturn(textFileFields);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isFastDump()).thenReturn(Boolean.TRUE);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isFileAppended()).thenReturn(Boolean.FALSE);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isFileNameInField()).thenReturn(Boolean.FALSE);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isHeaderEnabled()).thenReturn(Boolean.TRUE);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isRemoveSelectedFields()).thenReturn(Boolean.TRUE);
        concatFields.setInputRowMeta(inputRowMeta);
        try {
            concatFields.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface);
            concatFields.prepareOutputRow(row);
        } catch (NullPointerException npe) {
            Assert.fail("NullPointerException issue PDI-8870 still reproduced ");
        }
    }
}

