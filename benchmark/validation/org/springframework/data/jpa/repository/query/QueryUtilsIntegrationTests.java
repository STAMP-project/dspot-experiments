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
package org.springframework.data.jpa.repository.query;


import JoinType.LEFT;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.sample.Category;
import org.springframework.data.jpa.domain.sample.Order;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.infrastructure.HibernateTestUtils;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Integration tests for {@link QueryUtils}.
 *
 * @author Oliver Gierke
 * @author S?bastien P?ralta
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:infrastructure.xml")
public class QueryUtilsIntegrationTests {
    @PersistenceContext
    EntityManager em;

    // DATAJPA-403
    @Test
    public void reusesExistingJoinForExpression() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> from = query.from(User.class);
        PropertyPath managerFirstname = PropertyPath.from("manager.firstname", User.class);
        PropertyPath managerLastname = PropertyPath.from("manager.lastname", User.class);
        QueryUtils.toExpressionRecursively(from, managerLastname);
        QueryUtils.toExpressionRecursively(from, managerFirstname);
        assertThat(from.getJoins()).hasSize(1);
    }

    // DATAJPA-401, DATAJPA-1238
    @Test
    public void createsJoinForNavigationAcrossOptionalAssociation() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        QueryUtils.toExpressionRecursively(root, PropertyPath.from("manager.firstname", User.class));
        assertThat(getNonInnerJoins(root)).hasSize(1);
    }

    // DATAJPA-1404
    @Test
    public void createsJoinForOptionalOneToOneInReverseDirection() {
        doInMerchantContext(( emf) -> {
            CriteriaBuilder builder = emf.getCriteriaBuilder();
            CriteriaQuery<QueryUtilsIntegrationTests.Address> query = builder.createQuery(QueryUtilsIntegrationTests.Address.class);
            Root<QueryUtilsIntegrationTests.Address> root = query.from(QueryUtilsIntegrationTests.Address.class);
            QueryUtils.toExpressionRecursively(root, PropertyPath.from("merchant", QueryUtilsIntegrationTests.Address.class));
            assertThat(getNonInnerJoins(root)).hasSize(1);
        });
    }

    // DATAJPA-1404
    @Test
    public void createsNoJoinForOptionalOneToOneInNormalDirection() {
        doInMerchantContext(( emf) -> {
            CriteriaBuilder builder = emf.getCriteriaBuilder();
            CriteriaQuery<QueryUtilsIntegrationTests.Merchant> query = builder.createQuery(QueryUtilsIntegrationTests.Merchant.class);
            Root<QueryUtilsIntegrationTests.Merchant> root = query.from(QueryUtilsIntegrationTests.Merchant.class);
            QueryUtils.toExpressionRecursively(root, PropertyPath.from("address", QueryUtilsIntegrationTests.Merchant.class));
            assertThat(getNonInnerJoins(root)).isEmpty();
        });
    }

    // DATAJPA-401, DATAJPA-1238
    @Test
    public void doesNotCreateJoinForOptionalAssociationWithoutFurtherNavigation() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        QueryUtils.toExpressionRecursively(root, PropertyPath.from("manager", User.class));
        assertThat(getNonInnerJoins(root)).hasSize(0);
    }

    // DATAJPA-401
    @Test
    public void doesNotCreateAJoinForNonOptionalAssociation() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Order> query = builder.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);
        QueryUtils.toExpressionRecursively(root, PropertyPath.from("customer", Order.class));
    }

    // DATAJPA-454
    @Test
    public void createsJoingToTraverseCollectionPath() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        QueryUtils.toExpressionRecursively(root, PropertyPath.from("colleaguesLastname", User.class));
        assertThat(root.getJoins()).hasSize(1);
    }

    // DATAJPA-476
    @Test
    public void traversesPluralAttributeCorrectly() {
        doInMerchantContext(( emf) -> {
            CriteriaBuilder builder = emf.createEntityManager().getCriteriaBuilder();
            CriteriaQuery<QueryUtilsIntegrationTests.Merchant> query = builder.createQuery(QueryUtilsIntegrationTests.Merchant.class);
            Root<QueryUtilsIntegrationTests.Merchant> root = query.from(QueryUtilsIntegrationTests.Merchant.class);
            QueryUtils.toExpressionRecursively(root, PropertyPath.from("employeesCredentialsUid", QueryUtilsIntegrationTests.Merchant.class));
        });
    }

    // DATAJPA-763
    @Test
    @SuppressWarnings("unchecked")
    public void doesNotCreateAJoinForAlreadyFetchedAssociation() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Category> query = builder.createQuery(Category.class);
        Root<Category> root = query.from(Category.class);
        Root<Category> mock = Mockito.mock(Root.class);
        Mockito.doReturn(root.getModel()).when(mock).getModel();
        Mockito.doReturn(Collections.singleton(root.fetch("product", LEFT))).when(mock).getFetches();
        QueryUtils.toExpressionRecursively(mock, PropertyPath.from("product", Category.class));
        Mockito.verify(mock, Mockito.times(1)).get("product");
        Mockito.verify(mock, Mockito.times(0)).join(Mockito.eq("product"), Mockito.any(JoinType.class));
    }

    // DATAJPA-1080
    @Test
    public void toOrdersCanSortByJoinColumn() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        Join<User, User> join = root.join("manager", LEFT);
        Sort sort = new Sort(Direction.ASC, "age");
        List<javax.persistence.criteria.Order> orders = QueryUtils.toOrders(sort, join, builder);
        assertThat(orders).hasSize(1);
    }

    /**
     * This test documents an ambiguity in the JPA spec (or it's implementation in Hibernate vs EclipseLink) that we have
     * to work around in the test {@link #doesNotCreateJoinForOptionalAssociationWithoutFurtherNavigation()}. See also:
     * https://github.com/javaee/jpa-spec/issues/169 Compare to: {@link EclipseLinkQueryUtilsIntegrationTests}
     */
    // DATAJPA-1238
    @Test
    public void demonstrateDifferentBehavorOfGetJoin() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        root.get("manager");
        assertThat(root.getJoins()).hasSize(getNumberOfJoinsAfterCreatingAPath());
    }

    @Entity
    @SuppressWarnings("unused")
    static class Merchant {
        @Id
        String id;

        @OneToMany
        Set<QueryUtilsIntegrationTests.Employee> employees;

        @OneToOne
        QueryUtilsIntegrationTests.Address address;
    }

    @Entity
    @SuppressWarnings("unused")
    static class Address {
        @Id
        String id;

        @OneToOne(mappedBy = "address")
        QueryUtilsIntegrationTests.Merchant merchant;
    }

    @Entity
    @SuppressWarnings("unused")
    static class Employee {
        @Id
        String id;

        @OneToMany
        Set<QueryUtilsIntegrationTests.Credential> credentials;
    }

    @Entity
    @SuppressWarnings("unused")
    static class Credential {
        @Id
        String id;

        String uid;
    }

    /**
     * A {@link PersistenceProviderResolver} that returns only a Hibernate {@link PersistenceProvider} and ignores others.
     *
     * @author Thomas Darimont
     * @author Oliver Gierke
     */
    static class HibernateOnlyPersistenceProviderResolver implements PersistenceProviderResolver {
        @Override
        public List<PersistenceProvider> getPersistenceProviders() {
            return Collections.singletonList(HibernateTestUtils.getPersistenceProvider());
        }

        @Override
        public void clearCachedProviders() {
        }
    }
}

