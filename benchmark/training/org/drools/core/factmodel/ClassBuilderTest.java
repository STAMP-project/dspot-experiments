/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.factmodel;


import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Date;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.junit.Assert;
import org.junit.Test;


public class ClassBuilderTest {
    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    ClassLoader classLoader;

    JavaDialectRuntimeData data;

    /* Test method for 'org.drools.core.common.asm.ClassBuilder.buildClass(ClassDefinition)' */
    @Test
    public void testBuildClass() {
        try {
            ClassBuilder builder = new ClassBuilderFactory().getBeanClassBuilder();
            ClassDefinition classDef = new ClassDefinition("org.drools.TestClass1", null, new String[]{ "java.io.Serializable" });
            FieldDefinition intDef = new FieldDefinition("intAttr", "int");
            FieldDefinition stringDef = new FieldDefinition("stringAttr", "java.lang.String");// "java.lang.String" );

            classDef.addField(intDef);
            classDef.addField(stringDef);
            Class clazz = build(builder, classDef);
            intDef.setReadWriteAccessor(store.getAccessor(clazz, intDef.getName()));
            stringDef.setReadWriteAccessor(store.getAccessor(clazz, stringDef.getName()));
            byte[] d = builder.buildClass(classDef, classLoader);
            Assert.assertSame("Returned class should be the same", clazz, classDef.getDefinedClass());
            Assert.assertEquals("Class name should be equal", classDef.getClassName(), clazz.getName());
            Serializable instance = ((Serializable) (clazz.newInstance()));
            String stringValue = "Atributo String ok";
            stringDef.setValue(instance, stringValue);
            Assert.assertEquals("Attribute should have been correctly set", stringValue, stringDef.getValue(instance));
            int intValue = 50;
            intDef.setValue(instance, new Integer(intValue));
            Assert.assertEquals("Attribute should have been correctly set", intValue, ((Integer) (intDef.getValue(instance))).intValue());
            // testing class rebuilding
            clazz = build(builder, classDef);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Error creating class");
        }
    }

    @Test
    public void testEquals() {
        try {
            ClassBuilder builder = new ClassBuilderFactory().getBeanClassBuilder();
            ClassDefinition classDef = new ClassDefinition("org.drools.TestClass2", null, new String[]{  });
            FieldDefinition long1Def = new FieldDefinition("longAttr1", "long", true);
            FieldDefinition long2Def = new FieldDefinition("longAttr2", "long", true);
            FieldDefinition doubleDef = new FieldDefinition("doubleAttr", "double", true);
            FieldDefinition intDef = new FieldDefinition("intAttr", "int", true);
            FieldDefinition strDef = new FieldDefinition("stringAttr", "java.lang.String", true);
            FieldDefinition dateDef = new FieldDefinition("dateAttr", "java.util.Date", true);
            FieldDefinition str2Def = new FieldDefinition("stringAttr2", "java.lang.String");
            classDef.addField(long1Def);
            classDef.addField(long2Def);
            classDef.addField(doubleDef);
            classDef.addField(intDef);
            classDef.addField(strDef);
            classDef.addField(dateDef);
            classDef.addField(str2Def);
            Class clazz = build(builder, classDef);
            long1Def.setReadWriteAccessor(store.getAccessor(clazz, long1Def.getName()));
            long2Def.setReadWriteAccessor(store.getAccessor(clazz, long2Def.getName()));
            doubleDef.setReadWriteAccessor(store.getAccessor(clazz, doubleDef.getName()));
            intDef.setReadWriteAccessor(store.getAccessor(clazz, intDef.getName()));
            strDef.setReadWriteAccessor(store.getAccessor(clazz, strDef.getName()));
            dateDef.setReadWriteAccessor(store.getAccessor(clazz, dateDef.getName()));
            str2Def.setReadWriteAccessor(store.getAccessor(clazz, str2Def.getName()));
            Object x = clazz.newInstance();
            Object y = clazz.newInstance();
            long1Def.setValue(x, new Long(20));
            long2Def.setValue(x, new Long(30));
            doubleDef.setValue(x, new Double(50.0));
            intDef.setValue(x, new Integer(10));
            strDef.setValue(x, "abc");
            dateDef.setValue(x, new Date(1000));
            str2Def.setValue(x, "instance1");
            long1Def.setValue(y, new Long(20));
            long2Def.setValue(y, new Long(30));
            doubleDef.setValue(y, new Double(50.0));
            intDef.setValue(y, new Integer(10));
            strDef.setValue(y, "abc");
            dateDef.setValue(y, new Date(1000));
            str2Def.setValue(y, "instance2");
            Object o = new Object();
            Assert.assertTrue(x.equals(x));
            Assert.assertFalse(x.equals(null));
            Assert.assertFalse(x.equals(o));
            Assert.assertTrue(x.equals(y));
            intDef.setValue(y, new Integer(1));
            Assert.assertFalse(x.equals(y));
            intDef.setValue(y, new Integer(10));
            strDef.setValue(y, "xyz");
            Assert.assertFalse(x.equals(y));
            strDef.setValue(y, null);
            Assert.assertFalse(x.equals(y));
            strDef.setValue(y, "abc");
            dateDef.setValue(y, new Date(1));
            Assert.assertFalse(x.equals(y));
            dateDef.setValue(y, null);
            Assert.assertFalse(x.equals(y));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception not expected");
        }
    }

