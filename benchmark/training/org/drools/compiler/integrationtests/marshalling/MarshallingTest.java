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
package org.drools.compiler.integrationtests.marshalling;


import ClockType.PSEUDO_CLOCK;
import DeclarativeAgendaOption.ENABLED;
import EnvironmentName.OBJECT_MARSHALLING_STRATEGIES;
import EqualityBehaviorOption.EQUALITY;
import EventProcessingOption.STREAM;
import ResourceType.DRL;
import TimedRuleExecutionOption.YES;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.drools.compiler.Address;
import org.drools.compiler.Cell;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.FactB;
import org.drools.compiler.FactC;
import org.drools.compiler.Message;
import org.drools.compiler.Person;
import org.drools.compiler.Primitives;
import org.drools.compiler.integrationtests.IteratorToList;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.common.BaseNode;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.RuleBaseNodes;
import org.drools.core.rule.MapBackedClassLoader;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.time.SessionClock;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;


public class MarshallingTest extends CommonTestMethodBase {
    @Test
    public void testSerializable() throws Exception {
        Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Serializable.drl");
        KiePackage kpkg = kpkgs.iterator().next();
        kpkg = SerializationHelper.serializeObject(kpkg);
        InternalKnowledgeBase kbase = ((InternalKnowledgeBase) (loadKnowledgeBase()));
        kbase.addPackages(Collections.singleton(kpkg));
        Map<String, InternalKnowledgeBase> map = new HashMap<String, InternalKnowledgeBase>();
        map.put("x", kbase);
        map = SerializationHelper.serializeObject(map);
        kbase = map.get("x");
        final org.kie[] rules = kbase.getKiePackages().iterator().next().getRules().toArray(new Rule[0]);
        Assert.assertEquals(4, rules.length);
        Assert.assertEquals("match Person 1", rules[0].getName());
        Assert.assertEquals("match Person 2", rules[1].getName());
        Assert.assertEquals("match Person 3", rules[2].getName());
        Assert.assertEquals("match Integer", rules[3].getName());
        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal("list", new ArrayList());
        final Person bob = new Person("bob");
        ksession.insert(bob);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        Assert.assertEquals(1, ksession.getFactCount());
        Assert.assertEquals(bob, ksession.getObjects().iterator().next());
        Assert.assertEquals(2, agendaSize());
        ksession.fireAllRules();
        List list = ((List) (ksession.getGlobal("list")));
        Assert.assertEquals(3, list.size());
        // because of agenda-groups
        Assert.assertEquals(new Integer(4), list.get(0));
        // need to create a new collection or otherwise the collection will be identity based
        List<?> objects = new ArrayList<Object>(ksession.getObjects());
        Assert.assertEquals(2, objects.size());
        Assert.assertTrue(objects.contains(bob));
        Assert.assertTrue(objects.contains(new Person("help")));
    }

    @Test
    public void testSerializeWorkingMemoryAndRuleBase1() throws Exception {
        // has the first newStatefulSession before the ruleBase is serialised
        Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Serializable.drl");
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
        KieSession session = kBase.newKieSession();
        Map map = new HashMap();
        map.put("x", kBase);
        map = SerializationHelper.serializeObject(map);
        kBase = ((InternalKnowledgeBase) (map.get("x")));
        kBase.addPackages(kpkgs);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        kBase = SerializationHelper.serializeObject(kBase);
        session.setGlobal("list", new ArrayList());
        final Person bob = new Person("bob");
        session.insert(bob);
        flushPropagations();
        org.kie[] rules = ((org.kie[]) (kBase.getPackage("org.drools.compiler.test").getRules().toArray(new Rule[0])));
        Assert.assertEquals(4, rules.length);
        Assert.assertEquals("match Person 1", rules[0].getName());
        Assert.assertEquals("match Person 2", rules[1].getName());
        Assert.assertEquals("match Person 3", rules[2].getName());
        Assert.assertEquals("match Integer", rules[3].getName());
        Assert.assertEquals(1, session.getObjects().size());
        Assert.assertEquals(bob, IteratorToList.convert(session.getObjects().iterator()).get(0));
        Assert.assertEquals(2, agendaSize());
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        final List list = ((List) (session.getGlobal("list")));
        Assert.assertEquals(3, list.size());
        // because of agenda-groups
        Assert.assertEquals(new Integer(4), list.get(0));
        Assert.assertEquals(2, session.getObjects().size());
        Assert.assertTrue(IteratorToList.convert(session.getObjects().iterator()).contains(bob));
        Assert.assertTrue(IteratorToList.convert(session.getObjects().iterator()).contains(new Person("help")));
    }

    @Test
    public void testSerializeWorkingMemoryAndRuleBase2() throws Exception {
        Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Serializable.drl");
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
        // serialise a hashmap with the RuleBase as a key
        Map map = new HashMap();
        map.put("x", kBase);
        map = SerializationHelper.serializeObject(map);
        kBase = ((InternalKnowledgeBase) (map.get("x")));
        KieSession session = kBase.newKieSession();
        // serialise the working memory before population
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        kBase.addPackages(kpkgs);
        session.setGlobal("list", new ArrayList());
        final Person bob = new Person("bob");
        session.insert(bob);
        flushPropagations();
        org.kie[] rules = ((org.kie[]) (kBase.getPackage("org.drools.compiler.test").getRules().toArray(new Rule[0])));
        Assert.assertEquals(4, rules.length);
        Assert.assertEquals("match Person 1", rules[0].getName());
        Assert.assertEquals("match Person 2", rules[1].getName());
        Assert.assertEquals("match Person 3", rules[2].getName());
        Assert.assertEquals("match Integer", rules[3].getName());
        Assert.assertEquals(1, session.getObjects().size());
        Assert.assertEquals(bob, IteratorToList.convert(session.getObjects().iterator()).get(0));
        Assert.assertEquals(2, agendaSize());
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        final List list = ((List) (session.getGlobal("list")));
        Assert.assertEquals(3, list.size());
        // because of agenda-groups
        Assert.assertEquals(new Integer(4), list.get(0));
        Assert.assertEquals(2, session.getObjects().size());
        Assert.assertTrue(IteratorToList.convert(session.getObjects().iterator()).contains(bob));
        Assert.assertTrue(IteratorToList.convert(session.getObjects().iterator()).contains(new Person("help")));
    }

    @Test
    public void testSerializeWorkingMemoryAndRuleBase3() throws Exception {
        // has the first newStatefulSession after the ruleBase is serialised
        Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Serializable.drl");
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
        KieSession session = kBase.newKieSession();
        kBase.addPackages(kpkgs);
        session.setGlobal("list", new ArrayList());
        final Person bob = new Person("bob");
        session.insert(bob);
        // serialise a hashmap with the RuleBase as a key, after WM population
        Map map = new HashMap();
        map.put("x", kBase);
        map = SerializationHelper.serializeObject(map);
        kBase = ((InternalKnowledgeBase) (map.get("x")));
        // now try serialising with a fully populated wm from a serialised rulebase
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        org.kie[] rules = ((org.kie[]) (kBase.getPackage("org.drools.compiler.test").getRules().toArray(new Rule[0])));
        Assert.assertEquals(4, rules.length);
        Assert.assertEquals("match Person 1", rules[0].getName());
        Assert.assertEquals("match Person 2", rules[1].getName());
        Assert.assertEquals("match Person 3", rules[2].getName());
        Assert.assertEquals("match Integer", rules[3].getName());
        Assert.assertEquals(1, session.getObjects().size());
        Assert.assertEquals(bob, IteratorToList.convert(session.getObjects().iterator()).get(0));
        Assert.assertEquals(2, agendaSize());
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        final List list = ((List) (session.getGlobal("list")));
        Assert.assertEquals(3, list.size());
        // because of agenda-groups
        Assert.assertEquals(new Integer(4), list.get(0));
        Assert.assertEquals(2, session.getObjects().size());
        Assert.assertTrue(IteratorToList.convert(session.getObjects().iterator()).contains(bob));
        Assert.assertTrue(IteratorToList.convert(session.getObjects().iterator()).contains(new Person("help")));
    }

