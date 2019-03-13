/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.jmx;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class JmxTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-7405")
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.jmx.Person person = new org.hibernate.userguide.jmx.Person();
            person.id = 1L;
            entityManager.persist(person);
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String firstName;

        private String lastName;
    }
}

