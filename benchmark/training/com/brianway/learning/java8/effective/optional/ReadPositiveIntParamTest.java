package com.brianway.learning.java8.effective.optional;


import java.util.Properties;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Created by brian on 17/3/27.
 */
public class ReadPositiveIntParamTest {
    @Test
    public void testMap() {
        Properties props = new Properties();
        props.setProperty("a", "5");
        props.setProperty("b", "true");
        props.setProperty("c", "-3");
        TestCase.assertEquals(5, ReadPositiveIntParam.readDurationImperative(props, "a"));
        TestCase.assertEquals(0, ReadPositiveIntParam.readDurationImperative(props, "b"));
        TestCase.assertEquals(0, ReadPositiveIntParam.readDurationImperative(props, "c"));
        TestCase.assertEquals(0, ReadPositiveIntParam.readDurationImperative(props, "d"));
        TestCase.assertEquals(5, ReadPositiveIntParam.readDurationWithOptional(props, "a"));
        TestCase.assertEquals(0, ReadPositiveIntParam.readDurationWithOptional(props, "b"));
        TestCase.assertEquals(0, ReadPositiveIntParam.readDurationWithOptional(props, "c"));
        TestCase.assertEquals(0, ReadPositiveIntParam.readDurationWithOptional(props, "d"));
    }
}

