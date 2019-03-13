package com.baeldung.string;


import org.junit.Assert;
import org.junit.Test;


public class CharSequenceVsStringUnitTest {
    @Test
    public void givenUsingString_whenInstantiatingString_thenWrong() {
        CharSequence firstString = "bealdung";
        String secondString = "baeldung";
        Assert.assertNotEquals(firstString, secondString);
    }

    @Test
    public void givenIdenticalCharSequences_whenCastToString_thenEqual() {
        CharSequence charSequence1 = "baeldung_1";
        CharSequence charSequence2 = "baeldung_2";
        Assert.assertTrue(((charSequence1.toString().compareTo(charSequence2.toString())) < 0));
    }

    @Test
    public void givenString_whenAppended_thenUnmodified() {
        String test = "a";
        int firstAddressOfTest = System.identityHashCode(test);
        test += "b";
        int secondAddressOfTest = System.identityHashCode(test);
        Assert.assertNotEquals(firstAddressOfTest, secondAddressOfTest);
    }

    @Test
    public void givenStringBuilder_whenAppended_thenModified() {
        StringBuilder test = new StringBuilder();
        test.append("a");
        int firstAddressOfTest = System.identityHashCode(test);
        test.append("b");
        int secondAddressOfTest = System.identityHashCode(test);
        Assert.assertEquals(firstAddressOfTest, secondAddressOfTest);
    }
}

