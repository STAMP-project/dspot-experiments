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
package org.drools.compiler.oopath;


import ResourceType.DRL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.assertj.core.api.Assertions;
import org.drools.compiler.oopath.model.Child;
import org.drools.compiler.oopath.model.Man;
import org.drools.compiler.oopath.model.Toy;
import org.drools.compiler.oopath.model.Woman;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;


public class OOPathBindTest {
    @Test
    public void testBindIntegerFireAllRules() throws InterruptedException, ExecutionException {
        testBindInteger(false);
    }

    @Test(timeout = 1000)
    public void testBindIntegerFireUntilHalt() throws InterruptedException, ExecutionException {
        testBindInteger(true);
    }

    @Test
    public void testBindString() {
        final String drl = "import org.drools.compiler.oopath.model.*;\n" + (((((("global java.util.List list\n" + "\n") + "rule R when\n") + "  Adult( $name: /name )\n") + "then\n") + "  list.add( $name );\n") + "end\n");
        testScenarioBindString(drl, "Bob", "Alice");
    }

    @Test
    public void testBindStringWithConstraint() {
        final String drl = "import org.drools.compiler.oopath.model.*;\n" + (((((("global java.util.List list\n" + "\n") + "rule R when\n") + "  Adult( $name: /name[this == \"Bob\"] )\n") + "then\n") + "  list.add( $name );\n") + "end\n");
        testScenarioBindString(drl, "Bob");
    }

    @Test
    public void testBindStringWithAlphaConstraint() {
        final String drl = "import org.drools.compiler.oopath.model.*;\n" + (((((("global java.util.List list\n" + "\n") + "rule R when\n") + "  Adult( $name: /name[this.length == 3, this != \"George\"] )\n") + "then\n") + "  list.add( $name );\n") + "end\n");
        testScenarioBindString(drl, "Bob");
    }

    @Test
    public void testBindStringWithBetaConstraint() {
        final String drl = "import org.drools.compiler.oopath.model.*;\n" + ((((((("global java.util.List list\n" + "\n") + "rule R when\n") + "  $alice: Adult(name == \"Alice\") \n") + "  Adult( $name: /name[this.length == 3, this != $alice.name] )\n") + "then\n") + "  list.add( $name );\n") + "end\n");
        testScenarioBindString(drl, "Bob");
    }

    @Test
    public void testBindObjectFromList() {
        final String drl = "import org.drools.compiler.oopath.model.*;\n" + (((((("global java.util.List list\n" + "\n") + "rule R when\n") + "  Adult( $child: /children )\n") + "then\n") + "  list.add( $child.getName() );\n") + "end\n");
        final KieSession ksession = new KieHelper().addContent(drl, DRL).build().newKieSession();
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        final Man bob = new Man("Bob", 40);
        bob.addChild(new Child("Charles", 12));
        bob.addChild(new Child("Debbie", 8));
        ksession.insert(bob);
        ksession.fireAllRules();
        Assertions.assertThat(list).containsExactlyInAnyOrder("Charles", "Debbie");
    }

    @Test
    public void testBindList() {
        final String drl = "import org.drools.compiler.oopath.model.*;\n" + (((((("global java.util.List list\n" + "\n") + "rule R when\n") + "  Man( $toys: /wife/children.toys )\n") + "then\n") + "  list.add( $toys.size() );\n") + "end\n");
        final KieSession ksession = new KieHelper().addContent(drl, DRL).build().newKieSession();
        final List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        final Woman alice = new Woman("Alice", 38);
        final Man bob = new Man("Bob", 40);
        bob.setWife(alice);
        final Child charlie = new Child("Charles", 12);
        final Child debbie = new Child("Debbie", 8);
        alice.addChild(charlie);
        alice.addChild(debbie);
        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));
        ksession.insert(bob);
        ksession.fireAllRules();
        Assertions.assertThat(list).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    public void testBindListWithConstraint() {
        final String drl = "import org.drools.compiler.oopath.model.*;\n" + (((((("global java.util.List list\n" + "\n") + "rule R when\n") + "  Man( $toys: /wife/children[age > 10].toys )\n") + "then\n") + "  list.add( $toys.size() );\n") + "end\n");
        final KieSession ksession = new KieHelper().addContent(drl, DRL).build().newKieSession();
        final List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        final Woman alice = new Woman("Alice", 38);
        final Man bob = new Man("Bob", 40);
        bob.setWife(alice);
        final Child charlie = new Child("Charles", 12);
        final Child debbie = new Child("Debbie", 8);
        alice.addChild(charlie);
        alice.addChild(debbie);
        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));
        ksession.insert(bob);
        ksession.fireAllRules();
        Assertions.assertThat(list).containsExactlyInAnyOrder(2);
    }
}

