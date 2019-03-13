/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.e5.b;


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
public class DerivedIdentityIdClassParentSameIdTypeEmbeddedIdDepTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testOneToOneExplicitJoinColumn() throws Exception {
        Assert.assertTrue(SchemaUtil.isColumnPresent("MedicalHistory", "FK1", metadata()));
        Assert.assertTrue(SchemaUtil.isColumnPresent("MedicalHistory", "FK2", metadata()));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("MedicalHistory", "firstname", metadata()))));
        Person e = new Person();
        e.firstName = "Emmanuel";
        e.lastName = "Bernard";
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(e);
        MedicalHistory d = new MedicalHistory();
        d.patient = e;
        s.persist(d);
        s.flush();
        s.clear();
        d = ((MedicalHistory) (s.get(MedicalHistory.class, d.id)));
        Assert.assertEquals(d.id.firstName, d.patient.firstName);
        s.getTransaction().rollback();
        s.close();
    }
}

