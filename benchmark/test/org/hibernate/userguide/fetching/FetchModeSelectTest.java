/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.fetching;


import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::fetching-strategies-fetch-mode-select-mapping-example[]
public class FetchModeSelectTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            for (long i = 0; i < 2; i++) {
                org.hibernate.userguide.fetching.Department department = new org.hibernate.userguide.fetching.Department();
                department.id = i + 1;
                entityManager.persist(department);
                for (long j = 0; j < 3; j++) {
                    org.hibernate.userguide.fetching.Employee employee1 = new org.hibernate.userguide.fetching.Employee();
                    employee1.username = String.format("user %d_%d", i, j);
                    employee1.department = department;
                    entityManager.persist(employee1);
                }
            }
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::fetching-strategies-fetch-mode-select-example[]
            List<org.hibernate.userguide.fetching.Department> departments = entityManager.createQuery("select d from Department d", .class).getResultList();
            log.infof("Fetched %d Departments", departments.size());
            for (org.hibernate.userguide.fetching.Department department : departments) {
                assertEquals(3, department.getEmployees().size());
            }
            // end::fetching-strategies-fetch-mode-select-example[]
        });
    }

    // tag::fetching-strategies-fetch-mode-select-mapping-example[]
    // tag::fetching-strategies-fetch-mode-select-mapping-example[]
    @Entity(name = "Department")
    public static class Department {
        @Id
        private Long id;

        @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
        @Fetch(FetchMode.SELECT)
        private java.util.List<FetchModeSelectTest.Employee> employees = new ArrayList<>();

        // Getters and setters omitted for brevity
        // end::fetching-strategies-fetch-mode-select-mapping-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public java.util.List<FetchModeSelectTest.Employee> getEmployees() {
            return employees;
        }

        public void setEmployees(java.util.List<FetchModeSelectTest.Employee> employees) {
            this.employees = employees;
        }
    }

    // Getters and setters omitted for brevity
    @Entity(name = "Employee")
    public static class Employee {
        @Id
        @GeneratedValue
        private Long id;

        @NaturalId
        private String username;

        @ManyToOne(fetch = FetchType.LAZY)
        private FetchModeSelectTest.Department department;
    }
}

