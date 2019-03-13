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
package org.pentaho.di.core.database;


import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaBinary;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaInternetAddress;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.row.value.ValueMetaTimestamp;

import static DatabaseMeta.TYPE_ACCESS_ODBC;


public class MSAccessDatabaseMetaTest {
    private MSAccessDatabaseMeta odbcMeta;

    @Test
    public void testSettings() throws Exception {
        Assert.assertArrayEquals(new int[]{ TYPE_ACCESS_ODBC }, odbcMeta.getAccessTypeList());
        Assert.assertEquals((-1), odbcMeta.getDefaultDatabasePort());
        Assert.assertTrue(odbcMeta.supportsAutoInc());
        Assert.assertEquals(1, odbcMeta.getNotFoundTK(true));
        Assert.assertEquals(0, odbcMeta.getNotFoundTK(false));
        Assert.assertEquals("sun.jdbc.odbc.JdbcOdbcDriver", odbcMeta.getDriverClass());
        Assert.assertEquals("jdbc:odbc:FOO", odbcMeta.getURL("IGNORED", "IGNORED", "FOO"));
        Assert.assertFalse(odbcMeta.isFetchSizeSupported());
        Assert.assertFalse(odbcMeta.supportsBitmapIndex());
        Assert.assertFalse(odbcMeta.supportsSynonyms());
        Assert.assertFalse(odbcMeta.supportsSetCharacterStream());
        Assert.assertEquals("[FOO].[BAR]", odbcMeta.getSchemaTableCombination("FOO", "BAR"));
        Assert.assertEquals(65536, odbcMeta.getMaxTextFieldLength());
        Assert.assertFalse(odbcMeta.supportsTransactions());
        Assert.assertFalse(odbcMeta.supportsSetLong());
        Assert.assertFalse(odbcMeta.supportsGetBlob());
        Assert.assertFalse(odbcMeta.supportsViews());
        Assert.assertEquals(new String[]{ /* http://support.microsoft.com/kb/q109312 Note that if you set a reference to a type library, an object library,
        or an ActiveX control, that library's reserved words are also reserved words in your database. For example, if
        you add an ActiveX control to a form, a reference is set and the names of the objects, methods, and properties
        of that control become reserved words in your database. For existing objects with names that contain reserved
        words, you can avoid errors by surrounding the object name with brackets [ ], see
        getStartQuote(),getEndQuote().
         */
        "ADD", "ALL", "ALPHANUMERIC", "ALTER", "AND", "ANY", "APPLICATION", "AS", "ASC", "ASSISTANT", "AUTOINCREMENT", "AVG", "BETWEEN", "BINARY", "BIT", "BOOLEAN", "BY", "BYTE", "CHAR", "CHARACTER", "COLUMN", "COMPACTDATABASE", "CONSTRAINT", "CONTAINER", "COUNT", "COUNTER", "CREATE", "CREATEDATABASE", "CREATEFIELD", "CREATEGROUP", "CREATEINDEX", "CREATEOBJECT", "CREATEPROPERTY", "CREATERELATION", "CREATETABLEDEF", "CREATEUSER", "CREATEWORKSPACE", "CURRENCY", "CURRENTUSER", "DATABASE", "DATE", "DATETIME", "DELETE", "DESC", "DESCRIPTION", "DISALLOW", "DISTINCT", "DISTINCTROW", "DOCUMENT", "DOUBLE", "DROP", "ECHO", "ELSE", "END", "EQV", "ERROR", "EXISTS", "EXIT", "FALSE", "FIELD", "FIELDS", "FILLCACHE", "FLOAT", "FLOAT4", "FLOAT8", "FOREIGN", "FORM", "FORMS", "FROM", "FULL", "FUNCTION", "GENERAL", "GETOBJECT", "GETOPTION", "GOTOPAGE", "GROUP", "GUID", "HAVING", "IDLE", "IEEEDOUBLE", "IEEESINGLE", "IF", "IGNORE", "IMP", "IN", "INDEX", "INDEX", "INDEXES", "INNER", "INSERT", "INSERTTEXT", "INT", "INTEGER", "INTEGER1", "INTEGER2", "INTEGER4", "INTO", "IS", "JOIN", "KEY", "LASTMODIFIED", "LEFT", "LEVEL", "LIKE", "LOGICAL", "LOGICAL1", "LONG", "LONGBINARY", "LONGTEXT", "MACRO", "MATCH", "MAX", "MIN", "MOD", "MEMO", "MODULE", "MONEY", "MOVE", "NAME", "NEWPASSWORD", "NO", "NOT", "NULL", "NUMBER", "NUMERIC", "OBJECT", "OLEOBJECT", "OFF", "ON", "OPENRECORDSET", "OPTION", "OR", "ORDER", "OUTER", "OWNERACCESS", "PARAMETER", "PARAMETERS", "PARTIAL", "PERCENT", "PIVOT", "PRIMARY", "PROCEDURE", "PROPERTY", "QUERIES", "QUERY", "QUIT", "REAL", "RECALC", "RECORDSET", "REFERENCES", "REFRESH", "REFRESHLINK", "REGISTERDATABASE", "RELATION", "REPAINT", "REPAIRDATABASE", "REPORT", "REPORTS", "REQUERY", "RIGHT", "SCREEN", "SECTION", "SELECT", "SET", "SETFOCUS", "SETOPTION", "SHORT", "SINGLE", "SMALLINT", "SOME", "SQL", "STDEV", "STDEVP", "STRING", "SUM", "TABLE", "TABLEDEF", "TABLEDEFS", "TABLEID", "TEXT", "TIME", "TIMESTAMP", "TOP", "TRANSFORM", "TRUE", "TYPE", "UNION", "UNIQUE", "UPDATE", "USER", "VALUE", "VALUES", "VAR", "VARP", "VARBINARY", "VARCHAR", "WHERE", "WITH", "WORKSPACE", "XOR", "YEAR", "YES", "YESNO" }, odbcMeta.getReservedWords());
        Assert.assertEquals("[", odbcMeta.getStartQuote());
        Assert.assertEquals("]", odbcMeta.getEndQuote());
        Assert.assertArrayEquals(new String[]{  }, odbcMeta.getUsedLibraries());
    }

