/**
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * **************************************************************************
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
 */
package org.pentaho.di.core.row.value;


import Const.KETTLE_COMPATIBILITY_DB_IGNORE_TIMEZONE;
import ValueMetaInterface.STORAGE_TYPE_NORMAL;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class ValueMetaBaseSetPreparedStmntValueTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    private DatabaseMeta dbMeta;

    private PreparedStatement ps;

    private Date date;

    private Timestamp ts;

    @Test
    public void testXMLParsingWithNoDataFormatLocale() throws IOException {
        ValueMetaInterface r1 = new ValueMetaString("value");
        r1.setDateFormatLocale(null);
        RowMetaInterface row = new RowMeta();
        row.setValueMetaList(new ArrayList<ValueMetaInterface>(Arrays.asList(r1)));
        row.getMetaXML();
    }

    @Test
    public void testDateRegular() throws Exception {
        System.setProperty(KETTLE_COMPATIBILITY_DB_IGNORE_TIMEZONE, "N");
        ValueMetaBase valueMeta = new ValueMetaDate("", (-1), (-1));
        valueMeta.setPrecision(1);
        valueMeta.setPreparedStatementValue(dbMeta, ps, 1, date);
        Mockito.verify(ps).setDate(ArgumentMatchers.eq(1), ArgumentMatchers.any(java.sql.Date.class), ArgumentMatchers.any(Calendar.class));
    }

    @Test
    public void testDateIgnoreTZ() throws Exception {
        System.setProperty(KETTLE_COMPATIBILITY_DB_IGNORE_TIMEZONE, "Y");
        ValueMetaBase valueMeta = new ValueMetaDate("", (-1), (-1));
        valueMeta.setPrecision(1);
        valueMeta.setPreparedStatementValue(dbMeta, ps, 1, date);
        Mockito.verify(ps).setDate(ArgumentMatchers.eq(1), ArgumentMatchers.any(java.sql.Date.class));
    }

    @Test
    public void testTimestampRegular() throws Exception {
        System.setProperty(KETTLE_COMPATIBILITY_DB_IGNORE_TIMEZONE, "N");
        ValueMetaBase valueMeta = new ValueMetaDate("", (-1), (-1));
        valueMeta.setPreparedStatementValue(dbMeta, ps, 1, ts);
        Mockito.verify(ps).setTimestamp(ArgumentMatchers.eq(1), ArgumentMatchers.any(Timestamp.class), ArgumentMatchers.any(Calendar.class));
    }

    @Test
    public void testTimestampIgnoreTZ() throws Exception {
        System.setProperty(KETTLE_COMPATIBILITY_DB_IGNORE_TIMEZONE, "Y");
        ValueMetaBase valueMeta = new ValueMetaDate("", (-1), (-1));
        valueMeta.setPreparedStatementValue(dbMeta, ps, 1, ts);
        Mockito.verify(ps).setTimestamp(ArgumentMatchers.eq(1), ArgumentMatchers.any(Timestamp.class));
    }

    @Test
    public void testConvertedTimestampRegular() throws Exception {
        System.setProperty(KETTLE_COMPATIBILITY_DB_IGNORE_TIMEZONE, "N");
        ValueMetaBase valueMeta = new ValueMetaDate("", (-1), (-1));
        valueMeta.setPreparedStatementValue(dbMeta, ps, 1, date);
        valueMeta.setStorageType(STORAGE_TYPE_NORMAL);
        Mockito.verify(ps).setTimestamp(ArgumentMatchers.eq(1), ArgumentMatchers.any(Timestamp.class), ArgumentMatchers.any(Calendar.class));
    }

    @Test
    public void testConvertedTimestampIgnoreTZ() throws Exception {
        System.setProperty(KETTLE_COMPATIBILITY_DB_IGNORE_TIMEZONE, "Y");
        ValueMetaBase valueMeta = new ValueMetaDate("", (-1), (-1));
        valueMeta.setPreparedStatementValue(dbMeta, ps, 1, date);
        valueMeta.setStorageType(STORAGE_TYPE_NORMAL);
        Mockito.verify(ps).setTimestamp(ArgumentMatchers.eq(1), ArgumentMatchers.any(Timestamp.class));
    }
}

