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
package org.pentaho.di.trans.steps.tableoutput;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.StepMeta;


public class TableOutputTest {
    private DatabaseMeta databaseMeta;

    private StepMeta stepMeta;

    private TableOutput tableOutput;

    private TableOutput tableOutputSpy;

    private TableOutputMeta tableOutputMeta;

    private TableOutputMeta tableOutputMetaSpy;

    private TableOutputData tableOutputData;

    private TableOutputData tableOutputDataSpy;

    private Database db;

    @Test
    public void testWriteToTable() throws Exception {
        tableOutputSpy.writeToTable(Mockito.mock(RowMetaInterface.class), new Object[]{  });
    }

    @Test
    public void testTruncateTable_off() throws Exception {
        tableOutputSpy.truncateTable();
        Mockito.verify(db, Mockito.never()).truncateTable(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void testTruncateTable_on() throws Exception {
        Mockito.when(tableOutputMeta.truncateTable()).thenReturn(true);
        Mockito.when(tableOutputSpy.getCopy()).thenReturn(0);
        Mockito.when(tableOutputSpy.getUniqueStepNrAcrossSlaves()).thenReturn(0);
        tableOutputSpy.truncateTable();
        Mockito.verify(db).truncateTable(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void testTruncateTable_on_PartitionId() throws Exception {
        Mockito.when(tableOutputMeta.truncateTable()).thenReturn(true);
        Mockito.when(tableOutputSpy.getCopy()).thenReturn(1);
        Mockito.when(tableOutputSpy.getUniqueStepNrAcrossSlaves()).thenReturn(0);
        Mockito.when(tableOutputSpy.getPartitionID()).thenReturn("partition id");
        tableOutputSpy.truncateTable();
        Mockito.verify(db).truncateTable(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void testProcessRow_truncatesIfNoRowsAvailable() throws Exception {
        Mockito.when(tableOutputMeta.truncateTable()).thenReturn(true);
        Mockito.doReturn(null).when(tableOutputSpy).getRow();
        boolean result = tableOutputSpy.processRow(tableOutputMeta, tableOutputData);
        Assert.assertFalse(result);
        Mockito.verify(tableOutputSpy).truncateTable();
    }

    @Test
    public void testProcessRow_doesNotTruncateIfNoRowsAvailableAndTruncateIsOff() throws Exception {
        Mockito.when(tableOutputMeta.truncateTable()).thenReturn(false);
        Mockito.doReturn(null).when(tableOutputSpy).getRow();
        boolean result = tableOutputSpy.processRow(tableOutputMeta, tableOutputData);
        Assert.assertFalse(result);
        Mockito.verify(tableOutputSpy, Mockito.never()).truncateTable();
    }

    @Test
    public void testProcessRow_truncatesOnFirstRow() throws Exception {
        Mockito.when(tableOutputMeta.truncateTable()).thenReturn(true);
        Object[] row = new Object[]{  };
        Mockito.doReturn(row).when(tableOutputSpy).getRow();
        try {
            boolean result = tableOutputSpy.processRow(tableOutputMeta, tableOutputData);
        } catch (NullPointerException npe) {
            // not everything is set up to process an entire row, but we don't need that for this test
        }
        Mockito.verify(tableOutputSpy, Mockito.times(1)).truncateTable();
    }

    @Test
    public void testProcessRow_doesNotTruncateOnOtherRows() throws Exception {
        Mockito.when(tableOutputMeta.truncateTable()).thenReturn(true);
        Object[] row = new Object[]{  };
        Mockito.doReturn(row).when(tableOutputSpy).getRow();
        tableOutputSpy.first = false;
        Mockito.doReturn(null).when(tableOutputSpy).writeToTable(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(row.getClass()));
        boolean result = tableOutputSpy.processRow(tableOutputMeta, tableOutputData);
        Assert.assertTrue(result);
        Mockito.verify(tableOutputSpy, Mockito.never()).truncateTable();
    }

    @Test
    public void testInit_unsupportedConnection() {
        TableOutputMeta meta = Mockito.mock(TableOutputMeta.class);
        TableOutputData data = Mockito.mock(TableOutputData.class);
        DatabaseInterface dbInterface = Mockito.mock(DatabaseInterface.class);
        Mockito.doNothing().when(tableOutputSpy).logError(ArgumentMatchers.anyString());
        Mockito.when(meta.getCommitSize()).thenReturn("1");
        Mockito.when(meta.getDatabaseMeta()).thenReturn(databaseMeta);
        Mockito.when(databaseMeta.getDatabaseInterface()).thenReturn(dbInterface);
        String unsupportedTableOutputMessage = "unsupported exception";
        Mockito.when(dbInterface.getUnsupportedTableOutputMessage()).thenReturn(unsupportedTableOutputMessage);
        // Will cause the Kettle Exception
        Mockito.when(dbInterface.supportsStandardTableOutput()).thenReturn(false);
        tableOutputSpy.init(meta, data);
        KettleException ke = new KettleException(unsupportedTableOutputMessage);
        Mockito.verify(tableOutputSpy, Mockito.times(1)).logError(("An error occurred intialising this step: " + (ke.getMessage())));
    }
}

