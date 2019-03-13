/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter.elementCollection;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Converter;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
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
public class CollectionElementConversionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testElementCollectionConversion() {
        Session session = openSession();
        session.getTransaction().begin();
        CollectionElementConversionTest.Customer customer = new CollectionElementConversionTest.Customer();
        customer.id = 1;
        customer.set = new HashSet<CollectionElementConversionTest.Color>();
        customer.set.add(CollectionElementConversionTest.Color.RED);
        customer.set.add(CollectionElementConversionTest.Color.GREEN);
        customer.set.add(CollectionElementConversionTest.Color.BLUE);
        customer.map = new HashMap<CollectionElementConversionTest.Color, CollectionElementConversionTest.Status>();
        customer.map.put(CollectionElementConversionTest.Color.RED, CollectionElementConversionTest.Status.INACTIVE);
        customer.map.put(CollectionElementConversionTest.Color.GREEN, CollectionElementConversionTest.Status.ACTIVE);
        customer.map.put(CollectionElementConversionTest.Color.BLUE, CollectionElementConversionTest.Status.PENDING);
        session.persist(customer);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        Assert.assertEquals(customer.set, session.get(CollectionElementConversionTest.Customer.class, 1).set);
        Assert.assertEquals(customer.map, session.get(CollectionElementConversionTest.Customer.class, 1).map);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        customer = session.get(CollectionElementConversionTest.Customer.class, 1);
        session.delete(customer);
        session.getTransaction().commit();
        session.close();
    }

    @Entity
    @Table(name = "Customer")
    public static class Customer {
        @Id
        private Integer id;

        @ElementCollection
        @Column(name = "`set`")
        private Set<CollectionElementConversionTest.Color> set;

        @ElementCollection
        @Enumerated(EnumType.STRING)
        private Map<CollectionElementConversionTest.Color, CollectionElementConversionTest.Status> map;
    }

    public static class Color {
        public static CollectionElementConversionTest.Color RED = new CollectionElementConversionTest.Color(16711680);

        public static CollectionElementConversionTest.Color GREEN = new CollectionElementConversionTest.Color(65280);

        public static CollectionElementConversionTest.Color BLUE = new CollectionElementConversionTest.Color(255);

        private final int rgb;

        public Color(int rgb) {
            this.rgb = rgb;
        }

        @Override
        public int hashCode() {
            return this.rgb;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof CollectionElementConversionTest.Color) && ((((CollectionElementConversionTest.Color) (obj)).rgb) == (this.rgb));
        }
    }

    public static enum Status {

        ACTIVE,
        INACTIVE,
        PENDING;}

    @Converter(autoApply = true)
    public static class ColorConverter implements AttributeConverter<CollectionElementConversionTest.Color, String> {
        @Override
        public String convertToDatabaseColumn(CollectionElementConversionTest.Color attribute) {
            return attribute == null ? null : Integer.toString(attribute.rgb, 16);
        }

        @Override
        public CollectionElementConversionTest.Color convertToEntityAttribute(String dbData) {
            return dbData == null ? null : new CollectionElementConversionTest.Color(Integer.parseInt(dbData, 16));
        }
    }
}

