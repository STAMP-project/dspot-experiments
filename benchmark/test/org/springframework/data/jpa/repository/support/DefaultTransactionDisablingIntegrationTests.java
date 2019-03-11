/**
 * Copyright 2015-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.support;


import javax.persistence.TransactionRequiredException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.repository.sample.UserRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Integration tests for disabling default transactions using JavaConfig.
 *
 * @author Oliver Gierke
 * @unknown The Intersphere - Live in Mannheim
 */
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class DefaultTransactionDisablingIntegrationTests {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    UserRepository repository;

    @Autowired
    TransactionalRepositoryTests.DelegatingTransactionManager txManager;

    // DATAJPA-685
    @Test
    public void considersExplicitConfigurationOnRepositoryInterface() {
        repository.findById(1);
        Assert.assertThat(txManager.getDefinition().isReadOnly(), CoreMatchers.is(false));
    }

    // DATAJPA-685
    @Test
    public void doesNotUseDefaultTransactionsOnNonRedeclaredMethod() {
        repository.findAll(PageRequest.of(0, 10));
        Assert.assertThat(txManager.getDefinition(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    // DATAJPA-685
    @Test
    public void persistingAnEntityShouldThrowExceptionDueToMissingTransaction() {
        exception.expect(InvalidDataAccessApiUsageException.class);
        exception.expectCause(CoreMatchers.is(Matchers.<Throwable>instanceOf(TransactionRequiredException.class)));
        saveAndFlush(new User());
    }
}

