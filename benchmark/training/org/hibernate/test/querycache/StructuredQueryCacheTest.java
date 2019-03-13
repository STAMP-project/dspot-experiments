/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.querycache;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalceca
 */
@TestForIssue(jiraKey = "HHH-12107")
public class StructuredQueryCacheTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12107")
    public void testEmbeddedIdInOneToMany() {
        StructuredQueryCacheTest.OneToManyWithEmbeddedIdKey key = new StructuredQueryCacheTest.OneToManyWithEmbeddedIdKey(1234);
        final StructuredQueryCacheTest.OneToManyWithEmbeddedId o = new StructuredQueryCacheTest.OneToManyWithEmbeddedId(key);
        o.setItems(new HashSet<>());
        o.getItems().add(new StructuredQueryCacheTest.OneToManyWithEmbeddedIdChild(1));
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(o);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.querycache.OneToManyWithEmbeddedId _entity = session.find(.class, key);
            assertTrue(session.getSessionFactory().getCache().containsEntity(.class, key));
            assertNotNull(_entity);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.querycache.OneToManyWithEmbeddedId _entity = session.find(.class, key);
            assertTrue(session.getSessionFactory().getCache().containsEntity(.class, key));
            assertNotNull(_entity);
        });
    }

    @Entity(name = "OneToManyWithEmbeddedId")
    public static class OneToManyWithEmbeddedId {
        private StructuredQueryCacheTest.OneToManyWithEmbeddedIdKey id;

        private Set<StructuredQueryCacheTest.OneToManyWithEmbeddedIdChild> items = new HashSet<>();

        public OneToManyWithEmbeddedId() {
        }

        public OneToManyWithEmbeddedId(StructuredQueryCacheTest.OneToManyWithEmbeddedIdKey id) {
            this.id = id;
        }

        @EmbeddedId
        public StructuredQueryCacheTest.OneToManyWithEmbeddedIdKey getId() {
            return id;
        }

        public void setId(StructuredQueryCacheTest.OneToManyWithEmbeddedIdKey id) {
            this.id = id;
        }

        @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = StructuredQueryCacheTest.OneToManyWithEmbeddedIdChild.class, orphanRemoval = true)
        @JoinColumn(name = "parent_id")
        public Set<StructuredQueryCacheTest.OneToManyWithEmbeddedIdChild> getItems() {
            return items;
        }

        public void setItems(Set<StructuredQueryCacheTest.OneToManyWithEmbeddedIdChild> items) {
            this.items = items;
        }
    }

    @Entity(name = "OneToManyWithEmbeddedIdChild")
    public static class OneToManyWithEmbeddedIdChild {
        private Integer id;

        public OneToManyWithEmbeddedIdChild() {
        }

        public OneToManyWithEmbeddedIdChild(Integer id) {
            this.id = id;
        }

        @Id
        @Column(name = "id")
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @Embeddable
    public static class OneToManyWithEmbeddedIdKey implements Serializable {
        private Integer id;

        public OneToManyWithEmbeddedIdKey() {
        }

        public OneToManyWithEmbeddedIdKey(Integer id) {
            this.id = id;
        }

        @Column(name = "id")
        public Integer getId() {
            return this.id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}

