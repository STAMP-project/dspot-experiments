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
package org.springframework.data.jpa.repository.support;


import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration tests for {@link Querydsl}.
 *
 * @author Thomas Darimont
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:infrastructure.xml" })
@Transactional
public class QuerydslIntegrationTests {
    @PersistenceContext
    EntityManager em;

    Querydsl querydsl;

    PathBuilder<User> userPath;

    JPQLQuery<User> userQuery;

    // DATAJPA-499
    @Test
    public void defaultOrderingShouldNotGenerateAnNullOrderingHint() {
        JPQLQuery<User> result = querydsl.applySorting(Sort.by("firstname"), userQuery);
        Assert.assertThat(result, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(result.toString(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.anyOf(CoreMatchers.containsString("nulls first"), CoreMatchers.containsString("nulls last")))));
    }
}

