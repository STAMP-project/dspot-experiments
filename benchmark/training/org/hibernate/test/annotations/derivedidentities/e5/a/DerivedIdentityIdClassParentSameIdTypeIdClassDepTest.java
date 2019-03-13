/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.e5.a;


import org.hibernate.Session;
import org.hibernate.test.util.SchemaUtil;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class DerivedIdentityIdClassParentSameIdTypeIdClassDepTest extends BaseNonConfigCoreFunctionalTestCase {
    private static final String FIRST_NAME = "Emmanuel";

    private static final String LAST_NAME = "Bernard";

    @Test
    public void testOneToOneExplicitJoinColumn() throws Exception {
        Assert.assertTrue(SchemaUtil.isColumnPresent("MedicalHistory", "FK1", metadata()));
        Assert.assertTrue(SchemaUtil.isColumnPresent("MedicalHistory", "FK2", metadata()));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("MedicalHistory", "firstname", metadata()))));
        Session s = openSession();
        s.getTransaction().begin();
        Person e = new Person(DerivedIdentityIdClassParentSameIdTypeIdClassDepTest.FIRST_NAME, DerivedIdentityIdClassParentSameIdTypeIdClassDepTest.LAST_NAME);
        s.persist(e);
        MedicalHistory d = new MedicalHistory(e);
        s.persist(d);
        s.flush();
        s.refresh(d);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        PersonId pId = new PersonId(DerivedIdentityIdClassParentSameIdTypeIdClassDepTest.FIRST_NAME, DerivedIdentityIdClassParentSameIdTypeIdClassDepTest.LAST_NAME);
        MedicalHistory d2 = ((MedicalHistory) (s.get(MedicalHistory.class, pId)));
        Person p2 = ((Person) (s.get(Person.class, pId)));
        Assert.assertEquals(pId.firstName, d2.patient.firstName);
        Assert.assertEquals(pId.firstName, p2.firstName);
        s.delete(d2);
        s.delete(p2);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testTckLikeBehavior() throws Exception {
        Assert.assertTrue(SchemaUtil.isColumnPresent("MedicalHistory", "FK1", metadata()));
        Assert.assertTrue(SchemaUtil.isColumnPresent("MedicalHistory", "FK2", metadata()));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("MedicalHistory", "firstname", metadata()))));
        Session s = openSession();
        s.getTransaction().begin();
        Person e = new Person(DerivedIdentityIdClassParentSameIdTypeIdClassDepTest.FIRST_NAME, DerivedIdentityIdClassParentSameIdTypeIdClassDepTest.LAST_NAME);
        s.persist(e);
        MedicalHistory d = new MedicalHistory(e);
        s.persist(d);
        s.flush();
        s.refresh(d);
        s.getTransaction().commit();
        // NOTE THAT WE LEAVE THE SESSION OPEN!
        s.getTransaction().begin();
        PersonId pId = new PersonId(DerivedIdentityIdClassParentSameIdTypeIdClassDepTest.FIRST_NAME, DerivedIdentityIdClassParentSameIdTypeIdClassDepTest.LAST_NAME);
        MedicalHistory d2 = ((MedicalHistory) (s.get(MedicalHistory.class, pId)));
        Person p2 = ((Person) (s.get(Person.class, pId)));
        Assert.assertEquals(pId.firstName, d2.patient.firstName);
        Assert.assertEquals(pId.firstName, p2.firstName);
        s.delete(d2);
        s.delete(p2);
        s.getTransaction().commit();
        s.close();
    }
}

