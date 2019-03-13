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


import BaseDatabaseMeta.poolingParameters;
import ValueMetaInterface.TYPE_STRING;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.RowMetaAndData;


public class DatabaseConnectionPoolParameterTest {
    @Test
    public void testGetRowList() {
        List<RowMetaAndData> result = DatabaseConnectionPoolParameter.getRowList(poolingParameters, "myTitleParameter", "myTitleDefaultValue", "myTitleDescription");
        Assert.assertNotNull(result);
        for (RowMetaAndData rmd : result) {
            Assert.assertEquals(3, rmd.getRowMeta().size());
            Assert.assertEquals("myTitleParameter", rmd.getRowMeta().getValueMeta(0).getName());
            Assert.assertEquals(TYPE_STRING, rmd.getRowMeta().getValueMeta(0).getType());
            Assert.assertEquals("myTitleDefaultValue", rmd.getRowMeta().getValueMeta(1).getName());
            Assert.assertEquals(TYPE_STRING, rmd.getRowMeta().getValueMeta(1).getType());
            Assert.assertEquals("myTitleDescription", rmd.getRowMeta().getValueMeta(2).getName());
            Assert.assertEquals(TYPE_STRING, rmd.getRowMeta().getValueMeta(2).getType());
        }
    }
}

