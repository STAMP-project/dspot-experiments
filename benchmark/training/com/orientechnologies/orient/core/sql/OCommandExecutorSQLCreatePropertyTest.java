/**
 * *  Copyright 2015 OrientDB LTD (info(at)orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientdb.com
 */
package com.orientechnologies.orient.core.sql;


import OStatement.CUSTOM_STRICT_SQL;
import OType.EMBEDDEDLIST;
import OType.INTEGER;
import OType.LINK;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michael MacFadden
 */
public class OCommandExecutorSQLCreatePropertyTest {
    private static final String PROP_NAME = "name";

    private static final String PROP_FULL_NAME = "company.name";

    private static final String PROP_DIVISION = "division";

    private static final String PROP_FULL_DIVISION = "company.division";

    private static final String PROP_OFFICERS = "officers";

    private static final String PROP_FULL_OFFICERS = "company.officers";

    private static final String PROP_ID = "id";

    private static final String PROP_FULL_ID = "company.id";

    @Test
    public void testBasicCreateProperty() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE class company")).execute();
        db.command(new OCommandSQL("CREATE property company.name STRING")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty nameProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_NAME);
        Assert.assertEquals(nameProperty.getType(), STRING);
        TestCase.assertFalse(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
        db.close();
    }

    @Test
    public void testBasicUnsafeCreateProperty() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE class company")).execute();
        db.command(new OCommandSQL("CREATE property company.name STRING UNSAFE")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty nameProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_NAME);
        Assert.assertEquals(nameProperty.getType(), STRING);
        TestCase.assertFalse(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
        db.close();
    }

    @Test
    public void testCreatePropertyWithLinkedClass() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE class division")).execute();
        db.command(new OCommandSQL("CREATE class company")).execute();
        db.command(new OCommandSQL("CREATE property company.division LINK division")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty nameProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_DIVISION);
        Assert.assertEquals(nameProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_DIVISION);
        Assert.assertEquals(nameProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_DIVISION);
        Assert.assertEquals(nameProperty.getType(), LINK);
        Assert.assertEquals(nameProperty.getLinkedClass().getName(), "division");
        TestCase.assertFalse(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
        db.close();
    }

    @Test
    public void testCreatePropertyWithEmbeddedType() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE Class company")).execute();
        db.command(new OCommandSQL("CREATE Property company.officers EMBEDDEDLIST STRING")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty nameProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_OFFICERS);
        Assert.assertEquals(nameProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(nameProperty.getLinkedType(), STRING);
        TestCase.assertFalse(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
        db.close();
    }

    @Test
    public void testCreateMandatoryProperty() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE class company")).execute();
        db.command(new OCommandSQL("CREATE property company.name STRING (MANDATORY)")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty nameProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_NAME);
        Assert.assertTrue(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
        db.close();
    }

    @Test
    public void testCreateNotNullProperty() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE class company")).execute();
        db.command(new OCommandSQL("CREATE property company.name STRING (NOTNULL)")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty nameProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_NAME);
        TestCase.assertFalse(nameProperty.isMandatory());
        Assert.assertTrue(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
        db.close();
    }

    @Test
    public void testCreateReadOnlyProperty() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE class company")).execute();
        db.command(new OCommandSQL("CREATE property company.name STRING (READONLY)")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty nameProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_NAME);
        TestCase.assertFalse(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        Assert.assertTrue(nameProperty.isReadonly());
        db.close();
    }

    @Test
    public void testCreateReadOnlyFalseProperty() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE class company")).execute();
        db.command(new OCommandSQL("CREATE property company.name STRING (READONLY false)")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty nameProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_NAME);
        TestCase.assertFalse(nameProperty.isReadonly());
        db.close();
    }

    @Test
    public void testCreateMandatoryPropertyWithEmbeddedType() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE Class company")).execute();
        db.command(new OCommandSQL("CREATE Property company.officers EMBEDDEDLIST STRING (MANDATORY)")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty nameProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_OFFICERS);
        Assert.assertEquals(nameProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(nameProperty.getLinkedType(), STRING);
        Assert.assertTrue(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
        db.close();
    }

    @Test
    public void testCreateUnsafePropertyWithEmbeddedType() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE Class company")).execute();
        db.command(new OCommandSQL("CREATE Property company.officers EMBEDDEDLIST STRING UNSAFE")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty nameProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_OFFICERS);
        Assert.assertEquals(nameProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(nameProperty.getLinkedType(), STRING);
        db.close();
    }

    @Test
    public void testComplexCreateProperty() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE Class company")).execute();
        db.command(new OCommandSQL("CREATE Property company.officers EMBEDDEDLIST STRING (MANDATORY, READONLY, NOTNULL) UNSAFE")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty nameProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_OFFICERS);
        Assert.assertEquals(nameProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(nameProperty.getLinkedType(), STRING);
        Assert.assertTrue(nameProperty.isMandatory());
        Assert.assertTrue(nameProperty.isNotNull());
        Assert.assertTrue(nameProperty.isReadonly());
        db.close();
    }

    @Test
    public void testLinkedTypeDefaultAndMinMaxUnsafeProperty() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE CLASS company")).execute();
        db.command(new OCommandSQL("CREATE PROPERTY company.id EMBEDDEDLIST Integer (DEFAULT 5, MIN 1, MAX 10) UNSAFE")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty idProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_ID);
        Assert.assertEquals(idProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_ID);
        Assert.assertEquals(idProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_ID);
        Assert.assertEquals(idProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(idProperty.getLinkedType(), INTEGER);
        TestCase.assertFalse(idProperty.isMandatory());
        TestCase.assertFalse(idProperty.isNotNull());
        TestCase.assertFalse(idProperty.isReadonly());
        Assert.assertEquals(idProperty.getDefaultValue(), "5");
        Assert.assertEquals(idProperty.getMin(), "1");
        Assert.assertEquals(idProperty.getMax(), "10");
        db.close();
    }

    @Test
    public void testDefaultAndMinMaxUnsafeProperty() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE CLASS company")).execute();
        db.command(new OCommandSQL("CREATE PROPERTY company.id INTEGER (DEFAULT 5, MIN 1, MAX 10) UNSAFE")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty idProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_ID);
        Assert.assertEquals(idProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_ID);
        Assert.assertEquals(idProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_ID);
        Assert.assertEquals(idProperty.getType(), INTEGER);
        Assert.assertEquals(idProperty.getLinkedType(), null);
        TestCase.assertFalse(idProperty.isMandatory());
        TestCase.assertFalse(idProperty.isNotNull());
        TestCase.assertFalse(idProperty.isReadonly());
        Assert.assertEquals(idProperty.getDefaultValue(), "5");
        Assert.assertEquals(idProperty.getMin(), "1");
        Assert.assertEquals(idProperty.getMax(), "10");
        db.close();
    }

