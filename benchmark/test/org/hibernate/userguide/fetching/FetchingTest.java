/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.fetching;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.annotations.ColumnTransformer;
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
// end::fetching-strategies-domain-model-example[]
@RequiresDialect(H2Dialect.class)
public class FetchingTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.fetching.Department department = new org.hibernate.userguide.fetching.Department();
            department.id = 1L;
            entityManager.persist(department);
            org.hibernate.userguide.fetching.Employee employee1 = new org.hibernate.userguide.fetching.Employee();
            employee1.id = 1L;
            employee1.username = "user1";
            employee1.password = "3fabb4de8f1ee2e97d7793bab2db1116";
            employee1.accessLevel = 0;
            employee1.department = department;
            entityManager.persist(employee1);
            org.hibernate.userguide.fetching.Employee employee2 = new org.hibernate.userguide.fetching.Employee();
            employee2.id = 2L;
            employee2.username = "user2";
            employee2.password = "3fabb4de8f1ee2e97d7793bab2db1116";
            employee2.accessLevel = 1;
            employee2.department = department;
            entityManager.persist(employee2);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String username = "user1";
            String password = "3fabb4de8f1ee2e97d7793bab2db1116";
            // tag::fetching-strategies-no-fetching-example[]
            org.hibernate.userguide.fetching.Employee employee = entityManager.createQuery(("select e " + ((("from Employee e " + "where ") + "	e.username = :username and ") + "	e.password = :password")), .class).setParameter("username", username).setParameter("password", password).getSingleResult();
            // end::fetching-strategies-no-fetching-example[]
            assertNotNull(employee);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String username = "user1";
            String password = "3fabb4de8f1ee2e97d7793bab2db1116";
            // tag::fetching-strategies-no-fetching-scalar-example[]
            Integer accessLevel = entityManager.createQuery(("select e.accessLevel " + ((("from Employee e " + "where ") + "	e.username = :username and ") + "	e.password = :password")), .class).setParameter("username", username).setParameter("password", password).getSingleResult();
            // end::fetching-strategies-no-fetching-scalar-example[]
            assertEquals(Integer.valueOf(0), accessLevel);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String username = "user1";
            String password = "3fabb4de8f1ee2e97d7793bab2db1116";
            // tag::fetching-strategies-dynamic-fetching-jpql-example[]
            org.hibernate.userguide.fetching.Employee employee = entityManager.createQuery(("select e " + (((("from Employee e " + "left join fetch e.projects ") + "where ") + "	e.username = :username and ") + "	e.password = :password")), .class).setParameter("username", username).setParameter("password", password).getSingleResult();
            // end::fetching-strategies-dynamic-fetching-jpql-example[]
            assertNotNull(employee);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String username = "user1";
            String password = "3fabb4de8f1ee2e97d7793bab2db1116";
            // tag::fetching-strategies-dynamic-fetching-criteria-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.userguide.fetching.Employee> query = builder.createQuery(.class);
            Root<org.hibernate.userguide.fetching.Employee> root = query.from(.class);
            root.fetch("projects", JoinType.LEFT);
            query.select(root).where(builder.and(builder.equal(root.get("username"), username), builder.equal(root.get("password"), password)));
            org.hibernate.userguide.fetching.Employee employee = entityManager.createQuery(query).getSingleResult();
            // end::fetching-strategies-dynamic-fetching-criteria-example[]
            assertNotNull(employee);
        });
    }

    // tag::fetching-strategies-domain-model-example[]
    // Getters and setters omitted for brevity
    @Entity(name = "Department")
    public static class Department {
        @Id
        private Long id;

        @OneToMany(mappedBy = "department")
        private List<FetchingTest.Employee> employees = new ArrayList<>();
    }

    // tag::mapping-column-read-and-write-example[]
    // Getters and setters omitted for brevity
    @Entity(name = "Employee")
    public static class Employee {
        @Id
        private Long id;

        @NaturalId
        private String username;

        @Column(name = "pswd")
        @ColumnTransformer(read = "decrypt( 'AES', '00', pswd  )", write = "encrypt('AES', '00', ?)")
        private String password;

        private int accessLevel;

        @ManyToOne(fetch = FetchType.LAZY)
        private FetchingTest.Department department;

        @ManyToMany(mappedBy = "employees")
        private List<FetchingTest.Project> projects = new ArrayList<>();
    }

    // end::mapping-column-read-and-write-example[]
    // Getters and setters omitted for brevity
    @Entity(name = "Project")
    public class Project {
        @Id
        private Long id;

        @ManyToMany
        private List<FetchingTest.Employee> employees = new ArrayList<>();
    }
}

