/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter.map;


import java.util.HashMap;
import java.util.Map;
import javax.persistence.AttributeConverter;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Converter;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
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
public class MapElementConversionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testElementCollectionConversion() {
        Session session = openSession();
        session.getTransaction().begin();
        MapElementConversionTest.Customer customer = new MapElementConversionTest.Customer(1);
        customer.colors.put("eyes", MapElementConversionTest.ColorType.BLUE);
        session.persist(customer);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        Assert.assertEquals(1, session.get(MapElementConversionTest.Customer.class, 1).colors.size());
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        customer = session.get(MapElementConversionTest.Customer.class, 1);
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
        @MapKeyColumn(name = "color_key")
        @Column(name = "color", nullable = false)
        private Map<String, MapElementConversionTest.ColorType> colors = new HashMap<String, MapElementConversionTest.ColorType>();

        public Customer() {
        }

        public Customer(Integer id) {
            this.id = id;
        }
    }

    // an enum-like class (converters are technically not allowed to apply to enums)
    public static class ColorType {
        public static MapElementConversionTest.ColorType BLUE = new MapElementConversionTest.ColorType("blue");

        public static MapElementConversionTest.ColorType RED = new MapElementConversionTest.ColorType("red");

        public static MapElementConversionTest.ColorType YELLOW = new MapElementConversionTest.ColorType("yellow");

        private final String color;

        public ColorType(String color) {
            this.color = color;
        }

        public String toExternalForm() {
            return color;
        }

        public static MapElementConversionTest.ColorType fromExternalForm(String color) {
            if (MapElementConversionTest.ColorType.BLUE.color.equals(color)) {
                return MapElementConversionTest.ColorType.BLUE;
            } else
                if (MapElementConversionTest.ColorType.RED.color.equals(color)) {
                    return MapElementConversionTest.ColorType.RED;
                } else
                    if (MapElementConversionTest.ColorType.YELLOW.color.equals(color)) {
                        return MapElementConversionTest.ColorType.YELLOW;
                    } else {
                        throw new RuntimeException(("Unknown color : " + color));
                    }


        }
    }

    @Converter(autoApply = true)
    public static class ColorTypeConverter implements AttributeConverter<MapElementConversionTest.ColorType, String> {
        @Override
        public String convertToDatabaseColumn(MapElementConversionTest.ColorType attribute) {
            return attribute == null ? null : attribute.toExternalForm();
        }

        @Override
        public MapElementConversionTest.ColorType convertToEntityAttribute(String dbData) {
            return MapElementConversionTest.ColorType.fromExternalForm(dbData);
        }
    }
}

