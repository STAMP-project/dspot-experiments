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
package org.pentaho.di.job.entries.columnsexist;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;

import static junit.framework.Assert.assertTrue;


/**
 * Unit tests for column exist job entry.
 *
 * @author Tim Ryzhov
 * @since 21-03-2017
 */
public class JobEntryColumnsExistTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static final String TABLENAME = "TABLE";

    private static final String SCHEMANAME = "SCHEMA";

    private static final String[] COLUMNS = new String[]{ "COLUMN1", "COLUMN2" };

    private JobEntryColumnsExist jobEntry;

    private Database db;

    @Test
    public void jobFail_tableNameIsEmpty() throws KettleException {
        jobEntry.setTablename(null);
        final Result result = jobEntry.execute(new Result(), 0);
        Assert.assertEquals("Should be error", 1, result.getNrErrors());
        Assert.assertFalse("Result should be false", result.getResult());
    }

    @Test
    public void jobFail_columnsArrayIsEmpty() throws KettleException {
        jobEntry.setArguments(null);
        final Result result = jobEntry.execute(new Result(), 0);
        Assert.assertEquals("Should be error", 1, result.getNrErrors());
        Assert.assertFalse("Result should be false", result.getResult());
    }

    @Test
    public void jobFail_connectionIsNull() throws KettleException {
        jobEntry.setDatabase(null);
        final Result result = jobEntry.execute(new Result(), 0);
        Assert.assertEquals("Should be error", 1, result.getNrErrors());
        Assert.assertFalse("Result should be false", result.getResult());
    }

    @Test
    public void jobFail_tableNotExist() throws KettleException {
        Mockito.when(jobEntry.getNewDatabaseFromMeta()).thenReturn(db);
        Mockito.doNothing().when(db).connect(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.doReturn(false).when(db).checkTableExists(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        final Result result = jobEntry.execute(new Result(), 0);
        Assert.assertEquals("Should be error", 1, result.getNrErrors());
        Assert.assertFalse("Result should be false", result.getResult());
        Mockito.verify(db, Mockito.atLeastOnce()).disconnect();
    }

    @Test
    public void jobFail_columnNotExist() throws KettleException {
        Mockito.doReturn(db).when(jobEntry).getNewDatabaseFromMeta();
        Mockito.doNothing().when(db).connect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(true).when(db).checkTableExists(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(false).when(db).checkColumnExists(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        final Result result = jobEntry.execute(new Result(), 0);
        Assert.assertEquals("Should be some errors", 1, result.getNrErrors());
        Assert.assertFalse("Result should be false", result.getResult());
        Mockito.verify(db, Mockito.atLeastOnce()).disconnect();
    }

    @Test
    public void jobSuccess() throws KettleException {
        Mockito.doReturn(db).when(jobEntry).getNewDatabaseFromMeta();
        Mockito.doNothing().when(db).connect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(true).when(db).checkColumnExists(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(true).when(db).checkTableExists(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        final Result result = jobEntry.execute(new Result(), 0);
        Assert.assertEquals("Should be no error", 0, result.getNrErrors());
        assertTrue("Result should be true", result.getResult());
        Assert.assertEquals("Lines written", JobEntryColumnsExistTest.COLUMNS.length, result.getNrLinesWritten());
        Mockito.verify(db, Mockito.atLeastOnce()).disconnect();
    }
}

