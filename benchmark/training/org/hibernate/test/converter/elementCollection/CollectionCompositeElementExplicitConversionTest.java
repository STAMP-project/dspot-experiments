/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter.elementCollection;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.CollectionTable;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 * Similar to {@link CollectionCompositeElementConversionTest} except here we have an
 * explicit {@code @Convert} defined on the converted attribute
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-10277")
public class CollectionCompositeElementExplicitConversionTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    private Field simpleValueAttributeConverterDescriptorField;

    @Test
    public void testCollectionOfEmbeddablesWithConvertedAttributes() throws Exception {
        final MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(CollectionCompositeElementExplicitConversionTest.Traits.class).buildMetadata()));
        metadata.validate();
        final PersistentClass entityBinding = metadata.getEntityBinding(CollectionCompositeElementExplicitConversionTest.Disguise.class.getName());
        // first check the singular composite...
        final Property singularTraitsProperty = entityBinding.getProperty("singularTraits");
        checkComposite(((Component) (singularTraitsProperty.getValue())));
        // then check the plural composite...
        final Property pluralTraitsProperty = entityBinding.getProperty("pluralTraits");
        checkComposite(((Component) (getElement())));
    }

    @Entity(name = "Disguise")
    @Table(name = "DISGUISE")
    public static class Disguise {
        @Id
        private Integer id;

        private CollectionCompositeElementExplicitConversionTest.Traits singularTraits;

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "DISGUISE_TRAIT", joinColumns = @JoinColumn(name = "DISGUISE_FK", nullable = false))
        private Set<CollectionCompositeElementExplicitConversionTest.Traits> pluralTraits = new HashSet<CollectionCompositeElementExplicitConversionTest.Traits>();

        public Disguise() {
        }

        public Disguise(Integer id) {
            this.id = id;
        }
    }

    @Embeddable
    public static class Traits {
        @Convert(converter = CollectionCompositeElementExplicitConversionTest.ColorTypeConverter.class)
        public CollectionCompositeElementExplicitConversionTest.ColorType eyeColor;

        @Convert(converter = CollectionCompositeElementExplicitConversionTest.ColorTypeConverter.class)
        public CollectionCompositeElementExplicitConversionTest.ColorType hairColor;

        public Traits() {
        }

        public Traits(CollectionCompositeElementExplicitConversionTest.ColorType eyeColor, CollectionCompositeElementExplicitConversionTest.ColorType hairColor) {
            this.eyeColor = eyeColor;
            this.hairColor = hairColor;
        }
    }

    public static class ColorType implements Serializable {
        public static CollectionCompositeElementExplicitConversionTest.ColorType BLUE = new CollectionCompositeElementExplicitConversionTest.ColorType("blue");

        public static CollectionCompositeElementExplicitConversionTest.ColorType RED = new CollectionCompositeElementExplicitConversionTest.ColorType("red");

        public static CollectionCompositeElementExplicitConversionTest.ColorType YELLOW = new CollectionCompositeElementExplicitConversionTest.ColorType("yellow");

        private final String color;

        public ColorType(String color) {
            this.color = color;
        }

        public String toExternalForm() {
            return color;
        }

        public static CollectionCompositeElementExplicitConversionTest.ColorType fromExternalForm(String color) {
            if (CollectionCompositeElementExplicitConversionTest.ColorType.BLUE.color.equals(color)) {
                return CollectionCompositeElementExplicitConversionTest.ColorType.BLUE;
            } else
                if (CollectionCompositeElementExplicitConversionTest.ColorType.RED.color.equals(color)) {
                    return CollectionCompositeElementExplicitConversionTest.ColorType.RED;
                } else
                    if (CollectionCompositeElementExplicitConversionTest.ColorType.YELLOW.color.equals(color)) {
                        return CollectionCompositeElementExplicitConversionTest.ColorType.YELLOW;
                    } else {
                        throw new RuntimeException(("Unknown color : " + color));
                    }


        }
    }

    @Converter(autoApply = false)
    public static class ColorTypeConverter implements AttributeConverter<CollectionCompositeElementExplicitConversionTest.ColorType, String> {
        @Override
        public String convertToDatabaseColumn(CollectionCompositeElementExplicitConversionTest.ColorType attribute) {
            return attribute == null ? null : attribute.toExternalForm();
        }

        @Override
        public CollectionCompositeElementExplicitConversionTest.ColorType convertToEntityAttribute(String dbData) {
            return CollectionCompositeElementExplicitConversionTest.ColorType.fromExternalForm(dbData);
        }
    }
}

