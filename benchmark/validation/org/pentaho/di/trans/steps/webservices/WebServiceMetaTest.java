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
package org.pentaho.di.trans.steps.webservices;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.utils.TestUtils;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;


public class WebServiceMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testLoadXml() throws Exception {
        Node node = getTestNode();
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        WebServiceMeta webServiceMeta = new WebServiceMeta(node, Collections.singletonList(dbMeta), metastore);
        Assert.assertEquals("httpUser", webServiceMeta.getHttpLogin());
        Assert.assertEquals("tryandguess", webServiceMeta.getHttpPassword());
        Assert.assertEquals("http://webservices.gama-system.com/exchangerates.asmx?WSDL", webServiceMeta.getUrl());
        Assert.assertEquals("GetCurrentExchangeRate", webServiceMeta.getOperationName());
        Assert.assertEquals("opRequestName", webServiceMeta.getOperationRequestName());
        Assert.assertEquals("GetCurrentExchangeRateResult", webServiceMeta.getOutFieldArgumentName());
        Assert.assertEquals("aProxy", webServiceMeta.getProxyHost());
        Assert.assertEquals("4444", webServiceMeta.getProxyPort());
        Assert.assertEquals(1, webServiceMeta.getCallStep());
        Assert.assertFalse(webServiceMeta.isPassingInputData());
        Assert.assertTrue(webServiceMeta.isCompatible());
        Assert.assertFalse(webServiceMeta.isReturningReplyAsString());
        List<WebServiceField> fieldsIn = webServiceMeta.getFieldsIn();
        Assert.assertEquals(3, fieldsIn.size());
        assertWebServiceField(fieldsIn.get(0), "Bank", "strBank", "string", 2);
        assertWebServiceField(fieldsIn.get(1), "ToCurrency", "strCurrency", "string", 2);
        assertWebServiceField(fieldsIn.get(2), "Rank", "intRank", "int", 5);
        List<WebServiceField> fieldsOut = webServiceMeta.getFieldsOut();
        Assert.assertEquals(1, fieldsOut.size());
        assertWebServiceField(fieldsOut.get(0), "GetCurrentExchangeRateResult", "GetCurrentExchangeRateResult", "decimal", 6);
        WebServiceMeta clone = webServiceMeta.clone();
        Assert.assertNotSame(clone, webServiceMeta);
        Assert.assertEquals(clone.getXML(), webServiceMeta.getXML());
    }

    @Test
    public void testReadRep() throws Exception {
        Repository rep = Mockito.mock(Repository.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        StringObjectId id_step = new StringObjectId("oid");
        Mockito.when(rep.getStepAttributeString(id_step, "wsOperation")).thenReturn("GetCurrentExchangeRate");
        Mockito.when(rep.getStepAttributeString(id_step, "wsOperationRequest")).thenReturn("opRequest");
        Mockito.when(rep.getStepAttributeString(id_step, "wsOperationNamespace")).thenReturn("opNamespace");
        Mockito.when(rep.getStepAttributeString(id_step, "wsInFieldContainer")).thenReturn("ifc");
        Mockito.when(rep.getStepAttributeString(id_step, "wsInFieldArgument")).thenReturn("ifa");
        Mockito.when(rep.getStepAttributeString(id_step, "wsOutFieldContainer")).thenReturn("ofc");
        Mockito.when(rep.getStepAttributeString(id_step, "wsOutFieldArgument")).thenReturn("ofa");
        Mockito.when(rep.getStepAttributeString(id_step, "proxyHost")).thenReturn("phost");
        Mockito.when(rep.getStepAttributeString(id_step, "proxyPort")).thenReturn("1234");
        Mockito.when(rep.getStepAttributeString(id_step, "httpLogin")).thenReturn("user");
        Mockito.when(rep.getStepAttributeString(id_step, "httpPassword")).thenReturn("password");
        Mockito.when(rep.getStepAttributeInteger(id_step, "callStep")).thenReturn(2L);
        Mockito.when(rep.getStepAttributeBoolean(id_step, "passingInputData")).thenReturn(true);
        Mockito.when(rep.getStepAttributeBoolean(id_step, 0, "compatible", true)).thenReturn(false);
        Mockito.when(rep.getStepAttributeString(id_step, "repeating_element")).thenReturn("repeat");
        Mockito.when(rep.getStepAttributeBoolean(id_step, 0, "reply_as_string")).thenReturn(true);
        Mockito.when(rep.countNrStepAttributes(id_step, "fieldIn_ws_name")).thenReturn(2);
        Mockito.when(rep.getStepAttributeString(id_step, 0, "fieldIn_name")).thenReturn("bank");
        Mockito.when(rep.getStepAttributeString(id_step, 0, "fieldIn_ws_name")).thenReturn("inBank");
        Mockito.when(rep.getStepAttributeString(id_step, 0, "fieldIn_xsd_type")).thenReturn("string");
        Mockito.when(rep.getStepAttributeString(id_step, 1, "fieldIn_name")).thenReturn("branch");
        Mockito.when(rep.getStepAttributeString(id_step, 1, "fieldIn_ws_name")).thenReturn("inBranch");
        Mockito.when(rep.getStepAttributeString(id_step, 1, "fieldIn_xsd_type")).thenReturn("string");
        Mockito.when(rep.countNrStepAttributes(id_step, "fieldOut_ws_name")).thenReturn(2);
        Mockito.when(rep.getStepAttributeString(id_step, 0, "fieldOut_name")).thenReturn("balance");
        Mockito.when(rep.getStepAttributeString(id_step, 0, "fieldOut_ws_name")).thenReturn("outBalance");
        Mockito.when(rep.getStepAttributeString(id_step, 0, "fieldOut_xsd_type")).thenReturn("int");
        Mockito.when(rep.getStepAttributeString(id_step, 1, "fieldOut_name")).thenReturn("transactions");
        Mockito.when(rep.getStepAttributeString(id_step, 1, "fieldOut_ws_name")).thenReturn("outTransactions");
        Mockito.when(rep.getStepAttributeString(id_step, 1, "fieldOut_xsd_type")).thenReturn("int");
        WebServiceMeta webServiceMeta = new WebServiceMeta(rep, metastore, id_step, Collections.singletonList(dbMeta));
        String expectedXml = "" + (((((((((((((((((((((((((((((((((((((((("    <wsURL/>\n" + "    <wsOperation>GetCurrentExchangeRate</wsOperation>\n") + "    <wsOperationRequest>opRequest</wsOperationRequest>\n") + "    <wsOperationNamespace>opNamespace</wsOperationNamespace>\n") + "    <wsInFieldContainer>ifc</wsInFieldContainer>\n") + "    <wsInFieldArgument>ifa</wsInFieldArgument>\n") + "    <wsOutFieldContainer>ofc</wsOutFieldContainer>\n") + "    <wsOutFieldArgument>ofa</wsOutFieldArgument>\n") + "    <proxyHost>phost</proxyHost>\n") + "    <proxyPort>1234</proxyPort>\n") + "    <httpLogin>user</httpLogin>\n") + "    <httpPassword>password</httpPassword>\n") + "    <callStep>2</callStep>\n") + "    <passingInputData>Y</passingInputData>\n") + "    <compatible>N</compatible>\n") + "    <repeating_element>repeat</repeating_element>\n") + "    <reply_as_string>Y</reply_as_string>\n") + "    <fieldsIn>\n") + "    <field>\n") + "        <name>bank</name>\n") + "        <wsName>inBank</wsName>\n") + "        <xsdType>string</xsdType>\n") + "    </field>\n") + "    <field>\n") + "        <name>branch</name>\n") + "        <wsName>inBranch</wsName>\n") + "        <xsdType>string</xsdType>\n") + "    </field>\n") + "      </fieldsIn>\n") + "    <fieldsOut>\n") + "    <field>\n") + "        <name>balance</name>\n") + "        <wsName>outBalance</wsName>\n") + "        <xsdType>int</xsdType>\n") + "    </field>\n") + "    <field>\n") + "        <name>transactions</name>\n") + "        <wsName>outTransactions</wsName>\n") + "        <xsdType>int</xsdType>\n") + "    </field>\n") + "      </fieldsOut>\n");
        String actualXml = TestUtils.toUnixLineSeparators(webServiceMeta.getXML());
        Assert.assertEquals(expectedXml, actualXml);
    }

    @Test
    public void testSaveRep() throws Exception {
        Node node = getTestNode();
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        Repository rep = Mockito.mock(Repository.class);
        WebServiceMeta webServiceMeta = new WebServiceMeta();
        webServiceMeta.loadXML(node, Collections.singletonList(dbMeta), metastore);
        StringObjectId aTransId = new StringObjectId("aTransId");
        StringObjectId aStepId = new StringObjectId("aStepId");
        webServiceMeta.saveRep(rep, metastore, aTransId, aStepId);
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "wsUrl", "http://webservices.gama-system.com/exchangerates.asmx?WSDL");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "wsOperation", "GetCurrentExchangeRate");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "wsOperationRequest", "opRequestName");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "wsOperationNamespace", "http://www.gama-system.com/webservices");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "wsInFieldContainer", null);
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "wsInFieldArgument", null);
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "wsOutFieldContainer", "GetCurrentExchangeRateResult");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "wsOutFieldArgument", "GetCurrentExchangeRateResult");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "proxyHost", "aProxy");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "proxyPort", "4444");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "httpLogin", "httpUser");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "httpPassword", "tryandguess");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "callStep", 1);
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "passingInputData", false);
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "compatible", true);
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "repeating_element", null);
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, "reply_as_string", false);
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 0, "fieldIn_name", "Bank");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 0, "fieldIn_ws_name", "strBank");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 0, "fieldIn_xsd_type", "string");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 1, "fieldIn_name", "ToCurrency");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 1, "fieldIn_ws_name", "strCurrency");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 1, "fieldIn_xsd_type", "string");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 2, "fieldIn_name", "Rank");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 2, "fieldIn_ws_name", "intRank");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 2, "fieldIn_xsd_type", "int");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 0, "fieldOut_name", "GetCurrentExchangeRateResult");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 0, "fieldOut_ws_name", "GetCurrentExchangeRateResult");
        Mockito.verify(rep).saveStepAttribute(aTransId, aStepId, 0, "fieldOut_xsd_type", "decimal");
        Mockito.verifyNoMoreInteractions(rep);
    }

    @Test
    public void testGetFields() throws Exception {
        WebServiceMeta webServiceMeta = new WebServiceMeta();
        webServiceMeta.setDefault();
        RowMetaInterface rmi = Mockito.mock(RowMetaInterface.class);
        RowMetaInterface rmi2 = Mockito.mock(RowMetaInterface.class);
        StepMeta nextStep = Mockito.mock(StepMeta.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        Repository rep = Mockito.mock(Repository.class);
        WebServiceField field1 = new WebServiceField();
        field1.setName("field1");
        field1.setWsName("field1WS");
        field1.setXsdType("string");
        WebServiceField field2 = new WebServiceField();
        field2.setName("field2");
        field2.setWsName("field2WS");
        field2.setXsdType("string");
        WebServiceField field3 = new WebServiceField();
        field3.setName("field3");
        field3.setWsName("field3WS");
        field3.setXsdType("string");
        webServiceMeta.setFieldsOut(Arrays.asList(field1, field2, field3));
        webServiceMeta.getFields(rmi, "idk", new RowMetaInterface[]{ rmi2 }, nextStep, new Variables(), rep, metastore);
        Mockito.verify(rmi).addValueMeta(ArgumentMatchers.argThat(matchValueMetaString("field1")));
        Mockito.verify(rmi).addValueMeta(ArgumentMatchers.argThat(matchValueMetaString("field2")));
        Mockito.verify(rmi).addValueMeta(ArgumentMatchers.argThat(matchValueMetaString("field3")));
    }

    @Test
    public void testCheck() throws Exception {
        WebServiceMeta webServiceMeta = new WebServiceMeta();
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        RowMetaInterface prev = Mockito.mock(RowMetaInterface.class);
        RowMetaInterface info = Mockito.mock(RowMetaInterface.class);
        Repository rep = Mockito.mock(Repository.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        String[] input = new String[]{ "one" };
        ArrayList<CheckResultInterface> remarks = new ArrayList<>();
        webServiceMeta.check(remarks, transMeta, stepMeta, null, input, null, info, new Variables(), rep, metastore);
        Assert.assertEquals(2, remarks.size());
        Assert.assertEquals("Not receiving any fields from previous steps!", remarks.get(0).getText());
        Assert.assertEquals("Step is receiving info from other steps.", remarks.get(1).getText());
        remarks.clear();
        webServiceMeta.setInFieldArgumentName("ifan");
        Mockito.when(prev.size()).thenReturn(2);
        webServiceMeta.check(remarks, transMeta, stepMeta, prev, new String[]{  }, null, info, new Variables(), rep, metastore);
        Assert.assertEquals(2, remarks.size());
        Assert.assertEquals("Step is connected to previous one, receiving 2 fields", remarks.get(0).getText());
        Assert.assertEquals("No input received from other steps!", remarks.get(1).getText());
    }

    @Test
    public void testGetFieldOut() throws Exception {
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        WebServiceMeta webServiceMeta = new WebServiceMeta(getTestNode(), Collections.singletonList(dbMeta), metastore);
        Assert.assertNull(webServiceMeta.getFieldOutFromWsName("", true));
        Assert.assertEquals("GetCurrentExchangeRateResult", webServiceMeta.getFieldOutFromWsName("GetCurrentExchangeRateResult", false).getName());
        Assert.assertEquals("GetCurrentExchangeRateResult", webServiceMeta.getFieldOutFromWsName("something:GetCurrentExchangeRateResult", true).getName());
    }
}

