package com.vaadin.v7.data.util.sqlcontainer;


import com.vaadin.v7.data.Container.ItemSetChangeEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.filter.Like;
import com.vaadin.v7.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.v7.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.v7.data.util.sqlcontainer.query.TableQuery;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;

import static SQLContainer.CACHE_RATIO;
import static SQLContainer.DEFAULT_PAGE_LENGTH;
import static com.vaadin.v7.data.util.sqlcontainer.SQLTestsConstants.DB.MSSQL;
import static com.vaadin.v7.data.util.sqlcontainer.SQLTestsConstants.DB.ORACLE;


public class SQLContainerTableQueryTest {
    private static final int offset = SQLTestsConstants.offset;

    private final int numberOfRowsInContainer = 4;

    private final int numberOfPropertiesInContainer = 3;

    private final String NAME = "NAME";

    private final String ID = "ID";

    private final String AGE = "AGE";

    private JDBCConnectionPool connectionPool;

    private TableQuery query;

    private SQLContainer container;

    private final RowId existingItemId = getRowId(1);

    private final RowId nonExistingItemId = getRowId(1337);

    @Test
    public void itemWithExistingVersionColumnIsRemoved() throws SQLException {
        container.setAutoCommit(true);
        query.setVersionColumn(ID);
        Assert.assertTrue(container.removeItem(container.lastItemId()));
    }

    @Test(expected = SQLException.class)
    public void itemWithNonExistingVersionColumnCannotBeRemoved() throws SQLException {
        query.setVersionColumn("version");
        container.removeItem(container.lastItemId());
        container.commit();
    }

    @Test
    public void containerContainsId() {
        Assert.assertTrue(container.containsId(existingItemId));
    }

    @Test
    public void containerDoesNotContainId() {
        Assert.assertFalse(container.containsId(nonExistingItemId));
    }

