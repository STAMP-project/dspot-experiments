/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12251")
public class IdClassManyToOneCascadeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "HHH-12251")
    public void testMergeCascadesToManyToOne() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.id.ReferencedEntity referencedEntity = new org.hibernate.id.ReferencedEntity();
            referencedEntity.setId(42L);
            org.hibernate.id.SomeEntity someEntity = new org.hibernate.id.SomeEntity();
            someEntity.setId(23L);
            someEntity.setReferencedEntity(referencedEntity);
            entityManager.merge(someEntity);
            assertTrue(entityManager.contains(referencedEntity));
        });
    }

    @Test
    public void testPersistCascadesToManyToOne() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.id.ReferencedEntity referencedEntity = new org.hibernate.id.ReferencedEntity();
            referencedEntity.setId(42L);
            org.hibernate.id.SomeEntity someEntity = new org.hibernate.id.SomeEntity();
            someEntity.setId(23L);
            someEntity.setReferencedEntity(referencedEntity);
            entityManager.persist(someEntity);
            assertTrue(entityManager.contains(referencedEntity));
        });
    }

    @Entity(name = "SomeEntity")
    @IdClass(IdClassManyToOneCascadeTest.SomeEntityPK.class)
    public static class SomeEntity {
        @Id
        private long id;

        @Id
        @ManyToOne
        private IdClassManyToOneCascadeTest.ReferencedEntity referencedEntity;

        public IdClassManyToOneCascadeTest.ReferencedEntity getReferencedEntity() {
            return referencedEntity;
        }

        public void setReferencedEntity(IdClassManyToOneCascadeTest.ReferencedEntity referencedEntity) {
            this.referencedEntity = referencedEntity;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public static class SomeEntityPK implements Serializable {
        private Long id;

        private Long referencedEntity;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getReferencedEntity() {
            return referencedEntity;
        }

        public void setReferencedEntity(Long referencedEntity) {
            this.referencedEntity = referencedEntity;
        }
    }

    @Entity(name = "ReferencedEntity")
    public static class ReferencedEntity {
        @Id
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}

