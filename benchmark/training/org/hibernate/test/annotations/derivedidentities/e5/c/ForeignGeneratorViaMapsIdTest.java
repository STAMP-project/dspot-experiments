/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.e5.c;


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
public class ForeignGeneratorViaMapsIdTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testForeignGenerator() throws Exception {
        Assert.assertTrue(SchemaUtil.isColumnPresent("MedicalHistory", "patient_id", metadata()));
        Person e = new Person();
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(e);
        MedicalHistory d = new MedicalHistory();
        d.patient = e;
        s.persist(d);
        s.flush();
        s.clear();
        d = ((MedicalHistory) (s.get(MedicalHistory.class, e.id)));
        Assert.assertEquals(e.id, d.id);
        s.delete(d);
        s.delete(d.patient);
        s.getTransaction().rollback();
        s.close();
    }
}

