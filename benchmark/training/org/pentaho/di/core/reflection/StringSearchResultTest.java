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
package org.pentaho.di.core.reflection;


import ValueMetaInterface.TYPE_STRING;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;


public class StringSearchResultTest {
    private Class<?> PKG = Const.class;

    @Test
    public void testgetResultRowMeta() {
        RowMetaInterface rm = StringSearchResult.getResultRowMeta();
        Assert.assertNotNull(rm);
        Assert.assertEquals(4, rm.getValueMetaList().size());
        Assert.assertEquals(TYPE_STRING, rm.getValueMeta(0).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "SearchResult.TransOrJob"), rm.getValueMeta(0).getName());
        Assert.assertEquals(TYPE_STRING, rm.getValueMeta(1).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "SearchResult.StepDatabaseNotice"), rm.getValueMeta(1).getName());
        Assert.assertEquals(TYPE_STRING, rm.getValueMeta(2).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "SearchResult.String"), rm.getValueMeta(2).getName());
        Assert.assertEquals(TYPE_STRING, rm.getValueMeta(3).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "SearchResult.FieldName"), rm.getValueMeta(3).getName());
    }
}

