/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.secure;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11805")
public class JaccIntegratorTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testAllow() {
        setPolicy(true);
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.secure.Person person = new org.hibernate.secure.Person();
            person.id = 1L;
            person.name = "John Doe";
            entityManager.persist(person);
        });
    }

    @Test
    public void testDisallow() {
        setPolicy(false);
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                org.hibernate.secure.Person person = new org.hibernate.secure.Person();
                person.id = 1L;
                person.name = "John Doe";
                entityManager.persist(person);
            });
            Assert.fail("Should have thrown SecurityException");
        } catch (Exception e) {
            Assert.assertTrue(((e.getCause()) instanceof SecurityException));
        }
    }

    @Entity
    public static class Person {
        @Id
        private Long id;

        private String name;
    }
}

