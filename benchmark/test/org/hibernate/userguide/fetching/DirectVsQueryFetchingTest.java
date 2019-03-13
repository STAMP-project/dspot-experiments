/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.fetching;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.NaturalId;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::fetching-direct-vs-query-domain-model-example[]
@RequiresDialect(H2Dialect.class)
public class DirectVsQueryFetchingTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.fetching.Department department = new org.hibernate.userguide.fetching.Department();
            department.id = 1L;
            entityManager.persist(department);
            org.hibernate.userguide.fetching.Employee employee1 = new org.hibernate.userguide.fetching.Employee();
            employee1.id = 1L;
            employee1.username = "user1";
            employee1.department = department;
            entityManager.persist(employee1);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::fetching-direct-vs-query-direct-fetching-example[]
            org.hibernate.userguide.fetching.Employee employee = entityManager.find(.class, 1L);
            // end::fetching-direct-vs-query-direct-fetching-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::fetching-direct-vs-query-entity-query-example[]
            org.hibernate.userguide.fetching.Employee employee = entityManager.createQuery(("select e " + ("from Employee e " + "where e.id = :id")), .class).setParameter("id", 1L).getSingleResult();
            // end::fetching-direct-vs-query-entity-query-example[]
        });
    }

    // tag::fetching-direct-vs-query-domain-model-example[]
    // Getters and setters omitted for brevity
    @Entity(name = "Department")
    public static class Department {
        @Id
        private Long id;
    }

    // Getters and setters omitted for brevity
    @Entity(name = "Employee")
    public static class Employee {
        @Id
        private Long id;

        @NaturalId
        private String username;

        @ManyToOne(fetch = FetchType.EAGER)
        private DirectVsQueryFetchingTest.Department department;
    }
}

