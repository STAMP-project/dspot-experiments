/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.override;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.util.SchemaUtil;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class AssociationOverrideTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testOverriding() throws Exception {
        Location paris = new Location();
        paris.setName("Paris");
        Location atlanta = new Location();
        atlanta.setName("Atlanta");
        Trip trip = new Trip();
        trip.setFrom(paris);
        // trip.setTo( atlanta );
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(paris);
        s.persist(atlanta);
        try {
            s.persist(trip);
            s.flush();
            Assert.fail("Should be non nullable");
        } catch (PersistenceException e) {
            // success
        } finally {
            tx.rollback();
            s.close();
        }
    }

    @Test
    public void testDottedNotation() throws Exception {
        Assert.assertTrue(SchemaUtil.isTablePresent("Employee", metadata()));
        Assert.assertTrue("Overridden @JoinColumn fails", SchemaUtil.isColumnPresent("Employee", "fld_address_fk", metadata()));
        Assert.assertTrue("Overridden @JoinTable name fails", SchemaUtil.isTablePresent("tbl_empl_sites", metadata()));
        Assert.assertTrue("Overridden @JoinTable with default @JoinColumn fails", SchemaUtil.isColumnPresent("tbl_empl_sites", "employee_id", metadata()));
        Assert.assertTrue("Overridden @JoinTable.inverseJoinColumn fails", SchemaUtil.isColumnPresent("tbl_empl_sites", "to_website_fk", metadata()));
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        ContactInfo ci = new ContactInfo();
        Addr address = new Addr();
        address.setCity("Boston");
        address.setCountry("USA");
        address.setState("MA");
        address.setStreet("27 School Street");
        address.setZipcode("02108");
        ci.setAddr(address);
        List<PhoneNumber> phoneNumbers = new ArrayList();
        PhoneNumber num = new PhoneNumber();
        num.setNumber(5577188);
        Employee e = new Employee();
        Collection employeeList = new ArrayList();
        employeeList.add(e);
        e.setContactInfo(ci);
        num.setEmployees(employeeList);
        phoneNumbers.add(num);
        ci.setPhoneNumbers(phoneNumbers);
        SocialTouchPoints socialPoints = new SocialTouchPoints();
        List<SocialSite> sites = new ArrayList<SocialSite>();
        SocialSite site = new SocialSite();
        site.setEmployee(employeeList);
        site.setWebsite("www.jboss.org");
        sites.add(site);
        socialPoints.setWebsite(sites);
        ci.setSocial(socialPoints);
        s.persist(e);
        tx.commit();
        tx = s.beginTransaction();
        s.clear();
        e = ((Employee) (s.get(Employee.class, e.getId())));
        tx.commit();
        s.close();
    }
}

