/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.connection;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class DriverManagerConnectionProviderValidationConfigTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.connection.Event event = new org.hibernate.connection.Event();
            entityManager.persist(event);
            assertTrue(Thread.getAllStackTraces().keySet().stream().filter(( thread) -> (thread.getName().equals("Hibernate Connection Pool Validation Thread")) && (thread.isDaemon())).map(Thread::isDaemon).findAny().isPresent());
        });
    }

    @Entity(name = "Event")
    public static class Event {
        @Id
        @GeneratedValue
        private Long id;

        private String name;
    }
}

