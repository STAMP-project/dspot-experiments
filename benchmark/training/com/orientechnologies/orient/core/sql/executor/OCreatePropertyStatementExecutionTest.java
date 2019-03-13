package com.orientechnologies.orient.core.sql.executor;


import OType.EMBEDDEDLIST;
import OType.INTEGER;
import OType.LINK;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OCreatePropertyStatementExecutionTest {
    static ODatabaseDocument db;

    private static final String PROP_NAME = "name";

    private static final String PROP_DIVISION = "division";

    private static final String PROP_OFFICERS = "officers";

    private static final String PROP_ID = "id";

    @Test
    public void testBasicCreateProperty() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE class testBasicCreateProperty").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE property testBasicCreateProperty.name STRING").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testBasicCreateProperty");
        OProperty nameProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), "testBasicCreateProperty.name");
        Assert.assertEquals(nameProperty.getType(), STRING);
        TestCase.assertFalse(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
    }

    @Test
    public void testBasicUnsafeCreateProperty() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE class testBasicUnsafeCreateProperty").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE property testBasicUnsafeCreateProperty.name STRING UNSAFE").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testBasicUnsafeCreateProperty");
        OProperty nameProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), "testBasicUnsafeCreateProperty.name");
        Assert.assertEquals(nameProperty.getType(), STRING);
        TestCase.assertFalse(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
    }

    @Test
    public void testCreatePropertyWithLinkedClass() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE class testCreatePropertyWithLinkedClass_1").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE class testCreatePropertyWithLinkedClass_2").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE property testCreatePropertyWithLinkedClass_2.division LINK testCreatePropertyWithLinkedClass_1").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testCreatePropertyWithLinkedClass_2");
        OProperty nameProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_DIVISION);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_DIVISION);
        Assert.assertEquals(nameProperty.getFullName(), "testCreatePropertyWithLinkedClass_2.division");
        Assert.assertEquals(nameProperty.getType(), LINK);
        Assert.assertEquals(nameProperty.getLinkedClass().getName(), "testCreatePropertyWithLinkedClass_1");
        TestCase.assertFalse(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
    }

    @Test
    public void testCreatePropertyWithEmbeddedType() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE Class testCreatePropertyWithEmbeddedType").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE Property testCreatePropertyWithEmbeddedType.officers EMBEDDEDLIST STRING").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testCreatePropertyWithEmbeddedType");
        OProperty nameProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getFullName(), "testCreatePropertyWithEmbeddedType.officers");
        Assert.assertEquals(nameProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(nameProperty.getLinkedType(), STRING);
        TestCase.assertFalse(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
    }

    @Test
    public void testCreateMandatoryProperty() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE class testCreateMandatoryProperty").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE property testCreateMandatoryProperty.name STRING (MANDATORY)").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testCreateMandatoryProperty");
        OProperty nameProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), "testCreateMandatoryProperty.name");
        Assert.assertTrue(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
    }

    @Test
    public void testCreateNotNullProperty() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE class testCreateNotNullProperty").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE property testCreateNotNullProperty.name STRING (NOTNULL)").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testCreateNotNullProperty");
        OProperty nameProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), "testCreateNotNullProperty.name");
        TestCase.assertFalse(nameProperty.isMandatory());
        Assert.assertTrue(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
    }

    @Test
    public void testCreateReadOnlyProperty() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE class testCreateReadOnlyProperty").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE property testCreateReadOnlyProperty.name STRING (READONLY)").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testCreateReadOnlyProperty");
        OProperty nameProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), "testCreateReadOnlyProperty.name");
        TestCase.assertFalse(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        Assert.assertTrue(nameProperty.isReadonly());
    }

    @Test
    public void testCreateReadOnlyFalseProperty() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE class testCreateReadOnlyFalseProperty").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE property testCreateReadOnlyFalseProperty.name STRING (READONLY false)").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testCreateReadOnlyFalseProperty");
        OProperty nameProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), "testCreateReadOnlyFalseProperty.name");
        TestCase.assertFalse(nameProperty.isReadonly());
    }

    @Test
    public void testCreateMandatoryPropertyWithEmbeddedType() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE Class testCreateMandatoryPropertyWithEmbeddedType").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE Property testCreateMandatoryPropertyWithEmbeddedType.officers EMBEDDEDLIST STRING (MANDATORY)").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testCreateMandatoryPropertyWithEmbeddedType");
        OProperty nameProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getFullName(), "testCreateMandatoryPropertyWithEmbeddedType.officers");
        Assert.assertEquals(nameProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(nameProperty.getLinkedType(), STRING);
        Assert.assertTrue(nameProperty.isMandatory());
        TestCase.assertFalse(nameProperty.isNotNull());
        TestCase.assertFalse(nameProperty.isReadonly());
    }

    @Test
    public void testCreateUnsafePropertyWithEmbeddedType() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE Class testCreateUnsafePropertyWithEmbeddedType").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE Property testCreateUnsafePropertyWithEmbeddedType.officers EMBEDDEDLIST STRING UNSAFE").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testCreateUnsafePropertyWithEmbeddedType");
        OProperty nameProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getFullName(), "testCreateUnsafePropertyWithEmbeddedType.officers");
        Assert.assertEquals(nameProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(nameProperty.getLinkedType(), STRING);
    }

    @Test
    public void testComplexCreateProperty() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE Class testComplexCreateProperty").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE Property testComplexCreateProperty.officers EMBEDDEDLIST STRING (MANDATORY, READONLY, NOTNULL) UNSAFE").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testComplexCreateProperty");
        OProperty nameProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_OFFICERS);
        Assert.assertEquals(nameProperty.getFullName(), "testComplexCreateProperty.officers");
        Assert.assertEquals(nameProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(nameProperty.getLinkedType(), STRING);
        Assert.assertTrue(nameProperty.isMandatory());
        Assert.assertTrue(nameProperty.isNotNull());
        Assert.assertTrue(nameProperty.isReadonly());
    }

    @Test
    public void testLinkedTypeDefaultAndMinMaxUnsafeProperty() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE CLASS testLinkedTypeDefaultAndMinMaxUnsafeProperty").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE PROPERTY testLinkedTypeDefaultAndMinMaxUnsafeProperty.id EMBEDDEDLIST Integer (DEFAULT 5, MIN 1, MAX 10) UNSAFE").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testLinkedTypeDefaultAndMinMaxUnsafeProperty");
        OProperty idProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_ID);
        Assert.assertEquals(idProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_ID);
        Assert.assertEquals(idProperty.getFullName(), "testLinkedTypeDefaultAndMinMaxUnsafeProperty.id");
        Assert.assertEquals(idProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(idProperty.getLinkedType(), INTEGER);
        TestCase.assertFalse(idProperty.isMandatory());
        TestCase.assertFalse(idProperty.isNotNull());
        TestCase.assertFalse(idProperty.isReadonly());
        Assert.assertEquals(idProperty.getDefaultValue(), "5");
        Assert.assertEquals(idProperty.getMin(), "1");
        Assert.assertEquals(idProperty.getMax(), "10");
    }

    @Test
    public void testDefaultAndMinMaxUnsafeProperty() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE CLASS testDefaultAndMinMaxUnsafeProperty").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE PROPERTY testDefaultAndMinMaxUnsafeProperty.id INTEGER (DEFAULT 5, MIN 1, MAX 10) UNSAFE").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testDefaultAndMinMaxUnsafeProperty");
        OProperty idProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_ID);
        Assert.assertEquals(idProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_ID);
        Assert.assertEquals(idProperty.getFullName(), "testDefaultAndMinMaxUnsafeProperty.id");
        Assert.assertEquals(idProperty.getType(), INTEGER);
        Assert.assertEquals(idProperty.getLinkedType(), null);
        TestCase.assertFalse(idProperty.isMandatory());
        TestCase.assertFalse(idProperty.isNotNull());
        TestCase.assertFalse(idProperty.isReadonly());
        Assert.assertEquals(idProperty.getDefaultValue(), "5");
        Assert.assertEquals(idProperty.getMin(), "1");
        Assert.assertEquals(idProperty.getMax(), "10");
    }

    @Test
    public void testExtraSpaces() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE CLASS testExtraSpaces").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE PROPERTY testExtraSpaces.id INTEGER  ( DEFAULT  5 ,  MANDATORY  )  UNSAFE ").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testExtraSpaces");
        OProperty idProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_ID);
        Assert.assertEquals(idProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_ID);
        Assert.assertEquals(idProperty.getFullName(), "testExtraSpaces.id");
        Assert.assertEquals(idProperty.getType(), INTEGER);
        Assert.assertEquals(idProperty.getLinkedType(), null);
        Assert.assertTrue(idProperty.isMandatory());
        Assert.assertEquals(idProperty.getDefaultValue(), "5");
    }

    @Test(expected = OCommandExecutionException.class)
    public void testInvalidAttributeName() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE CLASS OCommandExecutionException").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE PROPERTY OCommandExecutionException.id INTEGER (MANDATORY, INVALID, NOTNULL)  UNSAFE").close();
    }

    @Test(expected = OCommandExecutionException.class)
    public void testMissingAttributeValue() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE CLASS testMissingAttributeValue").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE PROPERTY testMissingAttributeValue.id INTEGER (DEFAULT)  UNSAFE").close();
    }

    @Test
    public void testMandatoryAsLinkedName() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE CLASS testMandatoryAsLinkedName").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE CLASS testMandatoryAsLinkedName_2").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE PROPERTY testMandatoryAsLinkedName.id EMBEDDEDLIST testMandatoryAsLinkedName_2 UNSAFE").close();
        OClass companyClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testMandatoryAsLinkedName");
        OClass mandatoryClass = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testMandatoryAsLinkedName_2");
        OProperty idProperty = companyClass.getProperty(OCreatePropertyStatementExecutionTest.PROP_ID);
        Assert.assertEquals(idProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_ID);
        Assert.assertEquals(idProperty.getFullName(), "testMandatoryAsLinkedName.id");
        Assert.assertEquals(idProperty.getType(), EMBEDDEDLIST);
        Assert.assertEquals(idProperty.getLinkedClass(), mandatoryClass);
        TestCase.assertFalse(idProperty.isMandatory());
    }

    @Test
    public void testIfNotExists() throws Exception {
        OCreatePropertyStatementExecutionTest.db.command("CREATE class testIfNotExists").close();
        OCreatePropertyStatementExecutionTest.db.command("CREATE property testIfNotExists.name if not exists STRING").close();
        OClass clazz = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testIfNotExists");
        OProperty nameProperty = clazz.getProperty(OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), "testIfNotExists.name");
        Assert.assertEquals(nameProperty.getType(), STRING);
        OCreatePropertyStatementExecutionTest.db.command("CREATE property testIfNotExists.name if not exists STRING").close();
        clazz = OCreatePropertyStatementExecutionTest.db.getMetadata().getSchema().getClass("testIfNotExists");
        nameProperty = clazz.getProperty(OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getName(), OCreatePropertyStatementExecutionTest.PROP_NAME);
        Assert.assertEquals(nameProperty.getFullName(), "testIfNotExists.name");
        Assert.assertEquals(nameProperty.getType(), STRING);
    }
}

