/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.naturalid.inheritance;


import org.hibernate.Session;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class InheritedNaturalIdTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10360")
    public void testNaturalIdNullability() {
        final EntityPersister persister = sessionFactory().getEntityPersister(User.class.getName());
        final EntityMetamodel entityMetamodel = persister.getEntityMetamodel();
        // nullability is not specified, so it should be nullable by annotations-specific default
        Assert.assertTrue(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("uid")]);
    }

    @Test
    public void testIt() {
        Session s = openSession();
        s.beginTransaction();
        s.save(new User("steve"));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.bySimpleNaturalId(Principal.class).load("steve");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.bySimpleNaturalId(User.class).load("steve");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(s.bySimpleNaturalId(User.class).load("steve"));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSubclassModifieablNaturalId() {
        Session s = openSession();
        s.beginTransaction();
        s.save(new User("steve"));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Principal p = ((Principal) (s.bySimpleNaturalId(Principal.class).load("steve")));
        Assert.assertNotNull(p);
        User u = ((User) (s.bySimpleNaturalId(User.class).load("steve")));
        Assert.assertNotNull(u);
        Assert.assertSame(p, u);
        // change the natural id
        u.setUid("sebersole");
        s.flush();
        // make sure we can no longer access the info based on the old natural id value
        Assert.assertNull(s.bySimpleNaturalId(Principal.class).load("steve"));
        Assert.assertNull(s.bySimpleNaturalId(User.class).load("steve"));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(u);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSubclassDeleteNaturalId() {
        Session s = openSession();
        s.beginTransaction();
        s.save(new User("steve"));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Principal p = ((Principal) (s.bySimpleNaturalId(Principal.class).load("steve")));
        Assert.assertNotNull(p);
        s.delete(p);
        s.flush();
        // assertNull( s.bySimpleNaturalId( Principal.class ).load( "steve" ) );
        Assert.assertNull(s.bySimpleNaturalId(User.class).load("steve"));
        s.getTransaction().commit();
        s.close();
    }
}

