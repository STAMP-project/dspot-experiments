/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.oopath.graph;


import ResourceType.DRL;
import java.util.ArrayList;
import java.util.List;
import org.drools.core.phreak.AbstractReactiveObject;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;


public class OOPathOnGraphTest {
    @Test
    public void testOOPathOnGraph() {
        String drl = ((((((((((((((("import org.drools.compiler.oopath.graph.*;\n" + "import ") + (OOPathOnGraphTest.Library.class.getCanonicalName())) + ";\n") + "import ") + (OOPathOnGraphTest.Book.class.getCanonicalName())) + ";\n") + "import ") + (OOPathOnGraphTest.Person.class.getCanonicalName())) + ";\n") + "global java.util.List list\n") + "\n") + "rule R when\n") + "  Vertex( it instanceof Library, $a : /outVs/outVs/it#Person[ age > 40 ] )\n") + "then\n") + "  list.add( $a.getName() );\n") + "end\n";
        KieSession ksession = new KieHelper().addContent(drl, DRL).build().newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        Vertex<OOPathOnGraphTest.Library> library = getGraph();
        ksession.insert(library);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains("Mario"));
        list.clear();
        Vertex<?> book = library.getOutVs().get(0);
        Vertex<OOPathOnGraphTest.Person> alan = new Vertex<OOPathOnGraphTest.Person>(new OOPathOnGraphTest.Person("Alan", 53));
        book.connectTo(alan);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains("Alan"));
    }

    @Test
    public void testOOPathOnGraphWithReactiveContentModification() {
        String drl = ((((((((((((((("import org.drools.compiler.oopath.graph.*;\n" + "import ") + (OOPathOnGraphTest.Library.class.getCanonicalName())) + ";\n") + "import ") + (OOPathOnGraphTest.Book.class.getCanonicalName())) + ";\n") + "import ") + (OOPathOnGraphTest.Person.class.getCanonicalName())) + ";\n") + "global java.util.List list\n") + "\n") + "rule R when\n") + "  Vertex( it instanceof Library, $a : /outVs/outVs/it#Person[ age > 25 ] )\n") + "then\n") + "  list.add( $a.getName() );\n") + "end\n";
        KieSession ksession = new KieHelper().addContent(drl, DRL).build().newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        Vertex<OOPathOnGraphTest.Library> library = getGraph();
        ksession.insert(library);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains("Mario"));
        list.clear();
        OOPathOnGraphTest.Person raoul = ((OOPathOnGraphTest.Person) (library.getOutVs().get(0).getOutVs().get(0).getIt()));
        Assert.assertEquals("Raoul", raoul.getName());
        raoul.setAge(((raoul.getAge()) + 1));
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains("Raoul"));
    }

    @Test
    public void testOOPathOnGraphWithReactiveContentModificationInSubgraph() {
        String drl = ((((((((((((((("import org.drools.compiler.oopath.graph.*;\n" + "import ") + (OOPathOnGraphTest.Library.class.getCanonicalName())) + ";\n") + "import ") + (OOPathOnGraphTest.Book.class.getCanonicalName())) + ";\n") + "import ") + (OOPathOnGraphTest.Person.class.getCanonicalName())) + ";\n") + "global java.util.List list\n") + "\n") + "rule R when\n") + "  Vertex( it instanceof Library, $v : /outVs/outVs[ /it#Person[ age > 25 ] ] )\n") + "then\n") + "  list.add( ((Person)$v.getIt()).getName() );\n") + "end\n";
        KieSession ksession = new KieHelper().addContent(drl, DRL).build().newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        Vertex<OOPathOnGraphTest.Library> library = getGraph();
        ksession.insert(library);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains("Mario"));
        list.clear();
        OOPathOnGraphTest.Person raoul = ((OOPathOnGraphTest.Person) (library.getOutVs().get(0).getOutVs().get(0).getIt()));
        Assert.assertEquals("Raoul", raoul.getName());
        raoul.setAge(((raoul.getAge()) + 1));
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains("Raoul"));
    }

    @Test
    public void testOOPathOnGraphWithNonReactiveContentModification() {
        String drl = ((((((((((((((("import org.drools.compiler.oopath.graph.*;\n" + "import ") + (OOPathOnGraphTest.Library.class.getCanonicalName())) + ";\n") + "import ") + (OOPathOnGraphTest.Book.class.getCanonicalName())) + ";\n") + "import ") + (OOPathOnGraphTest.Person.class.getCanonicalName())) + ";\n") + "global java.util.List list\n") + "\n") + "rule R when\n") + "  Vertex( it instanceof Library, $v : /outVs/outVs[ it#Person.age > 25 ] )\n") + "then\n") + "  list.add( ((Person)$v.getIt()).getName() );\n") + "end\n";
        KieSession ksession = new KieHelper().addContent(drl, DRL).build().newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        Vertex<OOPathOnGraphTest.Library> library = getGraph();
        ksession.insert(library);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains("Mario"));
        list.clear();
        OOPathOnGraphTest.Person raoul = ((OOPathOnGraphTest.Person) (library.getOutVs().get(0).getOutVs().get(0).getIt()));
        Assert.assertEquals("Raoul", raoul.getName());
        raoul.setAge(((raoul.getAge()) + 1));
        ksession.fireAllRules();
        Assert.assertEquals(0, list.size());
    }

    public static class Library {}

    public static class Book {
        private final String title;

        public Book(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class Person extends AbstractReactiveObject {
        private final String name;

        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
            notifyModification();
        }
    }
}

