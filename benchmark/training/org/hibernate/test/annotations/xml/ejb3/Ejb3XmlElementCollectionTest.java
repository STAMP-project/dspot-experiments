/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.xml.ejb3;


import AccessType.PROPERTY;
import EnumType.STRING;
import FetchType.EAGER;
import FetchType.LAZY;
import TemporalType.DATE;
import javax.persistence.Access;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.MapKeyJoinColumns;
import javax.persistence.MapKeyTemporal;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import org.junit.Assert;
import org.junit.Test;


public class Ejb3XmlElementCollectionTest extends Ejb3XmlTestCase {
    @Test
    public void testNoChildren() throws Exception {
        reader = getReader(Entity2.class, "field1", "element-collection.orm1.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(OrderBy.class);
        assertAnnotationNotPresent(OrderColumn.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        assertAnnotationNotPresent(Column.class);
        assertAnnotationNotPresent(Temporal.class);
        assertAnnotationNotPresent(Enumerated.class);
        assertAnnotationNotPresent(Lob.class);
        assertAnnotationNotPresent(AttributeOverride.class);
        assertAnnotationNotPresent(AttributeOverrides.class);
        assertAnnotationNotPresent(AssociationOverride.class);
        assertAnnotationNotPresent(AssociationOverrides.class);
        assertAnnotationNotPresent(CollectionTable.class);
        assertAnnotationNotPresent(Access.class);
        ElementCollection relAnno = reader.getAnnotation(ElementCollection.class);
        Assert.assertEquals(LAZY, relAnno.fetch());
        Assert.assertEquals(void.class, relAnno.targetClass());
    }

    @Test
    public void testOrderBy() throws Exception {
        reader = getReader(Entity2.class, "field1", "element-collection.orm2.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationPresent(OrderBy.class);
        assertAnnotationNotPresent(OrderColumn.class);
        Assert.assertEquals("col1 ASC, col2 DESC", reader.getAnnotation(OrderBy.class).value());
    }

    @Test
    public void testOrderColumnNoAttributes() throws Exception {
        reader = getReader(Entity2.class, "field1", "element-collection.orm3.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(OrderBy.class);
        assertAnnotationPresent(OrderColumn.class);
        OrderColumn orderColumnAnno = reader.getAnnotation(OrderColumn.class);
        Assert.assertEquals("", orderColumnAnno.columnDefinition());
        Assert.assertEquals("", orderColumnAnno.name());
        Assert.assertTrue(orderColumnAnno.insertable());
        Assert.assertTrue(orderColumnAnno.nullable());
        Assert.assertTrue(orderColumnAnno.updatable());
    }

    @Test
    public void testOrderColumnAllAttributes() throws Exception {
        reader = getReader(Entity2.class, "field1", "element-collection.orm4.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(OrderBy.class);
        assertAnnotationPresent(OrderColumn.class);
        OrderColumn orderColumnAnno = reader.getAnnotation(OrderColumn.class);
        Assert.assertEquals("int", orderColumnAnno.columnDefinition());
        Assert.assertEquals("col1", orderColumnAnno.name());
        Assert.assertFalse(orderColumnAnno.insertable());
        Assert.assertFalse(orderColumnAnno.nullable());
        Assert.assertFalse(orderColumnAnno.updatable());
    }

    @Test
    public void testMapKeyNoAttributes() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm5.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        Assert.assertEquals("", reader.getAnnotation(MapKey.class).name());
    }

    @Test
    public void testMapKeyAllAttributes() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm6.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        Assert.assertEquals("field2", reader.getAnnotation(MapKey.class).name());
    }

    @Test
    public void testMapKeyClass() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm7.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        Assert.assertEquals(Entity2.class, reader.getAnnotation(MapKeyClass.class).value());
    }

    @Test
    public void testMapKeyTemporal() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm8.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        Assert.assertEquals(DATE, reader.getAnnotation(MapKeyTemporal.class).value());
    }

    @Test
    public void testMapKeyEnumerated() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm9.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        Assert.assertEquals(STRING, reader.getAnnotation(MapKeyEnumerated.class).value());
    }

