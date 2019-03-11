package com.svgandroid;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Created by Vlad Medvedev on 28.01.2016.
 * vladislav.medvedev@devfactory.com
 */
@RunWith(PowerMockRunner.class)
public class SVGHandlerGradientTransformTest extends SVGHandlerTestSupport {
    @Test
    public void testGradientTransform_Matrix() throws Exception {
        testGradientTransform("matrix(0.2883 0 0 0.2865 153.3307 265.0264)", matrix);
        Mockito.verify(matrix).setValues(new float[]{ 0.2883F, 0.0F, 153.3307F, 0.0F, 0.2865F, 265.0264F, 0.0F, 0.0F, 1.0F });
    }

    @Test
    public void testGradientTransform_Translate() throws Exception {
        testGradientTransform("translate(0,-924.36218)", matrix);
        Mockito.verify(matrix).postTranslate(0.0F, (-924.3622F));
    }

    @Test
    public void testGradientTransform_Scale() throws Exception {
        testGradientTransform("scale(100.2,120.34)", matrix);
        Mockito.verify(matrix).postScale(100.2F, 120.34F);
    }

    @Test
    public void testGradientTransform_SkewX() throws Exception {
        testGradientTransform("skewX(240.23)", matrix);
        Mockito.verify(matrix).postSkew(((float) (Math.tan(240.23F))), 0.0F);
    }

    @Test
    public void testGradientTransform_SkewY() throws Exception {
        testGradientTransform("skewY(240.23)", matrix);
        Mockito.verify(matrix).postSkew(0.0F, ((float) (Math.tan(240.23F))));
    }

    @Test
    public void testGradientTransform_Rotate() throws Exception {
        testGradientTransform("rotate(120.2, 240.23, 123.11)", matrix);
        Mockito.verify(matrix).postTranslate(240.23F, 123.11F);
        Mockito.verify(matrix).postRotate(120.2F);
        Mockito.verify(matrix).postTranslate((-240.23F), (-123.11F));
    }
}

