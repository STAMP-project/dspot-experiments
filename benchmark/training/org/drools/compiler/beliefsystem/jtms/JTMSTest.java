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
package org.drools.compiler.beliefsystem.jtms;


import java.util.ArrayList;
import java.util.List;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSystem;
import org.drools.core.common.NamedEntryPoint;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;


public class JTMSTest {
    @Test(timeout = 10000)
    public void testPosNegNonConflictingInsertions() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" + (((((((((((((((((((((((((((((((((("\n" + "import java.util.List \n") + "import org.drools.core.common.AgendaItem;") + "global java.util.List list;\n") + "\n") + "rule \"go1\"\n") + "when\n") + "    String( this == \'go1\' )\n") + "then\n") + "    insertLogical( \'neg\', \'neg\' );\n") + "end\n") + "\n") + "rule \"go2\"\n") + "when\n") + "    String( this == \'go2\' )\n") + "then\n") + "    insertLogical( \'pos\' );\n") + "end\n") + "\n") + "rule \"Positive\"\n") + "when\n") + "    $n : String( this != \'go1\' || == \'go2\' ) \n") + "then\n") + "    final String s = '+' + $n;") + "    final List l = list;") + "    l.add( s );\n") + "end\n") + "rule \"Negative\"\n") + "when\n") + "    $n : String(   _.neg, this != \'go1\' || == \'go2\' ) \n") + "then\n") + "    final String s = \'-\' + $n; \n") + "    final List l = list; \n") + "    l.add( s ); \n") + "end\n");
        KieSession kSession = getSessionFromString(s);
        List list = new ArrayList();
        kSession.setGlobal("list", list);
        ((org.kie.internal.event.rule.RuleEventManager) (kSession)).addEventListener(new RuleEventListener() {
            @Override
            public void onDeleteMatch(Match match) {
                String rule = match.getRule().getName();
                if (rule.equals("Positive")) {
                    list.remove(("+" + (match.getDeclarationValue("$n"))));
                } else
                    if (rule.equals("Negative")) {
                        list.remove(("-" + (match.getDeclarationValue("$n"))));
                    }

            }
        });
        FactHandle fhGo1 = kSession.insert("go1");
        kSession.fireAllRules();
        Assert.assertTrue(list.contains("-neg"));
        Assert.assertEquals(1, kSession.getEntryPoint("DEFAULT").getObjects().size());// just go1

        Assert.assertEquals(1, getNegativeObjects(kSession).size());
        FactHandle fhGo2 = kSession.insert("go2");
        kSession.fireAllRules();
        Assert.assertTrue(list.contains("-neg"));
        Assert.assertTrue(list.contains("+pos"));
        Assert.assertEquals(3, kSession.getEntryPoint("DEFAULT").getObjects().size());// go1, go2, pos

        Assert.assertEquals(1, getNegativeObjects(kSession).size());
        kSession.retract(fhGo1);
        kSession.fireAllRules();
        Assert.assertFalse(list.contains("-neg"));
        Assert.assertTrue(list.contains("+pos"));
        Assert.assertEquals(2, kSession.getEntryPoint("DEFAULT").getObjects().size());// go2, pos

        Assert.assertEquals(0, getNegativeObjects(kSession).size());
        kSession.retract(fhGo2);
        kSession.fireAllRules();
        Assert.assertFalse(list.contains("-neg"));
        Assert.assertFalse(list.contains("+pos"));
        Assert.assertEquals(0, kSession.getEntryPoint("DEFAULT").getObjects().size());
        Assert.assertEquals(0, getNegativeObjects(kSession).size());
    }

    @Test(timeout = 10000)
    public void testConflictToggleWithoutGoingEmpty() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" + ((((((((((((((((((((((((((((((((((((((((((((((("\n" + "import java.util.List \n") + "import org.drools.core.common.AgendaItem;") + "global java.util.List list;\n") + "\n") + "rule \"go1\"\n") + "when\n") + "    String( this == \'go1\' )\n") + "then\n") + "    insertLogical( \'xxx\' );\n") + "end\n") + "rule \"go2\"\n") + "when\n") + "    String( this == \'go2\' )\n") + "then\n") + "    insertLogical( \'xxx\');\n") + "end\n") + "rule \"go3\"\n") + "when\n") + "    String( this == \'go3\' )\n") + "then\n") + "    insertLogical( \'xxx\');\n") + "end\n") + "\n") + "rule \"go4\"\n") + "when\n") + "    String( this == \'go4\' )\n") + "then\n") + "    insertLogical( \'xxx\', \'neg\' );\n") + "end\n") + "\n") + "rule \"Positive\"\n") + "when\n") + "    $n : String( this == \'xxx\' ) \n") + "then\n") + "    final String s = '+' + $n;") + "    final List l = list;") + "    l.add( s );\n") + "end\n") + "rule \"Negative\"\n") + "when\n") + "    $n : String( _.neg, this == \'xxx\' )\n") + "then\n") + "    final String s = \'-\' + $n; \n") + "    final List l = list; \n") + "    l.add( s ); \n") + "end\n") + "");
        KieSession kSession = getSessionFromString(s);
        List list = new ArrayList();
        kSession.setGlobal("list", list);
        addEventListener(new RuleEventListener() {
            @Override
            public void onDeleteMatch(Match match) {
                String rule = match.getRule().getName();
                if (rule.equals("Positive")) {
                    list.remove(("+" + (match.getDeclarationValue("$n"))));
                } else
                    if (rule.equals("Negative")) {
                        list.remove(("-" + (match.getDeclarationValue("$n"))));
                    }

            }
        });
        FactHandle fhGo1 = kSession.insert("go1");
        FactHandle fhGo2 = kSession.insert("go2");
        FactHandle fhGo3 = kSession.insert("go3");
        kSession.fireAllRules();
        System.out.println(list);
        Assert.assertTrue(list.contains("+xxx"));
        FactHandle fhGo4 = kSession.insert("go4");
        kSession.fireAllRules();
        Assert.assertTrue(list.isEmpty());
        kSession.delete(fhGo4);
        kSession.fireAllRules();
        Assert.assertTrue(list.contains("+xxx"));
    }

    @Test(timeout = 10000)
    public void testConflictStrict() {
        KieSession kSession = getSessionFromFile("posNegConflict.drl");
        ArrayList list = new ArrayList();
        kSession.setGlobal("list", list);
        NamedEntryPoint ep = ((NamedEntryPoint) (getEntryPoint("DEFAULT")));
        JTMSBeliefSystem bs = ((JTMSBeliefSystem) (ep.getTruthMaintenanceSystem().getBeliefSystem()));
        bs.STRICT = true;
        try {
            kSession.fireAllRules();
            Assert.fail("A fact and its negation should have been asserted, but no exception was trhown in strict mode");
        } catch (Exception e) {
        } finally {
            bs.STRICT = false;
        }
    }

    @Test
    public void testPrimeJustificationWithEqualityMode() {
        String droolsSource = "package org.drools.tms.test; \n" + (((((((((((((((((((((("declare Bar end \n" + "") + "declare Holder x : Bar end \n") + "") + "") + "rule Init \n") + "when \n") + "then \n") + "   insert( new Holder( new Bar() ) ); \n") + "end \n") + "rule Justify \n") + "when \n") + " $s : Integer() \n") + " $h : Holder( $b : x ) \n") + "then \n") + " insertLogical( $b ); \n") + "end \n") + "rule React \n") + "when \n") + " $b : Bar(  ) \n") + "then \n") + " System.out.println( $b );  \n") + "end \n");
        KieSession session = getSessionFromString(droolsSource);
        FactHandle handle1 = session.insert(10);
        FactHandle handle2 = session.insert(20);
        Assert.assertEquals(4, session.fireAllRules());
        session.delete(handle1);
        Assert.assertEquals(0, session.fireAllRules());
    }
}

