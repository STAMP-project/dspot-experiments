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
package org.springframework.test.context.junit4.spr9645;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.tests.transaction.CallCountingTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration tests that verify the behavior requested in
 * <a href="https://jira.spring.io/browse/SPR-9645">SPR-9645</a>.
 *
 * @author Sam Brannen
 * @since 3.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class LookUpTxMgrByTypeTests {
    private static final CallCountingTransactionManager txManager = new CallCountingTransactionManager();

    @Configuration
    static class Config {
        @Bean
        public PlatformTransactionManager txManager() {
            return LookUpTxMgrByTypeTests.txManager;
        }
    }

    @Test
    public void transactionalTest() {
        Assert.assertEquals(1, LookUpTxMgrByTypeTests.txManager.begun);
        Assert.assertEquals(1, LookUpTxMgrByTypeTests.txManager.inflight);
        Assert.assertEquals(0, LookUpTxMgrByTypeTests.txManager.commits);
        Assert.assertEquals(0, LookUpTxMgrByTypeTests.txManager.rollbacks);
    }
}

