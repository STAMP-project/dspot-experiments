/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.event;


import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class EmbeddableCallbackTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12326")
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.event.Employee employee = new org.hibernate.event.Employee();
            employee.details = new org.hibernate.event.EmployeeDetails();
            employee.id = 1;
            entityManager.persist(employee);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.event.Employee employee = entityManager.find(.class, 1);
            assertEquals("Vlad", employee.name);
            assertEquals("Developer Advocate", employee.details.jobTitle);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13110")
    public void testNullEmbeddable() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.event.Employee employee = new org.hibernate.event.Employee();
            employee.id = 1;
            entityManager.persist(employee);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.event.Employee employee = entityManager.find(.class, 1);
            assertEquals("Vlad", employee.name);
            assertNull(employee.details);
        });
    }

    @Entity(name = "Employee")
    public static class Employee {
        @Id
        private Integer id;

        private String name;

        private EmbeddableCallbackTest.EmployeeDetails details;

        @PrePersist
        public void setUp() {
            name = "Vlad";
        }
    }

    @Embeddable
    public static class EmployeeDetails {
        private String jobTitle;

        @PrePersist
        public void setUp() {
            jobTitle = "Developer Advocate";
        }
    }
}

