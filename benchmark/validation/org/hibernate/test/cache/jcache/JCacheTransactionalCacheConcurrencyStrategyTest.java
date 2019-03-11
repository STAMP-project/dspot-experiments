/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cache.jcache;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


public class JCacheTransactionalCacheConcurrencyStrategyTest extends BaseNonConfigCoreFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Entity(name = "Parent")
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public static class Parent {
        @Id
        @GeneratedValue
        private Long id;

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parent")
        @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
        private List<JCacheTransactionalCacheConcurrencyStrategyTest.Child> children = new ArrayList<JCacheTransactionalCacheConcurrencyStrategyTest.Child>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<JCacheTransactionalCacheConcurrencyStrategyTest.Child> getChildren() {
            return children;
        }

        public void setChildren(List<JCacheTransactionalCacheConcurrencyStrategyTest.Child> children) {
            this.children = children;
        }

        JCacheTransactionalCacheConcurrencyStrategyTest.Child addChild() {
            final JCacheTransactionalCacheConcurrencyStrategyTest.Child c = new JCacheTransactionalCacheConcurrencyStrategyTest.Child();
            c.setParent(this);
            this.children.add(c);
            return c;
        }
    }

    @Entity(name = "Child")
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public static class Child {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        private JCacheTransactionalCacheConcurrencyStrategyTest.Parent parent;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public JCacheTransactionalCacheConcurrencyStrategyTest.Parent getParent() {
            return parent;
        }

        public void setParent(JCacheTransactionalCacheConcurrencyStrategyTest.Parent parent) {
            this.parent = parent;
        }
    }

    @Test
    public void testTransactional() {
        JCacheTransactionalCacheConcurrencyStrategyTest.Parent parent = new JCacheTransactionalCacheConcurrencyStrategyTest.Parent();
        doInHibernate(this::sessionFactory, ( session) -> {
            for (int i = 0; i < 2; i++) {
                parent.addChild();
                session.persist(parent);
            }
        });
        doInHibernate(this::sessionFactory, ( session) -> {
            sqlStatementInterceptor.getSqlQueries().clear();
            org.hibernate.test.cache.jcache.Parent _parent = session.find(.class, parent.getId());
            assertEquals(0, sqlStatementInterceptor.getSqlQueries().size());
            assertEquals(2, _parent.getChildren().size());
        });
        doInHibernate(this::sessionFactory, ( session) -> {
            sqlStatementInterceptor.getSqlQueries().clear();
            org.hibernate.test.cache.jcache.Parent _parent = session.find(.class, parent.getId());
            assertEquals(2, _parent.getChildren().size());
            assertEquals(0, sqlStatementInterceptor.getSqlQueries().size());
        });
    }
}

