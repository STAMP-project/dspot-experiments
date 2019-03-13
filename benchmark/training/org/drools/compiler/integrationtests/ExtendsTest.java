/**
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.compiler.integrationtests;


import KieServices.Factory;
import Message.Level.ERROR;
import ResourceType.DRL;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.core.common.EventFactHandle;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.ByteArrayResource;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;


/**
 * Test for declared bean Extension
 */
public class ExtendsTest extends CommonTestMethodBase {
    @Test
    public void testExtends() throws Exception {
        // Test Base Fact Type
        KieSession ksession = genSession("test_Extends.drl");
        FactType person = ksession.getKieBase().getFactType("defaultpkg", "Person");
        FactType eqPair = ksession.getKieBase().getFactType("defaultpkg", "EqualityPair");
        Object p = person.newInstance();
        Assert.assertNotNull(p);
        ksession.insert("Populate");
        ksession.fireAllRules();
        // A Rule will generate 3 Persons, one with default constructor, two with field constructor
        // and 3 Students, which extends Person. One with default, one with field constructor
        Assert.assertEquals(6, ksession.getObjects(new org.kie.api.runtime.ClassObjectFilter(person.getFactClass())).size());
        Assert.assertEquals((((1 + 4) + 1) + 4), ksession.getObjects(new org.kie.api.runtime.ClassObjectFilter(eqPair.getFactClass())).size());
        ksession.dispose();
    }

    @Test
    public void testGeneratedMethods() throws Exception {
        KieSession ksession = genSession("test_Extends.drl");
        FactType student = ksession.getKieBase().getFactType("defaultpkg", "Student");
        Constructor constructor = student.getFactClass().getConstructor(String.class, int.class, String.class);
        Assert.assertNotNull(constructor);
        Method equals = student.getFactClass().getMethod("equals", Object.class);
        Assert.assertNotNull(equals);
        Object s1 = constructor.newInstance("John", 18, "Skool");
        Assert.assertNotNull(s1);
        Assert.assertEquals("Student( name=John, age=18, school=Skool )", s1.toString());
        Object s2 = constructor.newInstance("John", 25, "Skool");
        Assert.assertNotNull(s2);
        Assert.assertEquals("Student( name=John, age=25, school=Skool )", s2.toString());
        Assert.assertEquals(s1.hashCode(), s2.hashCode());
        Assert.assertTrue(((Boolean) (equals.invoke(s1, s2))));
        Assert.assertTrue(s1.equals(s2));
        Object s3 = constructor.newInstance("Mark", 18, "Skool");
        Assert.assertNotNull(s3);
        Assert.assertEquals("Student( name=Mark, age=18, school=Skool )", s3.toString());
        Assert.assertNotSame(s1.hashCode(), s3.hashCode());
        Assert.assertNotSame(s2.hashCode(), s3.hashCode());
        Assert.assertFalse(s1.equals(s3));
        Assert.assertFalse(s2.equals(s3));
        Object s4 = constructor.newInstance("John", 25, "AnotherSkool");
        Assert.assertNotNull(s4);
        Assert.assertEquals("Student( name=John, age=25, school=AnotherSkool )", s4.toString());
        Assert.assertNotSame(s1.hashCode(), s4.hashCode());
        Assert.assertNotSame(s2.hashCode(), s4.hashCode());
        Assert.assertNotSame(s3.hashCode(), s4.hashCode());
        Assert.assertFalse(s1.equals(s4));
        Assert.assertFalse(s2.equals(s4));
        Assert.assertFalse(s3.equals(s4));
        ksession.dispose();
    }

    @Test
    public void testDeepExt() throws Exception {
        KieSession ksession = genSession("test_Extends.drl");
        FactType LTstudent = ksession.getKieBase().getFactType("defaultpkg", "LongTermStudent");
        Constructor constructor = LTstudent.getFactClass().getConstructor(String.class, int.class, String.class, String.class, int.class);
        Assert.assertNotNull(constructor);
        Object ls1 = constructor.newInstance("John", 18, "Skool", "C1245", 4);
        Object ls2 = constructor.newInstance("John", 33, "Skool", "C1421", 4);
        Assert.assertEquals(ls1, ls2);
        ksession.dispose();
    }

