package org.robolectric.shadows;


import Color.BLACK;
import Color.BLUE;
import Color.GREEN;
import Color.RED;
import Color.TRANSPARENT;
import Color.WHITE;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;

import static Type.LINE_TO;


@RunWith(AndroidJUnit4.class)
public class ShadowCanvasTest {
    private Bitmap targetBitmap;

    private Bitmap imageBitmap;

    @Test
    public void shouldDescribeBitmapDrawing() throws Exception {
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(imageBitmap, 1, 2, new Paint());
        canvas.drawBitmap(imageBitmap, 100, 200, new Paint());
        Assert.assertEquals(("Bitmap for file:/an/image.jpg at (1,2)\n" + "Bitmap for file:/an/image.jpg at (100,200)"), Shadows.shadowOf(canvas).getDescription());
        Assert.assertEquals(("Bitmap for file:/an/image.jpg at (1,2)\n" + "Bitmap for file:/an/image.jpg at (100,200)"), Shadows.shadowOf(targetBitmap).getDescription());
    }

    @Test
    public void shouldDescribeBitmapDrawing_withDestinationRect() throws Exception {
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(imageBitmap, new Rect(1, 2, 3, 4), new Rect(5, 6, 7, 8), new Paint());
        Assert.assertEquals("Bitmap for file:/an/image.jpg at (5,6) with height=2 and width=2 taken from Rect(1, 2 - 3, 4)", Shadows.shadowOf(canvas).getDescription());
    }

    @Test
    public void shouldDescribeBitmapDrawing_withDestinationRectF() throws Exception {
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(imageBitmap, new Rect(1, 2, 3, 4), new RectF(5.0F, 6.0F, 7.5F, 8.5F), new Paint());
        Assert.assertEquals("Bitmap for file:/an/image.jpg at (5.0,6.0) with height=2.5 and width=2.5 taken from Rect(1, 2 - 3, 4)", Shadows.shadowOf(canvas).getDescription());
    }

    @Test
    public void shouldDescribeBitmapDrawing_WithMatrix() throws Exception {
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(imageBitmap, new Matrix(), new Paint());
        canvas.drawBitmap(imageBitmap, new Matrix(), new Paint());
        Assert.assertEquals(("Bitmap for file:/an/image.jpg transformed by Matrix[pre=[], set={}, post=[]]\n" + "Bitmap for file:/an/image.jpg transformed by Matrix[pre=[], set={}, post=[]]"), Shadows.shadowOf(canvas).getDescription());
        Assert.assertEquals(("Bitmap for file:/an/image.jpg transformed by Matrix[pre=[], set={}, post=[]]\n" + "Bitmap for file:/an/image.jpg transformed by Matrix[pre=[], set={}, post=[]]"), Shadows.shadowOf(targetBitmap).getDescription());
    }

    @Test
    public void visualize_shouldReturnDescription() throws Exception {
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(imageBitmap, new Matrix(), new Paint());
        canvas.drawBitmap(imageBitmap, new Matrix(), new Paint());
        Assert.assertEquals(("Bitmap for file:/an/image.jpg transformed by Matrix[pre=[], set={}, post=[]]\n" + "Bitmap for file:/an/image.jpg transformed by Matrix[pre=[], set={}, post=[]]"), ShadowCanvas.visualize(canvas));
    }

    @Test
    public void drawColor_shouldReturnDescription() throws Exception {
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawColor(WHITE);
        canvas.drawColor(GREEN);
        canvas.drawColor(TRANSPARENT);
        Assert.assertEquals("draw color -1draw color -16711936draw color 0", Shadows.shadowOf(canvas).getDescription());
    }

