/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.onetoone.optional;


import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class OptionalOneToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOptionalOneToOneRetrieval() {
        Session s = openSession();
        s.beginTransaction();
        Person me = new Person();
        me.name = "Steve";
        s.save(me);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        me = ((Person) (s.load(Person.class, me.name)));
        Assert.assertNull(me.address);
        s.delete(me);
        s.getTransaction().commit();
        s.close();
    }
}

