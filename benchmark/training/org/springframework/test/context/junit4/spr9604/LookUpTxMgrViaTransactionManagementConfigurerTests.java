/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.test.context.junit4.spr9604;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.tests.transaction.CallCountingTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration tests that verify the behavior requested in
 * <a href="https://jira.spring.io/browse/SPR-9604">SPR-9604</a>.
 *
 * @author Sam Brannen
 * @since 3.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class LookUpTxMgrViaTransactionManagementConfigurerTests {
    private static final CallCountingTransactionManager txManager1 = new CallCountingTransactionManager();

    private static final CallCountingTransactionManager txManager2 = new CallCountingTransactionManager();

    @Configuration
    static class Config implements TransactionManagementConfigurer {
        @Override
        public PlatformTransactionManager annotationDrivenTransactionManager() {
            return txManager1();
        }

        @Bean
        public PlatformTransactionManager txManager1() {
            return LookUpTxMgrViaTransactionManagementConfigurerTests.txManager1;
        }

        @Bean
        public PlatformTransactionManager txManager2() {
            return LookUpTxMgrViaTransactionManagementConfigurerTests.txManager2;
        }
    }

    @Test
    public void transactionalTest() {
        Assert.assertEquals(1, LookUpTxMgrViaTransactionManagementConfigurerTests.txManager1.begun);
        Assert.assertEquals(1, LookUpTxMgrViaTransactionManagementConfigurerTests.txManager1.inflight);
        Assert.assertEquals(0, LookUpTxMgrViaTransactionManagementConfigurerTests.txManager1.commits);
        Assert.assertEquals(0, LookUpTxMgrViaTransactionManagementConfigurerTests.txManager1.rollbacks);
        Assert.assertEquals(0, LookUpTxMgrViaTransactionManagementConfigurerTests.txManager2.begun);
        Assert.assertEquals(0, LookUpTxMgrViaTransactionManagementConfigurerTests.txManager2.inflight);
        Assert.assertEquals(0, LookUpTxMgrViaTransactionManagementConfigurerTests.txManager2.commits);
        Assert.assertEquals(0, LookUpTxMgrViaTransactionManagementConfigurerTests.txManager2.rollbacks);
    }
}

