/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import EventProcessingOption.STREAM;
import ResourceType.DRL;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;


public class PolymorphismTest {
    @Test
    public void testModifySubclassOverWindow() {
        // DROOLS-1501
        String drl = "declare Number @role( event ) end\n" + (((((((((("declare Integer @role( event ) end\n" + "\n") + "rule R1 no-loop when\n") + "    $i: Integer()\n") + "then\n") + "    update($i);\n") + "end\n") + "rule R2 when\n") + "    $n: Number() over window:length(1)\n") + "then\n") + "end");
        KieSession ksession = new KieHelper().addContent(drl, DRL).build(STREAM).newKieSession();
        ksession.insert(1);
        ksession.fireAllRules();
    }

    @Test
    public void testModifySubclass() {
        // DROOLS-1501
        String drl = (((((((((((((((((((((((((((((((((("import " + (PolymorphismTest.A.class.getCanonicalName())) + "\n;") + "import ") + (PolymorphismTest.B.class.getCanonicalName())) + "\n;") + "import ") + (PolymorphismTest.C.class.getCanonicalName())) + "\n;") + "import ") + (PolymorphismTest.D.class.getCanonicalName())) + "\n;") + "\n") + "rule Ra when\n") + "    $a: A(id == 3)\n") + "then\n") + "    delete($a);\n") + "end\n") + "rule Rb when\n") + "    $a: A(id == 2)\n") + "    $b: B($id : id == 2)\n") + "then\n") + "    modify($b) { setId($id+1) };\n") + "end\n") + "rule Rc when\n") + "    $a: A(id == 1)\n") + "    $c: C($id : id == 1)\n") + "then\n") + "    modify($c) { setId($id+1) };\n") + "end\n") + "rule Rd when\n") + "    $a: A(id == 0)\n") + "    $d: D($id : id == 0)\n") + "then\n") + "    modify($d) { setId($id+1) };\n") + "end";
        KieSession ksession = new KieHelper().addContent(drl, DRL).build(STREAM).newKieSession();
        ksession.insert(new PolymorphismTest.D(0));
        ksession.fireAllRules();
        Assert.assertEquals(0, ksession.getObjects().size());
    }

    @Test
    public void testModifySubclass2() {
        // DROOLS-1501
        String drl = (((((((((((((((((((((((((((((((((("import " + (PolymorphismTest.A.class.getCanonicalName())) + "\n;") + "import ") + (PolymorphismTest.B.class.getCanonicalName())) + "\n;") + "import ") + (PolymorphismTest.C.class.getCanonicalName())) + "\n;") + "import ") + (PolymorphismTest.D.class.getCanonicalName())) + "\n;") + "\n") + "rule Rd when\n") + "    $a: D(id == 0)\n") + "    $d: C($id : id == 0)\n") + "then\n") + "    modify($d) { setId($id+1) };\n") + "end\n") + "rule Rc when\n") + "    $a: D(id == 1)\n") + "    $c: B($id : id == 1)\n") + "then\n") + "    modify($c) { setId($id+1) };\n") + "end\n") + "rule Rb when\n") + "    $a: D(id == 2)\n") + "    $b: A($id : id == 2)\n") + "then\n") + "    modify($b) { setId($id+1) };\n") + "end\n") + "rule Ra when\n") + "    $a: D(id == 3)\n") + "then\n") + "    delete($a);\n") + "end";
        KieSession ksession = new KieHelper().addContent(drl, DRL).build(STREAM).newKieSession();
        FactHandle fh = ksession.insert(new PolymorphismTest.D(0));
        ksession.fireAllRules();
        Assert.assertEquals(0, ksession.getObjects().size());
        System.out.println(fh);
    }

    public static class A {
        private int id;

        public A(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class B extends PolymorphismTest.A {
        public B(int id) {
            super(id);
        }
    }

    public static class C extends PolymorphismTest.B {
        public C(int id) {
            super(id);
        }
    }

    public static class D extends PolymorphismTest.C {
        public D(int id) {
            super(id);
        }
    }
}

