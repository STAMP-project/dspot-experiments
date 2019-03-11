/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.drawable;


import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link OrientedDrawable}
 */
@RunWith(RobolectricTestRunner.class)
public class OrientedDrawableTest {
    private Drawable mDrawable;

    private Canvas mCanvas;

    private Rect mBounds;

    @Test
    public void testCreation_invalidAngle() {
        try {
            new OrientedDrawable(mDrawable, 20, ExifInterface.ORIENTATION_NORMAL);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Do nothing, expected.
        }
    }

    @Test
    public void testCreation_zeroDegrees() {
        OrientedDrawable drawable = new OrientedDrawable(mDrawable, 0, ExifInterface.ORIENTATION_NORMAL);
        drawable.setBounds(mBounds);
        drawable.draw(mCanvas);
        Assert.assertTrue(drawable.mRotationMatrix.isIdentity());
        Mockito.verify(mDrawable).setBounds(new Rect(mBounds));
    }

    @Test
    public void testCreation_nintyDegrees() {
        OrientedDrawable drawable = new OrientedDrawable(mDrawable, 90, ExifInterface.ORIENTATION_ROTATE_90);
        drawable.setBounds(mBounds);
        drawable.draw(mCanvas);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setRotate(90, drawable.getBounds().centerX(), drawable.getBounds().centerY());
        Assert.assertFalse(drawable.mRotationMatrix.isIdentity());
        AndroidGraphicsTestUtils.assertEquals(expectedMatrix, drawable.mRotationMatrix);
        verifySetBounds(expectedMatrix);
    }

    @Test
    public void testCreation_hundredAndEightyDegrees() {
        OrientedDrawable drawable = new OrientedDrawable(mDrawable, 180, ExifInterface.ORIENTATION_ROTATE_180);
        drawable.setBounds(mBounds);
        drawable.draw(mCanvas);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setRotate(180, drawable.getBounds().centerX(), drawable.getBounds().centerY());
        Assert.assertFalse(drawable.mRotationMatrix.isIdentity());
        AndroidGraphicsTestUtils.assertEquals(expectedMatrix, drawable.mRotationMatrix);
        verifySetBounds(expectedMatrix);
    }

    @Test
    public void testCreation_twoHundredAndSeventyDegrees() {
        OrientedDrawable drawable = new OrientedDrawable(mDrawable, 270, ExifInterface.ORIENTATION_ROTATE_270);
        drawable.setBounds(mBounds);
        drawable.draw(mCanvas);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setRotate(270, drawable.getBounds().centerX(), drawable.getBounds().centerY());
        Assert.assertFalse(drawable.mRotationMatrix.isIdentity());
        AndroidGraphicsTestUtils.assertEquals(expectedMatrix, drawable.mRotationMatrix);
        verifySetBounds(expectedMatrix);
    }

    @Test
    public void testCreation_flipHorizontal() {
        OrientedDrawable drawable = new OrientedDrawable(mDrawable, 0, ExifInterface.ORIENTATION_FLIP_HORIZONTAL);
        drawable.setBounds(mBounds);
        drawable.draw(mCanvas);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setScale((-1), 1);
        Assert.assertFalse(drawable.mRotationMatrix.isIdentity());
        AndroidGraphicsTestUtils.assertEquals(expectedMatrix, drawable.mRotationMatrix);
        verifySetBounds(expectedMatrix);
    }

    @Test
    public void testCreation_flipVertical() {
        OrientedDrawable drawable = new OrientedDrawable(mDrawable, 0, ExifInterface.ORIENTATION_FLIP_VERTICAL);
        drawable.setBounds(mBounds);
        drawable.draw(mCanvas);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setScale(1, (-1));
        Assert.assertFalse(drawable.mRotationMatrix.isIdentity());
        AndroidGraphicsTestUtils.assertEquals(expectedMatrix, drawable.mRotationMatrix);
        verifySetBounds(expectedMatrix);
    }

    @Test
    public void testCreation_transpose() {
        OrientedDrawable drawable = new OrientedDrawable(mDrawable, 0, ExifInterface.ORIENTATION_TRANSPOSE);
        drawable.setBounds(mBounds);
        drawable.draw(mCanvas);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setRotate(270, drawable.getBounds().centerX(), drawable.getBounds().centerY());
        expectedMatrix.postScale(1, (-1));
        Assert.assertFalse(drawable.mRotationMatrix.isIdentity());
        AndroidGraphicsTestUtils.assertEquals(expectedMatrix, drawable.mRotationMatrix);
        verifySetBounds(expectedMatrix);
    }

    @Test
    public void testCreation_transverse() {
        OrientedDrawable drawable = new OrientedDrawable(mDrawable, 0, ExifInterface.ORIENTATION_TRANSVERSE);
        drawable.setBounds(mBounds);
        drawable.draw(mCanvas);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setRotate(270, drawable.getBounds().centerX(), drawable.getBounds().centerY());
        expectedMatrix.postScale((-1), 1);
        Assert.assertFalse(drawable.mRotationMatrix.isIdentity());
        AndroidGraphicsTestUtils.assertEquals(expectedMatrix, drawable.mRotationMatrix);
        verifySetBounds(expectedMatrix);
    }
}

