/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ops;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class ContainsTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        ContainsTest.Person _person = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.ops.Person person = new org.hibernate.jpa.test.ops.Person();
            person.id = 1L;
            person.name = "John Doe";
            entityManager.persist(person);
            assertTrue(entityManager.contains(person));
            return person;
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            assertFalse(entityManager.contains(_person));
            org.hibernate.jpa.test.ops.Person person = entityManager.find(.class, 1L);
            assertTrue(entityManager.contains(person));
        });
    }

    @Entity(name = "PersonEntity")
    public static class Person {
        @Id
        private Long id;

        private String name;
    }
}

