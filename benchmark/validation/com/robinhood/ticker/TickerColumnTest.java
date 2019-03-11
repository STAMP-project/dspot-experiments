package com.robinhood.ticker;


import TickerUtils.EMPTY_CHAR;
import android.graphics.Canvas;
import android.text.TextPaint;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TickerColumnTest {
    private static final float CHAR_HEIGHT = 5.0F;

    private static final float DEFAULT_CHAR_WIDTH = 10.0F;

    private static TickerCharacterList characterList = new TickerCharacterList("01234");

    @Mock
    TickerDrawMetrics metrics;

    @Mock
    Canvas canvas;

    @Mock
    TextPaint paint;

    private TickerColumn tickerColumn;

    @Test
    public void test_draw_differentWidth() {
        // Going from empty to not empty
        tickerColumn.setTargetChar('0');
        Assert.assertEquals(((int) (TickerColumnTest.DEFAULT_CHAR_WIDTH)), ((int) (tickerColumn.getMinimumRequiredWidth())));
        Assert.assertEquals(0, ((int) (tickerColumn.getCurrentWidth())));
        setProgress(0.4F);
        Assert.assertEquals(((int) (TickerColumnTest.DEFAULT_CHAR_WIDTH)), ((int) (tickerColumn.getMinimumRequiredWidth())));
        Assert.assertEquals(4, ((int) (tickerColumn.getCurrentWidth())));
        setProgress(1.0F);
        Assert.assertEquals(((int) (TickerColumnTest.DEFAULT_CHAR_WIDTH)), ((int) (tickerColumn.getMinimumRequiredWidth())));
        Assert.assertEquals(((int) (TickerColumnTest.DEFAULT_CHAR_WIDTH)), ((int) (tickerColumn.getCurrentWidth())));
        // Going from not empty to not empty
        tickerColumn.setTargetChar('1');
        Assert.assertEquals(((int) (TickerColumnTest.DEFAULT_CHAR_WIDTH)), ((int) (tickerColumn.getMinimumRequiredWidth())));
        Assert.assertEquals(((int) (TickerColumnTest.DEFAULT_CHAR_WIDTH)), ((int) (tickerColumn.getCurrentWidth())));
        setProgress(0.4F);
        Assert.assertEquals(((int) (TickerColumnTest.DEFAULT_CHAR_WIDTH)), ((int) (tickerColumn.getMinimumRequiredWidth())));
        Assert.assertEquals(((int) (TickerColumnTest.DEFAULT_CHAR_WIDTH)), ((int) (tickerColumn.getCurrentWidth())));
        setProgress(1.0F);
        Assert.assertEquals(((int) (TickerColumnTest.DEFAULT_CHAR_WIDTH)), ((int) (tickerColumn.getMinimumRequiredWidth())));
        Assert.assertEquals(((int) (TickerColumnTest.DEFAULT_CHAR_WIDTH)), ((int) (tickerColumn.getCurrentWidth())));
    }

    @Test
    public void test_draw_noAnimation() {
        tickerColumn.setTargetChar('0');
        setProgress(1.0F);
        Assert.assertEquals('0', tickerColumn.getTargetChar());
        verifyDraw(1, 0.0F);
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_noAnimation_edge1() {
        tickerColumn.setTargetChar(EMPTY_CHAR);
        setProgress(1.0F);
        verifyDraw(0, 0.0F);
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_noAnimation_edge2() {
        tickerColumn.setTargetChar('2');
        setProgress(1.0F);
        verifyDraw(3, 0.0F);
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_startAnimation() {
        tickerColumn.setTargetChar('1');
        setProgress(0.0F);
        verifyDraw(0, 0.0F);
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_duringAnimation1() {
        tickerColumn.setTargetChar('1');
        setProgress(0.5F);
        verifyDraw(1, 0.0F);
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_duringAnimation2() {
        tickerColumn.setTargetChar('1');
        setProgress(0.75F);
        // We should be half way between '0' and '1'.
        verifyDraw(1, ((TickerColumnTest.CHAR_HEIGHT) / 2));
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_interruptedStartAnimation_startAnimation() {
        tickerColumn.setTargetChar('1');
        setProgress(0.0F);
        tickerColumn.setTargetChar('2');
        setProgress(0.0F);
        verifyDraw(0, 0.0F, 2);
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_interruptedStartAnimation_midAnimation() {
        tickerColumn.setTargetChar('1');
        setProgress(0.0F);
        verifyDraw(0, 0.0F);
        tickerColumn.setTargetChar('2');
        setProgress(0.25F);
        // We should be 3 quarters way between EMPTY_CHAR and '0'.
        verifyDraw(0, (((TickerColumnTest.CHAR_HEIGHT) / 4) * 3));
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_interruptedStartAnimation_endAnimation() {
        tickerColumn.setTargetChar('1');
        setProgress(0.0F);
        verifyDraw(0, 0.0F);
        tickerColumn.setTargetChar('0');
        setProgress(1.0F);
        verifyDraw(1, 0.0F);
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_interruptedMidAnimation_startAnimation() {
        tickerColumn.setTargetChar('1');
        setProgress(0.75F);
        tickerColumn.setTargetChar(EMPTY_CHAR);
        setProgress(0.0F);
        // We should still be half way between '0' and '1' since the new animation just started.
        verifyDraw(1, ((TickerColumnTest.CHAR_HEIGHT) / 2), 2);
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_interruptedMidAnimation_midAnimation() {
        tickerColumn.setTargetChar('1');
        setProgress(0.75F);
        // We should be half way between '0' and '1'.
        verifyDraw(1, ((TickerColumnTest.CHAR_HEIGHT) / 2));
        tickerColumn.setTargetChar('0');
        setProgress(0.5F);
        // We are now quarter way between '0' and '1'.
        verifyDraw(1, ((TickerColumnTest.CHAR_HEIGHT) / 4));
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_interruptedMidAnimation_endAnimation() {
        tickerColumn.setTargetChar('1');
        setProgress(0.75F);
        // We should be half way between '0' and '1'.
        verifyDraw(1, ((TickerColumnTest.CHAR_HEIGHT) / 2));
        tickerColumn.setTargetChar('0');
        setProgress(1.0F);
        verifyDraw(1, 0.0F);
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_mockConsecutiveAnimations() {
        // Simulates how the ticker column would normally get callbacks
        tickerColumn.setTargetChar('0');
        setProgress(0.0F);
        verifyDraw(0, 0.0F);
        setProgress(0.25F);
        verifyDraw(0, ((TickerColumnTest.CHAR_HEIGHT) / 4));
        setProgress(0.75F);
        verifyDraw(0, (((TickerColumnTest.CHAR_HEIGHT) / 4) * 3));
        setProgress(1.0F);
        verifyDraw(1, 0.0F);
        Mockito.verifyNoMoreInteractions(canvas);
        Mockito.reset(canvas);
        tickerColumn.setTargetChar('1');
        setProgress(0.0F);
        verifyDraw(1, 0.0F);
        setProgress(0.25F);
        verifyDraw(1, ((TickerColumnTest.CHAR_HEIGHT) / 4));
        setProgress(0.75F);
        verifyDraw(1, (((TickerColumnTest.CHAR_HEIGHT) / 4) * 3));
        setProgress(1.0F);
        verifyDraw(2, 0.0F);
        Mockito.verifyNoMoreInteractions(canvas);
        Mockito.reset(canvas);
        tickerColumn.setTargetChar(EMPTY_CHAR);
        setProgress(0.0F);
        verifyDraw(2, 0.0F);
        setProgress(0.25F);
        verifyDraw(2, ((-(TickerColumnTest.CHAR_HEIGHT)) / 2));
        setProgress(0.75F);
        verifyDraw(1, ((-(TickerColumnTest.CHAR_HEIGHT)) / 2));
        setProgress(1.0F);
        verifyDraw(0, 0.0F);
        Mockito.verifyNoMoreInteractions(canvas);
    }

    @Test
    public void test_draw_wraparound() {
        tickerColumn.setTargetChar('4');
        setProgress(1.0F);
        verifyDraw(5, 0.0F);
        tickerColumn.setTargetChar('1');
        setProgress(0.5F);
        verifyDraw(6, 0.0F);
        setProgress(1.0F);
        verifyDraw(7, 0.0F);
    }
}

