package com.r0adkll.slidr.widget;


import SlidrPosition.BOTTOM;
import SlidrPosition.HORIZONTAL;
import SlidrPosition.LEFT;
import SlidrPosition.RIGHT;
import SlidrPosition.TOP;
import SlidrPosition.VERTICAL;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ScrimRendererTest {
    private static final int LEFT = 10;

    private static final int HORIZONTAL_LEFT = -10;

    private static final int RIGHT = 50;

    private static final int TOP = 20;

    private static final int VERTICAL_TOP = -20;

    private static final int BOTTOM = 100;

    private static final int WIDTH = 720;

    private static final int HEIGHT = 1280;

    @Mock
    Paint paint;

    @Mock
    Canvas canvas;

    @Mock
    View decorView;

    @Mock
    View rootView;

    private ScrimRenderer renderer;

    @Test
    public void shouldDrawRectForLeftPosition() {
        renderer.render(canvas, SlidrPosition.LEFT, paint);
        Mockito.verify(canvas).drawRect(0, 0, ScrimRendererTest.LEFT, ScrimRendererTest.HEIGHT, paint);
    }

    @Test
    public void shouldDrawRectForRightPosition() {
        renderer.render(canvas, SlidrPosition.RIGHT, paint);
        Mockito.verify(canvas).drawRect(ScrimRendererTest.RIGHT, 0, ScrimRendererTest.WIDTH, ScrimRendererTest.HEIGHT, paint);
    }

    @Test
    public void shouldDrawRectForTopPosition() {
        renderer.render(canvas, SlidrPosition.TOP, paint);
        Mockito.verify(canvas).drawRect(0, 0, ScrimRendererTest.WIDTH, ScrimRendererTest.TOP, paint);
    }

    @Test
    public void shouldDrawRectForBottomPosition() {
        renderer.render(canvas, SlidrPosition.BOTTOM, paint);
        Mockito.verify(canvas).drawRect(0, ScrimRendererTest.BOTTOM, ScrimRendererTest.WIDTH, ScrimRendererTest.HEIGHT, paint);
    }

    @Test
    public void shouldDrawRectForPositiveVerticalPosition() {
        renderer.render(canvas, VERTICAL, paint);
        Mockito.verify(canvas).drawRect(0, 0, ScrimRendererTest.WIDTH, ScrimRendererTest.TOP, paint);
    }

    @Test
    public void shouldDrawRectForNegativeVerticalPosition() {
        Mockito.when(decorView.getTop()).thenReturn(ScrimRendererTest.VERTICAL_TOP);
        renderer.render(canvas, VERTICAL, paint);
        Mockito.verify(canvas).drawRect(0, ScrimRendererTest.BOTTOM, ScrimRendererTest.WIDTH, ScrimRendererTest.HEIGHT, paint);
    }

    @Test
    public void shouldDrawRectForPositiveHorizontalPosition() {
        renderer.render(canvas, HORIZONTAL, paint);
        Mockito.verify(canvas).drawRect(0, 0, ScrimRendererTest.LEFT, ScrimRendererTest.HEIGHT, paint);
    }

    @Test
    public void shouldDrawRectForNegativeHorizontalPosition() {
        Mockito.when(decorView.getLeft()).thenReturn(ScrimRendererTest.HORIZONTAL_LEFT);
        renderer.render(canvas, HORIZONTAL, paint);
        Mockito.verify(canvas).drawRect(ScrimRendererTest.RIGHT, 0, ScrimRendererTest.WIDTH, ScrimRendererTest.HEIGHT, paint);
    }
}

