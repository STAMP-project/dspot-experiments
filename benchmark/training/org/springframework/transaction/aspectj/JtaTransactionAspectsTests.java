/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.transaction.aspectj;


import java.io.IOException;
import javax.transaction.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.tests.transaction.CallCountingTransactionManager;


/**
 *
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JtaTransactionAspectsTests.Config.class)
public class JtaTransactionAspectsTests {
    @Autowired
    private CallCountingTransactionManager txManager;

    @Test
    public void commitOnAnnotatedPublicMethod() throws Throwable {
        Assert.assertEquals(0, this.txManager.begun);
        new JtaTransactionAspectsTests.JtaAnnotationPublicAnnotatedMember().echo(null);
        Assert.assertEquals(1, this.txManager.commits);
    }

    @Test
    public void matchingRollbackOnApplied() throws Throwable {
        Assert.assertEquals(0, this.txManager.begun);
        InterruptedException test = new InterruptedException();
        try {
            new JtaTransactionAspectsTests.JtaAnnotationPublicAnnotatedMember().echo(test);
            Assert.fail("Should have thrown an exception");
        } catch (Throwable throwable) {
            Assert.assertEquals("wrong exception", test, throwable);
        }
        Assert.assertEquals(1, this.txManager.rollbacks);
        Assert.assertEquals(0, this.txManager.commits);
    }

    @Test
    public void nonMatchingRollbackOnApplied() throws Throwable {
        Assert.assertEquals(0, this.txManager.begun);
        IOException test = new IOException();
        try {
            new JtaTransactionAspectsTests.JtaAnnotationPublicAnnotatedMember().echo(test);
            Assert.fail("Should have thrown an exception");
        } catch (Throwable throwable) {
            Assert.assertEquals("wrong exception", test, throwable);
        }
        Assert.assertEquals(1, this.txManager.commits);
        Assert.assertEquals(0, this.txManager.rollbacks);
    }

    @Test
    public void commitOnAnnotatedProtectedMethod() {
        Assert.assertEquals(0, this.txManager.begun);
        new JtaTransactionAspectsTests.JtaAnnotationProtectedAnnotatedMember().doInTransaction();
        Assert.assertEquals(1, this.txManager.commits);
    }

    @Test
    public void nonAnnotatedMethodCallingProtectedMethod() {
        Assert.assertEquals(0, this.txManager.begun);
        new JtaTransactionAspectsTests.JtaAnnotationProtectedAnnotatedMember().doSomething();
        Assert.assertEquals(1, this.txManager.commits);
    }

    @Test
    public void commitOnAnnotatedPrivateMethod() {
        Assert.assertEquals(0, this.txManager.begun);
        new JtaTransactionAspectsTests.JtaAnnotationPrivateAnnotatedMember().doInTransaction();
        Assert.assertEquals(1, this.txManager.commits);
    }

    @Test
    public void nonAnnotatedMethodCallingPrivateMethod() {
        Assert.assertEquals(0, this.txManager.begun);
        new JtaTransactionAspectsTests.JtaAnnotationPrivateAnnotatedMember().doSomething();
        Assert.assertEquals(1, this.txManager.commits);
    }

    @Test
    public void notTransactional() {
        Assert.assertEquals(0, this.txManager.begun);
        new TransactionAspectTests.NotTransactional().noop();
        Assert.assertEquals(0, this.txManager.begun);
    }

    public static class JtaAnnotationPublicAnnotatedMember {
        @Transactional(rollbackOn = InterruptedException.class)
        public void echo(Throwable t) throws Throwable {
            if (t != null) {
                throw t;
            }
        }
    }

    protected static class JtaAnnotationProtectedAnnotatedMember {
        public void doSomething() {
            doInTransaction();
        }

        @Transactional
        protected void doInTransaction() {
        }
    }

    protected static class JtaAnnotationPrivateAnnotatedMember {
        public void doSomething() {
            doInTransaction();
        }

        @Transactional
        private void doInTransaction() {
        }
    }

    @Configuration
    protected static class Config {
        @Bean
        public CallCountingTransactionManager transactionManager() {
            return new CallCountingTransactionManager();
        }

        @Bean
        public JtaAnnotationTransactionAspect transactionAspect() {
            JtaAnnotationTransactionAspect aspect = JtaAnnotationTransactionAspect.aspectOf();
            aspect.setTransactionManager(transactionManager());
            return aspect;
        }
    }
}

