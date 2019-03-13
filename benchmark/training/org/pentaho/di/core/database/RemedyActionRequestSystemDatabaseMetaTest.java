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
package org.pentaho.di.core.database;


import org.junit.Assert;
import org.junit.Test;

import static DatabaseMeta.TYPE_ACCESS_JNDI;
import static DatabaseMeta.TYPE_ACCESS_ODBC;


public class RemedyActionRequestSystemDatabaseMetaTest {
    RemedyActionRequestSystemDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, odbcMeta.getAccessTypeList());
        Assert.assertEquals(1, odbcMeta.getNotFoundTK(true));
        Assert.assertEquals(0, odbcMeta.getNotFoundTK(false));
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:WIBBLE", odbcMeta.getURL("FOO", "BAR", "WIBBLE"));
        Assert.assertFalse(odbcMeta.isFetchSizeSupported());
        Assert.assertFalse(odbcMeta.supportsBitmapIndex());
        Assert.assertFalse(odbcMeta.isRequiringTransactionsOnQueries());
        Assert.assertFalse(odbcMeta.supportsViews());
    }
}

