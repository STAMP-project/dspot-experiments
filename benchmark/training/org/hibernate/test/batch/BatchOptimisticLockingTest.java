/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.batch;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OptimisticLockException;
import javax.persistence.Version;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class BatchOptimisticLockingTest extends BaseNonConfigCoreFunctionalTestCase {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Test
    public void testBatchAndOptimisticLocking() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.batch.Person person1 = new org.hibernate.test.batch.Person();
            person1.id = 1L;
            person1.name = "First";
            session.persist(person1);
            org.hibernate.test.batch.Person person2 = new org.hibernate.test.batch.Person();
            person2.id = 2L;
            person2.name = "Second";
            session.persist(person2);
            org.hibernate.test.batch.Person person3 = new org.hibernate.test.batch.Person();
            person3.id = 3L;
            person3.name = "Third";
            session.persist(person3);
        });
        try {
            TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
                List<org.hibernate.test.batch.Person> persons = session.createQuery("select p from Person p").getResultList();
                for (int i = 0; i < (persons.size()); i++) {
                    org.hibernate.test.batch.Person person = persons.get(i);
                    person.name += " Person";
                    if (i == 1) {
                        try {
                            executorService.submit(() -> {
                                doInHibernate(this::sessionFactory, ( _session) -> {
                                    org.hibernate.test.batch.Person _person = _session.find(.class, person.id);
                                    _person.name += " Person is the new Boss!";
                                });
                            }).get();
                        } catch (InterruptedException | ExecutionException e) {
                            fail(e.getMessage());
                        }
                    }
                }
            });
        } catch (Exception expected) {
            Assert.assertEquals(OptimisticLockException.class, expected.getClass());
        }
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String name;

        @Version
        private long version;
    }
}

