/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter.elementCollection;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
public class CollectionElementExplicitConversionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testElementCollectionConversion() {
        Session session = openSession();
        session.getTransaction().begin();
        CollectionElementExplicitConversionTest.Customer customer = new CollectionElementExplicitConversionTest.Customer(1);
        customer.colors.add(CollectionElementExplicitConversionTest.ColorType.BLUE);
        session.persist(customer);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        Assert.assertEquals(1, session.get(CollectionElementExplicitConversionTest.Customer.class, 1).colors.size());
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        customer = session.get(CollectionElementExplicitConversionTest.Customer.class, 1);
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
        @CollectionTable(name = "cust_color", joinColumns = @JoinColumn(name = "cust_fk", nullable = false), uniqueConstraints = @UniqueConstraint(columnNames = { "cust_fk", "color" }))
        @Column(name = "color", nullable = false)
        @Convert(converter = CollectionElementExplicitConversionTest.ColorTypeConverter.class)
        private Set<CollectionElementExplicitConversionTest.ColorType> colors = new HashSet<CollectionElementExplicitConversionTest.ColorType>();

        public Customer() {
        }

        public Customer(Integer id) {
            this.id = id;
        }
    }

    // an enum-like class (converters are technically not allowed to apply to enums)
    public static class ColorType {
        public static CollectionElementExplicitConversionTest.ColorType BLUE = new CollectionElementExplicitConversionTest.ColorType("blue");

        public static CollectionElementExplicitConversionTest.ColorType RED = new CollectionElementExplicitConversionTest.ColorType("red");

        public static CollectionElementExplicitConversionTest.ColorType YELLOW = new CollectionElementExplicitConversionTest.ColorType("yellow");

        private final String color;

        public ColorType(String color) {
            this.color = color;
        }

        public String toExternalForm() {
            return color;
        }

        public static CollectionElementExplicitConversionTest.ColorType fromExternalForm(String color) {
            if (CollectionElementExplicitConversionTest.ColorType.BLUE.color.equals(color)) {
                return CollectionElementExplicitConversionTest.ColorType.BLUE;
            } else
                if (CollectionElementExplicitConversionTest.ColorType.RED.color.equals(color)) {
                    return CollectionElementExplicitConversionTest.ColorType.RED;
                } else
                    if (CollectionElementExplicitConversionTest.ColorType.YELLOW.color.equals(color)) {
                        return CollectionElementExplicitConversionTest.ColorType.YELLOW;
                    } else {
                        throw new RuntimeException(("Unknown color : " + color));
                    }


        }
    }

    @Converter(autoApply = false)
    public static class ColorTypeConverter implements AttributeConverter<CollectionElementExplicitConversionTest.ColorType, String> {
        @Override
        public String convertToDatabaseColumn(CollectionElementExplicitConversionTest.ColorType attribute) {
            return attribute == null ? null : attribute.toExternalForm();
        }

        @Override
        public CollectionElementExplicitConversionTest.ColorType convertToEntityAttribute(String dbData) {
            return CollectionElementExplicitConversionTest.ColorType.fromExternalForm(dbData);
        }
    }
}

