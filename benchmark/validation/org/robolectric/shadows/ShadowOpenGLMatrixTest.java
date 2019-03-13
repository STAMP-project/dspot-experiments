package org.robolectric.shadows;


import android.opengl.Matrix;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowOpenGLMatrixTest {
    @Test(expected = IllegalArgumentException.class)
    public void multiplyMM_failIfResIsNull() throws Exception {
        Matrix.multiplyMM(null, 0, new float[16], 0, new float[16], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMM_failIfLhsIsNull() throws Exception {
        Matrix.multiplyMM(new float[16], 0, null, 0, new float[16], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMM_failIfRhsIsNull() throws Exception {
        Matrix.multiplyMM(new float[16], 0, new float[16], 0, null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMM_failIfResIsSmall() throws Exception {
        Matrix.multiplyMM(new float[15], 0, new float[16], 0, new float[16], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMM_failIfLhsIsSmall() throws Exception {
        Matrix.multiplyMM(new float[16], 0, new float[15], 0, new float[16], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMM_failIfRhsIsSmall() throws Exception {
        Matrix.multiplyMM(new float[16], 0, new float[16], 0, new float[15], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMM_failIfResOffsetIsOutOfBounds() throws Exception {
        Matrix.multiplyMM(new float[32], 30, new float[16], 0, new float[16], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMM_failIfLhsOffsetIsOutOfBounds() throws Exception {
        Matrix.multiplyMM(new float[16], 0, new float[32], 30, new float[16], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMM_failIfRhsOffsetIsOutOfBounds() throws Exception {
        Matrix.multiplyMM(new float[16], 0, new float[16], 0, new float[32], 30);
    }

    @Test
    public void multiplyIdentity() throws Exception {
        final float[] res = new float[16];
        final float[] i = new float[16];
        Matrix.setIdentityM(i, 0);
        final float[] m1 = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        Matrix.multiplyMM(res, 0, m1, 0, i, 0);
        assertThat(res).usingExactEquality().containsAllOf(m1);
        Matrix.multiplyMM(res, 0, i, 0, m1, 0);
        assertThat(res).usingExactEquality().containsAllOf(m1);
    }

    @Test
    public void multiplyIdentityWithOffset() throws Exception {
        final float[] res = new float[32];
        final float[] i = new float[32];
        Matrix.setIdentityM(i, 16);
        final float[] m1 = new float[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        Matrix.multiplyMM(res, 16, m1, 16, i, 16);
        assertThat(res).usingExactEquality().containsAllOf(m1);
        Matrix.multiplyMM(res, 16, i, 16, m1, 16);
        assertThat(res).usingExactEquality().containsAllOf(m1);
    }

    @Test
    public void multiplyMM() throws Exception {
        final float[] res = new float[16];
        final float[] m1 = new float[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        final float[] m2 = new float[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        final float[] expected = new float[]{ 56, 62, 68, 74, 152, 174, 196, 218, 248, 286, 324, 362, 344, 398, 452, 506 };
        Matrix.multiplyMM(res, 0, m1, 0, m2, 0);
        assertThat(res).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void multiplyMMWithOffset() throws Exception {
        final float[] res = new float[32];
        final float[] m1 = new float[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        final float[] m2 = new float[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 56, 62, 68, 74, 152, 174, 196, 218, 248, 286, 324, 362, 344, 398, 452, 506 };
        final float[] expected = new float[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1680, 1940, 2200, 2460, 4880, 5620, 6360, 7100, 8080, 9300, 10520, 11740, 11280, 12980, 14680, 16380 };
        Matrix.multiplyMM(res, 16, m1, 16, m2, 16);
        assertThat(res).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void multiplyMMRandom() throws Exception {
        final float[] m1 = new float[]{ 0.730964F, 0.006556F, 0.999294F, 0.886486F, 0.703636F, 0.865595F, 0.464857F, 0.861619F, 0.304945F, 0.74041F, 0.059668F, 0.876067F, 0.048256F, 0.259968F, 0.915555F, 0.35672F };
        final float[] m2 = new float[]{ 0.462205F, 0.86812F, 0.520904F, 0.959729F, 0.531887F, 0.882446F, 0.293452F, 0.878477F, 0.938628F, 0.796945F, 0.757566F, 0.983955F, 0.346051F, 0.972866F, 0.773706F, 0.895736F };
        final float[] expected = new float[]{ 1.153855F, 1.389652F, 1.775197F, 1.956428F, 1.141589F, 1.212979F, 1.763527F, 1.802296F, 1.52536F, 1.512691F, 2.254498F, 2.533418F, 1.216656F, 1.650098F, 1.664312F, 2.142354F };
        final float[] res = new float[16];
        Matrix.multiplyMM(res, 0, m1, 0, m2, 0);
        ShadowOpenGLMatrixTest.assertMatrixWithPrecision(res, expected, 1.0E-4F);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMVFailsIfResIsNull() throws Exception {
        Matrix.multiplyMV(null, 0, new float[16], 0, new float[4], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMVFailsIfLhsIsNull() throws Exception {
        Matrix.multiplyMV(new float[4], 0, null, 0, new float[4], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMVFailsIfRhsIsNull() throws Exception {
        Matrix.multiplyMV(new float[4], 0, new float[16], 0, null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMVFailsIfResIsSmall() throws Exception {
        Matrix.multiplyMV(new float[3], 0, new float[16], 0, new float[4], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMVFailsIfLhsIsSmall() throws Exception {
        Matrix.multiplyMV(new float[4], 0, new float[15], 0, new float[4], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMVFailsIfRhsIsSmall() throws Exception {
        Matrix.multiplyMV(new float[4], 0, new float[16], 0, new float[3], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMVFailsIfResOffsetIsOutOfBounds() throws Exception {
        Matrix.multiplyMV(new float[4], 1, new float[16], 0, new float[4], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMVFailsIfLhsOffsetIsOutOfBounds() throws Exception {
        Matrix.multiplyMV(new float[4], 0, new float[16], 1, new float[4], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyMVFailsIfRhsOffsetIsOutOfBounds() throws Exception {
        Matrix.multiplyMV(new float[4], 0, new float[16], 0, new float[4], 1);
    }

    @Test
    public void multiplyMVIdentity() throws Exception {
        final float[] res = new float[4];
        final float[] i = new float[16];
        Matrix.setIdentityM(i, 0);
        float[] v1 = new float[]{ 1, 2, 3, 4 };
        Matrix.multiplyMV(res, 0, i, 0, v1, 0);
        assertThat(res).usingExactEquality().containsAllOf(v1);
    }

    @Test
    public void multiplyMV() throws Exception {
        final float[] res = new float[4];
        final float[] m1 = new float[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        float[] v1 = new float[]{ 42, 239, 128, 1024 };
        float[] expected = new float[]{ 14268, 15701, 17134, 18567 };
        Matrix.multiplyMV(res, 0, m1, 0, v1, 0);
        assertThat(res).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void multiplyMVWithOffset() throws Exception {
        final float[] res = new float[5];
        final float[] m1 = new float[]{ 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        float[] v1 = new float[]{ 0, 0, 42, 239, 128, 1024 };
        float[] expected = new float[]{ 0, 14268, 15701, 17134, 18567 };
        Matrix.multiplyMV(res, 1, m1, 4, v1, 2);
        assertThat(res).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void multiplyMVRandom() throws Exception {
        final float[] m1 = new float[]{ 0.575544F, 0.182558F, 0.097663F, 0.413832F, 0.781248F, 0.466904F, 0.353418F, 0.79054F, 0.074133F, 0.69047F, 0.619758F, 0.191669F, 0.953532F, 0.018836F, 0.336544F, 0.972782F };
        final float[] v2 = new float[]{ 0.573973F, 0.096736F, 0.330662F, 0.758732F };
        final float[] expected = new float[]{ 1.15391F, 0.392554F, 0.550521F, 1.11546F };
        final float[] res = new float[4];
        Matrix.multiplyMV(res, 0, m1, 0, v2, 0);
        ShadowOpenGLMatrixTest.assertMatrixWithPrecision(res, expected, 1.0E-4F);
    }

    @Test
    public void testLength() {
        assertThat(Matrix.length(3, 4, 5)).isWithin(0.001F).of(7.071F);
    }

    @Test
    public void testInvertM() {
        float[] matrix = new float[]{ 10, 0, 0, 0, 0, 20, 0, 0, 0, 0, 30, 0, 40, 50, 60, 1 };
        float[] inverse = new float[]{ 0.1F, 0, 0, 0, 0, 0.05F, 0, 0, 0, 0, 0.03333F, 0, -4, -2.5F, -2, 1 };
        float[] output = new float[16];
        assertThat(Matrix.invertM(output, 0, matrix, 0)).isTrue();
        ShadowOpenGLMatrixTest.assertMatrixWithPrecision(output, inverse, 1.0E-4F);
    }

    @Test
    public void testMultiplyMM() {
        float[] matrix1 = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        float[] matrix2 = new float[]{ 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
        float[] expected = new float[]{ 386, 444, 502, 560, 274, 316, 358, 400, 162, 188, 214, 240, 50, 60, 70, 80 };
        float[] output = new float[16];
        Matrix.multiplyMM(output, 0, matrix1, 0, matrix2, 0);
        assertThat(output).usingExactEquality().containsAllOf(expected);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN, maxSdk = VERSION_CODES.JELLY_BEAN)
    public void testFrustumM() {
        // this is actually a bug
        // https://android.googlesource.com/platform/frameworks/base/+/0a088f5d4681fd2da6f610de157bf905df787bf7
        // expected[8] should be 1.5
        // see testFrustumJB_MR1 below
        float[] expected = new float[]{ 0.005F, 0, 0, 0, 0, 0.02F, 0, 0, 3.0F, 5, -1.020202F, -1, 0, 0, -2.020202F, 0 };
        float[] output = new float[16];
        Matrix.frustumM(output, 0, 100, 500, 200, 300, 1, 100);
        assertThat(output).usingExactEquality().containsAllOf(expected);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void testFrustumJB_MR1() {
        float[] expected = new float[]{ 0.005F, 0, 0, 0, 0, 0.02F, 0, 0, 1.5F, 5, -1.020202F, -1, 0, 0, -2.020202F, 0 };
        float[] output = new float[16];
        Matrix.frustumM(output, 0, 100, 500, 200, 300, 1, 100);
        assertThat(output).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void testPerspectiveM() {
        float[] expected = new float[]{ 1145.9144F, 0, 0, 0, 0, 572.9572F, 0, 0, 0, 0, -1.020202F, -1, 0, 0, -2.020202F, 0 };
        float[] output = new float[16];
        Matrix.perspectiveM(output, 0, 0.2F, 0.5F, 1, 100);
        assertThat(output).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void testMultiplyMV() {
        float[] matrix = new float[]{ 2, 0, 0, 0, 0, 4, 0, 0, 0, 0, 6, 0, 1, 2, 3, 1 };
        float[] vector = new float[]{ 5, 7, 9, 1 };
        float[] expected = new float[]{ 11, 30, 57, 1 };
        float[] output = new float[4];
        Matrix.multiplyMV(output, 0, matrix, 0, vector, 0);
        assertThat(output).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void testSetIdentityM() {
        float[] matrix = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        float[] expected = new float[]{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
        Matrix.setIdentityM(matrix, 0);
        assertThat(matrix).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void testScaleM() {
        float[] matrix = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        float[] expected = new float[]{ 2, 4, 6, 8, 20, 24, 28, 32, 54, 60, 66, 72, 13, 14, 15, 16 };
        float[] output = new float[16];
        Matrix.scaleM(output, 0, matrix, 0, 2, 4, 6);
        assertThat(output).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void testScaleMInPlace() {
        float[] matrix = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        float[] expected = new float[]{ 2, 4, 6, 8, 20, 24, 28, 32, 54, 60, 66, 72, 13, 14, 15, 16 };
        Matrix.scaleM(matrix, 0, 2, 4, 6);
        assertThat(matrix).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void testTranslateM() {
        float[] matrix = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        float[] expected = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 89, 102, 115, 128 };
        float[] output = new float[16];
        Matrix.translateM(output, 0, matrix, 0, 2, 4, 6);
        assertThat(output).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void testTranslateMInPlace() {
        float[] matrix = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        float[] expected = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 89, 102, 115, 128 };
        Matrix.translateM(matrix, 0, 2, 4, 6);
        assertThat(matrix).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void testRotateM() {
        float[] matrix = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        float[] expected = new float[]{ 0.95625275F, 1.9625025F, 2.968752F, 3.9750016F, 5.0910234F, 6.07802F, 7.0650167F, 8.052013F, 8.953606F, 9.960234F, 10.966862F, 11.973489F, 13, 14, 15, 16 };
        float[] output = new float[16];
        Matrix.rotateM(output, 0, matrix, 0, 2, 4, 6, 8);
        assertThat(output).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void testRotateMInPlace() {
        float[] matrix = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        float[] expected = new float[]{ 0.95625275F, 1.9625025F, 2.968752F, 3.9750016F, 5.0910234F, 6.07802F, 7.0650167F, 8.052013F, 8.953606F, 9.960234F, 10.966862F, 11.973489F, 13, 14, 15, 16 };
        Matrix.rotateM(matrix, 0, 2, 4, 6, 8);
        assertThat(matrix).usingExactEquality().containsAllOf(expected);
    }

    @Test
    public void testSetRotateM() {
        float[] matrix = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        float[] expected = new float[]{ 0.9998687F, 0.01299483F, -0.00968048F, 0, -0.012931813F, 0.999895F, 0.006544677F, 0, 0.009764502F, -0.006418644F, 0.99993175F, 0, 0, 0, 0, 1 };
        Matrix.setRotateM(matrix, 0, 1, 2, 3, 4);
        assertThat(matrix).usingExactEquality().containsAllOf(expected);
    }
}

