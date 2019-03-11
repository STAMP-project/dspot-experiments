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


import Direction.DESC;
import StringMatcher.ENDING;
import StringMatcher.REGEX;
import StringMatcher.STARTING;
import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.sample.Address;
import org.springframework.data.jpa.domain.sample.Role;
import org.springframework.data.jpa.domain.sample.SpecialUser;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.domain.sample.UserSpecifications;
import org.springframework.data.jpa.repository.sample.UserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.data.jpa.repository.sample.SampleEvaluationContextExtension.SampleSecurityContextHolder.getCurrent;


/**
 * Base integration test class for {@code UserRepository}. Loads a basic (non-namespace) Spring configuration file as
 * well as Hibernate configuration to execute tests.
 * <p>
 * To test further persistence providers subclass this class and provide a custom provider configuration.
 *
 * @author Oliver Gierke
 * @author Kevin Raymond
 * @author Thomas Darimont
 * @author Christoph Strobl
 * @author Mark Paluch
 * @author Kevin Peters
 * @author Jens Schauder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application-context.xml")
@Transactional
public class UserRepositoryTests {
    @PersistenceContext
    EntityManager em;

    // CUT
    @Autowired
    UserRepository repository;

    User firstUser;

    User secondUser;

    User thirdUser;

    // Test fixture
    User fourthUser;

    Integer id;

    Role adminRole;

    @Test
    public void testCreation() {
        Query countQuery = em.createQuery("select count(u) from User u");
        Long before = ((Long) (countQuery.getSingleResult()));
        flushTestUsers();
        assertThat(((Long) (countQuery.getSingleResult()))).isEqualTo((before + 4L));
    }

    @Test
    public void testRead() throws Exception {
        flushTestUsers();
        assertThat(repository.findById(id)).map(User::getFirstname).contains(firstUser.getFirstname());
    }

    @Test
    public void findsAllByGivenIds() {
        flushTestUsers();
        assertThat(repository.findAllById(Arrays.asList(firstUser.getId(), secondUser.getId()))).contains(firstUser, secondUser);
    }

    @Test
    public void testReadByIdReturnsNullForNotFoundEntities() {
        flushTestUsers();
        assertThat(repository.findById(((id) * 27))).isNotPresent();
    }

    @Test
    public void savesCollectionCorrectly() throws Exception {
        assertThat(saveAll(Arrays.asList(firstUser, secondUser, thirdUser))).hasSize(3).contains(firstUser, secondUser, thirdUser);
    }

    @Test
    public void savingEmptyCollectionIsNoOp() throws Exception {
        assertThat(saveAll(new ArrayList())).isEmpty();
    }

    @Test
    public void testUpdate() {
        flushTestUsers();
        User foundPerson = repository.findById(id).get();
        foundPerson.setLastname("Schlicht");
        assertThat(repository.findById(id)).map(User::getFirstname).contains(foundPerson.getFirstname());
    }

    @Test
    public void existReturnsWhetherAnEntityCanBeLoaded() throws Exception {
        flushTestUsers();
        assertThat(existsById(id)).isTrue();
        assertThat(repository.existsById(((id) * 27))).isFalse();
    }

    @Test
    public void deletesAUserById() {
        flushTestUsers();
        repository.deleteById(firstUser.getId());
        assertThat(existsById(id)).isFalse();
        assertThat(repository.findById(id)).isNotPresent();
    }

    @Test
    public void testDelete() {
        flushTestUsers();
        delete(firstUser);
        assertThat(existsById(id)).isFalse();
        assertThat(repository.findById(id)).isNotPresent();
    }

    @Test
    public void returnsAllSortedCorrectly() throws Exception {
        flushTestUsers();
        assertThat(repository.findAll(Sort.by(ASC, "lastname"))).hasSize(4).containsExactly(secondUser, firstUser, thirdUser, fourthUser);
    }

    // DATAJPA-296
    @Test
    public void returnsAllIgnoreCaseSortedCorrectly() throws Exception {
        flushTestUsers();
        Order order = ignoreCase();
        // 
        // 
        assertThat(repository.findAll(Sort.by(order))).hasSize(4).containsExactly(thirdUser, secondUser, fourthUser, firstUser);
    }

    @Test
    public void deleteColletionOfEntities() {
        flushTestUsers();
        long before = count();
        repository.deleteAll(Arrays.asList(firstUser, secondUser));
        assertThat(existsById(firstUser.getId())).isFalse();
        assertThat(existsById(secondUser.getId())).isFalse();
        assertThat(repository.count()).isEqualTo((before - 2));
    }

    @Test
    public void batchDeleteColletionOfEntities() {
        flushTestUsers();
        long before = count();
        deleteInBatch(Arrays.asList(firstUser, secondUser));
        assertThat(existsById(firstUser.getId())).isFalse();
        assertThat(existsById(secondUser.getId())).isFalse();
        assertThat(repository.count()).isEqualTo((before - 2));
    }

    @Test
    public void deleteEmptyCollectionDoesNotDeleteAnything() {
        assertDeleteCallDoesNotDeleteAnything(new ArrayList<User>());
    }

    @Test
    public void executesManipulatingQuery() throws Exception {
        flushTestUsers();
        repository.renameAllUsersTo("newLastname");
        long expected = count();
        assertThat(repository.findByLastname("newLastname").size()).isEqualTo(Long.valueOf(expected).intValue());
    }

    @Test
    public void testFinderInvocationWithNullParameter() {
        flushTestUsers();
        repository.findByLastname(((String) (null)));
    }

    @Test
    public void testFindByLastname() throws Exception {
        flushTestUsers();
        assertThat(repository.findByLastname("Gierke")).containsOnly(firstUser);
    }

    /**
     * Tests, that searching by the email address of the reference user returns exactly that instance.
     */
    @Test
    public void testFindByEmailAddress() throws Exception {
        flushTestUsers();
        assertThat(repository.findByEmailAddress("gierke@synyx.de")).isEqualTo(firstUser);
    }

    /**
     * Tests reading all users.
     */
    @Test
    public void testReadAll() {
        flushTestUsers();
        assertThat(repository.count()).isEqualTo(4L);
        assertThat(findAll()).contains(firstUser, secondUser, thirdUser, fourthUser);
    }

    /**
     * Tests that all users get deleted by triggering {@link UserRepository#deleteAll()}.
     */
    @Test
    public void deleteAll() throws Exception {
        flushTestUsers();
        repository.deleteAll();
        assertThat(repository.count()).isZero();
    }

    // DATAJPA-137
    @Test
    public void deleteAllInBatch() {
        flushTestUsers();
        repository.deleteAllInBatch();
        assertThat(repository.count()).isZero();
    }

    /**
     * Tests cascading persistence.
     */
    @Test
    public void testCascadesPersisting() {
        // Create link prior to persisting
        firstUser.addColleague(secondUser);
        // Persist
        flushTestUsers();
        // Fetches first user from database
        User firstReferenceUser = repository.findById(firstUser.getId()).get();
        assertThat(firstReferenceUser).isEqualTo(firstUser);
        // Fetch colleagues and assert link
        Set<User> colleagues = firstReferenceUser.getColleagues();
        assertThat(colleagues).containsOnly(secondUser);
    }

    /**
     * Tests, that persisting a relationsship without cascade attributes throws a {@code DataAccessException}.
     */
    @Test(expected = DataAccessException.class)
    public void testPreventsCascadingRolePersisting() {
        firstUser.addRole(new Role("USER"));
        flushTestUsers();
    }

    /**
     * Tests cascading on {@literal merge} operation.
     */
    @Test
    public void testMergingCascadesCollegueas() {
        firstUser.addColleague(secondUser);
        flushTestUsers();
        firstUser.addColleague(new User("Florian", "Hopf", "hopf@synyx.de"));
        firstUser = save(firstUser);
        User reference = repository.findById(firstUser.getId()).get();
        Set<User> colleagues = reference.getColleagues();
        assertThat(colleagues).hasSize(2);
    }

    @Test
    public void testCountsCorrectly() {
        long count = count();
        User user = new User();
        user.setEmailAddress("gierke@synyx.de");
        repository.save(user);
        assertThat(repository.count()).isEqualTo((count + 1));
    }

    @Test
    public void testInvocationOfCustomImplementation() {
        repository.someCustomMethod(new User());
    }

    @Test
    public void testOverwritingFinder() {
        repository.findByOverrridingMethod();
    }

    @Test
    public void testUsesQueryAnnotation() {
        assertThat(repository.findByAnnotatedQuery("gierke@synyx.de")).isNull();
    }

    @Test
    public void testExecutionOfProjectingMethod() {
        flushTestUsers();
        assertThat(repository.countWithFirstname("Oliver")).isEqualTo(1L);
    }

    @Test
    public void executesSpecificationCorrectly() {
        flushTestUsers();
        assertThat(repository.findAll(where(UserSpecifications.userHasFirstname("Oliver")))).hasSize(1);
    }

    @Test
    public void executesSingleEntitySpecificationCorrectly() throws Exception {
        flushTestUsers();
        assertThat(repository.findOne(UserSpecifications.userHasFirstname("Oliver"))).contains(firstUser);
    }

    @Test
    public void returnsNullIfNoEntityFoundForSingleEntitySpecification() throws Exception {
        flushTestUsers();
        assertThat(repository.findOne(UserSpecifications.userHasLastname("Beauford"))).isNotPresent();
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void throwsExceptionForUnderSpecifiedSingleEntitySpecification() {
        flushTestUsers();
        repository.findOne(UserSpecifications.userHasFirstnameLike("e"));
    }

    @Test
    public void executesCombinedSpecificationsCorrectly() {
        flushTestUsers();
        Specification.Specification<User> spec = UserSpecifications.userHasFirstname("Oliver").or(UserSpecifications.userHasLastname("Arrasz"));
        assertThat(repository.findAll(spec)).hasSize(2);
    }

    // DATAJPA-253
    @Test
    public void executesNegatingSpecificationCorrectly() {
        flushTestUsers();
        Specification.Specification<User> spec = Specification.not(UserSpecifications.userHasFirstname("Oliver")).and(UserSpecifications.userHasLastname("Arrasz"));
        assertThat(repository.findAll(spec)).containsOnly(secondUser);
    }

    @Test
    public void executesCombinedSpecificationsWithPageableCorrectly() {
        flushTestUsers();
        Specification.Specification<User> spec = UserSpecifications.userHasFirstname("Oliver").or(UserSpecifications.userHasLastname("Arrasz"));
        Page<User> users = repository.findAll(spec, PageRequest.of(0, 1));
        assertThat(users.getSize()).isEqualTo(1);
        assertThat(users.hasPrevious()).isFalse();
        assertThat(users.getTotalElements()).isEqualTo(2L);
    }

    @Test
    public void executesMethodWithAnnotatedNamedParametersCorrectly() throws Exception {
        firstUser = save(firstUser);
        secondUser = save(secondUser);
        assertThat(repository.findByLastnameOrFirstname("Oliver", "Arrasz")).contains(firstUser, secondUser);
    }

    @Test
    public void executesMethodWithNamedParametersCorrectlyOnMethodsWithQueryCreation() throws Exception {
        firstUser = save(firstUser);
        secondUser = save(secondUser);
        assertThat(repository.findByFirstnameOrLastname("Oliver", "Arrasz")).containsOnly(firstUser, secondUser);
    }

    @Test
    public void executesLikeAndOrderByCorrectly() throws Exception {
        flushTestUsers();
        assertThat(repository.findByLastnameLikeOrderByFirstnameDesc("%r%")).hasSize(3).containsExactly(fourthUser, firstUser, secondUser);
    }

    @Test
    public void executesNotLikeCorrectly() throws Exception {
        flushTestUsers();
        assertThat(repository.findByLastnameNotLike("%er%")).containsOnly(secondUser, thirdUser, fourthUser);
    }

    @Test
    public void executesSimpleNotCorrectly() throws Exception {
        flushTestUsers();
        assertThat(repository.findByLastnameNot("Gierke")).containsOnly(secondUser, thirdUser, fourthUser);
    }

    @Test
    public void returnsSameListIfNoSpecGiven() throws Exception {
        flushTestUsers();
        UserRepositoryTests.assertSameElements(findAll(), repository.findAll(((Specification.Specification<User>) (null))));
    }

    @Test
    public void returnsSameListIfNoSortIsGiven() throws Exception {
        flushTestUsers();
        UserRepositoryTests.assertSameElements(repository.findAll(Sort.unsorted()), findAll());
    }

    @Test
    public void returnsSamePageIfNoSpecGiven() throws Exception {
        Pageable pageable = PageRequest.of(0, 1);
        flushTestUsers();
        assertThat(repository.findAll(((Specification.Specification<User>) (null)), pageable)).isEqualTo(repository.findAll(pageable));
    }

    @Test
    public void returnsAllAsPageIfNoPageableIsGiven() throws Exception {
        flushTestUsers();
        assertThat(repository.findAll(Pageable.unpaged())).isEqualTo(new org.springframework.data.domain.PageImpl(findAll()));
    }

    @Test
    public void removeDetachedObject() throws Exception {
        flushTestUsers();
        em.detach(firstUser);
        delete(firstUser);
        assertThat(repository.count()).isEqualTo(3L);
    }

    @Test
    public void executesPagedSpecificationsCorrectly() throws Exception {
        Page<User> result = executeSpecWithSort(Sort.unsorted());
        assertThat(result.getContent()).isSubsetOf(firstUser, thirdUser);
    }

    @Test
    public void executesPagedSpecificationsWithSortCorrectly() throws Exception {
        Page<User> result = executeSpecWithSort(Sort.by(Direction.ASC, "lastname"));
        assertThat(result.getContent()).contains(firstUser).doesNotContain(secondUser, thirdUser);
    }

    @Test
    public void executesPagedSpecificationWithSortCorrectly2() throws Exception {
        Page<User> result = executeSpecWithSort(Sort.by(DESC, "lastname"));
        assertThat(result.getContent()).contains(thirdUser).doesNotContain(secondUser, firstUser);
    }

    @Test
    public void executesQueryMethodWithDeepTraversalCorrectly() throws Exception {
        flushTestUsers();
        firstUser.setManager(secondUser);
        thirdUser.setManager(firstUser);
        saveAll(Arrays.asList(firstUser, thirdUser));
        assertThat(repository.findByManagerLastname("Arrasz")).containsOnly(firstUser);
        assertThat(repository.findByManagerLastname("Gierke")).containsOnly(thirdUser);
    }

    @Test
    public void executesFindByColleaguesLastnameCorrectly() throws Exception {
        flushTestUsers();
        firstUser.addColleague(secondUser);
        thirdUser.addColleague(firstUser);
        saveAll(Arrays.asList(firstUser, thirdUser));
        assertThat(repository.findByColleaguesLastname(secondUser.getLastname())).containsOnly(firstUser);
        assertThat(repository.findByColleaguesLastname("Gierke")).containsOnly(thirdUser, secondUser);
    }

    @Test
    public void executesFindByNotNullLastnameCorrectly() throws Exception {
        flushTestUsers();
        assertThat(repository.findByLastnameNotNull()).containsOnly(firstUser, secondUser, thirdUser, fourthUser);
    }

    @Test
    public void executesFindByNullLastnameCorrectly() throws Exception {
        flushTestUsers();
        User forthUser = save(new User("Foo", null, "email@address.com"));
        assertThat(repository.findByLastnameNull()).containsOnly(forthUser);
    }

    @Test
    public void findsSortedByLastname() throws Exception {
        flushTestUsers();
        assertThat(repository.findByEmailAddressLike("%@%", Sort.by(Direction.ASC, "lastname"))).containsExactly(secondUser, firstUser, thirdUser, fourthUser);
    }

    @Test
    public void findsUsersBySpringDataNamedQuery() {
        flushTestUsers();
        assertThat(repository.findBySpringDataNamedQuery("Gierke")).containsOnly(firstUser);
    }

    // DATADOC-86
    @Test
    public void readsPageWithGroupByClauseCorrectly() {
        flushTestUsers();
        Page<String> result = repository.findByLastnameGrouped(PageRequest.of(0, 10));
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    public void executesLessThatOrEqualQueriesCorrectly() {
        flushTestUsers();
        assertThat(repository.findByAgeLessThanEqual(35)).containsOnly(firstUser, secondUser, fourthUser);
    }

    @Test
    public void executesGreaterThatOrEqualQueriesCorrectly() {
        flushTestUsers();
        assertThat(repository.findByAgeGreaterThanEqual(35)).containsOnly(secondUser, thirdUser);
    }

    // DATAJPA-117
    @Test
    public void executesNativeQueryCorrectly() {
        flushTestUsers();
        assertThat(repository.findNativeByLastname("Matthews")).containsOnly(thirdUser);
    }

    // DATAJPA-132
    @Test
    public void executesFinderWithTrueKeywordCorrectly() {
        flushTestUsers();
        firstUser.setActive(false);
        repository.save(firstUser);
        assertThat(repository.findByActiveTrue()).containsOnly(secondUser, thirdUser, fourthUser);
    }

    // DATAJPA-132
    @Test
    public void executesFinderWithFalseKeywordCorrectly() {
        flushTestUsers();
        firstUser.setActive(false);
        repository.save(firstUser);
        assertThat(repository.findByActiveFalse()).containsOnly(firstUser);
    }

    /**
     * Ignored until the query declaration is supported by OpenJPA.
     */
    @Test
    public void executesAnnotatedCollectionMethodCorrectly() {
        flushTestUsers();
        firstUser.addColleague(thirdUser);
        repository.save(firstUser);
        List<User> result = repository.findColleaguesFor(firstUser);
        assertThat(result).containsOnly(thirdUser);
    }

    // DATAJPA-188
    @Test
    public void executesFinderWithAfterKeywordCorrectly() {
        flushTestUsers();
        assertThat(repository.findByCreatedAtAfter(secondUser.getCreatedAt())).containsOnly(thirdUser, fourthUser);
    }

    // DATAJPA-188
    @Test
    public void executesFinderWithBeforeKeywordCorrectly() {
        flushTestUsers();
        assertThat(repository.findByCreatedAtBefore(thirdUser.getCreatedAt())).containsOnly(firstUser, secondUser);
    }

    // DATAJPA-180
    @Test
    public void executesFinderWithStartingWithCorrectly() {
        flushTestUsers();
        assertThat(repository.findByFirstnameStartingWith("Oli")).containsOnly(firstUser);
    }

    // DATAJPA-180
    @Test
    public void executesFinderWithEndingWithCorrectly() {
        flushTestUsers();
        assertThat(repository.findByFirstnameEndingWith("er")).containsOnly(firstUser);
    }

    // DATAJPA-180
    @Test
    public void executesFinderWithContainingCorrectly() {
        flushTestUsers();
        assertThat(repository.findByFirstnameContaining("a")).containsOnly(secondUser, thirdUser);
    }

    // DATAJPA-201
    @Test
    public void allowsExecutingPageableMethodWithUnpagedArgument() {
        flushTestUsers();
        assertThat(repository.findByFirstname("Oliver", null)).containsOnly(firstUser);
        Page<User> page = repository.findByFirstnameIn(Pageable.unpaged(), "Oliver");
        assertThat(page.getNumberOfElements()).isEqualTo(1);
        assertThat(page.getContent()).contains(firstUser);
        page = repository.findAll(Pageable.unpaged());
        assertThat(page.getNumberOfElements()).isEqualTo(4);
        assertThat(page.getContent()).contains(firstUser, secondUser, thirdUser, fourthUser);
    }

    // DATAJPA-207
    @Test
    public void executesNativeQueryForNonEntitiesCorrectly() {
        flushTestUsers();
        List<Integer> result = repository.findOnesByNativeQuery();
        assertThat(result.size()).isEqualTo(4);
        assertThat(result).contains(1);
    }

    // DATAJPA-232
    @Test
    public void handlesIterableOfIdsCorrectly() {
        flushTestUsers();
        Set<Integer> set = new HashSet<>();
        set.add(firstUser.getId());
        set.add(secondUser.getId());
        assertThat(findAllById(set)).containsOnly(firstUser, secondUser);
    }

    @Test
    public void ordersByReferencedEntityCorrectly() {
        flushTestUsers();
        firstUser.setManager(thirdUser);
        repository.save(firstUser);
        Page<User> all = repository.findAll(PageRequest.of(0, 10, Sort.by("manager.id")));
        assertThat(all.getContent().isEmpty()).isFalse();
    }

    // DATAJPA-252
    @Test
    public void bindsSortingToOuterJoinCorrectly() {
        flushTestUsers();
        // Managers not set, make sure adding the sort does not rule out those Users
        Page<User> result = repository.findAllPaged(PageRequest.of(0, 10, Sort.by("manager.lastname")));
        assertThat(result.getContent()).hasSize(((int) (repository.count())));
    }

    // DATAJPA-277
    @Test
    public void doesNotDropNullValuesOnPagedSpecificationExecution() {
        flushTestUsers();
        Page<User> page = repository.findAll(new Specification.Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("lastname"), "Gierke");
            }
        }, PageRequest.of(0, 20, Sort.by("manager.lastname")));
        assertThat(page.getNumberOfElements()).isEqualTo(1);
        assertThat(page).containsOnly(firstUser);
    }

    // DATAJPA-346
    @Test
    public void shouldGenerateLeftOuterJoinInfindAllWithPaginationAndSortOnNestedPropertyPath() {
        firstUser.setManager(null);
        secondUser.setManager(null);
        thirdUser.setManager(firstUser);// manager Oliver

        fourthUser.setManager(secondUser);// manager Joachim

        flushTestUsers();
        Page<User> pages = repository.findAll(PageRequest.of(0, 4, Sort.by(Sort.Direction.ASC, "manager.firstname")));
        assertThat(pages.getSize()).isEqualTo(4);
        assertThat(pages.getContent().get(0).getManager()).isNull();
        assertThat(pages.getContent().get(1).getManager()).isNull();
        assertThat(pages.getContent().get(2).getManager().getFirstname()).isEqualTo("Joachim");
        assertThat(pages.getContent().get(3).getManager().getFirstname()).isEqualTo("Oliver");
        assertThat(pages.getTotalElements()).isEqualTo(4L);
    }

    // DATAJPA-292
    @Test
    public void executesManualQueryWithPositionLikeExpressionCorrectly() {
        flushTestUsers();
        List<User> result = repository.findByFirstnameLike("Da");
        assertThat(result).containsOnly(thirdUser);
    }

    // DATAJPA-292
    @Test
    public void executesManualQueryWithNamedLikeExpressionCorrectly() {
        flushTestUsers();
        List<User> result = repository.findByFirstnameLikeNamed("Da");
        assertThat(result).containsOnly(thirdUser);
    }

    // DATAJPA-231
    @Test
    public void executesDerivedCountQueryToLong() {
        flushTestUsers();
        assertThat(repository.countByLastname("Matthews")).isEqualTo(1L);
    }

    // DATAJPA-231
    @Test
    public void executesDerivedCountQueryToInt() {
        flushTestUsers();
        assertThat(repository.countUsersByFirstname("Dave")).isEqualTo(1);
    }

    // DATAJPA-231
    @Test
    public void executesDerivedExistsQuery() {
        flushTestUsers();
        assertThat(repository.existsByLastname("Matthews")).isEqualTo(true);
        assertThat(repository.existsByLastname("Hans Peter")).isEqualTo(false);
    }

    // DATAJPA-332, DATAJPA-1168
    @Test
    public void findAllReturnsEmptyIterableIfNoIdsGiven() {
        assertThat(findAllById(Collections.<Integer>emptySet())).isEmpty();
    }

    // DATAJPA-391
    @Test
    public void executesManuallyDefinedQueryWithFieldProjection() {
        flushTestUsers();
        List<String> lastname = repository.findFirstnamesByLastname("Matthews");
        assertThat(lastname).containsOnly("Dave");
    }

    // DATAJPA-83
    @Test
    public void looksUpEntityReference() {
        flushTestUsers();
        User result = getOne(firstUser.getId());
        assertThat(result).isEqualTo(firstUser);
    }

    // DATAJPA-415
    @Test
    public void invokesQueryWithVarargsParametersCorrectly() {
        flushTestUsers();
        Collection<User> result = repository.findByIdIn(firstUser.getId(), secondUser.getId());
        assertThat(result).containsOnly(firstUser, secondUser);
    }

    // DATAJPA-415
    @Test
    public void shouldSupportModifyingQueryWithVarArgs() {
        flushTestUsers();
        repository.updateUserActiveState(false, firstUser.getId(), secondUser.getId(), thirdUser.getId(), fourthUser.getId());
        long expectedCount = count();
        assertThat(repository.findByActiveFalse().size()).isEqualTo(((int) (expectedCount)));
        assertThat(repository.findByActiveTrue().size()).isEqualTo(0);
    }

    // DATAJPA-405
    @Test
    public void executesFinderWithOrderClauseOnly() {
        flushTestUsers();
        assertThat(repository.findAllByOrderByLastnameAsc()).containsOnly(secondUser, firstUser, thirdUser, fourthUser);
    }

    // DATAJPA-427
    @Test
    public void sortByAssociationPropertyShouldUseLeftOuterJoin() {
        secondUser.getColleagues().add(firstUser);
        fourthUser.getColleagues().add(thirdUser);
        flushTestUsers();
        List<User> result = repository.findAll(Sort.by(Sort.Direction.ASC, "colleagues.id"));
        assertThat(result).hasSize(4);
    }

    // DATAJPA-427
    @Test
    public void sortByAssociationPropertyInPageableShouldUseLeftOuterJoin() {
        secondUser.getColleagues().add(firstUser);
        fourthUser.getColleagues().add(thirdUser);
        flushTestUsers();
        Page<User> page = repository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "colleagues.id")));
        assertThat(page.getContent()).hasSize(4);
    }

    // DATAJPA-427
    @Test
    public void sortByEmbeddedProperty() {
        thirdUser.setAddress(new Address("Germany", "Saarbr?cken", "HaveItYourWay", "123"));
        flushTestUsers();
        Page<User> page = repository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "address.streetName")));
        assertThat(page.getContent()).hasSize(4);
        assertThat(page.getContent().get(3)).isEqualTo(thirdUser);
    }

    // DATAJPA-454
    @Test
    public void findsUserByBinaryDataReference() throws Exception {
        byte[] data = "Woho!!".getBytes("UTF-8");
        firstUser.setBinaryData(data);
        flushTestUsers();
        List<User> result = repository.findByBinaryData(data);
        assertThat(result).containsOnly(firstUser);
        assertThat(result.get(0).getBinaryData()).isEqualTo(data);
    }

    // DATAJPA-461
    @Test
    public void customFindByQueryWithPositionalVarargsParameters() {
        flushTestUsers();
        Collection<User> result = repository.findByIdsCustomWithPositionalVarArgs(firstUser.getId(), secondUser.getId());
        assertThat(result).containsOnly(firstUser, secondUser);
    }

    // DATAJPA-461
    @Test
    public void customFindByQueryWithNamedVarargsParameters() {
        flushTestUsers();
        Collection<User> result = repository.findByIdsCustomWithNamedVarArgs(firstUser.getId(), secondUser.getId());
        assertThat(result).containsOnly(firstUser, secondUser);
    }

    // DATAJPA-464
    @Test
    public void saveAndFlushShouldSupportReturningSubTypesOfRepositoryEntity() {
        repository.deleteAll();
        SpecialUser user = new SpecialUser();
        user.setFirstname("Thomas");
        user.setEmailAddress("thomas@example.org");
        SpecialUser savedUser = saveAndFlush(user);
        assertThat(user.getFirstname()).isEqualTo(savedUser.getFirstname());
        assertThat(user.getEmailAddress()).isEqualTo(savedUser.getEmailAddress());
    }

    // DATAJPA-218
    @Test
    public void findAllByUntypedExampleShouldReturnSubTypesOfRepositoryEntity() {
        flushTestUsers();
        SpecialUser user = new SpecialUser();
        user.setFirstname("Thomas");
        user.setEmailAddress("thomas@example.org");
        repository.saveAndFlush(user);
        List<User> result = repository.findAll(Example.of(new User(), ExampleMatcher.matching().withIgnorePaths("age", "createdAt", "dateOfBirth")));
        assertThat(result).hasSize(5);
    }

    // DATAJPA-218
    @Test
    public void findAllByTypedUserExampleShouldReturnSubTypesOfRepositoryEntity() {
        flushTestUsers();
        SpecialUser user = new SpecialUser();
        user.setFirstname("Thomas");
        user.setEmailAddress("thomas@example.org");
        repository.saveAndFlush(user);
        Example<User> example = Example.of(new User(), matching().withIgnorePaths("age", "createdAt", "dateOfBirth"));
        List<User> result = repository.findAll(example);
        assertThat(result).hasSize(5);
    }

    // DATAJPA-218
    @Test
    public void findAllByTypedSpecialUserExampleShouldReturnSubTypesOfRepositoryEntity() {
        flushTestUsers();
        SpecialUser user = new SpecialUser();
        user.setFirstname("Thomas");
        user.setEmailAddress("thomas@example.org");
        repository.saveAndFlush(user);
        Example<SpecialUser> example = Example.of(new SpecialUser(), matching().withIgnorePaths("age", "createdAt", "dateOfBirth"));
        List<SpecialUser> result = repository.findAll(example);
        assertThat(result).hasSize(1);
    }

    // DATAJPA-491
    @Test
    public void sortByNestedAssociationPropertyWithSortInPageable() {
        firstUser.setManager(thirdUser);
        thirdUser.setManager(fourthUser);
        flushTestUsers();
        Page<User> page = repository.findAll(// 
        PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "manager.manager.firstname")));
        assertThat(page.getContent()).hasSize(4);
        assertThat(page.getContent().get(3)).isEqualTo(firstUser);
    }

    // DATAJPA-510
    @Test
    public void sortByNestedAssociationPropertyWithSortOrderIgnoreCaseInPageable() {
        firstUser.setManager(thirdUser);
        thirdUser.setManager(fourthUser);
        flushTestUsers();
        Page<User> page = repository.findAll(// 
        PageRequest.of(0, 10, Sort.by(new Sort.Order(Direction.ASC, "manager.manager.firstname").ignoreCase())));
        assertThat(page.getContent()).hasSize(4);
        assertThat(page.getContent().get(3)).isEqualTo(firstUser);
    }

    // DATAJPA-496
    @Test
    public void findByElementCollectionAttribute() {
        firstUser.getAttributes().add("cool");
        secondUser.getAttributes().add("hip");
        thirdUser.getAttributes().add("rockstar");
        flushTestUsers();
        List<User> result = repository.findByAttributesIn(new HashSet<>(Arrays.asList("cool", "hip")));
        assertThat(result).containsOnly(firstUser, secondUser);
    }

    // DATAJPA-460
    @Test
    public void deleteByShouldReturnListOfDeletedElementsWhenRetunTypeIsCollectionLike() {
        flushTestUsers();
        List<User> result = repository.deleteByLastname(firstUser.getLastname());
        assertThat(result).containsOnly(firstUser);
    }

    // DATAJPA-460
    @Test
    public void deleteByShouldRemoveElementsMatchingDerivedQuery() {
        flushTestUsers();
        repository.deleteByLastname(firstUser.getLastname());
        assertThat(repository.countByLastname(firstUser.getLastname())).isEqualTo(0L);
    }

    // DATAJPA-460
    @Test
    public void deleteByShouldReturnNumberOfEntitiesRemovedIfReturnTypeIsLong() {
        flushTestUsers();
        assertThat(repository.removeByLastname(firstUser.getLastname())).isEqualTo(1L);
    }

    // DATAJPA-460
    @Test
    public void deleteByShouldReturnZeroInCaseNoEntityHasBeenRemovedAndReturnTypeIsNumber() {
        flushTestUsers();
        assertThat(repository.removeByLastname("bubu")).isEqualTo(0L);
    }

    // DATAJPA-460
    @Test
    public void deleteByShouldReturnEmptyListInCaseNoEntityHasBeenRemovedAndReturnTypeIsCollectionLike() {
        flushTestUsers();
        assertThat(repository.deleteByLastname("dorfuaeB")).isEmpty();
    }

    // DATAJPA-506
    @Test
    public void findBinaryDataByIdNative() throws Exception {
        byte[] data = "Woho!!".getBytes("UTF-8");
        firstUser.setBinaryData(data);
        flushTestUsers();
        byte[] result = repository.findBinaryDataByIdNative(firstUser.getId());
        assertThat(result).isEqualTo(data);
        assertThat(result.length).isEqualTo(data.length);
    }

    // DATAJPA-456
    @Test
    public void findPaginatedExplicitQueryWithCountQueryProjection() {
        firstUser.setFirstname(null);
        flushTestUsers();
        Page<User> result = repository.findAllByFirstnameLike("", PageRequest.of(0, 10));
        assertThat(result.getContent().size()).isEqualTo(3);
    }

    // DATAJPA-456
    @Test
    public void findPaginatedNamedQueryWithCountQueryProjection() {
        flushTestUsers();
        Page<User> result = repository.findByNamedQueryAndCountProjection("Gierke", PageRequest.of(0, 10));
        assertThat(result.getContent().size()).isEqualTo(1);
    }

    // DATAJPA-551
    @Test
    public void findOldestUser() {
        flushTestUsers();
        User oldest = thirdUser;
        assertThat(repository.findFirstByOrderByAgeDesc()).isEqualTo(oldest);
        assertThat(repository.findFirst1ByOrderByAgeDesc()).isEqualTo(oldest);
    }

    // DATAJPA-551
    @Test
    public void findYoungestUser() {
        flushTestUsers();
        User youngest = firstUser;
        assertThat(repository.findTopByOrderByAgeAsc()).isEqualTo(youngest);
        assertThat(repository.findTop1ByOrderByAgeAsc()).isEqualTo(youngest);
    }

    // DATAJPA-551
    @Test
    public void find2OldestUsers() {
        flushTestUsers();
        User oldest1 = thirdUser;
        User oldest2 = secondUser;
        assertThat(repository.findFirst2ByOrderByAgeDesc()).contains(oldest1, oldest2);
        assertThat(repository.findTop2ByOrderByAgeDesc()).contains(oldest1, oldest2);
    }

    // DATAJPA-551
    @Test
    public void find2YoungestUsers() {
        flushTestUsers();
        User youngest1 = firstUser;
        User youngest2 = fourthUser;
        assertThat(repository.findFirst2UsersBy(Sort.by(ASC, "age"))).contains(youngest1, youngest2);
        assertThat(repository.findTop2UsersBy(Sort.by(ASC, "age"))).contains(youngest1, youngest2);
    }

    // DATAJPA-551
    @Test
    public void find3YoungestUsersPageableWithPageSize2() {
        flushTestUsers();
        User youngest1 = firstUser;
        User youngest2 = fourthUser;
        User youngest3 = secondUser;
        Page<User> firstPage = repository.findFirst3UsersBy(PageRequest.of(0, 2, ASC, "age"));
        assertThat(firstPage.getContent()).contains(youngest1, youngest2);
        Page<User> secondPage = repository.findFirst3UsersBy(PageRequest.of(1, 2, ASC, "age"));
        assertThat(secondPage.getContent()).contains(youngest3);
    }

    // DATAJPA-551
    @Test
    public void find2YoungestUsersPageableWithPageSize3() {
        flushTestUsers();
        User youngest1 = firstUser;
        User youngest2 = fourthUser;
        User youngest3 = secondUser;
        Page<User> firstPage = repository.findFirst2UsersBy(PageRequest.of(0, 3, ASC, "age"));
        assertThat(firstPage.getContent()).contains(youngest1, youngest2);
        Page<User> secondPage = repository.findFirst2UsersBy(PageRequest.of(1, 3, ASC, "age"));
        assertThat(secondPage.getContent()).contains(youngest3);
    }

    // DATAJPA-551
    @Test
    public void find3YoungestUsersPageableWithPageSize2Sliced() {
        flushTestUsers();
        User youngest1 = firstUser;
        User youngest2 = fourthUser;
        User youngest3 = secondUser;
        Slice<User> firstPage = repository.findTop3UsersBy(PageRequest.of(0, 2, ASC, "age"));
        assertThat(firstPage.getContent()).contains(youngest1, youngest2);
        Slice<User> secondPage = repository.findTop3UsersBy(PageRequest.of(1, 2, ASC, "age"));
        assertThat(secondPage.getContent()).contains(youngest3);
    }

    // DATAJPA-551
    @Test
    public void find2YoungestUsersPageableWithPageSize3Sliced() {
        flushTestUsers();
        User youngest1 = firstUser;
        User youngest2 = fourthUser;
        User youngest3 = secondUser;
        Slice<User> firstPage = repository.findTop2UsersBy(PageRequest.of(0, 3, ASC, "age"));
        assertThat(firstPage.getContent()).contains(youngest1, youngest2);
        Slice<User> secondPage = repository.findTop2UsersBy(PageRequest.of(1, 3, ASC, "age"));
        assertThat(secondPage.getContent()).contains(youngest3);
    }

    // DATAJPA-912
    @Test
    public void pageableQueryReportsTotalFromResult() {
        flushTestUsers();
        Page<User> firstPage = repository.findAll(PageRequest.of(0, 10));
        assertThat(firstPage.getContent()).hasSize(4);
        assertThat(firstPage.getTotalElements()).isEqualTo(4L);
        Page<User> secondPage = repository.findAll(PageRequest.of(1, 3));
        assertThat(secondPage.getContent()).hasSize(1);
        assertThat(secondPage.getTotalElements()).isEqualTo(4L);
    }

    // DATAJPA-912
    @Test
    public void pageableQueryReportsTotalFromCount() {
        flushTestUsers();
        Page<User> firstPage = repository.findAll(PageRequest.of(0, 4));
        assertThat(firstPage.getContent()).hasSize(4);
        assertThat(firstPage.getTotalElements()).isEqualTo(4L);
        Page<User> secondPage = repository.findAll(PageRequest.of(10, 10));
        assertThat(secondPage.getContent()).hasSize(0);
        assertThat(secondPage.getTotalElements()).isEqualTo(4L);
    }

    // DATAJPA-506
    @Test
    public void invokesQueryWithWrapperType() {
        flushTestUsers();
        Optional<User> result = repository.findOptionalByEmailAddress("gierke@synyx.de");
        assertThat(result.isPresent()).isEqualTo(true);
        assertThat(result.get()).isEqualTo(firstUser);
    }

    // DATAJPA-564
    @Test
    public void shouldFindUserByFirstnameAndLastnameWithSpelExpressionInStringBasedQuery() {
        flushTestUsers();
        List<User> users = repository.findByFirstnameAndLastnameWithSpelExpression("Oliver", "ierk");
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-564
    @Test
    public void shouldFindUserByLastnameWithSpelExpressionInStringBasedQuery() {
        flushTestUsers();
        List<User> users = repository.findByLastnameWithSpelExpression("ierk");
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-564
    @Test
    public void shouldFindBySpELExpressionWithoutArgumentsWithQuestionmark() {
        flushTestUsers();
        List<User> users = repository.findOliverBySpELExpressionWithoutArgumentsWithQuestionmark();
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-564
    @Test
    public void shouldFindBySpELExpressionWithoutArgumentsWithColon() {
        flushTestUsers();
        List<User> users = repository.findOliverBySpELExpressionWithoutArgumentsWithColon();
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-564
    @Test
    public void shouldFindUsersByAgeForSpELExpression() {
        flushTestUsers();
        List<User> users = repository.findUsersByAgeForSpELExpressionByIndexedParameter(35);
        assertThat(users).containsOnly(secondUser);
    }

    // DATAJPA-564
    @Test
    public void shouldfindUsersByFirstnameForSpELExpressionWithParameterNameVariableReference() {
        flushTestUsers();
        List<User> users = repository.findUsersByFirstnameForSpELExpression("Joachim");
        assertThat(users).containsOnly(secondUser);
    }

    // DATAJPA-564
    @Test
    public void shouldFindCurrentUserWithCustomQueryDependingOnSecurityContext() {
        flushTestUsers();
        getCurrent().setPrincipal(secondUser);
        List<User> users = repository.findCurrentUserWithCustomQuery();
        assertThat(users).containsOnly(secondUser);
        getCurrent().setPrincipal(firstUser);
        users = repository.findCurrentUserWithCustomQuery();
        assertThat(users).contains(firstUser);
    }

    // DATAJPA-564
    @Test
    public void shouldFindByFirstnameAndCurrentUserWithCustomQuery() {
        flushTestUsers();
        getCurrent().setPrincipal(secondUser);
        List<User> users = repository.findByFirstnameAndCurrentUserWithCustomQuery("Joachim");
        assertThat(users).containsOnly(secondUser);
    }

    // DATAJPA-564
    @Test
    public void shouldfindUsersByFirstnameForSpELExpressionOnlyWithParameterNameVariableReference() {
        flushTestUsers();
        List<User> users = repository.findUsersByFirstnameForSpELExpressionWithParameterVariableOnly("Joachim");
        assertThat(users).containsOnly(secondUser);
    }

    // DATAJPA-564
    @Test
    public void shouldfindUsersByFirstnameForSpELExpressionOnlyWithParameterIndexReference() {
        flushTestUsers();
        List<User> users = repository.findUsersByFirstnameForSpELExpressionWithParameterIndexOnly("Joachim");
        assertThat(users).containsOnly(secondUser);
    }

    // DATAJPA-564
    @Test
    public void shouldFindUsersInNativeQueryWithPagination() {
        flushTestUsers();
        Page<User> users = repository.findUsersInNativeQueryWithPagination(PageRequest.of(0, 3));
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(users.getContent()).extracting(User::getFirstname).containsExactly("Dave", "Joachim", "kevin");
        users = repository.findUsersInNativeQueryWithPagination(PageRequest.of(1, 3));
        softly.assertThat(users.getContent()).extracting(User::getFirstname).containsExactly("Oliver");
        softly.assertAll();
    }

    // DATAJPA-1140
    @Test
    public void shouldFindUsersByUserFirstnameAsSpELExpressionAndLastnameAsStringInStringBasedQuery() {
        flushTestUsers();
        List<User> users = repository.findUsersByUserFirstnameAsSpELExpressionAndLastnameAsString(firstUser, firstUser.getLastname());
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-1140
    @Test
    public void shouldFindUsersByFirstnameAsStringAndUserLastnameAsSpELExpressionInStringBasedQuery() {
        flushTestUsers();
        List<User> users = repository.findUsersByFirstnameAsStringAndUserLastnameAsSpELExpression(firstUser.getFirstname(), firstUser);
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-1140
    @Test
    public void shouldFindUsersByUserFirstnameAsSpELExpressionAndLastnameAsFakeSpELExpressionInStringBasedQuery() {
        flushTestUsers();
        List<User> users = repository.findUsersByUserFirstnameAsSpELExpressionAndLastnameAsFakeSpELExpression(firstUser, firstUser.getLastname());
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-1140
    @Test
    public void shouldFindUsersByFirstnameAsFakeSpELExpressionAndUserLastnameAsSpELExpressionInStringBasedQuery() {
        flushTestUsers();
        List<User> users = repository.findUsersByFirstnameAsFakeSpELExpressionAndUserLastnameAsSpELExpression(firstUser.getFirstname(), firstUser);
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-1140
    @Test
    public void shouldFindUsersByFirstnameWithLeadingPageableParameter() {
        flushTestUsers();
        List<User> users = repository.findUsersByFirstnamePaginated(PageRequest.of(0, 2), firstUser.getFirstname());
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-629
    @Test
    public void shouldfindUsersBySpELExpressionParametersWithSpelTemplateExpression() {
        flushTestUsers();
        List<User> users = repository.findUsersByFirstnameForSpELExpressionWithParameterIndexOnlyWithEntityExpression("Joachim", "Arrasz");
        assertThat(users).containsOnly(secondUser);
    }

    // DATAJPA-606
    @Test
    public void findByEmptyCollectionOfStrings() throws Exception {
        flushTestUsers();
        List<User> users = repository.findByAttributesIn(new HashSet<>());
        assertThat(users).hasSize(0);
    }

    // DATAJPA-606
    @Test
    public void findByEmptyCollectionOfIntegers() throws Exception {
        flushTestUsers();
        List<User> users = repository.findByAgeIn(Collections.emptyList());
        assertThat(users).hasSize(0);
    }

    // DATAJPA-606
    @Test
    public void findByEmptyArrayOfIntegers() throws Exception {
        flushTestUsers();
        List<User> users = repository.queryByAgeIn(new Integer[0]);
        assertThat(users).hasSize(0);
    }

    // DATAJPA-606
    @Test
    public void findByAgeWithEmptyArrayOfIntegersOrFirstName() {
        flushTestUsers();
        List<User> users = repository.queryByAgeInOrFirstname(new Integer[0], secondUser.getFirstname());
        assertThat(users).containsOnly(secondUser);
    }

    // DATAJPA-677
    @Test
    public void shouldSupportJava8StreamsForRepositoryFinderMethods() {
        flushTestUsers();
        try (Stream<User> stream = repository.findAllByCustomQueryAndStream()) {
            assertThat(stream).hasSize(4);
        }
    }

    // DATAJPA-677
    @Test
    public void shouldSupportJava8StreamsForRepositoryDerivedFinderMethods() {
        flushTestUsers();
        try (Stream<User> stream = repository.readAllByFirstnameNotNull()) {
            assertThat(stream).hasSize(4);
        }
    }

    // DATAJPA-677
    @Test
    public void supportsJava8StreamForPageableMethod() {
        flushTestUsers();
        try (Stream<User> stream = repository.streamAllPaged(PageRequest.of(0, 2))) {
            assertThat(stream).hasSize(2);
        }
    }

    // DATAJPA-218
    @Test
    public void findAllByExample() {
        flushTestUsers();
        User prototype = new User();
        prototype.setAge(28);
        prototype.setCreatedAt(null);
        List<User> users = repository.findAll(of(prototype));
        assertThat(users).hasSize(1);
        assertThat(users.get(0)).isEqualTo(firstUser);
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithEmptyProbe() {
        flushTestUsers();
        User prototype = new User();
        prototype.setCreatedAt(null);
        List<User> users = repository.findAll(of(prototype, ExampleMatcher.matching().withIgnorePaths("age", "createdAt", "active")));
        assertThat(users).hasSize(4);
    }

    // DATAJPA-218
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void findAllByNullExample() {
        repository.findAll(((Example<User>) (null)));
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithExcludedAttributes() {
        flushTestUsers();
        User prototype = new User();
        prototype.setAge(28);
        Example<User> example = Example.of(prototype, matching().withIgnorePaths("createdAt"));
        List<User> users = repository.findAll(example);
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithAssociation() {
        flushTestUsers();
        firstUser.setManager(secondUser);
        thirdUser.setManager(firstUser);
        saveAll(Arrays.asList(firstUser, thirdUser));
        User manager = new User();
        manager.setLastname("Arrasz");
        manager.setAge(secondUser.getAge());
        manager.setCreatedAt(null);
        User prototype = new User();
        prototype.setCreatedAt(null);
        prototype.setManager(manager);
        Example<User> example = Example.of(prototype, matching().withIgnorePaths("age"));
        List<User> users = repository.findAll(example);
        assertThat(users).hasSize(1);
        assertThat(users.get(0)).isEqualTo(firstUser);
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithEmbedded() {
        flushTestUsers();
        firstUser.setAddress(new Address("germany", "dresden", "", ""));
        repository.save(firstUser);
        User prototype = new User();
        prototype.setCreatedAt(null);
        prototype.setAddress(new Address("germany", null, null, null));
        Example<User> example = Example.of(prototype, matching().withIgnorePaths("age"));
        List<User> users = repository.findAll(example);
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithStartingStringMatcher() {
        flushTestUsers();
        User prototype = new User();
        prototype.setFirstname("Ol");
        Example<User> example = Example.of(prototype, matching().withStringMatcher(STARTING).withIgnorePaths("age", "createdAt"));
        List<User> users = repository.findAll(example);
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithEndingStringMatcher() {
        flushTestUsers();
        User prototype = new User();
        prototype.setFirstname("ver");
        Example<User> example = Example.of(prototype, matching().withStringMatcher(ENDING).withIgnorePaths("age", "createdAt"));
        List<User> users = repository.findAll(example);
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-218
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void findAllByExampleWithRegexStringMatcher() {
        flushTestUsers();
        User prototype = new User();
        prototype.setFirstname("^Oliver$");
        Example<User> example = Example.of(prototype, matching().withStringMatcher(REGEX));
        repository.findAll(example);
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithIgnoreCase() {
        flushTestUsers();
        User prototype = new User();
        prototype.setFirstname("oLiVer");
        Example<User> example = Example.of(prototype, matching().withIgnoreCase().withIgnorePaths("age", "createdAt"));
        List<User> users = repository.findAll(example);
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithStringMatcherAndIgnoreCase() {
        flushTestUsers();
        User prototype = new User();
        prototype.setFirstname("oLiV");
        Example<User> example = Example.of(prototype, matching().withStringMatcher(STARTING).withIgnoreCase().withIgnorePaths("age", "createdAt"));
        List<User> users = repository.findAll(example);
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithIncludeNull() {
        flushTestUsers();
        firstUser.setAddress(new Address("andor", "caemlyn", "", ""));
        User fifthUser = new User();
        fifthUser.setEmailAddress("foo@bar.com");
        fifthUser.setActive(firstUser.isActive());
        fifthUser.setAge(firstUser.getAge());
        fifthUser.setFirstname(firstUser.getFirstname());
        fifthUser.setLastname(firstUser.getLastname());
        saveAll(Arrays.asList(firstUser, fifthUser));
        User prototype = new User();
        prototype.setFirstname(firstUser.getFirstname());
        Example<User> example = Example.of(prototype, matching().withIncludeNullValues().withIgnorePaths("id", "binaryData", "lastname", "emailAddress", "age", "createdAt"));
        List<User> users = repository.findAll(example);
        assertThat(users).containsOnly(fifthUser);
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithPropertySpecifier() {
        flushTestUsers();
        User prototype = new User();
        prototype.setFirstname("oLi");
        Example<User> example = Example.of(prototype, matching().withIgnoreCase().withIgnorePaths("age", "createdAt").withMatcher("firstname", new GenericPropertyMatcher().startsWith()));
        List<User> users = repository.findAll(example);
        assertThat(users).containsOnly(firstUser);
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithSort() {
        flushTestUsers();
        User user1 = new User("Oliver", "Srping", "o@s.de");
        user1.setAge(30);
        repository.save(user1);
        User prototype = new User();
        prototype.setFirstname("oLi");
        Example<User> example = Example.of(prototype, matching().withIgnoreCase().withIgnorePaths("age", "createdAt").withStringMatcher(STARTING).withIgnoreCase());
        List<User> users = repository.findAll(example, Sort.by(DESC, "age"));
        assertThat(users).hasSize(2).containsExactly(user1, firstUser);
    }

    // DATAJPA-218
    @Test
    public void findAllByExampleWithPageable() {
        flushTestUsers();
        for (int i = 0; i < 99; i++) {
            User user1 = new User(("Oliver-" + i), "Srping", (("o" + i) + "@s.de"));
            user1.setAge((30 + i));
            repository.save(user1);
        }
        User prototype = new User();
        prototype.setFirstname("oLi");
        Example<User> example = Example.of(prototype, matching().withIgnoreCase().withIgnorePaths("age", "createdAt").withStringMatcher(STARTING).withIgnoreCase());
        Page<User> users = repository.findAll(example, PageRequest.of(0, 10, Sort.by(DESC, "age")));
        assertThat(users.getSize()).isEqualTo(10);
        assertThat(users.hasNext()).isEqualTo(true);
        assertThat(users.getTotalElements()).isEqualTo(100L);
    }

    // DATAJPA-218
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void findAllByExampleShouldNotAllowCycles() {
        flushTestUsers();
        User user1 = new User();
        user1.setFirstname("user1");
        user1.setManager(user1);
        Example<User> example = Example.of(user1, matching().withIgnoreCase().withIgnorePaths("age", "createdAt").withStringMatcher(STARTING).withIgnoreCase());
        repository.findAll(example, PageRequest.of(0, 10, Sort.by(DESC, "age")));
    }

    // DATAJPA-218
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void findAllByExampleShouldNotAllowCyclesOverSeveralInstances() {
        flushTestUsers();
        User user1 = new User();
        user1.setFirstname("user1");
        User user2 = new User();
        user2.setFirstname("user2");
        user1.setManager(user2);
        user2.setManager(user1);
        Example<User> example = Example.of(user1, matching().withIgnoreCase().withIgnorePaths("age", "createdAt").withStringMatcher(STARTING).withIgnoreCase());
        repository.findAll(example, PageRequest.of(0, 10, Sort.by(DESC, "age")));
    }

    // DATAJPA-218
    @Test
    public void findOneByExampleWithExcludedAttributes() {
        flushTestUsers();
        User prototype = new User();
        prototype.setAge(28);
        Example<User> example = Example.of(prototype, matching().withIgnorePaths("createdAt"));
        assertThat(repository.findOne(example)).contains(firstUser);
    }

    // DATAJPA-218
    @Test
    public void countByExampleWithExcludedAttributes() {
        flushTestUsers();
        User prototype = new User();
        prototype.setAge(28);
        Example<User> example = Example.of(prototype, matching().withIgnorePaths("createdAt"));
        long count = repository.count(example);
        assertThat(count).isEqualTo(1L);
    }

    // DATAJPA-218
    @Test
    public void existsByExampleWithExcludedAttributes() {
        flushTestUsers();
        User prototype = new User();
        prototype.setAge(28);
        Example<User> example = Example.of(prototype, matching().withIgnorePaths("createdAt"));
        boolean exists = repository.exists(example);
        assertThat(exists).isEqualTo(true);
    }

    // DATAJPA-905
    @Test
    public void executesPagedSpecificationSettingAnOrder() {
        flushTestUsers();
        Page<User> result = repository.findAll(UserSpecifications.userHasLastnameLikeWithSort("e"), PageRequest.of(0, 1));
        assertThat(result.getTotalElements()).isEqualTo(2L);
        assertThat(result.getNumberOfElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(thirdUser);
    }

    // DATAJPA-1172
    @Test
    public void exceptionsDuringParameterSettingGetThrown() {
        // 
        // 
        assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> repository.findByStringAge("twelve")).matches(( e) -> !(e.getMessage().contains("Named parameter [age] not set")));
    }

    // DATAJPA-1172
    @Test
    public void queryProvidesCorrectNumberOfParametersForNativeQuery() {
        Query query = em.createNativeQuery("select 1 from User where firstname=? and lastname=?");
        assertThat(query.getParameters()).hasSize(2);
    }

    // DATAJPA-1185
    @Test
    public void dynamicProjectionReturningStream() {
        flushTestUsers();
        assertThat(repository.findAsStreamByFirstnameLike("%O%", User.class)).hasSize(1);
    }

    // DATAJPA-1185
    @Test
    public void dynamicProjectionReturningList() {
        flushTestUsers();
        List<User> users = repository.findAsListByFirstnameLike("%O%", User.class);
        assertThat(users).hasSize(1);
    }

    // DATAJPA-1179
    @Test
    public void duplicateSpelsWorkAsIntended() {
        flushTestUsers();
        List<User> users = repository.findUsersByDuplicateSpel("Oliver");
        assertThat(users).hasSize(1);
    }

    // DATAJPA-980
    @Test
    public void supportsProjectionsWithNativeQueries() {
        flushTestUsers();
        User user = findAll().get(0);
        UserRepository.NameOnly result = repository.findByNativeQuery(user.getId());
        assertThat(result.getFirstname()).isEqualTo(user.getFirstname());
        assertThat(result.getLastname()).isEqualTo(user.getLastname());
    }

    // DATAJPA-1248
    @Test
    public void supportsProjectionsWithNativeQueriesAndCamelCaseProperty() {
        flushTestUsers();
        User user = findAll().get(0);
        UserRepository.EmailOnly result = repository.findEmailOnlyByNativeQuery(user.getId());
        String emailAddress = result.getEmailAddress();
        // 
        // 
        // 
        assertThat(emailAddress).isEqualTo(user.getEmailAddress()).as("ensuring email is actually not null").isNotNull();
    }

    // DATAJPA-1235
    @Test
    public void handlesColonsFollowedByIntegerInStringLiteral() {
        String firstName = "aFirstName";
        User expected = new User(firstName, "000:1", "something@something");
        User notExpected = new User(firstName, "000\\:1", "something@something.else");
        repository.save(expected);
        repository.save(notExpected);
        assertThat(findAll()).hasSize(2);
        List<User> users = repository.queryWithIndexedParameterAndColonFollowedByIntegerInString(firstName);
        assertThat(users).extracting(User::getId).containsExactly(expected.getId());
    }

    // DATAJPA-1233
    @Test
    public void handlesCountQueriesWithLessParametersSingleParam() {
        repository.findAllOrderedBySpecialNameSingleParam("Oliver", PageRequest.of(2, 3));
    }

    // DATAJPA-1233
    @Test
    public void handlesCountQueriesWithLessParametersMoreThanOne() {
        repository.findAllOrderedBySpecialNameMultipleParams("Oliver", "x", PageRequest.of(2, 3));
    }

    // DATAJPA-1233
    @Test
    public void handlesCountQueriesWithLessParametersMoreThanOneIndexed() {
        repository.findAllOrderedBySpecialNameMultipleParamsIndexed("Oliver", "x", PageRequest.of(2, 3));
    }

    // DATAJPA-928
    @Test
    public void executeNativeQueryWithPage() {
        flushTestUsers();
        Page<User> firstPage = repository.findByNativeNamedQueryWithPageable(new PageRequest(0, 3));
        Page<User> secondPage = repository.findByNativeNamedQueryWithPageable(new PageRequest(1, 3));
        SoftAssertions softly = new SoftAssertions();
        assertThat(firstPage.getTotalElements()).isEqualTo(4L);
        assertThat(firstPage.getNumberOfElements()).isEqualTo(3);
        // 
        // 
        assertThat(firstPage.getContent()).extracting(User::getFirstname).containsExactly("Dave", "Joachim", "kevin");
        assertThat(secondPage.getTotalElements()).isEqualTo(4L);
        assertThat(secondPage.getNumberOfElements()).isEqualTo(1);
        // 
        // 
        assertThat(secondPage.getContent()).extracting(User::getFirstname).containsExactly("Oliver");
        softly.assertAll();
    }

    // DATAJPA-928
    @Test
    public void executeNativeQueryWithPageWorkaround() {
        flushTestUsers();
        Page<String> firstPage = repository.findByNativeQueryWithPageable(new PageRequest(0, 3));
        Page<String> secondPage = repository.findByNativeQueryWithPageable(new PageRequest(1, 3));
        SoftAssertions softly = new SoftAssertions();
        assertThat(firstPage.getTotalElements()).isEqualTo(4L);
        assertThat(firstPage.getNumberOfElements()).isEqualTo(3);
        // 
        assertThat(firstPage.getContent()).containsExactly("Dave", "Joachim", "kevin");
        assertThat(secondPage.getTotalElements()).isEqualTo(4L);
        assertThat(secondPage.getNumberOfElements()).isEqualTo(1);
        // 
        assertThat(secondPage.getContent()).containsExactly("Oliver");
        softly.assertAll();
    }

    // DATAJPA-1273
    @Test
    public void bindsNativeQueryResultsToProjectionByName() {
        flushTestUsers();
        List<UserRepository.NameOnly> result = repository.findByNamedQueryWithAliasInInvertedOrder();
        assertThat(result).element(0).satisfies(( it) -> {
            assertThat(it.getFirstname()).isEqualTo("Joachim");
            assertThat(it.getLastname()).isEqualTo("Arrasz");
        });
    }

    // DATAJPA-1301
    @Test
    public void returnsNullValueInMap() {
        firstUser.setLastname(null);
        flushTestUsers();
        Map<String, Object> map = repository.findMapWithNullValues();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(map.keySet()).containsExactlyInAnyOrder("firstname", "lastname");
        softly.assertThat(map.containsKey("firstname")).isTrue();
        softly.assertThat(map.containsKey("lastname")).isTrue();
        softly.assertThat(map.get("firstname")).isEqualTo("Oliver");
        softly.assertThat(map.get("lastname")).isNull();
        softly.assertThat(map.get("non-existent")).isNull();
        softly.assertThat(map.get(new Object())).isNull();
        softly.assertAll();
    }

    // DATAJPA-1307
    @Test
    public void testFindByEmailAddressJdbcStyleParameter() throws Exception {
        flushTestUsers();
        assertThat(repository.findByEmailNativeAddressJdbcStyleParameter("gierke@synyx.de")).isEqualTo(firstUser);
    }
}

