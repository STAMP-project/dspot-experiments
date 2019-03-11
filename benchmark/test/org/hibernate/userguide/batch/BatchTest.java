/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.batch;


import org.hibernate.Session;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.userguide.model.Person;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class BatchTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testScroll() {
        withScroll();
    }

    @Test
    public void testStatelessSession() {
        withStatelessSession();
    }

    @Test
    public void testBulk() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(new Person("Vlad"));
            entityManager.persist(new Person("Mihalcea"));
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String oldName = "Vlad";
            String newName = "Alexandru";
            // tag::batch-session-jdbc-batch-size-example[]
            entityManager.unwrap(.class).setJdbcBatchSize(10);
            // end::batch-session-jdbc-batch-size-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String oldName = "Vlad";
            String newName = "Alexandru";
            // tag::batch-bulk-jpql-update-example[]
            int updatedEntities = entityManager.createQuery(("update Person p " + ("set p.name = :newName " + "where p.name = :oldName"))).setParameter("oldName", oldName).setParameter("newName", newName).executeUpdate();
            // end::batch-bulk-jpql-update-example[]
            assertEquals(1, updatedEntities);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String oldName = "Alexandru";
            String newName = "Vlad";
            Session session = entityManager.unwrap(.class);
            // tag::batch-bulk-hql-update-example[]
            int updatedEntities = session.createQuery(("update Person " + ("set name = :newName " + "where name = :oldName"))).setParameter("oldName", oldName).setParameter("newName", newName).executeUpdate();
            // end::batch-bulk-hql-update-example[]
            assertEquals(1, updatedEntities);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String oldName = "Vlad";
            String newName = "Alexandru";
            Session session = entityManager.unwrap(.class);
            // tag::batch-bulk-hql-update-version-example[]
            int updatedEntities = session.createQuery(("update versioned Person " + ("set name = :newName " + "where name = :oldName"))).setParameter("oldName", oldName).setParameter("newName", newName).executeUpdate();
            // end::batch-bulk-hql-update-version-example[]
            assertEquals(1, updatedEntities);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String name = "Alexandru";
            // tag::batch-bulk-jpql-delete-example[]
            int deletedEntities = entityManager.createQuery(("delete Person p " + "where p.name = :name")).setParameter("name", name).executeUpdate();
            // end::batch-bulk-jpql-delete-example[]
            assertEquals(1, deletedEntities);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::batch-bulk-hql-insert-example[]
            int insertedEntities = session.createQuery(("insert into Partner (id, name) " + ("select p.id, p.name " + "from Person p "))).executeUpdate();
            // end::batch-bulk-hql-insert-example[]
            assertEquals(1, insertedEntities);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String name = "Mihalcea";
            Session session = entityManager.unwrap(.class);
            // tag::batch-bulk-hql-delete-example[]
            int deletedEntities = session.createQuery(("delete Person " + "where name = :name")).setParameter("name", name).executeUpdate();
            // end::batch-bulk-hql-delete-example[]
            assertEquals(1, deletedEntities);
        });
    }
}