    @Test
    public void drawPath_shouldRecordThePathAndThePaint() throws Exception {
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.lineTo(10, 10);
        Paint paint = new Paint();
        paint.setColor(RED);
        paint.setAlpha(7);
        canvas.drawPath(path, paint);
        // changing the values on this Paint shouldn't affect recorded painted path
        paint.setColor(BLUE);
        paint.setAlpha(8);
        ShadowCanvas shadow = Shadows.shadowOf(canvas);
        assertThat(shadow.getPathPaintHistoryCount()).isEqualTo(1);
        ShadowPath drawnPath = Shadows.shadowOf(shadow.getDrawnPath(0));
        Assert.assertEquals(drawnPath.getPoints().get(0), new ShadowPath.Point(10, 10, LINE_TO));
        Paint drawnPathPaint = shadow.getDrawnPathPaint(0);
        assertThat(drawnPathPaint.getColor()).isEqualTo(RED);
        assertThat(drawnPathPaint.getAlpha()).isEqualTo(7);
    }

    @Test
    public void drawPath_shouldRecordThePointsOfEachPathEvenWhenItIsTheSameInstance() throws Exception {
        Canvas canvas = new Canvas(targetBitmap);
        Paint paint = new Paint();
        Path path = new Path();
        path.lineTo(10, 10);
        canvas.drawPath(path, paint);
        path.reset();
        path.lineTo(20, 20);
        canvas.drawPath(path, paint);
        ShadowCanvas shadow = Shadows.shadowOf(canvas);
        assertThat(shadow.getPathPaintHistoryCount()).isEqualTo(2);
        Assert.assertEquals(Shadows.shadowOf(shadow.getDrawnPath(0)).getPoints().get(0), new ShadowPath.Point(10, 10, LINE_TO));
        Assert.assertEquals(Shadows.shadowOf(shadow.getDrawnPath(1)).getPoints().get(0), new ShadowPath.Point(20, 20, LINE_TO));
    }

    @Test
    public void drawPath_shouldAppendDescriptionToBitmap() throws Exception {
        Canvas canvas = new Canvas(targetBitmap);
        Path path1 = new Path();
        path1.lineTo(10, 10);
        path1.moveTo(20, 15);
        Path path2 = new Path();
        path2.moveTo(100, 100);
        path2.lineTo(150, 140);
        Paint paint = new Paint();
        canvas.drawPath(path1, paint);
        canvas.drawPath(path2, paint);
        Assert.assertEquals((((("Path " + (Shadows.shadowOf(path1).getPoints().toString())) + "\n") + "Path ") + (Shadows.shadowOf(path2).getPoints().toString())), Shadows.shadowOf(canvas).getDescription());
        Assert.assertEquals((((("Path " + (Shadows.shadowOf(path1).getPoints().toString())) + "\n") + "Path ") + (Shadows.shadowOf(path2).getPoints().toString())), Shadows.shadowOf(targetBitmap).getDescription());
    }

    @Test
    public void resetCanvasHistory_shouldClearTheHistoryAndDescription() throws Exception {
        Canvas canvas = new Canvas();
        canvas.drawPath(new Path(), new Paint());
        canvas.drawText("hi", 1, 2, new Paint());
        ShadowCanvas shadow = Shadows.shadowOf(canvas);
        shadow.resetCanvasHistory();
        assertThat(shadow.getPathPaintHistoryCount()).isEqualTo(0);
        assertThat(shadow.getTextHistoryCount()).isEqualTo(0);
        Assert.assertEquals("", shadow.getDescription());
    }

    @Test
    public void shouldGetAndSetHeightAndWidth() throws Exception {
        Canvas canvas = new Canvas();
        Shadows.shadowOf(canvas).setWidth(99);
        Shadows.shadowOf(canvas).setHeight(42);
        Assert.assertEquals(99, canvas.getWidth());
        Assert.assertEquals(42, canvas.getHeight());
    }

