/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.persistence.dynamodb.internal;


import Operator.EQ;
import Operator.GT;
import Operator.GTE;
import Operator.LT;
import Operator.LTE;
import Operator.NEQ;
import Ordering.ASCENDING;
import Ordering.DESCENDING;
import java.util.Date;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.openhab.core.persistence.FilterCriteria;
import org.openhab.core.persistence.HistoricItem;


/**
 * This is abstract class helping with integration testing the persistence service. Different kind of queries are tested
 * against actual dynamo db database.
 *
 *
 * Inheritor of this base class needs to store two states of one item in a static method annotated with @BeforeClass.
 * This
 * static
 * class should update the private static fields
 * beforeStore (date before storing anything), afterStore1 (after storing first item, but before storing second item),
 * afterStore2 (after storing second item). The item name must correspond to getItemName. The first state needs to be
 * smaller than the second state.
 *
 * To have more comprehensive tests, the inheritor class can define getQueryItemStateBetween to provide a value between
 * the two states. Null can be used to omit the additional tests.
 *
 *
 * See DimmerItemIntegrationTest for example how to use this base class.
 *
 * @author Sami Salonen
 */
public abstract class AbstractTwoItemIntegrationTest extends BaseIntegrationTest {
    protected static Date beforeStore;

    protected static Date afterStore1;

    protected static Date afterStore2;

