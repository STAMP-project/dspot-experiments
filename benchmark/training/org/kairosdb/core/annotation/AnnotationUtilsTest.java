package org.kairosdb.core.annotation;


import java.lang.reflect.InvocationTargetException;
import org.junit.Test;


public class AnnotationUtilsTest {
    @Test(expected = NullPointerException.class)
    public void test_getPropertyMetadata_nullClass_invalid() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        AnnotationUtils.getPropertyMetadata(null);
    }
}

