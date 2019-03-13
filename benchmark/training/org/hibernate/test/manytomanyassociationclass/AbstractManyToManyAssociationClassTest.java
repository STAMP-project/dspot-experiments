/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.manytomanyassociationclass;


import java.util.HashSet;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract class for tests on many-to-many association using an association class.
 *
 * @author Gail Badner
 */
public abstract class AbstractManyToManyAssociationClassTest extends BaseCoreFunctionalTestCase {
    private User user;

    private Group group;

    private Membership membership;

    @Test
    public void testRemoveAndAddSameElement() {
        deleteMembership(user, group, membership);
        addMembership(user, group, membership);
        Session s = openSession();
        s.beginTransaction();
        s.merge(user);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = ((User) (s.get(User.class, user.getId())));
        group = ((Group) (s.get(Group.class, group.getId())));
        membership = ((Membership) (s.get(membership.getClass(), membership.getId())));
        Assert.assertEquals("user", user.getName());
        Assert.assertEquals("group", group.getName());
        Assert.assertEquals("membership", membership.getName());
        Assert.assertEquals(1, user.getMemberships().size());
        Assert.assertEquals(1, group.getMemberships().size());
        Assert.assertSame(membership, user.getMemberships().iterator().next());
        Assert.assertSame(membership, group.getMemberships().iterator().next());
        Assert.assertSame(user, membership.getUser());
        Assert.assertSame(group, membership.getGroup());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testRemoveAndAddEqualElement() {
        deleteMembership(user, group, membership);
        membership = createMembership("membership");
        addMembership(user, group, membership);
        Session s = openSession();
        s.beginTransaction();
        s.merge(user);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = ((User) (s.get(User.class, user.getId())));
        group = ((Group) (s.get(Group.class, group.getId())));
        membership = ((Membership) (s.get(membership.getClass(), membership.getId())));
        Assert.assertEquals("user", user.getName());
        Assert.assertEquals("group", group.getName());
        Assert.assertEquals("membership", membership.getName());
        Assert.assertEquals(1, user.getMemberships().size());
        Assert.assertEquals(1, group.getMemberships().size());
        Assert.assertSame(membership, user.getMemberships().iterator().next());
        Assert.assertSame(membership, group.getMemberships().iterator().next());
        Assert.assertSame(user, membership.getUser());
        Assert.assertSame(group, membership.getGroup());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testRemoveAndAddEqualCollection() {
        deleteMembership(user, group, membership);
        membership = createMembership("membership");
        user.setMemberships(new HashSet());
        group.setMemberships(new HashSet());
        addMembership(user, group, membership);
        Session s = openSession();
        s.beginTransaction();
        s.merge(user);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = ((User) (s.get(User.class, user.getId())));
        group = ((Group) (s.get(Group.class, group.getId())));
        membership = ((Membership) (s.get(membership.getClass(), membership.getId())));
        Assert.assertEquals("user", user.getName());
        Assert.assertEquals("group", group.getName());
        Assert.assertEquals("membership", membership.getName());
        Assert.assertEquals(1, user.getMemberships().size());
        Assert.assertEquals(1, group.getMemberships().size());
        Assert.assertSame(membership, user.getMemberships().iterator().next());
        Assert.assertSame(membership, group.getMemberships().iterator().next());
        Assert.assertSame(user, membership.getUser());
        Assert.assertSame(group, membership.getGroup());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testRemoveAndAddSameElementNonKeyModified() {
        deleteMembership(user, group, membership);
        addMembership(user, group, membership);
        membership.setName("membership1");
        Session s = openSession();
        s.beginTransaction();
        s.merge(user);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = ((User) (s.get(User.class, user.getId())));
        group = ((Group) (s.get(Group.class, group.getId())));
        membership = ((Membership) (s.get(membership.getClass(), membership.getId())));
        Assert.assertEquals("user", user.getName());
        Assert.assertEquals("group", group.getName());
        Assert.assertEquals("membership1", membership.getName());
        Assert.assertEquals(1, user.getMemberships().size());
        Assert.assertEquals(1, group.getMemberships().size());
        Assert.assertSame(membership, user.getMemberships().iterator().next());
        Assert.assertSame(membership, group.getMemberships().iterator().next());
        Assert.assertSame(user, membership.getUser());
        Assert.assertSame(group, membership.getGroup());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testRemoveAndAddEqualElementNonKeyModified() {
        deleteMembership(user, group, membership);
        membership = createMembership("membership");
        addMembership(user, group, membership);
        membership.setName("membership1");
        Session s = openSession();
        s.beginTransaction();
        s.merge(user);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = ((User) (s.get(User.class, user.getId())));
        group = ((Group) (s.get(Group.class, group.getId())));
        membership = ((Membership) (s.get(membership.getClass(), membership.getId())));
        Assert.assertEquals("user", user.getName());
        Assert.assertEquals("group", group.getName());
        Assert.assertEquals("membership1", membership.getName());
        Assert.assertEquals(1, user.getMemberships().size());
        Assert.assertEquals(1, group.getMemberships().size());
        Assert.assertSame(membership, user.getMemberships().iterator().next());
        Assert.assertSame(membership, group.getMemberships().iterator().next());
        Assert.assertSame(user, membership.getUser());
        Assert.assertSame(group, membership.getGroup());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testDeleteDetached() {
        Session s = openSession();
        s.beginTransaction();
        s.delete(user);
        s.delete(group);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Assert.assertNull(s.get(User.class, user.getId()));
        Assert.assertNull(s.get(Group.class, group.getId()));
        Assert.assertNull(s.get(membership.getClass(), membership.getId()));
        s.getTransaction().commit();
        s.close();
    }
}

