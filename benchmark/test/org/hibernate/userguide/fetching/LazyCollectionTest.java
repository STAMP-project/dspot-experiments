/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.fetching;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::fetching-LazyCollection-domain-model-example[]
public class LazyCollectionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::fetching-LazyCollection-persist-example[]
            org.hibernate.userguide.fetching.Department department = new org.hibernate.userguide.fetching.Department();
            department.setId(1L);
            entityManager.persist(department);
            for (long i = 1; i <= 3; i++) {
                org.hibernate.userguide.fetching.Employee employee = new org.hibernate.userguide.fetching.Employee();
                employee.setId(i);
                employee.setUsername(String.format("user_%d", i));
                department.addEmployee(employee);
            }
            // end::fetching-LazyCollection-persist-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::fetching-LazyCollection-select-example[]
            org.hibernate.userguide.fetching.Department department = entityManager.find(.class, 1L);
            int employeeCount = department.getEmployees().size();
            for (int i = 0; i < employeeCount; i++) {
                log.infof("Fetched employee: %s", department.getEmployees().get(i).getUsername());
            }
            // end::fetching-LazyCollection-select-example[]
        });
    }

    // tag::fetching-LazyCollection-domain-model-example[]
    // tag::fetching-LazyCollection-domain-model-example[]
    @Entity(name = "Department")
    public static class Department {
        @Id
        private Long id;

        @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
        @OrderColumn(name = "order_id")
        @LazyCollection(LazyCollectionOption.EXTRA)
        private List<LazyCollectionTest.Employee> employees = new ArrayList<>();

        // Getters and setters omitted for brevity
        // end::fetching-LazyCollection-domain-model-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<LazyCollectionTest.Employee> getEmployees() {
            return employees;
        }

        public void setEmployees(List<LazyCollectionTest.Employee> employees) {
            this.employees = employees;
        }

        public void addEmployee(LazyCollectionTest.Employee employee) {
            this.employees.add(employee);
            employee.setDepartment(this);
        }
    }

    // tag::fetching-LazyCollection-domain-model-example[]
    @Entity(name = "Employee")
    public static class Employee {
        @Id
        private Long id;

        @NaturalId
        private String username;

        @ManyToOne(fetch = FetchType.LAZY)
        private LazyCollectionTest.Department department;

        // Getters and setters omitted for brevity
        // end::fetching-LazyCollection-domain-model-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public LazyCollectionTest.Department getDepartment() {
            return department;
        }

        public void setDepartment(LazyCollectionTest.Department department) {
            this.department = department;
        }
    }
}

