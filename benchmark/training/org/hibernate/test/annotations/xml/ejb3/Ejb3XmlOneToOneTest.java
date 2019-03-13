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
import FetchType.EAGER;
import FetchType.LAZY;
import javax.persistence.Access;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.UniqueConstraint;
import org.junit.Assert;
import org.junit.Test;


public class Ejb3XmlOneToOneTest extends Ejb3XmlTestCase {
    @Test
    public void testNoChildren() throws Exception {
        reader = getReader(Entity1.class, "field1", "one-to-one.orm1.xml");
        assertAnnotationPresent(OneToOne.class);
        assertAnnotationNotPresent(MapsId.class);
        assertAnnotationNotPresent(Id.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumn.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumns.class);
        assertAnnotationNotPresent(JoinColumns.class);
        assertAnnotationNotPresent(JoinColumn.class);
        assertAnnotationNotPresent(JoinTable.class);
        assertAnnotationNotPresent(Access.class);
        OneToOne relAnno = reader.getAnnotation(OneToOne.class);
        Assert.assertEquals(0, relAnno.cascade().length);
        Assert.assertEquals(EAGER, relAnno.fetch());
        Assert.assertEquals("", relAnno.mappedBy());
        Assert.assertTrue(relAnno.optional());
        Assert.assertFalse(relAnno.orphanRemoval());
        Assert.assertEquals(void.class, relAnno.targetEntity());
    }

    /**
     * When there's a single primary key join column, we still wrap it with
     * a PrimaryKeyJoinColumns annotation.
     */
    @Test
    public void testSinglePrimaryKeyJoinColumn() throws Exception {
        reader = getReader(Entity1.class, "field1", "one-to-one.orm2.xml");
        assertAnnotationPresent(OneToOne.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumn.class);
        assertAnnotationPresent(PrimaryKeyJoinColumns.class);
        PrimaryKeyJoinColumns joinColumnsAnno = reader.getAnnotation(PrimaryKeyJoinColumns.class);
        assertAnnotationNotPresent(JoinColumns.class);
        assertAnnotationNotPresent(JoinColumn.class);
        assertAnnotationNotPresent(JoinTable.class);
        PrimaryKeyJoinColumn[] joinColumns = joinColumnsAnno.value();
        Assert.assertEquals(1, joinColumns.length);
        Assert.assertEquals("col1", joinColumns[0].name());
        Assert.assertEquals("col2", joinColumns[0].referencedColumnName());
        Assert.assertEquals("int", joinColumns[0].columnDefinition());
    }

    @Test
    public void testMultiplePrimaryKeyJoinColumn() throws Exception {
        reader = getReader(Entity1.class, "field1", "one-to-one.orm3.xml");
        assertAnnotationPresent(OneToOne.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumn.class);
        assertAnnotationPresent(PrimaryKeyJoinColumns.class);
        assertAnnotationNotPresent(JoinColumns.class);
        assertAnnotationNotPresent(JoinColumn.class);
        assertAnnotationNotPresent(JoinTable.class);
        PrimaryKeyJoinColumns joinColumnsAnno = reader.getAnnotation(PrimaryKeyJoinColumns.class);
        PrimaryKeyJoinColumn[] joinColumns = joinColumnsAnno.value();
        Assert.assertEquals(2, joinColumns.length);
        Assert.assertEquals("", joinColumns[0].name());
        Assert.assertEquals("", joinColumns[0].referencedColumnName());
        Assert.assertEquals("", joinColumns[0].columnDefinition());
        Assert.assertEquals("col1", joinColumns[1].name());
        Assert.assertEquals("col2", joinColumns[1].referencedColumnName());
        Assert.assertEquals("int", joinColumns[1].columnDefinition());
    }

    /**
     * When there's a single join column, we still wrap it with a JoinColumns
     * annotation.
     */
    @Test
    public void testSingleJoinColumn() throws Exception {
        reader = getReader(Entity1.class, "field1", "one-to-one.orm4.xml");
        assertAnnotationPresent(OneToOne.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumn.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumns.class);
        assertAnnotationPresent(JoinColumns.class);
        assertAnnotationNotPresent(JoinColumn.class);
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
        reader = getReader(Entity1.class, "field1", "one-to-one.orm5.xml");
        assertAnnotationPresent(OneToOne.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumn.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumns.class);
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
    public void testJoinTableNoChildren() throws Exception {
        reader = getReader(Entity1.class, "field1", "one-to-one.orm6.xml");
        assertAnnotationPresent(OneToOne.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumn.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumns.class);
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
        reader = getReader(Entity1.class, "field1", "one-to-one.orm7.xml");
        assertAnnotationPresent(OneToOne.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumn.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumns.class);
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

    @Test
    public void testCascadeAll() throws Exception {
        reader = getReader(Entity1.class, "field1", "one-to-one.orm8.xml");
        assertAnnotationPresent(OneToOne.class);
        OneToOne relAnno = reader.getAnnotation(OneToOne.class);
        Assert.assertEquals(1, relAnno.cascade().length);
        Assert.assertEquals(ALL, relAnno.cascade()[0]);
    }

    @Test
    public void testCascadeSomeWithDefaultPersist() throws Exception {
        reader = getReader(Entity1.class, "field1", "one-to-one.orm9.xml");
        assertAnnotationPresent(OneToOne.class);
        OneToOne relAnno = reader.getAnnotation(OneToOne.class);
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
        reader = getReader(Entity1.class, "field1", "one-to-one.orm10.xml");
        assertAnnotationPresent(OneToOne.class);
        OneToOne relAnno = reader.getAnnotation(OneToOne.class);
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
        reader = getReader(Entity1.class, "field1", "one-to-one.orm11.xml");
        assertAnnotationPresent(OneToOne.class);
        assertAnnotationPresent(MapsId.class);
        assertAnnotationPresent(Id.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumn.class);
        assertAnnotationNotPresent(PrimaryKeyJoinColumns.class);
        assertAnnotationNotPresent(JoinColumns.class);
        assertAnnotationNotPresent(JoinColumn.class);
        assertAnnotationNotPresent(JoinTable.class);
        assertAnnotationPresent(Access.class);
        OneToOne relAnno = reader.getAnnotation(OneToOne.class);
        Assert.assertEquals(0, relAnno.cascade().length);
        Assert.assertEquals(LAZY, relAnno.fetch());
        Assert.assertEquals("field2", relAnno.mappedBy());
        Assert.assertFalse(relAnno.optional());
        Assert.assertTrue(relAnno.orphanRemoval());
        Assert.assertEquals(Entity3.class, relAnno.targetEntity());
        Assert.assertEquals(PROPERTY, reader.getAnnotation(Access.class).value());
        Assert.assertEquals("field3", reader.getAnnotation(MapsId.class).value());
    }
}

