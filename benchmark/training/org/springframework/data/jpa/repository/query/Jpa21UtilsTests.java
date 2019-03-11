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


import javax.persistence.AttributeNode;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.support.EntityManagerTestUtils;
import org.springframework.data.jpa.util.IsAttributeNode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application-context.xml")
@Transactional
public class Jpa21UtilsTests {
    @Autowired
    EntityManager em;

    // DATAJPA-1041, DATAJPA-1075
    @Test
    public void shouldCreateGraphWithoutSubGraphCorrectly() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        EntityGraph<User> graph = em.createEntityGraph(User.class);
        Jpa21Utils.configureFetchGraphFrom(new JpaEntityGraph("name", EntityGraphType.FETCH, new String[]{ "roles", "colleagues" }), graph);
        AttributeNode<?> roles = IsAttributeNode.findNode("roles", graph);
        Assert.assertThat(roles, IsAttributeNode.terminatesGraph());
        AttributeNode<?> colleagues = IsAttributeNode.findNode("colleagues", graph);
        Assert.assertThat(colleagues, IsAttributeNode.terminatesGraph());
    }

    // DATAJPA-1041, DATAJPA-1075
    @Test
    public void shouldCreateGraphWithMultipleSubGraphCorrectly() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        EntityGraph<User> graph = em.createEntityGraph(User.class);
        Jpa21Utils.configureFetchGraphFrom(new JpaEntityGraph("name", EntityGraphType.FETCH, new String[]{ "roles", "colleagues.roles", "colleagues.colleagues" }), graph);
        AttributeNode<?> roles = IsAttributeNode.findNode("roles", graph);
        Assert.assertThat(roles, IsAttributeNode.terminatesGraph());
        AttributeNode<?> colleagues = IsAttributeNode.findNode("colleagues", graph);
        Assert.assertThat(colleagues, IsAttributeNode.terminatesGraphWith("roles", "colleagues"));
    }

    // DATAJPA-1041, DATAJPA-1075
    @Test
    public void shouldCreateGraphWithDeepSubGraphCorrectly() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        EntityGraph<User> graph = em.createEntityGraph(User.class);
        Jpa21Utils.configureFetchGraphFrom(new JpaEntityGraph("name", EntityGraphType.FETCH, new String[]{ "roles", "colleagues.roles", "colleagues.colleagues.roles" }), graph);
        AttributeNode<?> roles = IsAttributeNode.findNode("roles", graph);
        Assert.assertThat(roles, IsAttributeNode.terminatesGraph());
        AttributeNode<?> colleagues = IsAttributeNode.findNode("colleagues", graph);
        Assert.assertThat(colleagues, IsAttributeNode.terminatesGraphWith("roles"));
        Assert.assertThat(colleagues, IsAttributeNode.hasSubgraphs("colleagues"));
        AttributeNode<?> colleaguesOfColleagues = IsAttributeNode.findNode("colleagues", colleagues);
        Assert.assertThat(colleaguesOfColleagues, IsAttributeNode.terminatesGraphWith("roles"));
    }

    // DATAJPA-1041, DATAJPA-1075
    @Test
    public void shouldIgnoreIntermedeateSubGraphNodesThatAreNotNeeded() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        EntityGraph<User> graph = em.createEntityGraph(User.class);
        Jpa21Utils.configureFetchGraphFrom(new JpaEntityGraph("name", EntityGraphType.FETCH, new String[]{ "roles", "colleagues", "colleagues.roles", "colleagues.colleagues", "colleagues.colleagues.roles" }), graph);
        AttributeNode<?> roles = IsAttributeNode.findNode("roles", graph);
        Assert.assertThat(roles, IsAttributeNode.terminatesGraph());
        AttributeNode<?> colleagues = IsAttributeNode.findNode("colleagues", graph);
        Assert.assertThat(colleagues, IsAttributeNode.terminatesGraphWith("roles"));
        Assert.assertThat(colleagues, IsAttributeNode.hasSubgraphs("colleagues"));
        AttributeNode<?> colleaguesOfColleagues = IsAttributeNode.findNode("colleagues", colleagues);
        Assert.assertThat(colleaguesOfColleagues, IsAttributeNode.terminatesGraphWith("roles"));
    }

    // DATAJPA-1041, DATAJPA-1075
    @Test
    public void orderOfSubGraphsShouldNotMatter() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        EntityGraph<User> graph = em.createEntityGraph(User.class);
        Jpa21Utils.configureFetchGraphFrom(new JpaEntityGraph("name", EntityGraphType.FETCH, new String[]{ "colleagues.colleagues.roles", "roles", "colleagues.colleagues", "colleagues", "colleagues.roles" }), graph);
        AttributeNode<?> roles = IsAttributeNode.findNode("roles", graph);
        Assert.assertThat(roles, IsAttributeNode.terminatesGraph());
        AttributeNode<?> colleagues = IsAttributeNode.findNode("colleagues", graph);
        Assert.assertThat(colleagues, IsAttributeNode.terminatesGraphWith("roles"));
        Assert.assertThat(colleagues, IsAttributeNode.hasSubgraphs("colleagues"));
        AttributeNode<?> colleaguesOfColleagues = IsAttributeNode.findNode("colleagues", colleagues);
        Assert.assertThat(colleaguesOfColleagues, IsAttributeNode.terminatesGraphWith("roles"));
    }

    // DATAJPA-1041, DATAJPA-1075
    @Test(expected = Exception.class)
    public void errorsOnUnknownProperties() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        Jpa21Utils.configureFetchGraphFrom(new JpaEntityGraph("name", EntityGraphType.FETCH, new String[]{ "\u00af\\_(\u30c4)_/\u00af" }), em.createEntityGraph(User.class));
    }
}

