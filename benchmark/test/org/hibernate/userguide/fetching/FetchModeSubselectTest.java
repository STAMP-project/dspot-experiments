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
public class FetchModeSubselectTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            for (long i = 0; i < 2; i++) {
                org.hibernate.userguide.fetching.Department department = new org.hibernate.userguide.fetching.Department();
                department.id = i + 1;
                department.name = String.format("Department %d", department.id);
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
            // tag::fetching-strategies-fetch-mode-subselect-example[]
            List<org.hibernate.userguide.fetching.Department> departments = entityManager.createQuery(("select d " + ("from Department d " + "where d.name like :token")), .class).setParameter("token", "Department%").getResultList();
            log.infof("Fetched %d Departments", departments.size());
            for (org.hibernate.userguide.fetching.Department department : departments) {
                assertEquals(3, department.getEmployees().size());
            }
            // end::fetching-strategies-fetch-mode-subselect-example[]
        });
    }

    @Entity(name = "Department")
    public static class Department {
        @Id
        private Long id;

        private String name;

        // tag::fetching-strategies-fetch-mode-subselect-mapping-example[]
        @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
        @Fetch(FetchMode.SUBSELECT)
        private java.util.List<FetchModeSubselectTest.Employee> employees = new ArrayList<>();

        // end::fetching-strategies-fetch-mode-subselect-mapping-example[]
        // Getters and setters omitted for brevity
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public java.util.List<FetchModeSubselectTest.Employee> getEmployees() {
            return employees;
        }

        public void setEmployees(java.util.List<FetchModeSubselectTest.Employee> employees) {
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
        private FetchModeSubselectTest.Department department;
    }
}

