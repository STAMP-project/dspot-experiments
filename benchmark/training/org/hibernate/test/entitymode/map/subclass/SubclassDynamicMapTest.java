/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.entitymode.map.subclass;


import java.util.HashMap;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class SubclassDynamicMapTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testConcreateSubclassDeterminationOnEmptyDynamicMap() {
        Session s = openSession();
        s.beginTransaction();
        s.persist("Superclass", new HashMap());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.createQuery("delete Superclass").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }
}

