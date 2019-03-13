/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.naturalid.immutable;


import FetchMode.JOIN;
import java.lang.reflect.Field;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alex Burgel
 */
public class ImmutableEntityNaturalIdTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10360")
    public void testNaturalIdNullability() {
        final EntityPersister persister = sessionFactory().getEntityPersister(Child.class.getName());
        // nullability is not specified for either properties making up
        // the natural ID, so they should be non-nullable by hbm-specific default
        final EntityMetamodel entityMetamodel = persister.getEntityMetamodel();
        Assert.assertFalse(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("parent")]);
        Assert.assertFalse(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("name")]);
    }

    @Test
    public void testNaturalIdCheck() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Parent p = new Parent("alex");
        Child c = new Child("billy", p);
        s.persist(p);
        s.persist(c);
        t.commit();
        s.close();
        Field name = c.getClass().getDeclaredField("name");
        name.setAccessible(true);
        name.set(c, "phil");
        s = openSession();
        t = s.beginTransaction();
        try {
            s.saveOrUpdate(c);
            s.flush();
            Assert.fail("should have failed because immutable natural ID was altered");
        } catch (PersistenceException e) {
            // expected
        } finally {
            t.rollback();
            s.close();
        }
        name.set(c, "billy");
        s = openSession();
        t = s.beginTransaction();
        s.delete(c);
        s.delete(p);
        t.commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testSaveParentWithDetachedChildren() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Parent p = new Parent("alex");
        Child c = new Child("billy", p);
        s.persist(p);
        s.persist(c);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        p = ((Parent) (s.createCriteria(Parent.class).add(Restrictions.eq("name", "alex")).setFetchMode("children", JOIN).setCacheable(true).uniqueResult()));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Child c2 = new Child("joey", p);
        p.getChildren().add(c2);
        s.update(p);
        // this fails if AbstractEntityPersister returns identifiers instead of entities from
        // AbstractEntityPersister.getNaturalIdSnapshot()
        s.flush();
        s.delete(p);
        t.commit();
        s.close();
    }
}