    /**
     * When there's a single map key attribute override, we still wrap it with
     * an AttributeOverrides annotation.
     */
    @Test
    public void testSingleMapKeyAttributeOverride() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm10.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        assertAnnotationNotPresent(AttributeOverride.class);
        assertAnnotationPresent(AttributeOverrides.class);
        AttributeOverrides overridesAnno = reader.getAnnotation(AttributeOverrides.class);
        AttributeOverride[] overrides = overridesAnno.value();
        Assert.assertEquals(1, overrides.length);
        Assert.assertEquals("field1", overrides[0].name());
        Assert.assertEquals("col1", overrides[0].column().name());
    }

    @Test
    public void testMultipleMapKeyAttributeOverrides() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm11.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        assertAnnotationNotPresent(AttributeOverride.class);
        assertAnnotationPresent(AttributeOverrides.class);
        AttributeOverrides overridesAnno = reader.getAnnotation(AttributeOverrides.class);
        AttributeOverride[] overrides = overridesAnno.value();
        Assert.assertEquals(2, overrides.length);
        Assert.assertEquals("field1", overrides[0].name());
        Assert.assertEquals("", overrides[0].column().name());
        Assert.assertFalse(overrides[0].column().unique());
        Assert.assertTrue(overrides[0].column().nullable());
        Assert.assertTrue(overrides[0].column().insertable());
        Assert.assertTrue(overrides[0].column().updatable());
        Assert.assertEquals("", overrides[0].column().columnDefinition());
        Assert.assertEquals("", overrides[0].column().table());
        Assert.assertEquals(255, overrides[0].column().length());
        Assert.assertEquals(0, overrides[0].column().precision());
        Assert.assertEquals(0, overrides[0].column().scale());
        Assert.assertEquals("field2", overrides[1].name());
        Assert.assertEquals("col1", overrides[1].column().name());
        Assert.assertTrue(overrides[1].column().unique());
        Assert.assertFalse(overrides[1].column().nullable());
        Assert.assertFalse(overrides[1].column().insertable());
        Assert.assertFalse(overrides[1].column().updatable());
        Assert.assertEquals("int", overrides[1].column().columnDefinition());
        Assert.assertEquals("table1", overrides[1].column().table());
        Assert.assertEquals(50, overrides[1].column().length());
        Assert.assertEquals(2, overrides[1].column().precision());
        Assert.assertEquals(1, overrides[1].column().scale());
    }

    @Test
    public void testMapKeyColumnNoAttributes() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm12.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        MapKeyColumn keyColAnno = reader.getAnnotation(MapKeyColumn.class);
        Assert.assertEquals("", keyColAnno.columnDefinition());
        Assert.assertEquals("", keyColAnno.name());
        Assert.assertEquals("", keyColAnno.table());
        Assert.assertFalse(keyColAnno.nullable());
        Assert.assertTrue(keyColAnno.insertable());
        Assert.assertFalse(keyColAnno.unique());
        Assert.assertTrue(keyColAnno.updatable());
        Assert.assertEquals(255, keyColAnno.length());
        Assert.assertEquals(0, keyColAnno.precision());
        Assert.assertEquals(0, keyColAnno.scale());
    }

    @Test
    public void testMapKeyColumnAllAttributes() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm13.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        MapKeyColumn keyColAnno = reader.getAnnotation(MapKeyColumn.class);
        Assert.assertEquals("int", keyColAnno.columnDefinition());
        Assert.assertEquals("col1", keyColAnno.name());
        Assert.assertEquals("table1", keyColAnno.table());
        Assert.assertTrue(keyColAnno.nullable());
        Assert.assertFalse(keyColAnno.insertable());
        Assert.assertTrue(keyColAnno.unique());
        Assert.assertFalse(keyColAnno.updatable());
        Assert.assertEquals(50, keyColAnno.length());
        Assert.assertEquals(2, keyColAnno.precision());
        Assert.assertEquals(1, keyColAnno.scale());
    }

    /**
     * When there's a single map key join column, we still wrap it with a
     * MapKeyJoinColumns annotation.
     */
    @Test
    public void testSingleMapKeyJoinColumn() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm14.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        MapKeyJoinColumns joinColumnsAnno = reader.getAnnotation(MapKeyJoinColumns.class);
        MapKeyJoinColumn[] joinColumns = joinColumnsAnno.value();
        Assert.assertEquals(1, joinColumns.length);
        Assert.assertEquals("col1", joinColumns[0].name());
    }

    @Test
    public void testMultipleMapKeyJoinColumns() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm15.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        MapKeyJoinColumns joinColumnsAnno = reader.getAnnotation(MapKeyJoinColumns.class);
        MapKeyJoinColumn[] joinColumns = joinColumnsAnno.value();
        Assert.assertEquals(2, joinColumns.length);
        Assert.assertEquals("", joinColumns[0].name());
        Assert.assertEquals("", joinColumns[0].referencedColumnName());
        Assert.assertFalse(joinColumns[0].unique());
        Assert.assertFalse(joinColumns[0].nullable());
        Assert.assertTrue(joinColumns[0].insertable());
        Assert.assertTrue(joinColumns[0].updatable());
        Assert.assertEquals("", joinColumns[0].columnDefinition());
        Assert.assertEquals("", joinColumns[0].table());
        Assert.assertEquals("col1", joinColumns[1].name());
        Assert.assertEquals("col2", joinColumns[1].referencedColumnName());
        Assert.assertTrue(joinColumns[1].unique());
        Assert.assertTrue(joinColumns[1].nullable());
        Assert.assertFalse(joinColumns[1].insertable());
        Assert.assertFalse(joinColumns[1].updatable());
        Assert.assertEquals("int", joinColumns[1].columnDefinition());
        Assert.assertEquals("table1", joinColumns[1].table());
    }

    @Test
    public void testColumnNoAttributes() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm16.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationPresent(Column.class);
        Column column = reader.getAnnotation(Column.class);
        Assert.assertEquals("", column.name());
        Assert.assertFalse(column.unique());
        Assert.assertTrue(column.nullable());
        Assert.assertTrue(column.insertable());
        Assert.assertTrue(column.updatable());
        Assert.assertEquals("", column.columnDefinition());
        Assert.assertEquals("", column.table());
        Assert.assertEquals(255, column.length());
        Assert.assertEquals(0, column.precision());
        Assert.assertEquals(0, column.scale());
    }

    @Test
    public void testColumnAllAttributes() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm17.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationPresent(Column.class);
        Column column = reader.getAnnotation(Column.class);
        Assert.assertEquals("col1", column.name());
        Assert.assertTrue(column.unique());
        Assert.assertFalse(column.nullable());
        Assert.assertFalse(column.insertable());
        Assert.assertFalse(column.updatable());
        Assert.assertEquals("int", column.columnDefinition());
        Assert.assertEquals("table1", column.table());
        Assert.assertEquals(50, column.length());
        Assert.assertEquals(2, column.precision());
        Assert.assertEquals(1, column.scale());
    }

    @Test
    public void testTemporal() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm18.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationPresent(Temporal.class);
        assertAnnotationNotPresent(Enumerated.class);
        assertAnnotationNotPresent(Lob.class);
        Assert.assertEquals(DATE, reader.getAnnotation(Temporal.class).value());
    }

    @Test
    public void testEnumerated() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm19.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(Temporal.class);
        assertAnnotationPresent(Enumerated.class);
        assertAnnotationNotPresent(Lob.class);
        Assert.assertEquals(STRING, reader.getAnnotation(Enumerated.class).value());
    }

    @Test
    public void testLob() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm20.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(Temporal.class);
        assertAnnotationNotPresent(Enumerated.class);
        assertAnnotationPresent(Lob.class);
    }

    /**
     * When there's a single attribute override, we still wrap it with an
     * AttributeOverrides annotation.
     */
    @Test
    public void testSingleAttributeOverride() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm21.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(AttributeOverride.class);
        assertAnnotationPresent(AttributeOverrides.class);
        AttributeOverrides overridesAnno = reader.getAnnotation(AttributeOverrides.class);
        AttributeOverride[] overrides = overridesAnno.value();
        Assert.assertEquals(1, overrides.length);
        Assert.assertEquals("field1", overrides[0].name());
        Assert.assertEquals("col1", overrides[0].column().name());
    }

    @Test
    public void testMultipleAttributeOverrides() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm22.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(AttributeOverride.class);
        assertAnnotationPresent(AttributeOverrides.class);
        AttributeOverrides overridesAnno = reader.getAnnotation(AttributeOverrides.class);
        AttributeOverride[] overrides = overridesAnno.value();
        Assert.assertEquals(2, overrides.length);
        Assert.assertEquals("field1", overrides[0].name());
        Assert.assertEquals("", overrides[0].column().name());
        Assert.assertFalse(overrides[0].column().unique());
        Assert.assertTrue(overrides[0].column().nullable());
        Assert.assertTrue(overrides[0].column().insertable());
        Assert.assertTrue(overrides[0].column().updatable());
        Assert.assertEquals("", overrides[0].column().columnDefinition());
        Assert.assertEquals("", overrides[0].column().table());
        Assert.assertEquals(255, overrides[0].column().length());
        Assert.assertEquals(0, overrides[0].column().precision());
        Assert.assertEquals(0, overrides[0].column().scale());
        Assert.assertEquals("field2", overrides[1].name());
        Assert.assertEquals("col1", overrides[1].column().name());
        Assert.assertTrue(overrides[1].column().unique());
        Assert.assertFalse(overrides[1].column().nullable());
        Assert.assertFalse(overrides[1].column().insertable());
        Assert.assertFalse(overrides[1].column().updatable());
        Assert.assertEquals("int", overrides[1].column().columnDefinition());
        Assert.assertEquals("table1", overrides[1].column().table());
        Assert.assertEquals(50, overrides[1].column().length());
        Assert.assertEquals(2, overrides[1].column().precision());
        Assert.assertEquals(1, overrides[1].column().scale());
    }

    /**
     * Tests that map-key-attribute-override and attribute-override elements
     * both end up in the AttributeOverrides annotation.
     */
    @Test
    public void testMixedAttributeOverrides() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm23.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(AttributeOverride.class);
        assertAnnotationPresent(AttributeOverrides.class);
        AttributeOverrides overridesAnno = reader.getAnnotation(AttributeOverrides.class);
        AttributeOverride[] overrides = overridesAnno.value();
        Assert.assertEquals(2, overrides.length);
        Assert.assertEquals("field1", overrides[0].name());
        Assert.assertEquals("col1", overrides[0].column().name());
        Assert.assertEquals("field2", overrides[1].name());
        Assert.assertEquals("col2", overrides[1].column().name());
    }

    /**
     * When there's a single association override, we still wrap it with an
     * AssociationOverrides annotation.
     */
    @Test
    public void testSingleAssociationOverride() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm24.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(AssociationOverride.class);
        assertAnnotationPresent(AssociationOverrides.class);
        AssociationOverrides overridesAnno = reader.getAnnotation(AssociationOverrides.class);
        AssociationOverride[] overrides = overridesAnno.value();
        Assert.assertEquals(1, overrides.length);
        Assert.assertEquals("association1", overrides[0].name());
        Assert.assertEquals(0, overrides[0].joinColumns().length);
        Assert.assertEquals("", overrides[0].joinTable().name());
    }

    @Test
    public void testMultipleAssociationOverridesJoinColumns() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm25.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(AssociationOverride.class);
        assertAnnotationPresent(AssociationOverrides.class);
        AssociationOverrides overridesAnno = reader.getAnnotation(AssociationOverrides.class);
        AssociationOverride[] overrides = overridesAnno.value();
        Assert.assertEquals(2, overrides.length);
        // First, an association using join table
        Assert.assertEquals("association1", overrides[0].name());
        Assert.assertEquals(0, overrides[0].joinColumns().length);
        JoinTable joinTableAnno = overrides[0].joinTable();
        Assert.assertEquals("catalog1", joinTableAnno.catalog());
        Assert.assertEquals("table1", joinTableAnno.name());
        Assert.assertEquals("schema1", joinTableAnno.schema());
        // JoinColumns
        JoinColumn[] joinColumns = joinTableAnno.joinColumns();
        Assert.assertEquals(2, joinColumns.length);
        Assert.assertEquals("", joinColumns[0].name());
        Assert.assertEquals("", joinColumns[0].referencedColumnName());
        Assert.assertEquals("", joinColumns[0].table());
        Assert.assertEquals("", joinColumns[0].columnDefinition());
        Assert.assertTrue(joinColumns[0].insertable());
        Assert.assertTrue(joinColumns[0].updatable());
        Assert.assertTrue(joinColumns[0].nullable());
        Assert.assertFalse(joinColumns[0].unique());
        Assert.assertEquals("col1", joinColumns[1].name());
        Assert.assertEquals("col2", joinColumns[1].referencedColumnName());
        Assert.assertEquals("table2", joinColumns[1].table());
        Assert.assertEquals("int", joinColumns[1].columnDefinition());
        Assert.assertFalse(joinColumns[1].insertable());
        Assert.assertFalse(joinColumns[1].updatable());
        Assert.assertFalse(joinColumns[1].nullable());
        Assert.assertTrue(joinColumns[1].unique());
        // InverseJoinColumns
        JoinColumn[] inverseJoinColumns = joinTableAnno.inverseJoinColumns();
        Assert.assertEquals(2, inverseJoinColumns.length);
        Assert.assertEquals("", inverseJoinColumns[0].name());
        Assert.assertEquals("", inverseJoinColumns[0].referencedColumnName());
        Assert.assertEquals("", inverseJoinColumns[0].table());
        Assert.assertEquals("", inverseJoinColumns[0].columnDefinition());
        Assert.assertTrue(inverseJoinColumns[0].insertable());
        Assert.assertTrue(inverseJoinColumns[0].updatable());
        Assert.assertTrue(inverseJoinColumns[0].nullable());
        Assert.assertFalse(inverseJoinColumns[0].unique());
        Assert.assertEquals("col3", inverseJoinColumns[1].name());
        Assert.assertEquals("col4", inverseJoinColumns[1].referencedColumnName());
        Assert.assertEquals("table3", inverseJoinColumns[1].table());
        Assert.assertEquals("int", inverseJoinColumns[1].columnDefinition());
        Assert.assertFalse(inverseJoinColumns[1].insertable());
        Assert.assertFalse(inverseJoinColumns[1].updatable());
        Assert.assertFalse(inverseJoinColumns[1].nullable());
        Assert.assertTrue(inverseJoinColumns[1].unique());
        // UniqueConstraints
        UniqueConstraint[] uniqueConstraints = joinTableAnno.uniqueConstraints();
        Assert.assertEquals(2, uniqueConstraints.length);
        Assert.assertEquals("", uniqueConstraints[0].name());
        Assert.assertEquals(1, uniqueConstraints[0].columnNames().length);
        Assert.assertEquals("col5", uniqueConstraints[0].columnNames()[0]);
        Assert.assertEquals("uq1", uniqueConstraints[1].name());
        Assert.assertEquals(2, uniqueConstraints[1].columnNames().length);
        Assert.assertEquals("col6", uniqueConstraints[1].columnNames()[0]);
        Assert.assertEquals("col7", uniqueConstraints[1].columnNames()[1]);
        // Second, an association using join columns
        Assert.assertEquals("association2", overrides[1].name());
        // JoinColumns
        joinColumns = overrides[1].joinColumns();
        Assert.assertEquals(2, joinColumns.length);
        Assert.assertEquals("", joinColumns[0].name());
        Assert.assertEquals("", joinColumns[0].referencedColumnName());
        Assert.assertEquals("", joinColumns[0].table());
        Assert.assertEquals("", joinColumns[0].columnDefinition());
        Assert.assertTrue(joinColumns[0].insertable());
        Assert.assertTrue(joinColumns[0].updatable());
        Assert.assertTrue(joinColumns[0].nullable());
        Assert.assertFalse(joinColumns[0].unique());
        Assert.assertEquals("col8", joinColumns[1].name());
        Assert.assertEquals("col9", joinColumns[1].referencedColumnName());
        Assert.assertEquals("table4", joinColumns[1].table());
        Assert.assertEquals("int", joinColumns[1].columnDefinition());
        Assert.assertFalse(joinColumns[1].insertable());
        Assert.assertFalse(joinColumns[1].updatable());
        Assert.assertFalse(joinColumns[1].nullable());
        Assert.assertTrue(joinColumns[1].unique());
    }

    @Test
    public void testCollectionTableNoChildren() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm26.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationPresent(CollectionTable.class);
        CollectionTable tableAnno = reader.getAnnotation(CollectionTable.class);
        Assert.assertEquals("", tableAnno.name());
        Assert.assertEquals("", tableAnno.catalog());
        Assert.assertEquals("", tableAnno.schema());
        Assert.assertEquals(0, tableAnno.joinColumns().length);
        Assert.assertEquals(0, tableAnno.uniqueConstraints().length);
    }

    @Test
    public void testCollectionTableAllChildren() throws Exception {
        reader = getReader(Entity3.class, "field1", "element-collection.orm27.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationPresent(CollectionTable.class);
        CollectionTable tableAnno = reader.getAnnotation(CollectionTable.class);
        Assert.assertEquals("table1", tableAnno.name());
        Assert.assertEquals("catalog1", tableAnno.catalog());
        Assert.assertEquals("schema1", tableAnno.schema());
        // JoinColumns
        JoinColumn[] joinColumns = tableAnno.joinColumns();
        Assert.assertEquals(2, joinColumns.length);
        Assert.assertEquals("", joinColumns[0].name());
        Assert.assertEquals("", joinColumns[0].referencedColumnName());
        Assert.assertEquals("", joinColumns[0].table());
        Assert.assertEquals("", joinColumns[0].columnDefinition());
        Assert.assertTrue(joinColumns[0].insertable());
        Assert.assertTrue(joinColumns[0].updatable());
        Assert.assertTrue(joinColumns[0].nullable());
        Assert.assertFalse(joinColumns[0].unique());
        Assert.assertEquals("col1", joinColumns[1].name());
        Assert.assertEquals("col2", joinColumns[1].referencedColumnName());
        Assert.assertEquals("table2", joinColumns[1].table());
        Assert.assertEquals("int", joinColumns[1].columnDefinition());
        Assert.assertFalse(joinColumns[1].insertable());
        Assert.assertFalse(joinColumns[1].updatable());
        Assert.assertFalse(joinColumns[1].nullable());
        Assert.assertTrue(joinColumns[1].unique());
        // UniqueConstraints
        UniqueConstraint[] uniqueConstraints = tableAnno.uniqueConstraints();
        Assert.assertEquals(2, uniqueConstraints.length);
        Assert.assertEquals("", uniqueConstraints[0].name());
        Assert.assertEquals(1, uniqueConstraints[0].columnNames().length);
        Assert.assertEquals("col3", uniqueConstraints[0].columnNames()[0]);
        Assert.assertEquals("uq1", uniqueConstraints[1].name());
        Assert.assertEquals(2, uniqueConstraints[1].columnNames().length);
        Assert.assertEquals("col4", uniqueConstraints[1].columnNames()[0]);
        Assert.assertEquals("col5", uniqueConstraints[1].columnNames()[1]);
    }

    @Test
    public void testAllAttributes() throws Exception {
        reader = getReader(Entity2.class, "field1", "element-collection.orm28.xml");
        assertAnnotationPresent(ElementCollection.class);
        assertAnnotationNotPresent(OrderBy.class);
        assertAnnotationNotPresent(OrderColumn.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        assertAnnotationNotPresent(Column.class);
        assertAnnotationNotPresent(Temporal.class);
        assertAnnotationNotPresent(Enumerated.class);
        assertAnnotationNotPresent(Lob.class);
        assertAnnotationNotPresent(AttributeOverride.class);
        assertAnnotationNotPresent(AttributeOverrides.class);
        assertAnnotationNotPresent(AssociationOverride.class);
        assertAnnotationNotPresent(AssociationOverrides.class);
        assertAnnotationNotPresent(CollectionTable.class);
        assertAnnotationPresent(Access.class);
        ElementCollection relAnno = reader.getAnnotation(ElementCollection.class);
        Assert.assertEquals(EAGER, relAnno.fetch());
        Assert.assertEquals(Entity3.class, relAnno.targetClass());
        Assert.assertEquals(PROPERTY, reader.getAnnotation(Access.class).value());
    }
}

