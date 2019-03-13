/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.index.jpa;


import java.util.Iterator;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Strong Liu <stliu@hibernate.org>
 */
public abstract class AbstractJPAIndexTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testTableIndex() {
        PersistentClass entity = metadata().getEntityBinding(Car.class.getName());
        Iterator itr = entity.getTable().getUniqueKeyIterator();
        Assert.assertTrue(itr.hasNext());
        UniqueKey uk = ((UniqueKey) (itr.next()));
        Assert.assertFalse(itr.hasNext());
        Assert.assertTrue(StringHelper.isNotEmpty(uk.getName()));
        Assert.assertEquals(2, uk.getColumnSpan());
        Column column = ((Column) (uk.getColumns().get(0)));
        Assert.assertEquals("brand", column.getName());
        column = ((Column) (uk.getColumns().get(1)));
        Assert.assertEquals("producer", column.getName());
        Assert.assertSame(entity.getTable(), uk.getTable());
        itr = entity.getTable().getIndexIterator();
        Assert.assertTrue(itr.hasNext());
        Index index = ((Index) (itr.next()));
        Assert.assertFalse(itr.hasNext());
        Assert.assertEquals("Car_idx", index.getName());
        Assert.assertEquals(1, index.getColumnSpan());
        column = index.getColumnIterator().next();
        Assert.assertEquals("since", column.getName());
        Assert.assertSame(entity.getTable(), index.getTable());
    }

    @Test
    public void testSecondaryTableIndex() {
        PersistentClass entity = metadata().getEntityBinding(Car.class.getName());
        Join join = ((Join) (entity.getJoinIterator().next()));
        Iterator<Index> itr = join.getTable().getIndexIterator();
        Assert.assertTrue(itr.hasNext());
        Index index = itr.next();
        Assert.assertFalse(itr.hasNext());
        Assert.assertTrue("index name is not generated", StringHelper.isNotEmpty(index.getName()));
        Assert.assertEquals(2, index.getColumnSpan());
        Iterator<Column> columnIterator = index.getColumnIterator();
        Column column = columnIterator.next();
        Assert.assertEquals("dealer_name", column.getName());
        column = columnIterator.next();
        Assert.assertEquals("rate", column.getName());
        Assert.assertSame(join.getTable(), index.getTable());
    }

    @Test
    public void testCollectionTableIndex() {
        PersistentClass entity = metadata().getEntityBinding(Car.class.getName());
        Property property = entity.getProperty("otherDealers");
        Set set = ((Set) (property.getValue()));
        Table collectionTable = set.getCollectionTable();
        Iterator<Index> itr = collectionTable.getIndexIterator();
        Assert.assertTrue(itr.hasNext());
        Index index = itr.next();
        Assert.assertFalse(itr.hasNext());
        Assert.assertTrue("index name is not generated", StringHelper.isNotEmpty(index.getName()));
        Assert.assertEquals(1, index.getColumnSpan());
        Iterator<Column> columnIterator = index.getColumnIterator();
        Column column = columnIterator.next();
        Assert.assertEquals("name", column.getName());
        Assert.assertSame(collectionTable, index.getTable());
    }

    @Test
    public void testJoinTableIndex() {
        PersistentClass entity = metadata().getEntityBinding(Importer.class.getName());
        Property property = entity.getProperty("cars");
        Bag set = ((Bag) (property.getValue()));
        Table collectionTable = set.getCollectionTable();
        Iterator<Index> itr = collectionTable.getIndexIterator();
        Assert.assertTrue(itr.hasNext());
        Index index = itr.next();
        Assert.assertFalse(itr.hasNext());
        Assert.assertTrue("index name is not generated", StringHelper.isNotEmpty(index.getName()));
        Assert.assertEquals(1, index.getColumnSpan());
        Iterator<Column> columnIterator = index.getColumnIterator();
        Column column = columnIterator.next();
        Assert.assertEquals("importers_id", column.getName());
        Assert.assertSame(collectionTable, index.getTable());
    }

    @Test
    public void testTableGeneratorIndex() {
        // todo
    }
}

