/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter.elementCollection;


import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link org.hibernate.cfg.CollectionPropertyHolder}.
 *
 * Tests that {@link javax.persistence.AttributeConverter}s are considered correctly for {@link javax.persistence.ElementCollection}.
 *
 * @author Markus Heiden
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-9495")
public class ElementCollectionTests extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testSimpleConvertUsage() throws MalformedURLException {
        // first some assertions of the metamodel
        PersistentClass entityBinding = metadata().getEntityBinding(ElementCollectionTests.TheEntity.class.getName());
        Assert.assertNotNull(entityBinding);
        Property setAttributeBinding = entityBinding.getProperty("set");
        Collection setBinding = ((Collection) (setAttributeBinding.getValue()));
        ExtraAssertions.assertTyping(AttributeConverterTypeAdapter.class, setBinding.getElement().getType());
        Property mapAttributeBinding = entityBinding.getProperty("map");
        IndexedCollection mapBinding = ((IndexedCollection) (mapAttributeBinding.getValue()));
        ExtraAssertions.assertTyping(AttributeConverterTypeAdapter.class, mapBinding.getIndex().getType());
        ExtraAssertions.assertTyping(AttributeConverterTypeAdapter.class, mapBinding.getElement().getType());
        // now lets try to use the model, integration-testing-style!
        ElementCollectionTests.TheEntity entity = new ElementCollectionTests.TheEntity(1);
        Session s = openSession();
        s.beginTransaction();
        s.save(entity);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        ElementCollectionTests.TheEntity retrieved = ((ElementCollectionTests.TheEntity) (s.load(ElementCollectionTests.TheEntity.class, 1)));
        Assert.assertEquals(1, retrieved.getSet().size());
        Assert.assertEquals(new ElementCollectionTests.ValueType("set_value"), retrieved.getSet().iterator().next());
        Assert.assertEquals(1, retrieved.getMap().size());
        Assert.assertEquals(new ElementCollectionTests.ValueType("map_value"), retrieved.getMap().get(new ElementCollectionTests.ValueType("map_key")));
        s.delete(retrieved);
        s.getTransaction().commit();
        s.close();
    }

    /**
     * Non-serializable value type.
     */
    public static class ValueType {
        private final String value;

        public ValueType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof ElementCollectionTests.ValueType) && (value.equals(((ElementCollectionTests.ValueType) (o)).value));
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    /**
     * Converter for {@link ValueType}.
     */
    public static class ValueTypeConverter implements AttributeConverter<ElementCollectionTests.ValueType, String> {
        @Override
        public String convertToDatabaseColumn(ElementCollectionTests.ValueType type) {
            return type.getValue();
        }

        @Override
        public ElementCollectionTests.ValueType convertToEntityAttribute(String type) {
            return new ElementCollectionTests.ValueType(type);
        }
    }

    /**
     * Entity holding element collections.
     */
    @Entity(name = "TheEntity")
    @Table(name = "entity")
    public static class TheEntity {
        @Id
        public Integer id;

        /**
         * Element set with converter.
         */
        @Convert(converter = ElementCollectionTests.ValueTypeConverter.class)
        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "entity_set", joinColumns = @JoinColumn(name = "entity_id", nullable = false))
        @Column(name = "value", nullable = false)
        public Set<ElementCollectionTests.ValueType> set = new HashSet<ElementCollectionTests.ValueType>();

        /**
         * Element map with converters.
         */
        @Converts({ @Convert(attributeName = "key", converter = ElementCollectionTests.ValueTypeConverter.class), @Convert(attributeName = "value", converter = ElementCollectionTests.ValueTypeConverter.class) })
        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "entity_map", joinColumns = @JoinColumn(name = "entity_id", nullable = false))
        @MapKeyColumn(name = "map_key", nullable = false)
        @Column(name = "value", nullable = false)
        public Map<ElementCollectionTests.ValueType, ElementCollectionTests.ValueType> map = new HashMap<ElementCollectionTests.ValueType, ElementCollectionTests.ValueType>();

        public TheEntity() {
        }

        public TheEntity(Integer id) {
            this.id = id;
            this.set.add(new ElementCollectionTests.ValueType("set_value"));
            this.map.put(new ElementCollectionTests.ValueType("map_key"), new ElementCollectionTests.ValueType("map_value"));
        }

        public Set<ElementCollectionTests.ValueType> getSet() {
            return set;
        }

        public Map<ElementCollectionTests.ValueType, ElementCollectionTests.ValueType> getMap() {
            return map;
        }
    }
}

