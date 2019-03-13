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


import DatabaseMeta.EMPTY_OPTIONS_STRING;
import StringUtil.EMPTY_STRING;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.variables.Variables;


/**
 * This test is designed to check that jdbc url (with no extra parameters)
 * remains valid (sure, we check syntax only) after adding any extra parameters
 * in spite of number of this parameters and their validity.
 *
 * @author Ivan_Nikolaichuk
 */
public class DatabaseMeta_AppendExtraParamsTest {
    private static final String CONN_TYPE_MSSQL = "MSSQL";

    private static final String STRING_EXTRA_OPTION = "extraOption";

    private static final String STRING_OPTION_VALUE = "value";

    private static final String STRING_DEFAULT = "<def>";

    private DatabaseMeta meta;

    private DatabaseInterface mssqlServerDatabaseMeta;

    private Variables variables;

    private final String CONN_URL_NO_EXTRA_OPTIONS = "jdbc:sqlserver://127.0.0.1:1433";

    @Test
    public void urlNotChanges_WhenNoExtraOptionsGiven() {
        Map<String, String> extraOptions = generateExtraOptions(DatabaseMeta_AppendExtraParamsTest.CONN_TYPE_MSSQL, 0);
        String connUrlWithExtraOptions = meta.appendExtraOptions(CONN_URL_NO_EXTRA_OPTIONS, extraOptions);
        Assert.assertEquals(CONN_URL_NO_EXTRA_OPTIONS, connUrlWithExtraOptions);
    }

    /**
     * Extra option key is expected to be in pattern: ConnType.key If ConnType and key are not divided by point, extra
     * option is considered to be invalid
     */
    @Test
    public void urlNotChanges_WhenExtraOptionIsInvalid() {
        Map<String, String> extraOptions = generateExtraOptions(DatabaseMeta_AppendExtraParamsTest.CONN_TYPE_MSSQL, 0);
        extraOptions.put(DatabaseMeta_AppendExtraParamsTest.STRING_DEFAULT, DatabaseMeta_AppendExtraParamsTest.STRING_DEFAULT);
        String connUrlWithExtraOptions = meta.appendExtraOptions(CONN_URL_NO_EXTRA_OPTIONS, extraOptions);
        Assert.assertEquals(CONN_URL_NO_EXTRA_OPTIONS, connUrlWithExtraOptions);
    }

    @Test
    public void extraOptionsAreNotAppended_WhenTheyAreEmpty() {
        Map<String, String> extraOptions = generateExtraOptions(DatabaseMeta_AppendExtraParamsTest.CONN_TYPE_MSSQL, 0);
        final String validKey = ((DatabaseMeta_AppendExtraParamsTest.CONN_TYPE_MSSQL) + ".") + "key";
        extraOptions.put(validKey, EMPTY_STRING);
        extraOptions.put(validKey, EMPTY_OPTIONS_STRING);
        String connUrlWithExtraOptions = meta.appendExtraOptions(CONN_URL_NO_EXTRA_OPTIONS, extraOptions);
        Assert.assertEquals(CONN_URL_NO_EXTRA_OPTIONS, connUrlWithExtraOptions);
    }

    @Test
    public void extraOptionsAreNotAppended_WhenConnTypePointsToAnotherDataBase() throws Exception {
        Map<String, String> extraOptions = generateExtraOptions(DatabaseMeta_AppendExtraParamsTest.STRING_DEFAULT, 2);
        // emulate that there is no database with STRING_DEFAULT plugin id.
        Mockito.doThrow(new KettleDatabaseException()).when(meta).getDbInterface(DatabaseMeta_AppendExtraParamsTest.STRING_DEFAULT);
        String connUrlWithExtraOptions = meta.appendExtraOptions(CONN_URL_NO_EXTRA_OPTIONS, extraOptions);
        Assert.assertEquals(CONN_URL_NO_EXTRA_OPTIONS, connUrlWithExtraOptions);
    }

    @Test
    public void urlIsValid_AfterAddingValidExtraOptions() {
        Map<String, String> extraOptions = generateExtraOptions(DatabaseMeta_AppendExtraParamsTest.CONN_TYPE_MSSQL, 1);
        String expectedExtraOptionsUrl = ((((DatabaseMeta_AppendExtraParamsTest.STRING_EXTRA_OPTION) + 0) + (mssqlServerDatabaseMeta.getExtraOptionValueSeparator())) + (DatabaseMeta_AppendExtraParamsTest.STRING_OPTION_VALUE)) + 0;
        String expectedUrl = ((CONN_URL_NO_EXTRA_OPTIONS) + (mssqlServerDatabaseMeta.getExtraOptionSeparator())) + expectedExtraOptionsUrl;
        String connUrlWithExtraOptions = meta.appendExtraOptions(CONN_URL_NO_EXTRA_OPTIONS, extraOptions);
        Assert.assertEquals(expectedUrl, connUrlWithExtraOptions);
    }

    @Test
    public void onlyValidExtraOptions_AreAppendedToUrl() {
        Map<String, String> extraOptions = generateExtraOptions(DatabaseMeta_AppendExtraParamsTest.CONN_TYPE_MSSQL, 1);
        extraOptions.put(DatabaseMeta_AppendExtraParamsTest.STRING_DEFAULT, DatabaseMeta_AppendExtraParamsTest.STRING_DEFAULT);
        extraOptions.put((((DatabaseMeta_AppendExtraParamsTest.CONN_TYPE_MSSQL) + ".") + "key1"), EMPTY_STRING);
        extraOptions.put((((DatabaseMeta_AppendExtraParamsTest.CONN_TYPE_MSSQL) + ".") + "key2"), EMPTY_OPTIONS_STRING);
        String expectedExtraOptionsUrl = ((((DatabaseMeta_AppendExtraParamsTest.STRING_EXTRA_OPTION) + 0) + (mssqlServerDatabaseMeta.getExtraOptionValueSeparator())) + (DatabaseMeta_AppendExtraParamsTest.STRING_OPTION_VALUE)) + 0;
        String expectedUrl = ((CONN_URL_NO_EXTRA_OPTIONS) + (mssqlServerDatabaseMeta.getExtraOptionSeparator())) + expectedExtraOptionsUrl;
        String connUrlWithExtraOptions = meta.appendExtraOptions(CONN_URL_NO_EXTRA_OPTIONS, extraOptions);
        Assert.assertEquals(expectedUrl, connUrlWithExtraOptions);
    }
}

