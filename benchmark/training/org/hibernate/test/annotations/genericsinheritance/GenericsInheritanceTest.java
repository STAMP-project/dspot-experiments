/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.genericsinheritance;


import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class GenericsInheritanceTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testMapping() throws Exception {
        Session s = openSession();
        s.close();
        // mapping is tested
    }
}

