/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.mapping;


import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClassVisitor;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 * Simple smoke style tests to make sure visitors keep working.
 *
 * @author max
 */
public class PersistentClassVisitorTest extends BaseUnitTestCase {
    private StandardServiceRegistry serviceRegistry;

    private MetadataBuildingContext metadataBuildingContext;

    @Test
    public void testProperCallbacks() {
        PersistentClassVisitorTest.PersistentClassVisitorValidator vv = new PersistentClassVisitorTest.PersistentClassVisitorValidator();
        accept(vv);
        accept(vv);
        accept(vv);
        accept(vv);
        accept(vv);
    }

    public static class PersistentClassVisitorValidator implements PersistentClassVisitor {
        private Object validate(Class expectedClass, Object visitee) {
            if (!(visitee.getClass().getName().equals(expectedClass.getName()))) {
                throw new IllegalStateException((((visitee.getClass().getName()) + " did not call proper accept method. Was ") + (expectedClass.getName())));
            }
            return null;
        }

        public Object accept(RootClass class1) {
            return validate(RootClass.class, class1);
        }

        public Object accept(UnionSubclass subclass) {
            return validate(UnionSubclass.class, subclass);
        }

        public Object accept(SingleTableSubclass subclass) {
            return validate(SingleTableSubclass.class, subclass);
        }

        public Object accept(JoinedSubclass subclass) {
            return validate(JoinedSubclass.class, subclass);
        }

        public Object accept(Subclass subclass) {
            return validate(Subclass.class, subclass);
        }
    }
}

