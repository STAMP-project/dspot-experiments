/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.collection.custom.declaredtype;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.usertype.UserCollectionType;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Max Rydahl Andersen
 * @author David Weinberg
 */
public abstract class UserCollectionTypeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testBasicOperation() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        UserCollectionTypeTest.User u = new UserCollectionTypeTest.User("max");
        u.getEmailAddresses().add(new Email("max@hibernate.org"));
        u.getEmailAddresses().add(new Email("max.andersen@jboss.com"));
        s.persist(u);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        UserCollectionTypeTest.User u2 = ((UserCollectionTypeTest.User) (s.createCriteria(UserCollectionTypeTest.User.class).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(u2.getEmailAddresses()));
        Assert.assertEquals(u2.getEmailAddresses().size(), 2);
        Assert.assertNotNull(u2.getEmailAddresses().head());
        t.commit();
        s.close();
    }

    /**
     * A custom collection class that implements a simple method just for illustration.
     * We extend a java.util.Collection class which is required for annotations-based entities, but not xml-based.
     *
     * @author David Weinberg
     */
    public static class HeadList<X> extends ArrayList<X> implements UserCollectionTypeTest.IHeadList<X> {
        @Override
        public X head() {
            return isEmpty() ? null : get(0);
        }
    }

    public static class HeadListType implements UserCollectionType {
        @Override
        public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister) throws HibernateException {
            return new UserCollectionTypeTest.PersistentHeadList(session);
        }

        @Override
        public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
            return new UserCollectionTypeTest.PersistentHeadList(session, ((UserCollectionTypeTest.IHeadList) (collection)));
        }

        public Iterator getElementsIterator(Object collection) {
            return ((UserCollectionTypeTest.IHeadList) (collection)).iterator();
        }

        public boolean contains(Object collection, Object entity) {
            return ((UserCollectionTypeTest.IHeadList) (collection)).contains(entity);
        }

        public Object indexOf(Object collection, Object entity) {
            int l = ((UserCollectionTypeTest.IHeadList) (collection)).indexOf(entity);
            if (l < 0) {
                return null;
            } else {
                return l;
            }
        }

        @Override
        public Object replaceElements(Object original, Object target, CollectionPersister persister, Object owner, Map copyCache, SharedSessionContractImplementor session) throws HibernateException {
            UserCollectionTypeTest.IHeadList result = ((UserCollectionTypeTest.IHeadList) (target));
            result.clear();
            result.addAll(((UserCollectionTypeTest.HeadList) (original)));
            return result;
        }

        public Object instantiate(int anticipatedSize) {
            return new UserCollectionTypeTest.HeadList();
        }
    }

    public interface IHeadList<X> extends List<X> {
        X head();
    }

    public static class PersistentHeadList extends PersistentList implements UserCollectionTypeTest.IHeadList {
        public PersistentHeadList(SharedSessionContractImplementor session) {
            super(session);
        }

        public PersistentHeadList(SharedSessionContractImplementor session, UserCollectionTypeTest.IHeadList list) {
            super(session, list);
        }

        @Override
        public Object head() {
            return ((UserCollectionTypeTest.IHeadList) (list)).head();
        }
    }

    /**
     *
     *
     * @author Gavin King
     * @author Steve Ebersole
     */
    @Entity
    @Table(name = "UC_BSC_USER")
    @TypeDef(name = "HeadListType", typeClass = UserCollectionTypeTest.HeadListType.class)
    public static class User {
        private String userName;

        private UserCollectionTypeTest.IHeadList<Email> emailAddresses = new UserCollectionTypeTest.HeadList<Email>();

        private Map sessionData = new HashMap();

        User() {
        }

        public User(String name) {
            userName = name;
        }

        @Id
        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
        @CollectionType(type = "HeadListType")
        @JoinColumn(name = "userName")
        @OrderColumn(name = "displayOrder")
        public UserCollectionTypeTest.IHeadList<Email> getEmailAddresses() {
            // can declare a custom interface type
            return emailAddresses;
        }

        public void setEmailAddresses(UserCollectionTypeTest.IHeadList<Email> emailAddresses) {
            this.emailAddresses = emailAddresses;
        }

        @Transient
        public Map getSessionData() {
            return sessionData;
        }

        public void setSessionData(Map sessionData) {
            this.sessionData = sessionData;
        }
    }
}

