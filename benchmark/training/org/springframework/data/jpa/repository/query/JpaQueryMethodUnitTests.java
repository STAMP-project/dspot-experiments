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
package org.springframework.data.jpa.repository.query;


import LockModeType.PESSIMISTIC_WRITE;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.sample.UserRepository;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.query.Param;


/**
 * Unit test for {@link QueryMethod}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Christoph Strobl
 */
@RunWith(MockitoJUnitRunner.class)
public class JpaQueryMethodUnitTests {
    static final Class<?> DOMAIN_CLASS = User.class;

    static final String METHOD_NAME = "findByFirstname";

    @Mock
    QueryExtractor extractor;

    @Mock
    RepositoryMetadata metadata;

    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

    Method invalidReturnType;

    Method pageableAndSort;

    Method pageableTwice;

    Method sortableTwice;

    Method findWithLockMethod;

    Method findsProjections;

    Method findsProjection;

    Method queryMethodWithCustomEntityFetchGraph;

    @Test
    public void testname() throws Exception {
        JpaQueryMethod method = getQueryMethod(UserRepository.class, "findByLastname", String.class);
        Assert.assertEquals("User.findByLastname", method.getNamedQueryName());
        Assert.assertThat(method.isCollectionQuery(), is(true));
        Assert.assertThat(method.getAnnotatedQuery(), is(nullValue()));
        Assert.assertThat(method.isNativeQuery(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void preventsNullRepositoryMethod() {
        new JpaQueryMethod(null, metadata, factory, extractor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void preventsNullQueryExtractor() throws Exception {
        Method method = UserRepository.class.getMethod("findByLastname", String.class);
        new JpaQueryMethod(method, metadata, factory, null);
    }

    @Test
    public void returnsCorrectName() throws Exception {
        JpaQueryMethod method = getQueryMethod(UserRepository.class, "findByLastname", String.class);
        Assert.assertThat(method.getName(), is("findByLastname"));
    }

    @Test
    public void returnsQueryIfAvailable() throws Exception {
        JpaQueryMethod method = getQueryMethod(UserRepository.class, "findByLastname", String.class);
        Assert.assertThat(method.getAnnotatedQuery(), is(nullValue()));
        method = getQueryMethod(UserRepository.class, "findByAnnotatedQuery", String.class);
        Assert.assertThat(method.getAnnotatedQuery(), is(notNullValue()));
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsInvalidReturntypeOnPagebleFinder() {
        new JpaQueryMethod(invalidReturnType, metadata, factory, extractor);
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsPageableAndSortInFinderMethod() {
        new JpaQueryMethod(pageableAndSort, metadata, factory, extractor);
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsTwoPageableParameters() {
        new JpaQueryMethod(pageableTwice, metadata, factory, extractor);
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsTwoSortableParameters() {
        new JpaQueryMethod(sortableTwice, metadata, factory, extractor);
    }

    @Test
    public void recognizesModifyingMethod() throws Exception {
        JpaQueryMethod method = getQueryMethod(UserRepository.class, "renameAllUsersTo", String.class);
        Assert.assertTrue(method.isModifyingQuery());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsModifyingMethodWithPageable() throws Exception {
        Method method = JpaQueryMethodUnitTests.InvalidRepository.class.getMethod("updateMethod", String.class, Pageable.class);
        new JpaQueryMethod(method, metadata, factory, extractor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsModifyingMethodWithSort() throws Exception {
        Method method = JpaQueryMethodUnitTests.InvalidRepository.class.getMethod("updateMethod", String.class, Sort.class);
        new JpaQueryMethod(method, metadata, factory, extractor);
    }

    @Test
    public void discoversHintsCorrectly() throws Exception {
        JpaQueryMethod method = getQueryMethod(UserRepository.class, "findByLastname", String.class);
        List<QueryHint> hints = method.getHints();
        Assert.assertNotNull(hints);
        Assert.assertThat(hints.get(0).name(), is("foo"));
        Assert.assertThat(hints.get(0).value(), is("bar"));
    }

    @Test
    public void calculatesNamedQueryNamesCorrectly() throws Exception {
        RepositoryMetadata metadata = new DefaultRepositoryMetadata(UserRepository.class);
        JpaQueryMethod queryMethod = getQueryMethod(UserRepository.class, "findByLastname", String.class);
        Assert.assertThat(queryMethod.getNamedQueryName(), is("User.findByLastname"));
        Method method = UserRepository.class.getMethod("renameAllUsersTo", String.class);
        queryMethod = new JpaQueryMethod(method, metadata, factory, extractor);
        Assert.assertThat(queryMethod.getNamedQueryName(), is("User.renameAllUsersTo"));
        method = UserRepository.class.getMethod("findSpecialUsersByLastname", String.class);
        queryMethod = new JpaQueryMethod(method, metadata, factory, extractor);
        Assert.assertThat(queryMethod.getNamedQueryName(), is("SpecialUser.findSpecialUsersByLastname"));
    }

    // DATAJPA-117
    @Test
    public void discoversNativeQuery() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "findByLastname", String.class);
        Assert.assertThat(method.isNativeQuery(), is(true));
    }

    // DATAJPA-129
    @Test
    public void considersAnnotatedNamedQueryName() throws Exception {
        JpaQueryMethod queryMethod = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "findByNamedQuery");
        Assert.assertThat(queryMethod.getNamedQueryName(), is("HateoasAwareSpringDataWebConfiguration.bar"));
    }

    // DATAJPA-73
    @Test
    public void discoversLockModeCorrectly() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "findOneLocked", Integer.class);
        LockModeType lockMode = method.getLockModeType();
        Assert.assertEquals(PESSIMISTIC_WRITE, lockMode);
    }

    // DATAJPA-142
    @Test
    public void returnsDefaultCountQueryName() throws Exception {
        JpaQueryMethod method = getQueryMethod(UserRepository.class, "findByLastname", String.class);
        Assert.assertThat(method.getNamedCountQueryName(), is("User.findByLastname.count"));
    }

    // DATAJPA-142
    @Test
    public void returnsDefaultCountQueryNameBasedOnConfiguredNamedQueryName() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "findByNamedQuery");
        Assert.assertThat(method.getNamedCountQueryName(), is("HateoasAwareSpringDataWebConfiguration.bar.count"));
    }

    // DATAJPA-185
    @Test
    public void rejectsInvalidNamedParameter() throws Exception {
        try {
            getQueryMethod(JpaQueryMethodUnitTests.InvalidRepository.class, "findByAnnotatedQuery", String.class);
            Assert.fail();
        } catch (IllegalStateException e) {
            // Parameter from query
            Assert.assertThat(e.getMessage(), containsString("foo"));
            // Parameter name from annotation
            Assert.assertThat(e.getMessage(), containsString("param"));
            // Method name
            Assert.assertThat(e.getMessage(), containsString("findByAnnotatedQuery"));
        }
    }

    // DATAJPA-207
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void returnsTrueIfReturnTypeIsEntity() {
        Mockito.when(metadata.getDomainType()).thenReturn(((Class) (User.class)));
        Mockito.when(metadata.getReturnedDomainClass(findsProjections)).thenReturn(((Class) (Integer.class)));
        Mockito.when(metadata.getReturnedDomainClass(findsProjection)).thenReturn(((Class) (Integer.class)));
        Assert.assertThat(isQueryForEntity(), is(false));
        Assert.assertThat(isQueryForEntity(), is(false));
    }

    // DATAJPA-345
    @Test
    public void detectsLockAndQueryHintsOnIfUsedAsMetaAnnotation() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotation");
        Assert.assertThat(method.getLockModeType(), is(LockModeType.OPTIMISTIC_FORCE_INCREMENT));
        Assert.assertThat(method.getHints(), hasSize(1));
        Assert.assertThat(method.getHints().get(0).name(), is("foo"));
        Assert.assertThat(method.getHints().get(0).value(), is("bar"));
    }

    // DATAJPA-466
    @Test
    public void shouldStoreJpa21FetchGraphInformationAsHint() {
        Mockito.doReturn(User.class).when(metadata).getDomainType();
        Mockito.doReturn(User.class).when(metadata).getReturnedDomainClass(queryMethodWithCustomEntityFetchGraph);
        JpaQueryMethod method = new JpaQueryMethod(queryMethodWithCustomEntityFetchGraph, metadata, factory, extractor);
        Assert.assertThat(method.getEntityGraph(), is(notNullValue()));
        Assert.assertThat(method.getEntityGraph().getName(), is("User.propertyLoadPath"));
        Assert.assertThat(method.getEntityGraph().getType(), is(EntityGraphType.LOAD));
    }

    // DATAJPA-612
    @Test
    public void shouldFindEntityGraphAnnotationOnOverriddenSimpleJpaRepositoryMethod() throws Exception {
        Mockito.doReturn(User.class).when(metadata).getDomainType();
        Mockito.doReturn(User.class).when(metadata).getReturnedDomainClass(((Method) (ArgumentMatchers.any())));
        JpaQueryMethod method = new JpaQueryMethod(JpaQueryMethodUnitTests.JpaRepositoryOverride.class.getMethod("findAll"), metadata, factory, extractor);
        Assert.assertThat(method.getEntityGraph(), is(notNullValue()));
        Assert.assertThat(method.getEntityGraph().getName(), is("User.detail"));
        Assert.assertThat(method.getEntityGraph().getType(), is(EntityGraphType.FETCH));
    }

    // DATAJPA-689
    @Test
    public void shouldFindEntityGraphAnnotationOnOverriddenSimpleJpaRepositoryMethodFindOne() throws Exception {
        Mockito.doReturn(User.class).when(metadata).getDomainType();
        Mockito.doReturn(User.class).when(metadata).getReturnedDomainClass(((Method) (ArgumentMatchers.any())));
        JpaQueryMethod method = new JpaQueryMethod(JpaQueryMethodUnitTests.JpaRepositoryOverride.class.getMethod("findOne", Long.class), metadata, factory, extractor);
        Assert.assertThat(method.getEntityGraph(), is(notNullValue()));
        Assert.assertThat(method.getEntityGraph().getName(), is("User.detail"));
        Assert.assertThat(method.getEntityGraph().getType(), is(EntityGraphType.FETCH));
    }

    /**
     * DATAJPA-696
     */
    @Test
    public void shouldFindEntityGraphAnnotationOnQueryMethodGetOneByWithDerivedName() throws Exception {
        Mockito.doReturn(User.class).when(metadata).getDomainType();
        Mockito.doReturn(User.class).when(metadata).getReturnedDomainClass(((Method) (ArgumentMatchers.any())));
        JpaQueryMethod method = new JpaQueryMethod(JpaQueryMethodUnitTests.JpaRepositoryOverride.class.getMethod("getOneById", Long.class), metadata, factory, extractor);
        Assert.assertThat(method.getEntityGraph(), is(notNullValue()));
        Assert.assertThat(method.getEntityGraph().getName(), is("User.getOneById"));
        Assert.assertThat(method.getEntityGraph().getType(), is(EntityGraphType.FETCH));
    }

    // DATAJPA-758
    @Test
    public void allowsPositionalBindingEvenIfParametersAreNamed() throws Exception {
        getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "queryWithPositionalBinding", String.class);
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForLockLockMode() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotationUsingAliasFor");
        Assert.assertThat(method.getLockModeType(), is(LockModeType.PESSIMISTIC_FORCE_INCREMENT));
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForQueryHints() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotationUsingAliasFor");
        Assert.assertThat(method.getHints(), hasSize(1));
        Assert.assertThat(method.getHints().get(0).name(), is("foo"));
        Assert.assertThat(method.getHints().get(0).value(), is("bar"));
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForQueryHintsCounting() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotationUsingAliasFor");
        Assert.assertThat(method.applyHintsToCountQuery(), is(true));
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForModifyingClearAutomatically() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotationUsingAliasFor");
        Assert.assertThat(method.isModifyingQuery(), is(true));
        Assert.assertThat(method.getClearAutomatically(), is(true));
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForHintsApplyToCountQuery() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotationUsingAliasFor");
        Assert.assertThat(method.applyHintsToCountQuery(), is(true));
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForQueryValue() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotationUsingAliasFor");
        Assert.assertThat(method.getAnnotatedQuery(), is(equalTo("select u from User u where u.firstname = ?1")));
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForQueryCountQuery() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotationUsingAliasFor");
        Assert.assertThat(method.getCountQuery(), is(equalTo("select u from User u where u.lastname = ?1")));
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForQueryCountQueryProjection() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotationUsingAliasFor");
        Assert.assertThat(method.getCountQueryProjection(), is(equalTo("foo-bar")));
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForQueryNamedQueryName() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotationUsingAliasFor");
        Assert.assertThat(method.getNamedQueryName(), is(equalTo("namedQueryName")));
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForQueryNamedCountQueryName() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotationUsingAliasFor");
        Assert.assertThat(method.getNamedCountQueryName(), is(equalTo("namedCountQueryName")));
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForQueryNativeQuery() throws Exception {
        JpaQueryMethod method = getQueryMethod(JpaQueryMethodUnitTests.ValidRepository.class, "withMetaAnnotationUsingAliasFor");
        Assert.assertThat(method.isNativeQuery(), is(true));
    }

    // DATAJPA-871
    @Test
    public void usesAliasedValueForEntityGraph() throws Exception {
        Mockito.doReturn(User.class).when(metadata).getDomainType();
        Mockito.doReturn(User.class).when(metadata).getReturnedDomainClass(((Method) (ArgumentMatchers.any())));
        JpaQueryMethod method = new JpaQueryMethod(JpaQueryMethodUnitTests.JpaRepositoryOverride.class.getMethod("getOneWithCustomEntityGraphAnnotation"), metadata, factory, extractor);
        Assert.assertThat(method.getEntityGraph(), is(notNullValue()));
        Assert.assertThat(method.getEntityGraph().getName(), is("User.detail"));
        Assert.assertThat(method.getEntityGraph().getType(), is(EntityGraphType.LOAD));
    }

    /**
     * Interface to define invalid repository methods for testing.
     *
     * @author Oliver Gierke
     */
    static interface InvalidRepository extends Repository<User, Long> {
        // Invalid return type
        User findByFirstname(String firstname, Pageable pageable);

        // Should not use Pageable *and* Sort
        Page<User> findByFirstname(String firstname, Pageable pageable, Sort sort);

        // Must not use two Pageables
        Page<User> findByFirstname(String firstname, Pageable first, Pageable second);

        // Must not use two Pageables
        Page<User> findByFirstname(String firstname, Sort first, Sort second);

        // Not backed by a named query or @Query annotation
        @Modifying
        void updateMethod(String firstname);

        // Modifying and Pageable is not allowed
        @Modifying
        Page<String> updateMethod(String firstname, Pageable pageable);

        // Modifying and Sort is not allowed
        @Modifying
        void updateMethod(String firstname, Sort sort);

        // Typo in named parameter
        @Query("select u from User u where u.firstname = :foo")
        List<User> findByAnnotatedQuery(@Param("param")
        String param);
    }

    static interface ValidRepository extends Repository<User, Long> {
        @Query(value = "query", nativeQuery = true)
        List<User> findByLastname(String lastname);

        @Query(name = "HateoasAwareSpringDataWebConfiguration.bar")
        List<User> findByNamedQuery();

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select u from User u where u.id = ?1")
        List<User> findOneLocked(Integer primaryKey);

        List<Integer> findsProjections();

        Integer findsProjection();

        @JpaQueryMethodUnitTests.CustomAnnotation
        void withMetaAnnotation();

        // DATAJPA-466
        @EntityGraph(value = "User.propertyLoadPath", type = EntityGraphType.LOAD)
        User queryMethodWithCustomEntityFetchGraph(Integer id);

        @Query("select u from User u where u.firstname = ?1")
        User queryWithPositionalBinding(@Param("firstname")
        String firstname);

        @JpaQueryMethodUnitTests.CustomComposedAnnotationWithAliasFor
        void withMetaAnnotationUsingAliasFor();
    }

    static interface JpaRepositoryOverride extends JpaRepository<User, Long> {
        /**
         * DATAJPA-612
         */
        @Override
        @EntityGraph("User.detail")
        List<User> findAll();

        /**
         * DATAJPA-689
         */
        @EntityGraph("User.detail")
        Optional<User> findOne(Long id);

        /**
         * DATAJPA-696
         */
        @EntityGraph
        User getOneById(Long id);

        @JpaQueryMethodUnitTests.CustomComposedEntityGraphAnnotationWithAliasFor
        User getOneWithCustomEntityGraphAnnotation();
    }

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @QueryHints(@QueryHint(name = "foo", value = "bar"))
    @Retention(RetentionPolicy.RUNTIME)
    static @interface CustomAnnotation {}

    @Modifying
    @Query
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @QueryHints(@QueryHint(name = "foo", value = "bar"))
    @Retention(RetentionPolicy.RUNTIME)
    static @interface CustomComposedAnnotationWithAliasFor {
        @AliasFor(annotation = Modifying.class, attribute = "clearAutomatically")
        boolean doClear() default true;

        @AliasFor(annotation = Query.class, attribute = "value")
        String querystring() default "select u from User u where u.firstname = ?1";

        @AliasFor(annotation = Query.class, attribute = "countQuery")
        String countQueryString() default "select u from User u where u.lastname = ?1";

        @AliasFor(annotation = Query.class, attribute = "countProjection")
        String countProjectionString() default "foo-bar";

        @AliasFor(annotation = Query.class, attribute = "nativeQuery")
        boolean isNativeQuery() default true;

        @AliasFor(annotation = Query.class, attribute = "name")
        String namedQueryName() default "namedQueryName";

        @AliasFor(annotation = Query.class, attribute = "countName")
        String namedCountQueryName() default "namedCountQueryName";

        @AliasFor(annotation = Lock.class, attribute = "value")
        LockModeType lock() default LockModeType.PESSIMISTIC_FORCE_INCREMENT;

        @AliasFor(annotation = QueryHints.class, attribute = "value")
        QueryHint[] hints() default @QueryHint(name = "foo", value = "bar");

        @AliasFor(annotation = QueryHints.class, attribute = "forCounting")
        boolean doCount() default true;
    }

    @EntityGraph
    @Retention(RetentionPolicy.RUNTIME)
    static @interface CustomComposedEntityGraphAnnotationWithAliasFor {
        @AliasFor(annotation = EntityGraph.class, attribute = "value")
        String graphName() default "User.detail";

        @AliasFor(annotation = EntityGraph.class, attribute = "type")
        EntityGraphType graphType() default EntityGraphType.LOAD;

        @AliasFor(annotation = EntityGraph.class, attribute = "attributePaths")
        String[] paths() default { "foo", "bar" };
    }
}

