/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.join;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Jones and Gail Badner
 */
@SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
public class OptionalJoinTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUpdateNonNullOptionalJoinToDiffNonNull() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // create a new thing with a non-null name
        Thing thing = new Thing();
        thing.setName("one");
        s.save(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("one", thing.getName());
        Assert.assertEquals("ONE", thing.getNameUpper());
        // give it a new non-null name and save it
        thing.setName("one_changed");
        s.update(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("one_changed", thing.getName());
        Assert.assertEquals("ONE_CHANGED", thing.getNameUpper());
        s.delete(thing);
        t.commit();
        s.close();
    }

    @Test
    public void testUpdateNonNullOptionalJoinToDiffNonNullDetached() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // create a new thing with a non-null name
        Thing thing = new Thing();
        thing.setName("one");
        s.save(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("one", thing.getName());
        Assert.assertEquals("ONE", thing.getNameUpper());
        t.commit();
        s.close();
        // change detached thing name to a new non-null name and save it
        thing.setName("one_changed");
        s = openSession();
        t = s.beginTransaction();
        s.update(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("one_changed", thing.getName());
        Assert.assertEquals("ONE_CHANGED", thing.getNameUpper());
        s.delete(thing);
        t.commit();
        s.close();
    }

    @Test
    public void testMergeNonNullOptionalJoinToDiffNonNullDetached() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // create a new thing with a non-null name
        Thing thing = new Thing();
        thing.setName("one");
        s.save(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("one", thing.getName());
        Assert.assertEquals("ONE", thing.getNameUpper());
        t.commit();
        s.close();
        // change detached thing name to a new non-null name and save it
        thing.setName("one_changed");
        s = openSession();
        t = s.beginTransaction();
        s.merge(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("one_changed", thing.getName());
        Assert.assertEquals("ONE_CHANGED", thing.getNameUpper());
        s.delete(thing);
        t.commit();
        s.close();
    }

    @Test
    public void testUpdateNonNullOptionalJoinToNull() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // create a new thing with a non-null name
        Thing thing = new Thing();
        thing.setName("one");
        s.save(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("one", thing.getName());
        Assert.assertEquals("ONE", thing.getNameUpper());
        // give it a null name and save it
        thing.setName(null);
        s.update(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertNull(thing.getName());
        Assert.assertNull(thing.getNameUpper());
        s.delete(thing);
        t.commit();
        s.close();
    }

    @Test
    public void testUpdateNonNullOptionalJoinToNullDetached() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // create a new thing with a non-null name
        Thing thing = new Thing();
        thing.setName("one");
        s.save(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("one", thing.getName());
        Assert.assertEquals("ONE", thing.getNameUpper());
        t.commit();
        s.close();
        // give detached thing a null name and save it
        thing.setName(null);
        s = openSession();
        t = s.beginTransaction();
        s.update(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertNull(thing.getName());
        Assert.assertNull(thing.getNameUpper());
        s.delete(thing);
        t.commit();
        s.close();
    }

    @Test
    public void testMergeNonNullOptionalJoinToNullDetached() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // create a new thing with a non-null name
        Thing thing = new Thing();
        thing.setName("one");
        s.save(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("one", thing.getName());
        Assert.assertEquals("ONE", thing.getNameUpper());
        t.commit();
        s.close();
        // give detached thing a null name and save it
        thing.setName(null);
        s = openSession();
        t = s.beginTransaction();
        s.merge(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertNull(thing.getName());
        Assert.assertNull(thing.getNameUpper());
        s.delete(thing);
        t.commit();
        s.close();
    }

    @Test
    public void testUpdateNullOptionalJoinToNonNull() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // create a new thing with a null name
        Thing thing = new Thing();
        thing.setName(null);
        s.save(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertNull(thing.getName());
        // change name to a non-null value
        thing.setName("two");
        s.update(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("two", thing.getName());
        Assert.assertEquals("TWO", thing.getNameUpper());
        s.delete(thing);
        t.commit();
        s.close();
    }

    @Test
    public void testUpdateNullOptionalJoinToNonNullDetached() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // create a new thing with a null name
        Thing thing = new Thing();
        thing.setName(null);
        s.save(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertNull(thing.getName());
        Assert.assertNull(thing.getNameUpper());
        t.commit();
        s.close();
        // change detached thing name to a non-null value
        thing.setName("two");
        s = openSession();
        t = s.beginTransaction();
        s.update(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("two", thing.getName());
        Assert.assertEquals("TWO", thing.getNameUpper());
        s.delete(thing);
        t.commit();
        s.close();
    }

    @Test
    public void testMergeNullOptionalJoinToNonNullDetached() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // create a new thing with a null name
        Thing thing = new Thing();
        thing.setName(null);
        s.save(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertNull(thing.getName());
        Assert.assertNull(thing.getNameUpper());
        t.commit();
        s.close();
        // change detached thing name to a non-null value
        thing.setName("two");
        s = openSession();
        t = s.beginTransaction();
        s.merge(thing);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        things = s.createQuery("from Thing").list();
        Assert.assertEquals(1, things.size());
        thing = ((Thing) (things.get(0)));
        Assert.assertEquals("two", thing.getName());
        Assert.assertEquals("TWO", thing.getNameUpper());
        s.delete(thing);
        t.commit();
        s.close();
    }
}