    @Test
    public void testIllegalExtendsLegacy() throws Exception {
        // Test Base Fact Type
        genSession("test_ExtLegacyIllegal.drl", 7);
    }

    @Test
    public void testExtendsLegacy() throws Exception {
        KieSession ksession = genSession("test_ExtLegacy.drl", 0);
        FactType leg = ksession.getKieBase().getFactType("org.drools.compiler", "BetterLegacy");
        Assert.assertNotNull(leg);
        Object b = leg.newInstance();
        Assert.assertEquals(3.3, leg.get(b, "doubleField"));
        Assert.assertNull(leg.get(b, "objField"));
        Assert.assertEquals(245, leg.get(b, "intField"));
        Assert.assertEquals("XX", leg.get(b, "strField"));
        Assert.assertEquals(true, leg.get(b, "prop"));
        Assert.assertEquals("Hello", leg.get(b, "oneMoreField"));
        System.out.println(b);
    }

    @Test
    public void testExtendsAcrossFiles() throws Exception {
        KieSession ksession = new KieHelper().addResource(ResourceFactory.newClassPathResource("test_Ext1.drl", getClass()), DRL).addResource(ResourceFactory.newClassPathResource("test_Ext2.drl", getClass()), DRL).addResource(ResourceFactory.newClassPathResource("test_Ext3.drl", getClass()), DRL).addResource(ResourceFactory.newClassPathResource("test_Ext4.drl", getClass()), DRL).build().newKieSession();
        FactType person = ksession.getKieBase().getFactType("org.drools.compiler.ext.test", "Person");
        Assert.assertNotNull(person);
        FactType student = ksession.getKieBase().getFactType("org.drools.compiler.ext.test", "Student");
        Assert.assertNotNull(student);
        FactType worker = ksession.getKieBase().getFactType("org.drools.compiler.anothertest", "Worker");
        Assert.assertNotNull(worker);
        FactType ltss = ksession.getKieBase().getFactType("defaultpkg", "SubLTStudent");
        Assert.assertNotNull(ltss);
        Constructor ctor = worker.getFactClass().getConstructor(String.class, int.class, String.class, double.class, int.class);
        Assert.assertNotNull(ctor);
        Object w = ctor.newInstance("Adam", 20, "Carpenter", 150.0, 40);
        System.out.println(w);
        Assert.assertEquals("Adam", worker.get(w, "name"));
        ksession.fireAllRules();
    }

    @Test
    public void testFieldInit() throws Exception {
        KieSession ksession = genSession("test_ExtFieldInit.drl");
        FactType test = ksession.getKieBase().getFactType("org.drools.compiler", "MyBean3");
        Object x = test.newInstance();
        Assert.assertNotNull(x);
        Assert.assertEquals(12, test.get(x, "fieldint"));
        Assert.assertEquals("xyz", test.get(x, "fieldstr"));
        Assert.assertEquals(3.23, test.get(x, "fielddbl"));
        Assert.assertEquals(0, test.get(x, "field0"));
        Assert.assertEquals(0.0F, test.get(x, "field1"));
        Assert.assertEquals(1.2F, test.get(x, "fieldflt"));
        short sht = 2;
        Assert.assertEquals(sht, test.get(x, "fieldsht"));
        Assert.assertEquals(0, test.get(x, "field2"));
        byte byt = 1;
        Assert.assertEquals(byt, test.get(x, "fieldbyt"));
        Assert.assertEquals(true, test.get(x, "fieldbln"));
        Assert.assertEquals('x', test.get(x, "fieldchr"));
        Assert.assertEquals(9999L, test.get(x, "fieldlng"));
        System.out.println(x);
    }

