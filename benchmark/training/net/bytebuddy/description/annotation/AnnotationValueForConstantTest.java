package net.bytebuddy.description.annotation;


import org.junit.Test;

import static net.bytebuddy.description.annotation.AnnotationValue.ForConstant.of;


public class AnnotationValueForConstantTest {
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgument() throws Exception {
        of(new Object());
    }
}

