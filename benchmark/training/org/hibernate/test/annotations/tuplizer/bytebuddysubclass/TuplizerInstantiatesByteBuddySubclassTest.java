/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.tuplizer.bytebuddysubclass;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.annotations.Tuplizer;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Florian Bien
 */
public class TuplizerInstantiatesByteBuddySubclassTest extends BaseCoreFunctionalTestCase {
    @Test
    public void hhh11655Test() throws Exception {
        Session session = openSession();
        session.beginTransaction();
        TuplizerInstantiatesByteBuddySubclassTest.SimpleEntity simpleEntityNonProxy = new TuplizerInstantiatesByteBuddySubclassTest.SimpleEntity();
        Assert.assertFalse(session.contains(simpleEntityNonProxy));
        TuplizerInstantiatesByteBuddySubclassTest.SimpleEntity simpleEntity = MyEntityInstantiator.createInstance(TuplizerInstantiatesByteBuddySubclassTest.SimpleEntity.class);
        Assert.assertFalse(session.contains(simpleEntity));
        session.persist(simpleEntity);
        Assert.assertTrue(session.contains(simpleEntity));
        session.getTransaction().rollback();
        session.close();
    }

    @Entity(name = "SimpleEntity")
    @Tuplizer(impl = MyTuplizer.class)
    public static class SimpleEntity {
        protected SimpleEntity() {
        }

        @Id
        @GeneratedValue
        private Long id;
    }
}