    @Test
    public void testBoxedFieldInit() throws Exception {
        KieSession ksession = genSession("test_ExtFieldInit.drl");
        FactType test = ksession.getKieBase().getFactType("org.drools.compiler", "MyBoxBean");
        Object x = test.newInstance();
        Assert.assertNotNull(x);
        Assert.assertEquals(12, test.get(x, "fieldint"));
        Assert.assertEquals(3.23, test.get(x, "fielddbl"));
        Assert.assertEquals(1.2F, test.get(x, "fieldflt"));
        short sht = 2;
        Assert.assertEquals(sht, test.get(x, "fieldsht"));
        byte byt = 1;
        Assert.assertEquals(byt, test.get(x, "fieldbyt"));
        Assert.assertEquals(true, test.get(x, "fieldbln"));
        Assert.assertEquals('x', test.get(x, "fieldchr"));
        Assert.assertEquals(9999L, test.get(x, "fieldlng"));
        System.out.println(x);
    }

    @Test
    public void testExpressionFieldInit() throws Exception {
        KieSession ksession = genSession("test_ExtFieldInit.drl");
        FactType test = ksession.getKieBase().getFactType("org.drools.compiler", "MyBoxExpressionBean");
        Object x = test.newInstance();
        Assert.assertNotNull(x);
        Assert.assertEquals("foobar", test.get(x, "f0"));
        Assert.assertEquals((-32), test.get(x, "fieldint"));
        Assert.assertEquals(4.0, test.get(x, "fielddbl"));
        Assert.assertEquals(0.5F, test.get(x, "fieldflt"));
        short sht = 6;
        Assert.assertEquals(sht, test.get(x, "fieldsht"));
        byte byt = 2;
        Assert.assertEquals(byt, test.get(x, "fieldbyt"));
        Assert.assertEquals(true, test.get(x, "fieldbln"));
        Assert.assertEquals('x', test.get(x, "fieldchr"));
        Assert.assertEquals(9999L, test.get(x, "fieldlng"));
        System.out.println(x);
        FactType test2 = ksession.getKieBase().getFactType("org.drools.compiler", "MySimpleExpressionBean");
        x = test2.newInstance();
        Assert.assertNotNull(x);
        Assert.assertEquals("foobar", test2.get(x, "f0"));
        Assert.assertEquals((-32), test2.get(x, "fieldint"));
        Assert.assertEquals(4.0, test2.get(x, "fielddbl"));
        Assert.assertEquals(0.5F, test2.get(x, "fieldflt"));
        sht = 6;
        Assert.assertEquals(sht, test2.get(x, "fieldsht"));
        byt = 2;
        Assert.assertEquals(byt, test2.get(x, "fieldbyt"));
        Assert.assertEquals(true, test2.get(x, "fieldbln"));
        Assert.assertEquals('x', test2.get(x, "fieldchr"));
        Assert.assertEquals(9999L, test2.get(x, "fieldlng"));
        System.out.println(x);
    }

    @Test
    public void testHierarchy() throws Exception {
        KieSession ksession = genSession("test_ExtHierarchy.drl");
        ksession.setGlobal("list", new LinkedList());
        ksession.fireAllRules();
        Assert.assertEquals(1, ((List) (ksession.getGlobal("list"))).size());
    }

