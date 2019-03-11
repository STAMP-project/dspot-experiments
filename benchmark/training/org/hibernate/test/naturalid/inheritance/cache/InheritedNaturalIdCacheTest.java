/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.naturalid.inheritance.cache;


import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


public class InheritedNaturalIdCacheTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testLoadingInheritedEntitiesByNaturalId() {
        // create the data:
        // MyEntity#1
        // ExtendedEntity#1
        inTransaction(( session) -> {
            session.save(new MyEntity("base"));
            session.save(new ExtendedEntity("extended", "ext"));
        });
        // load the entities "properly" by natural-id
        inTransaction(( session) -> {
            final MyEntity entity = session.bySimpleNaturalId(.class).load("base");
            assertNotNull(entity);
            final ExtendedEntity extendedEntity = session.bySimpleNaturalId(.class).load("extended");
            assertNotNull(extendedEntity);
        });
        // finally, attempt to load MyEntity#1 as an ExtendedEntity, which should
        // throw a WrongClassException
        inTransaction(( session) -> {
            try {
                session.bySimpleNaturalId(.class).load("base");
                fail("Expecting WrongClassException");
            } catch ( expected) {
                // expected outcome
            } catch ( other) {
                throw new <other>AssertionError(("Unexpected exception type : " + (other.getClass().getName())));
            }
        });
        // this is functionally equivalent to loading the wrong class by id...
        inTransaction(( session) -> {
            try {
                session.byId(.class).load(1L);
                fail("Expecting WrongClassException");
            } catch ( expected) {
                // expected outcome
            } catch ( other) {
                throw new <other>AssertionError(("Unexpected exception type : " + (other.getClass().getName())));
            }
        });
    }
}

