/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.bag;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class BagElementNullBasicTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testPersistNullValue() {
        int entityId = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.AnEntity e = new org.hibernate.test.collection.bag.AnEntity();
            e.aCollection.add(null);
            session.persist(e);
            return e.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.AnEntity e = session.get(.class, entityId);
            assertEquals(0, e.aCollection.size());
            assertEquals(0, getCollectionElementRows(entityId).size());
            session.delete(e);
        });
    }

    @Test
    public void addNullValue() {
        int entityId = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.AnEntity e = new org.hibernate.test.collection.bag.AnEntity();
            session.persist(e);
            return e.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.AnEntity e = session.get(.class, entityId);
            assertEquals(0, e.aCollection.size());
            assertEquals(0, getCollectionElementRows(entityId).size());
            e.aCollection.add(null);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.AnEntity e = session.get(.class, entityId);
            assertEquals(0, e.aCollection.size());
            assertEquals(0, getCollectionElementRows(entityId).size());
            session.delete(e);
        });
    }

    @Test
    public void testUpdateNonNullValueToNull() {
        int entityId = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.AnEntity e = new org.hibernate.test.collection.bag.AnEntity();
            e.aCollection.add("def");
            session.persist(e);
            return e.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.AnEntity e = session.get(.class, entityId);
            assertEquals(1, e.aCollection.size());
            assertEquals(1, getCollectionElementRows(entityId).size());
            e.aCollection.set(0, null);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.AnEntity e = session.get(.class, entityId);
            assertEquals(0, e.aCollection.size());
            assertEquals(0, getCollectionElementRows(entityId).size());
            session.delete(e);
        });
    }

    @Test
    public void testUpdateNonNullValueToNullWithExtraValue() {
        int entityId = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.AnEntity e = new org.hibernate.test.collection.bag.AnEntity();
            e.aCollection.add("def");
            e.aCollection.add("ghi");
            session.persist(e);
            return e.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.AnEntity e = session.get(.class, entityId);
            assertEquals(2, e.aCollection.size());
            assertEquals(2, getCollectionElementRows(e.id).size());
            e.aCollection.set(0, null);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.AnEntity e = session.get(.class, entityId);
            assertEquals(1, e.aCollection.size());
            assertEquals(1, getCollectionElementRows(e.id).size());
            assertEquals("ghi", e.aCollection.get(0));
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
        @OrderBy
        private List<String> aCollection = new ArrayList<String>();
    }
}

