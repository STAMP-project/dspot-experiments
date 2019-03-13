/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import ResourceType.DRL;
import ResourceType.PKG;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.FileManager;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class KnowledgeBuilderTest {
    private FileManager fileManager;

    @Test
    public void testCompositeKnowledgeBuilder() throws Exception {
        String rule = "package org.drools.compiler.test\n" + (((("rule R1 when\n" + "   $fieldA : FactA( $fieldB : fieldB )\n") + "   FactB( this == $fieldB, fieldA == $fieldA )\n") + "then\n") + "end");
        String declarationA = "package org.drools.compiler.test\n" + (("declare FactA\n" + "    fieldB: FactB\n") + "end\n");
        String declarationB = "package org.drools.compiler.test\n" + (("declare FactB\n" + "    fieldA: FactA\n") + "end\n");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(declarationA.getBytes()), DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(declarationB.getBytes()), DRL);
        Assert.assertTrue(kbuilder.hasErrors());
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(declarationA.getBytes()), DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(declarationB.getBytes()), DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), DRL);
        Assert.assertTrue(kbuilder.hasErrors());
        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.batch().add(ResourceFactory.newByteArrayResource(rule.getBytes()), DRL).add(ResourceFactory.newByteArrayResource(declarationA.getBytes()), DRL).add(ResourceFactory.newByteArrayResource(declarationB.getBytes()), DRL).build();
        if (kbuilder2.hasErrors()) {
            Assert.fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder2.getKnowledgePackages());
        KieSession ksession = kbase.newKieSession();
        FactType aType = kbase.getFactType("org.drools.compiler.test", "FactA");
        Object a = aType.newInstance();
        FactType bType = kbase.getFactType("org.drools.compiler.test", "FactB");
        Object b = bType.newInstance();
        aType.set(a, "fieldB", b);
        bType.set(b, "fieldA", a);
        // JBRULES-3683 - check that the recurisive type declaration doesn't cause a StackOverflowError
        a.toString();
        b.toString();
        ksession.insert(a);
        ksession.insert(b);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
    }

    @Test
    public void testDifferentPackages() throws Exception {
        String rule = "package org.drools.compiler.test.rule\n" + ((((((("import org.drools.compiler.testA.FactA\n" + "import org.drools.compiler.testB.FactB\n") + "rule R1 when\n") + "   $fieldA : FactA( $fieldB : fieldB, bigint == 1 )\n") + "   FactB( this == $fieldB, fieldA == $fieldA )\n") + "then\n") + "   list.add(\"OK\");") + "end");
        String declarationA = "package org.drools.compiler.testA\n" + ((((("import org.drools.compiler.testB.FactB\n" + "import java.math.BigInteger\n") + "declare FactA\n") + "    fieldB: FactB\n") + "    bigint: BigInteger\n") + "end\n");
        String declarationB = "package org.drools.compiler.testB\n" + (((("import org.drools.compiler.testA.FactA\n" + "global java.util.List list\n") + "declare FactB\n") + "    fieldA: FactA\n") + "end\n");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.batch().type(DRL).add(ResourceFactory.newByteArrayResource(rule.getBytes())).add(ResourceFactory.newByteArrayResource(declarationA.getBytes())).add(ResourceFactory.newByteArrayResource(declarationB.getBytes())).build();
        if (kbuilder.hasErrors()) {
            Assert.fail(kbuilder.getErrors().toString());
        }
        String declarationC = "package org.drools.compiler.testA\n" + (("declare FactC\n" + "    field : UnknownClass\n") + "end\n");
        kbuilder.add(ResourceFactory.newByteArrayResource(declarationC.getBytes()), DRL);
        Assert.assertTrue(kbuilder.hasErrors());
        kbuilder.undo();
        Assert.assertFalse(kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        FactType aType = kbase.getFactType("org.drools.compiler.testA", "FactA");
        Object a = aType.newInstance();
        FactType bType = kbase.getFactType("org.drools.compiler.testB", "FactB");
        Object b = bType.newInstance();
        aType.set(a, "fieldB", b);
        aType.set(a, "bigint", new BigInteger("1"));
        bType.set(b, "fieldA", a);
        ksession.insert(a);
        ksession.insert(b);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
        Assert.assertEquals("OK", list.get(0));
    }

    @Test
    public void testUndoRule() throws Exception {
        String rule = "package org.drools.compiler.test\n" + (((((((("global java.util.List list\n" + "import org.drools.compiler.test.FactA\n") + "import org.drools.compiler.test.FactB\n") + "rule R1 when\n") + "   FactA( j == 1 )\n") + "   FactB( i == 1 )\n") + "then\n") + "   list.add(\"OK\");") + "end\n");
        String declarationA = "package org.drools.compiler.test\n" + (("declare FactA\n" + "    i : int\n") + "end\n");
        String declarationB = "package org.drools.compiler.test\n" + (("declare FactB\n" + "    i : int\n") + "end\n");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(declarationB.getBytes()), DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(declarationA.getBytes()), DRL);
        Assert.assertFalse(kbuilder.hasErrors());
        kbuilder.batch().type(DRL).add(ResourceFactory.newByteArrayResource(rule.getBytes())).build();
        Assert.assertTrue(kbuilder.hasErrors());
        kbuilder.undo();
        Assert.assertFalse(kbuilder.hasErrors());
        rule = "package org.drools.compiler.test\n" + (((((((("global java.util.List list\n" + "import org.drools.compiler.test.FactA\n") + "import org.drools.compiler.test.FactB\n") + "rule R1 when\n") + "   FactA( i == 1 )\n") + "   FactB( i == 1 )\n") + "then\n") + "   list.add(\"OK\");") + "end\n");
        kbuilder.batch().type(DRL).add(ResourceFactory.newByteArrayResource(rule.getBytes())).add(ResourceFactory.newByteArrayResource(declarationA.getBytes())).build();
        Assert.assertFalse(kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        FactType aType = kbase.getFactType("org.drools.compiler.test", "FactA");
        Object a = aType.newInstance();
        aType.set(a, "i", 1);
        FactType bType = kbase.getFactType("org.drools.compiler.test", "FactB");
        Object b = bType.newInstance();
        bType.set(b, "i", 1);
        ksession.insert(a);
        ksession.insert(b);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
        Assert.assertEquals("OK", list.get(0));
    }

    @Test
    public void testAddKPackageSingle() throws Exception {
        String rule = "package org.drools.compiler.test\n" + (((("import org.drools.compiler.StockTick\n" + "rule R1 when\n") + "   StockTick()\n") + "then\n") + "end\n");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();
        Assert.assertEquals(2, kpkgs.size());
        KiePackage kpkg = kpkgs.iterator().next();
        byte[] skpkg = DroolsStreamUtils.streamOut(kpkg);
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(skpkg), PKG);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        kpkgs = kbuilder.getKnowledgePackages();
        Assert.assertEquals(1, kpkgs.size());
        kpkg = kpkgs.iterator().next();
        Assert.assertEquals(1, kpkg.getRules().size());
    }

    @Test
    public void testAddKPackageCollection() throws Exception {
        String rule = "package org.drools.compiler.test\n" + ((((("import org.drools.compiler.StockTick\n" + "declare StockTick @role(event) end\n") + "rule R1 when\n") + "   StockTick()\n") + "then\n") + "end\n");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();
        Assert.assertEquals(2, kpkgs.size());
        byte[] skpkg = DroolsStreamUtils.streamOut(kpkgs);
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(skpkg), PKG);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        kpkgs = kbuilder.getKnowledgePackages();
        Assert.assertEquals(2, kpkgs.size());
    }

    @Test
    public void testAddPackageSingle() throws Exception {
        String rule = "package org.drools.compiler.test\n" + (((("import org.drools.compiler.StockTick\n" + "rule R1 when\n") + "   StockTick()\n") + "then\n") + "end\n");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();
        Assert.assertEquals(2, kpkgs.size());
        KiePackage kpkg = kpkgs.iterator().next();
        Assert.assertEquals(1, kpkg.getRules().size());
    }

    @Test
    public void testAddPackageArray() throws Exception {
        String rule = "package org.drools.compiler.test\n" + ((((("import org.drools.compiler.StockTick\n" + "declare StockTick @role(event) end\n") + "rule R1 when\n") + "   StockTick()\n") + "then\n") + "end\n");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();
        Assert.assertEquals(2, kpkgs.size());
    }

    @Test
    public void testRepeatedDeclarationInMultiplePackages() {
        String str = "package org.drools.test1;\n" + (((("import org.drools.compiler.Cheese;\n" + "") + "rule R\n") + "when Cheese() then end \n") + "");
        String str2 = "package org.drools.test2;\n" + (((("import org.drools.compiler.Cheese;\n" + "") + "rule S\n") + "when Cheese() then end \n") + "");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(str2.getBytes()), DRL);
        Assert.assertEquals(3, kbuilder.getKnowledgePackages().size());
        for (KiePackage kp : kbuilder.getKnowledgePackages()) {
            KnowledgePackageImpl kpi = ((KnowledgePackageImpl) (kp));
            TypeDeclaration cheez = kpi.getTypeDeclaration("Cheese");
            if ("org.drools.compiler".equals(kpi.getName())) {
                Assert.assertNotNull(cheez);
            } else {
                Assert.assertNull(cheez);
            }
        }
    }
}

