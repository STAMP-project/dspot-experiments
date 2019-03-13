package com.dlazaro66.qrcodereaderview;


import android.graphics.Point;
import android.graphics.PointF;
import com.google.zxing.ResultPoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static Orientation.LANDSCAPE;
import static Orientation.PORTRAIT;


@RunWith(RobolectricTestRunner.class)
public class QRToViewPointTransformerShould {
    private QRToViewPointTransformer qrToViewPointTransformer;

    @Test
    public void transformPortraitNotMirrorQRPointToViewPoint() throws Exception {
        ResultPoint qrPoint = new ResultPoint(100, 50);
        boolean isMirrorPreview = false;
        Orientation orientation = PORTRAIT;
        Point viewSize = new Point(100, 200);
        Point cameraPreviewSize = new Point(200, 100);
        PointF result = qrToViewPointTransformer.transform(qrPoint, isMirrorPreview, orientation, viewSize, cameraPreviewSize);
        Assert.assertEquals(result.x, 50, 0.0F);
        Assert.assertEquals(result.y, 100, 0.0F);
    }

    @Test
    public void transformPortraitMirrorQRPointToViewPoint() throws Exception {
        ResultPoint qrPoint = new ResultPoint(0, 0);
        boolean isMirrorPreview = true;
        Orientation orientation = PORTRAIT;
        Point viewSize = new Point(100, 200);
        Point cameraPreviewSize = new Point(200, 100);
        PointF result = qrToViewPointTransformer.transform(qrPoint, isMirrorPreview, orientation, viewSize, cameraPreviewSize);
        Assert.assertEquals(result.x, 100, 0.0F);
        Assert.assertEquals(result.y, 200, 0.0F);
    }

    @Test
    public void transformLandscapeNotMirrorQRPointToViewPoint() throws Exception {
        ResultPoint qrPoint = new ResultPoint(100, 50);
        boolean isMirrorPreview = false;
        Orientation orientation = LANDSCAPE;
        Point viewSize = new Point(200, 100);
        Point cameraPreviewSize = new Point(200, 100);
        PointF result = qrToViewPointTransformer.transform(qrPoint, isMirrorPreview, orientation, viewSize, cameraPreviewSize);
        Assert.assertEquals(result.x, 100, 0.0F);
        Assert.assertEquals(result.y, 50, 0.0F);
    }

    @Test
    public void transformLandscapeMirrorQRPointToViewPoint() throws Exception {
        ResultPoint qrPoint = new ResultPoint(0, 0);
        boolean isMirrorPreview = true;
        Orientation orientation = LANDSCAPE;
        Point viewSize = new Point(200, 100);
        Point cameraPreviewSize = new Point(200, 100);
        PointF result = qrToViewPointTransformer.transform(qrPoint, isMirrorPreview, orientation, viewSize, cameraPreviewSize);
        Assert.assertEquals(result.x, 0, 0.0F);
        Assert.assertEquals(result.y, 100, 0.0F);
    }
}

