/**
 * Copyright 2014-2019 the original author or authors.
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
package org.springframework.data.jpa.provider;


import javax.persistence.EntityManager;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.domain.sample.Category;
import org.springframework.data.jpa.domain.sample.Product;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.sample.CategoryRepository;
import org.springframework.data.jpa.repository.sample.ProductRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;


/**
 * Integration tests for {@link PersistenceProvider}.
 *
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PersistenceProviderIntegrationTests {
    @Configuration
    @ImportResource("classpath:infrastructure.xml")
    @EnableJpaRepositories(basePackageClasses = CategoryRepository.class// 
    , includeFilters = @Filter(value = { CategoryRepository.class, ProductRepository.class }, type = FilterType.ASSIGNABLE_TYPE))
    static class Config {}

    @Autowired
    CategoryRepository categories;

    @Autowired
    ProductRepository products;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    EntityManager em;

    Product product;

    Category category;

    // DATAJPA-630
    @Test
    public void testname() {
        execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                Product product = findById(category.getId()).get().getProduct();
                ProxyIdAccessor accessor = PersistenceProvider.fromEntityManager(em);
                Assert.assertThat(accessor.shouldUseAccessorFor(product), CoreMatchers.is(true));
                Assert.assertThat(accessor.getIdentifierFrom(product).toString(), CoreMatchers.is(((Object) (product.getId().toString()))));
                return null;
            }
        });
    }
}