    @Test
    public void idPropertyHasCorrectType() {
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertEquals(container.getType(ID), BigDecimal.class);
        } else {
            Assert.assertEquals(container.getType(ID), Integer.class);
        }
    }

    @Test
    public void namePropertyHasCorrectType() {
        Assert.assertEquals(container.getType(NAME), String.class);
    }

    @Test
    public void nonExistingPropertyDoesNotHaveType() {
        MatcherAssert.assertThat(container.getType("adsf"), Is.is(IsNull.nullValue()));
    }

    @Test
    public void sizeIsReturnedCorrectly() {
        Assert.assertEquals(numberOfRowsInContainer, container.size());
    }

    @Test
    public void propertyIsFetchedForExistingItem() {
        MatcherAssert.assertThat(container.getContainerProperty(existingItemId, NAME).getValue().toString(), Is.is("Kalle"));
    }

    @Test
    public void containerDoesNotContainPropertyForExistingItem() {
        MatcherAssert.assertThat(container.getContainerProperty(existingItemId, "asdf"), Is.is(IsNull.nullValue()));
    }

    @Test
    public void containerDoesNotContainExistingPropertyForNonExistingItem() {
        MatcherAssert.assertThat(container.getContainerProperty(nonExistingItemId, NAME), Is.is(IsNull.nullValue()));
    }

    @Test
    public void propertyIdsAreFetched() {
        List<String> propertyIds = new ArrayList<String>(((Collection<? extends String>) (container.getContainerPropertyIds())));
        MatcherAssert.assertThat(propertyIds.size(), Is.is(numberOfPropertiesInContainer));
        MatcherAssert.assertThat(propertyIds, CoreMatchers.hasItems(ID, NAME, AGE));
    }

    @Test
    public void existingItemIsFetched() {
        Item item = container.getItem(existingItemId);
        MatcherAssert.assertThat(item.getItemProperty(NAME).getValue().toString(), Is.is("Kalle"));
    }

    @Test
    public void newItemIsAdded() throws SQLException {
        Object id = container.addItem();
        getItem(id).getItemProperty(NAME).setValue("foo");
        container.commit();
        Item item = getItem(container.lastItemId());
        MatcherAssert.assertThat(item.getItemProperty(NAME).getValue().toString(), Is.is("foo"));
    }

    @Test
    public void itemPropertyIsNotRevertedOnRefresh() {
        getItem(existingItemId).getItemProperty(NAME).setValue("foo");
        container.refresh();
        MatcherAssert.assertThat(getItem(existingItemId).getItemProperty(NAME).getValue().toString(), Is.is("foo"));
    }

    @Test
    public void correctItemIsFetchedFromMultipleRows() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        Item item = container.getItem(getRowId(1337));
        MatcherAssert.assertThat(((Integer) (item.getItemProperty(ID).getValue())), Is.is(CoreMatchers.equalTo((1337 + (SQLContainerTableQueryTest.offset)))));
        MatcherAssert.assertThat(item.getItemProperty(NAME).getValue().toString(), Is.is("Person 1337"));
    }

    @Test
    public void getItemIds_table_returnsItemIdsWithKeys0through3() throws SQLException {
        Collection<?> itemIds = container.getItemIds();
        Assert.assertEquals(4, itemIds.size());
        RowId zero = new RowId(new Object[]{ 0 + (SQLContainerTableQueryTest.offset) });
        RowId one = new RowId(new Object[]{ 1 + (SQLContainerTableQueryTest.offset) });
        RowId two = new RowId(new Object[]{ 2 + (SQLContainerTableQueryTest.offset) });
        RowId three = new RowId(new Object[]{ 3 + (SQLContainerTableQueryTest.offset) });
        if ((SQLTestsConstants.db) == (ORACLE)) {
            String[] correct = new String[]{ "1", "2", "3", "4" };
            List<String> oracle = new ArrayList<String>();
            for (Object o : itemIds) {
                oracle.add(o.toString());
            }
            Assert.assertArrayEquals(correct, oracle.toArray());
        } else {
            Assert.assertArrayEquals(new Object[]{ zero, one, two, three }, itemIds.toArray());
        }
    }

    @Test
    public void size_tableOneAddedItem_returnsFive() throws SQLException {
        Connection conn = connectionPool.reserveConnection();
        Statement statement = conn.createStatement();
        if ((SQLTestsConstants.db) == (MSSQL)) {
            statement.executeUpdate("insert into people values('Bengt', 30)");
        } else {
            statement.executeUpdate("insert into people values(default, 'Bengt', 30)");
        }
        statement.close();
        conn.commit();
        connectionPool.releaseConnection(conn);
        Assert.assertEquals(5, container.size());
    }

    @Test
    public void indexOfId_tableWithParameterThree_returnsThree() throws SQLException {
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertEquals(3, container.indexOfId(new RowId(new Object[]{ new BigDecimal((3 + (SQLContainerTableQueryTest.offset))) })));
        } else {
            Assert.assertEquals(3, container.indexOfId(new RowId(new Object[]{ 3 + (SQLContainerTableQueryTest.offset) })));
        }
    }

    @Test
    public void indexOfId_table5000RowsWithParameter1337_returns1337() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        if ((SQLTestsConstants.db) == (ORACLE)) {
            container.getItem(new RowId(new Object[]{ new BigDecimal((1337 + (SQLContainerTableQueryTest.offset))) }));
            Assert.assertEquals(1337, container.indexOfId(new RowId(new Object[]{ new BigDecimal((1337 + (SQLContainerTableQueryTest.offset))) })));
        } else {
            container.getItem(new RowId(new Object[]{ 1337 + (SQLContainerTableQueryTest.offset) }));
            Assert.assertEquals(1337, container.indexOfId(new RowId(new Object[]{ 1337 + (SQLContainerTableQueryTest.offset) })));
        }
    }

    @Test
    public void getIdByIndex_table5000rowsIndex1337_returnsRowId1337() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        Object itemId = container.getIdByIndex(1337);
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertEquals(new RowId(new Object[]{ 1337 + (SQLContainerTableQueryTest.offset) }).toString(), itemId.toString());
        } else {
            Assert.assertEquals(new RowId(new Object[]{ 1337 + (SQLContainerTableQueryTest.offset) }), itemId);
        }
    }

    @Test
    public void getIdByIndex_tableWithPaging5000rowsIndex1337_returnsRowId1337() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        Object itemId = container.getIdByIndex(1337);
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertEquals(new RowId(new Object[]{ 1337 + (SQLContainerTableQueryTest.offset) }).toString(), itemId.toString());
        } else {
            Assert.assertEquals(new RowId(new Object[]{ 1337 + (SQLContainerTableQueryTest.offset) }), itemId);
        }
    }

    @Test
    public void nextItemId_tableCurrentItem1337_returnsItem1338() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        SQLContainer container = new SQLContainer(new TableQuery("people", connectionPool, SQLTestsConstants.sqlGen));
        Object itemId = container.getIdByIndex(1337);
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertEquals(new RowId(new Object[]{ 1338 + (SQLContainerTableQueryTest.offset) }).toString(), container.nextItemId(itemId).toString());
        } else {
            Assert.assertEquals(new RowId(new Object[]{ 1338 + (SQLContainerTableQueryTest.offset) }), container.nextItemId(itemId));
        }
    }

    @Test
    public void prevItemId_tableCurrentItem1337_returns1336() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        Object itemId = container.getIdByIndex(1337);
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertEquals(new RowId(new Object[]{ 1336 + (SQLContainerTableQueryTest.offset) }).toString(), container.prevItemId(itemId).toString());
        } else {
            Assert.assertEquals(new RowId(new Object[]{ 1336 + (SQLContainerTableQueryTest.offset) }), container.prevItemId(itemId));
        }
    }

    @Test
    public void firstItemId_table_returnsItemId0() throws SQLException {
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertEquals(new RowId(new Object[]{ 0 + (SQLContainerTableQueryTest.offset) }).toString(), container.firstItemId().toString());
        } else {
            Assert.assertEquals(new RowId(new Object[]{ 0 + (SQLContainerTableQueryTest.offset) }), container.firstItemId());
        }
    }

    @Test
    public void lastItemId_table5000Rows_returnsItemId4999() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertEquals(new RowId(new Object[]{ 4999 + (SQLContainerTableQueryTest.offset) }).toString(), container.lastItemId().toString());
        } else {
            Assert.assertEquals(new RowId(new Object[]{ 4999 + (SQLContainerTableQueryTest.offset) }), container.lastItemId());
        }
    }

    @Test
    public void isFirstId_tableActualFirstId_returnsTrue() throws SQLException {
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertTrue(container.isFirstId(new RowId(new Object[]{ new BigDecimal((0 + (SQLContainerTableQueryTest.offset))) })));
        } else {
            Assert.assertTrue(container.isFirstId(new RowId(new Object[]{ 0 + (SQLContainerTableQueryTest.offset) })));
        }
    }

    @Test
    public void isFirstId_tableSecondId_returnsFalse() throws SQLException {
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertFalse(container.isFirstId(new RowId(new Object[]{ new BigDecimal((1 + (SQLContainerTableQueryTest.offset))) })));
        } else {
            Assert.assertFalse(container.isFirstId(new RowId(new Object[]{ 1 + (SQLContainerTableQueryTest.offset) })));
        }
    }

    @Test
    public void isLastId_tableSecondId_returnsFalse() throws SQLException {
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertFalse(container.isLastId(new RowId(new Object[]{ new BigDecimal((1 + (SQLContainerTableQueryTest.offset))) })));
        } else {
            Assert.assertFalse(container.isLastId(new RowId(new Object[]{ 1 + (SQLContainerTableQueryTest.offset) })));
        }
    }

    @Test
    public void isLastId_tableLastId_returnsTrue() throws SQLException {
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertTrue(container.isLastId(new RowId(new Object[]{ new BigDecimal((3 + (SQLContainerTableQueryTest.offset))) })));
        } else {
            Assert.assertTrue(container.isLastId(new RowId(new Object[]{ 3 + (SQLContainerTableQueryTest.offset) })));
        }
    }

    @Test
    public void isLastId_table5000RowsLastId_returnsTrue() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        if ((SQLTestsConstants.db) == (ORACLE)) {
            Assert.assertTrue(container.isLastId(new RowId(new Object[]{ new BigDecimal((4999 + (SQLContainerTableQueryTest.offset))) })));
        } else {
            Assert.assertTrue(container.isLastId(new RowId(new Object[]{ 4999 + (SQLContainerTableQueryTest.offset) })));
        }
    }

    @Test
    public void allIdsFound_table5000RowsLastId_shouldSucceed() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        for (int i = 0; i < 5000; i++) {
            Assert.assertTrue(container.containsId(container.getIdByIndex(i)));
        }
    }

    @Test
    public void allIdsFound_table5000RowsLastId_autoCommit_shouldSucceed() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        container.setAutoCommit(true);
        for (int i = 0; i < 5000; i++) {
            Assert.assertTrue(container.containsId(container.getIdByIndex(i)));
        }
    }

    @Test
    public void refresh_table_sizeShouldUpdate() throws SQLException {
        Assert.assertEquals(4, container.size());
        DataGenerator.addFiveThousandPeople(connectionPool);
        container.refresh();
        Assert.assertEquals(5000, container.size());
    }

    @Test
    public void refresh_tableWithoutCallingRefresh_sizeShouldNotUpdate() throws SQLException {
        // Yeah, this is a weird one. We're testing that the size doesn't update
        // after adding lots of items unless we call refresh inbetween. This to
        // make sure that the refresh method actually refreshes stuff and isn't
        // a NOP.
        Assert.assertEquals(4, container.size());
        DataGenerator.addFiveThousandPeople(connectionPool);
        Assert.assertEquals(4, container.size());
    }

    @Test
    public void setAutoCommit_table_shouldSucceed() throws SQLException {
        container.setAutoCommit(true);
        Assert.assertTrue(container.isAutoCommit());
        container.setAutoCommit(false);
        Assert.assertFalse(container.isAutoCommit());
    }

    @Test
    public void getPageLength_table_returnsDefault100() throws SQLException {
        Assert.assertEquals(100, container.getPageLength());
    }

    @Test
    public void setPageLength_table_shouldSucceed() throws SQLException {
        container.setPageLength(20);
        Assert.assertEquals(20, container.getPageLength());
        container.setPageLength(200);
        Assert.assertEquals(200, container.getPageLength());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addContainerProperty_normal_isUnsupported() throws SQLException {
        container.addContainerProperty("asdf", String.class, "");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeContainerProperty_normal_isUnsupported() throws SQLException {
        container.removeContainerProperty("asdf");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemObject_normal_isUnsupported() throws SQLException {
        container.addItem("asdf");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAfterObjectObject_normal_isUnsupported() throws SQLException {
        container.addItemAfter("asdf", "foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAtIntObject_normal_isUnsupported() throws SQLException {
        container.addItemAt(2, "asdf");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAtInt_normal_isUnsupported() throws SQLException {
        container.addItemAt(2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAfterObject_normal_isUnsupported() throws SQLException {
        container.addItemAfter("asdf");
    }

    @Test
    public void addItem_tableAddOneNewItem_returnsItemId() throws SQLException {
        Object itemId = container.addItem();
        Assert.assertNotNull(itemId);
    }

    @Test
    public void addItem_tableAddOneNewItem_autoCommit_returnsFinalItemId() throws SQLException {
        container.setAutoCommit(true);
        Object itemId = container.addItem();
        Assert.assertNotNull(itemId);
        Assert.assertTrue((itemId instanceof RowId));
        Assert.assertFalse((itemId instanceof TemporaryRowId));
    }

    @Test
    public void addItem_tableAddOneNewItem_autoCommit_sizeIsIncreased() throws SQLException {
        container.setAutoCommit(true);
        int originalSize = container.size();
        container.addItem();
        Assert.assertEquals((originalSize + 1), container.size());
    }

    @Test
    public void addItem_tableAddOneNewItem_shouldChangeSize() throws SQLException {
        int size = container.size();
        container.addItem();
        Assert.assertEquals((size + 1), container.size());
    }

    @Test
    public void addItem_tableAddTwoNewItems_shouldChangeSize() throws SQLException {
        int size = container.size();
        Object id1 = container.addItem();
        Object id2 = container.addItem();
        Assert.assertEquals((size + 2), container.size());
        Assert.assertNotSame(id1, id2);
        Assert.assertFalse(id1.equals(id2));
    }

    @Test
    public void nextItemId_tableNewlyAddedItem_returnsNewlyAdded() throws SQLException {
        Object lastId = container.lastItemId();
        Object id = container.addItem();
        Assert.assertEquals(id, container.nextItemId(lastId));
    }

    @Test
    public void lastItemId_tableNewlyAddedItem_returnsNewlyAdded() throws SQLException {
        Object lastId = container.lastItemId();
        Object id = container.addItem();
        Assert.assertEquals(id, container.lastItemId());
        Assert.assertNotSame(lastId, container.lastItemId());
    }

    @Test
    public void indexOfId_tableNewlyAddedItem_returnsFour() throws SQLException {
        Object id = container.addItem();
        Assert.assertEquals(4, container.indexOfId(id));
    }

    @Test
    public void getItem_tableNewlyAddedItem_returnsNewlyAdded() throws SQLException {
        Object id = container.addItem();
        Assert.assertNotNull(container.getItem(id));
    }

    @Test
    public void getItemIds_tableNewlyAddedItem_containsNewlyAdded() throws SQLException {
        Object id = container.addItem();
        Assert.assertTrue(container.getItemIds().contains(id));
    }

    @Test
    public void getContainerProperty_tableNewlyAddedItem_returnsPropertyOfNewlyAddedItem() throws SQLException {
        Object id = container.addItem();
        Item item = container.getItem(id);
        item.getItemProperty(NAME).setValue("asdf");
        Assert.assertEquals("asdf", container.getContainerProperty(id, NAME).getValue());
    }

    @Test
    public void containsId_tableNewlyAddedItem_returnsTrue() throws SQLException {
        Object id = container.addItem();
        Assert.assertTrue(container.containsId(id));
    }

    @Test
    public void prevItemId_tableTwoNewlyAddedItems_returnsFirstAddedItem() throws SQLException {
        Object id1 = container.addItem();
        Object id2 = container.addItem();
        Assert.assertEquals(id1, container.prevItemId(id2));
    }

    @Test
    public void firstItemId_tableEmptyResultSet_returnsFirstAddedItem() throws SQLException {
        SQLContainer garbageContainer = getGarbageContainer();
        Object id = garbageContainer.addItem();
        Assert.assertSame(id, garbageContainer.firstItemId());
    }

    @Test
    public void isFirstId_tableEmptyResultSet_returnsFirstAddedItem() throws SQLException {
        SQLContainer garbageContainer = getGarbageContainer();
        Object id = garbageContainer.addItem();
        Assert.assertTrue(garbageContainer.isFirstId(id));
    }

    @Test
    public void isLastId_tableOneItemAdded_returnsTrueForAddedItem() throws SQLException {
        Object id = container.addItem();
        Assert.assertTrue(container.isLastId(id));
    }

    @Test
    public void isLastId_tableTwoItemsAdded_returnsTrueForLastAddedItem() throws SQLException {
        container.addItem();
        Object id2 = container.addItem();
        Assert.assertTrue(container.isLastId(id2));
    }

    @Test
    public void getIdByIndex_tableOneItemAddedLastIndexInContainer_returnsAddedItem() throws SQLException {
        Object id = container.addItem();
        Assert.assertEquals(id, container.getIdByIndex(((container.size()) - 1)));
    }

    @Test
    public void removeItem_tableNoAddedItems_removesItemFromContainer() throws SQLException {
        int originalSize = container.size();
        Object id = container.firstItemId();
        Assert.assertTrue(container.removeItem(id));
        Assert.assertNotSame(id, container.firstItemId());
        Assert.assertEquals((originalSize - 1), container.size());
    }

    @Test
    public void containsId_tableRemovedItem_returnsFalse() throws SQLException {
        Object id = container.firstItemId();
        Assert.assertTrue(container.removeItem(id));
        Assert.assertFalse(container.containsId(id));
    }

    @Test
    public void removeItem_tableOneAddedItem_removesTheAddedItem() throws SQLException {
        Object id = container.addItem();
        int size = container.size();
        Assert.assertTrue(container.removeItem(id));
        Assert.assertFalse(container.containsId(id));
        Assert.assertEquals((size - 1), container.size());
    }

    @Test
    public void getItem_tableItemRemoved_returnsNull() throws SQLException {
        Object id = container.firstItemId();
        Assert.assertTrue(container.removeItem(id));
        Assert.assertNull(container.getItem(id));
    }

    @Test
    public void getItem_tableAddedItemRemoved_returnsNull() throws SQLException {
        Object id = container.addItem();
        Assert.assertNotNull(container.getItem(id));
        Assert.assertTrue(container.removeItem(id));
        Assert.assertNull(container.getItem(id));
    }

    @Test
    public void getItemIds_tableItemRemoved_shouldNotContainRemovedItem() throws SQLException {
        Object id = container.firstItemId();
        Assert.assertTrue(container.getItemIds().contains(id));
        Assert.assertTrue(container.removeItem(id));
        Assert.assertFalse(container.getItemIds().contains(id));
    }

    @Test
    public void getItemIds_tableAddedItemRemoved_shouldNotContainRemovedItem() throws SQLException {
        Object id = container.addItem();
        Assert.assertTrue(container.getItemIds().contains(id));
        Assert.assertTrue(container.removeItem(id));
        Assert.assertFalse(container.getItemIds().contains(id));
    }

    @Test
    public void containsId_tableItemRemoved_returnsFalse() throws SQLException {
        Object id = container.firstItemId();
        Assert.assertTrue(container.containsId(id));
        Assert.assertTrue(container.removeItem(id));
        Assert.assertFalse(container.containsId(id));
    }

    @Test
    public void containsId_tableAddedItemRemoved_returnsFalse() throws SQLException {
        Object id = container.addItem();
        Assert.assertTrue(container.containsId(id));
        Assert.assertTrue(container.removeItem(id));
        Assert.assertFalse(container.containsId(id));
    }

    @Test
    public void nextItemId_tableItemRemoved_skipsRemovedItem() throws SQLException {
        Object first = container.getIdByIndex(0);
        Object second = container.getIdByIndex(1);
        Object third = container.getIdByIndex(2);
        Assert.assertTrue(container.removeItem(second));
        Assert.assertEquals(third, container.nextItemId(first));
    }

    @Test
    public void nextItemId_tableAddedItemRemoved_skipsRemovedItem() throws SQLException {
        Object first = container.lastItemId();
        Object second = container.addItem();
        Object third = container.addItem();
        Assert.assertTrue(container.removeItem(second));
        Assert.assertEquals(third, container.nextItemId(first));
    }

    @Test
    public void prevItemId_tableItemRemoved_skipsRemovedItem() throws SQLException {
        Object first = container.getIdByIndex(0);
        Object second = container.getIdByIndex(1);
        Object third = container.getIdByIndex(2);
        Assert.assertTrue(container.removeItem(second));
        Assert.assertEquals(first, container.prevItemId(third));
    }

    @Test
    public void prevItemId_tableAddedItemRemoved_skipsRemovedItem() throws SQLException {
        Object first = container.lastItemId();
        Object second = container.addItem();
        Object third = container.addItem();
        Assert.assertTrue(container.removeItem(second));
        Assert.assertEquals(first, container.prevItemId(third));
    }

    @Test
    public void firstItemId_tableFirstItemRemoved_resultChanges() throws SQLException {
        Object first = container.firstItemId();
        Assert.assertTrue(container.removeItem(first));
        Assert.assertNotSame(first, container.firstItemId());
    }

    @Test
    public void firstItemId_tableNewlyAddedFirstItemRemoved_resultChanges() throws SQLException {
        SQLContainer garbageContainer = getGarbageContainer();
        Object first = garbageContainer.addItem();
        Object second = garbageContainer.addItem();
        Assert.assertSame(first, garbageContainer.firstItemId());
        Assert.assertTrue(garbageContainer.removeItem(first));
        Assert.assertSame(second, garbageContainer.firstItemId());
    }

    @Test
    public void lastItemId_tableLastItemRemoved_resultChanges() throws SQLException {
        Object last = container.lastItemId();
        Assert.assertTrue(container.removeItem(last));
        Assert.assertNotSame(last, container.lastItemId());
    }

    @Test
    public void lastItemId_tableAddedLastItemRemoved_resultChanges() throws SQLException {
        Object last = container.addItem();
        Assert.assertSame(last, container.lastItemId());
        Assert.assertTrue(container.removeItem(last));
        Assert.assertNotSame(last, container.lastItemId());
    }

    @Test
    public void isFirstId_tableFirstItemRemoved_returnsFalse() throws SQLException {
        Object first = container.firstItemId();
        Assert.assertTrue(container.removeItem(first));
        Assert.assertFalse(container.isFirstId(first));
    }

    @Test
    public void isFirstId_tableAddedFirstItemRemoved_returnsFalse() throws SQLException {
        SQLContainer garbageContainer = getGarbageContainer();
        Object first = garbageContainer.addItem();
        garbageContainer.addItem();
        Assert.assertSame(first, garbageContainer.firstItemId());
        Assert.assertTrue(garbageContainer.removeItem(first));
        Assert.assertFalse(garbageContainer.isFirstId(first));
    }

    @Test
    public void isLastId_tableLastItemRemoved_returnsFalse() throws SQLException {
        Object last = container.lastItemId();
        Assert.assertTrue(container.removeItem(last));
        Assert.assertFalse(container.isLastId(last));
    }

    @Test
    public void isLastId_tableAddedLastItemRemoved_returnsFalse() throws SQLException {
        Object last = container.addItem();
        Assert.assertSame(last, container.lastItemId());
        Assert.assertTrue(container.removeItem(last));
        Assert.assertFalse(container.isLastId(last));
    }

    @Test
    public void indexOfId_tableItemRemoved_returnsNegOne() throws SQLException {
        Object id = container.getIdByIndex(2);
        Assert.assertTrue(container.removeItem(id));
        Assert.assertEquals((-1), container.indexOfId(id));
    }

    @Test
    public void indexOfId_tableAddedItemRemoved_returnsNegOne() throws SQLException {
        Object id = container.addItem();
        Assert.assertTrue(((container.indexOfId(id)) != (-1)));
        Assert.assertTrue(container.removeItem(id));
        Assert.assertEquals((-1), container.indexOfId(id));
    }

    @Test
    public void getIdByIndex_tableItemRemoved_resultChanges() throws SQLException {
        Object id = container.getIdByIndex(2);
        Assert.assertTrue(container.removeItem(id));
        Assert.assertNotSame(id, container.getIdByIndex(2));
    }

    @Test
    public void getIdByIndex_tableAddedItemRemoved_resultChanges() throws SQLException {
        Object id = container.addItem();
        container.addItem();
        int index = container.indexOfId(id);
        Assert.assertTrue(container.removeItem(id));
        Assert.assertNotSame(id, container.getIdByIndex(index));
    }

    @Test
    public void removeAllItems_table_shouldSucceed() throws SQLException {
        Assert.assertTrue(container.removeAllItems());
        Assert.assertEquals(0, container.size());
    }

    @Test
    public void removeAllItems_tableAddedItems_shouldSucceed() throws SQLException {
        container.addItem();
        container.addItem();
        Assert.assertTrue(container.removeAllItems());
        Assert.assertEquals(0, container.size());
    }

    // Set timeout to ensure there is no infinite looping (#12882)
    @Test(timeout = 1000)
    public void removeAllItems_manyItems_commit_shouldSucceed() throws SQLException {
        final int itemNumber = (((CACHE_RATIO) + 1) * (DEFAULT_PAGE_LENGTH)) + 1;
        container.removeAllItems();
        Assert.assertEquals(container.size(), 0);
        for (int i = 0; i < itemNumber; ++i) {
            container.addItem();
        }
        container.commit();
        Assert.assertEquals(container.size(), itemNumber);
        Assert.assertTrue(container.removeAllItems());
        container.commit();
        Assert.assertEquals(container.size(), 0);
    }

    @Test
    public void commit_tableAddedItem_shouldBeWrittenToDB() throws SQLException {
        Object id = container.addItem();
        container.getContainerProperty(id, NAME).setValue("New Name");
        Assert.assertTrue((id instanceof TemporaryRowId));
        Assert.assertSame(id, container.lastItemId());
        container.commit();
        Assert.assertFalse(((container.lastItemId()) instanceof TemporaryRowId));
        Assert.assertEquals("New Name", container.getContainerProperty(container.lastItemId(), NAME).getValue());
    }

    @Test
    public void commit_tableTwoAddedItems_shouldBeWrittenToDB() throws SQLException {
        Object id = container.addItem();
        Object id2 = container.addItem();
        container.getContainerProperty(id, NAME).setValue("Herbert");
        container.getContainerProperty(id2, NAME).setValue("Larry");
        Assert.assertTrue((id2 instanceof TemporaryRowId));
        Assert.assertSame(id2, container.lastItemId());
        container.commit();
        Object nextToLast = container.getIdByIndex(((container.size()) - 2));
        Assert.assertFalse((nextToLast instanceof TemporaryRowId));
        Assert.assertEquals("Herbert", container.getContainerProperty(nextToLast, NAME).getValue());
        Assert.assertFalse(((container.lastItemId()) instanceof TemporaryRowId));
        Assert.assertEquals("Larry", container.getContainerProperty(container.lastItemId(), NAME).getValue());
    }

    @Test
    public void commit_tableRemovedItem_shouldBeRemovedFromDB() throws SQLException {
        Object last = container.lastItemId();
        container.removeItem(last);
        container.commit();
        Assert.assertFalse(last.equals(container.lastItemId()));
    }

    @Test
    public void commit_tableLastItemUpdated_shouldUpdateRowInDB() throws SQLException {
        Object last = container.lastItemId();
        container.getContainerProperty(last, NAME).setValue("Donald");
        container.commit();
        Assert.assertEquals("Donald", container.getContainerProperty(container.lastItemId(), NAME).getValue());
    }

    @Test
    public void commit_removeModifiedItem_shouldSucceed() throws SQLException {
        int size = container.size();
        Object key = container.firstItemId();
        Item row = container.getItem(key);
        row.getItemProperty(NAME).setValue("Pekka");
        Assert.assertTrue(container.removeItem(key));
        container.commit();
        Assert.assertEquals((size - 1), container.size());
    }

    @Test
    public void rollback_tableItemAdded_discardsAddedItem() throws SQLException {
        int size = container.size();
        Object id = container.addItem();
        container.getContainerProperty(id, NAME).setValue("foo");
        Assert.assertEquals((size + 1), container.size());
        container.rollback();
        Assert.assertEquals(size, container.size());
        Assert.assertFalse("foo".equals(container.getContainerProperty(container.lastItemId(), NAME).getValue()));
    }

    @Test
    public void rollback_tableItemRemoved_restoresRemovedItem() throws SQLException {
        int size = container.size();
        Object last = container.lastItemId();
        container.removeItem(last);
        Assert.assertEquals((size - 1), container.size());
        container.rollback();
        Assert.assertEquals(size, container.size());
        Assert.assertEquals(last, container.lastItemId());
    }

    @Test
    public void rollback_tableItemChanged_discardsChanges() throws SQLException {
        Object last = container.lastItemId();
        container.getContainerProperty(last, NAME).setValue("foo");
        container.rollback();
        Assert.assertFalse("foo".equals(container.getContainerProperty(container.lastItemId(), NAME).getValue()));
    }

    @Test
    public void itemChangeNotification_table_isModifiedReturnsTrue() throws SQLException {
        Assert.assertFalse(container.isModified());
        RowItem last = ((RowItem) (container.getItem(container.lastItemId())));
        container.itemChangeNotification(last);
        Assert.assertTrue(container.isModified());
    }

    @Test
    public void itemSetChangeListeners_table_shouldFire() throws SQLException {
        ItemSetChangeListener listener = EasyMock.createMock(ItemSetChangeListener.class);
        listener.containerItemSetChange(EasyMock.isA(ItemSetChangeEvent.class));
        EasyMock.replay(listener);
        container.addListener(listener);
        container.addItem();
        EasyMock.verify(listener);
    }

    @Test
    public void itemSetChangeListeners_tableItemRemoved_shouldFire() throws SQLException {
        ItemSetChangeListener listener = EasyMock.createMock(ItemSetChangeListener.class);
        listener.containerItemSetChange(EasyMock.isA(ItemSetChangeEvent.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(listener);
        container.addListener(listener);
        container.removeItem(container.lastItemId());
        EasyMock.verify(listener);
    }

    @Test
    public void removeListener_table_shouldNotFire() throws SQLException {
        ItemSetChangeListener listener = EasyMock.createMock(ItemSetChangeListener.class);
        EasyMock.replay(listener);
        container.addListener(listener);
        container.removeListener(listener);
        container.addItem();
        EasyMock.verify(listener);
    }

    @Test
    public void isModified_tableRemovedItem_returnsTrue() throws SQLException {
        Assert.assertFalse(container.isModified());
        container.removeItem(container.lastItemId());
        Assert.assertTrue(container.isModified());
    }

    @Test
    public void isModified_tableAddedItem_returnsTrue() throws SQLException {
        Assert.assertFalse(container.isModified());
        container.addItem();
        Assert.assertTrue(container.isModified());
    }

    @Test
    public void isModified_tableChangedItem_returnsTrue() throws SQLException {
        Assert.assertFalse(container.isModified());
        container.getContainerProperty(container.lastItemId(), NAME).setValue("foo");
        Assert.assertTrue(container.isModified());
    }

    @Test
    public void getSortableContainerPropertyIds_table_returnsAllPropertyIds() throws SQLException {
        Collection<?> sortableIds = container.getSortableContainerPropertyIds();
        Assert.assertTrue(sortableIds.contains(ID));
        Assert.assertTrue(sortableIds.contains(NAME));
        Assert.assertTrue(sortableIds.contains("AGE"));
        Assert.assertEquals(3, sortableIds.size());
        if (((SQLTestsConstants.db) == (MSSQL)) || ((SQLTestsConstants.db) == (ORACLE))) {
            Assert.assertFalse(sortableIds.contains("rownum"));
        }
    }

    @Test
    public void addOrderBy_table_shouldReorderResults() throws SQLException {
        // Ville, Kalle, Pelle, B?rje
        Assert.assertEquals("Ville", container.getContainerProperty(container.firstItemId(), NAME).getValue());
        Assert.assertEquals("B?rje", container.getContainerProperty(container.lastItemId(), NAME).getValue());
        container.addOrderBy(new OrderBy(NAME, true));
        // B?rje, Kalle, Pelle, Ville
        Assert.assertEquals("B?rje", container.getContainerProperty(container.firstItemId(), NAME).getValue());
        Assert.assertEquals("Ville", container.getContainerProperty(container.lastItemId(), NAME).getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrderBy_tableIllegalColumn_shouldFail() throws SQLException {
        container.addOrderBy(new OrderBy("asdf", true));
    }

    @Test
    public void sort_table_sortsByName() throws SQLException {
        // Ville, Kalle, Pelle, B?rje
        Assert.assertEquals("Ville", container.getContainerProperty(container.firstItemId(), NAME).getValue());
        Assert.assertEquals("B?rje", container.getContainerProperty(container.lastItemId(), NAME).getValue());
        container.sort(new Object[]{ NAME }, new boolean[]{ true });
        // B?rje, Kalle, Pelle, Ville
        Assert.assertEquals("B?rje", container.getContainerProperty(container.firstItemId(), NAME).getValue());
        Assert.assertEquals("Ville", container.getContainerProperty(container.lastItemId(), NAME).getValue());
    }

    @Test
    public void addFilter_table_filtersResults() throws SQLException {
        // Ville, Kalle, Pelle, B?rje
        Assert.assertEquals(4, container.size());
        Assert.assertEquals("B?rje", container.getContainerProperty(container.lastItemId(), NAME).getValue());
        container.addContainerFilter(new Like(NAME, "%lle"));
        // Ville, Kalle, Pelle
        Assert.assertEquals(3, container.size());
        Assert.assertEquals("Pelle", container.getContainerProperty(container.lastItemId(), NAME).getValue());
    }

    @Test
    public void addContainerFilter_filtersResults() throws SQLException {
        // Ville, Kalle, Pelle, B?rje
        Assert.assertEquals(4, container.size());
        container.addContainerFilter(NAME, "Vi", false, false);
        // Ville
        Assert.assertEquals(1, container.size());
        Assert.assertEquals("Ville", container.getContainerProperty(container.lastItemId(), NAME).getValue());
    }

    @Test
    public void addContainerFilter_ignoreCase_filtersResults() throws SQLException {
        // Ville, Kalle, Pelle, B?rje
        Assert.assertEquals(4, container.size());
        container.addContainerFilter(NAME, "vi", true, false);
        // Ville
        Assert.assertEquals(1, container.size());
        Assert.assertEquals("Ville", container.getContainerProperty(container.lastItemId(), NAME).getValue());
    }

    @Test
    public void removeAllContainerFilters_table_noFiltering() throws SQLException {
        // Ville, Kalle, Pelle, B?rje
        Assert.assertEquals(4, container.size());
        container.addContainerFilter(NAME, "Vi", false, false);
        // Ville
        Assert.assertEquals(1, container.size());
        Assert.assertEquals("Ville", container.getContainerProperty(container.lastItemId(), NAME).getValue());
        container.removeAllContainerFilters();
        Assert.assertEquals(4, container.size());
        Assert.assertEquals("B?rje", container.getContainerProperty(container.lastItemId(), NAME).getValue());
    }

    @Test
    public void removeContainerFilters_table_noFiltering() throws SQLException {
        // Ville, Kalle, Pelle, B?rje
        Assert.assertEquals(4, container.size());
        container.addContainerFilter(NAME, "Vi", false, false);
        // Ville
        Assert.assertEquals(1, container.size());
        Assert.assertEquals("Ville", container.getContainerProperty(container.lastItemId(), NAME).getValue());
        container.removeContainerFilters(NAME);
        Assert.assertEquals(4, container.size());
        Assert.assertEquals("B?rje", container.getContainerProperty(container.lastItemId(), NAME).getValue());
    }

    @Test
    public void addFilter_tableBufferedItems_alsoFiltersBufferedItems() throws SQLException {
        // Ville, Kalle, Pelle, B?rje
        Assert.assertEquals(4, container.size());
        Assert.assertEquals("B?rje", container.getContainerProperty(container.lastItemId(), NAME).getValue());
        Object id1 = container.addItem();
        container.getContainerProperty(id1, NAME).setValue("Palle");
        Object id2 = container.addItem();
        container.getContainerProperty(id2, NAME).setValue("Bengt");
        container.addContainerFilter(new Like(NAME, "%lle"));
        // Ville, Kalle, Pelle, Palle
        Assert.assertEquals(4, container.size());
        Assert.assertEquals("Ville", container.getContainerProperty(container.getIdByIndex(0), NAME).getValue());
        Assert.assertEquals("Kalle", container.getContainerProperty(container.getIdByIndex(1), NAME).getValue());
        Assert.assertEquals("Pelle", container.getContainerProperty(container.getIdByIndex(2), NAME).getValue());
        Assert.assertEquals("Palle", container.getContainerProperty(container.getIdByIndex(3), NAME).getValue());
        try {
            container.getIdByIndex(4);
            Assert.fail("SQLContainer.getIdByIndex() returned a value for an index beyond the end of the container");
        } catch (IndexOutOfBoundsException e) {
            // should throw exception - item is filtered out
        }
        Assert.assertNull(container.nextItemId(container.getIdByIndex(3)));
        Assert.assertFalse(container.containsId(id2));
        Assert.assertFalse(container.getItemIds().contains(id2));
        Assert.assertNull(container.getItem(id2));
        Assert.assertEquals((-1), container.indexOfId(id2));
        Assert.assertNotSame(id2, container.lastItemId());
        Assert.assertSame(id1, container.lastItemId());
    }

    @Test
    public void sort_tableBufferedItems_sortsBufferedItemsLastInOrderAdded() throws SQLException {
        // Ville, Kalle, Pelle, B?rje
        Assert.assertEquals("Ville", container.getContainerProperty(container.firstItemId(), NAME).getValue());
        Assert.assertEquals("B?rje", container.getContainerProperty(container.lastItemId(), NAME).getValue());
        Object id1 = container.addItem();
        container.getContainerProperty(id1, NAME).setValue("Wilbert");
        Object id2 = container.addItem();
        container.getContainerProperty(id2, NAME).setValue("Albert");
        container.sort(new Object[]{ NAME }, new boolean[]{ true });
        // B?rje, Kalle, Pelle, Ville, Wilbert, Albert
        Assert.assertEquals("B?rje", container.getContainerProperty(container.firstItemId(), NAME).getValue());
        Assert.assertEquals("Wilbert", container.getContainerProperty(container.getIdByIndex(((container.size()) - 2)), NAME).getValue());
        Assert.assertEquals("Albert", container.getContainerProperty(container.lastItemId(), NAME).getValue());
    }
}

