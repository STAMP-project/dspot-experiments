package org.robolectric.shadows;


import Build.VERSION_CODES;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowMatrixTest {
    private static final float EPSILON = 1.0E-7F;

    @Test
    public void preOperationsAreStacked() {
        Matrix m = new Matrix();
        m.preRotate(4, 8, 15);
        m.preTranslate(16, 23);
        m.preSkew(42, 108);
        assertThat(Shadows.shadowOf(m).getPreOperations()).containsExactly("skew 42.0 108.0", "translate 16.0 23.0", "rotate 4.0 8.0 15.0");
    }

    @Test
    public void postOperationsAreQueued() {
        Matrix m = new Matrix();
        m.postRotate(4, 8, 15);
        m.postTranslate(16, 23);
        m.postSkew(42, 108);
        assertThat(Shadows.shadowOf(m).getPostOperations()).containsExactly("rotate 4.0 8.0 15.0", "translate 16.0 23.0", "skew 42.0 108.0");
    }

    @Test
    public void setOperationsOverride() {
        Matrix m = new Matrix();
        m.setRotate(4);
        m.setRotate(8);
        m.setRotate(15);
        m.setRotate(16);
        m.setRotate(23);
        m.setRotate(42);
        m.setRotate(108);
        assertThat(Shadows.shadowOf(m).getSetOperations()).containsEntry("rotate", "108.0");
    }

    @Test
    public void set_shouldAddOpsToMatrix() {
        final Matrix matrix = new Matrix();
        matrix.setScale(1, 1);
        matrix.preScale(2, 2, 2, 2);
        matrix.postScale(3, 3, 3, 3);
        final ShadowMatrix shadow = Shadows.shadowOf(matrix);
        assertThat(shadow.getSetOperations().get("scale")).isEqualTo("1.0 1.0");
        assertThat(shadow.getPreOperations().get(0)).isEqualTo("scale 2.0 2.0 2.0 2.0");
        assertThat(shadow.getPostOperations().get(0)).isEqualTo("scale 3.0 3.0 3.0 3.0");
    }

    @Test
    public void setScale_shouldAddOpsToMatrix() {
        final Matrix matrix = new Matrix();
        matrix.setScale(1, 2, 3, 4);
        final ShadowMatrix shadow = Shadows.shadowOf(matrix);
        assertThat(shadow.getSetOperations().get("scale")).isEqualTo("1.0 2.0 3.0 4.0");
    }

    @Test
    public void set_shouldOverrideValues() {
        final Matrix matrix1 = new Matrix();
        matrix1.setScale(1, 2);
        final Matrix matrix2 = new Matrix();
        matrix2.setScale(3, 4);
        matrix2.set(matrix1);
        final ShadowMatrix shadow = Shadows.shadowOf(matrix2);
        assertThat(shadow.getSetOperations().get("scale")).isEqualTo("1.0 2.0");
    }

    @Test
    public void set_whenNull_shouldReset() {
        final Matrix matrix1 = new Matrix();
        matrix1.setScale(1, 2);
        final Matrix matrix2 = new Matrix();
        matrix2.set(matrix1);
        matrix2.set(null);
        final ShadowMatrix shadow = Shadows.shadowOf(matrix2);
        assertThat(shadow.getSetOperations()).isEmpty();
    }

    @Test
    public void testIsIdentity() {
        final Matrix matrix = new Matrix();
        assertThat(matrix.isIdentity()).isTrue();
        matrix.postScale(2.0F, 2.0F);
        assertThat(matrix.isIdentity()).isFalse();
    }

    @Test
    @Config(sdk = { VERSION_CODES.LOLLIPOP, VERSION_CODES.LOLLIPOP_MR1, VERSION_CODES.M })
    public void testIsAffine() {
        final Matrix matrix = new Matrix();
        assertThat(matrix.isAffine()).isTrue();
        matrix.postScale(2.0F, 2.0F);
        assertThat(matrix.isAffine()).isTrue();
        matrix.postTranslate(1.0F, 2.0F);
        assertThat(matrix.isAffine()).isTrue();
        matrix.postRotate(45.0F);
        assertThat(matrix.isAffine()).isTrue();
        matrix.setValues(new float[]{ 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 2.0F });
        assertThat(matrix.isAffine()).isFalse();
    }

    @Test
    public void testRectStaysRect() {
        final Matrix matrix = new Matrix();
        assertThat(matrix.rectStaysRect()).isTrue();
        matrix.postScale(2.0F, 2.0F);
        assertThat(matrix.rectStaysRect()).isTrue();
        matrix.postTranslate(1.0F, 2.0F);
        assertThat(matrix.rectStaysRect()).isTrue();
        matrix.postRotate(45.0F);
        assertThat(matrix.rectStaysRect()).isFalse();
        matrix.postRotate(45.0F);
        assertThat(matrix.rectStaysRect()).isTrue();
    }

    @Test
    public void testGetSetValues() {
        final Matrix matrix = new Matrix();
        final float[] values = new float[]{ 0.0F, 1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F };
        matrix.setValues(values);
        final float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        assertThat(matrixValues).isEqualTo(values);
    }

    @Test
    public void testSet() {
        final Matrix matrix1 = new Matrix();
        matrix1.postScale(2.0F, 2.0F);
        matrix1.postTranslate(1.0F, 2.0F);
        matrix1.postRotate(45.0F);
        final Matrix matrix2 = new Matrix();
        matrix2.set(matrix1);
        assertThat(matrix1).isEqualTo(matrix2);
        matrix2.set(null);
        assertThat(matrix2.isIdentity()).isTrue();
    }

    @Test
    public void testReset() {
        final Matrix matrix = new Matrix();
        matrix.postScale(2.0F, 2.0F);
        matrix.postTranslate(1.0F, 2.0F);
        matrix.postRotate(45.0F);
        matrix.reset();
        assertThat(matrix.isIdentity()).isTrue();
    }

    @Test
    public void testSetTranslate() {
        final Matrix matrix = new Matrix();
        matrix.setTranslate(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 1.0F, 1.0F), new PointF(3.0F, 3.0F));
        matrix.setTranslate((-2.0F), (-2.0F));
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 1.0F, 1.0F), new PointF((-1.0F), (-1.0F)));
    }

    @Test
    public void testPostTranslate() {
        final Matrix matrix1 = new Matrix();
        matrix1.postTranslate(1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(2.0F, 2.0F));
        matrix1.postTranslate(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(4.0F, 4.0F));
        final Matrix matrix2 = new Matrix();
        matrix2.setScale(2.0F, 2.0F);
        matrix2.postTranslate((-5.0F), 10.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix2, 1.0F, 1.0F), new PointF((-3.0F), 12.0F));
    }

    @Test
    public void testPreTranslate() {
        final Matrix matrix1 = new Matrix();
        matrix1.preTranslate(1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(2.0F, 2.0F));
        matrix1.preTranslate(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(4.0F, 4.0F));
        final Matrix matrix2 = new Matrix();
        matrix2.setScale(2.0F, 2.0F);
        matrix2.preTranslate((-5.0F), 10.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix2, 1.0F, 1.0F), new PointF((-8.0F), 22.0F));
    }

    @Test
    public void testSetScale() {
        final Matrix matrix = new Matrix();
        matrix.setScale(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 1.0F, 1.0F), new PointF(2.0F, 2.0F));
        matrix.setScale((-2.0F), (-3.0F));
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 2.0F, 3.0F), new PointF((-4.0F), (-9.0F)));
        matrix.setScale((-2.0F), (-3.0F), 1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 2.0F, 3.0F), new PointF((-1.0F), (-5.0F)));
    }

    @Test
    public void testPostScale() {
        final Matrix matrix1 = new Matrix();
        matrix1.postScale(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(2.0F, 2.0F));
        matrix1.postScale(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(4.0F, 4.0F));
        final Matrix matrix2 = new Matrix();
        matrix2.postScale(2.0F, 2.0F, 1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix2, 1.0F, 1.0F), new PointF(1.0F, 1.0F));
        matrix2.setTranslate(1.0F, 2.0F);
        matrix2.postScale(2.0F, 2.0F, 1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix2, 1.0F, 1.0F), new PointF(3.0F, 5.0F));
    }

    @Test
    public void testPreScale() {
        final Matrix matrix1 = new Matrix();
        matrix1.preScale(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(2.0F, 2.0F));
        matrix1.preScale(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(4.0F, 4.0F));
        final Matrix matrix2 = new Matrix();
        matrix2.preScale(2.0F, 2.0F, 1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix2, 1.0F, 1.0F), new PointF(1.0F, 1.0F));
        matrix2.setTranslate(1.0F, 2.0F);
        matrix2.preScale(2.0F, 2.0F, 1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix2, 1.0F, 1.0F), new PointF(2.0F, 3.0F));
    }

    @Test
    public void testSetRotate() {
        final Matrix matrix = new Matrix();
        matrix.setRotate(90.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF((-1.0F), 0.0F));
        matrix.setRotate(180.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(0.0F, (-1.0F)));
        matrix.setRotate(270.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(1.0F, 0.0F));
        matrix.setRotate(360.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(0.0F, 1.0F));
        matrix.setRotate(45.0F, 0.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(0.0F, 1.0F));
    }

    @Test
    public void testPostRotate() {
        final Matrix matrix = new Matrix();
        matrix.postRotate(90.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF((-1.0F), 0.0F));
        matrix.postRotate(90.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(0.0F, (-1.0F)));
        matrix.postRotate(90.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(1.0F, 0.0F));
        matrix.postRotate(90.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(0.0F, 1.0F));
        matrix.setTranslate(1.0F, 2.0F);
        matrix.postRotate(45.0F, 0.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF((-0.70710677F), 3.1213202F));
    }

    @Test
    public void testPreRotate() {
        final Matrix matrix = new Matrix();
        matrix.preRotate(90.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF((-1.0F), 0.0F));
        matrix.preRotate(90.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(0.0F, (-1.0F)));
        matrix.preRotate(90.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(1.0F, 0.0F));
        matrix.preRotate(90.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(0.0F, 1.0F));
        matrix.setTranslate(1.0F, 2.0F);
        matrix.preRotate(45.0F, 0.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(1.0F, 3.0F));
    }

    @Test
    public void testSetSinCos() {
        final Matrix matrix = new Matrix();
        matrix.setSinCos(1.0F, 0.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF((-1.0F), 0.0F));
        matrix.setSinCos(0.0F, (-1.0F));
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(0.0F, (-1.0F)));
        matrix.setSinCos((-1.0F), 0.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(1.0F, 0.0F));
        matrix.setSinCos(0.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(0.0F, 1.0F));
        final float sinCos = ((float) (Math.sqrt(2))) / 2;
        matrix.setSinCos(sinCos, sinCos, 0.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 0.0F, 1.0F), new PointF(0.0F, 1.0F));
    }

    @Test
    public void testSetSkew() {
        final Matrix matrix = new Matrix();
        matrix.setSkew(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 1.0F, 1.0F), new PointF(3.0F, 3.0F));
        matrix.setSkew((-2.0F), (-3.0F));
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 2.0F, 3.0F), new PointF((-4.0F), (-3.0F)));
        matrix.setSkew((-2.0F), (-3.0F), 1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 2.0F, 3.0F), new PointF((-2.0F), 0.0F));
    }

    @Test
    public void testPostSkew() {
        final Matrix matrix1 = new Matrix();
        matrix1.postSkew(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(3.0F, 3.0F));
        matrix1.postSkew(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(9.0F, 9.0F));
        final Matrix matrix2 = new Matrix();
        matrix2.postSkew(2.0F, 2.0F, 1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix2, 1.0F, 1.0F), new PointF(1.0F, 1.0F));
        matrix2.setTranslate(1.0F, 2.0F);
        matrix2.postSkew(2.0F, 2.0F, 1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix2, 1.0F, 1.0F), new PointF(6.0F, 5.0F));
    }

    @Test
    public void testPreSkew() {
        final Matrix matrix1 = new Matrix();
        matrix1.preSkew(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(3.0F, 3.0F));
        matrix1.preSkew(2.0F, 2.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix1, 1.0F, 1.0F), new PointF(9.0F, 9.0F));
        final Matrix matrix2 = new Matrix();
        matrix2.preSkew(2.0F, 2.0F, 1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix2, 1.0F, 1.0F), new PointF(1.0F, 1.0F));
        matrix2.setTranslate(1.0F, 2.0F);
        matrix2.preSkew(2.0F, 2.0F, 1.0F, 1.0F);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix2, 1.0F, 1.0F), new PointF(2.0F, 3.0F));
    }

    @Test
    public void testSetConcat() {
        final Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(2.0F, 3.0F);
        final Matrix translateMatrix = new Matrix();
        translateMatrix.postTranslate(5.0F, 7.0F);
        final Matrix matrix = new Matrix();
        matrix.setConcat(translateMatrix, scaleMatrix);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 2.0F, 2.0F), new PointF(9.0F, 13.0F));
        final Matrix rotateMatrix = new Matrix();
        rotateMatrix.postRotate(90.0F);
        matrix.setConcat(rotateMatrix, matrix);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 2.0F, 2.0F), new PointF((-13.0F), 9.0F));
    }

    @Test
    public void testPostConcat() {
        final Matrix matrix = new Matrix();
        matrix.postScale(2.0F, 3.0F);
        final Matrix translateMatrix = new Matrix();
        translateMatrix.postTranslate(5.0F, 7.0F);
        matrix.postConcat(translateMatrix);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 2.0F, 2.0F), new PointF(9.0F, 13.0F));
        final Matrix rotateMatrix = new Matrix();
        rotateMatrix.postRotate(90.0F);
        matrix.postConcat(rotateMatrix);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 2.0F, 2.0F), new PointF((-13.0F), 9.0F));
    }

    @Test
    public void testPreConcat() {
        final Matrix matrix = new Matrix();
        matrix.preScale(2.0F, 3.0F);
        final Matrix translateMatrix = new Matrix();
        translateMatrix.setTranslate(5.0F, 7.0F);
        matrix.preConcat(translateMatrix);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 2.0F, 2.0F), new PointF(14.0F, 27.0F));
        final Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate(90.0F);
        matrix.preConcat(rotateMatrix);
        ShadowMatrixTest.assertPointsEqual(ShadowMatrixTest.mapPoint(matrix, 2.0F, 2.0F), new PointF(6.0F, 27.0F));
    }

    @Test
    public void testInvert() {
        final Matrix matrix = new Matrix();
        final Matrix inverse = new Matrix();
        matrix.setScale(0.0F, 1.0F);
        assertThat(matrix.invert(inverse)).isFalse();
        matrix.setScale(1.0F, 0.0F);
        assertThat(matrix.invert(inverse)).isFalse();
        matrix.setScale(1.0F, 1.0F);
        ShadowMatrixTest.checkInverse(matrix);
        matrix.setScale((-3.0F), 5.0F);
        ShadowMatrixTest.checkInverse(matrix);
        matrix.setTranslate(5.0F, 2.0F);
        ShadowMatrixTest.checkInverse(matrix);
        matrix.setScale((-3.0F), 5.0F);
        matrix.postTranslate(5.0F, 2.0F);
        ShadowMatrixTest.checkInverse(matrix);
        matrix.setScale((-3.0F), 5.0F);
        matrix.postRotate((-30.0F), 1.0F, 2.0F);
        matrix.postTranslate(5.0F, 2.0F);
        ShadowMatrixTest.checkInverse(matrix);
    }

    @Test
    public void testMapRect() {
        final Matrix matrix = new Matrix();
        matrix.postScale(2.0F, 3.0F);
        final RectF input = new RectF(1.0F, 1.0F, 2.0F, 2.0F);
        final RectF output1 = new RectF();
        matrix.mapRect(output1, input);
        assertThat(output1).isEqualTo(new RectF(2.0F, 3.0F, 4.0F, 6.0F));
        matrix.postScale((-1.0F), (-1.0F));
        final RectF output2 = new RectF();
        matrix.mapRect(output2, input);
        assertThat(output2).isEqualTo(new RectF((-4.0F), (-6.0F), (-2.0F), (-3.0F)));
    }

    @Test
    public void testMapPoints() {
        final Matrix matrix = new Matrix();
        matrix.postTranslate((-1.0F), (-2.0F));
        matrix.postScale(2.0F, 3.0F);
        final float[] input = new float[]{ 0.0F, 0.0F, 1.0F, 2.0F };
        final float[] output = new float[input.length];
        matrix.mapPoints(output, input);
        assertThat(output).usingExactEquality().containsExactly((-2.0F), (-6.0F), 0.0F, 0.0F);
    }

    @Test
    public void testMapVectors() {
        final Matrix matrix = new Matrix();
        matrix.postTranslate((-1.0F), (-2.0F));
        matrix.postScale(2.0F, 3.0F);
        final float[] input = new float[]{ 0.0F, 0.0F, 1.0F, 2.0F };
        final float[] output = new float[input.length];
        matrix.mapVectors(output, input);
        assertThat(output).usingExactEquality().containsExactly(0.0F, 0.0F, 2.0F, 6.0F);
    }
}

