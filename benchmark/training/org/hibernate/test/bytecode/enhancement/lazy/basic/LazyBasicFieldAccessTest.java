/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy.basic;


import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Gail Badner
 */
@RunWith(BytecodeEnhancerRunner.class)
public class LazyBasicFieldAccessTest extends BaseCoreFunctionalTestCase {
    private LazyBasicFieldAccessTest.LazyEntity entity;

    private Long entityId;

    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            entity = s.get(.class, entityId);
            Assert.assertFalse(isPropertyInitialized(entity, "description"));
            checkDirtyTracking(entity);
            assertEquals("desc", entity.getDescription());
            assertTrue(isPropertyInitialized(entity, "description"));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            entity.setDescription("desc1");
            s.update(entity);
            // Assert.assertFalse( Hibernate.isPropertyInitialized( entity, "description" ) );
            checkDirtyTracking(entity, "description");
            assertEquals("desc1", entity.getDescription());
            assertTrue(isPropertyInitialized(entity, "description"));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            entity = s.get(.class, entityId);
            assertEquals("desc1", entity.getDescription());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            entity.setDescription("desc2");
            org.hibernate.test.bytecode.enhancement.lazy.basic.LazyEntity mergedEntity = ((org.hibernate.test.bytecode.enhancement.lazy.basic.LazyEntity) (s.merge(entity)));
            // Assert.assertFalse( isPropertyInitialized( entity, "description" ) );
            checkDirtyTracking(mergedEntity, "description");
            assertEquals("desc2", mergedEntity.getDescription());
            assertTrue(isPropertyInitialized(mergedEntity, "description"));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.basic.LazyEntity entity = s.get(.class, entityId);
            assertEquals("desc2", entity.getDescription());
        });
    }

    // --- //
    @Entity
    @Table(name = "LAZY_FIELD_ENTITY")
    private static class LazyEntity {
        Long id;

        String description;

        @Id
        @GeneratedValue
        Long getId() {
            return id;
        }

        void setId(Long id) {
            this.id = id;
        }

        @Basic(fetch = FetchType.LAZY)
        String getDescription() {
            return description;
        }

        void setDescription(String description) {
            this.description = description;
        }
    }
}