    @Test
    public void testExtendOverride() {
        String drl = "package test.beans;\n" + ((((((((((((((((((((((((("\n" + "import java.util.List;\n") + "import java.util.ArrayList;\n") + "\n") + "global List ans;") + "\n") + "\n") + "declare ArrayList\n") + "end\n") + "") + "declare Bean extends ArrayList\n") + "  fld : int = 4 \n") + "  myField : String = \"xxx\" \n") + "end\n") + "\n") + "declare Bean2 extends Bean\n") + "  moref : double\n") + "  myField : String\n") + "end\n") + "rule \"Init\"\n") + "when\n") + "then\n") + "  Bean b = new Bean2();\n") + "  ans.add(b);") + "  System.out.println(b);\t\n") + "end\n");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        if (kbuilder.hasErrors()) {
            Assert.fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession ksession = createKnowledgeSession(kbase);
        List out = new ArrayList();
        ksession.setGlobal("ans", out);
        ksession.fireAllRules();
        Object x = out.get(0);
        FactType type = kbase.getFactType("test.beans", "Bean2");
        Assert.assertEquals(4, type.get(x, "fld"));
        Assert.assertEquals("xxx", type.get(x, "myField"));
        Assert.assertEquals(0.0, type.get(x, "moref"));
        Assert.assertTrue((x instanceof ArrayList));
    }

    @Test
    public void testRedefineDefaults() throws Exception {
        // Test Base Fact Type
        KieSession ksession = genSession("test_Extends.drl");
        FactType person = ksession.getKieBase().getFactType("defaultpkg", "Person");
        FactType student = ksession.getKieBase().getFactType("defaultpkg", "Student");
        Object p = person.newInstance();
        Object s = student.newInstance();
        Assert.assertNotNull(p);
        Assert.assertNotNull(s);
        Assert.assertEquals(99, person.get(p, "age"));
        Assert.assertEquals(18, person.get(s, "age"));
        ksession.dispose();
    }

    @Test
    public void testExtendFromOtherPackage() throws Exception {
        String s1 = "package org.drools.compiler.test.pack1;\n" + ((((((("\n" + "declare Base\n") + "  id    : int\n") + "end\n") + "\n") + "declare Sub extends Base\n") + "  field : int\n") + "end\n");
        String s2 = "package org.drools.compiler.test.pack2;\n" + ((((((((((((((((((((((((((("\n" + "import org.drools.compiler.test.pack1.Base;\n") + "import org.drools.compiler.test.pack1.Sub;\n") + "\n") + "declare Sub end\n") + "\n") + "declare SubChild extends Sub\n") + "  field2 : int\n") + "end\n") + "\n") + "rule \"Init\"\n") + "when\n") + "then\n") + "  insert( new SubChild( 1, 2, 3 ) );\n") + "end\n") + "\n") + "rule \"Touch Base\"\n") + "when\n") + "  $b : Base()\n") + "then\n") + "  System.out.println( $b );\n") + "end \n") + "rule \"Touch Base 2\"\n") + "when\n") + "  $c : Sub()\n") + "then\n") + "  System.out.println( $c );\n") + "end");
        KieSession kSession = new KieHelper().addContent(s1, DRL).addContent(s2, DRL).build().newKieSession();
        Assert.assertEquals(3, kSession.fireAllRules());
    }

    @Test
    public void testInheritAnnotationsInOtherPackage() throws Exception {
        String s1 = "package org.drools.compiler.test.pack1;\n" + ((((((((((((("global java.util.List list;" + "\n") + "declare Event\n") + "@role(event)") + "  id    : int\n") + "end\n") + "\n") + "rule \"X\"\n") + "when\n") + "  $s1 : Event()\n") + "then\n") + "  System.out.println( $s1 );\n") + "  list.add( $s1.getId() );\n ") + "end");
        String s2 = "package org.drools.compiler.test.pack2;\n" + ((((((((((((((((((((((((((((((((("\n" + "import org.drools.compiler.test.pack1.Event;\n") + "global java.util.List list;") + "\n") + "declare Event end\n") + "\n") + "declare SubEvent extends Event\n") + "end\n") + "\n") + "rule \"Init\"\n") + "when\n") + "then\n") + "  list.add( 0 );\n") + "  insert( new SubEvent( 1 ) );\n") + "  insert( new SubEvent( 2 ) );\n") + "end\n") + "\n") + "rule \"Seq\"\n") + "when\n") + "  $s1 : SubEvent( id == 1 )\n") + "  $s2 : SubEvent( id == 2, this after[0,10s] $s1 )\n") + "then\n") + "  list.add( 3 );\n") + "  System.out.println( $s1 + \" after \" + $s1 );\n") + "end \n") + "\n") + "rule \"Seq 2 \"\n") + "when\n") + "  $s1 : Event( id == 1 )\n") + "  $s2 : Event( id == 2, this after[0,10s] $s1 )\n") + "then\n") + "  list.add( 4 );\n") + "  System.out.println( $s1 + \" after II \" + $s1 );\n") + "end");
        KieSession kSession = new KieHelper().addContent(s1, DRL).addContent(s2, DRL).build().newKieSession();
        List list = new ArrayList();
        kSession.setGlobal("list", list);
        kSession.fireAllRules();
        for (Object o : kSession.getObjects()) {
            FactHandle h = kSession.getFactHandle(o);
            Assert.assertTrue((h instanceof EventFactHandle));
        }
        System.out.println(list);
        Assert.assertEquals(5, list.size());
        Assert.assertTrue(list.contains(0));
        Assert.assertTrue(list.contains(1));
        Assert.assertTrue(list.contains(2));
        Assert.assertTrue(list.contains(3));
        Assert.assertTrue(list.contains(4));
    }

    @Test
    public void testInheritFromClassWithDefaults() throws Exception {
        // car is a java class with attributes
        // brand (default ferrari)
        // expensive (default true)
        String s1 = "package org.drools.compiler;\n" + ((((((((((((((((((("global java.util.List list;\n" + "declare Car end\n") + "\n") + "declare MyCar extends Car \n") + " miles : int\n") + "end\n") + "\n") + "rule \"Init\"\n") + "when\n") + "then\n") + "  MyCar c = new MyCar();\n") + "  c.setMiles( 100 );") + "  insert( c );\n") + "end\n") + "rule \"Match\"\n") + "when\n") + "  MyCar( brand == \"ferrari\", expensive == true, $miles : miles ) ") + "then\n") + "  list.add( $miles );\n") + "end");
        KieSession kSession = new KieHelper().addContent(s1, DRL).build().newKieSession();
        List list = new ArrayList();
        kSession.setGlobal("list", list);
        kSession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(100));
    }

