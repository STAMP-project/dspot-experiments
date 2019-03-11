/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.locking;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Version;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Jeroen Stiekema (jeroen@stiekema.eu)
 */
public class JoinedInheritanceOptimisticForceIncrementTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11979")
    public void testForceIncrement() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.locking.Employee lockedEmployee = session.get(.class, 1L);
            session.lock(lockedEmployee, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        });
    }

    @Entity(name = "Person")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class Person {
        @Id
        @Column(name = "PERSON_ID")
        private Long id;

        @Version
        @Column(name = "ver")
        private Integer version;

        private String name;

        public Person() {
        }

        public Person(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Entity(name = "Employee")
    @PrimaryKeyJoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "PERSON_ID")
    public static class Employee extends JoinedInheritanceOptimisticForceIncrementTest.Person {
        private Integer salary;

        public Employee() {
        }

        public Employee(Long id, String name, Integer salary) {
            super(id, name);
            this.salary = salary;
        }
    }
}