    @Test
    public void testSerializeAdd() throws Exception {
        Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Dynamic1.drl");
        kpkgs = SerializationHelper.serializeObject(kpkgs);
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
        kBase.addPackages(kpkgs);
        kBase = SerializationHelper.serializeObject(kBase);
        KieSession session = kBase.newKieSession();
        List list = new ArrayList();
        session.setGlobal("list", list);
        InternalFactHandle stilton = ((InternalFactHandle) (session.insert(new Cheese("stilton", 10))));
        InternalFactHandle brie = ((InternalFactHandle) (session.insert(new Cheese("brie", 10))));
        session.fireAllRules();
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals("stilton", list.get(0));
        // now recreate the rulebase, deserialize the session and test it
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        list = ((List) (session.getGlobal("list")));
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals("stilton", list.get(0));
        kpkgs = loadKnowledgePackages("../test_Dynamic3.drl");
        kpkgs = SerializationHelper.serializeObject(kpkgs);
        kBase.addPackages(kpkgs);
        InternalFactHandle stilton2 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 10))));
        InternalFactHandle brie2 = ((InternalFactHandle) (session.insert(new Cheese("brie", 10))));
        InternalFactHandle bob = ((InternalFactHandle) (session.insert(new Person("bob", 30))));
        session.fireAllRules();
        Assert.assertEquals(list.size(), 3);
        Assert.assertEquals(bob.getObject(), list.get(2));
        Assert.assertEquals("stilton", list.get(1));
        session.dispose();
    }

    @Test
    public void testSerializationOfIndexedWM() throws Exception {
        Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Serializable2.drl");
        kpkgs = SerializationHelper.serializeObject(kpkgs);
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
        kBase.addPackages(kpkgs);
        kBase = SerializationHelper.serializeObject(kBase);
        Map map = new HashMap();
        map.put("x", kBase);
        map = SerializationHelper.serializeObject(map);
        kBase = ((InternalKnowledgeBase) (map.get("x")));
        org.kie[] rules = ((org.kie[]) (kBase.getPackage("org.drools.compiler").getRules().toArray(new Rule[0])));
        Assert.assertEquals(3, rules.length);
        KieSession session = kBase.newKieSession();
        session.setGlobal("list", new ArrayList());
        final Primitives p = new Primitives();
        p.setBytePrimitive(((byte) (1)));
        p.setShortPrimitive(((short) (2)));
        p.setIntPrimitive(((int) (3)));
        session.insert(p);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        Assert.assertEquals(1, session.getObjects().size());
        Assert.assertEquals(p, IteratorToList.convert(session.getObjects().iterator()).get(0));
        Assert.assertEquals(3, agendaSize());
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        final List list = ((List) (session.getGlobal("list")));
        Assert.assertEquals(3, list.size());
        // because of agenda-groups
        Assert.assertEquals("1", list.get(0));
        Assert.assertEquals("2", list.get(1));
        Assert.assertEquals("3", list.get(2));
    }

    /* Here I am inserting data points which are not used by any rule (e.g Person).
     Later adding rule (e.g. Rule: 'match Person') for those data points.

     Result: Pkg/Rule addition is failing with ClassCastException
     */
    @Test
    public void testSerializeAdd2() throws Exception {
        // Create a rulebase, a session, and test it
        Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl");
        kpkgs = SerializationHelper.serializeObject(kpkgs);
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
        kBase.addPackages(kpkgs);
        kBase = SerializationHelper.serializeObject(kBase);
        List results = new ArrayList();
        KieSession session = kBase.newKieSession();
        session.setGlobal("results", results);
        InternalFactHandle stilton1 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 10))));
        session.insert(new Cheese("brie", 10));
        InternalFactHandle bob = ((InternalFactHandle) (session.insert(new Person("bob", 10))));
        // fire rules
        session.fireAllRules();
        // check the results are correct
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(stilton1.getObject(), results.get(0));
        // serialize session and rulebase
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        // dynamically add a new package
        kpkgs = loadKnowledgePackages("../test_Dynamic3_0.drl");
        kBase.addPackages(SerializationHelper.serializeObject(kpkgs));
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        InternalFactHandle stilton2 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 20))));
        session.insert(new Cheese("brie", 20));
        InternalFactHandle mark = ((InternalFactHandle) (session.insert(new Person("mark", 20))));
        session.fireAllRules();
        results = ((List) (session.getGlobal("results")));
        Assert.assertEquals(4, results.size());
        Assert.assertEquals(stilton2.getObject(), results.get(1));
        Assert.assertEquals(bob.getObject(), results.get(2));
        Assert.assertEquals(mark.getObject(), results.get(3));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        // dispose session
        session.dispose();
    }

    /* Here I am inserting data points(e.g. Cheese) with  'stilton' / 'brie' as type value.
     Initially I had only 1 rule ('like stilton') for data points(e.g. Cheese) having type as 'stilton'.

     Later added new rule ('like brie')  for data points(e.g. Cheese) having type as 'brie'.

     Result: new rule is not getting fired for new data points having type as 'brie'.
             Only for old data points having type as 'brie' the new rule got fired.
     */
    @Test
    public void testSerializeAdd_newRuleNotFiredForNewData() throws Exception {
        // Create a rulebase, a session, and test it
        Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl");
        kpkgs = SerializationHelper.serializeObject(kpkgs);
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
        kBase.addPackages(kpkgs);
        kBase = SerializationHelper.serializeObject(kBase);
        List results = new ArrayList();
        KieSession session = kBase.newKieSession();
        session.setGlobal("results", results);
        InternalFactHandle stilton1 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 10))));
        InternalFactHandle brie1 = ((InternalFactHandle) (session.insert(new Cheese("brie", 10))));
        session.fireAllRules();
        // serialize session and rulebase
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        // dynamically add a new package
        kpkgs = loadKnowledgePackages("../test_Dynamic1_1.drl");
        kBase.addPackages(SerializationHelper.serializeObject(kpkgs));
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        InternalFactHandle stilton2 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 20))));
        InternalFactHandle brie2 = ((InternalFactHandle) (session.insert(new Cheese("brie", 20))));
        InternalFactHandle brie3 = ((InternalFactHandle) (session.insert(new Cheese("brie", 30))));
        session.fireAllRules();
        Assert.assertEquals(5, results.size());
        Assert.assertEquals(stilton2.getObject(), results.get(1));
        Assert.assertEquals(brie1.getObject(), results.get(2));
        Assert.assertEquals(brie3.getObject(), results.get(3));
        Assert.assertEquals(brie2.getObject(), results.get(4));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session.dispose();
    }

    /* Works Fine if both the scenarios mentioned above are skipped. */
    @Test
    public void testSerializeAdd3() throws Exception {
        // Create a rulebase, a session, and test it
        Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl");
        kpkgs = SerializationHelper.serializeObject(kpkgs);
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
        kBase.addPackages(kpkgs);
        kBase = SerializationHelper.serializeObject(kBase);
        List results = new ArrayList();
        KieSession session = kBase.newKieSession();
        session.setGlobal("results", results);
        InternalFactHandle stilton1 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 10))));
        InternalFactHandle brie1 = ((InternalFactHandle) (session.insert(new Cheese("brie", 10))));
        session.fireAllRules();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(stilton1.getObject(), results.get(0));
        // serialize session and rulebase
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        // dynamic add pkgs
        kpkgs = loadKnowledgePackages("../test_Dynamic3_0.drl");
        kBase.addPackages(SerializationHelper.serializeObject(kpkgs));
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        InternalFactHandle stilton2 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 20))));
        InternalFactHandle brie2 = ((InternalFactHandle) (session.insert(new Cheese("brie", 20))));
        InternalFactHandle bob1 = ((InternalFactHandle) (session.insert(new Person("bob", 20))));
        InternalFactHandle bob2 = ((InternalFactHandle) (session.insert(new Person("bob", 30))));
        session.fireAllRules();
        Assert.assertEquals(4, results.size());
        Assert.assertEquals(stilton2.getObject(), results.get(1));
        Assert.assertEquals(bob2.getObject(), results.get(2));
        Assert.assertEquals(bob1.getObject(), results.get(3));
        // serialize session and rulebase
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        // dynamic add pkgs
        kpkgs = loadKnowledgePackages("../test_Dynamic1_2.drl");
        kBase.addPackages(SerializationHelper.serializeObject(kpkgs));
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        InternalFactHandle stilton3 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 40))));
        InternalFactHandle brie3 = ((InternalFactHandle) (session.insert(new Cheese("brie", 40))));
        InternalFactHandle bob3 = ((InternalFactHandle) (session.insert(new Person("bob", 40))));
        InternalFactHandle bob4 = ((InternalFactHandle) (session.insert(new Person("bob", 40))));
        InternalFactHandle addr1 = ((InternalFactHandle) (session.insert(new Address("bangalore"))));
        InternalFactHandle addr2 = ((InternalFactHandle) (session.insert(new Address("India"))));
        session.fireAllRules();
        Assert.assertEquals(9, results.size());
        Assert.assertEquals(stilton3.getObject(), results.get(4));
        Assert.assertEquals(bob4.getObject(), results.get(5));
        Assert.assertEquals(bob3.getObject(), results.get(6));
        Assert.assertEquals(addr2.getObject(), results.get(7));
        Assert.assertEquals(addr1.getObject(), results.get(8));
        // serialize session and rulebase
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        InternalFactHandle stilton4 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 50))));
        InternalFactHandle brie4 = ((InternalFactHandle) (session.insert(new Cheese("brie", 50))));
        InternalFactHandle bob5 = ((InternalFactHandle) (session.insert(new Person("bob", 50))));
        InternalFactHandle bob6 = ((InternalFactHandle) (session.insert(new Person("bob", 50))));
        InternalFactHandle addr3 = ((InternalFactHandle) (session.insert(new Address("Tripura"))));
        InternalFactHandle addr4 = ((InternalFactHandle) (session.insert(new Address("Agartala"))));
        session.fireAllRules();
        Assert.assertEquals(14, results.size());
        Assert.assertEquals(stilton4.getObject(), results.get(9));
        Assert.assertEquals(bob6.getObject(), results.get(10));
        Assert.assertEquals(bob5.getObject(), results.get(11));
        Assert.assertEquals(addr4.getObject(), results.get(12));
        Assert.assertEquals(addr3.getObject(), results.get(13));
        // serialize session and rulebase
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.dispose();
    }

    /* I have tried both the scenarios
    1. Remove a rule from a pkg.
    2. Remove a pkg

    But both cases after inserting associated data points (i.e data points which are used to fire/activate the removed rule)
    session.fireAllRules() is throwing NoClassDefFoundError
     */
    @Test
    public void testSerializeAddRemove_NoClassDefFoundError() throws Exception {
        // Create a rulebase, a session, and test it
        Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl");
        kpkgs = SerializationHelper.serializeObject(kpkgs);
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
        kBase.addPackages(kpkgs);
        kBase = SerializationHelper.serializeObject(kBase);
        List results = new ArrayList();
        KieSession session = kBase.newKieSession();
        session.setGlobal("results", results);
        InternalFactHandle stilton1 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 10))));
        InternalFactHandle brie1 = ((InternalFactHandle) (session.insert(new Cheese("brie", 10))));
        session.fireAllRules();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(stilton1.getObject(), results.get(0));
        // serialize session and rulebase
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        // dynamic add pkgs
        kpkgs = loadKnowledgePackages("../test_Dynamic3_0.drl");
        kBase.addPackages(SerializationHelper.serializeObject(kpkgs));
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        results.clear();
        InternalFactHandle stilton2 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 20))));
        InternalFactHandle brie2 = ((InternalFactHandle) (session.insert(new Cheese("brie", 20))));
        InternalFactHandle bob1 = ((InternalFactHandle) (session.insert(new Person("bob", 20))));
        InternalFactHandle bob2 = ((InternalFactHandle) (session.insert(new Person("bob", 30))));
        session.fireAllRules();
        Assert.assertEquals(3, results.size());
        Assert.assertEquals(stilton2.getObject(), results.get(0));
        Assert.assertEquals(bob2.getObject(), results.get(1));
        Assert.assertEquals(bob1.getObject(), results.get(2));
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        results.clear();
        // CASE 1: remove rule
        kBase.removeRule("org.drools.compiler.test", "like stilton");
        InternalFactHandle stilton3 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 20))));
        InternalFactHandle brie3 = ((InternalFactHandle) (session.insert(new Cheese("brie", 20))));
        InternalFactHandle bob3 = ((InternalFactHandle) (session.insert(new Person("bob", 20))));
        InternalFactHandle bob4 = ((InternalFactHandle) (session.insert(new Person("bob", 30))));
        session.fireAllRules();
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(bob4.getObject(), results.get(0));
        Assert.assertEquals(bob3.getObject(), results.get(1));
        // now recreate the rulebase, deserialize the session and test it
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        results.clear();
        // CASE 2: remove pkg
        kBase.removeKiePackage("org.drools.compiler.test");
        InternalFactHandle stilton4 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 20))));
        InternalFactHandle brie4 = ((InternalFactHandle) (session.insert(new Cheese("brie", 20))));
        InternalFactHandle bob5 = ((InternalFactHandle) (session.insert(new Person("bob", 20))));
        InternalFactHandle bob6 = ((InternalFactHandle) (session.insert(new Person("bob", 30))));
        session.fireAllRules();
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(bob6.getObject(), results.get(0));
        Assert.assertEquals(bob5.getObject(), results.get(1));
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        results.clear();
        session.setGlobal("results", results);
        InternalFactHandle stilton5 = ((InternalFactHandle) (session.insert(new Cheese("stilton", 30))));
        InternalFactHandle brie5 = ((InternalFactHandle) (session.insert(new Cheese("brie", 30))));
        InternalFactHandle bob7 = ((InternalFactHandle) (session.insert(new Person("bob", 30))));
        InternalFactHandle bob8 = ((InternalFactHandle) (session.insert(new Person("bob", 40))));
        session.fireAllRules();
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(bob8.getObject(), results.get(0));
        Assert.assertEquals(bob7.getObject(), results.get(1));
        kBase = ((InternalKnowledgeBase) (SerializationHelper.serializeObject(kBase)));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.dispose();
    }

    /* Testing the signature framework */
    @Test
    public void testSignedSerialization1() throws Exception {
        try {
            setPrivateKeyProperties();
            setPublicKeyProperties();
            // Compile a package, add it to kbase, serialize both
            Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl");
            kpkgs = SerializationHelper.serializeObject(kpkgs);
            InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
            kBase.addPackages(kpkgs);
            kBase = SerializationHelper.serializeObject(kBase);
        } finally {
            unsetPrivateKeyProperties();
            unsetPublicKeyProperties();
        }
    }

    /* Deserializing a signed package without the proper public key
     should fail.
     */
    @Test
    public void testSignedSerialization2() throws Exception {
        try {
            // set only the serialisation properties, but not the deserialization
            setPrivateKeyProperties();
            try {
                // Test package serialization/deserialization
                Collection<KiePackage> kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl");
                Assert.fail("Deserialisation should have failed.");
            } catch (Exception e) {
                // success
            }
        } finally {
            unsetPrivateKeyProperties();
        }
    }

    /* Deserializing a signed rulebase without the proper public key
     should fail.
     */
    @Test
    public void testSignedSerialization3() throws Exception {
        try {
            // set only the serialisation properties, but not the deserialization
            setPrivateKeyProperties();
            // create the kpkgs, but do not let them serialize
            Collection<KiePackage> kpkgs = loadKnowledgePackages(null, false, "../test_Dynamic1_0.drl");
            InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
            kBase.addPackages(kpkgs);
            try {
                kBase = SerializationHelper.serializeObject(kBase);
                Assert.fail("Deserialisation should have failed.");
            } catch (Exception e) {
                // success
            }
        } finally {
            unsetPrivateKeyProperties();
        }
    }

    /* A client environment configured to use signed serialization
     should refuse any non-signed serialized rulebase
     */
    @Test
    public void testSignedSerialization4() throws Exception {
        Collection<KiePackage> kpkgs = loadKnowledgePackages(null, false, "../test_Dynamic1_0.drl");
        kpkgs = SerializationHelper.serializeObject(kpkgs);
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (getKnowledgeBase()));
        kBase.addPackages(kpkgs);
        kBase = SerializationHelper.serializeObject(kBase);
        try {
            // set only the deserialisation properties, but not the serialization
            setPublicKeyProperties();
            kBase = SerializationHelper.serializeObject(kBase);
            Assert.fail("Should not deserialize an unsigned rulebase on an environment configured to work with signed rulebases.");
        } catch (Exception e) {
            // success
        } finally {
            unsetPublicKeyProperties();
        }
    }

    /**
     * In this case we are dealing with facts which are not on the systems classpath.
     */
    @Test
    public void testSerializabilityWithJarFacts() throws Exception {
        MapBackedClassLoader loader = new MapBackedClassLoader(this.getClass().getClassLoader());
        JarInputStream jis = new JarInputStream(this.getClass().getResourceAsStream("/billasurf.jar"));
        JarEntry entry = null;
        byte[] buf = new byte[1024];
        int len = 0;
        while ((entry = jis.getNextJarEntry()) != null) {
            if (!(entry.isDirectory())) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ((len = jis.read(buf)) >= 0) {
                    out.write(buf, 0, len);
                } 
                loader.addResource(entry.getName(), out.toByteArray());
            }
        } 
        String drl = "package foo.bar \n" + ((((("import com.billasurf.Board\n" + "rule \'MyGoodRule\' \n dialect \'mvel\' \n when ") + "   Board() ") + "then \n") + " System.err.println(42); \n") + "end\n");
        KnowledgeBuilderConfiguration kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, loader);
        Collection<KiePackage> kpkgs = loadKnowledgePackagesFromString(kbuilderConf, drl);
        kpkgs = SerializationHelper.serializeObject(kpkgs, loader);
    }

    @Test
    public void testEmptyRule() throws Exception {
        String rule = "package org.drools.compiler.test;\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "then\n";
        rule += "    list.add( \"fired\" );\n";
        rule += "end";
        KieBase kBase = loadKnowledgeBaseFromString(rule);
        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap(((InternalKnowledgeBase) (kBase)));
        Assert.assertEquals(2, nodes.size());
        Assert.assertEquals("InitialFactImpl", getClassType().getSimpleName());
        Assert.assertEquals("Rule 1", getRule().getName());
        KieSession session = kBase.newKieSession();
        List list = new ArrayList();
        session.setGlobal("list", list);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        Assert.assertEquals(1, ((List) (session.getGlobal("list"))).size());
        Assert.assertEquals("fired", ((List) (session.getGlobal("list"))).get(0));
    }

    @Test
    public void testDynamicEmptyRule() throws Exception {
        String rule1 = "package org.drools.compiler.test;\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "    list.add( \"fired1\" );\n";
        rule1 += "end";
        String rule2 = "package org.drools.compiler.test;\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "    list.add( \"fired2\" );\n";
        rule2 += "end";
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (loadKnowledgeBaseFromString(rule1)));
        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap(((InternalKnowledgeBase) (kBase)));
        // Make sure the rete node map is created correctly
        Assert.assertEquals(2, nodes.size());
        Assert.assertEquals("InitialFactImpl", getClassType().getSimpleName());
        Assert.assertEquals("Rule 1", getRule().getName());
        KieSession session = kBase.newKieSession();
        List list = new ArrayList();
        session.setGlobal("list", list);
        KieSession session1 = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, false);
        session1.fireAllRules();
        Assert.assertEquals(1, ((List) (session1.getGlobal("list"))).size());
        KieSession session2 = SerializationHelper.getSerialisedStatefulKnowledgeSession(session1, kBase, false);
        session.dispose();
        session1.dispose();
        Collection<KiePackage> kpkgs = loadKnowledgePackagesFromString(rule2);
        kBase.addPackages(kpkgs);
        session2.fireAllRules();
        System.out.println(session2.getGlobal("list"));
        Assert.assertEquals(2, ((List) (session2.getGlobal("list"))).size());
        Assert.assertEquals("fired1", ((List) (session2.getGlobal("list"))).get(0));
        Assert.assertEquals("fired2", ((List) (session2.getGlobal("list"))).get(1));
    }

    @Test
    public void testSinglePattern() throws Exception {
        String rule = "package org.drools.compiler.test;\n";
        rule += "import org.drools.compiler.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";
        KieBase kBase = loadKnowledgeBaseFromString(rule);
        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap(((InternalKnowledgeBase) (kBase)));
        Assert.assertEquals(3, nodes.size());
        Assert.assertEquals("Person", getClassType().getSimpleName());
        Assert.assertEquals("Rule 1", getRule().getName());
        KieSession session = kBase.newKieSession();
        List list = new ArrayList();
        session.setGlobal("list", list);
        Person p = new Person("bobba fet", 32);
        session.insert(p);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        Assert.assertEquals(1, ((List) (session.getGlobal("list"))).size());
        Assert.assertEquals(p, ((List) (session.getGlobal("list"))).get(0));
    }

    @Test
    public void testSingleRuleSingleJoinNodePattern() throws Exception {
        String rule = "package org.drools.compiler.test;\n";
        rule += "import org.drools.compiler.Person\n";
        rule += "import org.drools.compiler.Cheese\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "    $c : Cheese( ) \n";
        rule += "    $p : Person( cheese == $c ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";
        KieBase kBase = loadKnowledgeBaseFromString(rule);
        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap(((InternalKnowledgeBase) (kBase)));
        Assert.assertEquals(5, nodes.size());
        Assert.assertEquals("Cheese", getClassType().getSimpleName());
        Assert.assertEquals("Person", getClassType().getSimpleName());
        Assert.assertTrue("Should end with JoinNode", nodes.get(6).getClass().getSimpleName().endsWith("JoinNode"));
        Assert.assertEquals("Rule 1", getRule().getName());
        KieSession session = kBase.newKieSession();
        List list = new ArrayList();
        session.setGlobal("list", list);
        Cheese stilton = new Cheese("stilton", 25);
        Cheese brie = new Cheese("brie", 49);
        Person bobba = new Person("bobba fet", 32);
        bobba.setCheese(stilton);
        Person vadar = new Person("darth vadar", 32);
        session.insert(stilton);
        session.insert(bobba);
        session.insert(vadar);
        session.insert(brie);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        Assert.assertEquals(1, ((List) (session.getGlobal("list"))).size());
        Assert.assertEquals(bobba, ((List) (session.getGlobal("list"))).get(0));
        Person c3po = new Person("c3p0", 32);
        c3po.setCheese(stilton);
        session.insert(c3po);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        Assert.assertEquals(2, ((List) (session.getGlobal("list"))).size());
        Assert.assertEquals(c3po, ((List) (session.getGlobal("list"))).get(1));
        Person r2d2 = new Person("r2d2", 32);
        r2d2.setCheese(brie);
        session.insert(r2d2);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        Assert.assertEquals(3, ((List) (session.getGlobal("list"))).size());
        Assert.assertEquals(r2d2, ((List) (session.getGlobal("list"))).get(2));
    }

    @Test
    public void testMultiRuleMultiJoinNodePatternsWithHalt() throws Exception {
        String rule1 = "package org.drools.compiler.test;\n";
        rule1 += "import org.drools.compiler.Person\n";
        rule1 += "import org.drools.compiler.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "    $p : Person( cheese == $c ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( $p );\n";
        rule1 += "end";
        String rule2 = "package org.drools.compiler.test;\n";
        rule2 += "import org.drools.compiler.Person\n";
        rule2 += "import org.drools.compiler.Cheese\n";
        rule2 += "import org.drools.compiler.Cell\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "    $p : Person( cheese == $c ) \n";
        rule2 += "    $x : Cell( value == $p.age ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( $x );\n";
        rule2 += "end";
        String rule3 = "package org.drools.compiler.test;\n";
        rule3 += "import org.drools.compiler.FactA\n";
        rule3 += "import org.drools.compiler.FactB\n";
        rule3 += "import org.drools.compiler.FactC\n";
        rule3 += "import org.drools.compiler.Person\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "when\n";
        rule3 += "    $a : FactA( field2 > 10 ) \n";
        rule3 += "    $b : FactB( f2 >= $a.field2 ) \n";
        rule3 += "    $p : Person( name == \"darth vadar\" ) \n";
        rule3 += "    $c : FactC( f2 >= $b.f2 ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( $c );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";
        KieBase kBase = loadKnowledgeBaseFromString(rule1, rule2, rule3);
        KieSession session = kBase.newKieSession();
        List list = new ArrayList();
        session.setGlobal("list", list);
        Cheese stilton = new Cheese("stilton", 25);
        Cheese brie = new Cheese("brie", 49);
        Person bobba = new Person("bobba fet", 30);
        bobba.setCheese(stilton);
        Person vadar = new Person("darth vadar", 38);
        Person c3po = new Person("c3p0", 17);
        c3po.setCheese(stilton);
        Person r2d2 = new Person("r2d2", 58);
        r2d2.setCheese(brie);
        session.insert(stilton);
        session.insert(bobba);
        session.insert(vadar);
        session.insert(brie);
        session.insert(c3po);
        session.insert(r2d2);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        list = ((List) (session.getGlobal("list")));
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains(r2d2));
        Assert.assertTrue(list.contains(c3po));
        Assert.assertTrue(list.contains(bobba));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        Cell cell30 = new Cell(30);
        session.insert(cell30);
        Cell cell58 = new Cell(58);
        session.insert(cell58);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        Assert.assertEquals(5, list.size());
        Assert.assertTrue(list.contains(cell30));
        Assert.assertTrue(list.contains(cell58));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.insert(new FactA(15));
        session.insert(new FactB(20));
        FactC factC27 = new FactC(27);
        session.insert(factC27);
        FactC factC52 = new FactC(52);
        session.insert(factC52);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();
        session.fireAllRules();
        Assert.assertEquals(7, list.size());
        Assert.assertTrue(list.contains(factC52));
        Assert.assertTrue(list.contains(factC27));
    }

    @Test
    public void testNot() throws Exception {
        String header = "package org.drools.compiler.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.compiler.Person\n";
        header += "import org.drools.compiler.Cheese\n";
        header += "global java.util.List list;\n";
        String rule1 = "rule \"not rule test\"\n";
        rule1 += "salience 10\n";
        rule1 += "when\n";
        rule1 += "    Person()\n";
        rule1 += "    not Cheese( price >= 5 )\n";
        rule1 += "then\n";
        rule1 += "    list.add( new Integer( 5 ) );\n";
        rule1 += "end\n";
        KieBase kBase = loadKnowledgeBaseFromString((header + rule1));
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{ new org.drools.core.marshalling.impl.IdentityPlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT) });
        KieSession ksession = kBase.newKieSession(null, env);
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        // add a person, no cheese
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        Person bobba = new Person("bobba fet", 50);
        ksession.insert(bobba);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        // add another person, no cheese
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        Person darth = new Person("darth vadar", 200);
        ksession.insert(darth);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(2, list.size());
        // add cheese
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        Cheese stilton = new Cheese("stilton", 5);
        ksession.insert(stilton);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(2, list.size());
        // remove cheese
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.retract(ksession.getFactHandle(stilton));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(4, list.size());
        // put 2 cheeses back in
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.insert(stilton);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        Cheese brie = new Cheese("brie", 18);
        ksession.insert(brie);
        ksession.fireAllRules();
        Assert.assertEquals(4, list.size());
        // now remove a cheese, should be no change
        ksession.retract(ksession.getFactHandle(stilton));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(4, list.size());
        // now remove a person, should be no change
        ksession.retract(ksession.getFactHandle(bobba));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(4, list.size());
        // removal remaining cheese, should increase by one, as one person left
        ksession.retract(ksession.getFactHandle(brie));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(5, list.size());
    }

    @Test
    public void testExists() throws Exception {
        String header = "package org.drools.compiler.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.compiler.Person\n";
        header += "import org.drools.compiler.Cheese\n";
        header += "global java.util.List list;\n";
        String rule1 = "rule \"not rule test\"\n";
        rule1 += "salience 10\n";
        rule1 += "when\n";
        rule1 += "    Person()\n";
        rule1 += "    exists Cheese( price >= 5 )\n";
        rule1 += "then\n";
        rule1 += "    list.add( new Integer( 5 ) );\n";
        rule1 += "end\n";
        KieBase kBase = loadKnowledgeBaseFromString((header + rule1));
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{ new org.drools.core.marshalling.impl.IdentityPlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT) });
        KieSession ksession = kBase.newKieSession(null, env);
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        // add a person, no cheese
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        Person bobba = new Person("bobba fet", 50);
        ksession.insert(bobba);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(0, list.size());
        // add another person, no cheese
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        Person darth = new Person("darth vadar", 200);
        ksession.insert(darth);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(0, list.size());
        // add cheese
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        Cheese stilton = new Cheese("stilton", 5);
        ksession.insert(stilton);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(2, list.size());
        // remove cheese
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.retract(ksession.getFactHandle(stilton));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(2, list.size());
        // put 2 cheeses back in
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.insert(stilton);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        Cheese brie = new Cheese("brie", 18);
        ksession.insert(brie);
        ksession.fireAllRules();
        Assert.assertEquals(4, list.size());
        // now remove a cheese, should be no change
        ksession.retract(ksession.getFactHandle(stilton));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(4, list.size());
        // now remove a person, should be no change
        ksession.retract(ksession.getFactHandle(bobba));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(4, list.size());
        // removal remaining cheese, no
        ksession.retract(ksession.getFactHandle(brie));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(4, list.size());
        // put one cheese back in, with one person should increase by one
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.insert(stilton);
        ksession.fireAllRules();
        Assert.assertEquals(5, list.size());
    }

    @Test
    public void testTruthMaintenance() throws Exception {
        String header = "package org.drools.compiler.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.compiler.Person\n";
        header += "import org.drools.compiler.Cheese\n";
        header += "global Cheese cheese;\n";
        header += "global Person person;\n";
        header += "global java.util.List list;\n";
        String rule1 = "rule \"not person then cheese\"\n";
        rule1 += "when \n";
        rule1 += "    not Person() \n";
        rule1 += "then \n";
        rule1 += "    if (list.size() < 3) { \n";
        rule1 += "        list.add(new Integer(0)); \n";
        rule1 += "        insertLogical( cheese ); \n" + "    }\n";
        rule1 += "    drools.halt();\n" + "end\n";
        String rule2 = "rule \"if cheese then person\"\n";
        rule2 += "when\n";
        rule2 += "    Cheese()\n";
        rule2 += "then\n";
        rule2 += "    if (list.size() < 3) {\n";
        rule2 += "        list.add(new Integer(0));\n";
        rule2 += "        insertLogical( person );\n";
        rule2 += "    }\n" + "    drools.halt();\n";
        rule2 += "end\n";
        KieBase kBase = loadKnowledgeBaseFromString(((header + rule1) + rule2));
        KieSession ksession = kBase.newKieSession();
        final List list = new ArrayList();
        final Person person = new Person("person");
        final Cheese cheese = new Cheese("cheese", 0);
        ksession.setGlobal("cheese", cheese);
        ksession.setGlobal("person", person);
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(2, list.size());
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(3, list.size());
        // should not grow any further
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void testActivationGroups() throws Exception {
        String rule1 = "package org.drools.compiler.test;\n";
        rule1 += "import org.drools.compiler.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    activation-group \"activation-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";
        String rule2 = "package org.drools.compiler.test;\n";
        rule2 += "import org.drools.compiler.Cheese\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "    salience 10\n";
        rule2 += "    activation-group \"activation-group-1\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( \"rule2\" );\n";
        rule2 += "    drools.halt();\n";
        rule2 += "end";
        String rule3 = "package org.drools.compiler.test;\n";
        rule3 += "import org.drools.compiler.Cheese\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "    activation-group \"activation-group-1\"\n";
        rule3 += "when\n";
        rule3 += "    $c : Cheese( ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( \"rule3\" );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";
        String rule4 = "package org.drools.compiler.test;\n";
        rule4 += "import org.drools.compiler.Cheese\n";
        rule4 += "global java.util.List list\n";
        rule4 += "rule \"Rule 4\"\n";
        rule4 += "    activation-group \"activation-group-2\"\n";
        rule4 += "when\n";
        rule4 += "    $c : Cheese( ) \n";
        rule4 += "then\n";
        rule4 += "    list.add( \"rule4\" );\n";
        rule4 += "    drools.halt();\n";
        rule4 += "end";
        KieBase kBase = loadKnowledgeBaseFromString(rule1, rule2, rule3, rule4);
        KieSession ksession = kBase.newKieSession();
        kBase = SerializationHelper.serializeObject(kBase);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        final Cheese brie = new Cheese("brie", 12);
        ksession.insert(brie);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("rule2", list.get(0));
        Assert.assertEquals("rule4", list.get(1));
    }

    @Test
    public void testAgendaGroups() throws Exception {
        String rule1 = "package org.drools.compiler.test;\n";
        rule1 += "import org.drools.compiler.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    agenda-group \"agenda-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";
        String rule2 = "package org.drools.compiler.test;\n";
        rule2 += "import org.drools.compiler.Cheese\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "    salience 10\n";
        rule2 += "    agenda-group \"agenda-group-1\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( \"rule2\" );\n";
        rule2 += "    drools.halt();\n";
        rule2 += "end";
        String rule3 = "package org.drools.compiler.test;\n";
        rule3 += "import org.drools.compiler.Cheese\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "    salience 10\n";
        rule3 += "    agenda-group \"agenda-group-2\"\n";
        rule3 += "    activation-group \"activation-group-2\"\n";
        rule3 += "when\n";
        rule3 += "    $c : Cheese( ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( \"rule3\" );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";
        String rule4 = "package org.drools.compiler.test;\n";
        rule4 += "import org.drools.compiler.Cheese\n";
        rule4 += "global java.util.List list\n";
        rule4 += "rule \"Rule 4\"\n";
        rule4 += "    agenda-group \"agenda-group-2\"\n";
        rule4 += "    activation-group \"activation-group-2\"\n";
        rule4 += "when\n";
        rule4 += "    $c : Cheese( ) \n";
        rule4 += "then\n";
        rule4 += "    list.add( \"rule4\" );\n";
        rule4 += "    drools.halt();\n";
        rule4 += "end";
        KieBase kBase = loadKnowledgeBaseFromString(rule1, rule2, rule3, rule4);
        KieSession ksession = kBase.newKieSession();
        kBase = SerializationHelper.serializeObject(kBase);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        final Cheese brie = new Cheese("brie", 12);
        ksession.insert(brie);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.getAgenda().getAgendaGroup("agenda-group-1").setFocus();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals("rule2", list.get(0));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.getAgenda().getAgendaGroup("agenda-group-2").setFocus();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals("rule3", list.get(1));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals("rule1", list.get(2));
    }

    @Test
    public void testRuleFlowGroups() throws Exception {
        String rule1 = "package org.drools.compiler.test;\n";
        rule1 += "import org.drools.compiler.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    ruleflow-group \"ruleflow-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";
        String rule2 = "package org.drools.compiler.test;\n";
        rule2 += "import org.drools.compiler.Cheese\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "    salience 10\n";
        rule2 += "    ruleflow-group \"ruleflow-group-1\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( \"rule2\" );\n";
        rule2 += "    drools.halt();\n";
        rule2 += "end";
        String rule3 = "package org.drools.compiler.test;\n";
        rule3 += "import org.drools.compiler.Cheese\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "    salience 10\n";
        rule3 += "    ruleflow-group \"ruleflow-group-2\"\n";
        rule3 += "    activation-group \"activation-group-2\"\n";
        rule3 += "when\n";
        rule3 += "    $c : Cheese( ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( \"rule3\" );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";
        String rule4 = "package org.drools.compiler.test;\n";
        rule4 += "import org.drools.compiler.Cheese\n";
        rule4 += "global java.util.List list\n";
        rule4 += "rule \"Rule 4\"\n";
        rule4 += "    ruleflow-group \"ruleflow-group-2\"\n";
        rule4 += "    activation-group \"activation-group-2\"\n";
        rule4 += "when\n";
        rule4 += "    $c : Cheese( ) \n";
        rule4 += "then\n";
        rule4 += "    list.add( \"rule4\" );\n";
        rule4 += "    drools.halt();\n";
        rule4 += "end";
        KieBase kbase = loadKnowledgeBaseFromString(rule1, rule2, rule3, rule4);
        KieSession ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(kbase.newKieSession(), true);
        kbase = SerializationHelper.serializeObject(kbase);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        final Cheese brie = new Cheese("brie", 12);
        ksession.insert(brie);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        activateRuleFlowGroup("ruleflow-group-1");
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals("rule2", list.get(0));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        activateRuleFlowGroup("ruleflow-group-2");
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals("rule3", list.get(1));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals("rule1", list.get(2));
    }

    @Test
    public void testAccumulate() throws Exception {
        String rule = "package org.drools\n" + ((((((("import org.drools.compiler.Message\n" + "global java.util.List results\n") + "rule MyRule\n") + "  when\n") + "    $n : Number( intValue >= 2 ) from accumulate ( m: Message( ), count( m ) )\n") + "  then\n") + "    results.add($n);\n") + "end");
        KieBase kBase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(kBase.newKieSession(), true);
        kBase = SerializationHelper.serializeObject(kBase);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.setGlobal("results", new ArrayList());
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.insert(new Message());
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        List results = ((List) (ksession.getGlobal("results")));
        ksession.insert(new Message());
        ksession.insert(new Message());
        ksession.fireAllRules();
        Assert.assertEquals(3, ((Number) (results.get(0))).intValue());
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.insert(new Message());
        ksession.insert(new Message());
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        Assert.assertEquals(1, agendaSize());
        ksession.fireAllRules();
        Assert.assertEquals(5, ((Number) (results.get(1))).intValue());
    }

    @Test
    public void testAccumulate2() throws Exception {
        String str = "package org.drools\n" + (((((((("\n" + "import org.drools.compiler.Message\n") + "\n") + "rule MyRule\n") + "  when\n") + "    Number( intValue >= 5 ) from accumulate ( m: Message( ), count( m ) )\n") + "  then\n") + "    System.out.println(\"Found messages\");\n") + "end\n");
        KieBase kBase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kBase.newKieSession();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.insert(new Message());
        ksession.insert(new Message());
        ksession.insert(new Message());
        ksession.insert(new Message());
        ksession.insert(new Message());
        flushPropagations();
        Assert.assertEquals(1, agendaSize());
    }

    @Test
    public void testAccumulateSessionSerialization() throws Exception {
        KieBase kbase = loadKnowledgeBase("../test_AccumulateSerialization.drl");
        KieSession ksession = kbase.newKieSession();
        final List<Number> results = new ArrayList<Number>();
        ksession.setGlobal("results", results);
        ksession.insert(new Cheese("stilton", 10));
        ksession.insert(new Cheese("brie", 5));
        ksession.insert(new Cheese("provolone", 150));
        ksession.insert(new Cheese("brie", 20));
        ksession.insert(new Person("Bob", "brie"));
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(25, results.get(0).intValue());
    }

    @Test
    public void testAccumulateSerialization() throws Exception {
        KieBase kbase = loadKnowledgeBase("org/drools/compiler/integrationtests/marshalling/test_SerializableAccumulate.drl");
        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal("results", new ArrayList());
        Cheese t1 = new Cheese("brie", 10);
        Cheese t2 = new Cheese("brie", 15);
        Cheese t3 = new Cheese("stilton", 20);
        Cheese t4 = new Cheese("brie", 30);
        ksession.insert(t1);
        ksession.insert(t2);
        ksession.insert(t3);
        ksession.insert(t4);
        // ksession.fireAllRules();
        Marshaller marshaller = createSerializableMarshaller(kbase);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new DroolsObjectOutputStream(baos);
        out.writeObject(kbase);
        marshaller.marshall(out, ksession);
        out.flush();
        out.close();
        ObjectInputStream in = new DroolsObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        kbase = ((InternalKnowledgeBase) (in.readObject()));
        marshaller = createSerializableMarshaller(kbase);
        ksession = ((StatefulKnowledgeSession) (marshaller.unmarshall(in)));
        in.close();
        // setting the global again, since it is not serialized with the session
        List<List> results = ((List<List>) (new ArrayList<List>()));
        ksession.setGlobal("results", results);
        Assert.assertNotNull(results);
        ksession.fireAllRules();
        ksession.dispose();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(3, results.get(0).size());
    }

    @Test
    public void testMarshallWithNot() throws Exception {
        String str = ((((((((((((((("import " + (getClass().getCanonicalName())) + ".*\n") + "rule one\n") + "when\n") + "   A()\n") + "   not(B())\n") + "then\n") + "System.out.println(\"a\");\n") + "end\n") + "\n") + "rule two\n") + "when\n") + "   A()\n") + "then\n") + "System.out.println(\"b\");\n") + "end\n";
        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(STREAM);
        KieBase kBase = loadKnowledgeBaseFromString(config, str);
        KieSession ksession = kBase.newKieSession();
        ksession.insert(new MarshallingTest.A());
        MarshallerFactory.newMarshaller(kBase).marshall(new ByteArrayOutputStream(), ksession);
    }

    @Test
    public void testMarshallEvents() throws Exception {
        String str = (((((((((((((((("import " + (getClass().getCanonicalName())) + ".*\n") + "declare A\n") + " @role( event )\n") + " @expires( 10m )\n") + "end\n") + "declare B\n") + " @role( event )\n") + " @expires( 10m )\n") + "end\n") + "rule one\n") + "when\n") + "   $a : A()\n") + "   B(this after $a)\n") + "then\n") + "insert(new C());") + "end\n";
        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(STREAM);
        KieBase kBase = loadKnowledgeBaseFromString(config, str);
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption(ClockTypeOption.get("pseudo"));
        ksconf.setOption(TimerJobFactoryOption.get("trackable"));
        KieSession ksession = kBase.newKieSession(ksconf, null);
        ksession.insert(new MarshallingTest.A());
        ksession = marsallStatefulKnowledgeSession(ksession);
        ksession.insert(new MarshallingTest.B());
        ksession = marsallStatefulKnowledgeSession(ksession);
        ksession.fireAllRules();
        Assert.assertEquals(2, ksession.getObjects().size());
    }

    public static class A implements Serializable {
        @Override
        public String toString() {
            return "A[]";
        }
    }

    public static class B implements Serializable {
        @Override
        public String toString() {
            return "B[]";
        }
    }

    public static class C implements Serializable {
        @Override
        public String toString() {
            return "C[]";
        }
    }

    @Test
    public void testMarshallEntryPointsWithExpires() throws Exception {
        String str = (((((((((((((((((((((((((((((((((((((("package org.domain.test \n" + "import ") + (getClass().getCanonicalName())) + ".*\n") + "global java.util.List list\n") + "declare A\n") + " @role( event )\n") + " @expires( 10s )\n") + "end\n") + "declare B\n") + "") + " @role( event )\n") + " @expires( 10s )\n") + "end\n") + "") + "declare C\n") + " @role( event )\n") + " @expires( 15s )\n") + "end\n") + "") + "rule a1\n") + "when\n") + "   $a : A() from entry-point \'a-ep\'\n") + "then\n") + "list.add( $a );") + "end\n") + "") + "rule b1\n") + "when\n") + "   $b : B() from entry-point \'b-ep\'\n") + "then\n") + "list.add( $b );") + "end\n") + "") + "rule c1\n") + "when\n") + "   $c : C() from entry-point \'c-ep\'\n") + "then\n") + "list.add( $c );") + "end\n";
        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(STREAM);
        KieBase kBase = loadKnowledgeBaseFromString(config, str);
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption(ClockTypeOption.get("pseudo"));
        ksconf.setOption(TimerJobFactoryOption.get("trackable"));
        KieSession ksession = kBase.newKieSession(ksconf, null);
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        EntryPoint aep = ksession.getEntryPoint("a-ep");
        aep.insert(new MarshallingTest.A());
        ksession = marsallStatefulKnowledgeSession(ksession);
        EntryPoint bep = ksession.getEntryPoint("b-ep");
        bep.insert(new MarshallingTest.B());
        ksession = marsallStatefulKnowledgeSession(ksession);
        EntryPoint cep = ksession.getEntryPoint("c-ep");
        cep.insert(new MarshallingTest.C());
        ksession = marsallStatefulKnowledgeSession(ksession);
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession(ksession);
        Assert.assertEquals(3, list.size());
        aep = ksession.getEntryPoint("a-ep");
        Assert.assertEquals(1, aep.getFactHandles().size());
        bep = ksession.getEntryPoint("b-ep");
        Assert.assertEquals(1, bep.getFactHandles().size());
        cep = ksession.getEntryPoint("c-ep");
        Assert.assertEquals(1, cep.getFactHandles().size());
        PseudoClockScheduler timeService = ((PseudoClockScheduler) (ksession.<SessionClock>getSessionClock()));
        timeService.advanceTime(11, TimeUnit.SECONDS);
        ksession = marsallStatefulKnowledgeSession(ksession);
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession(ksession);
        aep = ksession.getEntryPoint("a-ep");
        Assert.assertEquals(0, aep.getFactHandles().size());
        bep = ksession.getEntryPoint("b-ep");
        Assert.assertEquals(0, bep.getFactHandles().size());
        cep = ksession.getEntryPoint("c-ep");
        Assert.assertEquals(1, cep.getFactHandles().size());
    }

    @Test
    public void testMarshallEntryPointsWithNot() throws Exception {
        String str = (((((((((((((((((((("package org.domain.test \n" + "import ") + (getClass().getCanonicalName())) + ".*\n") + "global java.util.List list\n") + "declare A\n") + " @role( event )\n") + " @expires( 10m )\n") + "end\n") + "declare B\n") + "") + " @role( event )\n") + " @expires( 10m )\n") + "end\n") + "") + "rule a1\n") + "when\n") + "   $a : A() from entry-point \'a-ep\'\n") + "   not B( this after[0s, 10s] $a) from entry-point \'a-ep\'\n") + "then\n") + "list.add( $a );") + "end\n";
        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(STREAM);
        KieBase kBase = loadKnowledgeBaseFromString(config, str);
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption(ClockTypeOption.get("pseudo"));
        ksconf.setOption(TimerJobFactoryOption.get("trackable"));
        KieSession ksession = kBase.newKieSession(ksconf, null);
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        EntryPoint aep = ksession.getEntryPoint("a-ep");
        aep.insert(new MarshallingTest.A());
        ksession = marsallStatefulKnowledgeSession(ksession);
        PseudoClockScheduler timeService = ((PseudoClockScheduler) (ksession.<SessionClock>getSessionClock()));
        timeService.advanceTime(3, TimeUnit.SECONDS);
        ksession = marsallStatefulKnowledgeSession(ksession);
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession(ksession);
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testMarshallEntryPointsWithSlidingLengthWindow() throws Exception {
        String str = (((((((((((((((((((("package org.domain.test \n" + "import ") + (getClass().getCanonicalName())) + ".*\n") + "import java.util.List\n") + "global java.util.List list\n") + "declare A\n") + " @role( event )\n") + " @expires( 10m )\n") + "end\n") + "declare B\n") + "") + " @role( event )\n") + " @expires( 10m )\n") + "end\n") + "") + "rule a1\n") + "when\n") + "   $l : List() from collect( A()  over window:length(3) from entry-point \'a-ep\') \n") + "then\n") + "   list.add( $l );") + "end\n";
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption(STREAM);
        final KieBase kbase = loadKnowledgeBaseFromString(conf, str);
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption(ClockTypeOption.get("pseudo"));
        ksconf.setOption(TimerJobFactoryOption.get("trackable"));
        KieSession ksession = createKnowledgeSession(kbase, ksconf);
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        EntryPoint aep = ksession.getEntryPoint("a-ep");
        aep.insert(new MarshallingTest.A());
        ksession = marsallStatefulKnowledgeSession(ksession);
        aep = ksession.getEntryPoint("a-ep");
        aep.insert(new MarshallingTest.A());
        ksession = marsallStatefulKnowledgeSession(ksession);
        list.clear();
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession(ksession);
        Assert.assertEquals(2, ((List) (list.get(0))).size());
        aep = ksession.getEntryPoint("a-ep");
        aep.insert(new MarshallingTest.A());
        ksession = marsallStatefulKnowledgeSession(ksession);
        aep = ksession.getEntryPoint("a-ep");
        aep.insert(new MarshallingTest.A());
        ksession = marsallStatefulKnowledgeSession(ksession);
        list.clear();
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession(ksession);
        Assert.assertEquals(3, ((List) (list.get(0))).size());
    }

    @Test
    public void testMarshalWithProtoBuf() throws Exception {
        KieBase kbase = loadKnowledgeBase("../test_Serializable.drl");
        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal("list", new ArrayList());
        final Person bob = new Person("bob");
        ksession.insert(bob);
        ksession = marsallStatefulKnowledgeSession(ksession);
        Assert.assertEquals(1, ksession.getFactCount());
        Assert.assertEquals(bob, ksession.getObjects().iterator().next());
        int fired = ksession.fireAllRules();
        Assert.assertEquals(3, fired);
        List list = ((List) (ksession.getGlobal("list")));
        Assert.assertEquals(3, list.size());
        // because of agenda-groups
        Assert.assertEquals(new Integer(4), list.get(0));
        Collection<? extends Object> facts = ksession.getObjects();
        System.out.println(new ArrayList(facts));
        Assert.assertEquals(2, facts.size());
    }

    @Test
    public void testMarshallWithCollects() throws Exception {
        // BZ-1193600
        String str = "import java.util.Collection\n" + ((((("rule R1 when\n" + "    Collection(empty==false) from collect( Integer() )\n") + "    Collection() from collect( String() )\n") + "then\n") + "end\n") + "rule R2 when then end\n");
        KieBase kbase = new KieHelper().addContent(str, DRL).build();
        KieSession ksession = kbase.newKieSession();
        try {
            Marshaller marshaller = MarshallerFactory.newMarshaller(kbase);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshall(baos, ksession);
            marshaller = MarshallerFactory.newMarshaller(kbase);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            baos.close();
            ksession = marshaller.unmarshall(bais);
            bais.close();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("unexpected exception :" + (e.getMessage())));
        }
    }

    @Test
    public void testMarshallWithTimedRule() {
        // DROOLS-795
        String drl = "rule \"Rule A Timeout\"\n" + ((((((((((((("when\n" + "    String( this == \"ATrigger\" )\n") + "then\n") + "   insert (new String( \"A-Timer\") );\n") + "end\n") + "\n") + "rule \"Timer For rule A Timeout\"\n") + "    timer ( int: 5s )\n") + "when\n") + "   String( this == \"A-Timer\")\n") + "then\n") + "   delete ( \"A-Timer\" );\n") + "   delete ( \"ATrigger\" );\n") + "end\n");
        KieBase kbase = new KieHelper().addContent(drl, DRL).build(EQUALITY, ENABLED, STREAM);
        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption(ClockTypeOption.get("pseudo"));
        KieSession ksession = kbase.newKieSession(sessionConfig, null);
        ksession.insert("ATrigger");
        Assert.assertEquals(1, ksession.getFactCount());
        ksession.fireAllRules();
        Assert.assertEquals(2, ksession.getFactCount());
        SessionPseudoClock clock = ksession.getSessionClock();
        clock.advanceTime(4, TimeUnit.SECONDS);
        Assert.assertEquals(2, ksession.getFactCount());
        ksession.fireAllRules();
        Assert.assertEquals(2, ksession.getFactCount());
        ksession = MarshallingTest.marshallAndUnmarshall(kbase, ksession, sessionConfig);
        clock = ksession.getSessionClock();
        clock.advanceTime(4, TimeUnit.SECONDS);
        Assert.assertEquals(2, ksession.getFactCount());
        ksession.fireAllRules();
        Assert.assertEquals(0, ksession.getFactCount());
    }

    @Test
    public void testSnapshotRecoveryScheduledRulesPlain() throws Exception {
        // DROOLS-1537
        String drl = "package com.drools.restore.reproducer\n" + ((((((((((((((((("global java.util.List list;\n" + "global java.util.List list2;\n") + "rule R1\n") + " timer (int: 20s)\n") + " when\n") + "   $m : String( this == \"Hello World1\" )\n") + " then\n") + "   list.add( $m );\n") + "   retract( $m );\n") + "end\n") + "rule R2\n") + " timer (int: 30s)\n") + " when\n") + "   $m : String( this == \"Hello World2\" )\n") + " then\n") + "   list2.add( $m );\n") + "   retract( $m );\n") + "end\n");
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption(ClockTypeOption.get(PSEUDO_CLOCK.getId()));
        ksconf.setOption(YES);
        ksconf.setOption(TimerJobFactoryOption.get("trackable"));
        ksconf.setOption(ClockTypeOption.get("pseudo"));
        KieBase kbase1 = new KieHelper().addContent(drl, DRL).build(STREAM);
        KieSession ksession = kbase1.newKieSession(ksconf, null);
        PseudoClockScheduler timeService = ((PseudoClockScheduler) (ksession.<SessionClock>getSessionClock()));
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        List list2 = new ArrayList();
        ksession.setGlobal("list2", list2);
        ksession.insert("Hello World1");
        ksession.insert("Hello World2");
        ksession.fireAllRules();
        timeService.advanceTime(10500, TimeUnit.MILLISECONDS);
        KieBase kbase2 = new KieHelper().addContent(drl, DRL).build(STREAM);
        ksession = MarshallingTest.marshallAndUnmarshall(kbase1, kbase2, ksession, ksconf);
        ksession.setGlobal("list", list);
        ksession.setGlobal("list2", list2);
        PseudoClockScheduler timeService2 = ((PseudoClockScheduler) (ksession.<SessionClock>getSessionClock()));
        ksession.fireAllRules();
        long accumulatedSleepTime = 0;
        for (int i = 0; i < 6; i++) {
            timeService2.advanceTime(5050, TimeUnit.MILLISECONDS);
            accumulatedSleepTime += 5050;
            Assert.assertEquals((i < 1 ? 0 : 1), list.size());
            Assert.assertEquals((i < 3 ? 0 : 1), list2.size());
        }
    }
}

