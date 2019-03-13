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
package org.pentaho.di.trans.steps.salesforce;


import Connector.END_POINT;
import SalesforceConnectionUtils.recordsFilterDesc.length;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.bind.XmlObject;
import com.sforce.ws.wsdl.Constants;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import javax.xml.namespace.QName;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class SalesforceConnectionTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private LogChannelInterface logInterface = Mockito.mock(LogChannelInterface.class);

    private String url = "url";

    private String username = "username";

    private String password = "password";

    private int recordsFilter = 0;

    @Test
    public void testConstructor_emptyUrl() throws KettleException {
        try {
            new SalesforceConnection(logInterface, null, username, password);
            Assert.fail();
        } catch (KettleException expected) {
            // OK
        }
    }

    @Test
    public void testConstructor_emptyUserName() throws KettleException {
        try {
            new SalesforceConnection(logInterface, url, null, password);
            Assert.fail();
        } catch (KettleException expected) {
            // OK
        }
    }

    @Test
    public void testSetCalendarStartNull() throws KettleException {
        SalesforceConnection connection = new SalesforceConnection(logInterface, url, username, password);
        GregorianCalendar endDate = new GregorianCalendar(2000, 2, 10);
        try {
            connection.setCalendar(recordsFilter, null, endDate);
            Assert.fail();
        } catch (KettleException expected) {
            // OK
        }
    }

    @Test
    public void testSetCalendarEndNull() throws KettleException {
        SalesforceConnection connection = new SalesforceConnection(logInterface, url, username, password);
        GregorianCalendar startDate = new GregorianCalendar(2000, 2, 10);
        try {
            connection.setCalendar(recordsFilter, startDate, null);
            Assert.fail();
        } catch (KettleException expected) {
            // OK
        }
    }

    @Test
    public void testSetCalendarStartDateTooOlder() throws KettleException {
        SalesforceConnection connection = new SalesforceConnection(logInterface, url, username, password);
        GregorianCalendar startDate = new GregorianCalendar(2000, 3, 20);
        GregorianCalendar endDate = new GregorianCalendar(2000, 2, 10);
        try {
            connection.setCalendar(recordsFilter, startDate, endDate);
            Assert.fail();
        } catch (KettleException expected) {
            // OK
        }
    }

    @Test
    public void testSetCalendarDatesTooFarApart() throws KettleException {
        SalesforceConnection connection = new SalesforceConnection(logInterface, url, username, password);
        GregorianCalendar startDate = new GregorianCalendar(2000, 1, 1);
        GregorianCalendar endDate = new GregorianCalendar(2000, 2, 11);
        try {
            connection.setCalendar(recordsFilter, startDate, endDate);
            Assert.fail();
        } catch (KettleException expected) {
            // OK
        }
    }

    @Test
    public void testConstructor() {
        SalesforceConnection conn;
        // Test all-invalid parameters
        try {
            conn = null;
            conn = new SalesforceConnection(null, null, null, null);
            Assert.fail();
        } catch (KettleException expected) {
            // Ignore, expected result
        }
        // Test null Log Interface
        try {
            conn = null;
            conn = new SalesforceConnection(null, "http://localhost:1234", "anonymous", "mypwd");
            Assert.assertTrue(((conn.getURL().length()) > 0));
            Assert.assertTrue(((conn.getUsername().length()) > 0));
            Assert.assertTrue(((conn.getPassword().length()) > 0));
        } catch (KettleException e) {
            Assert.fail();
        }
        // Test valid Log Interface
        try {
            conn = null;
            conn = new SalesforceConnection(new LogChannel(this), "http://localhost:1234", "anonymous", "mypwd");
            Assert.assertTrue(((conn.getURL().length()) > 0));
            Assert.assertTrue(((conn.getUsername().length()) > 0));
            Assert.assertTrue(((conn.getPassword().length()) > 0));
        } catch (KettleException e) {
            Assert.fail();
        }
        // Test missing target URL (should fail)
        try {
            conn = null;
            conn = new SalesforceConnection(null, null, "anonymous", "mypwd");
            Assert.fail();
        } catch (KettleException expected) {
            // Ignore, expected result
        }
        // Test missing username (should fail)
        try {
            conn = null;
            conn = new SalesforceConnection(null, "http://localhost:1234", null, "mypwd");
            Assert.fail();
        } catch (KettleException expected) {
            // Ignore, expected result
        }
        // Test missing password (should fail)
        try {
            conn = null;
            conn = new SalesforceConnection(null, "http://localhost:1234", "anonymous", null);
            Assert.assertTrue(((conn.getURL().length()) > 0));
            Assert.assertEquals("anonymous", conn.getUsername());
        } catch (KettleException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSetCalendar() {
        SalesforceConnection conn = Mockito.mock(SalesforceConnection.class, Mockito.CALLS_REAL_METHODS);
        // Test valid data
        try {
            conn.setCalendar(new Random().nextInt(length), new GregorianCalendar(2016, Calendar.JANUARY, 1), new GregorianCalendar(2016, Calendar.JANUARY, 31));
            // No errors detected
        } catch (KettleException e) {
            Assert.fail();
        }
        // Test reversed dates (should fail)
        try {
            conn.setCalendar(new Random().nextInt(length), new GregorianCalendar(2016, Calendar.JANUARY, 31), new GregorianCalendar(2016, Calendar.JANUARY, 1));
            Assert.fail();
        } catch (KettleException expected) {
            // Ignore, expected result
        }
        // Test null start date (should fail)
        try {
            conn.setCalendar(new Random().nextInt(length), null, new GregorianCalendar(2016, Calendar.JANUARY, 31));
            Assert.fail();
        } catch (KettleException expected) {
            // Ignore, expected result
        }
        // Test null end date (should fail)
        try {
            conn.setCalendar(new Random().nextInt(length), new GregorianCalendar(2016, Calendar.JANUARY, 1), null);
            Assert.fail();
        } catch (KettleException expected) {
            // Ignore, expected result
        }
    }

    @Test
    public void testMessageElements() throws Exception {
        XmlObject me = SalesforceConnection.fromTemplateElement("myName", 123, false);
        Assert.assertNotNull(me);
        Assert.assertEquals("myName", me.getName().getLocalPart());
        Assert.assertNull(me.getValue());
        me = null;
        me = SalesforceConnection.fromTemplateElement("myName", 123, true);
        Assert.assertNotNull(me);
        Assert.assertEquals("myName", me.getName().getLocalPart());
        Assert.assertEquals(123, me.getValue());
        me = null;
        me = SalesforceConnection.createMessageElement("myName", 123, false);
        Assert.assertNotNull(me);
        Assert.assertEquals("myName", me.getName().getLocalPart());
        Assert.assertEquals(123, me.getValue());
        me = null;
        try {
            me = SalesforceConnection.createMessageElement("myName", 123, true);
            Assert.fail();
        } catch (Exception expected) {
            // Ignore, name was expected to have a colon ':'
        }
        me = null;
        try {
            me = SalesforceConnection.createMessageElement("myType:Name", "123", true);
            Assert.assertNotNull(me);
            Assert.assertEquals("Name", me.getName().getLocalPart());
            Assert.assertEquals("myType", me.getField("type"));
            Assert.assertEquals("123", me.getField("Name"));
        } catch (Exception expected) {
            Assert.fail();
        }
        me = null;
        try {
            me = SalesforceConnection.createMessageElement("myType:Name/MyLookupField", 123, true);
            Assert.assertNotNull(me);
            Assert.assertEquals("MyLookupField", me.getName().getLocalPart());
            // assertEquals( "myType", me.getField( "type" ) );
            Assert.assertEquals(123, me.getField("Name"));
        } catch (Exception expected) {
            Assert.fail();
        }
    }

    @Test
    public void testCreateBinding() throws ConnectionException, KettleException {
        SalesforceConnection conn = new SalesforceConnection(null, "http://localhost:1234", "aUser", "aPass");
        ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(END_POINT);
        config.setManualLogin(true);// Required to prevent connection attempt during test

        Assert.assertNull(conn.getBinding());
        conn.createBinding(config);
        PartnerConnection binding1 = conn.getBinding();
        conn.createBinding(config);
        PartnerConnection binding2 = conn.getBinding();
        Assert.assertSame(binding1, binding2);
    }

    // PDI-15973
    @Test
    public void testGetRecordValue() throws Exception {
        // PDI-15973
        SalesforceConnection conn = Mockito.mock(SalesforceConnection.class, Mockito.CALLS_REAL_METHODS);
        SObject sObject = new SObject();
        sObject.setName(new QName(Constants.PARTNER_SOBJECT_NS, "sObject"));
        SObject testObject = createObject("field", "value");
        sObject.addField("field", testObject);
        Assert.assertEquals("Get value of simple record", "value", conn.getRecordValue(sObject, "field"));
        SObject parentObject = createObject("parentField", null);
        sObject.addField("parentField", parentObject);
        SObject childObject = createObject("subField", "subValue");
        parentObject.addField("subField", childObject);
        Assert.assertEquals("Get value of record with hierarchy", "subValue", conn.getRecordValue(sObject, "parentField.subField"));
        XmlObject nullObject = new XmlObject(new QName("nullField"));
        sObject.addField("nullField", nullObject);
        Assert.assertEquals("Get null value when relational query id is null", null, conn.getRecordValue(sObject, "nullField.childField"));
    }

    // PDI-16459
    @Test
    public void getFieldsTest() throws KettleException {
        String name = "name";
        SalesforceConnection conn = new SalesforceConnection(null, "http://localhost:1234", "aUser", "aPass");
        Field[] fields = new Field[1];
        Field field = new Field();
        field.setRelationshipName("Parent");
        field.setName(name);
        fields[0] = field;
        String[] names = conn.getFields(fields);
        Assert.assertEquals(name, names[0]);
    }
}

