package com.robinhood.ticker;


import TickerUtils.EMPTY_CHAR;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


public class TickerColumnManagerTest {
    @Mock
    TickerDrawMetrics metrics;

    private TickerColumnManager tickerColumnManager;

    @Test
    public void test_setText_animate() {
        Assert.assertEquals(0, numberOfTickerColumns());
        tickerColumnManager.setText("1234".toCharArray());
        tickerColumnManager.setAnimationProgress(1.0F);
        Assert.assertEquals(4, numberOfTickerColumns());
        Assert.assertEquals('1', tickerColumnAtIndex(0).getTargetChar());
        Assert.assertEquals('2', tickerColumnAtIndex(1).getTargetChar());
        Assert.assertEquals('3', tickerColumnAtIndex(2).getTargetChar());
        Assert.assertEquals('4', tickerColumnAtIndex(3).getTargetChar());
        tickerColumnManager.setText("999".toCharArray());
        Assert.assertEquals(4, numberOfTickerColumns());
        Assert.assertEquals(EMPTY_CHAR, tickerColumnAtIndex(0).getTargetChar());
        Assert.assertEquals('9', tickerColumnAtIndex(1).getTargetChar());
        Assert.assertEquals('9', tickerColumnAtIndex(2).getTargetChar());
        Assert.assertEquals('9', tickerColumnAtIndex(3).getTargetChar());
        tickerColumnManager.setAnimationProgress(1.0F);
        tickerColumnManager.setText("899".toCharArray());
        Assert.assertEquals(3, numberOfTickerColumns());
        Assert.assertEquals('8', tickerColumnAtIndex(0).getTargetChar());
        Assert.assertEquals('9', tickerColumnAtIndex(1).getTargetChar());
        Assert.assertEquals('9', tickerColumnAtIndex(2).getTargetChar());
    }

    @Test
    public void test_setText_noAnimate() {
        Assert.assertEquals(0, numberOfTickerColumns());
        tickerColumnManager.setText("1234".toCharArray());
        Assert.assertEquals(4, numberOfTickerColumns());
        Assert.assertEquals('1', tickerColumnAtIndex(0).getTargetChar());
        Assert.assertEquals('2', tickerColumnAtIndex(1).getTargetChar());
        Assert.assertEquals('3', tickerColumnAtIndex(2).getTargetChar());
        Assert.assertEquals('4', tickerColumnAtIndex(3).getTargetChar());
        tickerColumnManager.setText("999".toCharArray());
        Assert.assertEquals(3, numberOfTickerColumns());
        Assert.assertEquals('9', tickerColumnAtIndex(0).getTargetChar());
        Assert.assertEquals('9', tickerColumnAtIndex(1).getTargetChar());
        Assert.assertEquals('9', tickerColumnAtIndex(2).getTargetChar());
    }
}

