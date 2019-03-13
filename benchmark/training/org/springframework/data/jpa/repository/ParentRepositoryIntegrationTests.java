/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.data.jpa.repository;


import Sort.Direction.ASC;
import java.util.List;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.sample.Child;
import org.springframework.data.jpa.domain.sample.Parent;
import org.springframework.data.jpa.repository.sample.ParentRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/namespace-application-context.xml")
public class ParentRepositoryIntegrationTests {
    @Autowired
    ParentRepository repository;

    // DATAJPA-287
    @Test
    public void testWithoutJoin() throws Exception {
        Page<Parent> page = repository.findAll(new org.springframework.data.jpa.domain.Specification<Parent>() {
            @Override
            public Predicate toPredicate(Root<Parent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<Set<Child>> childrenPath = root.get("children");
                query.distinct(true);
                return cb.isNotEmpty(childrenPath);
            }
        }, PageRequest.of(0, 5, Sort.by(ASC, "id")));
        List<Parent> content = page.getContent();
        Assert.assertThat(content.size(), CoreMatchers.is(3));
        Assert.assertThat(page.getSize(), CoreMatchers.is(5));
        Assert.assertThat(page.getNumber(), CoreMatchers.is(0));
        Assert.assertThat(page.getTotalElements(), CoreMatchers.is(3L));
        Assert.assertThat(page.getTotalPages(), CoreMatchers.is(1));
    }

    // DATAJPA-287
    @Test
    public void testWithJoin() throws Exception {
        Page<Parent> page = repository.findAll(new org.springframework.data.jpa.domain.Specification<Parent>() {
            @Override
            public Predicate toPredicate(Root<Parent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                root.join("children");
                // we are interesting in distinct items, especially when join presents in query
                query.distinct(true);
                return cb.isNotEmpty(root.<Set<Child>>get("children"));
            }
        }, PageRequest.of(0, 5, Sort.by(ASC, "id")));
        List<Parent> content = page.getContent();
        // according to the initial setup there should be
        // 3 parents which children collection is not empty
        Assert.assertThat(content.size(), CoreMatchers.is(3));
        Assert.assertThat(page.getSize(), CoreMatchers.is(5));
        Assert.assertThat(page.getNumber(), CoreMatchers.is(0));
        // we get here wrong total elements number since
        // count query doesn't take into account the distinct marker of query
        Assert.assertThat(page.getTotalElements(), CoreMatchers.is(3L));
        Assert.assertThat(page.getTotalPages(), CoreMatchers.is(1));
    }
}

