/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.collectionelement.embeddables.withcustomenumdef;


import java.util.Iterator;
import junit.framework.Assert;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import static org.hibernate.test.annotations.collectionelement.embeddables.withcustomenumdef.Location.Type.COMMUNE;
import static org.hibernate.test.annotations.collectionelement.embeddables.withcustomenumdef.Location.Type.COUNTY;


/**
 *
 *
 * @author Steve Ebersole
 */
public class TestBasicOps extends BaseCoreFunctionalTestCase {
    @Test
    public void testLoadAndStore() {
        Session s = openSession();
        s.beginTransaction();
        Query q = new Query(new Location("first", COUNTY));
        s.save(q);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        q = ((Query) (s.get(Query.class, q.getId())));
        Assert.assertEquals(1, q.getIncludedLocations().size());
        Location l = q.getIncludedLocations().iterator().next();
        Assert.assertEquals(COUNTY, l.getType());
        s.delete(q);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7072")
    public void testEmbeddableWithNullables() {
        Session s = openSession();
        s.beginTransaction();
        Query q = new Query(new Location(null, COMMUNE));
        s.save(q);
        s.getTransaction().commit();
        s.clear();
        s.beginTransaction();
        q.getIncludedLocations().add(new Location(null, COUNTY));
        s.update(q);
        s.getTransaction().commit();
        s.clear();
        s.beginTransaction();
        q = ((Query) (s.get(Query.class, q.getId())));
        // assertEquals( 2, q.getIncludedLocations().size() );
        s.getTransaction().commit();
        s.clear();
        s.beginTransaction();
        Iterator<Location> itr = q.getIncludedLocations().iterator();
        itr.next();
        itr.remove();
        s.update(q);
        s.getTransaction().commit();
        s.clear();
        s.beginTransaction();
        q = ((Query) (s.get(Query.class, q.getId())));
        Assert.assertEquals(1, q.getIncludedLocations().size());
        s.delete(q);
        s.getTransaction().commit();
        s.close();
    }
}

