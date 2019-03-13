package cn.hutool.core.annotation;


import org.junit.Assert;
import org.junit.Test;


public class AnnotationUtilTest {
    @Test
    public void getAnnotationValueTest() {
        Object value = AnnotationUtil.getAnnotationValue(AnnotationUtilTest.ClassWithAnnotation.class, AnnotationForTest.class);
        Assert.assertEquals("??", value);
    }

    @AnnotationForTest("??")
    class ClassWithAnnotation {}
}

