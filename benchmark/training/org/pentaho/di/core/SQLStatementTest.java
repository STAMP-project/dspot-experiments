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
package org.pentaho.di.core;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;


public class SQLStatementTest {
    @Test
    public void testClass() throws KettleException {
        final String name = "stepName";
        final DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        final String sql = "sql string";
        final String error = "error";
        SQLStatement statement = new SQLStatement(name, dbMeta, sql);
        Assert.assertSame(name, statement.getStepname());
        Assert.assertSame(dbMeta, statement.getDatabase());
        Assert.assertTrue(statement.hasSQL());
        Assert.assertSame(sql, statement.getSQL());
        statement.setStepname(null);
        Assert.assertNull(statement.getStepname());
        statement.setDatabase(null);
        Assert.assertNull(statement.getDatabase());
        statement.setSQL(null);
        Assert.assertNull(statement.getSQL());
        Assert.assertFalse(statement.hasSQL());
        Assert.assertFalse(statement.hasError());
        statement.setError(error);
        Assert.assertTrue(statement.hasError());
        Assert.assertSame(error, statement.getError());
    }
}

