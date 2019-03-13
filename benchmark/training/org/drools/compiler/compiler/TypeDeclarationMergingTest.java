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
package org.drools.compiler.compiler;


import Format.POJO;
import ResourceType.DRL;
import Role.Type.EVENT;
import Role.Type.FACT;
import TypeDeclaration.FORMAT_BIT;
import TypeDeclaration.ROLE_BIT;
import TypeDeclaration.TYPESAFE_BIT;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.rule.TypeDeclaration;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.definition.type.Position;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class TypeDeclarationMergingTest {
    @Test
    public void testMask() {
        TypeDeclaration tdeclr = new TypeDeclaration(CImpl.class.getName());
        Assert.assertEquals(0, tdeclr.getSetMask());
        tdeclr.setRole(EVENT);
        Assert.assertEquals(ROLE_BIT, ((tdeclr.getSetMask()) & (TypeDeclaration.ROLE_BIT)));
        Assert.assertFalse(((TypeDeclaration.TYPESAFE_BIT) == ((tdeclr.getSetMask()) & (TypeDeclaration.TYPESAFE_BIT))));
        Assert.assertFalse(((TypeDeclaration.FORMAT_BIT) == ((tdeclr.getSetMask()) & (TypeDeclaration.FORMAT_BIT))));
        tdeclr.setTypesafe(false);
        Assert.assertEquals(ROLE_BIT, ((tdeclr.getSetMask()) & (TypeDeclaration.ROLE_BIT)));
        Assert.assertEquals(TYPESAFE_BIT, ((tdeclr.getSetMask()) & (TypeDeclaration.TYPESAFE_BIT)));
        Assert.assertFalse(((TypeDeclaration.FORMAT_BIT) == ((tdeclr.getSetMask()) & (TypeDeclaration.FORMAT_BIT))));
        tdeclr = new TypeDeclaration(CImpl.class.getName());
        tdeclr.setTypesafe(true);
        Assert.assertFalse(((TypeDeclaration.ROLE_BIT) == ((tdeclr.getSetMask()) & (TypeDeclaration.ROLE_BIT))));
        Assert.assertEquals(TYPESAFE_BIT, ((tdeclr.getSetMask()) & (TypeDeclaration.TYPESAFE_BIT)));
        Assert.assertFalse(((TypeDeclaration.FORMAT_BIT) == ((tdeclr.getSetMask()) & (TypeDeclaration.FORMAT_BIT))));
        tdeclr.setFormat(POJO);
        Assert.assertFalse(((TypeDeclaration.ROLE_BIT) == ((tdeclr.getSetMask()) & (TypeDeclaration.ROLE_BIT))));
        Assert.assertEquals(TYPESAFE_BIT, ((tdeclr.getSetMask()) & (TypeDeclaration.TYPESAFE_BIT)));
        Assert.assertEquals(FORMAT_BIT, ((tdeclr.getSetMask()) & (TypeDeclaration.FORMAT_BIT)));
    }

    @Test
    public void testOverrideFromParentClass() {
        // inherits role, but not typesafe
        String str = (((((((((("" + (("package org.drools.compiler.test \n" + "global java.util.List list \n") + "declare ")) + (CImpl.class.getCanonicalName())) + "\n") + "    @typesafe(true)\n") + "    @role(event)\n") + "end\n") + "declare ") + (DImpl.class.getCanonicalName())) + "\n") + "    @typesafe(false)\n") + "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder(str);
        TypeDeclaration tdecl = builder.getTypeDeclaration(DImpl.class);
        Assert.assertEquals(false, tdecl.isTypesafe());
        Assert.assertEquals(EVENT, tdecl.getRole());
    }

    @Test
    public void testInheritNoneExitenceFromParentClass() {
        // inherits role and typesafe
        String str = ((((("" + (("package org.drools.compiler.test \n" + "global java.util.List list \n") + "declare ")) + (CImpl.class.getCanonicalName())) + "\n") + "    @typesafe(true)\n") + "    @role(event)\n") + "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder(str);
        TypeDeclaration tdecl = builder.getTypeDeclaration(DImpl.class);
        Assert.assertEquals(true, tdecl.isTypesafe());
        Assert.assertEquals(EVENT, tdecl.getRole());
    }

    @Test
    public void testInheritExitenceFromParentClass() {
        // inherits role and typesafe
        String str = ((((((((("" + (("package org.drools.compiler.test \n" + "global java.util.List list \n") + "declare ")) + (CImpl.class.getCanonicalName())) + "\n") + "    @typesafe(true)\n") + "    @role(event)\n") + "end\n") + "declare ") + (DImpl.class.getCanonicalName())) + "\n") + "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder(str);
        TypeDeclaration tdecl = builder.getTypeDeclaration(DImpl.class);
        Assert.assertEquals(true, tdecl.isTypesafe());
        Assert.assertEquals(EVENT, tdecl.getRole());
    }

    @Test
    public void testOverrideFromParentInterface() {
        // inherits role but not typesafe
        String str = (((((((((("" + (("package org.drools.compiler.test \n" + "global java.util.List list \n") + "declare ")) + (IB.class.getCanonicalName())) + "\n") + "    @typesafe(true)\n") + "    @role(event)\n") + "end\n") + "declare ") + (DImpl.class.getCanonicalName())) + "\n") + "    @typesafe(false)\n") + "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder(str);
        TypeDeclaration tdecl = builder.getTypeDeclaration(DImpl.class);
        Assert.assertEquals(false, tdecl.isTypesafe());
        Assert.assertEquals(EVENT, tdecl.getRole());
    }

    @Test
    public void testOverrideFromDeeperParentInterface() {
        // inherits role but not typesafe
        String str = (((((((((("" + (("package org.drools.compiler.test \n" + "global java.util.List list \n") + "declare ")) + (IA.class.getCanonicalName())) + "\n") + "    @typesafe(true)\n") + "    @role(event)\n") + "end\n") + "declare ") + (DImpl.class.getCanonicalName())) + "\n") + "    @typesafe(false)\n") + "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder(str);
        TypeDeclaration tdecl = builder.getTypeDeclaration(DImpl.class);
        Assert.assertEquals(false, tdecl.isTypesafe());
        Assert.assertEquals(EVENT, tdecl.getRole());
    }

    @Test
    public void testOverrideFromDeeperHierarchyParentInterface() {
        // inherits role from and typesafe from the other
        String str = (((((((((((((("" + (("package org.drools.compiler.test \n" + "global java.util.List list \n") + "declare ")) + (IA.class.getCanonicalName())) + "\n") + "    @typesafe(true)\n") + "    @role(event)\n") + "end\n") + "declare ") + (IB.class.getCanonicalName())) + "\n") + "    @role(fact)\n") + "end\n") + "declare ") + (DImpl.class.getCanonicalName())) + "\n") + "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder(str);
        TypeDeclaration tdecl = builder.getTypeDeclaration(DImpl.class);
        Assert.assertEquals(true, tdecl.isTypesafe());
        Assert.assertEquals(FACT, tdecl.getRole());
    }

    @Test
    public void testInheritNoneExitenceFromParentInterface() {
        // inherits role and typesafe
        String str = ((((("" + (("package org.drools.compiler.test \n" + "global java.util.List list \n") + "declare ")) + (IB.class.getCanonicalName())) + "\n") + "    @typesafe(true)\n") + "    @role(event)\n") + "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder(str);
        TypeDeclaration tdecl = builder.getTypeDeclaration(DImpl.class);
        Assert.assertEquals(true, tdecl.isTypesafe());
        Assert.assertEquals(EVENT, tdecl.getRole());
    }

    @Test
    public void testInheritExitenceFromParentInterface() {
        // inherits role and typesafe
        String str = ((((((((("" + (("package org.drools.compiler.test \n" + "global java.util.List list \n") + "declare ")) + (IB.class.getCanonicalName())) + "\n") + "    @typesafe(true)\n") + "    @role(event)\n") + "end\n") + "declare ") + (DImpl.class.getCanonicalName())) + "\n") + "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder(str);
        TypeDeclaration tdecl = builder.getTypeDeclaration(DImpl.class);
        Assert.assertEquals(true, tdecl.isTypesafe());
        Assert.assertEquals(EVENT, tdecl.getRole());
    }

    @Test
    public void testOverrideFromMixedHierarchyParentInterface() {
        // inherits role from and typesafe from the other
        String str = (((((((((((((("" + (("package org.drools.compiler.test \n" + "global java.util.List list \n") + "declare ")) + (IA.class.getCanonicalName())) + "\n") + "    @typesafe(true)\n") + "    @role(event)\n") + "end\n") + "declare ") + (CImpl.class.getCanonicalName())) + "\n") + "    @role(fact)\n") + "end\n") + "declare ") + (DImpl.class.getCanonicalName())) + "\n") + "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder(str);
        TypeDeclaration tdecl = builder.getTypeDeclaration(DImpl.class);
        Assert.assertEquals(true, tdecl.isTypesafe());
        Assert.assertEquals(FACT, tdecl.getRole());
    }

    @Test
    public void testNotOverwritePOJOMetadata() {
        final String eventClassName = TypeDeclarationMergingTest.PositionAnnotatedEvent.class.getCanonicalName();
        // should add metadata to metadata already defined in POJO
        String str = ((((((((((("package org.drools.compiler.test \n" + "declare ") + eventClassName) + "\n") + " @role(event)\n") + "end \n") + "rule \'sample rule\' \n") + "when \n") + " ") + eventClassName) + "( \'value1\', \'value2\'; ) \n") + "then \n") + "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        try {
            kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        } catch (IndexOutOfBoundsException e) {
            final String msg = e.getMessage();
            if ("Error trying to access field at position 0".equals(msg)) {
                Assert.fail("@Position declared in POJO was ignored.");
            } else {
                Assert.fail(("Check the test, unexpected error message: " + msg));
            }
        }
        Assert.assertFalse(("Check the test, unexpected error message: " + (kbuilder.getErrors())), kbuilder.hasErrors());
    }

    public static class PositionAnnotatedEvent {
        @Position(1)
        private String arg1;

        @Position(0)
        private String arg0;

        public String getArg1() {
            return arg1;
        }

        public String getArg0() {
            return arg0;
        }

        public void setArg1(String arg1) {
            this.arg1 = arg1;
        }

        public void setArg0(String arg0) {
            this.arg0 = arg0;
        }
    }
}

