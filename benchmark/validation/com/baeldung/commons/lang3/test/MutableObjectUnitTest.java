package com.baeldung.commons.lang3.test;


import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Test;


public class MutableObjectUnitTest {
    private static MutableObject mutableObject;

    @Test
    public void givenMutableObject_whenCalledgetValue_thenCorrect() {
        assertThat(MutableObjectUnitTest.mutableObject.getValue()).isInstanceOf(String.class);
    }

    @Test
    public void givenMutableObject_whenCalledsetValue_thenCorrect() {
        MutableObjectUnitTest.mutableObject.setValue("Another value");
        assertThat(MutableObjectUnitTest.mutableObject.getValue()).isEqualTo("Another value");
    }

    @Test
    public void givenMutableObject_whenCalledtoString_thenCorrect() {
        assertThat(MutableObjectUnitTest.mutableObject.toString()).isEqualTo("Another value");
    }
}

