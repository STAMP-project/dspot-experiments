/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.fetchprofiles.join.selfReferencing;


import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class JoinSelfReferentialFetchProfileTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testEnablingJoinFetchProfileAgainstSelfReferentialAssociation() {
        Session s = openSession();
        s.beginTransaction();
        s.enableFetchProfile(Employee.FETCH_PROFILE_TREE);
        s.createCriteria(Employee.class).add(Restrictions.isNull("manager")).list();
        s.getTransaction().commit();
        s.close();
    }
}

