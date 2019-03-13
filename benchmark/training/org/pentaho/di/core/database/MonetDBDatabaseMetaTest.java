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


import MonetDBDatabaseMeta.safeModeLocal;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaInternetAddress;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.row.value.ValueMetaTimestamp;

import static DatabaseMeta.TYPE_ACCESS_JNDI;
import static DatabaseMeta.TYPE_ACCESS_NATIVE;
import static DatabaseMeta.TYPE_ACCESS_ODBC;


public class MonetDBDatabaseMetaTest {
    private MonetDBDatabaseMeta nativeMeta;

    private MonetDBDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_NATIVE, TYPE_ACCESS_ODBC, TYPE_ACCESS_JNDI }, nativeMeta.getAccessTypeList());
        Assert.assertEquals(50000, nativeMeta.getDefaultDatabasePort());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertTrue(nativeMeta.supportsAutoInc());
        Assert.assertEquals(1, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(false));
        Assert.assertEquals("nl.cwi.monetdb.jdbc.MonetDriver", nativeMeta.getDriverClass());
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertEquals("jdbc:monetdb://FOO:BAR/WIBBLE", nativeMeta.getURL("FOO", "BAR", "WIBBLE"));
        Assert.assertEquals("jdbc:monetdb://FOO/WIBBLE", nativeMeta.getURL("FOO", "", "WIBBLE"));
        Assert.assertFalse(nativeMeta.isFetchSizeSupported());
        Assert.assertTrue(nativeMeta.supportsBitmapIndex());
        Assert.assertFalse(nativeMeta.supportsSynonyms());
        Assert.assertTrue(nativeMeta.supportsBatchUpdates());
        Assert.assertTrue(nativeMeta.supportsSetMaxRows());
        Assert.assertArrayEquals(new String[]{ "monetdb-jdbc-2.8.jar" }, nativeMeta.getUsedLibraries());
        Assert.assertArrayEquals(new String[]{ "IS", "ISNULL", "NOTNULL", "IN", "BETWEEN", "OVERLAPS", "LIKE", "ILIKE", "NOT", "AND", "OR", "CHAR", "VARCHAR", "CLOB", "BLOB", "DECIMAL", "DEC", "NUMERIC", "TINYINT", "SMALLINT", "INT", "BIGINT", "REAL", "DOUBLE", "BOOLEAN", "DATE", "TIME", "TIMESTAMP", "INTERVAL", "YEAR", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND", "TIMEZONE", "EXTRACT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "LOCALTIME", "LOCALTIMESTAMP", "CURRENT_TIME", "SERIAL", "START", "WITH", "INCREMENT", "CACHE", "CYCLE", "SEQUENCE", "GETANCHOR", "GETBASENAME", "GETCONTENT", "GETCONTEXT", "GETDOMAIN", "GETEXTENSION", "GETFILE", "GETHOST", "GETPORT", "GETPROTOCOL", "GETQUERY", "GETUSER", "GETROBOTURL", "ISURL", "NEWURL", "BROADCAST", "MASKLEN", "SETMASKLEN", "NETMASK", "HOSTMASK", "NETWORK", "TEXT", "ABBREV", "CREATE", "TYPE", "NAME", "DROP", "USER" }, nativeMeta.getReservedWords());
        Assert.assertTrue(nativeMeta.supportsResultSetMetadataRetrievalOnly());
        Assert.assertEquals(Integer.MAX_VALUE, nativeMeta.getMaxVARCHARLength());
        Assert.assertTrue(nativeMeta.supportsSequences());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("ALTER TABLE FOO ADD BAR VARCHAR(15)", nativeMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO MODIFY BAR VARCHAR(15)", nativeMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("insert into FOO(FOOKEY, FOOVERSION) values (0, 1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
        Assert.assertEquals("DELETE FROM FOO", nativeMeta.getTruncateTableStatement("FOO"));
        Assert.assertEquals("SELECT * FROM FOO;", nativeMeta.getSQLQueryFields("FOO"));// Pretty sure the semicolon shouldn't be there - likely bug.

        Assert.assertEquals("SELECT name FROM sys.sequences", nativeMeta.getSQLListOfSequences());
        Assert.assertEquals("SELECT * FROM sys.sequences WHERE name = 'FOO'", nativeMeta.getSQLSequenceExists("FOO"));
        Assert.assertEquals("SELECT get_value_for( 'sys', 'FOO' )", nativeMeta.getSQLCurrentSequenceValue("FOO"));
        Assert.assertEquals("SELECT next_value_for( 'sys', 'FOO' )", nativeMeta.getSQLNextSequenceValue("FOO"));
    }

    @Test
    public void testGetFieldDefinition() {
        Assert.assertEquals("FOO TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("TIMESTAMP", nativeMeta.getFieldDefinition(new ValueMetaTimestamp("FOO"), "", "", false, false, false));
        // Simple hack to prevent duplication of code. Checking the case of supported boolean type
        // both supported and unsupported. Should return BOOLEAN if supported, or CHAR(1) if not.
        String[] typeCk = new String[]{ "CHAR(1)", "BOOLEAN", "CHAR(1)" };
        int i = (nativeMeta.supportsBooleanDataType()) ? 1 : 0;
        Assert.assertEquals(typeCk[i], nativeMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        odbcMeta.setSupportsBooleanDataType((!(odbcMeta.supportsBooleanDataType())));
        Assert.assertEquals(typeCk[(i + 1)], odbcMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        odbcMeta.setSupportsBooleanDataType((!(odbcMeta.supportsBooleanDataType())));
        Assert.assertEquals("SERIAL", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 8, 0), "", "FOO", true, false, false));
        Assert.assertEquals("BIGINT", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "FOO", "", false, false, false));
        Assert.assertEquals("BIGINT", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 8, 0), "", "FOO", false, false, false));
        // integer types ( precision == 0 )
        Assert.assertEquals("BIGINT", nativeMeta.getFieldDefinition(new ValueMetaInteger("FOO", 8, 0), "", "", false, false, false));
        Assert.assertEquals("BIGINT", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "", "", false, false, false));
        Assert.assertEquals("DECIMAL(19)", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 19, 0), "", "", false, false, false));
        Assert.assertEquals("DOUBLE", nativeMeta.getFieldDefinition(new ValueMetaNumber("FOO", 8, 0), "", "", false, false, false));
        // Numerics with precisions
        Assert.assertEquals("DECIMAL(19, 5)", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 19, 5), "", "", false, false, false));
        Assert.assertEquals("DECIMAL(19)", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 19, (-5)), "", "", false, false, false));
        Assert.assertEquals("DOUBLE", nativeMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 11, 5), "", "", false, false, false));
        // String Types
        Assert.assertEquals("VARCHAR(10)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", 10, 0), "", "", false, false, false));
        // This next one is a bug:
        // getMaxVARCHARLength = Integer.MAX_VALUE,
        // if statement for CLOB trips if length > getMaxVARCHARLength()
        // length is of type int - so this could never happen
        Assert.assertEquals("VARCHAR()", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", ((nativeMeta.getMaxVARCHARLength()) + 1), 0), "", "", false, false, false));
        Assert.assertEquals("VARCHAR()", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", (-2), 0), "", "", false, false, false));// should end up with (100) if "safeMode = true"

        safeModeLocal.set(new Boolean(true));
        Assert.assertEquals("VARCHAR(100)", nativeMeta.getFieldDefinition(new ValueMetaString("FOO", (-2), 0), "", "", false, false, false));// should end up with (100) if "safeMode = true"

        Assert.assertEquals(" UNKNOWN", nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), nativeMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }
}

