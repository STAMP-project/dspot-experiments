/**
 * Copyright 2011-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.cdi;


import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.spi.Bean;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration tests for Spring Data JPA CDI extension.
 *
 * @author Dirk Mahler
 * @author Oliver Gierke
 * @author Mark Paluch
 */
public class CdiExtensionIntegrationTests {
    private static Logger LOGGER = LoggerFactory.getLogger(CdiExtensionIntegrationTests.class);

    static SeContainer container;

    // DATAJPA-319, DATAJPA-1180
    @Test
    @SuppressWarnings("rawtypes")
    public void foo() {
        Set<Bean<?>> beans = CdiExtensionIntegrationTests.container.getBeanManager().getBeans(PersonRepository.class);
        Assert.assertThat(beans, hasSize(1));
        Assert.assertThat(beans.iterator().next().getScope(), is(equalTo(((Class) (ApplicationScoped.class)))));
    }

    // DATAJPA-136, DATAJPA-1180
    @Test
    public void saveAndFindAll() {
        RepositoryConsumer repositoryConsumer = CdiExtensionIntegrationTests.container.select(RepositoryConsumer.class).get();
        Person person = new Person();
        repositoryConsumer.save(person);
        repositoryConsumer.findAll();
    }

    // DATAJPA-584, DATAJPA-1180
    @Test
    public void returnOneFromCustomImpl() {
        RepositoryConsumer repositoryConsumer = CdiExtensionIntegrationTests.container.select(RepositoryConsumer.class).get();
        Assert.assertThat(repositoryConsumer.returnOne(), is(1));
    }

    // DATAJPA-584, DATAJPA-1180
    @Test
    public void useQualifiedCustomizedUserRepo() {
        RepositoryConsumer repositoryConsumer = CdiExtensionIntegrationTests.container.select(RepositoryConsumer.class).get();
        repositoryConsumer.doSomethingOnUserDB();
    }

    // DATAJPA-1287
    @Test
    public void useQualifiedFragmentUserRepo() {
        RepositoryConsumer repositoryConsumer = CdiExtensionIntegrationTests.container.select(RepositoryConsumer.class).get();
        Assert.assertThat(repositoryConsumer.returnOneUserDB(), is(1));
    }
}

