/**
 * * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.drools.compiler.Person;
import org.drools.core.SessionConfiguration;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;


@RunWith(Parameterized.class)
public class JpaPersistentStatefulSessionTest {
    private Map<String, Object> context;

    private Environment env;

    private final boolean locking;

    public JpaPersistentStatefulSessionTest(final String locking) {
        this.locking = DroolsPersistenceUtil.PESSIMISTIC_LOCKING.equals(locking);
    }

    @Test
    public void testFactHandleSerialization() {
        factHandleSerialization(false);
    }

    @Test
    public void testFactHandleSerializationWithOOPath() {
        factHandleSerialization(true);
    }

    @Test
    public void testLocalTransactionPerStatement() {
        localTransactionPerStatement(false);
    }

    @Test
    public void testLocalTransactionPerStatementWithOOPath() {
        localTransactionPerStatement(true);
    }

    @Test
    public void testUserTransactions() throws Exception {
        userTransactions(false);
    }

    @Test
    public void testUserTransactionsWithOOPath() throws Exception {
        userTransactions(true);
    }

    @Test
    public void testInterceptor() {
        interceptor(false);
    }

    @Test
    public void testInterceptorWithOOPath() {
        interceptor(true);
    }

    @Test
    public void testInterceptorOnRollback() throws Exception {
        interceptorOnRollback(false);
    }

    @Test
    public void testInterceptorOnRollbackWithOOPAth() throws Exception {
        interceptorOnRollback(true);
    }

    @Test
    public void testSetFocus() {
        testFocus(false);
    }

    @Test
    public void testSetFocusWithOOPath() {
        testFocus(true);
    }

    @Test
    public void testSharedReferences() {
        final KieBase kbase = new KieHelper().getKieContainer().getKieBase();
        final KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, null, env);
        final Person x = new Person("test");
        final List<Person> test = new ArrayList<>();
        final List<Person> test2 = new ArrayList<>();
        test.add(x);
        test2.add(x);
        assertThat(test.get(0)).isSameAs(test2.get(0));
        ksession.insert(test);
        ksession.insert(test2);
        ksession.fireAllRules();
        final StatefulKnowledgeSession ksession2 = JPAKnowledgeService.loadStatefulKnowledgeSession(ksession.getIdentifier(), kbase, null, env);
        final Iterator c = ksession2.getObjects().iterator();
        final List ref1 = ((List) (c.next()));
        final List ref2 = ((List) (c.next()));
        assertThat(ref1.get(0)).isSameAs(ref2.get(0));
    }

    @Test
    public void testMergeConfig() {
        // JBRULES-3155
        final KieBase kbase = new KieHelper().getKieContainer().getKieBase();
        final Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "com.example.CustomJPAProcessInstanceManagerFactory");
        final KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        final KieSession ksession = KieServices.get().getStoreServices().newKieSession(kbase, config, env);
        final SessionConfiguration sessionConfig = ((SessionConfiguration) (ksession.getSessionConfiguration()));
        assertThat(sessionConfig.getProcessInstanceManagerFactory()).isEqualTo("com.example.CustomJPAProcessInstanceManagerFactory");
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAndDestroySession() {
        createAndDestroySession(false);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAndDestroySessionWithOOPath() {
        createAndDestroySession(true);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAndDestroyNonPersistentSession() {
        createAndDestroyNonPersistentSession(false);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAndDestroyNonPersistentSessionWithOOPath() {
        createAndDestroyNonPersistentSession(true);
    }

    @Test
    public void testFromNodeWithModifiedCollection() {
        fromNodeWithModifiedCollection(false);
    }

    @Test
    public void testFromNodeWithModifiedCollectionWithOOPath() {
        fromNodeWithModifiedCollection(true);
    }
}

