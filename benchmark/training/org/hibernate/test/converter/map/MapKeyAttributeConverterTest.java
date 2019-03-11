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
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Janario Oliveira
 */
public class MapKeyAttributeConverterTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testImplicitType() {
        MapKeyAttributeConverterTest.MapValue mapValue = create();
        mapValue.implicitType = ColorType.BLUE;
        mapValue.mapEntity.implicitType.put(mapValue.implicitType, mapValue);
        MapKeyAttributeConverterTest.MapEntity found = persist(mapValue.mapEntity);
        Assert.assertEquals(1, found.implicitType.size());
        MapKeyAttributeConverterTest.MapValue foundValue = found.implicitType.get(ColorType.BLUE);
        Assert.assertEquals(ColorType.BLUE, foundValue.implicitType);
        Assert.assertEquals("blue", findDatabaseValue(foundValue, "implicitType"));
        getSession().close();
    }

    @Test
    public void testExplicitType() {
        MapKeyAttributeConverterTest.MapValue mapValue = create();
        mapValue.explicitType = ColorType.RED;
        mapValue.mapEntity.explicitType.put(mapValue.explicitType, mapValue);
        MapKeyAttributeConverterTest.MapEntity found = persist(mapValue.mapEntity);
        Assert.assertEquals(1, found.explicitType.size());
        MapKeyAttributeConverterTest.MapValue foundValue = found.explicitType.get(ColorType.RED);
        Assert.assertEquals(ColorType.RED, foundValue.explicitType);
        Assert.assertEquals("COLOR-red", findDatabaseValue(foundValue, "explicitType"));
        getSession().close();
    }

    @Test
    public void testEnumDefaultType() {
        MapKeyAttributeConverterTest.MapValue mapValue = create();
        mapValue.enumDefault = MapKeyAttributeConverterTest.EnumMapKey.VALUE_1;
        mapValue.mapEntity.enumDefaultType.put(mapValue.enumDefault, mapValue);
        MapKeyAttributeConverterTest.MapEntity found = persist(mapValue.mapEntity);
        Assert.assertEquals(1, found.enumDefaultType.size());
        MapKeyAttributeConverterTest.MapValue foundValue = found.enumDefaultType.get(MapKeyAttributeConverterTest.EnumMapKey.VALUE_1);
        Assert.assertEquals(MapKeyAttributeConverterTest.EnumMapKey.VALUE_1, foundValue.enumDefault);
        Assert.assertEquals(0, ((Number) (findDatabaseValue(foundValue, "enumDefault"))).intValue());
        getSession().close();
    }

    @Test
    public void testEnumExplicitOrdinalType() {
        MapKeyAttributeConverterTest.MapValue mapValue = create();
        mapValue.enumExplicitOrdinal = MapKeyAttributeConverterTest.EnumMapKey.VALUE_2;
        mapValue.mapEntity.enumExplicitOrdinalType.put(mapValue.enumExplicitOrdinal, mapValue);
        MapKeyAttributeConverterTest.MapEntity found = persist(mapValue.mapEntity);
        Assert.assertEquals(1, found.enumExplicitOrdinalType.size());
        MapKeyAttributeConverterTest.MapValue foundValue = found.enumExplicitOrdinalType.get(MapKeyAttributeConverterTest.EnumMapKey.VALUE_2);
        Assert.assertEquals(MapKeyAttributeConverterTest.EnumMapKey.VALUE_2, foundValue.enumExplicitOrdinal);
        Assert.assertEquals(1, ((Number) (findDatabaseValue(foundValue, "enumExplicitOrdinal"))).intValue());
        getSession().close();
    }

    @Test
    public void testEnumExplicitStringType() {
        MapKeyAttributeConverterTest.MapValue mapValue = create();
        mapValue.enumExplicitString = MapKeyAttributeConverterTest.EnumMapKey.VALUE_1;
        mapValue.mapEntity.enumExplicitStringType.put(mapValue.enumExplicitString, mapValue);
        MapKeyAttributeConverterTest.MapEntity found = persist(mapValue.mapEntity);
        Assert.assertEquals(1, found.enumExplicitStringType.size());
        MapKeyAttributeConverterTest.MapValue foundValue = found.enumExplicitStringType.get(MapKeyAttributeConverterTest.EnumMapKey.VALUE_1);
        Assert.assertEquals(MapKeyAttributeConverterTest.EnumMapKey.VALUE_1, foundValue.enumExplicitString);
        Assert.assertEquals("VALUE_1", findDatabaseValue(foundValue, "enumExplicitString"));
        getSession().close();
    }

    @Test
    public void testEnumExplicitType() {
        MapKeyAttributeConverterTest.MapValue mapValue = create();
        mapValue.enumExplicit = MapKeyAttributeConverterTest.EnumMapKey.VALUE_2;
        mapValue.mapEntity.enumExplicitType.put(mapValue.enumExplicit, mapValue);
        MapKeyAttributeConverterTest.MapEntity found = persist(mapValue.mapEntity);
        Assert.assertEquals(1, found.enumExplicitType.size());
        MapKeyAttributeConverterTest.MapValue foundValue = found.enumExplicitType.get(MapKeyAttributeConverterTest.EnumMapKey.VALUE_2);
        Assert.assertEquals(MapKeyAttributeConverterTest.EnumMapKey.VALUE_2, foundValue.enumExplicit);
        Assert.assertEquals("2", findDatabaseValue(foundValue, "enumExplicit"));
        getSession().close();
    }

    @Test
    public void testEnumImplicitType() {
        MapKeyAttributeConverterTest.MapValue mapValue = create();
        mapValue.enumImplicit = MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_2;
        mapValue.mapEntity.enumImplicitType.put(mapValue.enumImplicit, mapValue);
        MapKeyAttributeConverterTest.MapEntity found = persist(mapValue.mapEntity);
        Assert.assertEquals(1, found.enumImplicitType.size());
        MapKeyAttributeConverterTest.MapValue foundValue = found.enumImplicitType.get(MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_2);
        Assert.assertEquals(MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_2, foundValue.enumImplicit);
        Assert.assertEquals("I2", findDatabaseValue(foundValue, "enumImplicit"));
        getSession().close();
    }

    @Test
    public void testEnumImplicitOverrideOrdinalType() {
        MapKeyAttributeConverterTest.MapValue mapValue = create();
        mapValue.enumImplicitOverrideOrdinal = MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_1;
        mapValue.mapEntity.enumImplicitOverrideOrdinalType.put(mapValue.enumImplicitOverrideOrdinal, mapValue);
        MapKeyAttributeConverterTest.MapEntity found = persist(mapValue.mapEntity);
        Assert.assertEquals(1, found.enumImplicitOverrideOrdinalType.size());
        MapKeyAttributeConverterTest.MapValue foundValue = found.enumImplicitOverrideOrdinalType.get(MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_1);
        Assert.assertEquals(MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_1, foundValue.enumImplicitOverrideOrdinal);
        Assert.assertEquals(0, ((Number) (findDatabaseValue(foundValue, "enumImplicitOverrideOrdinal"))).intValue());
        getSession().close();
    }

    @Test
    public void testEnumImplicitOverrideStringType() {
        MapKeyAttributeConverterTest.MapValue mapValue = create();
        mapValue.enumImplicitOverrideString = MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_2;
        mapValue.mapEntity.enumImplicitOverrideStringType.put(mapValue.enumImplicitOverrideString, mapValue);
        MapKeyAttributeConverterTest.MapEntity found = persist(mapValue.mapEntity);
        Assert.assertEquals(1, found.enumImplicitOverrideStringType.size());
        MapKeyAttributeConverterTest.MapValue foundValue = found.enumImplicitOverrideStringType.get(MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_2);
        Assert.assertEquals(MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_2, foundValue.enumImplicitOverrideString);
        Assert.assertEquals("VALUE_2", findDatabaseValue(foundValue, "enumImplicitOverrideString"));
        getSession().close();
    }

    @Test
    public void testEnumImplicitOverridedType() {
        MapKeyAttributeConverterTest.MapValue mapValue = create();
        mapValue.enumImplicitOverrided = MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_1;
        mapValue.mapEntity.enumImplicitOverridedType.put(mapValue.enumImplicitOverrided, mapValue);
        MapKeyAttributeConverterTest.MapEntity found = persist(mapValue.mapEntity);
        Assert.assertEquals(1, found.enumImplicitOverridedType.size());
        MapKeyAttributeConverterTest.MapValue foundValue = found.enumImplicitOverridedType.get(MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_1);
        Assert.assertEquals(MapKeyAttributeConverterTest.ImplicitEnumMapKey.VALUE_1, foundValue.enumImplicitOverrided);
        Assert.assertEquals("O1", findDatabaseValue(foundValue, "enumImplicitOverrided"));
        getSession().close();
    }

    @Entity
    @Table(name = "map_entity")
    public static class MapEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer id;

        @OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
        @MapKey(name = "implicitType")
        private Map<ColorType, MapKeyAttributeConverterTest.MapValue> implicitType = new HashMap<ColorType, MapKeyAttributeConverterTest.MapValue>();

        @OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
        @MapKey(name = "explicitType")
        private Map<ColorType, MapKeyAttributeConverterTest.MapValue> explicitType = new HashMap<ColorType, MapKeyAttributeConverterTest.MapValue>();

        @OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
        @MapKey(name = "enumDefault")
        private Map<MapKeyAttributeConverterTest.EnumMapKey, MapKeyAttributeConverterTest.MapValue> enumDefaultType = new HashMap<MapKeyAttributeConverterTest.EnumMapKey, MapKeyAttributeConverterTest.MapValue>();

        @OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
        @MapKey(name = "enumExplicitOrdinal")
        private Map<MapKeyAttributeConverterTest.EnumMapKey, MapKeyAttributeConverterTest.MapValue> enumExplicitOrdinalType = new HashMap<MapKeyAttributeConverterTest.EnumMapKey, MapKeyAttributeConverterTest.MapValue>();

        @OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
        @MapKey(name = "enumExplicitString")
        private Map<MapKeyAttributeConverterTest.EnumMapKey, MapKeyAttributeConverterTest.MapValue> enumExplicitStringType = new HashMap<MapKeyAttributeConverterTest.EnumMapKey, MapKeyAttributeConverterTest.MapValue>();

        @OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
        @MapKey(name = "enumExplicit")
        private Map<MapKeyAttributeConverterTest.EnumMapKey, MapKeyAttributeConverterTest.MapValue> enumExplicitType = new HashMap<MapKeyAttributeConverterTest.EnumMapKey, MapKeyAttributeConverterTest.MapValue>();

        @OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
        @MapKey(name = "enumImplicit")
        private Map<MapKeyAttributeConverterTest.ImplicitEnumMapKey, MapKeyAttributeConverterTest.MapValue> enumImplicitType = new HashMap<MapKeyAttributeConverterTest.ImplicitEnumMapKey, MapKeyAttributeConverterTest.MapValue>();

        @OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
        @MapKey(name = "enumImplicitOverrideOrdinal")
        private Map<MapKeyAttributeConverterTest.ImplicitEnumMapKey, MapKeyAttributeConverterTest.MapValue> enumImplicitOverrideOrdinalType = new HashMap<MapKeyAttributeConverterTest.ImplicitEnumMapKey, MapKeyAttributeConverterTest.MapValue>();

        @OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
        @MapKey(name = "enumImplicitOverrideString")
        private Map<MapKeyAttributeConverterTest.ImplicitEnumMapKey, MapKeyAttributeConverterTest.MapValue> enumImplicitOverrideStringType = new HashMap<MapKeyAttributeConverterTest.ImplicitEnumMapKey, MapKeyAttributeConverterTest.MapValue>();

        @OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
        @MapKey(name = "enumImplicitOverrided")
        private Map<MapKeyAttributeConverterTest.ImplicitEnumMapKey, MapKeyAttributeConverterTest.MapValue> enumImplicitOverridedType = new HashMap<MapKeyAttributeConverterTest.ImplicitEnumMapKey, MapKeyAttributeConverterTest.MapValue>();
    }

    @Entity
    @Table(name = "map_value")
    public static class MapValue {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer id;

        @ManyToOne
        @JoinColumn(name = "map_entity_id")
        private MapKeyAttributeConverterTest.MapEntity mapEntity;

        private ColorType implicitType;

        @Convert(converter = MapKeyAttributeConverterTest.CustomColorTypeConverter.class)
        private ColorType explicitType;

        private MapKeyAttributeConverterTest.EnumMapKey enumDefault;

        @Enumerated
        private MapKeyAttributeConverterTest.EnumMapKey enumExplicitOrdinal;

        @Enumerated(EnumType.STRING)
        private MapKeyAttributeConverterTest.EnumMapKey enumExplicitString;

        @Convert(converter = MapKeyAttributeConverterTest.ExplicitEnumMapKeyConverter.class)
        private MapKeyAttributeConverterTest.EnumMapKey enumExplicit;

        private MapKeyAttributeConverterTest.ImplicitEnumMapKey enumImplicit;

        @Enumerated
        private MapKeyAttributeConverterTest.ImplicitEnumMapKey enumImplicitOverrideOrdinal;

        @Enumerated(EnumType.STRING)
        private MapKeyAttributeConverterTest.ImplicitEnumMapKey enumImplicitOverrideString;

        @Convert(converter = MapKeyAttributeConverterTest.ImplicitEnumMapKeyOverridedConverter.class)
        private MapKeyAttributeConverterTest.ImplicitEnumMapKey enumImplicitOverrided;

        protected MapValue() {
        }

        public MapValue(MapKeyAttributeConverterTest.MapEntity mapEntity) {
            this.mapEntity = mapEntity;
        }
    }

    public enum EnumMapKey {

        VALUE_1,
        VALUE_2;}

    public enum ImplicitEnumMapKey {

        VALUE_1,
        VALUE_2;}

    @Converter
    public static class CustomColorTypeConverter implements AttributeConverter<ColorType, String> {
        @Override
        public String convertToDatabaseColumn(ColorType attribute) {
            return attribute == null ? null : "COLOR-" + (attribute.toExternalForm());
        }

        @Override
        public ColorType convertToEntityAttribute(String dbData) {
            return dbData == null ? null : ColorType.fromExternalForm(dbData.substring(6));
        }
    }

    @Converter
    public static class ExplicitEnumMapKeyConverter implements AttributeConverter<MapKeyAttributeConverterTest.EnumMapKey, String> {
        @Override
        public String convertToDatabaseColumn(MapKeyAttributeConverterTest.EnumMapKey attribute) {
            return attribute == null ? null : attribute.name().substring(((attribute.name().length()) - 1));
        }

        @Override
        public MapKeyAttributeConverterTest.EnumMapKey convertToEntityAttribute(String dbData) {
            return dbData == null ? null : MapKeyAttributeConverterTest.EnumMapKey.valueOf(("VALUE_" + dbData));
        }
    }

    @Converter(autoApply = true)
    public static class ImplicitEnumMapKeyConverter implements AttributeConverter<MapKeyAttributeConverterTest.ImplicitEnumMapKey, String> {
        @Override
        public String convertToDatabaseColumn(MapKeyAttributeConverterTest.ImplicitEnumMapKey attribute) {
            return attribute == null ? null : "I" + (attribute.name().substring(((attribute.name().length()) - 1)));
        }

        @Override
        public MapKeyAttributeConverterTest.ImplicitEnumMapKey convertToEntityAttribute(String dbData) {
            return dbData == null ? null : MapKeyAttributeConverterTest.ImplicitEnumMapKey.valueOf(("VALUE_" + (dbData.substring(1))));
        }
    }

    @Converter
    public static class ImplicitEnumMapKeyOverridedConverter implements AttributeConverter<MapKeyAttributeConverterTest.ImplicitEnumMapKey, String> {
        @Override
        public String convertToDatabaseColumn(MapKeyAttributeConverterTest.ImplicitEnumMapKey attribute) {
            return attribute == null ? null : "O" + (attribute.name().substring(((attribute.name().length()) - 1)));
        }

        @Override
        public MapKeyAttributeConverterTest.ImplicitEnumMapKey convertToEntityAttribute(String dbData) {
            return dbData == null ? null : MapKeyAttributeConverterTest.ImplicitEnumMapKey.valueOf(("VALUE_" + (dbData.substring(1))));
        }
    }
}

