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
package org.pentaho.di.trans.steps.systemdata;


import SystemDataTypes.TYPE_SYSTEM_INFO_HOSTNAME;
import SystemDataTypes.TYPE_SYSTEM_INFO_HOSTNAME_REAL;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * User: Dzmitry Stsiapanau Date: 1/20/14 Time: 12:12 PM
 */
public class SystemDataTest {
    private class SystemDataHandler extends SystemData {
        Object[] row = new Object[]{ "anyData" };

        Object[] outputRow;

        public SystemDataHandler(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
        }

        @SuppressWarnings("unused")
        public void setRow(Object[] row) {
            this.row = row;
        }

        /**
         * In case of getRow, we receive data from previous steps through the input rowset. In case we split the stream, we
         * have to copy the data to the alternate splits: rowsets 1 through n.
         */
        @Override
        public Object[] getRow() throws KettleException {
            return row;
        }

        /**
         * putRow is used to copy a row, to the alternate rowset(s) This should get priority over everything else!
         * (synchronized) If distribute is true, a row is copied only once to the output rowsets, otherwise copies are sent
         * to each rowset!
         *
         * @param row
         * 		The row to put to the destination rowset(s).
         * @throws org.pentaho.di.core.exception.KettleStepException
         * 		
         */
        @Override
        public void putRow(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
            outputRow = row;
        }

        public Object[] getOutputRow() {
            return outputRow;
        }
    }

    private StepMockHelper<SystemDataMeta, SystemDataData> stepMockHelper;

    @Test
    public void testProcessRow() throws Exception {
        SystemDataData systemDataData = new SystemDataData();
        SystemDataMeta systemDataMeta = new SystemDataMeta();
        systemDataMeta.allocate(2);
        String[] names = systemDataMeta.getFieldName();
        SystemDataTypes[] types = systemDataMeta.getFieldType();
        names[0] = "hostname";
        names[1] = "hostname_real";
        types[0] = SystemDataTypes.getTypeFromString(TYPE_SYSTEM_INFO_HOSTNAME.getDescription());
        types[1] = SystemDataTypes.getTypeFromString(TYPE_SYSTEM_INFO_HOSTNAME_REAL.getDescription());
        SystemDataTest.SystemDataHandler systemData = new SystemDataTest.SystemDataHandler(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        Object[] expectedRow = new Object[]{ Const.getHostname(), Const.getHostnameReal() };
        RowMetaInterface inputRowMeta = Mockito.mock(RowMetaInterface.class);
        Mockito.when(inputRowMeta.clone()).thenReturn(inputRowMeta);
        Mockito.when(inputRowMeta.size()).thenReturn(2);
        systemDataData.outputRowMeta = inputRowMeta;
        systemData.init(systemDataMeta, systemDataData);
        Assert.assertFalse(systemData.processRow(systemDataMeta, systemDataData));
        Object[] out = systemData.getOutputRow();
        Assert.assertArrayEquals(expectedRow, out);
    }
}

