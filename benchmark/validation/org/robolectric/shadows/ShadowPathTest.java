package org.robolectric.shadows;


import ShadowPath.Point;
import android.graphics.Path;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;

import static Type.LINE_TO;
import static Type.MOVE_TO;


@RunWith(AndroidJUnit4.class)
public class ShadowPathTest {
    @Test
    public void testMoveTo() throws Exception {
        Path path = new Path();
        path.moveTo(2, 3);
        path.moveTo(3, 4);
        List<ShadowPath.Point> moveToPoints = Shadows.shadowOf(path).getPoints();
        Assert.assertEquals(2, moveToPoints.size());
        Assert.assertEquals(new ShadowPath.Point(2, 3, MOVE_TO), moveToPoints.get(0));
        Assert.assertEquals(new ShadowPath.Point(3, 4, MOVE_TO), moveToPoints.get(1));
    }

    @Test
    public void testLineTo() throws Exception {
        Path path = new Path();
        path.lineTo(2, 3);
        path.lineTo(3, 4);
        List<ShadowPath.Point> lineToPoints = Shadows.shadowOf(path).getPoints();
        Assert.assertEquals(2, lineToPoints.size());
        Assert.assertEquals(new ShadowPath.Point(2, 3, LINE_TO), lineToPoints.get(0));
        Assert.assertEquals(new ShadowPath.Point(3, 4, LINE_TO), lineToPoints.get(1));
    }

    @Test
    public void testReset() throws Exception {
        Path path = new Path();
        path.moveTo(0, 3);
        path.lineTo(2, 3);
        path.quadTo(2, 3, 4, 5);
        path.reset();
        ShadowPath shadowPath = Shadows.shadowOf(path);
        List<ShadowPath.Point> points = shadowPath.getPoints();
        Assert.assertEquals(0, points.size());
    }

    @Test
    public void test_copyConstructor() throws Exception {
        Path path = new Path();
        path.moveTo(0, 3);
        path.lineTo(2, 3);
        path.quadTo(2, 3, 4, 5);
        Path copiedPath = new Path(path);
        Assert.assertEquals(Shadows.shadowOf(path).getPoints(), Shadows.shadowOf(copiedPath).getPoints());
    }
}

