/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.e4.b;


import java.util.Date;
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
public class DerivedIdentitySimpleParentSimpleDepMapsIdTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testOneToOneExplicitJoinColumn() throws Exception {
        Assert.assertTrue(SchemaUtil.isColumnPresent("MedicalHistory", "FK", metadata()));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("MedicalHistory", "id", metadata()))));
        Person e = new Person();
        e.ssn = "aaa";
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(e);
        MedicalHistory d = new MedicalHistory();
        d.patient = e;
        // d.id = "aaa"; //FIXME not needed when foreign is enabled
        s.persist(d);
        s.flush();
        s.clear();
        d = ((MedicalHistory) (s.get(MedicalHistory.class, d.id)));
        Assert.assertEquals(d.id, d.patient.ssn);
        d.lastupdate = new Date();
        s.flush();
        s.clear();
        d = ((MedicalHistory) (s.get(MedicalHistory.class, d.id)));
        Assert.assertNotNull(d.lastupdate);
        s.getTransaction().rollback();
        s.close();
    }

    @Test
    public void testManyToOneExplicitJoinColumn() throws Exception {
        Assert.assertTrue(SchemaUtil.isColumnPresent("FinancialHistory", "FK", metadata()));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("FinancialHistory", "id", metadata()))));
        Person e = new Person();
        e.ssn = "aaa";
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(e);
        FinancialHistory d = new FinancialHistory();
        d.patient = e;
        // d.id = "aaa"; //FIXME not needed when foreign is enabled
        s.persist(d);
        s.flush();
        s.clear();
        d = ((FinancialHistory) (s.get(FinancialHistory.class, d.id)));
        Assert.assertEquals(d.id, d.patient.ssn);
        d.lastupdate = new Date();
        s.flush();
        s.clear();
        d = ((FinancialHistory) (s.get(FinancialHistory.class, d.id)));
        Assert.assertNotNull(d.lastupdate);
        s.getTransaction().rollback();
        s.close();
    }

    @Test
    public void testExplicitlyAssignedDependentIdAttributeValue() {
        // even though the id is by definition generated (using the "foreign" strategy), JPA
        // still does allow manually setting the generated id attribute value which providers
        // are expected to promptly disregard :?
        Session s = openSession();
        s.beginTransaction();
        Person person = new Person("123456789");
        MedicalHistory medicalHistory = new MedicalHistory("987654321", person);
        s.persist(person);
        s.persist(medicalHistory);
        s.getTransaction().commit();
        s.close();
        // again, even though we specified an id value of "987654321" prior to persist,
        // Hibernate should have replaced that with the "123456789" from the associated
        // person
        Assert.assertEquals(person.ssn, medicalHistory.patient.ssn);
        Assert.assertEquals(person, medicalHistory.patient);
        Assert.assertEquals(person.ssn, medicalHistory.id);
        s = openSession();
        s.beginTransaction();
        // Should return null...
        MedicalHistory separateMedicalHistory = ((MedicalHistory) (s.get(MedicalHistory.class, "987654321")));
        Assert.assertNull(separateMedicalHistory);
        // Now we should find it...
        separateMedicalHistory = ((MedicalHistory) (s.get(MedicalHistory.class, "123456789")));
        Assert.assertNotNull(separateMedicalHistory);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(medicalHistory);
        s.delete(person);
        s.getTransaction().commit();
        s.close();
    }
}

