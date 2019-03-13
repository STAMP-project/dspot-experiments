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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.testcoverage.common.model.Alarm;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;


/**
 * see JBPM-4764
 */
@RunWith(Parameterized.class)
public class ActivateAndDeleteOnListenerTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ActivateAndDeleteOnListenerTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test
    public void testActivateOnMatchAndDelete() {
        testActivateOnMatch(new DefaultAgendaEventListener() {
            @Override
            public void matchCreated(final MatchCreatedEvent event) {
                final Collection<? extends FactHandle> alarms = event.getKieRuntime().getFactHandles(new ClassObjectFilter(Alarm.class));
                for (final FactHandle alarm : alarms) {
                    event.getKieRuntime().delete(alarm);
                }
            }
        });
    }

    @Test
    public void testActivateOnMatchAndUpdate() {
        testActivateOnMatch(new DefaultAgendaEventListener() {
            @Override
            public void matchCreated(final MatchCreatedEvent event) {
                final Collection<? extends FactHandle> alarms = event.getKieRuntime().getFactHandles(new ClassObjectFilter(Alarm.class));
                for (final FactHandle alarm : alarms) {
                    event.getKieRuntime().update(alarm, new Alarm());
                }
            }
        });
    }

    @Test
    public void testEagerEvaluationWith2Paths() {
        final String drl = "package org.simple \n" + ((((((((((("rule xxx \n" + "when \n") + "  $s : String()\n") + "  $i : Integer()\n") + "then \n") + "end  \n") + "rule yyy \n") + "when \n") + "  $s : String()\n") + "  $i : Integer()\n") + "then \n") + "end  \n");
        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();
            final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
                @Override
                public void matchCreated(final MatchCreatedEvent event) {
                    list.add("activated");
                }

                @Override
                public void matchCancelled(final MatchCancelledEvent event) {
                    list.add("cancelled");
                }
            };
            ksession.addEventListener(agendaEventListener);
            ksession.insert("test");
            Assert.assertEquals(0, list.size());
            final FactHandle fh = ksession.insert(1);
            Assert.assertEquals(2, list.size());
            Assert.assertEquals("activated", list.get(0));
            Assert.assertEquals("activated", list.get(1));
            list.clear();
            ksession.delete(fh);
            Assert.assertEquals(2, list.size());
            Assert.assertEquals("cancelled", list.get(0));
            Assert.assertEquals("cancelled", list.get(1));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEagerEvaluationWith2SubPaths() {
        final String drl = "package org.simple \n" + ((((((((((("rule xxx \n" + "when \n") + "  $s : String()\n") + "  exists( Integer() or Long() )\n") + "then \n") + "end  \n") + "rule yyy \n") + "when \n") + "  $s : String()\n") + "  exists( Integer() or Long() )\n") + "then \n") + "end  \n");
        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();
            final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
                public void matchCreated(final MatchCreatedEvent event) {
                    list.add("activated");
                }
            };
            ksession.addEventListener(agendaEventListener);
            ksession.insert("test");
            Assert.assertEquals(0, list.size());
            ksession.insert(1);
            Assert.assertEquals(2, list.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOneLinkedAndOneUnlinkedPath() {
        final String drl = "package org.simple \n" + ((((((((((((("rule xxx \n" + "when \n") + "  String()\n") + "  Integer()\n") + "  Long()\n") + "then \n") + "end  \n") + "rule yyy \n") + "when \n") + "  String()\n") + "  Integer()\n") + "  Boolean()\n") + "then \n") + "end  \n");
        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();
            final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
                public void matchCreated(final MatchCreatedEvent event) {
                    list.add(event.getMatch().getRule().getName());
                }
            };
            ksession.addEventListener(agendaEventListener);
            ksession.insert("test");
            Assert.assertEquals(0, list.size());
            ksession.insert(Boolean.TRUE);
            Assert.assertEquals(0, list.size());
            ksession.insert(1);
            Assert.assertEquals(1, list.size());
            Assert.assertEquals("yyy", list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOneLazyAndOneImmediateSubPath() {
        final String drl = "package org.simple \n" + ((((((((((("rule xxx \n" + "when \n") + "  $s : String()\n") + "  exists( Integer() or Long() )\n") + "then \n") + "end  \n") + "rule yyy \n") + "when \n") + "  $s : String()\n") + "  exists( Integer() or Long() )\n") + "then \n") + "end  \n");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("activate-delete-test", kieBaseTestConfiguration, drl);
        final KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(new ForceEagerActivationOption.FILTERED(( rule) -> rule.getName().equals("yyy")));
        final KieSession ksession = kbase.newKieSession(conf, null);
        try {
            final List<String> list = new ArrayList<>();
            final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
                public void matchCreated(final MatchCreatedEvent event) {
                    list.add(event.getMatch().getRule().getName());
                }
            };
            ksession.addEventListener(agendaEventListener);
            ksession.insert("test");
            Assert.assertEquals(0, list.size());
            ksession.insert(1);
            Assert.assertEquals(1, list.size());
            Assert.assertEquals("yyy", list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEagerEvaluationWithSubSubPath() {
        final String drl = "package org.simple \n" + ((((("rule xxx \n" + "when \n") + "  $s : String()\n") + "  exists( Boolean() and not(not(Integer()) and not(Double())) )\n") + "then \n") + "end  \n");
        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();
            final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
                public void matchCreated(final MatchCreatedEvent event) {
                    list.add("activated");
                }
            };
            ksession.addEventListener(agendaEventListener);
            ksession.insert(Boolean.TRUE);
            Assert.assertEquals(0, list.size());
            ksession.insert("test");
            Assert.assertEquals(0, list.size());
            ksession.insert(1);
            Assert.assertEquals(1, list.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000L)
    public void testSegMemInitializationWithForceEagerActivation() {
        // DROOLS-1247
        final String drl = "global java.util.List list\n" + (((((((((((((((((((((((((("declare  SimpleFact end\n" + "declare  AnotherFact end\n") + "\n") + "rule Init when\n") + "    not (SimpleFact())\n") + "    not (AnotherFact())\n") + "then\n") + "    insert(new SimpleFact());\n") + "    insert(new AnotherFact());\n") + "end\n") + "\n") + "rule R1 no-loop when\n") + "    $f : SimpleFact()  \n") + "    $h : AnotherFact() \n") + "    $s : String() \n") + "    eval(true)\n") + "then\n") + "    list.add(\"1\");\n") + "end\n") + "\n") + "rule R2 no-loop when\n") + "    $f : SimpleFact()  \n") + "    $h : AnotherFact()  \n") + "    $s : String() \n") + "then\n") + "    list.add(\"2\");\n") + "end");
        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);
            ksession.insert("test");
            ksession.fireAllRules();
            Assert.assertEquals(2, list.size());
            Assert.assertTrue(list.containsAll(Arrays.asList("1", "2")));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000L)
    public void testSegMemInitializationWithForceEagerActivationAndAcc() {
        // DROOLS-1247
        final String drl = "global java.util.List list\n" + (((((((((((((((((((((((((("declare  SimpleFact end\n" + "declare  AnotherFact end\n") + "\n") + "rule Init when\n") + "    not (SimpleFact())\n") + "    not (AnotherFact())\n") + "then\n") + "    insert(new SimpleFact());\n") + "    insert(new AnotherFact());\n") + "end\n") + "\n") + "rule R1 no-loop when\n") + "    $f : SimpleFact()  \n") + "    $h : AnotherFact() \n") + "    $s : String() \n") + "    accumulate($i: Integer(), $res : count($i))\n") + "then\n") + "    list.add(\"1\");\n") + "end\n") + "\n") + "rule R2 no-loop when\n") + "    $f : SimpleFact()  \n") + "    $h : AnotherFact()  \n") + "    $s : String() \n") + "then\n") + "    list.add(\"2\");\n") + "end");
        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);
            ksession.insert("test");
            ksession.fireAllRules();
            Assert.assertEquals(2, list.size());
            Assert.assertTrue(list.containsAll(Arrays.asList("1", "2")));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000L)
    public void testSegMemInitializationWithForceEagerActivationAndExistsWithNots() {
        // DROOLS-1247
        final String drl = "global java.util.List list\n" + (((((((((((((((((((((((((("declare  SimpleFact end\n" + "declare  AnotherFact end\n") + "\n") + "rule Init when\n") + "    not (SimpleFact())\n") + "    not (AnotherFact())\n") + "then\n") + "    insert(new SimpleFact());\n") + "    insert(new AnotherFact());\n") + "end\n") + "\n") + "rule R1 no-loop when\n") + "    $f : SimpleFact()  \n") + "    $h : AnotherFact() \n") + "    $s : String() \n") + "    exists(not(Integer()) or not(Double()))\n") + "then\n") + "    list.add(\"1\");\n") + "end\n") + "\n") + "rule R2 no-loop when\n") + "    $f : SimpleFact()  \n") + "    $h : AnotherFact()  \n") + "    $s : String() \n") + "then\n") + "    list.add(\"2\");\n") + "end");
        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);
            ksession.insert("test");
            ksession.fireAllRules();
            Assert.assertEquals(2, list.size());
            Assert.assertTrue(list.containsAll(Arrays.asList("1", "2")));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000L)
    public void testNoLoopWithForceEagerActivation() {
        // DROOLS-1349
        final String drl = ((((((("import " + (Person.class.getCanonicalName())) + "\n") + "\n") + "rule Birthday no-loop when\n") + "    $p: Person()\n") + "then\n") + "    modify($p) { setAge($p.getAge()+1) };\n") + "end";
        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final Person mario = new Person("mario", 42);
            ksession.insert(mario);
            ksession.fireAllRules();
            Assert.assertEquals(43, mario.getAge());
        } finally {
            ksession.dispose();
        }
    }
}

