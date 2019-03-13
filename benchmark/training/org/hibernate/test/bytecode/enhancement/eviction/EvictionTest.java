/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.eviction;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.engine.spi.ManagedEntity;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Gail Badner
 */
@RunWith(BytecodeEnhancerRunner.class)
public class EvictionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            // Delete the Parent
            org.hibernate.test.bytecode.enhancement.eviction.Parent loadedParent = ((org.hibernate.test.bytecode.enhancement.eviction.Parent) (s.createQuery("SELECT p FROM Parent p WHERE name=:name").setParameter("name", "PARENT").uniqueResult()));
            assertTyping(.class, loadedParent);
            ManagedEntity managedParent = ((ManagedEntity) (loadedParent));
            // before eviction
            assertNotNull(managedParent.$$_hibernate_getEntityInstance());
            assertNotNull(managedParent.$$_hibernate_getEntityEntry());
            assertNull(managedParent.$$_hibernate_getPreviousManagedEntity());
            assertNull(managedParent.$$_hibernate_getNextManagedEntity());
            assertTrue(s.contains(managedParent));
            s.evict(managedParent);
            // after eviction
            assertFalse(s.contains(managedParent));
            assertNotNull(managedParent.$$_hibernate_getEntityInstance());
            assertNull(managedParent.$$_hibernate_getEntityEntry());
            assertNull(managedParent.$$_hibernate_getPreviousManagedEntity());
            assertNull(managedParent.$$_hibernate_getNextManagedEntity());
            // evict again
            s.evict(managedParent);
            assertFalse(s.contains(managedParent));
            assertNotNull(managedParent.$$_hibernate_getEntityInstance());
            assertNull(managedParent.$$_hibernate_getEntityEntry());
            assertNull(managedParent.$$_hibernate_getPreviousManagedEntity());
            assertNull(managedParent.$$_hibernate_getNextManagedEntity());
            s.delete(managedParent);
        });
    }

    // --- //
    @Entity(name = "Parent")
    @Table(name = "PARENT")
    private static class Parent {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;

        String name;
    }
}