    @Test
    public void testExtraSpaces() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE CLASS company")).execute();
        db.command(new OCommandSQL("CREATE PROPERTY company.id INTEGER  ( DEFAULT  5 ,  MANDATORY  )  UNSAFE ")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty idProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_ID);
        Assert.assertEquals(idProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_ID);
        Assert.assertEquals(idProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_ID);
        Assert.assertEquals(idProperty.getType(), INTEGER);
        Assert.assertEquals(idProperty.getLinkedType(), null);
        Assert.assertTrue(idProperty.isMandatory());
        Assert.assertEquals(idProperty.getDefaultValue(), "5");
        db.close();
    }

    @Test
    public void testNonStrict() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.getStorage().setProperty(CUSTOM_STRICT_SQL, "false");
        db.command(new OCommandSQL("CREATE CLASS company")).execute();
        db.command(new OCommandSQL("CREATE PROPERTY company.id INTEGER (MANDATORY, NOTNULL false, READONLY true, MAX 10, MIN 4, DEFAULT 6)  UNSAFE")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OProperty idProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_ID);
        Assert.assertEquals(idProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_ID);
        Assert.assertEquals(idProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_ID);
        Assert.assertEquals(idProperty.getType(), INTEGER);
        Assert.assertEquals(idProperty.getLinkedType(), null);
        Assert.assertTrue(idProperty.isMandatory());
        TestCase.assertFalse(idProperty.isNotNull());
        Assert.assertTrue(idProperty.isReadonly());
        Assert.assertEquals(idProperty.getMin(), "4");
        Assert.assertEquals(idProperty.getMax(), "10");
        Assert.assertEquals(idProperty.getDefaultValue(), "6");
        db.close();
    }

    @Test(expected = OCommandSQLParsingException.class)
    public void testInvalidAttributeName() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE CLASS company")).execute();
        try {
            db.command(new OCommandSQL("CREATE PROPERTY company.id INTEGER (MANDATORY, INVALID, NOTNULL)  UNSAFE")).execute();
        } finally {
            db.close();
        }
    }

    @Test(expected = OCommandSQLParsingException.class)
    public void testMissingAttributeValue() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE CLASS company")).execute();
        try {
            db.command(new OCommandSQL("CREATE PROPERTY company.id INTEGER (DEFAULT)  UNSAFE")).execute();
        } finally {
            db.close();
        }
    }

    @Test(expected = OCommandSQLParsingException.class)
    public void tooManyAttributeParts() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE CLASS company")).execute();
        try {
            db.command(new OCommandSQL("CREATE PROPERTY company.id INTEGER (DEFAULT 5 10)  UNSAFE")).execute();
        } finally {
            db.close();
        }
    }

    @Test
    public void testMandatoryAsLinkedName() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE CLASS company")).execute();
        db.command(new OCommandSQL("CREATE CLASS Mandatory")).execute();
        db.command(new OCommandSQL("CREATE PROPERTY company.id EMBEDDEDLIST Mandatory UNSAFE")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("company");
        OClass mandatoryClass = db.getMetadata().getSchema().getClass("Mandatory");
        OProperty idProperty = companyClass.getProperty(OCommandExecutorSQLCreatePropertyTest.PROP_ID);
        Assert.assertEquals(idProperty.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_ID);
        Assert.assertEquals(idProperty.getFullName(), OCommandExecutorSQLCreatePropertyTest.PROP_FULL_ID);
        Assert.assertEquals(idProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(idProperty.getLinkedClass(), mandatoryClass);
        TestCase.assertFalse(idProperty.isMandatory());
        db.close();
    }

    @Test
    public void testIfNotExists() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:OCommandExecutorSQLCreatePropertyTest" + (System.nanoTime())));
        db.create();
        db.command(new OCommandSQL("CREATE class testIfNotExists")).execute();
        db.command(new OCommandSQL("CREATE property testIfNotExists.name if not exists STRING")).execute();
        OClass companyClass = db.getMetadata().getSchema().getClass("testIfNotExists");
        OProperty property = companyClass.getProperty("name");
        Assert.assertEquals(property.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        db.command(new OCommandSQL("CREATE property testIfNotExists.name if not exists STRING")).execute();
        companyClass = db.getMetadata().getSchema().getClass("testIfNotExists");
        property = companyClass.getProperty("name");
        Assert.assertEquals(property.getName(), OCommandExecutorSQLCreatePropertyTest.PROP_NAME);
        db.drop();
    }
}

