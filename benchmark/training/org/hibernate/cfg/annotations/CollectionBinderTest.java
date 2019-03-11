/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.cfg.annotations;


import java.sql.SQLException;
import java.util.HashMap;
import org.hibernate.MappingException;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test for HHH-10106
 *
 * @author Vyacheslav Rarata
 */
public class CollectionBinderTest extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10106")
    public void testAssociatedClassException() throws SQLException {
        final Collection collection = Mockito.mock(Collection.class);
        final XClass collectionType = Mockito.mock(XClass.class);
        final MetadataBuildingContext buildingContext = Mockito.mock(MetadataBuildingContext.class);
        final InFlightMetadataCollector inFly = Mockito.mock(InFlightMetadataCollector.class);
        final PersistentClass persistentClass = Mockito.mock(PersistentClass.class);
        final Table table = Mockito.mock(Table.class);
        Mockito.when(buildingContext.getMetadataCollector()).thenReturn(inFly);
        Mockito.when(collection.getOwner()).thenReturn(persistentClass);
        Mockito.when(collectionType.getName()).thenReturn("List");
        Mockito.when(persistentClass.getTable()).thenReturn(table);
        Mockito.when(table.getName()).thenReturn("Hibernate");
        CollectionBinder collectionBinder = new CollectionBinder(false) {
            @Override
            protected Collection createCollection(PersistentClass persistentClass) {
                return null;
            }

            {
                final PropertyHolder propertyHolder = Mockito.mock(PropertyHolder.class);
                Mockito.when(propertyHolder.getClassName()).thenReturn(CollectionBinderTest.class.getSimpleName());
                this.propertyName = "abc";
                this.propertyHolder = propertyHolder;
            }
        };
        String expectMessage = "Association [abc] for entity [CollectionBinderTest] references unmapped class [List]";
        try {
            collectionBinder.bindOneToManySecondPass(collection, new HashMap(), null, collectionType, false, false, buildingContext, null);
        } catch (MappingException e) {
            Assert.assertEquals(expectMessage, e.getMessage());
        }
    }
}

