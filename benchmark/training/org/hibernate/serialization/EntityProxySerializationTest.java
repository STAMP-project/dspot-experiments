/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.serialization;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.internal.util.SerializationHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Selaron
 */
public class EntityProxySerializationTest extends BaseCoreFunctionalTestCase {
    /**
     * Tests that serializing an initialized proxy will serialize the target instead.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testInitializedProxySerializationIfTargetInPersistenceContext() {
        final Session s = openSession();
        final Transaction t = s.beginTransaction();
        try {
            final EntityProxySerializationTest.ChildEntity child = s.find(EntityProxySerializationTest.ChildEntity.class, 1L);
            final EntityProxySerializationTest.SimpleEntity parent = child.getParent();
            // assert we have an uninitialized proxy
            Assert.assertTrue((parent instanceof HibernateProxy));
            Assert.assertFalse(Hibernate.isInitialized(parent));
            // Initialize the proxy
            parent.getName();
            Assert.assertTrue(Hibernate.isInitialized(parent));
            // serialize/deserialize the proxy
            final EntityProxySerializationTest.SimpleEntity deserializedParent = ((EntityProxySerializationTest.SimpleEntity) (SerializationHelper.clone(parent)));
            // assert the deserialized object is no longer a proxy, but the target of the proxy
            Assert.assertFalse((deserializedParent instanceof HibernateProxy));
            Assert.assertEquals("TheParent", deserializedParent.getName());
        } finally {
            if (t.isActive()) {
                t.rollback();
            }
            s.close();
        }
    }

    /**
     * Tests that serializing a proxy which is not initialized
     * but whose target has been (separately) added to the persistence context
     * will serialize the target instead.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testUninitializedProxySerializationIfTargetInPersistenceContext() {
        final Session s = openSession();
        final Transaction t = s.beginTransaction();
        try {
            final EntityProxySerializationTest.ChildEntity child = s.find(EntityProxySerializationTest.ChildEntity.class, 1L);
            final EntityProxySerializationTest.SimpleEntity parent = child.getParent();
            // assert we have an uninitialized proxy
            Assert.assertTrue((parent instanceof HibernateProxy));
            Assert.assertFalse(Hibernate.isInitialized(parent));
            // Load the target of the proxy without the proxy being made aware of it
            s.detach(parent);
            s.find(EntityProxySerializationTest.SimpleEntity.class, 1L);
            s.update(parent);
            // assert we still have an uninitialized proxy
            Assert.assertFalse(Hibernate.isInitialized(parent));
            // serialize/deserialize the proxy
            final EntityProxySerializationTest.SimpleEntity deserializedParent = ((EntityProxySerializationTest.SimpleEntity) (SerializationHelper.clone(parent)));
            // assert the deserialized object is no longer a proxy, but the target of the proxy
            Assert.assertFalse((deserializedParent instanceof HibernateProxy));
            Assert.assertEquals("TheParent", deserializedParent.getName());
        } finally {
            if (t.isActive()) {
                t.rollback();
            }
            s.close();
        }
    }

    /**
     * Tests that lazy loading without transaction nor open session is generally
     * working. The magic is done by {@link AbstractLazyInitializer} who opens a
     * temporary session.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testProxyInitializationWithoutTX() {
        final Session s = openSession();
        final Transaction t = s.beginTransaction();
        try {
            final EntityProxySerializationTest.ChildEntity child = s.find(EntityProxySerializationTest.ChildEntity.class, 1L);
            final EntityProxySerializationTest.SimpleEntity parent = child.getParent();
            t.rollback();
            session.close();
            // assert we have an uninitialized proxy
            Assert.assertTrue((parent instanceof HibernateProxy));
            Assert.assertFalse(Hibernate.isInitialized(parent));
            Assert.assertEquals("TheParent", parent.getName());
            // assert we have an initialized proxy now
            Assert.assertTrue(Hibernate.isInitialized(parent));
        } finally {
            if (t.isActive()) {
                t.rollback();
            }
            s.close();
        }
    }

    /**
     * Tests that lazy loading without transaction nor open session is generally
     * working. The magic is done by {@link AbstractLazyInitializer} who opens a
     * temporary session.
     */
    @SuppressWarnings("unchecked")
    @Test
    @TestForIssue(jiraKey = "HHH-12720")
    public void testProxyInitializationWithoutTXAfterDeserialization() {
        final Session s = openSession();
        final Transaction t = s.beginTransaction();
        try {
            final EntityProxySerializationTest.ChildEntity child = s.find(EntityProxySerializationTest.ChildEntity.class, 1L);
            final EntityProxySerializationTest.SimpleEntity parent = child.getParent();
            // destroy AbstractLazyInitializer internal state
            final EntityProxySerializationTest.SimpleEntity deserializedParent = ((EntityProxySerializationTest.SimpleEntity) (SerializationHelper.clone(parent)));
            t.rollback();
            session.close();
            // assert we have an uninitialized proxy
            Assert.assertTrue((deserializedParent instanceof HibernateProxy));
            Assert.assertFalse(Hibernate.isInitialized(deserializedParent));
            Assert.assertEquals("TheParent", deserializedParent.getName());
            // assert we have an initialized proxy now
            Assert.assertTrue(Hibernate.isInitialized(deserializedParent));
        } finally {
            if (t.isActive()) {
                t.rollback();
            }
            s.close();
        }
    }

    @Entity(name = "SimpleEntity")
    static class SimpleEntity implements Serializable {
        private Long id;

        private String name;

        Set<EntityProxySerializationTest.ChildEntity> children = new HashSet<>();

        @Id
        public Long getId() {
            return id;
        }

        public void setId(final Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @OneToMany(targetEntity = EntityProxySerializationTest.ChildEntity.class, mappedBy = "parent")
        @LazyCollection(LazyCollectionOption.EXTRA)
        @Fetch(FetchMode.SELECT)
        public Set<EntityProxySerializationTest.ChildEntity> getChildren() {
            return children;
        }

        public void setChildren(final Set<EntityProxySerializationTest.ChildEntity> children) {
            this.children = children;
        }
    }

    @Entity(name = "ChildEntity")
    static class ChildEntity {
        private Long id;

        private EntityProxySerializationTest.SimpleEntity parent;

        @Id
        public Long getId() {
            return id;
        }

        public void setId(final Long id) {
            this.id = id;
        }

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn
        @LazyToOne(LazyToOneOption.PROXY)
        public EntityProxySerializationTest.SimpleEntity getParent() {
            return parent;
        }

        public void setParent(final EntityProxySerializationTest.SimpleEntity parent) {
            this.parent = parent;
        }
    }
}