    @Test
    public void testExtendSelf() throws Exception {
        String s1 = "package org.drools;\n" + (((("global java.util.List list;\n" + "\n") + "declare Bean extends Bean \n") + " foo : int @key\n") + "end\n");
        KieBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(s1.getBytes()), DRL);
        Assert.assertTrue(kBuilder.hasErrors());
    }

    @Test
    public void testExtendCircular() throws Exception {
        String s1 = "package org.drools;\n" + (((((((((((("global java.util.List list;\n" + "\n") + "declare Bean1 extends Bean2 \n") + " foo : int @key\n") + "end\n") + "") + "declare Bean2 extends Bean3 \n") + " foo : int @key\n") + "end\n") + "") + "declare Bean3 extends Bean1 \n") + " foo : int @key\n") + "end\n");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(s1.getBytes()), DRL);
        Assert.assertTrue(kBuilder.hasErrors());
        System.out.println(kBuilder.getErrors());
        Assert.assertTrue(kBuilder.getErrors().toString().contains("circular"));
    }

    @Test
    public void testExtendsDump() {
        PackageDescrBuilder pkgd = DescrFactory.newPackage();
        pkgd.name("org.test").newDeclare().type().name("Foo").newField("id").type("int").end().end().newDeclare().type().name("Bar").superType("Foo").newField("val").type("int").initialValue("42").end().end();
        String drl = new DrlDumper().dump(pkgd.getDescr());
        KieBase kb = loadKnowledgeBaseFromString(drl);
        FactType bar = kb.getFactType("org.test", "Bar");
        try {
            Object x = bar.newInstance();
            Assert.assertEquals(42, bar.get(x, "val"));
            bar.set(x, "id", 1);
            Assert.assertEquals(1, bar.get(x, "id"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMultipleInheritanceWithPosition1() throws Exception {
        // DROOLS-249
        String drl = "package org.drools.test\n" + ((((((((((("declare trait PosTrait\n" + "@propertyReactive\n") + " field0 : int = 100 //@position(0)\n") + " field1 : int = 101 //@position(1)\n") + " field2 : int = 102 //@position(0)\n") + "end\n") + "\n") + "declare trait MultiInhPosTrait extends PosTrait\n") + "@propertyReactive\n") + " mfield0 : int = 200 //@position(0)\n") + " mfield1 : int = 201 @position(2)\n") + "end");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        Assert.assertFalse(kBuilder.hasErrors());
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());
        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        Assert.assertEquals(5, sw.getFields().size());
        Assert.assertEquals("field0", sw.getFields().get(0).getName());
        Assert.assertEquals("field1", sw.getFields().get(1).getName());
        Assert.assertEquals("mfield1", sw.getFields().get(2).getName());
        Assert.assertEquals("field2", sw.getFields().get(3).getName());
        Assert.assertEquals("mfield0", sw.getFields().get(4).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition2() throws Exception {
        // DROOLS-249
        String drl = "package org.drools.test\n" + ((((((((((("declare trait PosTrait\n" + "@propertyReactive\n") + " field0 : int = 100 //@position(0)\n") + " field1 : int = 101 //@position(1)\n") + " field2 : int = 102 //@position(0)\n") + "end\n") + "\n") + "declare trait MultiInhPosTrait extends PosTrait\n") + "@propertyReactive\n") + " mfield0 : int = 200 @position(0)\n") + " mfield1 : int = 201 @position(2)\n") + "end");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        Assert.assertFalse(kBuilder.hasErrors());
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());
        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        Assert.assertEquals(5, sw.getFields().size());
        Assert.assertEquals("mfield0", sw.getFields().get(0).getName());
        Assert.assertEquals("field0", sw.getFields().get(1).getName());
        Assert.assertEquals("mfield1", sw.getFields().get(2).getName());
        Assert.assertEquals("field1", sw.getFields().get(3).getName());
        Assert.assertEquals("field2", sw.getFields().get(4).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition3() throws Exception {
        // DROOLS-249
        String drl = "package org.drools.test\n" + ((((((((((("declare trait PosTrait\n" + "@propertyReactive\n") + " field0 : int = 100 @position(0)\n") + " field1 : int = 101 @position(1)\n") + " field2 : int = 102 @position(0)\n") + "end\n") + "\n") + "declare trait MultiInhPosTrait extends PosTrait\n") + "@propertyReactive\n") + " mfield0 : int = 200 //@position(0)\n") + " mfield1 : int = 201 @position(2)\n") + "end");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        Assert.assertFalse(kBuilder.hasErrors());
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());
        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        Assert.assertEquals(5, sw.getFields().size());
        Assert.assertEquals("field0", sw.getFields().get(0).getName());
        Assert.assertEquals("field2", sw.getFields().get(1).getName());
        Assert.assertEquals("field1", sw.getFields().get(2).getName());
        Assert.assertEquals("mfield1", sw.getFields().get(3).getName());
        Assert.assertEquals("mfield0", sw.getFields().get(4).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition4() throws Exception {
        // DROOLS-249
        String drl = "package org.drools.test\n" + ((((((((((("declare trait PosTrait\n" + "@propertyReactive\n") + " field0 : int = 100 @position(1)\n") + " field1 : int = 101 @position(0)\n") + " field2 : int = 102 @position(0)\n") + "end\n") + "\n") + "declare trait MultiInhPosTrait extends PosTrait\n") + "@propertyReactive\n") + " mfield0 : int = 200 @position(0)\n") + " mfield1 : int = 201 @position(2)\n") + "end");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        Assert.assertFalse(kBuilder.hasErrors());
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());
        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        Assert.assertEquals(5, sw.getFields().size());
        Assert.assertEquals("field1", sw.getFields().get(0).getName());
        Assert.assertEquals("field2", sw.getFields().get(1).getName());
        Assert.assertEquals("mfield0", sw.getFields().get(2).getName());
        Assert.assertEquals("field0", sw.getFields().get(3).getName());
        Assert.assertEquals("mfield1", sw.getFields().get(4).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition5() throws Exception {
        // DROOLS-249
        String drl = "package org.drools.test\n" + ((((((((((("declare trait PosTrait\n" + "@propertyReactive\n") + " field0 : int = 100 @position(2)\n") + " field1 : int = 101 @position(1)\n") + " field2 : int = 102 @position(8)\n") + "end\n") + "\n") + "declare trait MultiInhPosTrait extends PosTrait\n") + "@propertyReactive\n") + " mfield0 : int = 200 @position(7)\n") + " mfield1 : int = 201 @position(2)\n") + "end");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        Assert.assertFalse(kBuilder.hasErrors());
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());
        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        Assert.assertEquals(5, sw.getFields().size());
        Assert.assertEquals("field1", sw.getFields().get(0).getName());
        Assert.assertEquals("field0", sw.getFields().get(1).getName());
        Assert.assertEquals("mfield1", sw.getFields().get(2).getName());
        Assert.assertEquals("mfield0", sw.getFields().get(3).getName());
        Assert.assertEquals("field2", sw.getFields().get(4).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition6() throws Exception {
        // DROOLS-249
        String drl = "package org.drools.test\n" + ((((((((((((((((("declare trait PosTrait\n" + "@propertyReactive\n") + " field0 : int = 100 //@position(0)\n") + " field1 : int = 101 //@position(1)\n") + " field2 : int = 102 //@position(0)\n") + "end\n") + "\n") + "declare trait SecPosTrait\n") + "@propertyReactive\n") + " field3 : int = 100 //@position(0)\n") + " field1 : int = 101 //@position(1)\n") + "end\n") + "\n") + "declare trait MultiInhPosTrait extends PosTrait, SecPosTrait\n") + "@propertyReactive\n") + " mfield0 : int = 200 //@position(0)\n") + " mfield1 : int = 201 //@position(2)\n") + "end");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        Assert.assertFalse(kBuilder.hasErrors());
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());
        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        Assert.assertEquals(6, sw.getFields().size());
        Assert.assertEquals("field0", sw.getFields().get(0).getName());
        Assert.assertEquals("field1", sw.getFields().get(1).getName());
        Assert.assertEquals("field2", sw.getFields().get(2).getName());
        Assert.assertEquals("field3", sw.getFields().get(3).getName());
        Assert.assertEquals("mfield0", sw.getFields().get(4).getName());
        Assert.assertEquals("mfield1", sw.getFields().get(5).getName());
    }

    @Test
    public void testMultipleInheritanceWithPosition7() throws Exception {
        // DROOLS-249
        String drl = "package org.drools.test\n" + ((((((((((((((((("declare trait PosTrait\n" + "@propertyReactive\n") + " field0 : int = 100 @position(0)\n") + " field1 : int = 101 @position(1)\n") + " field2 : int = 102 @position(0)\n") + "end\n") + "\n") + "declare trait SecPosTrait\n") + "@propertyReactive\n") + " field3 : int = 100 @position(2)\n") + " field1 : int = 101 //@position(1)\n") + "end\n") + "\n") + "declare trait MultiInhPosTrait extends PosTrait, SecPosTrait\n") + "@propertyReactive\n") + " mfield0 : int = 200 @position(0)\n") + " mfield1 : int = 201 @position(2)\n") + "end");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        Assert.assertFalse(kBuilder.hasErrors());
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());
        FactType sw = knowledgeBase.getFactType("org.drools.test", "MultiInhPosTrait");
        Assert.assertEquals(6, sw.getFields().size());
        Assert.assertEquals("field0", sw.getFields().get(0).getName());
        Assert.assertEquals("field2", sw.getFields().get(1).getName());
        Assert.assertEquals("mfield0", sw.getFields().get(2).getName());
        Assert.assertEquals("field1", sw.getFields().get(3).getName());
        Assert.assertEquals("field3", sw.getFields().get(4).getName());
        Assert.assertEquals("mfield1", sw.getFields().get(5).getName());
    }

    public static interface A {}

    public static interface B extends ExtendsTest.A {}

    public static interface C extends ExtendsTest.B {}

    public static class X implements ExtendsTest.C {
        private int x = 1;

        public int getX() {
            return x;
        }
    }

    @Test
    public void testDeclareInheritance() throws Exception {
        String s1 = "package org.drools;\n" + ((((((((((((((("import org.drools.compiler.integrationtests.ExtendsTest.*;\n" + "\n") + "declare A \n") + " @role( event )") + " @typesafe( false )\n") + "end\n") + "") + "declare C @role( event ) @typesafe( false ) end \n") + "") + "rule R \n") + "when ") + "   $x : C( this.x == 1 ) \n") + "then\n") + "   System.out.println( $x ); \n") + "end\n") + "");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(s1.getBytes()), DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        Assert.assertFalse(kBuilder.hasErrors());
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(kBuilder.getKnowledgePackages());
        KieSession knowledgeSession = knowledgeBase.newKieSession();
        FactHandle h = knowledgeSession.insert(new ExtendsTest.X());
        Assert.assertTrue(isEvent());
    }

    @Test
    public void testDeclareExtendsJavaParent() {
        String drl = "package org.drools.test; \n" + (("import org.drools.compiler.Person; \n" + "declare Student extends Person end \n") + "");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        Assert.assertFalse(kBuilder.hasErrors());
    }

    @Test
    public void testDeclareExtendsJavaParentOuterPackaga() {
        String drl = "package org.drools.test; \n" + (("import org.drools.compiler.integrationtests.ExtendsTest.X; \n" + "declare Student extends X end \n") + "");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        Assert.assertFalse(kBuilder.hasErrors());
    }

    @Test
    public void testDeclareExtendsMissingJavaParent() {
        String drl = "package org.drools.test; \n" + (("import org.drools.compiler.integrationtests.ExtendsTest.Y; \n" + "declare Student extends Y end \n") + "");
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        if (kBuilder.hasErrors()) {
            System.err.println(kBuilder.getErrors());
        }
        Assert.assertTrue(kBuilder.hasErrors());
    }

    @Test
    public void testDeclareExtendsWithFullyQualifiedName() {
        String drl = "package org.drools.extends.test; \n" + ((("" + "declare org.drools.extends.test.Foo end \n") + "declare org.drools.extends.test.Bar extends org.drools.extends.test.Foo end \n") + "");
        KieServices kieServices = Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write(kieServices.getResources().newByteArrayResource(drl.getBytes()).setSourcePath("test.drl").setResourceType(DRL));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs);
        kieBuilder.buildAll();
        Assert.assertFalse(kieBuilder.getResults().hasMessages(ERROR));
    }

    @Test
    public void testExtendsBasic() throws Exception {
        final KieBase kbase = loadKnowledgeBase("extend_rule_test.drl");
        final KieSession session = createKnowledgeSession(kbase);
        // Test 2 levels of inheritance, and basic rule
        List list = new ArrayList();
        session.setGlobal("list", list);
        final Cheese mycheese = new Cheese("cheddar", 4);
        final FactHandle handle = session.insert(mycheese);
        session.fireAllRules();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains("rule 4"));
        Assert.assertTrue(list.contains("rule 2b"));
        // Test 2nd level (parent) to make sure rule honors the extend rule
        list = new ArrayList();
        session.setGlobal("list", list);
        session.delete(handle);
        final Cheese mycheese2 = new Cheese("notcheddar", 4);
        final FactHandle handle2 = session.insert(mycheese2);
        session.fireAllRules();
        Assert.assertEquals("rule 4", list.get(0));
        Assert.assertEquals(1, list.size());
        // Test 3 levels of inheritance, all levels
        list = new ArrayList();
        session.setGlobal("list", list);
        session.delete(handle2);
        final Cheese mycheese3 = new Cheese("stilton", 6);
        final FactHandle handle3 = session.insert(mycheese3);
        session.fireAllRules();
        // System.out.println(list.toString());
        Assert.assertEquals("rule 3", list.get(0));
        Assert.assertEquals(1, list.size());
        // Test 3 levels of inheritance, third only
        list = new ArrayList();
        session.setGlobal("list", list);
        session.delete(handle3);
        final Cheese mycheese4 = new Cheese("notstilton", 6);
        final FactHandle handle4 = session.insert(mycheese4);
        session.fireAllRules();
        // System.out.println(((List) session.getGlobal( "list" )).toString());
        Assert.assertTrue(((((List) (session.getGlobal("list"))).size()) == 0));
        // Test 3 levels of inheritance, 2nd only
        list = new ArrayList();
        session.setGlobal("list", list);
        session.delete(handle4);
        final Cheese mycheese5 = new Cheese("stilton", 7);
        session.insert(mycheese5);
        session.fireAllRules();
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testExtendsBasic2() {
        final KieBase kbase = loadKnowledgeBase("test_RuleExtend.drl");
        final KieSession ksession = createKnowledgeSession(kbase);
        final List results = new ArrayList();
        ksession.setGlobal("results", results);
        final Cheese stilton = new Cheese("stilton", 5);
        final Cheese cheddar = new Cheese("cheddar", 7);
        final Cheese brie = new Cheese("brie", 5);
        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);
        ksession.fireAllRules();
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("stilton", results.get(0));
        Assert.assertEquals("brie", results.get(1));
    }
}

