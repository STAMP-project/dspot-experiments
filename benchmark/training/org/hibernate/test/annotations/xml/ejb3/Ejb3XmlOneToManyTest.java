/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.xml.ejb3;


import AccessType.PROPERTY;
import CascadeType.ALL;
import CascadeType.DETACH;
import CascadeType.MERGE;
import CascadeType.PERSIST;
import CascadeType.REFRESH;
import CascadeType.REMOVE;
import EnumType.STRING;
import FetchType.EAGER;
import FetchType.LAZY;
import TemporalType.DATE;
import javax.persistence.Access;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.MapKeyJoinColumns;
import javax.persistence.MapKeyTemporal;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.UniqueConstraint;
import org.junit.Assert;
import org.junit.Test;


public class Ejb3XmlOneToManyTest extends Ejb3XmlTestCase {
    @Test
    public void testNoChildren() throws Exception {
        reader = getReader(Entity2.class, "field1", "one-to-many.orm1.xml");
        assertAnnotationPresent(OneToMany.class);
        assertAnnotationNotPresent(OrderBy.class);
        assertAnnotationNotPresent(OrderColumn.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        assertAnnotationNotPresent(JoinTable.class);
        assertAnnotationNotPresent(JoinColumns.class);
        assertAnnotationNotPresent(JoinColumn.class);
        assertAnnotationNotPresent(Access.class);
        OneToMany relAnno = reader.getAnnotation(OneToMany.class);
        Assert.assertEquals(0, relAnno.cascade().length);
        Assert.assertEquals(LAZY, relAnno.fetch());
        Assert.assertEquals("", relAnno.mappedBy());
        Assert.assertFalse(relAnno.orphanRemoval());
        Assert.assertEquals(void.class, relAnno.targetEntity());
    }

    @Test
    public void testOrderBy() throws Exception {
        reader = getReader(Entity2.class, "field1", "one-to-many.orm2.xml");
        assertAnnotationPresent(OneToMany.class);
        assertAnnotationPresent(OrderBy.class);
        assertAnnotationNotPresent(OrderColumn.class);
        Assert.assertEquals("col1 ASC, col2 DESC", reader.getAnnotation(OrderBy.class).value());
    }

    @Test
    public void testOrderColumnNoAttributes() throws Exception {
        reader = getReader(Entity2.class, "field1", "one-to-many.orm3.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity2.class, "field1", "one-to-many.orm4.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity3.class, "field1", "one-to-many.orm5.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity3.class, "field1", "one-to-many.orm6.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity3.class, "field1", "one-to-many.orm7.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity3.class, "field1", "one-to-many.orm8.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity3.class, "field1", "one-to-many.orm9.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity3.class, "field1", "one-to-many.orm10.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity3.class, "field1", "one-to-many.orm11.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity3.class, "field1", "one-to-many.orm12.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity3.class, "field1", "one-to-many.orm13.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity3.class, "field1", "one-to-many.orm14.xml");
        assertAnnotationPresent(OneToMany.class);
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
        reader = getReader(Entity3.class, "field1", "one-to-many.orm15.xml");
        assertAnnotationPresent(OneToMany.class);
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
    public void testJoinTableNoChildren() throws Exception {
        reader = getReader(Entity2.class, "field1", "one-to-many.orm16.xml");
        assertAnnotationPresent(OneToMany.class);
        assertAnnotationPresent(JoinTable.class);
        assertAnnotationNotPresent(JoinColumns.class);
        assertAnnotationNotPresent(JoinColumn.class);
        JoinTable joinTableAnno = reader.getAnnotation(JoinTable.class);
        Assert.assertEquals("", joinTableAnno.catalog());
        Assert.assertEquals("", joinTableAnno.name());
        Assert.assertEquals("", joinTableAnno.schema());
        Assert.assertEquals(0, joinTableAnno.joinColumns().length);
        Assert.assertEquals(0, joinTableAnno.inverseJoinColumns().length);
        Assert.assertEquals(0, joinTableAnno.uniqueConstraints().length);
    }

    @Test
    public void testJoinTableAllChildren() throws Exception {
        reader = getReader(Entity2.class, "field1", "one-to-many.orm17.xml");
        assertAnnotationPresent(OneToMany.class);
        assertAnnotationPresent(JoinTable.class);
        assertAnnotationNotPresent(JoinColumns.class);
        assertAnnotationNotPresent(JoinColumn.class);
        JoinTable joinTableAnno = reader.getAnnotation(JoinTable.class);
        Assert.assertEquals("cat1", joinTableAnno.catalog());
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
    }

    /**
     * When there's a single join column, we still wrap it with a JoinColumns
     * annotation.
     */
    @Test
    public void testSingleJoinColumn() throws Exception {
        reader = getReader(Entity2.class, "field1", "one-to-many.orm18.xml");
        assertAnnotationPresent(OneToMany.class);
        assertAnnotationNotPresent(JoinColumn.class);
        assertAnnotationPresent(JoinColumns.class);
        assertAnnotationNotPresent(JoinTable.class);
        JoinColumns joinColumnsAnno = reader.getAnnotation(JoinColumns.class);
        JoinColumn[] joinColumns = joinColumnsAnno.value();
        Assert.assertEquals(1, joinColumns.length);
        Assert.assertEquals("col1", joinColumns[0].name());
        Assert.assertEquals("col2", joinColumns[0].referencedColumnName());
        Assert.assertEquals("table1", joinColumns[0].table());
    }

    @Test
    public void testMultipleJoinColumns() throws Exception {
        reader = getReader(Entity2.class, "field1", "one-to-many.orm19.xml");
        assertAnnotationPresent(OneToMany.class);
        assertAnnotationNotPresent(JoinColumn.class);
        assertAnnotationPresent(JoinColumns.class);
        assertAnnotationNotPresent(JoinTable.class);
        JoinColumns joinColumnsAnno = reader.getAnnotation(JoinColumns.class);
        JoinColumn[] joinColumns = joinColumnsAnno.value();
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
        Assert.assertEquals("table1", joinColumns[1].table());
        Assert.assertEquals("int", joinColumns[1].columnDefinition());
        Assert.assertFalse(joinColumns[1].insertable());
        Assert.assertFalse(joinColumns[1].updatable());
        Assert.assertFalse(joinColumns[1].nullable());
        Assert.assertTrue(joinColumns[1].unique());
    }

    @Test
    public void testCascadeAll() throws Exception {
        reader = getReader(Entity2.class, "field1", "one-to-many.orm20.xml");
        assertAnnotationPresent(OneToMany.class);
        OneToMany relAnno = reader.getAnnotation(OneToMany.class);
        Assert.assertEquals(1, relAnno.cascade().length);
        Assert.assertEquals(ALL, relAnno.cascade()[0]);
    }

    @Test
    public void testCascadeSomeWithDefaultPersist() throws Exception {
        reader = getReader(Entity2.class, "field1", "one-to-many.orm21.xml");
        assertAnnotationPresent(OneToMany.class);
        OneToMany relAnno = reader.getAnnotation(OneToMany.class);
        Assert.assertEquals(4, relAnno.cascade().length);
        Assert.assertEquals(REMOVE, relAnno.cascade()[0]);
        Assert.assertEquals(REFRESH, relAnno.cascade()[1]);
        Assert.assertEquals(DETACH, relAnno.cascade()[2]);
        Assert.assertEquals(PERSIST, relAnno.cascade()[3]);
    }

    /**
     * Make sure that it doesn't break the handler when {@link CascadeType#ALL}
     * is specified in addition to a default cascade-persist or individual
     * cascade settings.
     */
    @Test
    public void testCascadeAllPlusMore() throws Exception {
        reader = getReader(Entity2.class, "field1", "one-to-many.orm22.xml");
        assertAnnotationPresent(OneToMany.class);
        OneToMany relAnno = reader.getAnnotation(OneToMany.class);
        Assert.assertEquals(6, relAnno.cascade().length);
        Assert.assertEquals(ALL, relAnno.cascade()[0]);
        Assert.assertEquals(PERSIST, relAnno.cascade()[1]);
        Assert.assertEquals(MERGE, relAnno.cascade()[2]);
        Assert.assertEquals(REMOVE, relAnno.cascade()[3]);
        Assert.assertEquals(REFRESH, relAnno.cascade()[4]);
        Assert.assertEquals(DETACH, relAnno.cascade()[5]);
    }

    @Test
    public void testAllAttributes() throws Exception {
        reader = getReader(Entity2.class, "field1", "one-to-many.orm23.xml");
        assertAnnotationPresent(OneToMany.class);
        assertAnnotationNotPresent(OrderBy.class);
        assertAnnotationNotPresent(OrderColumn.class);
        assertAnnotationNotPresent(MapKey.class);
        assertAnnotationNotPresent(MapKeyClass.class);
        assertAnnotationNotPresent(MapKeyTemporal.class);
        assertAnnotationNotPresent(MapKeyEnumerated.class);
        assertAnnotationNotPresent(MapKeyColumn.class);
        assertAnnotationNotPresent(MapKeyJoinColumns.class);
        assertAnnotationNotPresent(MapKeyJoinColumn.class);
        assertAnnotationNotPresent(JoinTable.class);
        assertAnnotationNotPresent(JoinColumns.class);
        assertAnnotationNotPresent(JoinColumn.class);
        assertAnnotationPresent(Access.class);
        OneToMany relAnno = reader.getAnnotation(OneToMany.class);
        Assert.assertEquals(0, relAnno.cascade().length);
        Assert.assertEquals(EAGER, relAnno.fetch());
        Assert.assertEquals("field2", relAnno.mappedBy());
        Assert.assertTrue(relAnno.orphanRemoval());
        Assert.assertEquals(Entity3.class, relAnno.targetEntity());
        Assert.assertEquals(PROPERTY, reader.getAnnotation(Access.class).value());
    }
}

