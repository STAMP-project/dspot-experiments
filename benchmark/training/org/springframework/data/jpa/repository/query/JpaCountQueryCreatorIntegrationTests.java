/**
 * Copyright 2017-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.query;


import java.lang.reflect.Method;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.jpa.domain.sample.Role;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.AbstractRepositoryMetadata;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Integration tests for {@link JpaCountQueryCreator}.
 *
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:infrastructure.xml")
public class JpaCountQueryCreatorIntegrationTests {
    @PersistenceContext
    EntityManager entityManager;

    // DATAJPA-1044
    @Test
    public void distinctFlagOnCountQueryIssuesCountDistinct() throws Exception {
        Method method = JpaCountQueryCreatorIntegrationTests.SomeRepository.class.getMethod("findDistinctByRolesIn", List.class);
        PersistenceProvider provider = PersistenceProvider.fromEntityManager(entityManager);
        JpaQueryMethod queryMethod = new JpaQueryMethod(method, AbstractRepositoryMetadata.getMetadata(JpaCountQueryCreatorIntegrationTests.SomeRepository.class), new SpelAwareProxyProjectionFactory(), provider);
        PartTree tree = new PartTree("findDistinctByRolesIn", User.class);
        ParameterMetadataProvider metadataProvider = new ParameterMetadataProvider(entityManager.getCriteriaBuilder(), queryMethod.getParameters(), provider);
        JpaCountQueryCreator creator = new JpaCountQueryCreator(tree, queryMethod.getResultProcessor().getReturnedType(), entityManager.getCriteriaBuilder(), metadataProvider);
        TypedQuery<? extends Object> query = entityManager.createQuery(creator.createQuery());
        Assert.assertThat(HibernateUtils.getHibernateQuery(query), CoreMatchers.startsWith("select distinct count(distinct"));
    }

    interface SomeRepository extends Repository<User, Long> {
        void findDistinctByRolesIn(List<Role> roles);
    }
}

