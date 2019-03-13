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
import javax.persistence.Converter;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
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
public class CollectionCompositeElementConversionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testElementCollectionConversion() {
        Session session = openSession();
        session.getTransaction().begin();
        CollectionCompositeElementConversionTest.Disguise disguise = new CollectionCompositeElementConversionTest.Disguise(1);
        disguise.traits.add(new CollectionCompositeElementConversionTest.Traits(CollectionCompositeElementConversionTest.ColorType.BLUE, CollectionCompositeElementConversionTest.ColorType.RED));
        session.persist(disguise);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        Assert.assertEquals(1, session.get(CollectionCompositeElementConversionTest.Disguise.class, 1).traits.size());
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        disguise = session.get(CollectionCompositeElementConversionTest.Disguise.class, 1);
        session.delete(disguise);
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "Disguise")
    @Table(name = "DISGUISE")
    public static class Disguise {
        @Id
        private Integer id;

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "DISGUISE_TRAIT", joinColumns = @JoinColumn(name = "DISGUISE_FK", nullable = false))
        private Set<CollectionCompositeElementConversionTest.Traits> traits = new HashSet<CollectionCompositeElementConversionTest.Traits>();

        public Disguise() {
        }

        public Disguise(Integer id) {
            this.id = id;
        }
    }

    @Embeddable
    public static class Traits {
        public CollectionCompositeElementConversionTest.ColorType eyeColor;

        public CollectionCompositeElementConversionTest.ColorType hairColor;

        public Traits() {
        }

        public Traits(CollectionCompositeElementConversionTest.ColorType eyeColor, CollectionCompositeElementConversionTest.ColorType hairColor) {
            this.eyeColor = eyeColor;
            this.hairColor = hairColor;
        }
    }

    public static class ColorType {
        public static CollectionCompositeElementConversionTest.ColorType BLUE = new CollectionCompositeElementConversionTest.ColorType("blue");

        public static CollectionCompositeElementConversionTest.ColorType RED = new CollectionCompositeElementConversionTest.ColorType("red");

        public static CollectionCompositeElementConversionTest.ColorType YELLOW = new CollectionCompositeElementConversionTest.ColorType("yellow");

        private final String color;

        public ColorType(String color) {
            this.color = color;
        }

        public String toExternalForm() {
            return color;
        }

        public static CollectionCompositeElementConversionTest.ColorType fromExternalForm(String color) {
            if (CollectionCompositeElementConversionTest.ColorType.BLUE.color.equals(color)) {
                return CollectionCompositeElementConversionTest.ColorType.BLUE;
            } else
                if (CollectionCompositeElementConversionTest.ColorType.RED.color.equals(color)) {
                    return CollectionCompositeElementConversionTest.ColorType.RED;
                } else
                    if (CollectionCompositeElementConversionTest.ColorType.YELLOW.color.equals(color)) {
                        return CollectionCompositeElementConversionTest.ColorType.YELLOW;
                    } else {
                        throw new RuntimeException(("Unknown color : " + color));
                    }


        }
    }

    @Converter(autoApply = true)
    public static class ColorTypeConverter implements AttributeConverter<CollectionCompositeElementConversionTest.ColorType, String> {
        @Override
        public String convertToDatabaseColumn(CollectionCompositeElementConversionTest.ColorType attribute) {
            return attribute == null ? null : attribute.toExternalForm();
        }

        @Override
        public CollectionCompositeElementConversionTest.ColorType convertToEntityAttribute(String dbData) {
            return CollectionCompositeElementConversionTest.ColorType.fromExternalForm(dbData);
        }
    }
}

