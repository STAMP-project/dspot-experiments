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


import DatabaseMeta.TYPE_ACCESS_NATIVE;
import DatabaseMeta.TYPE_ACCESS_ODBC;
import org.junit.Assert;
import org.junit.Test;


public class InfobrightDatabaseMetaTest extends MySQLDatabaseMetaTest {
    @Test
    public void mysqlTestOverrides() throws Exception {
        InfobrightDatabaseMeta idm = new InfobrightDatabaseMeta();
        idm.setAccessType(TYPE_ACCESS_NATIVE);
        Assert.assertEquals(5029, idm.getDefaultDatabasePort());
        idm.setAccessType(TYPE_ACCESS_ODBC);
        Assert.assertEquals((-1), idm.getDefaultDatabasePort());
    }
}

