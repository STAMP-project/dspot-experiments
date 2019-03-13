package android.graphics;


import ScaleToFit.CENTER;
import ScaleToFit.END;
import ScaleToFit.FILL;
import ScaleToFit.START;
import androidx.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Compatibility test for {@link Matrix}
 */
@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public final class MatrixTest {
    @Test
    public void mapRadius() throws Exception {
        Matrix matrix = new Matrix();
        assertThat(matrix.mapRadius(100.0F)).isEqualTo(100.0F);
        assertThat(matrix.mapRadius(Float.MAX_VALUE)).isEqualTo(Float.POSITIVE_INFINITY);
        assertThat(matrix.mapRadius(Float.MIN_VALUE)).isEqualTo(0.0F);
        matrix.postScale(2.0F, 2.0F);
        assertThat(matrix.mapRadius(1.0F)).isWithin(0.01F).of(2.0F);
    }

    @Test
    public void mapPoints() {
        float[] value = new float[9];
        value[0] = 100.0F;
        new Matrix().mapPoints(value);
        assertThat(value[0]).isEqualTo(100.0F);
    }

    @Test(expected = Exception.class)
    public void mapPointsNull() {
        new Matrix().mapPoints(null);
    }

    @Test
    public void mapPoints2() {
        float[] dst = new float[9];
        dst[0] = 100.0F;
        float[] src = new float[9];
        src[0] = 200.0F;
        new Matrix().mapPoints(dst, src);
        assertThat(dst[0]).isEqualTo(200.0F);
    }

    @Test(expected = Exception.class)
    public void mapPointsArraysMismatch() {
        new Matrix().mapPoints(new float[8], new float[9]);
    }

    @Test
    public void mapPointsWithIndices() {
        float[] dst = new float[9];
        dst[0] = 100.0F;
        float[] src = new float[9];
        src[0] = 200.0F;
        new Matrix().mapPoints(dst, 0, src, 0, (9 >> 1));
        assertThat(dst[0]).isEqualTo(200.0F);
    }

    @Test(expected = Exception.class)
    public void mapPointsWithIndicesNull() {
        new Matrix().mapPoints(null, 0, new float[9], 0, 1);
    }

    @Test
    public void setRectToRect() {
        RectF r1 = new RectF();
        r1.set(1.0F, 2.0F, 3.0F, 3.0F);
        RectF r2 = new RectF();
        r1.set(10.0F, 20.0F, 30.0F, 30.0F);
        Matrix matrix = new Matrix();
        float[] result = new float[9];
        assertThat(matrix.setRectToRect(r1, r2, CENTER)).isTrue();
        matrix.getValues(result);
        assertThat(result).isEqualTo(new float[]{ 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F });
        matrix.setRectToRect(r1, r2, END);
        matrix.getValues(result);
        assertThat(result).isEqualTo(new float[]{ 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F });
        matrix.setRectToRect(r1, r2, FILL);
        matrix.getValues(result);
        assertThat(result).isEqualTo(new float[]{ 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F });
        matrix.setRectToRect(r1, r2, START);
        matrix.getValues(result);
        assertThat(result).isEqualTo(new float[]{ 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F });
        assertThat(matrix.setRectToRect(r2, r1, CENTER)).isFalse();
        matrix.getValues(result);
        assertThat(result).isEqualTo(new float[]{ 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F });
        assertThat(matrix.setRectToRect(r2, r1, FILL)).isFalse();
        matrix.getValues(result);
        assertThat(result).isEqualTo(new float[]{ 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F });
        assertThat(matrix.setRectToRect(r2, r1, START)).isFalse();
        matrix.getValues(result);
        assertThat(result).isEqualTo(new float[]{ 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F });
        assertThat(matrix.setRectToRect(r2, r1, END)).isFalse();
        matrix.getValues(result);
        assertThat(result).isEqualTo(new float[]{ 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F });
    }

    @Test(expected = Exception.class)
    public void testSetRectToRectNull() {
        new Matrix().setRectToRect(null, null, CENTER);
    }
}

