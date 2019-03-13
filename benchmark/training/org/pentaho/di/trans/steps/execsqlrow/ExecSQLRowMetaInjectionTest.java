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
package org.pentaho.di.trans.steps.execsqlrow;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.injection.BaseMetadataInjectionTest;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;


public class ExecSQLRowMetaInjectionTest extends BaseMetadataInjectionTest<ExecSQLRowMeta> {
    @Test
    public void test() throws Exception {
        check("SQL_FIELD_NAME", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getSqlFieldName();
            }
        });
        check("COMMIT_SIZE", new BaseMetadataInjectionTest.IntGetter() {
            @Override
            public int get() {
                return meta.getCommitSize();
            }
        });
        check("READ_SQL_FROM_FILE", new BaseMetadataInjectionTest.BooleanGetter() {
            @Override
            public boolean get() {
                return meta.isSqlFromfile();
            }
        });
        check("SEND_SINGLE_STATEMENT", new BaseMetadataInjectionTest.BooleanGetter() {
            @Override
            public boolean get() {
                return meta.IsSendOneStatement();
            }
        });
        check("UPDATE_STATS", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getUpdateField();
            }
        });
        check("INSERT_STATS", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getInsertField();
            }
        });
        check("DELETE_STATS", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getDeleteField();
            }
        });
        check("READ_STATS", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getReadField();
            }
        });
        skipPropertyTest("CONNECTION_NAME");
        List<DatabaseMeta> databasesList = new ArrayList<>();
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        Mockito.when(dbMeta.getName()).thenReturn("testDBMeta");
        databasesList.add(dbMeta);
        meta.setDatabasesList(databasesList);
        ValueMetaInterface valueMeta = new ValueMetaString("DBMETA");
        injector.setProperty(meta, "CONNECTION_NAME", setValue(valueMeta, "testDBMeta"), "DBMETA");
        Assert.assertEquals("testDBMeta", meta.getDatabaseMeta().getName());
    }
}

