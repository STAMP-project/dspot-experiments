/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy;


import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-9937")
@RunWith(BytecodeEnhancerRunner.class)
public class LazyBasicFieldNotInitializedTest extends BaseCoreFunctionalTestCase {
    private Long entityId;

    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.TestEntity entity = s.get(.class, entityId);
            Assert.assertFalse(Hibernate.isPropertyInitialized(entity, "description"));
            EntityPersister entityPersister = sessionFactory().getMetamodel().entityPersister(.class);
            boolean[] propertyLaziness = entityPersister.getPropertyLaziness();
            assertEquals(1, propertyLaziness.length);
            assertTrue(propertyLaziness[0]);
            // Make sure NonIdentifierAttribute#isLazy is consistent (HHH-10551)
            NonIdentifierAttribute[] properties = entityPersister.getEntityMetamodel().getProperties();
            assertEquals(1, properties.length);
            assertTrue(properties[0].isLazy());
        });
    }

    // --- //
    @Entity
    @Table(name = "TEST_ENTITY")
    private static class TestEntity {
        @Id
        @GeneratedValue
        Long id;

        @Basic(fetch = FetchType.LAZY)
        String description;
    }
}

