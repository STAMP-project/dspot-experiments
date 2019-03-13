/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.enhancement;


import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.testing.transaction.TransactionUtil.JPATransactionVoidFunction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@TestForIssue(jiraKey = "HHH-7573")
@RunWith(BytecodeEnhancerRunner.class)
public class TestLazyPropertyOnPreUpdate extends BaseEntityManagerFunctionalTestCase {
    private TestLazyPropertyOnPreUpdate.EntityWithLazyProperty entity;

    /**
     * Set a non lazy field, therefore the lazyData field will be LazyPropertyInitializer.UNFETCHED_PROPERTY
     * for both state and newState so the field should not change. This should no longer cause a ClassCastException.
     */
    @Test
    public void testNoUpdate() {
        byte[] testArray = new byte[]{ 42 };
        TransactionUtil.doInJPA(this::entityManagerFactory, new JPATransactionVoidFunction() {
            @Override
            public void accept(EntityManager em) {
                entity = em.find(TestLazyPropertyOnPreUpdate.EntityWithLazyProperty.class, entity.id);
                entity.setSomeField("TEST1");
                Assert.assertFalse(Hibernate.isPropertyInitialized(entity, "lazyData"));
            }

            @Override
            public void afterTransactionCompletion() {
                Assert.assertFalse(Hibernate.isPropertyInitialized(entity, "lazyData"));
            }
        });
        checkLazyField(entity, testArray);
    }

    /**
     * Set the updateLazyFieldInPreUpdate flag so that the lazy field is updated from within the
     * PreUpdate annotated callback method. So state == LazyPropertyInitializer.UNFETCHED_PROPERTY and
     * newState == EntityWithLazyProperty.PRE_UPDATE_VALUE. This should no longer cause a ClassCastException.
     */
    @Test
    public void testPreUpdate() {
        TransactionUtil.doInJPA(this::entityManagerFactory, new JPATransactionVoidFunction() {
            @Override
            public void accept(EntityManager em) {
                entity = em.find(TestLazyPropertyOnPreUpdate.EntityWithLazyProperty.class, entity.id);
                entity.setUpdateLazyFieldInPreUpdate(true);
                entity.setSomeField("TEST2");
                Assert.assertFalse(Hibernate.isPropertyInitialized(entity, "lazyData"));
            }

            @Override
            public void afterTransactionCompletion() {
                Assert.assertTrue(Hibernate.isPropertyInitialized(entity, "lazyData"));
            }
        });
        checkLazyField(entity, TestLazyPropertyOnPreUpdate.EntityWithLazyProperty.PRE_UPDATE_VALUE);
    }

    /**
     * Set the updateLazyFieldInPreUpdate flag so that the lazy field is updated from within the
     * PreUpdate annotated callback method and also set the lazyData field directly to testArray1. When we reload we
     * should get EntityWithLazyProperty.PRE_UPDATE_VALUE.
     */
    @Test
    public void testPreUpdateOverride() {
        byte[] testArray = new byte[]{ 42 };
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            entity = em.find(.class, entity.id);
            entity.setUpdateLazyFieldInPreUpdate(true);
            assertFalse(Hibernate.isPropertyInitialized(entity, "lazyData"));
            entity.setLazyData(testArray);
            assertTrue(Hibernate.isPropertyInitialized(entity, "lazyData"));
            entity.setSomeField("TEST3");
        });
        checkLazyField(entity, TestLazyPropertyOnPreUpdate.EntityWithLazyProperty.PRE_UPDATE_VALUE);
    }

    // --- //
    /**
     * Test entity with a lazy property which requires build time instrumentation.
     *
     * @author Martin Ball
     */
    @Entity
    @Table(name = "ENTITY_WITH_LAZY_PROPERTY")
    private static class EntityWithLazyProperty {
        public static final byte[] PRE_UPDATE_VALUE = new byte[]{ 42, 42, 42, 42 };

        @Id
        @GeneratedValue
        private Long id;

        @Basic(fetch = FetchType.LAZY)
        private byte[] lazyData;

        private String someField;

        private boolean updateLazyFieldInPreUpdate;

        public void setLazyData(byte[] lazyData) {
            this.lazyData = lazyData;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }

        public void setUpdateLazyFieldInPreUpdate(boolean updateLazyFieldInPreUpdate) {
            this.updateLazyFieldInPreUpdate = updateLazyFieldInPreUpdate;
        }

        @PreUpdate
        public void onPreUpdate() {
            // Allow the update of the lazy field from within the pre update to check that this does not break things.
            if (updateLazyFieldInPreUpdate) {
                this.lazyData = TestLazyPropertyOnPreUpdate.EntityWithLazyProperty.PRE_UPDATE_VALUE;
            }
        }
    }
}

