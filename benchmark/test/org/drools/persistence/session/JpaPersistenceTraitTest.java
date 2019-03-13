/**
 * Copyright 2011 Red Hat Inc.
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
package org.drools.persistence.session;


import ResourceType.DRL;
import Variable.v;
import VirtualPropertyMode.MAP;
import VirtualPropertyMode.TRIPLES;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;


@RunWith(Parameterized.class)
public class JpaPersistenceTraitTest {
    private Map<String, Object> context;

    private Environment env;

    private boolean locking;

    public JpaPersistenceTraitTest(String locking) {
        this.locking = DroolsPersistenceUtil.PESSIMISTIC_LOCKING.equals(locking);
    }

    @Test
    public void testTripleBasedTraitsWithJPA() {
        String str = "package org.drools.trait.test; \n" + ((((((((((((((((((((((((((((((((((((((((((((((("global java.util.List list; \n" + "") + "declare TBean \n") + "  @propertyReactive \n") + "  @Traitable \n") + "  fld : String \n") + "end \n ") + "") + "declare trait Mask \n") + "  @propertyReactive \n") + "  fld : String \n") + "  xyz : int  \n") + "end \n") + "\n ") + "declare trait Cloak \n") + "  @propertyReactive \n") + "  fld : String \n") + "  ijk : String  \n") + "end \n") + "") + "rule Init \n") + "when \n") + "then \n") + "  insert( new TBean(\"abc\") ); \n") + "end \n") + "") + "rule Don \n") + "no-loop \n") + "when \n") + "  $b : TBean( ) \n") + "then \n") + "  Mask m = don( $b, Mask.class ); \n") + "  modify (m) { setXyz( 10 ); } \n") + "  list.add( m ); \n") + "  System.out.println( \"Don result : \" + m ); \n ") + "end \n") + "\n") + "rule Don2 \n") + "no-loop \n") + "when \n") + "  $b : TBean( ) \n") + "then \n") + "  Cloak c = don( $b, Cloak.class ); \n") + "  modify (c) { setIjk( \"ijklmn\" ); } \n") + "  list.add( c ); \n") + "  System.out.println( \"Don result : \" + c ); \n ") + "end \n") + "");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        if (kbuilder.hasErrors()) {
            Assert.fail(kbuilder.getErrors().toString());
        }
        kbase.addPackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        // StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<?> list = new ArrayList<Object>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        Assert.assertEquals(2, list.size());
        long id = ksession.getIdentifier();
        StatefulKnowledgeSession ksession2 = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        // StatefulKnowledgeSession ksession2 = kbase.newStatefulKnowledgeSession();
        ksession2.fireAllRules();
        Collection x = ksession2.getObjects();
        Assert.assertEquals(3, x.size());
        TraitableBean core = null;
        for (Object o : x) {
            if (o instanceof TraitableBean) {
                core = ((TraitableBean) (o));
                break;
            }
        }
        Assert.assertNotNull(core);
        Assert.assertEquals(2, core._getDynamicProperties().size());
        Assert.assertNotNull(core.getTrait("org.drools.trait.test.Mask"));
        Assert.assertNotNull(core.getTrait("org.drools.trait.test.Cloak"));
    }

    @Test
    public void testMapBasedTraitsWithJPA() {
        String str = "package org.drools.trait.test; \n" + ((((((((((((((((((((((((((((((((((((((((((((((("global java.util.List list; \n" + "") + "declare TBean2 \n") + "  @propertyReactive \n") + "  @Traitable \n") + "  fld : String \n") + "end \n ") + "") + "declare trait Mask2 \n") + "  @propertyReactive \n") + "  fld : String \n") + "  xyz : int  \n") + "end \n") + "\n ") + "declare trait Cloak2 \n") + "  @propertyReactive \n") + "  fld : String \n") + "  ijk : String  \n") + "end \n") + "") + "rule Init \n") + "when \n") + "then \n") + "  insert( new TBean2(\"abc\") ); \n") + "end \n") + "") + "rule Don \n") + "no-loop \n") + "when \n") + "  $b : TBean2( ) \n") + "then \n") + "  Mask2 m = don( $b, Mask2.class ); \n") + "  modify (m) { setXyz( 10 ); } \n") + "  list.add( m ); \n") + "  System.out.println( \"Don result : \" + m ); \n ") + "end \n") + "\n") + "rule Don2 \n") + "no-loop \n") + "when \n") + "  $b : TBean2( ) \n") + "then \n") + "  Cloak2 c = don( $b, Cloak2.class ); \n") + "  modify (c) { setIjk( \"ijklmn\" ); } \n") + "  list.add( c ); \n") + "  System.out.println( \"Don result : \" + c ); \n ") + "end \n") + "");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode(MAP, kbase);
        if (kbuilder.hasErrors()) {
            Assert.fail(kbuilder.getErrors().toString());
        }
        kbase.addPackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        List<?> list = new ArrayList<Object>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        Assert.assertEquals(2, list.size());
        long id = ksession.getIdentifier();
        StatefulKnowledgeSession ksession2 = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession2.fireAllRules();
        Collection x = ksession2.getObjects();
        Assert.assertEquals(3, x.size());
        TraitableBean core = null;
        for (Object o : x) {
            if (o instanceof TraitableBean) {
                core = ((TraitableBean) (o));
                break;
            }
        }
        Assert.assertNotNull(core);
        Assert.assertEquals(2, core._getDynamicProperties().size());
        Assert.assertNotNull(core.getTrait("org.drools.trait.test.Mask2"));
        Assert.assertNotNull(core.getTrait("org.drools.trait.test.Cloak2"));
    }

    @Test
    public void testTraitsOnLegacyJPATriple() {
        traitsLegacyWrapperWithJPA(TRIPLES);
    }

    @Test
    public void testTraitsOnLegacyJPAMap() {
        traitsLegacyWrapperWithJPA(MAP);
    }

    @Test
    public void testTraitWithJPAOnFreshKieBase() {
        // DROOLS-904
        String str = "package org.drools.trait.test; " + ((((((((((((("global java.util.List list; " + "declare TBean2  ") + "  @propertyReactive  ") + "  @Traitable  ") + "end   ") + "declare trait Mask ") + "  @propertyReactive  ") + "end  ") + "query getTraits( Mask $m ) ") + "  $m := Mask() ") + "end ") + "rule Init when then don( new TBean2(), Mask.class ); end ") + "rule Trig when String() then don( new TBean2(), Mask.class ); end ") + "rule Main when $m : Mask() then list.add( $m ); end ");
        List list = new ArrayList();
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        if (kbuilder.hasErrors()) {
            Assert.fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        long id = ksession.getIdentifier();
        InternalKnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode(MAP, kbase);
        kbase2.addPackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession2 = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase2, null, env);
        ksession.setGlobal("list", list);
        ksession2.insert("go");
        ksession2.fireAllRules();
        Assert.assertEquals(2, list.size());
        Class<?> oldProxyClass = list.get(0).getClass();
        Class<?> newProxyClass = list.get(1).getClass();
        Assert.assertNotSame(oldProxyClass, newProxyClass);
        QueryResults qry = ksession2.getQueryResults("getTraits", v);
        Assert.assertEquals(2, qry.size());
        Iterator<QueryResultsRow> iter = qry.iterator();
        int j = 0;
        while (iter.hasNext()) {
            QueryResultsRow row = iter.next();
            Object entry = row.get("$m");
            Assert.assertNotNull(entry);
            Assert.assertSame(newProxyClass, entry.getClass());
            j++;
        } 
        Assert.assertEquals(2, j);
        for (Object o : ksession2.getObjects()) {
            if (o.getClass().getName().contains("Mask")) {
                Assert.assertSame(newProxyClass, o.getClass());
            }
        }
    }
}

