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


import PropertySpecificOption.ALLOWED;
import PropertySpecificOption.ALWAYS;
import java.util.ArrayList;
import java.util.List;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;


@RunWith(Parameterized.class)
public class AccumulateUnsupportedWithModelTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AccumulateUnsupportedWithModelTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    // See https://issues.jboss.org/browse/DROOLS-2733
    @Test(timeout = 10000)
    public void testMVELAccumulate() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration, "org/drools/compiler/integrationtests/test_AccumulateMVEL.drl");
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();
            wm.setGlobal("results", results);
            wm.insert(new Person("Bob", "stilton", 20));
            wm.insert(new Person("Mark", "provolone"));
            wm.insert(new Cheese("stilton", 10));
            wm.insert(new Cheese("brie", 5));
            wm.insert(new Cheese("provolone", 150));
            wm.fireAllRules();
            Assert.assertEquals(165, results.get(0));
            Assert.assertEquals(10, results.get(1));
            Assert.assertEquals(150, results.get(2));
            Assert.assertEquals(10, results.get(3));
            Assert.assertEquals(210, results.get(4));
        } finally {
            wm.dispose();
        }
    }

    // See https://issues.jboss.org/browse/DROOLS-2733
    @Test(timeout = 10000)
    public void testMVELAccumulate2WM() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration, "org/drools/compiler/integrationtests/test_AccumulateMVEL.drl");
        final KieSession wm1 = kbase.newKieSession();
        try {
            final List<?> results1 = new ArrayList<>();
            wm1.setGlobal("results", results1);
            final List<?> results2 = new ArrayList<>();
            final KieSession wm2 = kbase.newKieSession();
            try {
                wm2.setGlobal("results", results2);
                wm1.insert(new Person("Bob", "stilton", 20));
                wm1.insert(new Person("Mark", "provolone"));
                wm2.insert(new Person("Bob", "stilton", 20));
                wm2.insert(new Person("Mark", "provolone"));
                wm1.insert(new Cheese("stilton", 10));
                wm1.insert(new Cheese("brie", 5));
                wm2.insert(new Cheese("stilton", 10));
                wm1.insert(new Cheese("provolone", 150));
                wm2.insert(new Cheese("brie", 5));
                wm2.insert(new Cheese("provolone", 150));
                wm1.fireAllRules();
                wm2.fireAllRules();
            } finally {
                wm2.dispose();
            }
            Assert.assertEquals(165, results1.get(0));
            Assert.assertEquals(10, results1.get(1));
            Assert.assertEquals(150, results1.get(2));
            Assert.assertEquals(10, results1.get(3));
            Assert.assertEquals(210, results1.get(4));
            Assert.assertEquals(165, results2.get(0));
            Assert.assertEquals(10, results2.get(1));
            Assert.assertEquals(150, results2.get(2));
            Assert.assertEquals(10, results2.get(3));
            Assert.assertEquals(210, results2.get(4));
        } finally {
            wm1.dispose();
        }
    }

    @Test
    public void testAccFunctionOpaqueJoins() {
        // DROOLS-661
        testAccFunctionOpaqueJoins(ALLOWED);
    }

    @Test
    public void testAccFunctionOpaqueJoinsWithPropertyReactivity() {
        // DROOLS-1445
        testAccFunctionOpaqueJoins(ALWAYS);
    }

    @Test
    public void testAccumulateWithSameSubnetwork() {
        final String drl = ((((((((((((((((((((((("package org.drools.compiler.test;\n" + "import ") + (Cheese.class.getCanonicalName())) + ";\n") + "import ") + (Person.class.getCanonicalName())) + ";\n") + "global java.util.List list; \n") + "rule r1 salience 100 \n") + "    when\n") + "        $person      : Person( name == \'Alice\', $likes : likes )\n") + "        $total       : Number() from accumulate( $p : Person(likes != $likes, $l : likes) and $c : Cheese( type == $l ),\n") + "                                                min($c.getPrice()) )\n") + "    then\n") + "        list.add( \'r1\' + \':\' + $total);\n") + "end\n") + "rule r2 \n") + "    when\n") + "        $person      : Person( name == \'Alice\', $likes : likes )\n") + "        $total       : Number() from accumulate( $p : Person(likes != $likes, $l : likes) and $c : Cheese( type == $l ),\n") + "                                                max($c.getPrice()) )\n") + "    then\n") + "        list.add( \'r2\' + \':\' + $total);\n") + "end\n") + "";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            wm.setGlobal("list", list);
            // Check the network formation, to ensure the RiaNode is shared.
            final ObjectTypeNode cheeseOtn = KieUtil.getObjectTypeNode(kbase, Cheese.class);
            Assert.assertNotNull(cheeseOtn);
            final ObjectSink[] oSinks = cheeseOtn.getObjectSinkPropagator().getSinks();
            Assert.assertEquals(1, oSinks.length);
            final JoinNode cheeseJoin = ((JoinNode) (oSinks[0]));
            final LeftTupleSink[] ltSinks = cheeseJoin.getSinkPropagator().getSinks();
            Assert.assertEquals(1, ltSinks.length);
            final RightInputAdapterNode rian = ((RightInputAdapterNode) (ltSinks[0]));
            Assert.assertEquals(2, rian.getObjectSinkPropagator().size());// RiaNode is shared, if this has two outputs

            wm.insert(new Cheese("stilton", 10));
            wm.insert(new Person("Alice", "brie"));
            wm.insert(new Person("Bob", "stilton"));
            wm.fireAllRules();
            Assert.assertEquals(2, list.size());
            Assert.assertEquals("r1:10", list.get(0));
            Assert.assertEquals("r2:10", list.get(1));
        } finally {
            wm.dispose();
        }
    }
}

