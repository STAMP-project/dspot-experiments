/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.naturalid.inheritance.cache;


import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


public class InheritedNaturalIdNoCacheTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testLoadExtendedByNormal() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            MyEntity user = session.byNaturalId(.class).using("uid", "base").load();
            ExtendedEntity extendedMyEntity = session.byNaturalId(.class).using("uid", "extended").load();
            assertNotNull(user);
            assertNotNull(extendedMyEntity);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            ExtendedEntity user = session.byNaturalId(.class).using("uid", "base").load();
            assertNull(user);
        });
    }
}

