/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idgen.identity.joinedSubClass;


import DialectChecks.SupportsIdentityColumns;
import org.hibernate.Session;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Vlasov
 * @author Steve Ebersole
 */
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class JoinedSubclassHierarchyWithIdentityGenerationTest extends BaseCoreFunctionalTestCase {
    @Test
    public void shouldPersistDebtorAccountWhenParentServiceAgreementPersisted() {
        Session s = openSession();
        s.beginTransaction();
        s.save(new Sub());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.createQuery("delete Sub").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }
}

