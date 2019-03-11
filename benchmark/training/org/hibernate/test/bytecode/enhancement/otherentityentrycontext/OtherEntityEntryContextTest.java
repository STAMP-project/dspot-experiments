/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.otherentityentrycontext;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This task tests ManagedEntity objects that are already associated with a different PersistenceContext.
 *
 * @author Gail Badner
 */
@RunWith(BytecodeEnhancerRunner.class)
public class OtherEntityEntryContextTest extends BaseCoreFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.otherentityentrycontext.Parent p = s.get(.class, 1L);
            assertTrue(.class.isInstance(p));
            p.name = "second";
            assertTrue(s.contains(p));
            // open another session and evict p from the new session
            doInHibernate(this::sessionFactory, ( session2) -> {
                // s2 should contains no entities
                assertFalse(session2.contains(p));
                // evict should do nothing, since p is not associated with s2
                session2.evict(p);
                assertFalse(session2.contains(p));
                assertNull(((SharedSessionContractImplementor) (session2)).getPersistenceContext().getEntry(p));
                try {
                    session2.update(p);
                    fail("should have failed because p is already associated with a PersistenceContext that is still open.");
                } catch ( ignored) {
                    // expected
                }
            });
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.otherentityentrycontext.Parent p = s.get(.class, 1L);
            p.name = "third";
            s.update(p);
            assertTrue(s.contains(p));
            s.evict(p);
            assertFalse(s.contains(p));
            p = s.get(.class, p.id);
            assertEquals("second", p.name);
        });
    }

    // --- //
    @Entity
    @Table(name = "PARENT")
    private static class Parent {
        @Id
        Long id;

        String name;

        Parent() {
        }

        Parent(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

