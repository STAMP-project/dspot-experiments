package com.beloo.widget.chipslayoutmanager.layouter;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class DecrementalPositionIteratorTest extends AbstractPositionIteratorTest {
    @Test(expected = IllegalStateException.class)
    public void callingNextWhenNegativePositionReachedShouldThrowException() {
        AbstractPositionIterator iterator = providePositionIterator(5);
        Assert.assertTrue(((iterator.next()) == 0));
        iterator.next();
    }

    @Test
    public void nextShouldDecreaseResultPosition() {
        AbstractPositionIterator iterator = providePositionIterator(5);
        iterator.move(3);
        Assert.assertTrue(((iterator.next()) == 3));
        Assert.assertTrue(((iterator.next()) == 2));
    }

    @Test
    public void hasNextShouldReturnTrueIfZeroPositionIsNotPrevious() {
        AbstractPositionIterator iterator = providePositionIterator(2);
        iterator.move(1);
        Assert.assertTrue(iterator.hasNext());
        iterator.next();
        Assert.assertTrue(iterator.hasNext());
        iterator.next();
        Assert.assertFalse(iterator.hasNext());
    }
}