    @Test
    public void shouldRecordText() throws Exception {
        Canvas canvas = new Canvas();
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        paint.setColor(1);
        paint2.setColor(5);
        canvas.drawText("hello", 1, 2, paint);
        canvas.drawText("hello 2", 4, 6, paint2);
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getTextHistoryCount()).isEqualTo(2);
        Assert.assertEquals(1.0F, shadowCanvas.getDrawnTextEvent(0).x, 0);
        Assert.assertEquals(2.0F, shadowCanvas.getDrawnTextEvent(0).y, 0);
        Assert.assertEquals(4.0F, shadowCanvas.getDrawnTextEvent(1).x, 0);
        Assert.assertEquals(6.0F, shadowCanvas.getDrawnTextEvent(1).y, 0);
        Assert.assertEquals(paint, shadowCanvas.getDrawnTextEvent(0).paint);
        Assert.assertEquals(paint2, shadowCanvas.getDrawnTextEvent(1).paint);
        Assert.assertEquals("hello", shadowCanvas.getDrawnTextEvent(0).text);
        Assert.assertEquals("hello 2", shadowCanvas.getDrawnTextEvent(1).text);
    }

    @Test
    public void shouldRecordText_charArrayOverload() throws Exception {
        Canvas canvas = new Canvas();
        Paint paint = new Paint();
        paint.setColor(1);
        canvas.drawText(new char[]{ 'h', 'e', 'l', 'l', 'o' }, 2, 3, 1.0F, 2.0F, paint);
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getTextHistoryCount()).isEqualTo(1);
        Assert.assertEquals(1.0F, shadowCanvas.getDrawnTextEvent(0).x, 0);
        Assert.assertEquals(2.0F, shadowCanvas.getDrawnTextEvent(0).y, 0);
        Assert.assertEquals(paint, shadowCanvas.getDrawnTextEvent(0).paint);
        Assert.assertEquals("llo", shadowCanvas.getDrawnTextEvent(0).text);
    }

    @Test
    public void shouldRecordText_stringWithRangeOverload() throws Exception {
        Canvas canvas = new Canvas();
        Paint paint = new Paint();
        paint.setColor(1);
        canvas.drawText("hello", 1, 4, 1.0F, 2.0F, paint);
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getTextHistoryCount()).isEqualTo(1);
        Assert.assertEquals(1.0F, shadowCanvas.getDrawnTextEvent(0).x, 0);
        Assert.assertEquals(2.0F, shadowCanvas.getDrawnTextEvent(0).y, 0);
        Assert.assertEquals(paint, shadowCanvas.getDrawnTextEvent(0).paint);
        Assert.assertEquals("ell", shadowCanvas.getDrawnTextEvent(0).text);
    }

    @Test
    public void shouldRecordText_charSequenceOverload() throws Exception {
        Canvas canvas = new Canvas();
        Paint paint = new Paint();
        paint.setColor(1);
        // StringBuilder implements CharSequence:
        canvas.drawText(new StringBuilder("hello"), 1, 4, 1.0F, 2.0F, paint);
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getTextHistoryCount()).isEqualTo(1);
        Assert.assertEquals(1.0F, shadowCanvas.getDrawnTextEvent(0).x, 0);
        Assert.assertEquals(2.0F, shadowCanvas.getDrawnTextEvent(0).y, 0);
        Assert.assertEquals(paint, shadowCanvas.getDrawnTextEvent(0).paint);
        Assert.assertEquals("ell", shadowCanvas.getDrawnTextEvent(0).text);
    }

    @Test
    public void drawCircle_shouldRecordCirclePaintHistoryEvents() throws Exception {
        Canvas canvas = new Canvas();
        Paint paint0 = new Paint();
        Paint paint1 = new Paint();
        canvas.drawCircle(1, 2, 3, paint0);
        canvas.drawCircle(4, 5, 6, paint1);
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getDrawnCircle(0).centerX).isEqualTo(1.0F);
        assertThat(shadowCanvas.getDrawnCircle(0).centerY).isEqualTo(2.0F);
        assertThat(shadowCanvas.getDrawnCircle(0).radius).isEqualTo(3.0F);
        assertThat(shadowCanvas.getDrawnCircle(0).paint).isSameAs(paint0);
        assertThat(shadowCanvas.getDrawnCircle(1).centerX).isEqualTo(4.0F);
        assertThat(shadowCanvas.getDrawnCircle(1).centerY).isEqualTo(5.0F);
        assertThat(shadowCanvas.getDrawnCircle(1).radius).isEqualTo(6.0F);
        assertThat(shadowCanvas.getDrawnCircle(1).paint).isSameAs(paint1);
    }

    @Test
    public void drawArc_shouldRecordArcHistoryEvents() throws Exception {
        Canvas canvas = new Canvas();
        RectF oval0 = new RectF();
        RectF oval1 = new RectF();
        Paint paint0 = new Paint();
        Paint paint1 = new Paint();
        canvas.drawArc(oval0, 1.0F, 2.0F, true, paint0);
        canvas.drawArc(oval1, 3.0F, 4.0F, false, paint1);
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getDrawnArc(0).oval).isEqualTo(oval0);
        assertThat(shadowCanvas.getDrawnArc(0).startAngle).isEqualTo(1.0F);
        assertThat(shadowCanvas.getDrawnArc(0).sweepAngle).isEqualTo(2.0F);
        assertThat(shadowCanvas.getDrawnArc(0).useCenter).isTrue();
        assertThat(shadowCanvas.getDrawnArc(0).paint).isSameAs(paint0);
        assertThat(shadowCanvas.getDrawnArc(1).oval).isEqualTo(oval1);
        assertThat(shadowCanvas.getDrawnArc(1).startAngle).isEqualTo(3.0F);
        assertThat(shadowCanvas.getDrawnArc(1).sweepAngle).isEqualTo(4.0F);
        assertThat(shadowCanvas.getDrawnArc(1).useCenter).isFalse();
        assertThat(shadowCanvas.getDrawnArc(1).paint).isSameAs(paint1);
    }

    @Test
    public void getArcHistoryCount_shouldReturnTotalNumberOfDrawArcEvents() throws Exception {
        Canvas canvas = new Canvas();
        canvas.drawArc(new RectF(), 0.0F, 0.0F, true, new Paint());
        canvas.drawArc(new RectF(), 0.0F, 0.0F, true, new Paint());
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getArcPaintHistoryCount()).isEqualTo(2);
    }

    @Test
    public void getRectHistoryCount_shouldReturnTotalNumberOfDrawRectEvents() throws Exception {
        Canvas canvas = new Canvas();
        canvas.drawRect(1.0F, 2.0F, 3.0F, 4.0F, new Paint());
        canvas.drawRect(1.0F, 2.0F, 3.0F, 4.0F, new Paint());
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getRectPaintHistoryCount()).isEqualTo(2);
    }

    @Test
    public void getOvalHistoryCount_shouldReturnTotalNumberOfDrawOvalEvents() throws Exception {
        Canvas canvas = new Canvas();
        canvas.drawOval(new RectF(), new Paint());
        canvas.drawOval(new RectF(), new Paint());
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getOvalPaintHistoryCount()).isEqualTo(2);
    }

    @Test
    public void getLineHistoryCount_shouldReturnTotalNumberOfDrawLineEvents() throws Exception {
        Canvas canvas = new Canvas();
        canvas.drawLine(0.0F, 1.0F, 2.0F, 3.0F, new Paint());
        canvas.drawLine(0.0F, 1.0F, 2.0F, 3.0F, new Paint());
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getLinePaintHistoryCount()).isEqualTo(2);
    }

    @Test
    public void drawLine_shouldRecordLineHistoryEvents() throws Exception {
        Canvas canvas = new Canvas();
        Paint paint0 = new Paint();
        paint0.setColor(RED);
        paint0.setStrokeWidth(1.0F);
        Paint paint1 = new Paint();
        paint1.setColor(WHITE);
        paint1.setStrokeWidth(2.0F);
        canvas.drawLine(0.0F, 2.0F, 3.0F, 4.0F, paint0);
        canvas.drawLine(5.0F, 6.0F, 7.0F, 8.0F, paint1);
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getDrawnLine(0).startX).isEqualTo(0.0F);
        assertThat(shadowCanvas.getDrawnLine(0).startY).isEqualTo(2.0F);
        assertThat(shadowCanvas.getDrawnLine(0).stopX).isEqualTo(3.0F);
        assertThat(shadowCanvas.getDrawnLine(0).stopY).isEqualTo(4.0F);
        assertThat(shadowCanvas.getDrawnLine(0).paint.getColor()).isEqualTo(RED);
        assertThat(shadowCanvas.getDrawnLine(0).paint.getStrokeWidth()).isEqualTo(1.0F);
        assertThat(shadowCanvas.getDrawnLine(1).startX).isEqualTo(5.0F);
        assertThat(shadowCanvas.getDrawnLine(1).startY).isEqualTo(6.0F);
        assertThat(shadowCanvas.getDrawnLine(1).stopX).isEqualTo(7.0F);
        assertThat(shadowCanvas.getDrawnLine(1).stopY).isEqualTo(8.0F);
        assertThat(shadowCanvas.getDrawnLine(1).paint.getColor()).isEqualTo(WHITE);
        assertThat(shadowCanvas.getDrawnLine(1).paint.getStrokeWidth()).isEqualTo(2.0F);
    }

    @Test
    public void drawOval_shouldRecordOvalHistoryEvents() throws Exception {
        Canvas canvas = new Canvas();
        RectF oval0 = new RectF();
        RectF oval1 = new RectF();
        Paint paint0 = new Paint();
        paint0.setColor(RED);
        Paint paint1 = new Paint();
        paint1.setColor(WHITE);
        canvas.drawOval(oval0, paint0);
        canvas.drawOval(oval1, paint1);
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getDrawnOval(0).oval).isEqualTo(oval0);
        assertThat(shadowCanvas.getDrawnOval(0).paint.getColor()).isEqualTo(RED);
        assertThat(shadowCanvas.getDrawnOval(1).oval).isEqualTo(oval1);
        assertThat(shadowCanvas.getDrawnOval(1).paint.getColor()).isEqualTo(WHITE);
    }

    @Test
    public void drawRect_shouldRecordRectHistoryEvents() throws Exception {
        Canvas canvas = new Canvas();
        Paint paint0 = new Paint();
        paint0.setColor(WHITE);
        Paint paint1 = new Paint();
        paint1.setColor(BLACK);
        RectF rect0 = new RectF(0.0F, 2.0F, 3.0F, 4.0F);
        RectF rect1 = new RectF(5.0F, 6.0F, 7.0F, 8.0F);
        canvas.drawRect(0.0F, 2.0F, 3.0F, 4.0F, paint0);
        canvas.drawRect(5.0F, 6.0F, 7.0F, 8.0F, paint1);
        ShadowCanvas shadowCanvas = Shadows.shadowOf(canvas);
        assertThat(shadowCanvas.getDrawnRect(0).left).isEqualTo(0.0F);
        assertThat(shadowCanvas.getDrawnRect(0).top).isEqualTo(2.0F);
        assertThat(shadowCanvas.getDrawnRect(0).right).isEqualTo(3.0F);
        assertThat(shadowCanvas.getDrawnRect(0).bottom).isEqualTo(4.0F);
        assertThat(shadowCanvas.getDrawnRect(0).rect).isEqualTo(rect0);
        assertThat(shadowCanvas.getDrawnRect(0).paint.getColor()).isEqualTo(WHITE);
        assertThat(shadowCanvas.getDrawnRect(1).left).isEqualTo(5.0F);
        assertThat(shadowCanvas.getDrawnRect(1).top).isEqualTo(6.0F);
        assertThat(shadowCanvas.getDrawnRect(1).right).isEqualTo(7.0F);
        assertThat(shadowCanvas.getDrawnRect(1).bottom).isEqualTo(8.0F);
        assertThat(shadowCanvas.getDrawnRect(1).rect).isEqualTo(rect1);
        assertThat(shadowCanvas.getDrawnRect(1).paint.getColor()).isEqualTo(BLACK);
    }
}

