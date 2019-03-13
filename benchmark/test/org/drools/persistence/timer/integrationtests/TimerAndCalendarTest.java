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
package org.drools.persistence.timer.integrationtests;


import EventProcessingOption.STREAM;
import ResourceType.DRL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;


@RunWith(Parameterized.class)
public class TimerAndCalendarTest {
    private Map<String, Object> context;

    private boolean locking;

    public TimerAndCalendarTest(String locking) {
        this.locking = DroolsPersistenceUtil.PESSIMISTIC_LOCKING.equals(locking);
    }

    @Test
    public void testEventExpires() throws Exception {
        String timerRule = "package org.drools.test\n" + ((((((((("declare TestEvent \n" + "    @role( event )\n") + "    @expires( 10s )\n") + "end\n") + "") + "rule TimerRule \n") + "    when \n") + "        TestEvent( ) from entry-point \"Test\"\n") + "    then \n") + "end");
        KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption(STREAM);
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbconf);
        Resource resource = ResourceFactory.newByteArrayResource(timerRule.getBytes());
        Collection<KiePackage> kpackages = buildKnowledgePackage(resource, DRL);
        kbase.addPackages(kpackages);
        KieSession ksession = createSession(kbase);
        FactType type = kbase.getFactType("org.drools.test", "TestEvent");
        Assert.assertNotNull("could not get type", type);
        ksession = disposeAndReloadSession(ksession, kbase);
        ksession.getEntryPoint("Test").insert(type.newInstance());
        ksession.fireAllRules();
        ksession = disposeAndReloadSession(ksession, kbase);
        ksession = disposeAndReloadSession(ksession, kbase);
    }

    @Test
    public void testTimerWithRemovingRule() throws Exception {
        // DROOLS-576
        // Only reproducible with RETEOO
        InternalKnowledgeBase kbase1 = KnowledgeBaseFactory.newKnowledgeBase();
        String str1 = "package org.test; " + (((((((("import java.util.*; " + "global java.util.List list; ") + "rule R1\n") + "    timer ( int: 5s )\n") + "when\n") + "    $s : String( )\n") + "then\n") + "    list.add( $s );\n") + "end\n");
        Resource resource1 = ResourceFactory.newByteArrayResource(str1.getBytes());
        Collection<KiePackage> kpackages1 = buildKnowledgePackage(resource1, DRL);
        kbase1.addPackages(kpackages1);
        StatefulKnowledgeSession ksession1 = JPAKnowledgeService.newStatefulKnowledgeSession(kbase1, null, DroolsPersistenceUtil.createEnvironment(context));
        long ksessionId = ksession1.getIdentifier();
        ArrayList<String> list = new ArrayList<String>();
        ksession1.setGlobal("list", list);
        ksession1.insert("hello");
        ksession1.fireAllRules();
        ksession1.dispose();// dispose before firing

        Assert.assertEquals(0, list.size());
        Thread.sleep(5000);
        // A new kbase without the timer's activated rule
        InternalKnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase();
        String str2 = "package org.test; " + ((((((("import java.util.*; " + "global java.util.List list; ") + "rule R2\n") + "when\n") + "    $s : Integer( )\n") + "then\n") + "    list.add( $s );\n") + "end\n");
        Resource resource2 = ResourceFactory.newByteArrayResource(str2.getBytes());
        Collection<KiePackage> kpackages2 = buildKnowledgePackage(resource2, DRL);
        kbase2.addPackages(kpackages2);
        StatefulKnowledgeSession ksession2 = JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase2, null, DroolsPersistenceUtil.createEnvironment(context));
        ksession2.setGlobal("list", list);
        ksession2.fireAllRules();
        ksession2.dispose();
        Assert.assertEquals(0, list.size());
    }
}

