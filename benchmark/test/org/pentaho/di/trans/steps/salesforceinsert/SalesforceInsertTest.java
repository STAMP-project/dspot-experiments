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
package org.pentaho.di.trans.steps.salesforceinsert;


import Constants.PARTNER_SOBJECT_NS;
import com.sforce.ws.bind.XmlObject;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.pentaho.di.trans.steps.salesforce.SalesforceConnection;


public class SalesforceInsertTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static final String ACCOUNT_EXT_ID_ACCOUNT_ID_C_ACCOUNT = "Account:ExtID_AccountId__c/Account";

    private static final String ACCOUNT_ID = "AccountId";

    private StepMockHelper<SalesforceInsertMeta, SalesforceInsertData> smh;

    @Test
    public void testWriteToSalesForceForNullExtIdField_WithExtIdNO() throws Exception {
        SalesforceInsert sfInputStep = new SalesforceInsert(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        SalesforceInsertMeta meta = generateSalesforceInsertMeta(new String[]{ SalesforceInsertTest.ACCOUNT_ID }, new Boolean[]{ false });
        SalesforceInsertData data = generateSalesforceInsertData();
        sfInputStep.init(meta, data);
        RowMeta rowMeta = new RowMeta();
        ValueMetaBase valueMeta = new ValueMetaString("AccNoExtId");
        rowMeta.addValueMeta(valueMeta);
        smh.initStepDataInterface.inputRowMeta = rowMeta;
        sfInputStep.writeToSalesForce(new Object[]{ null });
        Assert.assertEquals(1, data.sfBuffer[0].getFieldsToNull().length);
        Assert.assertEquals(SalesforceInsertTest.ACCOUNT_ID, data.sfBuffer[0].getFieldsToNull()[0]);
        Assert.assertNull(SalesforceConnection.getChildren(data.sfBuffer[0]));
    }

    @Test
    public void testWriteToSalesForceForNullExtIdField_WithExtIdYES() throws Exception {
        SalesforceInsert sfInputStep = new SalesforceInsert(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        SalesforceInsertMeta meta = generateSalesforceInsertMeta(new String[]{ SalesforceInsertTest.ACCOUNT_EXT_ID_ACCOUNT_ID_C_ACCOUNT }, new Boolean[]{ true });
        SalesforceInsertData data = generateSalesforceInsertData();
        sfInputStep.init(meta, data);
        RowMeta rowMeta = new RowMeta();
        ValueMetaBase valueMeta = new ValueMetaString("AccExtId");
        rowMeta.addValueMeta(valueMeta);
        smh.initStepDataInterface.inputRowMeta = rowMeta;
        sfInputStep.writeToSalesForce(new Object[]{ null });
        Assert.assertEquals(1, data.sfBuffer[0].getFieldsToNull().length);
        Assert.assertEquals(SalesforceInsertTest.ACCOUNT_ID, data.sfBuffer[0].getFieldsToNull()[0]);
        Assert.assertNull(SalesforceConnection.getChildren(data.sfBuffer[0]));
    }

    @Test
    public void testWriteToSalesForceForNotNullExtIdField_WithExtIdNO() throws Exception {
        SalesforceInsert sfInputStep = new SalesforceInsert(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        SalesforceInsertMeta meta = generateSalesforceInsertMeta(new String[]{ SalesforceInsertTest.ACCOUNT_ID }, new Boolean[]{ false });
        SalesforceInsertData data = generateSalesforceInsertData();
        sfInputStep.init(meta, data);
        RowMeta rowMeta = new RowMeta();
        ValueMetaBase valueMeta = new ValueMetaString("AccNoExtId");
        rowMeta.addValueMeta(valueMeta);
        smh.initStepDataInterface.inputRowMeta = rowMeta;
        sfInputStep.writeToSalesForce(new Object[]{ "001i000001c5Nv9AAE" });
        Assert.assertEquals(0, data.sfBuffer[0].getFieldsToNull().length);
        Assert.assertEquals(1, SalesforceConnection.getChildren(data.sfBuffer[0]).length);
        Assert.assertEquals(PARTNER_SOBJECT_NS, SalesforceConnection.getChildren(data.sfBuffer[0])[0].getName().getNamespaceURI());
        Assert.assertEquals(SalesforceInsertTest.ACCOUNT_ID, SalesforceConnection.getChildren(data.sfBuffer[0])[0].getName().getLocalPart());
        Assert.assertEquals("001i000001c5Nv9AAE", SalesforceConnection.getChildren(data.sfBuffer[0])[0].getValue());
        Assert.assertFalse(SalesforceConnection.getChildren(data.sfBuffer[0])[0].hasChildren());
    }

    @Test
    public void testWriteToSalesForceForNotNullExtIdField_WithExtIdYES() throws Exception {
        SalesforceInsert sfInputStep = new SalesforceInsert(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        SalesforceInsertMeta meta = generateSalesforceInsertMeta(new String[]{ SalesforceInsertTest.ACCOUNT_EXT_ID_ACCOUNT_ID_C_ACCOUNT }, new Boolean[]{ true });
        SalesforceInsertData data = generateSalesforceInsertData();
        sfInputStep.init(meta, data);
        RowMeta rowMeta = new RowMeta();
        ValueMetaBase valueMeta = new ValueMetaString("AccExtId");
        rowMeta.addValueMeta(valueMeta);
        smh.initStepDataInterface.inputRowMeta = rowMeta;
        sfInputStep.writeToSalesForce(new Object[]{ "tkas88" });
        Assert.assertEquals(0, data.sfBuffer[0].getFieldsToNull().length);
        Assert.assertEquals(1, SalesforceConnection.getChildren(data.sfBuffer[0]).length);
        Assert.assertEquals(PARTNER_SOBJECT_NS, SalesforceConnection.getChildren(data.sfBuffer[0])[0].getName().getNamespaceURI());
        Assert.assertEquals("Account", SalesforceConnection.getChildren(data.sfBuffer[0])[0].getName().getLocalPart());
        Assert.assertNull(SalesforceConnection.getChildren(data.sfBuffer[0])[0].getValue());
        Assert.assertFalse(SalesforceConnection.getChildren(data.sfBuffer[0])[0].hasChildren());
    }

    @Test
    public void testLogMessageInDetailedModeFotWriteToSalesForce() throws KettleException {
        SalesforceInsert sfInputStep = new SalesforceInsert(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        SalesforceInsertMeta meta = generateSalesforceInsertMeta(new String[]{ SalesforceInsertTest.ACCOUNT_ID }, new Boolean[]{ false });
        SalesforceInsertData data = generateSalesforceInsertData();
        sfInputStep.init(meta, data);
        Mockito.when(sfInputStep.getLogChannel().isDetailed()).thenReturn(true);
        RowMeta rowMeta = new RowMeta();
        ValueMetaBase valueMeta = new ValueMetaString("AccNoExtId");
        rowMeta.addValueMeta(valueMeta);
        smh.initStepDataInterface.inputRowMeta = rowMeta;
        Mockito.verify(sfInputStep.getLogChannel(), Mockito.never()).logDetailed(ArgumentMatchers.anyString());
        sfInputStep.writeToSalesForce(new Object[]{ "001i000001c5Nv9AAE" });
        Mockito.verify(sfInputStep.getLogChannel()).logDetailed("Called writeToSalesForce with 0 out of 2");
    }

    @Test
    public void testWriteToSalesForcePentahoIntegerValue() throws Exception {
        SalesforceInsert sfInputStep = new SalesforceInsert(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        SalesforceInsertMeta meta = generateSalesforceInsertMeta(new String[]{ SalesforceInsertTest.ACCOUNT_ID }, new Boolean[]{ false });
        SalesforceInsertData data = generateSalesforceInsertData();
        sfInputStep.init(meta, data);
        RowMeta rowMeta = new RowMeta();
        ValueMetaBase valueMeta = new ValueMetaInteger("IntValue");
        rowMeta.addValueMeta(valueMeta);
        smh.initStepDataInterface.inputRowMeta = rowMeta;
        sfInputStep.writeToSalesForce(new Object[]{ 1L });
        XmlObject sObject = data.sfBuffer[0].getChild(SalesforceInsertTest.ACCOUNT_ID);
        Assert.assertEquals(sObject.getValue(), 1);
    }
}