    @Test
    public void testSQLStatements() {
        Assert.assertEquals("DELETE FROM FOO", odbcMeta.getTruncateTableStatement("FOO"));
        Assert.assertEquals("ALTER TABLE FOO ADD COLUMN BAR TEXT(15)", odbcMeta.getAddColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals(("ALTER TABLE FOO DROP COLUMN BAR" + (System.getProperty("line.separator"))), odbcMeta.getDropColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("ALTER TABLE FOO ALTER COLUMN BAR TEXT(15)", odbcMeta.getModifyColumnStatement("FOO", new ValueMetaString("BAR", 15, 0), "", false, "", false));
        Assert.assertEquals("insert into FOO(FOOVERSION) values (1)", odbcMeta.getSQLInsertAutoIncUnknownDimensionRow("FOO", "FOOKEY", "FOOVERSION"));
    }

    @Test
    public void testGetFieldDefinition() {
        Assert.assertEquals("FOO DATETIME", odbcMeta.getFieldDefinition(new ValueMetaDate("FOO"), "", "", false, true, false));
        Assert.assertEquals("DATETIME", odbcMeta.getFieldDefinition(new ValueMetaTimestamp("FOO"), "", "", false, false, false));
        Assert.assertFalse(odbcMeta.supportsBooleanDataType());
        Assert.assertEquals("CHAR(1)", odbcMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        odbcMeta.setSupportsBooleanDataType(true);
        Assert.assertEquals("BIT", odbcMeta.getFieldDefinition(new ValueMetaBoolean("FOO"), "", "", false, false, false));
        odbcMeta.setSupportsBooleanDataType(false);
        // Key field Stuff
        Assert.assertEquals("COUNTER PRIMARY KEY", odbcMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 8, 0), "", "FOO", true, false, false));
        Assert.assertEquals("LONG PRIMARY KEY", odbcMeta.getFieldDefinition(new ValueMetaNumber("FOO", 10, 0), "FOO", "", false, false, false));
        Assert.assertEquals("LONG PRIMARY KEY", odbcMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 8, 0), "", "FOO", false, false, false));
        // Integer types
        Assert.assertEquals("INTEGER", odbcMeta.getFieldDefinition(new ValueMetaInteger("FOO", 4, 0), "", "", false, false, false));
        Assert.assertEquals("LONG", odbcMeta.getFieldDefinition(new ValueMetaNumber("FOO", 6, 0), "", "", false, false, false));
        Assert.assertEquals("LONG", odbcMeta.getFieldDefinition(new ValueMetaNumber("FOO", 9, 0), "", "", false, false, false));
        Assert.assertEquals("DOUBLE", odbcMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 10, 0), "", "", false, false, false));
        // Number Types ( as written, precision != 0 )
        Assert.assertEquals("DOUBLE", odbcMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 10, 1), "", "", false, false, false));
        Assert.assertEquals("DOUBLE", odbcMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 3, 1), "", "", false, false, false));
        Assert.assertEquals("DOUBLE", odbcMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", 3, (-5)), "", "", false, false, false));
        Assert.assertEquals("DOUBLE", odbcMeta.getFieldDefinition(new ValueMetaBigNumber("FOO", (-3), (-5)), "", "", false, false, false));
        // String Types
        Assert.assertEquals("TEXT(255)", odbcMeta.getFieldDefinition(new ValueMetaString("FOO", 255, 0), "", "", false, false, false));// Likely a bug - the maxTextFieldLength is set to 65536 - so this limitation is likely wrong

        Assert.assertEquals("TEXT(1)", odbcMeta.getFieldDefinition(new ValueMetaString("FOO", 1, 0), "", "", false, false, false));
        Assert.assertEquals("MEMO", odbcMeta.getFieldDefinition(new ValueMetaString("FOO", 256, 0), "", "", false, false, false));
        Assert.assertEquals("TEXT", odbcMeta.getFieldDefinition(new ValueMetaString("FOO", 0, 0), "", "", false, false, false));
        Assert.assertEquals("TEXT", odbcMeta.getFieldDefinition(new ValueMetaString("FOO"), "", "", false, false, false));
        // Other Types
        Assert.assertEquals(" LONGBINARY", odbcMeta.getFieldDefinition(new ValueMetaBinary("FOO", 200, 1), "", "", false, false, false));
        // Unknowns
        Assert.assertEquals(" UNKNOWN", odbcMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, false));
        Assert.assertEquals((" UNKNOWN" + (System.getProperty("line.separator"))), odbcMeta.getFieldDefinition(new ValueMetaInternetAddress("FOO"), "", "", false, false, true));
    }

    private int rowCnt = 0;

    @Test
    public void testCheckIndexExists() throws Exception {
        Database db = Mockito.mock(Database.class);
        ResultSet rs = Mockito.mock(ResultSet.class);
        DatabaseMetaData dmd = Mockito.mock(DatabaseMetaData.class);
        DatabaseMeta dm = Mockito.mock(DatabaseMeta.class);
        Mockito.when(dm.getQuotedSchemaTableCombination("", "FOO")).thenReturn("FOO");
        Mockito.when(rs.next()).thenAnswer(new Answer<Boolean>() {
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                (rowCnt)++;
                return new Boolean(((rowCnt) < 3));
            }
        });
        Mockito.when(db.getDatabaseMetaData()).thenReturn(dmd);
        Mockito.when(dmd.getIndexInfo(null, null, "FOO", false, true)).thenReturn(rs);
        Mockito.when(rs.getString("COLUMN_NAME")).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if ((rowCnt) == 1) {
                    return "ROW1COL2";
                } else
                    if ((rowCnt) == 2) {
                        return "ROW2COL2";
                    } else {
                        return null;
                    }

            }
        });
        Mockito.when(db.getDatabaseMeta()).thenReturn(dm);
        Assert.assertTrue(odbcMeta.checkIndexExists(db, "", "FOO", new String[]{ "ROW1COL2", "ROW2COL2" }));
        Assert.assertFalse(odbcMeta.checkIndexExists(db, "", "FOO", new String[]{ "ROW2COL2", "NOTTHERE" }));
        Assert.assertFalse(odbcMeta.checkIndexExists(db, "", "FOO", new String[]{ "NOTTHERE", "ROW1COL2" }));
    }
}

