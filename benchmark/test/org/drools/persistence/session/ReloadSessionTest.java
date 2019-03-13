/**
 * Copyright 2011 Red Hat Inc
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


import EnvironmentName.PERSISTENCE_CONTEXT_MANAGER;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.drools.persistence.api.PersistenceContextManager;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;


@RunWith(Parameterized.class)
public class ReloadSessionTest {
    // Datasource (setup & clean up)
    private Map<String, Object> context;

    private EntityManagerFactory emf;

    private boolean locking;

    private static final String ENTRY_POINT = "ep1";

    private static String simpleRule = "package org.kie.test\n" + ((((((("global java.util.List list\n" + "rule rule1\n") + "when\n") + "  Integer(intValue > 0)\n") + "then\n") + "  list.add( 1 );\n") + "end\n") + "\n");

    private static final String RULE_WITH_EP = (((((("package org.kie.test\n" + ((("global java.util.List list\n" + "rule rule1\n") + "when\n") + "  Integer(intValue > 0) from entry-point ")) + (ReloadSessionTest.ENTRY_POINT)) + " \n") + "then\n") + "  list.add( 1 );\n") + "end\n") + "\n";

    public ReloadSessionTest(String locking) {
        this.locking = DroolsPersistenceUtil.PESSIMISTIC_LOCKING.equals(locking);
    }

    @Test
    public void reloadKnowledgeSessionTest() {
        // Initialize drools environment stuff
        Environment env = createEnvironment();
        KieBase kbase = initializeKnowledgeBase(ReloadSessionTest.simpleRule);
        StatefulKnowledgeSession commandKSession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        Assert.assertTrue("There should be NO facts present in a new (empty) knowledge session.", commandKSession.getFactHandles().isEmpty());
        // Persist a facthandle to the database
        Integer integerFact = (new Random().nextInt(((Integer.MAX_VALUE) - 1))) + 1;
        commandKSession.insert(integerFact);
        // At this point in the code, the fact has been persisted to the database
        // (within a transaction via the PersistableRunner)
        Collection<FactHandle> factHandles = commandKSession.getFactHandles();
        Assert.assertTrue("At least one fact should have been inserted by the ksession.insert() method above.", (!(factHandles.isEmpty())));
        FactHandle origFactHandle = factHandles.iterator().next();
        Assert.assertTrue("The stored fact should contain the same number as the value inserted (but does not).", ((Integer.parseInt(getObject().toString())) == (integerFact.intValue())));
        // Save the sessionInfo id in order to retrieve it later
        long sessionInfoId = commandKSession.getIdentifier();
        // Clean up the session, environment, etc.
        PersistenceContextManager pcm = ((PersistenceContextManager) (commandKSession.getEnvironment().get(PERSISTENCE_CONTEXT_MANAGER)));
        commandKSession.dispose();
        pcm.dispose();
        emf.close();
        // Reload session from the database
        emf = Persistence.createEntityManagerFactory(DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME);
        context.put(ENTITY_MANAGER_FACTORY, emf);
        env = createEnvironment();
        // Re-initialize the knowledge session:
        StatefulKnowledgeSession newCommandKSession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionInfoId, kbase, null, env);
        // Test that the session has been successfully reinitialized
        factHandles = newCommandKSession.getFactHandles();
        Assert.assertTrue("At least one fact should have been persisted by the ksession.insert above.", ((!(factHandles.isEmpty())) && ((factHandles.size()) == 1)));
        FactHandle retrievedFactHandle = factHandles.iterator().next();
        Assert.assertTrue("If the retrieved and original FactHandle object are the same, then the knowledge session has NOT been reloaded!", (origFactHandle != retrievedFactHandle));
        Assert.assertTrue("The retrieved fact should contain the same info as the original (but does not).", ((Integer.parseInt(getObject().toString())) == (integerFact.intValue())));
        // Test to see if the (retrieved) facts can be processed
        ArrayList<Object> list = new ArrayList<Object>();
        newCommandKSession.setGlobal("list", list);
        newCommandKSession.fireAllRules();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testInsert() {
        final Environment env = createEnvironment();
        final KieBase kbase = initializeKnowledgeBase(ReloadSessionTest.RULE_WITH_EP);
        KieSession kieSession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        Assert.assertTrue("There should be NO facts present in a new (empty) knowledge session.", kieSession.getFactHandles().isEmpty());
        kieSession.insert(Integer.valueOf(10));
        kieSession = reloadSession(kieSession, env);
        Collection<? extends Object> objects = kieSession.getObjects();
        Assert.assertEquals("Reloaded working memory should contain the fact.", 1, objects.size());
    }

    @Test
    public void testInsertIntoEntryPoint() {
        // RHBRMS-2815
        final Environment env = createEnvironment();
        final KieBase kbase = initializeKnowledgeBase(ReloadSessionTest.RULE_WITH_EP);
        KieSession kieSession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        Assert.assertTrue("There should be NO facts present in a new (empty) knowledge session.", kieSession.getFactHandles().isEmpty());
        kieSession.getEntryPoint(ReloadSessionTest.ENTRY_POINT).insert(Integer.valueOf(10));
        kieSession = reloadSession(kieSession, env);
        Collection<? extends Object> objects = kieSession.getEntryPoint(ReloadSessionTest.ENTRY_POINT).getObjects();
        Assert.assertEquals("Reloaded working memory should contain the fact in the entry point.", 1, objects.size());
    }
}

