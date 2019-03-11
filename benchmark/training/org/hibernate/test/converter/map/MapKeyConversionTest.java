/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter.map;


import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-8529")
public class MapKeyConversionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testElementCollectionConversion() {
        Session session = openSession();
        session.getTransaction().begin();
        MapKeyConversionTest.Customer customer = new MapKeyConversionTest.Customer(1);
        customer.colors.put(ColorType.BLUE, "favorite");
        session.persist(customer);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        Assert.assertEquals(1, session.get(MapKeyConversionTest.Customer.class, 1).colors.size());
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        customer = session.get(MapKeyConversionTest.Customer.class, 1);
        session.delete(customer);
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "Customer")
    @Table(name = "CUST")
    public static class Customer {
        @Id
        private Integer id;

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "cust_color", joinColumns = @JoinColumn(name = "cust_fk"))
        private Map<ColorType, String> colors = new HashMap<ColorType, String>();

        public Customer() {
        }

        public Customer(Integer id) {
            this.id = id;
        }
    }
}

