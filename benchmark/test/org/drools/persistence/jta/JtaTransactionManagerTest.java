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
package org.drools.persistence.jta;


import EnvironmentName.TRANSACTION;
import EnvironmentName.TRANSACTION_MANAGER;
import EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;
import org.drools.persistence.PersistableRunner;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.jpa.JpaPersistenceContextManager;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.hibernate.TransientObjectException;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JtaTransactionManagerTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    // Datasource (setup & clean up)
    private Map<String, Object> context;

    private EntityManagerFactory emf;

    private static String simpleRule = "package org.kie.test\n" + ((((((("global java.util.List list\n" + "rule rule1\n") + "when\n") + "  Integer(intValue > 0)\n") + "then\n") + "  list.add( 1 );\n") + "end\n") + "\n");

    public static final String DEFAULT_USER_TRANSACTION_NAME = "java:comp/UserTransaction";

    @Test
    public void showingTransactionTestObjectsNeedTransactions() throws Exception {
        String testName = getTestName();
        // Create linked transactionTestObjects but only persist the main one.
        TransactionTestObject badMainObject = new TransactionTestObject();
        badMainObject.setName(("bad" + testName));
        TransactionTestObject subObject = new TransactionTestObject();
        subObject.setName(("sub" + testName));
        badMainObject.setSubObject(subObject);
        // Initialized persistence/tx's and persist to db
        EntityManager em = emf.createEntityManager();
        UserTransaction tx = findUserTransaction();
        tx.begin();
        em.joinTransaction();
        em.persist(badMainObject);
        boolean rollBackExceptionthrown = false;
        try {
            logger.info((("The following " + (IllegalStateException.class.getSimpleName())) + " SHOULD be thrown."));
            tx.commit();
        } catch (Exception e) {
            if ((e instanceof RollbackException) || ((e.getCause()) instanceof TransientObjectException)) {
                rollBackExceptionthrown = true;
                if ((tx.getStatus()) == 1) {
                    tx.rollback();
                }
            }
        }
        Assert.assertTrue("A rollback exception should have been thrown because of foreign key violations.", rollBackExceptionthrown);
        TransactionTestObject mainObject = new TransactionTestObject();
        mainObject.setName(("main" + testName));
        mainObject.setSubObject(subObject);
        // Now persist both..
        tx.begin();
        em.joinTransaction();
        em.persist(mainObject);
        em.persist(subObject);
        try {
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("No exception should have been thrown: " + (e.getMessage())));
        }
    }

    @Test
    public void basicTransactionManagerTest() {
        String testName = getTestName();
        // Setup the JtaTransactionmanager
        Environment env = DroolsPersistenceUtil.createEnvironment(context);
        Object tm = env.get(TRANSACTION_MANAGER);
        TransactionManager txm = new JtaTransactionManager(env.get(TRANSACTION), env.get(TRANSACTION_SYNCHRONIZATION_REGISTRY), tm);
        // Create linked transactionTestObjects
        TransactionTestObject mainObject = new TransactionTestObject();
        mainObject.setName(("main" + testName));
        TransactionTestObject subObject = new TransactionTestObject();
        subObject.setName(("sub" + testName));
        mainObject.setSubObject(subObject);
        // Commit the mainObject after "commiting" the subObject
        EntityManager em = emf.createEntityManager();
        try {
            // Begin the real trasnaction
            boolean txOwner = txm.begin();
            // Do the "sub" transaction
            // - the txm doesn't really commit,
            // because we keep track of who's the tx owner.
            boolean notTxOwner = txm.begin();
            em.persist(mainObject);
            txm.commit(notTxOwner);
            // Finish the transaction off
            em.persist(subObject);
            txm.commit(txOwner);
        } catch (Throwable t) {
            Assert.fail(("No exception should have been thrown: " + (t.getMessage())));
        }
    }

    @Test
    public void basicTransactionRollbackTest() {
        Environment env = DroolsPersistenceUtil.createEnvironment(context);
        Object tm = env.get(TRANSACTION_MANAGER);
        TransactionManager txm = new JtaTransactionManager(env.get(TRANSACTION), env.get(TRANSACTION_SYNCHRONIZATION_REGISTRY), tm);
        // Create linked transactionTestObjects
        TransactionTestObject mainObject = new TransactionTestObject();
        mainObject.setName("main");
        TransactionTestObject subObject = new TransactionTestObject();
        subObject.setName("sub");
        mainObject.setSubObject(subObject);
        EntityManager em = emf.createEntityManager();
        try {
            boolean txOwner = txm.begin();
            boolean notTxOwner = txm.begin();
            em.persist(mainObject);
            txm.rollback(notTxOwner);
            em.persist(subObject);
            txm.rollback(txOwner);
        } catch (Exception e) {
            Assert.fail(("There should not be an exception thrown here: " + (e.getMessage())));
        }
    }

    public static String COMMAND_ENTITY_MANAGER = "drools.persistence.test.command.EntityManager";

    public static String COMMAND_ENTITY_MANAGER_FACTORY = "drools.persistence.test.EntityManagerFactory";

    @Test
    public void testSingleSessionCommandServiceAndJtaTransactionManagerTogether() throws Exception {
        // Initialize drools environment stuff
        Environment env = DroolsPersistenceUtil.createEnvironment(context);
        KieBase kbase = initializeKnowledgeBase(JtaTransactionManagerTest.simpleRule);
        KieSession commandKSession = KieServices.get().getStoreServices().newKieSession(kbase, null, env);
        // StatefulKnowledgeSession commandKSession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        commandKSession.getIdentifier();// initialize CSEM

        PersistableRunner commandService = ((PersistableRunner) (getRunner()));
        JpaPersistenceContextManager jpm = ((JpaPersistenceContextManager) (JtaTransactionManagerTest.getValueOfField("jpm", commandService)));
        TransactionTestObject mainObject = new TransactionTestObject();
        mainObject.setName("mainCommand");
        TransactionTestObject subObject = new TransactionTestObject();
        subObject.setName("subCommand");
        mainObject.setSubObject(subObject);
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        HashMap<String, Object> emEnv = new HashMap<String, Object>();
        emEnv.put(JtaTransactionManagerTest.COMMAND_ENTITY_MANAGER_FACTORY, emf);
        emEnv.put(JtaTransactionManagerTest.COMMAND_ENTITY_MANAGER, jpm.getCommandScopedEntityManager());
        TransactionTestCommand txTestCmd = new TransactionTestCommand(mainObject, subObject, emEnv);
        commandKSession.execute(txTestCmd);
        ut.commit();
    }
}