    @Test
    public void testQueryUsingName() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOrdering(ASCENDING);
        criteria.setItemName(getItemName());
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        assertIterableContainsItems(iterable, true);
    }

    @Test
    public void testQueryUsingNameAndStart() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOrdering(ASCENDING);
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        assertIterableContainsItems(iterable, true);
    }

    @Test
    public void testQueryUsingNameAndStartNoMatch() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Assert.assertFalse(iterable.iterator().hasNext());
    }

    @Test
    public void testQueryUsingNameAndEnd() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOrdering(ASCENDING);
        criteria.setItemName(getItemName());
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        assertIterableContainsItems(iterable, true);
    }

    @Test
    public void testQueryUsingNameAndEndNoMatch() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setItemName(getItemName());
        criteria.setEndDate(AbstractTwoItemIntegrationTest.beforeStore);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Assert.assertFalse(iterable.iterator().hasNext());
    }

    @Test
    public void testQueryUsingNameAndStartAndEnd() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOrdering(ASCENDING);
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        assertIterableContainsItems(iterable, true);
    }

    @Test
    public void testQueryUsingNameAndStartAndEndDesc() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOrdering(DESCENDING);
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        assertIterableContainsItems(iterable, false);
    }

    @Test
    public void testQueryUsingNameAndStartAndEndWithNEQOperator() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOperator(NEQ);
        criteria.setState(getSecondItemState());
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Iterator<HistoricItem> iterator = iterable.iterator();
        HistoricItem actual1 = iterator.next();
        Assert.assertFalse(iterator.hasNext());
        assertStateEquals(getFirstItemState(), actual1.getState());
        Assert.assertTrue(actual1.getTimestamp().before(AbstractTwoItemIntegrationTest.afterStore1));
        Assert.assertTrue(actual1.getTimestamp().after(AbstractTwoItemIntegrationTest.beforeStore));
    }

    @Test
    public void testQueryUsingNameAndStartAndEndWithEQOperator() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOperator(EQ);
        criteria.setState(getFirstItemState());
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Iterator<HistoricItem> iterator = iterable.iterator();
        HistoricItem actual1 = iterator.next();
        Assert.assertFalse(iterator.hasNext());
        assertStateEquals(getFirstItemState(), actual1.getState());
        Assert.assertTrue(actual1.getTimestamp().before(AbstractTwoItemIntegrationTest.afterStore1));
        Assert.assertTrue(actual1.getTimestamp().after(AbstractTwoItemIntegrationTest.beforeStore));
    }

    @Test
    public void testQueryUsingNameAndStartAndEndWithLTOperator() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOperator(LT);
        criteria.setState(getSecondItemState());
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Iterator<HistoricItem> iterator = iterable.iterator();
        HistoricItem actual1 = iterator.next();
        Assert.assertFalse(iterator.hasNext());
        assertStateEquals(getFirstItemState(), actual1.getState());
        Assert.assertTrue(actual1.getTimestamp().before(AbstractTwoItemIntegrationTest.afterStore1));
        Assert.assertTrue(actual1.getTimestamp().after(AbstractTwoItemIntegrationTest.beforeStore));
    }

    @Test
    public void testQueryUsingNameAndStartAndEndWithLTOperatorNoMatch() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOperator(LT);
        criteria.setState(getFirstItemState());
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Iterator<HistoricItem> iterator = iterable.iterator();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testQueryUsingNameAndStartAndEndWithLTEOperator() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOperator(LTE);
        criteria.setState(getFirstItemState());
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Iterator<HistoricItem> iterator = iterable.iterator();
        HistoricItem actual1 = iterator.next();
        Assert.assertFalse(iterator.hasNext());
        assertStateEquals(getFirstItemState(), actual1.getState());
        Assert.assertTrue(actual1.getTimestamp().before(AbstractTwoItemIntegrationTest.afterStore1));
        Assert.assertTrue(actual1.getTimestamp().after(AbstractTwoItemIntegrationTest.beforeStore));
    }

    @Test
    public void testQueryUsingNameAndStartAndEndWithGTOperator() {
        // Skip for subclasses which have null "state between"
        Assume.assumeTrue(((getQueryItemStateBetween()) != null));
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOperator(GT);
        criteria.setState(getQueryItemStateBetween());
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Iterator<HistoricItem> iterator = iterable.iterator();
        HistoricItem actual1 = iterator.next();
        Assert.assertFalse(iterator.hasNext());
        assertStateEquals(getSecondItemState(), actual1.getState());
        Assert.assertTrue(actual1.getTimestamp().before(AbstractTwoItemIntegrationTest.afterStore2));
        Assert.assertTrue(actual1.getTimestamp().after(AbstractTwoItemIntegrationTest.afterStore1));
    }

    @Test
    public void testQueryUsingNameAndStartAndEndWithGTOperatorNoMatch() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOperator(GT);
        criteria.setState(getSecondItemState());
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Iterator<HistoricItem> iterator = iterable.iterator();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testQueryUsingNameAndStartAndEndWithGTEOperator() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOperator(GTE);
        criteria.setState(getSecondItemState());
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore2);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Iterator<HistoricItem> iterator = iterable.iterator();
        HistoricItem actual1 = iterator.next();
        Assert.assertFalse(iterator.hasNext());
        assertStateEquals(getSecondItemState(), actual1.getState());
        Assert.assertTrue(actual1.getTimestamp().before(AbstractTwoItemIntegrationTest.afterStore2));
        Assert.assertTrue(actual1.getTimestamp().after(AbstractTwoItemIntegrationTest.afterStore1));
    }

    @Test
    public void testQueryUsingNameAndStartAndEndFirst() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setOrdering(ASCENDING);
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.afterStore1);
        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Iterator<HistoricItem> iterator = iterable.iterator();
        HistoricItem actual1 = iterator.next();
        Assert.assertFalse(iterator.hasNext());
        assertStateEquals(getFirstItemState(), actual1.getState());
        Assert.assertTrue(actual1.getTimestamp().before(AbstractTwoItemIntegrationTest.afterStore1));
        Assert.assertTrue(actual1.getTimestamp().after(AbstractTwoItemIntegrationTest.beforeStore));
    }

    @Test
    public void testQueryUsingNameAndStartAndEndNoMatch() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setItemName(getItemName());
        criteria.setBeginDate(AbstractTwoItemIntegrationTest.beforeStore);
        criteria.setEndDate(AbstractTwoItemIntegrationTest.beforeStore);// sic

        Iterable<HistoricItem> iterable = BaseIntegrationTest.service.query(criteria);
        Assert.assertFalse(iterable.iterator().hasNext());
    }
}

