/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.persistence;


import org.drools.core.impl.EnvironmentFactory;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.persistence.jta.JtaTransactionManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.Environment;


public class TransactionManagerFactoryTest {
    TransactionManagerFactory transactionManagerFactory = getTransactionManagerFactory();

    @Test
    public void defaultsToJtaTransactionManagerFactory() throws Exception {
        Assert.assertTrue(((transactionManagerFactory.get()) instanceof JtaTransactionManagerFactory));
    }

    @Test
    public void createsSystemPropertySpecifiedFactory() throws Exception {
        System.setProperty("org.kie.txm.factory.class", TransactionManagerFactoryTest.TestTransactionManagerFactory.class.getName());
        transactionManagerFactory.resetInstance();
        Assert.assertEquals(TransactionManagerFactoryTest.TestTransactionManagerFactory.class.getName(), transactionManagerFactory.get().getClass().getName());
    }

    @Test
    public void createAndResetSystemPropertySpecifiedFactory() throws Exception {
        System.setProperty("org.kie.txm.factory.class", TransactionManagerFactoryTest.TestTransactionManagerFactory.class.getName());
        transactionManagerFactory.resetInstance();
        Assert.assertEquals(TransactionManagerFactoryTest.TestTransactionManagerFactory.class.getName(), transactionManagerFactory.get().getClass().getName());
        System.setProperty("org.kie.txm.factory.class", TransactionManagerFactoryTest.TestTransactionManagerFactoryTwo.class.getName());
        transactionManagerFactory.resetInstance();
        Assert.assertEquals(TransactionManagerFactoryTest.TestTransactionManagerFactoryTwo.class.getName(), transactionManagerFactory.get().getClass().getName());
        transactionManagerFactory.resetInstance();
    }

    @Test
    public void createsJtaTransactionManager() throws Exception {
        Assert.assertEquals(JtaTransactionManager.class.getName(), transactionManagerFactory.newTransactionManager().getClass().getName());
    }

    @Test
    public void createsJtaTransactionManagerWithEnvironment() throws Exception {
        Environment env = EnvironmentFactory.newEnvironment();
        Assert.assertEquals(JtaTransactionManagerFactory.class.getName(), transactionManagerFactory.get().getClass().getName());
        Assert.assertNotNull(transactionManagerFactory.newTransactionManager(env));
        Assert.assertEquals(JtaTransactionManager.class.getName(), transactionManagerFactory.newTransactionManager(env).getClass().getName());
    }

    public static final class TestTransactionManagerFactory extends TransactionManagerFactory {
        @Override
        public TransactionManager newTransactionManager() {
            return null;
        }

        @Override
        public TransactionManager newTransactionManager(Environment environment) {
            return null;
        }
    }

    public static final class TestTransactionManagerFactoryTwo extends TransactionManagerFactory {
        @Override
        public TransactionManager newTransactionManager() {
            return null;
        }

        @Override
        public TransactionManager newTransactionManager(Environment environment) {
            return null;
        }
    }
}

