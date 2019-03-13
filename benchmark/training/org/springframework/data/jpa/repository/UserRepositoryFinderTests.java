/**
 * Copyright 2008-2019 the original author or authors.
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


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.sample.Role;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.repository.sample.RoleRepository;
import org.springframework.data.jpa.repository.sample.UserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration test for executing finders, thus testing various query lookup strategies.
 *
 * @see QueryLookupStrategy
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/namespace-application-context.xml")
@Transactional
public class UserRepositoryFinderTests {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    User dave;

    User carter;

    User oliver;

    Role drummer;

    Role guitarist;

    Role singer;

    /**
     * Tests creation of a simple query.
     */
    @Test
    public void testSimpleCustomCreatedFinder() {
        User user = userRepository.findByEmailAddressAndLastname("dave@dmband.com", "Matthews");
        Assert.assertEquals(dave, user);
    }

    /**
     * Tests that the repository returns {@code null} for not found objects for finder methods that return a single domain
     * object.
     */
    @Test
    public void returnsNullIfNothingFound() {
        User user = userRepository.findByEmailAddress("foobar");
        Assert.assertEquals(null, user);
    }

    /**
     * Tests creation of a simple query consisting of {@code AND} and {@code OR} parts.
     */
    @Test
    public void testAndOrFinder() {
        List<User> users = userRepository.findByEmailAddressAndLastnameOrFirstname("dave@dmband.com", "Matthews", "Carter");
        Assert.assertNotNull(users);
        Assert.assertEquals(2, users.size());
        Assert.assertTrue(users.contains(dave));
        Assert.assertTrue(users.contains(carter));
    }

    @Test
    public void executesPagingMethodToPageCorrectly() {
        Page<User> page = userRepository.findByLastname(PageRequest.of(0, 1), "Matthews");
        Assert.assertThat(page.getNumberOfElements(), is(1));
        Assert.assertThat(page.getTotalElements(), is(2L));
        Assert.assertThat(page.getTotalPages(), is(2));
    }

    @Test
    public void executesPagingMethodToListCorrectly() {
        List<User> list = userRepository.findByFirstname("Carter", PageRequest.of(0, 1));
        Assert.assertThat(list.size(), is(1));
    }

    @Test
    public void executesInKeywordForPageCorrectly() {
        Page<User> page = userRepository.findByFirstnameIn(PageRequest.of(0, 1), "Dave", "Oliver August");
        Assert.assertThat(page.getNumberOfElements(), is(1));
        Assert.assertThat(page.getTotalElements(), is(2L));
        Assert.assertThat(page.getTotalPages(), is(2));
    }

    @Test
    public void executesNotInQueryCorrectly() throws Exception {
        List<User> result = userRepository.findByFirstnameNotIn(Arrays.asList("Dave", "Carter"));
        Assert.assertThat(result.size(), is(1));
        Assert.assertThat(result.get(0), is(oliver));
    }

    // DATAJPA-92
    @Test
    public void findsByLastnameIgnoringCase() throws Exception {
        List<User> result = userRepository.findByLastnameIgnoringCase("BeAUfoRd");
        Assert.assertThat(result.size(), is(1));
        Assert.assertThat(result.get(0), is(carter));
    }

    // DATAJPA-92
    @Test
    public void findsByLastnameIgnoringCaseLike() throws Exception {
        List<User> result = userRepository.findByLastnameIgnoringCaseLike("BeAUfo%");
        Assert.assertThat(result.size(), is(1));
        Assert.assertThat(result.get(0), is(carter));
    }

    // DATAJPA-92
    @Test
    public void findByLastnameAndFirstnameAllIgnoringCase() throws Exception {
        List<User> result = userRepository.findByLastnameAndFirstnameAllIgnoringCase("MaTTheWs", "DaVe");
        Assert.assertThat(result.size(), is(1));
        Assert.assertThat(result.get(0), is(dave));
    }

    // DATAJPA-94
    @Test
    public void respectsPageableOrderOnQueryGenerateFromMethodName() throws Exception {
        Page<User> ascending = userRepository.findByLastnameIgnoringCase(PageRequest.of(0, 10, Sort.by(ASC, "firstname")), "Matthews");
        Page<User> descending = userRepository.findByLastnameIgnoringCase(PageRequest.of(0, 10, Sort.by(DESC, "firstname")), "Matthews");
        Assert.assertThat(ascending.getTotalElements(), is(2L));
        Assert.assertThat(descending.getTotalElements(), is(2L));
        Assert.assertThat(ascending.getContent().get(0).getFirstname(), is(not(equalTo(descending.getContent().get(0).getFirstname()))));
        Assert.assertThat(ascending.getContent().get(0).getFirstname(), is(equalTo(descending.getContent().get(1).getFirstname())));
        Assert.assertThat(ascending.getContent().get(1).getFirstname(), is(equalTo(descending.getContent().get(0).getFirstname())));
    }

    // DATAJPA-486
    @Test
    public void executesQueryToSlice() {
        Slice<User> slice = userRepository.findSliceByLastname("Matthews", PageRequest.of(0, 1, ASC, "firstname"));
        Assert.assertThat(slice.getContent(), hasItem(dave));
        Assert.assertThat(slice.hasNext(), is(true));
    }

    // DATAJPA-830
    @Test
    public void executesMethodWithNotContainingOnStringCorrectly() {
        Assert.assertThat(userRepository.findByLastnameNotContaining("u"), containsInAnyOrder(dave, oliver));
    }

    // DATAJPA-829
    @Test
    public void translatesContainsToMemberOf() {
        List<User> singers = userRepository.findByRolesContaining(singer);
        Assert.assertThat(singers, hasSize(2));
        Assert.assertThat(singers, hasItems(dave, carter));
        Assert.assertThat(userRepository.findByRolesContaining(drummer), contains(carter));
    }

    // DATAJPA-829
    @Test
    public void translatesNotContainsToNotMemberOf() {
        Assert.assertThat(userRepository.findByRolesNotContaining(drummer), hasItems(dave, oliver));
    }

    // DATAJPA-974
    @Test
    public void executesQueryWithProjectionContainingReferenceToPluralAttribute() {
        Assert.assertThat(userRepository.findRolesAndFirstnameBy(), is(notNullValue()));
    }

    // DATAJPA-1023, DATACMNS-959
    @Test(expected = InvalidDataAccessApiUsageException.class)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void rejectsStreamExecutionIfNoSurroundingTransactionActive() {
        userRepository.findAllByCustomQueryAndStream();
    }

    // DATAJPA-1334
    @Test
    public void executesNamedQueryWithConstructorExpression() {
        userRepository.findByNamedQueryWithConstructorExpression();
    }
}