    @Test
    public void testHashCode() {
        try {
            ClassBuilder builder = new ClassBuilderFactory().getBeanClassBuilder();
            ClassDefinition classDef = new ClassDefinition("org.drools.TestClass3", null, new String[]{  });
            FieldDefinition intDef = new FieldDefinition("intAttr", "int", true);
            FieldDefinition strDef = new FieldDefinition("stringAttr", "java.lang.String", false);
            classDef.addField(intDef);
            classDef.addField(strDef);
            Class clazz = build(builder, classDef);
            intDef.setReadWriteAccessor(store.getAccessor(clazz, intDef.getName()));
            strDef.setReadWriteAccessor(store.getAccessor(clazz, strDef.getName()));
            Object x = clazz.newInstance();
            intDef.setValue(x, new Integer(10));
            strDef.setValue(x, "abc");
            Assert.assertEquals("Wrong hashcode calculation", (31 + 10), x.hashCode());
            Assert.assertEquals("Wrong hashcode calculation", x.hashCode(), x.hashCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception not expected");
        }
    }

    @Test
    public void testToString() {
        try {
            ClassBuilder builder = new ClassBuilderFactory().getBeanClassBuilder();
            ClassDefinition classDef = new ClassDefinition("org.drools.TestClass4", null, new String[]{  });
            FieldDefinition long1Def = new FieldDefinition("longAttr1", "long", true);
            FieldDefinition long2Def = new FieldDefinition("longAttr2", "long", true);
            FieldDefinition doubleDef = new FieldDefinition("doubleAttr", "double", true);
            FieldDefinition intDef = new FieldDefinition("intAttr", "int", true);
            FieldDefinition strDef = new FieldDefinition("stringAttr", "java.lang.String", true);
            FieldDefinition dateDef = new FieldDefinition("dateAttr", "java.util.Date", true);
            FieldDefinition str2Def = new FieldDefinition("stringAttr2", "java.lang.String");
            classDef.addField(long1Def);
            classDef.addField(long2Def);
            classDef.addField(doubleDef);
            classDef.addField(intDef);
            classDef.addField(strDef);
            classDef.addField(dateDef);
            classDef.addField(str2Def);
            Class clazz = build(builder, classDef);
            long1Def.setReadWriteAccessor(store.getAccessor(clazz, long1Def.getName()));
            long2Def.setReadWriteAccessor(store.getAccessor(clazz, long2Def.getName()));
            doubleDef.setReadWriteAccessor(store.getAccessor(clazz, doubleDef.getName()));
            intDef.setReadWriteAccessor(store.getAccessor(clazz, intDef.getName()));
            strDef.setReadWriteAccessor(store.getAccessor(clazz, strDef.getName()));
            dateDef.setReadWriteAccessor(store.getAccessor(clazz, dateDef.getName()));
            str2Def.setReadWriteAccessor(store.getAccessor(clazz, str2Def.getName()));
            Object x = clazz.newInstance();
            long1Def.setValue(x, new Long(20));
            long2Def.setValue(x, new Long(30));
            doubleDef.setValue(x, new Double(50.0));
            intDef.setValue(x, new Integer(10));
            strDef.setValue(x, "abc");
            dateDef.setValue(x, new Date(1000));
            str2Def.setValue(x, "instance1");
            String result = x.toString();
            Assert.assertTrue(result.contains(long1Def.getName()));
            Assert.assertTrue(result.contains(long2Def.getName()));
            Assert.assertTrue(result.contains(doubleDef.getName()));
            Assert.assertTrue(result.contains(intDef.getName()));
            Assert.assertTrue(result.contains(strDef.getName()));
            Assert.assertTrue(result.contains(dateDef.getName()));
            Assert.assertTrue(result.contains(str2Def.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception not expected");
        }
    }

    @Test
    public void testConstructorWithFields() {
        try {
            ClassBuilder builder = new ClassBuilderFactory().getBeanClassBuilder();
            ClassDefinition classDef = new ClassDefinition("org.drools.TestClass5", null, new String[]{  });
            String[] types = new String[]{ "byte", "short", "int", "long", "float", "double", "char", "java.lang.String", "boolean" };
            FieldDefinition[] fields = new FieldDefinition[types.length];
            for (int i = 0; i < (types.length); i++) {
                String attrName = types[i].substring(((types[i].lastIndexOf('.')) + 1));
                attrName = ((attrName.substring(0, 1).toLowerCase()) + (attrName.substring(1))) + "Attr";
                fields[i] = // attr name
                // attr type
                new FieldDefinition(attrName, types[i], ((i % 2) == 0));// half of them are key

                classDef.addField(fields[i]);
            }
            Class clazz = build(builder, classDef);
            for (FieldDefinition field : fields) {
                field.setReadWriteAccessor(store.getAccessor(clazz, field.getName()));
            }
            Constructor<?>[] cons = clazz.getConstructors();
            Assert.assertEquals(3, cons.length);
            for (Constructor<?> c : cons) {
                Class<?>[] ptypes = c.getParameterTypes();
                if ((ptypes.length) == 0) {
                    // default constructor
                } else
                    if ((ptypes.length) == (fields.length)) {
                        // constructor with fields
                        for (int i = 0; i < (ptypes.length); i++) {
                            if (!(ptypes[i].equals(fields[i].getType()))) {
                                Assert.fail(((((("Wrong parameter in constructor. index=" + i) + " expected=") + (fields[i].getType())) + " found=") + (ptypes[i])));
                            }
                        }
                        // test actual invocation
                        Object instance = c.newInstance(((byte) (1)), ((short) (2)), 3, 4L, 5.0F, 6.0, 'a', "xyz", true);
                        Assert.assertEquals(((byte) (1)), fields[0].getValue(instance));
                        Assert.assertEquals(((short) (2)), fields[1].getValue(instance));
                        Assert.assertEquals(3, fields[2].getValue(instance));
                        Assert.assertEquals(4L, fields[3].getValue(instance));
                        Assert.assertEquals(5.0F, fields[4].getValue(instance));
                        Assert.assertEquals(6.0, fields[5].getValue(instance));
                        Assert.assertEquals('a', fields[6].getValue(instance));
                        Assert.assertEquals("xyz", fields[7].getValue(instance));
                        Assert.assertEquals(true, fields[8].getValue(instance));
                    } else
                        if ((ptypes.length) == (((fields.length) / 2) + 1)) {
                            // as defined in the beginning of the test
                            // constructor with key fields
                            int i = 0;
                            for (FieldDefinition field : fields) {
                                if ((field.isKey()) && (!(ptypes[(i++)].equals(field.getType())))) {
                                    Assert.fail(((((("Wrong parameter in constructor. index=" + i) + " expected=") + (field.getType())) + " found=") + (ptypes[i])));
                                }
                            }
                            // test actual invocation
                            Object instance = c.newInstance(((byte) (1)), 3, 5.0F, 'a', true);
                            Assert.assertEquals(((byte) (1)), fields[0].getValue(instance));
                            Assert.assertEquals(3, fields[2].getValue(instance));
                            Assert.assertEquals(5.0F, fields[4].getValue(instance));
                            Assert.assertEquals('a', fields[6].getValue(instance));
                            Assert.assertEquals(true, fields[8].getValue(instance));
                        } else {
                            Assert.fail(("Unexpected constructor: " + (c.toString())));
                        }


            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("Unexpected Exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testGetResourcesJBRULES3122() {
        try {
            ClassBuilder builder = new ClassBuilderFactory().getBeanClassBuilder();
            ClassDefinition classDef = new ClassDefinition("org.drools.TestClass4", null, new String[]{  });
            Class clazz = build(builder, classDef);
            ClassLoader cl = clazz.getClassLoader();
            // We expect normal classloader stuff to work
            Assert.assertFalse(cl.getResources("not-there.txt").hasMoreElements());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("Exception not expected: " + (e.getMessage())));
        }
    }
}

