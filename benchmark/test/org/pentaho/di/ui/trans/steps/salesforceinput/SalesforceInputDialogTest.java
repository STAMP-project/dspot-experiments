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
package org.pentaho.di.ui.trans.steps.salesforceinput;


import com.sforce.ws.bind.XmlObject;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class SalesforceInputDialogTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static final String VALUE = "value";

    private static boolean changedPropsUi;

    private SalesforceInputDialog dialog;

    @Test
    public void testAddFieldsFromSOQLQuery() throws Exception {
        final Set<String> fields = new LinkedHashSet<>();
        XmlObject testObject = createObject("Field1", SalesforceInputDialogTest.VALUE, SalesforceInputDialogTest.ObjectType.XMLOBJECT);
        dialog.addFields("", fields, testObject);
        dialog.addFields("", fields, testObject);
        Assert.assertArrayEquals("No duplicates", new String[]{ "Field1" }, fields.toArray());
        testObject = createObject("Field2", SalesforceInputDialogTest.VALUE, SalesforceInputDialogTest.ObjectType.XMLOBJECT);
        dialog.addFields("", fields, testObject);
        Assert.assertArrayEquals("Two fields", new String[]{ "Field1", "Field2" }, fields.toArray());
    }

    @Test
    public void testAddFields_nullIdNotAdded() throws Exception {
        final Set<String> fields = new LinkedHashSet<>();
        XmlObject testObject = createObject("Id", null, SalesforceInputDialogTest.ObjectType.XMLOBJECT);
        dialog.addFields("", fields, testObject);
        Assert.assertArrayEquals("Null Id field not added", new String[]{  }, fields.toArray());
    }

    @Test
    public void testAddFields_IdAdded() throws Exception {
        final Set<String> fields = new LinkedHashSet<>();
        XmlObject testObject = createObject("Id", SalesforceInputDialogTest.VALUE, SalesforceInputDialogTest.ObjectType.XMLOBJECT);
        dialog.addFields("", fields, testObject);
        Assert.assertArrayEquals("Id field added", new String[]{ "Id" }, fields.toArray());
    }

    @Test
    public void testAddFields_nullRelationalIdNotAdded() throws Exception {
        final Set<String> fields = new LinkedHashSet<>();
        XmlObject complexObject = createObject("Module2", null, SalesforceInputDialogTest.ObjectType.SOBJECT);
        complexObject.addField("Id", createObject("Id", null, SalesforceInputDialogTest.ObjectType.XMLOBJECT));
        dialog.addFields("", fields, complexObject);
        Assert.assertArrayEquals("Relational null Id not added", new String[]{  }, fields.toArray());
    }

    @Test
    public void testAddFields_relationalIdAdded() throws Exception {
        final Set<String> fields = new LinkedHashSet<>();
        XmlObject complexObject = createObject("Module2", null, SalesforceInputDialogTest.ObjectType.SOBJECT);
        complexObject.addField("Id", createObject("Id", SalesforceInputDialogTest.VALUE, SalesforceInputDialogTest.ObjectType.XMLOBJECT));
        complexObject.addField("Name", createObject("Name", SalesforceInputDialogTest.VALUE, SalesforceInputDialogTest.ObjectType.XMLOBJECT));
        dialog.addFields("", fields, complexObject);
        Assert.assertArrayEquals("Relational fields added", new String[]{ "Module2.Id", "Module2.Name" }, fields.toArray());
    }

    private enum ObjectType {

        SOBJECT,
        XMLOBJECT;}
}

