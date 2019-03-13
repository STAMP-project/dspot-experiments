/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.naturalid.nullable;


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
public class NullableNaturalIdTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10360")
    public void testNaturalIdNullability() {
        // A, B, C, and D are mapped using annotations;
        // none are mapped to be non-nullable, so all are nullable by annotations-specific default,
        // except primitives
        EntityPersister persister = sessionFactory().getEntityPersister(A.class.getName());
        EntityMetamodel entityMetamodel = persister.getEntityMetamodel();
        Assert.assertTrue(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("assC")]);
        Assert.assertTrue(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("myname")]);
        persister = sessionFactory().getEntityPersister(B.class.getName());
        entityMetamodel = persister.getEntityMetamodel();
        Assert.assertTrue(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("assA")]);
        // naturalid is a primitive, so it is non-nullable
        Assert.assertFalse(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("naturalid")]);
        persister = sessionFactory().getEntityPersister(C.class.getName());
        entityMetamodel = persister.getEntityMetamodel();
        Assert.assertTrue(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("name")]);
        persister = sessionFactory().getEntityPersister(D.class.getName());
        entityMetamodel = persister.getEntityMetamodel();
        Assert.assertTrue(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("name")]);
        Assert.assertTrue(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("associatedC")]);
        // User is mapped using hbm.xml; properties are explicitly mapped to be nullable
        persister = sessionFactory().getEntityPersister(User.class.getName());
        entityMetamodel = persister.getEntityMetamodel();
        Assert.assertTrue(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("name")]);
        Assert.assertTrue(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("org")]);
        // intVal is a primitive; hbm.xml apparently allows primitive to be nullable
        Assert.assertTrue(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("intVal")]);
    }

    @Test
    public void testNaturalIdNullValueOnPersist() {
        Session session = openSession();
        session.beginTransaction();
        C c = new C();
        session.persist(c);
        c.name = "someName";
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        session.delete(c);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testUniqueAssociation() {
        Session session = openSession();
        session.beginTransaction();
        A a = new A();
        B b = new B();
        b.naturalid = 100;
        session.persist(a);
        session.persist(b);// b.assA is declared NaturalId, his value is null this moment

        b.assA = a;
        a.assB.add(b);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        // this is OK
        Assert.assertNotNull(session.byNaturalId(B.class).using("naturalid", 100).using("assA", a).load());
        // this fails, cause EntityType.compare(Object x, Object y) always returns 0 !
        Assert.assertNull(session.byNaturalId(B.class).using("naturalid", 100).using("assA", null).load());
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        session.delete(b);
        session.delete(a);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testNaturalIdQuerySupportingNullValues() {
        Session session = openSession();
        session.beginTransaction();
        D d1 = new D();
        d1.name = "Titi";
        d1.associatedC = null;
        D d2 = new D();
        d2.name = null;
        C c = new C();
        d2.associatedC = c;
        session.persist(d1);
        session.persist(d2);
        session.persist(c);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        Assert.assertNotNull(session.byNaturalId(D.class).using("name", null).using("associatedC", c).load());
        Assert.assertNotNull(session.byNaturalId(D.class).using("name", "Titi").using("associatedC", null).load());
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        session.delete(c);
        session.delete(d1);
        session.delete(d2);
        session.getTransaction().commit();
        session.close();
    }
}

