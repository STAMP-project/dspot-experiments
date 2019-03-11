/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.set;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-11881")
public class SetElementNullBasicTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testPersistNullValue() {
        int entityId = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.set.AnEntity e = new org.hibernate.test.collection.set.AnEntity();
            e.aCollection.add(null);
            session.persist(e);
            return e.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.set.AnEntity e = session.get(.class, entityId);
            assertEquals(0, e.aCollection.size());
            assertEquals(0, getCollectionElementRows(entityId).size());
            session.delete(e);
        });
    }

    @Test
    public void addNullValue() {
        int entityId = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.set.AnEntity e = new org.hibernate.test.collection.set.AnEntity();
            session.persist(e);
            return e.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.set.AnEntity e = session.get(.class, entityId);
            assertEquals(0, e.aCollection.size());
            assertEquals(0, getCollectionElementRows(entityId).size());
            e.aCollection.add(null);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.set.AnEntity e = session.get(.class, entityId);
            assertEquals(0, e.aCollection.size());
            assertEquals(0, getCollectionElementRows(entityId).size());
            session.delete(e);
        });
    }

    @Test
    public void testUpdateNonNullValueToNull() {
        int entityId = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.set.AnEntity e = new org.hibernate.test.collection.set.AnEntity();
            e.aCollection.add("def");
            session.persist(e);
            return e.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.set.AnEntity e = session.get(.class, entityId);
            assertEquals(1, e.aCollection.size());
            assertEquals(1, getCollectionElementRows(entityId).size());
            e.aCollection.remove("def");
            e.aCollection.add(null);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.set.AnEntity e = session.get(.class, entityId);
            assertEquals(0, e.aCollection.size());
            assertEquals(0, getCollectionElementRows(entityId).size());
            session.delete(e);
        });
    }

    @Entity
    @Table(name = "AnEntity")
    public static class AnEntity {
        @Id
        @GeneratedValue
        private int id;

        @ElementCollection
        @CollectionTable(name = "AnEntity_aCollection", joinColumns = { @JoinColumn(name = "AnEntity_id") })
        private Set<String> aCollection = new HashSet<>();
    }
}

