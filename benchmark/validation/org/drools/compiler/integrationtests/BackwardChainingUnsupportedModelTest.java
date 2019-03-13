/**
 * Copyright 2005 JBoss Inc
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
package org.drools.compiler.integrationtests;


import AccumulateNode.AccumulateMemory;
import FromNode.FromMemory;
import PropertySpecificOption.ALLOWED;
import PropertySpecificOption.ALWAYS;
import Variable.v;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.drools.core.InitialFact;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.Variable;
import org.kie.api.runtime.rule.ViewChangedEventListener;


@RunWith(Parameterized.class)
public class BackwardChainingUnsupportedModelTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public BackwardChainingUnsupportedModelTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test
    public void testQueryWithEvalAndTypeBoxingUnboxing() {
        final String drl = "package org.drools.test;\n" + (((((((((((((((((((((((((((((((((((((((((((((((("\n" + "global java.util.List list \n;") + "\n") + "query primitiveInt( int $a )\n") + " Integer( intValue == $a )\n") + " eval( $a == 178 )\n") + "end\n") + "\n") + "query boxedInteger( Integer $a )\n") + " Integer( this == $a )\n") + " eval( $a == 178 )\n") + "end\n") + "\n") + "query boxInteger( int $a )\n") + " Integer( this == $a )\n") + " eval( $a == 178 )\n") + "end\n") + "\n") + "query unboxInteger( Integer $a )\n") + " Integer( intValue == $a )\n") + " eval( $a == 178 )\n") + "end\n") + "\n") + "query cast( int $a )\n") + " Integer( longValue == $a )\n") + " eval( $a == 178 )\n") + "end\n") + "") + "query cast2( long $a )\n") + " Integer( intValue == $a )\n") + " eval( $a == 178 )\n") + "end\n") + "\n") + "rule Init when then insert( 178 ); end\n") + "\n") + "rule Check\n") + "when\n") + " String()\n") + " ?primitiveInt( 178 ; )\n") + " ?boxedInteger( $x ; )\n") + " ?boxInteger( $x ; )\n") + " ?unboxInteger( $y ; )\n") + " ?cast( $z ; )\n") + " ?cast2( $z ; )\n") + "then\n") + " list.add( $x ); \n") + " list.add( $y ); \n") + " list.add( $z ); \n") + "end");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final ArrayList list = new ArrayList();
            ksession.setGlobal("list", list);
            ksession.fireAllRules();
            Assert.assertTrue(list.isEmpty());
            ksession.insert("go");
            ksession.fireAllRules();
            Assert.assertEquals(Arrays.asList(178, 178, 178), list);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNaniSearchsNoPropReactivity() throws IOException, ClassNotFoundException {
        testNaniSearchs(ALLOWED);
    }

    @Test
    public void testNaniSearchsWithPropReactivity() throws IOException, ClassNotFoundException {
        // DROOLS-1453
        testNaniSearchs(ALWAYS);
    }

    @Test(timeout = 10000)
    public void testSubNetworksAndQueries() {
        final String drl = "" + (((((((((((((((((((((((((((((((((((((((("package org.drools.compiler.test  \n" + "import java.util.List\n") + "import java.util.ArrayList\n") + "import java.util.Map\n") + "import java.util.HashMap\n") + "global List list\n") + "dialect \"mvel\"\n") + "\n") + "declare Location\n") + "    thing : String \n") + "    location : String \n") + "end") + "\n") + "declare Edible\n") + "   thing : String\n") + "end") + "\n") + "query whereFood( String x, String y ) \n") + "    Location(x, y;) Edible(x;) \n") + "end\n") + "\n") + "query look(String place, List food ) \n") + "    $s : String() // just here to give a OTN lookup point\n") + "    food := List() from accumulate( whereFood(thing, place;) ,") + "                                    collectList( thing ) )\n") + "    exists( whereFood(thing, place;) )\n") + "    not( whereFood(thing, place;) and\n ") + "         String( this == $s ) from thing )\n") + "end\n") + "\n") + "rule init when\n") + "then\n") + "        \n") + "        insert( new Location(\"apple\", \"kitchen\") );\n") + "        insert( new Location(\"crackers\", \"kitchen\") );\n") + "        insert( new Location(\"broccoli\", \"kitchen\") );\n") + "        insert( new Location(\"computer\", \"office\") );\n") + "        insert( new Edible(\"apple\") );\n") + "        insert( new Edible(\"crackers\") );\n") + "end\n") + "");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        // Get the accumulate node, so we can test it's memory later
        // now check beta memory was correctly cleared
        final List<ObjectTypeNode> nodes = getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for (final ObjectTypeNode n : nodes) {
            if ((getClassType()) == (String.class)) {
                node = n;
                break;
            }
        }
        Assert.assertNotNull(node);
        final BetaNode stringBetaNode = ((BetaNode) (node.getObjectSinkPropagator().getSinks()[0]));
        final QueryElementNode queryElementNode1 = ((QueryElementNode) (stringBetaNode.getSinkPropagator().getSinks()[0]));
        final RightInputAdapterNode riaNode1 = ((RightInputAdapterNode) (queryElementNode1.getSinkPropagator().getSinks()[0]));
        final AccumulateNode accNode = ((AccumulateNode) (riaNode1.getObjectSinkPropagator().getSinks()[0]));
        final QueryElementNode queryElementNode2 = ((QueryElementNode) (accNode.getSinkPropagator().getSinks()[0]));
        final RightInputAdapterNode riaNode2 = ((RightInputAdapterNode) (queryElementNode2.getSinkPropagator().getSinks()[0]));
        final ExistsNode existsNode = ((ExistsNode) (riaNode2.getObjectSinkPropagator().getSinks()[0]));
        final QueryElementNode queryElementNode3 = ((QueryElementNode) (existsNode.getSinkPropagator().getSinks()[0]));
        final FromNode fromNode = ((FromNode) (queryElementNode3.getSinkPropagator().getSinks()[0]));
        final RightInputAdapterNode riaNode3 = ((RightInputAdapterNode) (fromNode.getSinkPropagator().getSinks()[0]));
        final NotNode notNode = ((NotNode) (riaNode3.getObjectSinkPropagator().getSinks()[0]));
        final KieSession ksession = kbase.newKieSession();
        try {
            final InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (ksession));
            final AccumulateNode.AccumulateMemory accMemory = ((AccumulateNode.AccumulateMemory) (wm.getNodeMemory(accNode)));
            final BetaMemory existsMemory = ((BetaMemory) (wm.getNodeMemory(existsNode)));
            final FromNode.FromMemory fromMemory = ((FromNode.FromMemory) (wm.getNodeMemory(fromNode)));
            final BetaMemory notMemory = ((BetaMemory) (wm.getNodeMemory(notNode)));
            final List<Map<String, Object>> list = new ArrayList<>();
            ksession.setGlobal("list", list);
            final FactHandle fh = ksession.insert("bread");
            ksession.fireAllRules();
            final List food = new ArrayList();
            // Execute normal query and check no subnetwork tuples are left behind
            QueryResults results = ksession.getQueryResults("look", "kitchen", v);
            Assert.assertEquals(1, results.size());
            for (final QueryResultsRow row : results) {
                food.addAll(((Collection) (row.get("food"))));
            }
            Assert.assertEquals(2, food.size());
            BackwardChainingTest.assertContains(new String[]{ "crackers", "apple" }, food);
            Assert.assertEquals(0, accMemory.getBetaMemory().getRightTupleMemory().size());
            Assert.assertEquals(0, existsMemory.getRightTupleMemory().size());
            Assert.assertEquals(0, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            Assert.assertEquals(0, notMemory.getRightTupleMemory().size());
            // Now execute an open query and ensure the memory is left populated
            food.clear();
            final List foodUpdated = new ArrayList();
            final LiveQuery query = ksession.openLiveQuery("look", new Object[]{ "kitchen", Variable.v }, new ViewChangedEventListener() {
                public void rowUpdated(final Row row) {
                    foodUpdated.addAll(((Collection) (row.get("food"))));
                }

                public void rowDeleted(final Row row) {
                }

                public void rowInserted(final Row row) {
                    food.addAll(((Collection) (row.get("food"))));
                }
            });
            Assert.assertEquals(2, food.size());
            BackwardChainingTest.assertContains(new String[]{ "crackers", "apple" }, food);
            Assert.assertEquals(2, accMemory.getBetaMemory().getRightTupleMemory().size());
            Assert.assertEquals(2, existsMemory.getRightTupleMemory().size());
            Assert.assertEquals(2, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            Assert.assertEquals(0, notMemory.getRightTupleMemory().size());
            food.clear();
            // Now try again, make sure it only delete's it's own tuples
            results = ksession.getQueryResults("look", "kitchen", v);
            Assert.assertEquals(1, results.size());
            for (final QueryResultsRow row : results) {
                food.addAll(((Collection) (row.get("food"))));
            }
            Assert.assertEquals(2, food.size());
            BackwardChainingTest.assertContains(new String[]{ "crackers", "apple" }, food);
            Assert.assertEquals(2, accMemory.getBetaMemory().getRightTupleMemory().size());
            Assert.assertEquals(2, existsMemory.getRightTupleMemory().size());
            Assert.assertEquals(2, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            Assert.assertEquals(0, notMemory.getRightTupleMemory().size());
            food.clear();
            // do an update and check it's  still memory size 2
            // however this time the food should be empty, as 'crackers' now blocks the not.
            ksession.update(fh, "crackers");
            ksession.fireAllRules();
            Assert.assertEquals(2, accMemory.getBetaMemory().getRightTupleMemory().size());
            Assert.assertEquals(2, existsMemory.getRightTupleMemory().size());
            Assert.assertEquals(2, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            Assert.assertEquals(1, notMemory.getRightTupleMemory().size());
            Assert.assertEquals(0, foodUpdated.size());
            // do an update and check it's  still memory size 2
            // this time
            ksession.update(fh, "oranges");
            ksession.fireAllRules();
            Assert.assertEquals(2, accMemory.getBetaMemory().getRightTupleMemory().size());
            Assert.assertEquals(2, existsMemory.getRightTupleMemory().size());
            Assert.assertEquals(2, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            Assert.assertEquals(0, notMemory.getRightTupleMemory().size());
            Assert.assertEquals(2, food.size());
            BackwardChainingTest.assertContains(new String[]{ "crackers", "apple" }, food);
            // Close the open
            query.close();
            Assert.assertEquals(0, accMemory.getBetaMemory().getRightTupleMemory().size());
            Assert.assertEquals(0, existsMemory.getRightTupleMemory().size());
            Assert.assertEquals(0, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            Assert.assertEquals(0, notMemory.getRightTupleMemory().size());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testInsertionOrderTwo() {
        final StringBuilder drlBuilder = new StringBuilder(("" + (((((((((((((((((("package org.drools.compiler.test \n" + "import java.util.List \n") + "global List list \n") + "declare Thing \n") + "    thing : String @key \n") + "end \n") + "declare Edible extends Thing \n") + "end \n") + "declare Location extends Thing \n") + "    location : String  @key \n") + "end \n") + "declare Here \n") + "    place : String \n") + "end \n") + "rule kickOff \n") + "when \n") + "    Integer( $i: intValue ) \n") + "then \n") + "    switch( $i ){ \n")));
        String[] facts = new String[]{ "new Edible( 'peach' )", "new Location( 'peach', 'table' )", "new Here( 'table' )" };
        int f = 0;
        for (final String fact : facts) {
            for (final String fact1 : facts) {
                for (final String fact2 : facts) {
                    // use a Set to make sure we only include 3 unique values
                    final Set<String> set = new HashSet<>();
                    set.add(fact);
                    set.add(fact1);
                    set.add(fact2);
                    if ((set.size()) == 3) {
                        drlBuilder.append("    case ").append((f++)).append(": \n").append("        insert( ").append(fact).append(" ); \n").append("        insert( ").append(fact1).append(" ); \n").append("        insert( ").append(fact2).append(" ); \n").append("        break; \n");
                    }
                }
            }
        }
        facts = new String[]{ "new Edible( 'peach' )", "new Location( 'table', 'office' )", "new Location( 'peach', 'table' )", "new Here( 'office' )" };
        int h = f;
        for (final String fact : facts) {
            for (final String fact1 : facts) {
                for (final String fact3 : facts) {
                    for (final String fact2 : facts) {
                        // use a Set to make sure we only include 3 unique values
                        final Set<String> set = new HashSet<>();
                        set.add(fact);
                        set.add(fact1);
                        set.add(fact3);
                        set.add(fact2);
                        if ((set.size()) == 4) {
                            drlBuilder.append("    case ").append((h++)).append(": \n").append("        insert( ").append(fact).append(" ); \n").append("        insert( ").append(fact1).append(" ); \n").append("        insert( ").append(fact3).append(" ); \n").append("        insert( ").append(fact2).append(" ); \n").append("        break; \n");
                        }
                    }
                }
            }
        }
        drlBuilder.append(("    } \n" + ((((((((((((((((((((((("end \n" + "\n") + "query whereFood( String x, String y ) \n") + "    ( Location(x, y;) and \n") + "    Edible(x;) ) \n ") + "    or  \n") + "    ( Location(z, y;) and whereFood(x, z;) ) \n") + "end ") + "query look(String place, List things, List food)  \n") + "    Here(place;) \n") + "    things := List() from accumulate( Location(thing, place;), \n") + "                                      collectList( thing ) ) \n") + "    food := List() from accumulate( whereFood(thing, place;), \n") + "                                    collectList( thing ) ) \n") + "end \n") + "rule reactiveLook \n") + "when \n") + "    Here( $place : place)  \n") + "    look($place, $things; $food := food) \n") + "then \n") + "    list.addAll( $things ); \n") + "    list.addAll( $food   ); \n") + "end \n") + "")));
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drlBuilder.toString());
        for (int i = 0; i < f; i++) {
            final KieSession ksession = kbase.newKieSession();
            try {
                final List<String> list = new ArrayList<>();
                ksession.setGlobal("list", list);
                ksession.fireAllRules();
                list.clear();
                InternalFactHandle fh = ((InternalFactHandle) (ksession.insert(i)));
                ksession.fireAllRules();
                Assert.assertEquals(2, list.size());
                Assert.assertEquals("peach", list.get(0));
                Assert.assertEquals("peach", list.get(1));
                list.clear();
                final InternalFactHandle[] handles = ksession.getFactHandles().toArray(new InternalFactHandle[0]);
                for (int j = 0; j < (handles.length); j++) {
                    if (((handles[j].getObject()) instanceof InitialFact) || ((handles[j].getObject()) instanceof Integer)) {
                        continue;
                    }
                    handles[j] = BackwardChainingTest.getFactHandle(handles[j], ksession);
                    final Object o = handles[j].getObject();
                    // first retract + assert
                    ksession.delete(handles[j]);
                    handles[j] = ((InternalFactHandle) (ksession.insert(o)));
                    ksession.fireAllRules();
                    Assert.assertEquals(2, list.size());
                    Assert.assertEquals("peach", list.get(0));
                    Assert.assertEquals("peach", list.get(1));
                    list.clear();
                    // now try update
                    // session was serialised so need to get factHandle
                    handles[j] = BackwardChainingTest.getFactHandle(handles[j], ksession);
                    ksession.update(handles[j], handles[j].getObject());
                    ksession.fireAllRules();
                    Assert.assertEquals(2, list.size());
                    Assert.assertEquals("peach", list.get(0));
                    Assert.assertEquals("peach", list.get(1));
                    list.clear();
                }
                fh = BackwardChainingTest.getFactHandle(fh, ksession);
                ksession.delete(fh);
            } finally {
                ksession.dispose();
            }
        }
        for (int i = f; i < h; i++) {
            final KieSession ksession = kbase.newKieSession();
            try {
                final List<String> list = new ArrayList<>();
                ksession.setGlobal("list", list);
                ksession.fireAllRules();
                list.clear();
                InternalFactHandle fh = ((InternalFactHandle) (ksession.insert(i)));
                ksession.fireAllRules();
                Assert.assertEquals(2, list.size());
                Assert.assertEquals("table", list.get(0));
                Assert.assertEquals("peach", list.get(1));
                list.clear();
                final InternalFactHandle[] handles = ksession.getFactHandles().toArray(new InternalFactHandle[0]);
                for (int j = 0; j < (handles.length); j++) {
                    if (((handles[j].getObject()) instanceof InitialFact) || ((handles[j].getObject()) instanceof Integer)) {
                        continue;
                    }
                    handles[j] = BackwardChainingTest.getFactHandle(handles[j], ksession);
                    final Object o = handles[j].getObject();
                    ksession.delete(handles[j]);
                    handles[j] = ((InternalFactHandle) (ksession.insert(o)));
                    ksession.fireAllRules();
                    Assert.assertEquals(2, list.size());
                    Assert.assertEquals("table", list.get(0));
                    Assert.assertEquals("peach", list.get(1));
                    list.clear();
                    // now try update
                    handles[j] = BackwardChainingTest.getFactHandle(handles[j], ksession);
                    ksession.update(handles[j], handles[j].getObject());
                    ksession.fireAllRules();
                    Assert.assertEquals(2, list.size());
                    Assert.assertEquals("table", list.get(0));
                    Assert.assertEquals("peach", list.get(1));
                    list.clear();
                }
                fh = BackwardChainingTest.getFactHandle(fh, ksession);
                ksession.delete(fh);
            } finally {
                ksession.dispose();
            }
        }
    }
}

