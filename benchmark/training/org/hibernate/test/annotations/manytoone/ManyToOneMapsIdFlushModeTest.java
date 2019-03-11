/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.manytoone;


import DialectChecks.SupportsIdentityColumns;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-13044")
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class ManyToOneMapsIdFlushModeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testFlushModeCommitWithMapsIdAndIdentity() {
        final ManyToOneMapsIdFlushModeTest.ParentEntity parent = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.setFlushMode(FlushModeType.COMMIT);
            final org.hibernate.test.annotations.manytoone.ParentEntity parentEntity = new org.hibernate.test.annotations.manytoone.ParentEntity();
            parentEntity.setData("test");
            final org.hibernate.test.annotations.manytoone.ChildEntity childEntity = new org.hibernate.test.annotations.manytoone.ChildEntity();
            parentEntity.addChild(childEntity);
            entityManager.persist(parentEntity);
            entityManager.persist(childEntity);
            return parentEntity;
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.test.annotations.manytoone.ParentEntity parentEntity = entityManager.find(.class, parent.getId());
            assertNotNull(parentEntity);
            assertNotNull(parentEntity.getChildren());
            assertTrue((!(parentEntity.getChildren().isEmpty())));
            final org.hibernate.test.annotations.manytoone.ChildEntity childEntity = parentEntity.getChildren().iterator().next();
            assertNotNull(childEntity);
            assertEquals(parentEntity.getId(), childEntity.getId());
        });
    }

    @Entity(name = "ParentEntity")
    public static class ParentEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String data;

        @OneToMany(mappedBy = "parent")
        private Set<ManyToOneMapsIdFlushModeTest.ChildEntity> children = new HashSet<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public Set<ManyToOneMapsIdFlushModeTest.ChildEntity> getChildren() {
            return children;
        }

        public void setChildren(Set<ManyToOneMapsIdFlushModeTest.ChildEntity> children) {
            this.children = children;
        }

        public void addChild(ManyToOneMapsIdFlushModeTest.ChildEntity child) {
            getChildren().add(child);
            child.setParent(this);
        }
    }

    @Entity(name = "ChildEntity")
    public static class ChildEntity {
        @Id
        private Long id;

        @MapsId
        @ManyToOne(optional = false, targetEntity = ManyToOneMapsIdFlushModeTest.ParentEntity.class)
        private ManyToOneMapsIdFlushModeTest.ParentEntity parent;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public ManyToOneMapsIdFlushModeTest.ParentEntity getParent() {
            return parent;
        }

        public void setParent(ManyToOneMapsIdFlushModeTest.ParentEntity parent) {
            this.parent = parent;
        }
    }
}

