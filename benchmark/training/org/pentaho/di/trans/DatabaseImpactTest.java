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
package org.pentaho.di.trans;


import ValueMetaInterface.TYPE_STRING;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.i18n.BaseMessages;

import static DatabaseImpact.TYPE_IMPACT_READ;


public class DatabaseImpactTest {
    private Class<?> PKG = Trans.class;

    @Test
    public void testGetRow() throws KettleValueException {
        DatabaseImpact testObject = new DatabaseImpact(TYPE_IMPACT_READ, "myTrans", "aStep", "ProdDB", "DimCustomer", "Customer_Key", "MyValue", "Calculator 2", "SELECT * FROM dimCustomer", "Some remarks");
        RowMetaAndData rmd = testObject.getRow();
        Assert.assertNotNull(rmd);
        Assert.assertEquals(10, rmd.size());
        Assert.assertEquals(TYPE_STRING, rmd.getValueMeta(0).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "DatabaseImpact.RowDesc.Label.Type"), rmd.getValueMeta(0).getName());
        Assert.assertEquals("Read", rmd.getString(0, "default"));
        Assert.assertEquals(TYPE_STRING, rmd.getValueMeta(1).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "DatabaseImpact.RowDesc.Label.Transformation"), rmd.getValueMeta(1).getName());
        Assert.assertEquals("myTrans", rmd.getString(1, "default"));
        Assert.assertEquals(TYPE_STRING, rmd.getValueMeta(2).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "DatabaseImpact.RowDesc.Label.Step"), rmd.getValueMeta(2).getName());
        Assert.assertEquals("aStep", rmd.getString(2, "default"));
        Assert.assertEquals(TYPE_STRING, rmd.getValueMeta(3).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "DatabaseImpact.RowDesc.Label.Database"), rmd.getValueMeta(3).getName());
        Assert.assertEquals("ProdDB", rmd.getString(3, "default"));
        Assert.assertEquals(TYPE_STRING, rmd.getValueMeta(4).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "DatabaseImpact.RowDesc.Label.Table"), rmd.getValueMeta(4).getName());
        Assert.assertEquals("DimCustomer", rmd.getString(4, "default"));
        Assert.assertEquals(TYPE_STRING, rmd.getValueMeta(5).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "DatabaseImpact.RowDesc.Label.Field"), rmd.getValueMeta(5).getName());
        Assert.assertEquals("Customer_Key", rmd.getString(5, "default"));
        Assert.assertEquals(TYPE_STRING, rmd.getValueMeta(6).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "DatabaseImpact.RowDesc.Label.Value"), rmd.getValueMeta(6).getName());
        Assert.assertEquals("MyValue", rmd.getString(6, "default"));
        Assert.assertEquals(TYPE_STRING, rmd.getValueMeta(7).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "DatabaseImpact.RowDesc.Label.ValueOrigin"), rmd.getValueMeta(7).getName());
        Assert.assertEquals("Calculator 2", rmd.getString(7, "default"));
        Assert.assertEquals(TYPE_STRING, rmd.getValueMeta(8).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "DatabaseImpact.RowDesc.Label.SQL"), rmd.getValueMeta(8).getName());
        Assert.assertEquals("SELECT * FROM dimCustomer", rmd.getString(8, "default"));
        Assert.assertEquals(TYPE_STRING, rmd.getValueMeta(9).getType());
        Assert.assertEquals(BaseMessages.getString(PKG, "DatabaseImpact.RowDesc.Label.Remarks"), rmd.getValueMeta(9).getName());
        Assert.assertEquals("Some remarks", rmd.getString(9, "default"));
    }
}

